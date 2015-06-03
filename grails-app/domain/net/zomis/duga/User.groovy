package net.zomis.duga

//unnecessary if passwordEncoder is defined `def passwordEncoder`
import org.springframework.security.crypto.password.PasswordEncoder

class User {

    //This could be defined as `def passwordEncoder` as well and the import would be unnecessary
    PasswordEncoder passwordEncoder

    String username
    String password
    boolean enabled = true
    boolean accountExpired
    boolean accountLocked
    boolean credentialsExpired

    static constraints = {
        username blank: false, unique: true
        password blank: false
    }

    static mapping = {
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
