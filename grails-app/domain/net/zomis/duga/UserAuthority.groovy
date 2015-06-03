package net.zomis.duga

import org.apache.commons.lang.builder.HashCodeBuilder

class UserAuthority implements Serializable {

    private static final long serialVersionUID = 1

    User user
    Authority authority

    boolean equals(other) {
        if (!(other instanceof UserAuthority)) {
            return false
        }

        other.user?.id == user?.id &&
                other.authority?.id == authority?.id
    }

    int hashCode() {
        def builder = new HashCodeBuilder()
        if (user) builder.append(user.id)
        if (authority) builder.append(authority.id)
        builder.toHashCode()
    }

    static UserAuthority get(long userId, long authorityId) {
        UserAuthority.where {
            user == User.load(userId) &&
                    authority == Authority.load(authorityId)
        }.get()
    }

    static boolean exists(long userId, long authorityId) {
        UserAuthority.where {
            user == User.load(userId) &&
                    authority == Authority.load(authorityId)
        }.count() > 0
    }

    static UserAuthority create(User user, Authority authority, boolean flush = false) {
        def instance = new UserAuthority(user: user, authority: authority)
        instance.save(flush: flush, insert: true)
        instance
    }

    static boolean remove(User u, Authority r) {
        if (u == null || r == null) return false

        int rowCount = UserAuthority.where {
            user == User.load(u.id) &&
                    authority == Authority.load(r.id)
        }.deleteAll()

        rowCount > 0
    }

    static void removeAll(User u) {
        if (u == null) return

        UserAuthority.where {
            user == User.load(u.id)
        }.deleteAll()
    }

    static void removeAll(Authority r) {
        if (r == null) return

        UserAuthority.where {
            authority == Authority.load(r.id)
        }.deleteAll()
    }

    static constraints = {
        authority validator: { Authority r, UserAuthority ur ->
            if (ur.user == null) return
            boolean existing = false
            UserAuthority.withNewSession {
                existing = UserAuthority.exists(ur.user.id, r.id)
            }
            if (existing) {
                return 'userAuthority.exists'
            }
        }
    }

    static mapping = {
        id composite: ['authority', 'user']
        version false
    }
}
