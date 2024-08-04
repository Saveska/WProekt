package com.wproekt.model;

import com.wproekt.model.enums.Role;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Entity(name = "taskUser")
@Data
public class User implements UserDetails {

    @Id
    @GeneratedValue

    private Long id;

    @Column(unique = true)
    private String username;
    private String email;
    private String password;
    private String name;
    private String surname;
    private String token;
    private String hashedUsername;
    private Boolean isGithub;
    @OneToMany
    private List<Card> cards;

    @OneToMany(fetch = FetchType.EAGER)
    private Set<Label> labels;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    public User() {
        this.setIsGithub(false);
    }

    public User(String username, String email, String password, String name, String surname) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.role = Role.ROLE_USER;
        this.token = null;
        this.setIsGithub(false);

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(role);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return token == null;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", role=" + role +
                '}';
    }
}
