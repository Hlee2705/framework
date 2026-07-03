package controller;

import org.springframework.stereotype.Controller;

import com.framework.annotation.UrlMapping;

@Controller
public class DeptController {

    @UrlMapping(value = "dept/new", method = "GET")
    public void create() {
        System.out.println("Méthode create() exécutée !");
    }

    @UrlMapping(value = "dept/new", method = "POST")
    public void save() {
        System.out.println("Méthode save() exécutée !");
    }

    @UrlMapping(value = "dept/list", method = "GET")
    public void list() {
        System.out.println("Méthode list() exécutée !");
    }

    @UrlMapping(value = "andrana")
    public void andrana() {
        System.out.println("andrana fotsiny");
    }

    @UrlMapping(value = "pomme", method = "GET")

    public void pomme() {
        System.out.println("j'adore les pommes");
    }
}
