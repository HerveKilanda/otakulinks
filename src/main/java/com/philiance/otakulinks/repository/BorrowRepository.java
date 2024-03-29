package com.philiance.otakulinks.repository;

import com.philiance.otakulinks.model.Borrow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowRepository extends JpaRepository<Borrow,Long> {


    List<Borrow> findByEmprunteurId(Long EmprunteurId);
    List<Borrow> findByMangaId(Long MangaId);
}
