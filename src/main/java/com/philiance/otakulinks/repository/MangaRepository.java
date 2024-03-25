package com.philiance.otakulinks.repository;

import com.philiance.otakulinks.model.Manga;
import com.philiance.otakulinks.model.MangaStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MangaRepository extends JpaRepository<Manga, Long> {


   List<Manga> findByStatusAndUserIdAndIsDeletedFalse(MangaStatus status, Long userId);
   // Recupère une liste de manga d'un utilisateur non conecté et qui n'est pas supprimé.
   List<Manga> findByUserIdAndIsDeletedFalse(Long userId);
   // Recupere les mangas de l'utilisateur connecté.

}
