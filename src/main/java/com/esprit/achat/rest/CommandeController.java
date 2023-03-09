package com.esprit.achat.rest;

import com.esprit.achat.persistence.dto.MontantPanier;
import com.esprit.achat.persistence.dto.Panier;
import com.esprit.achat.persistence.entity.*;
import com.esprit.achat.persistence.enumeration.Etat;
import com.esprit.achat.repositories.CommandeRepository;
import com.esprit.achat.services.Interface.CommandeService;
import com.esprit.achat.services.Interface.FactureService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/commande")
@AllArgsConstructor
public class CommandeController {

    private CommandeService commandeService;

    private FactureService factureService;

    private CommandeRepository commandeRepository;

    @GetMapping
    List<Commande> retrieveAll(){
        return commandeService.retrieveAll();
    }

    @PostMapping("/add")
    void add(@Valid @RequestBody Commande c){

        if(Objects.nonNull(c.getFacture()) && Objects.nonNull(c.getFacture().getId()) ) {
            Facture facture =  factureService.retrieve(c.getFacture().getId());
            c.setFacture(facture);
        }


        commandeService.add(c);
    }
    @PreAuthorize("hasRole('Operateur')")
    @PutMapping("/edit")
    void update(@Valid @RequestBody Commande c){
        commandeService.update(c);
    }
    @PreAuthorize("hasRole('Operateur')")
    @DeleteMapping("/delete/{id}")
    void remove(@PathVariable("id") Integer id){
        commandeService.remove(id);
    }

    @GetMapping("/{id}")
    Commande retrieve(@PathVariable("id") Integer id){
        return commandeService.retrieve(id);
    }

    @PreAuthorize("hasRole('Operateur')")
    @PutMapping ("/calculermontantTTC/{commandeId}")
    public Commande  calculermontantTTC(@PathVariable ("commandeId") Integer commandeId){
        commandeService.retrieve(commandeId);
        Commande commande = commandeService.retrieve(commandeId);
        commande.setTotalttc(commandeService.calculermontantTTC(commande));


        return commandeRepository.save(commande);
    }
    @PreAuthorize("hasRole('Operateur')")
    @PostMapping("/montant-panier")
    MontantPanier montantPanier(@RequestBody Panier panier){
        return commandeService.calculMontantPanier(panier);
    }
    @PreAuthorize("hasRole('Operateur')")
    @GetMapping("/nbCommandeParEtat/{etat}")
    public Integer nbCommandeParEtat (@PathVariable Etat etat){
        return  commandeService.nbCommandeParEtat(etat);}
    @PreAuthorize("hasRole('Operateur')")
    @PostMapping("/affecter-devise")
    public ResponseEntity<Commande> affecterDeviseAuxCommandes() {
        commandeService.affecterDeviseAuxCommandes();
        return ResponseEntity.ok().build();
    }
    @PreAuthorize("hasRole('Operateur')")
    @GetMapping("/items-commande/{commandeId}")
    @ResponseStatus
    public List<ItemCommande> listeDesItemParCommande(@PathVariable Integer commandeId) {
        return commandeService.listeDesItemParCommande(commandeId);
    }

    @ControllerAdvice
    public class CommandeControllerAdvice {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        @ResponseBody
        public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
            Map<String, String> errors = new HashMap<>();
            ex.getBindingResult().getAllErrors().forEach((error) -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            });
            return errors;
        }
    }

}
