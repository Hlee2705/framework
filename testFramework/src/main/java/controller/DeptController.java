package controller;

import org.springframework.stereotype.Controller;

import com.framework.annotation.UrlMapping;

@Controller
public class DeptController {
    @UrlMapping("dept/new")
    public void create() {
        System.out.println("Méthode create() de DeptController exécutée !");
    }

    @UrlMapping("dept/list")
    public void list() {
        System.out.println("Méthode list() de DeptController exécutée !");
    }

    // Une méthode normale, non annotée, pour vérifier qu'elle est bien ignorée
    @UrlMapping("andrana")
    public void andrana() {
        System.out.println("andrana fotsiny");
    }
}
