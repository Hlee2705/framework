package controller;

import org.springframework.stereotype.Controller;

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

    // @UrlMapping(value = "dept/test-param", method = "GET")
    // public String testParam(String nom, String prenom) {
    //     return "Test parametres";
    // }

    // @UrlMapping(value = "dept/test-age", method = "GET")
    // public String testAge(int age) {
    //     return "Age reçu:  " + age;
    // }

    // @UrlMapping(value = "dept/test-types", method = "GET")
    // public String testTypes(
    //         String nom,
    //         int age,
    //         double salaire,
    //         boolean actif,
    //         long numero) {
    //     return "Nom : " + nom
    //             + ", Age : " + age
    //             + ", Salaire : " + salaire
    //             + ", Actif : " + actif
    //             + ", Numero : " + numero;
    // }

    // @UrlMapping(value = "dept/test-double", method = "GET")
    // public String testDouble(double salaire) {
    //     return "Salaire reçu : " + salaire;
    // }

    // @UrlMapping(value = "dept/test-boolean", method = "GET")
    // public String testBoolean(boolean actif) {
    //     return "Actif : " + actif;
    // }
}