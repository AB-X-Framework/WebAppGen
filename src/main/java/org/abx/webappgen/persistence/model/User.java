package org.abx.webappgen.persistence.model;

import jakarta.persistence.*;

@Entity
@Table(name = "UserAccount")
public class User {

    @Id
    @Column(nullable = false)
    public String username;

    @Column(nullable = false)
    public String password;

    @Column(nullable = false)
    public String role;

    @Column(nullable = false)
    public boolean enabled;


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((username== null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User user = (User) obj;
        if (!username.equals(user.username)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("User [id=")
                .append(", username=").append(username)
                .append(", role=").append(role)
                .append("]");
        return builder.toString();
    }
}

