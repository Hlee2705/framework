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
}