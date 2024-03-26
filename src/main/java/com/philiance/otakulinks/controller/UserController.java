package com.philiance.otakulinks.controller;

import com.philiance.otakulinks.jwt.JwtController;
import com.philiance.otakulinks.jwt.JwtFilter;
import com.philiance.otakulinks.jwt.JwtUtils;
import com.philiance.otakulinks.model.User;
import com.philiance.otakulinks.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@RestController
public class UserController {
    /**
     * Dans ce code, newUserData représente les informations de l'utilisateur reçues de la requête.
     * Ces informations sont utilisées pour créer un nouvel objet User (newUser),
     * qui est ensuite enregistré dans la base de données après avoir crypté le mot de passe.
     */

    @Autowired
     UserRepository userRepository;

    @Autowired
     PasswordEncoder passwordEncoder; // Utilise l'encoder de mot de passe injecté

    @Autowired
    JwtController jwtController;

    @Autowired
     JwtUtils jwtUtils;



    // Point de terminaison pour ajouter un nouvel utilisateur.
    @PostMapping("/users")
    public ResponseEntity<?> addUser(@Valid @RequestBody User newUserData) {

        User existingUser = userRepository.findOneByEmail( newUserData.getEmail());
        if(existingUser != null){
            return new ResponseEntity<>("User already exists", HttpStatus.BAD_REQUEST);
        }
        User usersaved = saveUser(newUserData);
        Authentication authentication = jwtController.logUser(newUserData.getEmail(), newUserData.getPassword());
        String jwt = jwtUtils.generateToken(authentication);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
        return new ResponseEntity<>(usersaved, HttpStatus.CREATED);


    }

    public User saveUser(User user) {
        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setNom(StringUtils.capitalize(user.getNom()));
        newUser.setPrenom(StringUtils.capitalize(user.getPrenom()));
         userRepository.save(newUser);
         return newUser;

    }

    /**
     * Cette méthode vérifie si un utilisateur est connecté et récupère son nom d'utilisateur.
     * @return Une ResponseEntity contenant le nom d'utilisateur si un utilisateur est connecté, ou un message d'erreur si aucun utilisateur n'est connecté.
     */
    @GetMapping(value = "/isConnected")
    public ResponseEntity getConnectedUser() {
        // Obtenir l'objet Principal du contexte de sécurité.
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Vérifier si le Principal est une instance de UserDetails.
        if (principal instanceof UserDetails) {
            // Si c'est le cas, renvoyer le nom d'utilisateur avec le statut HTTP OK.
            return new ResponseEntity(((UserDetails) principal).getUsername(), HttpStatus.OK);
        }

        // Si le Principal n'est pas une instance de UserDetails, cela signifie qu'aucun utilisateur n'est connecté. Renvoyer un message d'erreur avec le statut HTTP FORBIDDEN.
        return new ResponseEntity("L'utilisateur n'est pas connecté", HttpStatus.FORBIDDEN);
    }

}

