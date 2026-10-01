package controller;

public class Etudiant {
    private int id;
    private String etu;

    public Etudiant(int id, String etu) {
        this.id = id;
        this.etu = etu;
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

}
