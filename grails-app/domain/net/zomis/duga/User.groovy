package net.zomis.duga

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.springframework.security.crypto.password.PasswordEncoder

import java.nio.charset.StandardCharsets

class User {

    PasswordEncoder passwordEncoder

    String username
    String password
    boolean enabled = true
    boolean accountExpired
    boolean accountLocked
    boolean credentialsExpired
    String apiKey = ''
    String pingExpect = '' // used during signup process
    String githubName = ''
    String chatName = ''
    long chatId = 0

    static constraints = {
        username blank: false, unique: true
        password blank: false
    }

    static mapping = {
        table 'duga_users'
        password column: '`password`'
    }

    static transients = ['passwordEncoder']

    Set<Authority> getAuthorities() {
        UserAuthority.findAllByUser(this).collect { it.authority }
    }

    def beforeInsert() {
        encodePassword()
    }

    def beforeUpdate() {
        if (isDirty('password')) {
            encodePassword()
        }
    }

    protected void encodePassword() {
        password = passwordEncoder.encode(password)
    }

    def github(String apiPath) {
        char append = apiPath.contains('?') ? '&' : '?'
        URL url = new URL("https://api.github.com/$apiPath${append}access_token=$apiKey")
        println 'Github GET request: ' + url.toString()
        URLConnection conn = url.openConnection()
//        String encoding = Base64.getEncoder().encodeToString("$githubName:$apiKey".getBytes());
//        conn.setRequestProperty("Authorization", "Basic " + encoding);
        headerFields(conn)

        def is = conn.getInputStream();
        return new JsonSlurper().parse(is)
    }

    static def headerFields(URLConnection conn) {
        Map<String, List<String>> map = conn.getHeaderFields();

        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }

    def githubPost(String apiPath, obj) {
        URL url = new URL("https://api.github.com/$apiPath")
        HttpURLConnection conn = (HttpURLConnection) url.openConnection()
        String encoding = Base64.getEncoder().encodeToString("$githubName:$apiKey".getBytes());
        conn.setRequestProperty("Authorization", "Basic " + encoding);

        def json = new JsonBuilder(obj).toPrettyString()
        byte[] postData       = json.getBytes( StandardCharsets.UTF_8 );
        int    postDataLength = postData.length;
        conn.setDoOutput( true );
        conn.setInstanceFollowRedirects( false );
        conn.setRequestMethod( "POST" );
        conn.setRequestProperty( "Content-Type", "application/json");
        conn.setRequestProperty( "charset", "utf-8");
        conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
        conn.setUseCaches( false );
        new DataOutputStream(conn.getOutputStream()).withCloseable {
            it.write(postData, 0, postData.length);
            it.flush()
        }
        headerFields(conn)

        def is = conn.getInputStream();
        return new JsonSlurper().parse(is)
    }

}
