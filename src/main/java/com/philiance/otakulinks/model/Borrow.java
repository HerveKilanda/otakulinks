package com.philiance.otakulinks.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Borrow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne

    private User emprunteur;

    @ManyToOne

    private User preteur;

    @ManyToOne

    private Manga manga;

    private LocalDate dateEmprunt;
    private LocalDate finEmprunt;
}
