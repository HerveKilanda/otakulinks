package com.philiance.otakulinks.controller;

import com.philiance.otakulinks.model.*;
import com.philiance.otakulinks.repository.BorrowRepository;
import com.philiance.otakulinks.repository.CategoryRepository;
import com.philiance.otakulinks.repository.MangaRepository;
import com.philiance.otakulinks.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@SecurityRequirement(name = "bearerAuth")
public class MangaController {

    @Autowired
    private MangaRepository mangaRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BorrowRepository borrowRepository;


    @GetMapping(value = "/manga")
    public ResponseEntity<List<Manga>> listManga(Principal principal) {
        // Récupère l'ID de l'utilisateur connecté (dans une application réelle, cela viendrait de l'authentification)
        Long userConnectedId = this.getUserConnectedId(principal);

        List<Manga> mangaList;
        // Définit le statut pour lequel on veut récupérer les mangas
        MangaStatus status = MangaStatus.Libre; // Ce statut pourrait aussi venir d'un paramètre de la requête

        // Vérifie si un statut a été défini et s'il est égal à 'Libre'
        if (status != null && status == MangaStatus.Libre) {
            // Récupère les mangas avec le statut 'Libre' pour l'utilisateur connecté et qui ne sont pas supprimés
            mangaList = mangaRepository.findByStatusAndUserIdAndIsDeletedFalse(status, userConnectedId);
        } else {
            // Si aucun statut n'est défini, récupère tous les mangas de l'utilisateur connecté qui ne sont pas supprimés
            mangaList = mangaRepository.findByUserIdAndIsDeletedFalse(userConnectedId);
        }

        // Retourne la liste des mangas avec un statut HTTP OK
        return ResponseEntity.ok(mangaList);
    }




    /**
     * Cette méthode récupère l'ID de l'utilisateur connecté en utilisant l'objet Principal fourni.
     * @param principal L'objet Principal injecté par Spring Security représentant l'utilisateur actuellement authentifié.
     * @return L'ID de l'utilisateur connecté.
     * @throws RuntimeException si l'utilisateur connecté ne peut pas être trouvé ou si le Principal n'est pas du type attendu.
     */
    public Long getUserConnectedId(Principal principal) {
        // Vérifier que le Principal est du type UsernamePasswordAuthenticationToken pour s'assurer qu'il contient les informations d'authentification nécessaires.
        if (!(principal instanceof UsernamePasswordAuthenticationToken)) {
            throw new RuntimeException("Utilisateur non trouvé");
        }

        // Convertir le Principal en UsernamePasswordAuthenticationToken pour pouvoir obtenir le nom de l'utilisateur.
        UsernamePasswordAuthenticationToken principal1 = (UsernamePasswordAuthenticationToken) principal;

        // Utiliser le nom de l'utilisateur pour trouver l'entité User correspondante.
        User oneByEmail = userRepository.findOneByEmail(principal1.getName());

        // Si aucun utilisateur n'est trouvé avec cet email, lever une exception.
        if (oneByEmail == null) {
            throw new RuntimeException("Utilisateur non trouvé");
        }

        // Retourner l'ID de l'utilisateur trouvé.
        return oneByEmail.getId();
    }

    @PostMapping(value = "/manga")
    public ResponseEntity<Manga>addManga(@Valid @RequestBody Manga manga,Principal principal){
        Long userConnectedId = this.getUserConnectedId(principal);
        Optional<User> user = userRepository.findById(userConnectedId);
        Optional<Category> category = categoryRepository.findById(manga.getCategoryId());
        if(category.isPresent()) {
            manga.setCategory(category.get());
        }else {
            return new ResponseEntity("Vous devez ajoutez une categorie",HttpStatus.BAD_REQUEST);
        }
        if (user.isPresent()) {
            manga.setUser(user.get());
        }else {
            return new ResponseEntity("Vous devez ajoutez un utilisateur",HttpStatus.BAD_REQUEST);
        }
        manga.setIsDeleted(false);
        manga.setStatus(MangaStatus.Libre);
        mangaRepository.save(manga);


        return new ResponseEntity<>(manga, HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/manga/{mangaId}")
    public ResponseEntity<?> deleteManga(@PathVariable("mangaId") Long mangaId) {
        // Tente de récupérer le manga par son ID.
        Optional<Manga> mangaToDelete = mangaRepository.findById(mangaId);

        // Vérifie si le manga existe dans la base de données.
        if (!mangaToDelete.isPresent()) {
            // Si le manga n'existe pas, retourne une réponse indiquant que le manga n'a pas été trouvé.
            return new ResponseEntity<>("Manga non trouvé", HttpStatus.NOT_FOUND);
        }

        // Si le manga existe, obtient l'objet Manga.
        Manga manga = mangaToDelete.get();

        // Récupère tous les emprunts associés à ce manga.
        List<Borrow> borrows = borrowRepository.findByMangaId(manga.getId());

        // Parcourt les emprunts pour vérifier si le manga est actuellement emprunté.
        for (Borrow borrow : borrows) {
            // Si `finEmprunt` est `null`, cela signifie que l'emprunt est toujours en cours.
            if (borrow.getFinEmprunt() == null) {
                // Dans ce cas, il y a un conflit et le manga ne peut pas être marqué comme supprimé.
                return new ResponseEntity<>("Le manga est actuellement emprunté et ne peut pas être supprimé.", HttpStatus.CONFLICT);
            }
        }

        // Si le manga n'est pas emprunté, marquer le manga comme supprimé en définissant `isDeleted` sur `true`.
        manga.setIsDeleted(true);

        // Sauvegarde les modifications dans la base de données.
        mangaRepository.save(manga);

        // Retourne une réponse indiquant que la demande de suppression a été traitée avec succès, sans contenu dans le corps de la réponse.
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PutMapping(value = "/manga/{mangaId}")
    public ResponseEntity updateManga(@PathVariable("mangaId") String mangaId,@RequestBody Manga manga){

        Optional<Manga> mangaToUpdate = mangaRepository.findById(Long.parseLong(mangaId));
        if (!mangaToUpdate.isPresent()){
            // Si le manga n'existe pas, retourne une réponse indiquant que le manga n'a pas été trouvé.
            return new ResponseEntity<>("Manga non trouvé", HttpStatus.NOT_FOUND);
        }
        Manga mangaToSave = mangaToUpdate.get();
        Optional<Category> newCategorie = categoryRepository.findById(manga.getCategoryId());

        mangaToSave.setCategory(newCategorie.get());
        mangaToSave.setTitre(manga.getTitre());
        mangaRepository.save(mangaToSave);
        return new ResponseEntity(mangaToSave,HttpStatus.OK);
    }
    @GetMapping(value = "/categorie")
    public ResponseEntity<Category> getCategorie() {

        return new ResponseEntity(categoryRepository.findAll(),HttpStatus.OK);
    }

    @GetMapping(value = "/manga/{mangaId}")
    public ResponseEntity loadManga(@PathVariable("mangaId") String mangaId){

        Optional<Manga> mangaToLoad = mangaRepository.findById(Long.parseLong(mangaId));
        if(!mangaToLoad.isPresent()){
            // Si le manga n'existe pas, retourne une réponse indiquant que le manga n'a pas été trouvé.
            return new ResponseEntity<>("Le manga à chargé n'a pas trouvé", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(mangaToLoad.get(),HttpStatus.OK);
    }
}
