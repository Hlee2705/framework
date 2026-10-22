package controller;

import org.springframework.stereotype.Controller;

import com.framework.annotation.RequestParam;
import com.framework.annotation.UrlMapping;
import com.framework.model.Session;

@Controller 
public class LoginController {
    @UrlMapping(value = "login", method = "POST")
    public String connexion(@RequestParam("nomUtilisateur") String nomUtilisateur, 
    @RequestParam("motDePasse") String motDePasse, Session session){
        if("admin".equals(nomUtilisateur) && "1234".equals(motDePasse)){
            // enregistrer l'utilisateur dans la session
            session.setAttribute("utilisateur", nomUtilisateur);

            return "Connexion reussie. Bienvenue " + nomUtilisateur;
        }

        return "Nom d'utilisateur ou mot de passe incorrect";
    }

    @UrlMapping(value = "logout", method = "GET")
    public String deconnexion(Session session){
        session.removeAttribute("utilisateur");

        return "Deconnexion reussie.";
    }
}
