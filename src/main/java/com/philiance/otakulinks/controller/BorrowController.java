package com.philiance.otakulinks.controller;

import com.philiance.otakulinks.model.Borrow;
import com.philiance.otakulinks.model.Manga;
import com.philiance.otakulinks.model.MangaStatus;
import com.philiance.otakulinks.model.User;
import com.philiance.otakulinks.repository.BorrowRepository;
import com.philiance.otakulinks.repository.CategoryRepository;
import com.philiance.otakulinks.repository.MangaRepository;
import com.philiance.otakulinks.repository.UserRepository;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@SecurityRequirement(name = "bearerAuth")
public class BorrowController {

    @Autowired
    private MangaRepository mangaRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BorrowRepository borrowRepository;
    @Autowired
    MangaController mangaController;
    @GetMapping(value= "/emprunt")
    public ResponseEntity getMyBorrows(Principal principal){
        List<Borrow> borrows = borrowRepository.findByEmprunteurId(mangaController.getUserConnectedId(principal));
        return new ResponseEntity(borrows, HttpStatus.OK);
    }

    // Définition d'une méthode de type POST pour créer un emprunt de manga
    @PostMapping(value= "/emprunt/{mangaId}")
    public ResponseEntity createBorrows(@PathVariable("mangaId") String mangaId,Principal principal){
        // Récupération de l'ID de l'utilisateur connecté
        Long userConnectedId = mangaController.getUserConnectedId(principal);
        // Recherche de l'utilisateur (emprunteur) dans la base de données
        Optional<User> emprunteur = userRepository.findById(userConnectedId);
        // Recherche du manga dans la base de données en convertissant l'ID de type String en Long
        Optional<Manga> manga = mangaRepository.findById(Long.parseLong(mangaId));

        // Vérifie si l'emprunteur et le manga sont présents et si le manga est disponible
        if (emprunteur.isPresent() && manga.isPresent() && manga.get().getStatus().equals(MangaStatus.Libre)) {
            // Création d'un nouvel objet Borrow (emprunt)
            Borrow borrow = new Borrow();
            // Attribution du manga à l'objet emprunt
            Manga mangaEntity = manga.get();
            borrow.setManga(mangaEntity);
            // Attribution de l'emprunteur à l'objet emprunt
            borrow.setEmprunteur(emprunteur.get());
            // Attribution du prêteur (propriétaire du manga) à l'objet emprunt
            borrow.setPreteur(mangaEntity.getUser());
            // Définition de la date de l'emprunt à la date actuelle
            borrow.setDateEmprunt(LocalDate.now());

            // Changement du statut du manga à "Emprunté"
            mangaEntity.setStatus(MangaStatus.Emprunté);
            // Sauvegarde de l'état modifié du manga dans la base de données
            mangaRepository.save(mangaEntity);

            // Si le code atteint ce point, l'opération a été effectuée avec succès
            return new ResponseEntity(HttpStatus.OK);
        }

        // Si l'emprunteur ou le manga n'est pas trouvé, ou si le manga n'est pas disponible, retourner un statut HTTP 'BAD_REQUEST'
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/delete/{borrowId}")
    public ResponseEntity deleteBorrows(@PathVariable("borrowId") String borrowId){

        Optional<Borrow> borrows = borrowRepository.findById(Long.parseLong(borrowId));
       if (borrows.isEmpty()){
           return new ResponseEntity(HttpStatus.BAD_REQUEST);
       }
        Borrow borrowEntity = borrows.get();
        borrowEntity.setFinEmprunt(LocalDate.now());
        borrowRepository.save(borrowEntity);

        Manga manga = borrowEntity.getManga();
        manga.setStatus(MangaStatus.Libre);
        mangaRepository.save(manga);


        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

}
