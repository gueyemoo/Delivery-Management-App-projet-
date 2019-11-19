package ws.splash.projetcandidature.model;

import java.io.Serializable;

public class Livreur implements Serializable {

    public static final String TAG = "Livreur";
    // private static final long serialVersionUID = -7406082437623008161L;

    private long mId;
    private String mNom;
    private Integer mLivreur_X;
    private Integer mLivreur_Y;
    private String mDisponibilite;
    private Float mParcoursKM;
    private String mStatut;


    public Livreur() {
    }


    public Livreur(String nom, Integer livreur_x, Integer livreur_y, String disponibilite, Float parcoursKM, String statut) {
        this.mNom = nom;
        this.mLivreur_X = livreur_x;
        this.mLivreur_Y = livreur_y;
        this.mDisponibilite = disponibilite;
        this.mParcoursKM = parcoursKM;
        this.mStatut = statut;
    }

    public long getId() {
        return mId;
    }

    public void setId(long mId) {
        this.mId = mId;
    }

    public String getNom() {
        return mNom;
    }

    public void setNom(String mNom) {
        this.mNom = mNom;
    }

    public Integer getLivreur_X() {
        return mLivreur_X;
    }

    public void setLivreur_X(Integer mLivreur_X) {
        this.mLivreur_X = mLivreur_X;
    }

    public Integer getLivreur_Y() {
        return mLivreur_Y;
    }

    public void setLivreur_Y(Integer mLivreur_Y) {
        this.mLivreur_Y = mLivreur_Y;
    }

    public String getDisponibilite() {
        return mDisponibilite;
    }

    public void setDisponibilite(String mDisponibilite) {
        this.mDisponibilite = mDisponibilite;
    }

    public Float getParcoursKM() {
        return mParcoursKM;
    }

    public void setParcoursKM(Float mParcoursKM) {
        this.mParcoursKM = mParcoursKM;
    }

    public String getStatut() {
        return mStatut;
    }

    public void setStatut(String mStatut) {
        this.mStatut = mStatut;
    }
}

