package controller;

import java.util.ArrayList;
import java.util.List;

import model.Utilisateur;
import org.springframework.stereotype.Controller;

import com.framework.annotation.Auth;
import com.framework.annotation.RequestParam;
import com.framework.annotation.UrlMapping;
import com.framework.model.Session;

@Controller
public class LoginController {

    private List<Utilisateur> utilisateurs = new ArrayList<>();

    public LoginController() {
        utilisateurs.add(new Utilisateur("admin", "1234"));
        utilisateurs.add(new Utilisateur("valisoa", "valisoa"));
    }

    @UrlMapping(value = "login", method = "POST")
    public String connexion(
            @RequestParam("nomUtilisateur") String nomUtilisateur,
            @RequestParam("motDePasse") String motDePasse,
            Session session) {

        for (Utilisateur utilisateur : utilisateurs) {

            if (utilisateur.getNom().equals(nomUtilisateur)
                    && utilisateur.getMotDePasse().equals(motDePasse)) {

                session.setAttribute("utilisateur", nomUtilisateur);
                session.setAttribute("roles", new String[] { "UTILISATEUR" });
                session.setAttribute("roles", new String[]{"DIRECTEUR"});

                return "Connexion reussie. Bienvenue " + nomUtilisateur;
            }
        }

        return "Nom d'utilisateur ou mot de passe incorrect";
    }

    @UrlMapping(value = "logout", method = "GET")
    public String deconnexion(Session session) {

        session.removeAttribute("utilisateur");

        return "Deconnexion reussie.";
    }

    @Auth
    @UrlMapping(value = "profil", method = "GET")
    public String afficherProfil(Session session) {
        return "Bienvenue sur votre profil";
    }

    @UrlMapping(value = "accueil", method = "GET")
    public String afficherAccueil() {
        return "Bienvenue sur la page d'accueil !";
    }

    @Auth(roles = { "ADMIN" })
    @UrlMapping(value = "administration", method = "GET")
    public String administration() {
        return "Bienvenue dans l'administration !";
    }

    @Auth(roles = { "ADMIN", "DIRECTEUR" })
    @UrlMapping(value = "direction", method = "GET")
    public String direction() {
        return "Bienvenue dans l'espace de direction !";
    }
}