package ws.splash.projetcandidature.model;

import java.io.Serializable;

public class Colis implements Serializable {
    public static final String TAG = "Colis";
//	private static final long serialVersionUID = -7406082437623008161L;

    private long mId;
    private String mNom;
    private Integer mColis_X;
    private Integer mColis_Y;
    private Boolean mEstLivrer;
    private String mDate;
    private String mStatut;
    private String mTempsLivraison;
    private Livreur mLivreur;


    public Colis() {
    }

    public Colis(String nom, Integer colis_x, Integer colis_y, String date, String statut, String tempslivraison) {
        this.mNom = nom;
        this.mColis_X = colis_x;
        this.mColis_Y = colis_y;
        this.mDate = date;
        this.mStatut = statut;
        this.mTempsLivraison = tempslivraison;
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

    public Integer getColis_X() {
        return mColis_X;
    }

    public void setColis_X(Integer mColis_X) {
        this.mColis_X = mColis_X;
    }

    public Integer getColis_Y() {
        return mColis_Y;
    }

    public void setColis_Y(Integer mColis_Y) {
        this.mColis_Y = mColis_Y;
    }

    public Boolean getEstLivrer() {
        return mEstLivrer;
    }

    public void setEstLivrer(Boolean mEstLivrer) {
        this.mEstLivrer = mEstLivrer;
    }

    public Livreur getLivreur() {
        return mLivreur;
    }

    public void setLivreur(Livreur mLivreur) {
        this.mLivreur = mLivreur;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String mDate) {
        this.mDate = mDate;
    }

    public String getStatut() {
        return mStatut;
    }

    public void setStatut(String mStatut) {
        this.mStatut = mStatut;
    }

    public String getTempsLivraison() {
        return mTempsLivraison;
    }

    public void setTempsLivraison(String mTempsLivraison) {
        this.mTempsLivraison = mTempsLivraison;
    }
}
