package com.example.cookbook.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Set;

/**
 * Created by grzesiek on 23.08.2017.
 */

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Types type;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    public Role(Types type) {
        this.type = type;
    }

    public enum Types {
        ROLE_ADMIN,
        ROLE_USER
    }
}