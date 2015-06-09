package net.zomis.duga

import org.springframework.security.crypto.password.PasswordEncoder

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

}
