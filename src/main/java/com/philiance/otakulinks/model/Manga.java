package com.philiance.otakulinks.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter
@Entity(name="manga")
@NoArgsConstructor
public class Manga {



    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La case ne peut etre vide")
    private String titre;

    @Valid
    @NotNull(message = "La catégorie ne peut pas être nulle")
    @ManyToOne
    private Category category;

    @Transient
    private Long categoryId;

    @ManyToOne
    private User user;

    private Boolean isDeleted;

    private MangaStatus status;


}

