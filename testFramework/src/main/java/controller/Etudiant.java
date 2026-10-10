package controller;

public class Etudiant {
    private int id;
    private String etu;
    private String nom;

    public Etudiant() {
    }

    public Etudiant(int id, String etu, String nom) {
        this.id = id;
        this.etu = etu;
        this.nom = nom;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEtu() {
        return etu;
    }

    public void setEtu(String etu) {
        this.etu = etu;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

}
