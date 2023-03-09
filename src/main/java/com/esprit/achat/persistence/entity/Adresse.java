package com.esprit.achat.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
//import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.List;

@Entity
@Table(name = "Address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Adresse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Size(max = 100)
    @Pattern(regexp = "^[a-zA-Z\\s]*$")
    @NotBlank(message = "ce champ ne doit pas être vide")
    private String street;


    @Size(max = 50)
    @Pattern(regexp = "^[a-zA-Z\\s]*$")
    @NotBlank(message = "ce champ ne doit pas être vide")
    private String city;


    @Size(max = 50)
    @Pattern(regexp = "^[a-zA-Z\\s]*$")
    @NotBlank(message = "ce champ ne doit pas être vide")
    private String country;

    @NotNull
    @DecimalMax("90.000000")
    @DecimalMin("-90.000000")
    private Double latitude;

    @NotNull
    @DecimalMax("180.000000")
    @DecimalMin("-180.000000")
    private Double longitude;


    @JsonIgnore
    @OneToOne(mappedBy = "adresse", cascade = CascadeType.ALL)
    private Livreur livreur;

    @JsonIgnore
    @OneToMany(mappedBy = "adresse")
    private List<Livraison> livraison;
}
