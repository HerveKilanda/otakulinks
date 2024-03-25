package com.philiance.otakulinks.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 2, max = 25, message = "Le nom doit faire entre 2 et 25 caracteres")
    private String nom;
    @Size(min = 2, max = 25, message = "Le prenom doit faire entre 2 et 25 caracteres")
    private String prenom;

    private String email;

    private String password;

    public User(String email) {
        this.email = email;
    }

}
