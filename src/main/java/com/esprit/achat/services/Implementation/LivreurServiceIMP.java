package com.esprit.achat.services.Implementation;


import com.esprit.achat.persistence.entity.Livraison;
import com.esprit.achat.persistence.entity.Livreur;

import com.esprit.achat.repositories.LivraisonRepository;
import com.esprit.achat.repositories.LivreurRepository;
import com.esprit.achat.services.Interface.LivreurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.time.Year;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Configuration
@Service
public class LivreurServiceIMP extends CrudServiceIMP<Livreur,Long> implements LivreurService {


    @Autowired
    private LivreurRepository livreurRepository;
    @Autowired
    private LivraisonRepository livraisonRepository;



    public ResponseEntity<Livreur> ajouterLike(Long id) {
        Optional<Livreur> optionalLivreur = livreurRepository.findById(id);

        if (optionalLivreur.isPresent()) {
            Livreur livreur = optionalLivreur.get();
            livreur.setLikes(livreur.getLikes() + 1);
            livreurRepository.save(livreur);

            return ResponseEntity.ok(livreur);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

public ResponseEntity<Livreur> ajouterdisLike(Long id) {
        Optional<Livreur> optionalLivreur = livreurRepository.findById(id);

        if (optionalLivreur.isPresent()) {
            Livreur livreur = optionalLivreur.get();
            livreur.setDisLikes(livreur.getDisLikes() + 1);
            livreurRepository.save(livreur);

            return ResponseEntity.ok(livreur);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public String getLivreurMaxLikesForYear(int annee) {
        LocalDate debutAnnee = LocalDate.of(annee, Month.JANUARY, 1);
        LocalDate finAnnee = LocalDate.of(annee, Month.DECEMBER, 31);

        List<Livreur> livreurs = livreurRepository.findByDateCreationBetween(debutAnnee, finAnnee);

        if (livreurs.isEmpty()) {
            return "Aucun livreur trouv?? pour l'ann??e " + annee;
        } else {
            // Trouver le livreur avec le plus grand nombre de likes
            Livreur livreurMaxLikes = Collections.max(livreurs, Comparator.comparing(Livreur::getLikes));

            // V??rifier s'il y a d'autres livreurs avec le m??me nombre de likes
            List<Livreur> livreursMaxLikes = livreurs.stream()
                    .filter(l -> l.getLikes() == livreurMaxLikes.getLikes())
                    .collect(Collectors.toList());

            if (livreursMaxLikes.size() == 1) {
                // Si un seul livreur a le plus grand nombre de likes, il re??oit la prime
                livreurMaxLikes.setPrime(true);
                return "Le livreur qui a le plus de likes pour l'ann??e " + annee + " est " + livreurMaxLikes.getNom();
            } else {
                // Si plusieurs livreurs ont le m??me nombre de likes, trouver celui avec le moins de dislikes
                Livreur livreurMinDislikes = Collections.min(livreursMaxLikes, Comparator.comparing(Livreur::getDisLikes));
                livreurMinDislikes.setPrime(true);
                return "Plusieurs livreurs ont le m??me nombre de likes pour l'ann??e " + annee + ". Le livreur qui a le moins de dislikes est " + livreurMinDislikes.getNom() +" celui qui a le prime";
            }
        }
    }


/*
    public String supprimerLivreurPlusDislike(int annee) {
        // R??cup??ration de la liste des livreurs cr????s entre le 1er janvier et le 31 d??cembre de l'ann??e donn??e
        LocalDate debutAnnee = LocalDate.of(annee, Month.JANUARY, 1);
        LocalDate finAnnee = LocalDate.of(annee, Month.DECEMBER, 31);
        List<Livreur> livreurs = livreurRepository.findByDateCreationBetween(debutAnnee, finAnnee);

        // Recherche du livreur ayant le plus de dislikes
        Livreur livreurPlusDeTest = null;
        int maxDislikes = Integer.MIN_VALUE;
        for (Livreur livreur : livreurs) {
            if (livreur.getDisLikes() > maxDislikes) {
                maxDislikes = livreur.getDisLikes();
                livreurPlusDeTest = livreur;
            } else if (livreur.getDisLikes() == maxDislikes && livreur.getLikes() < livreurPlusDeTest.getLikes()) {
                // Si deux livreurs ont le m??me nombre de dislikes, on compare le nombre de likes
                livreurPlusDeTest = livreur;
            }
        }

        // Suppression du livreur trouv??
        if (livreurPlusDeTest != null) {
            livreurRepository.delete(livreurPlusDeTest);
            return ("Le livreur ayant le plus de dislikes pour l'ann??e " + annee + " a ??t?? supprim??.");
        } else {
            return ("Aucun livreur trouv?? pour l'ann??e " + annee + ".");
        }
    }
*/


    public String chercherLivreurDisponible(Long id) {
        Optional<Livreur> livreur = livreurRepository.findById(id);
        if (livreur.isPresent()) {
            return ("Le livreur est disponible");
        } else {
            return "Le livreur n'est pas disponible";
        }
    }
    @Transactional
    public String verifierPrimeLivreur(Long l) {
        Livreur livreur = livreurRepository.findById(l).orElse(null);
        int mois = 0;
        LocalDate dateMoyenneSuperieure = null;

        if (livreur.getMoyenneNote() >= 4.8) {
            if (livreur.getDateMoyenneSuperieure() == null) {
                // Si la moyenne est sup??rieure ?? 4.8 pour la premi??re fois, stocker la date correspondante
                livreur.setDateMoyenneSuperieure(LocalDate.now());
            } else {
                // Calculer le nombre de mois entre la date de la moyenne sup??rieure ?? 4.8 et la date actuelle
                mois = Period.between(livreur.getDateMoyenneSuperieure(), LocalDate.now()).getMonths();
            }

            if (mois >= 1 && mois <= 2) {
                // Si la moyenne est sup??rieure ?? 4.8 depuis 1 ou 2 mois, calculer la prime correspondante
                livreur.setPrime(true);
                return ("Le livreur " + livreur.getNom() + " " + livreur.getPrenom() + " a le droit ?? une prime de 150 dinars pour avoir une moyenne sup??rieure ?? 4.8 pendant " + mois + " mois.");
            } else if (mois > 2) {
                // Si plus de deux mois se sont ??coul??s depuis la derni??re prime, r??initialiser la prime et la date
                livreur.setPrime(false);
                livreur.setDateMoyenneSuperieure(null);
                livreurRepository.save(livreur);
                return ("La livreur " + livreur.getNom() + " " + livreur.getPrenom() + " a ??t?? r??initialis??e car plus de 2 mois se sont ??coul??s depuis qu'il a eu droit ?? la prime.");
            }
        } else {
            // R??initialiser la date de la moyenne sup??rieure ?? 4.8 si la moyenne est inf??rieure ?? 4.8
            livreur.setDateMoyenneSuperieure(null);
            livreur.setPrime(false);
            livreurRepository.save(livreur);
            return ("Le livreur " + livreur.getNom() + " " + livreur.getPrenom() + " n'a pas droit ?? la prime car sa moyenne est inf??rieure ?? 4.8.");
        }

        return "Fin de la proc??dure.";
    }


    public String ajouterNoteLivreur(Long idLivraison, int note) {
        Optional<Livraison> optionalLivraison = livraisonRepository.findById(idLivraison);
        if (optionalLivraison.isPresent()) {
            Livraison livraison = optionalLivraison.get();
            Livreur livreur = livraison.getLivreur();


            double moyenneNotes = (((livreur.getMoyenneNote() * livreur.getNombreNotes() + livraison.getNoteClient()) / ( livreur.getNombreNotes() +1)));
            livreur.setMoyenneNote(moyenneNotes);

            int nombreNotes = livreur.getNombreNotes() + 1;
            livreur.setNombreNotes(nombreNotes);

            livreurRepository.save(livreur);
            livraison.setNoteClient(note);

            livraisonRepository.save(livraison);
            return "La note a ??t?? ajout??e avec succ??s pour la livraison " + idLivraison;
        } else {
            return "La livraison avec l'identifiant " + idLivraison + " n'existe pas.";
        }
    }
    public List<Livreur> trierLivreurParNoteMoyenne()
    {
        return livreurRepository.findAllOrderByNoteMoyenneDesc();
    }
}
