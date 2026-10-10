package controller;

import org.springframework.stereotype.Controller;

import com.framework.annotation.RequestParam;
import com.framework.annotation.ApiRest;
import com.framework.annotation.UrlMapping;
import com.framework.model.ModelView;

@Controller
public class DeptController {

    @UrlMapping(value = "dept/new", method = "GET")
    public ModelView create() {

        ModelView mv = new ModelView("dept-form");

        mv.addObject("titre", "Création d'un département");

        return mv;
    }

    @UrlMapping(value = "dept/new", method = "POST")
    public ModelView save() {

        ModelView mv = new ModelView("dept-save");

        mv.addObject("message", "Département enregistré avec succès");

        return mv;
    }

    @UrlMapping(value = "dept/list", method = "GET")
    public ModelView list() {

        ModelView mv = new ModelView("dept-list");

        mv.addObject("nom", "Informatique");
        mv.addObject("chef", "Rakoto");
        mv.addObject("effectif", 120);

        return mv;
    }

    @UrlMapping(value = "andrana")
    public ModelView andrana() {

        ModelView mv = new ModelView("test");

        mv.addObject("texte", "Essai du framework");

        return mv;
    }

    @UrlMapping(value = "pomme", method = "GET")
    public ModelView pomme() {

        ModelView mv = new ModelView("pomme");

        mv.addObject("fruit", "J'adore les pommes");

        return mv;
    }

    @UrlMapping(value = "api/test", method = "GET")
    @ApiRest
    public String testerApi() {
        return "{\"message\":\"Bonjour depuis mon API\"}";
    }

    @UrlMapping(value = "api/employe", method = "GET")
    @ApiRest
    public Employe testEmploye() {
        Employe employe = new Employe(1, "Rakoto");

        return employe;
    }

    @UrlMapping(value = "api/etudiant", method = "GET")
    @ApiRest
    public Etudiant testEtudiant() {
        Etudiant etudiant = new Etudiant(1, "ETU003949");

        return etudiant;
    }

    @UrlMapping(value = "api/voiture", method = "GET")
    @ApiRest
    public Voiture testVoiture() {
        Voiture v = new Voiture(1, "bmw", "dddjdj");

        return v;
    }

    @UrlMapping(value = "dept/test-request-param", method = "GET")
    public String testRequestParam(
            @RequestParam("nom") String nomUtilisateur,
            @RequestParam("age") int ageUtilisateur) {

        return "Nom : " + nomUtilisateur
                + ", Age : " + ageUtilisateur;

    }

    @ApiRest
    @UrlMapping(value = "api/employe-request-param", method = "POST")
    public Employe save(@RequestParam("nom") String nomEmploye) {
        Employe employe = new Employe(1, nomEmploye);
        return employe;
    }

    @UrlMapping(value = "api/employe-requestParam", method = "POST")
    public Employe saveE(@RequestParam("nom") String nomEmploye) {
        Employe employe = new Employe(1, nomEmploye);
        return employe;
    }

}
