package com.mftplus.ecommerce.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuperBuilder
@Getter
@Setter
@Entity(name = "userEntity")
@Table(name = "user_tbl")
public class User extends Base implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "u_username", nullable = false, unique = true, length = 50)
    @Pattern(regexp = "^[a-z-0-9]{2,50}$",message = "incorrect username !")
    private String username;

    @JsonIgnore
    @Column(name = "u_password", nullable = false)
    private String password;

    @Email(message = "Incorrect Email Format!")
    @Column(name = "u_email", unique = true, length = 320)
    private String email;

    @Column(name = "u_first_name", nullable = false, length = 20)
    @Pattern(regexp = "^[A-Za-z]{3,20}$",message = "incorrect first name!")
    private String firstName;

    @Column(name = "u_last_name", nullable = false, length = 20)
    @Pattern(regexp = "^[A-Za-z]{3,20}$",message = "incorrect last name!")
    private String lastName;

    @Column(name = "u_phone_number", nullable = false, length = 11)
    @Pattern(regexp = "^[0-9]{11}$",message = "incorrect phone number!")
    private String phoneNumber;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id desc")
    private List<VerificationToken> verificationTokens = new ArrayList<>();

    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = {@JoinColumn (name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn (name = "role_id", referencedColumnName = "id")})
    private List<Role> roles;


    //for user details
    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorityList = new ArrayList<>();
        this.getRoles().forEach(role -> {authorityList.add(new SimpleGrantedAuthority(role.getName()));
        });
        return authorityList;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }

    public User(){

    }

    public User(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.password = user.getPassword();
        this.phoneNumber = user.getPhoneNumber();
        this.addresses = user.getAddresses();
        this.email = user.getEmail();
        this.verificationTokens = user.getVerificationTokens();
        this.emailVerified = user.getEmailVerified();
        this.roles = user.getRoles();
    }
}