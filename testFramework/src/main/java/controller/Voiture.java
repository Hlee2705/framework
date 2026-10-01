package controller;

public class Voiture {
    private int id;
    private String marque;
    private String immatriculation;

    public Voiture(int id, String marque, String immatriculation) {
        this.id = id;
        this.marque = marque;
        this.immatriculation = immatriculation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMarque() {
        return marque;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public String getImmatriculation() {
        return immatriculation;
    }

    public void setImmatriculation(String immatriculation) {
        this.immatriculation = immatriculation;
    }

}
