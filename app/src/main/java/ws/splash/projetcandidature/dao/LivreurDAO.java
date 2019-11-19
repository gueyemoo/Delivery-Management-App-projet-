package ws.splash.projetcandidature.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ws.splash.projetcandidature.model.Colis;
import ws.splash.projetcandidature.model.Livreur;

public class LivreurDAO {
    public static final String TAG = "LivreurDAO";

    // Database fields
    private SQLiteDatabase mDatabase;
    private DatabaseHelper mDbHelper;
    private Context mContext;
    private String[] mAllColumns = {DatabaseHelper.COLUMN_LIVREUR_ID,
            DatabaseHelper.COLUMN_LIVREUR_NOM,
            DatabaseHelper.COLUMN_LIVREUR_X,
            DatabaseHelper.COLUMN_LIVREUR_Y,
            DatabaseHelper.COLUMN_LIVREUR_DISPONIBILITE,
            DatabaseHelper.COLUMN_LIVREUR_PARCOURSKM,
            DatabaseHelper.COLUMN_LIVREUR_STATUT};

    public LivreurDAO(Context context) {
        this.mContext = context;
        mDbHelper = new DatabaseHelper(context);
        // open the database
        try {
            open();
        } catch (SQLException e) {
            Log.e(TAG, "SQLException on openning database " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void open() throws SQLException {
        mDatabase = mDbHelper.getWritableDatabase();
    }

    public void close() {
        mDbHelper.close();
    }

    //Method to create a new object "livreur"
    public Livreur createLivreur(String nom, int livreur_x, int livreur_y, String disponibilite, double parcoursKM, String statut) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_LIVREUR_NOM, nom);
        values.put(DatabaseHelper.COLUMN_LIVREUR_X, livreur_x);
        values.put(DatabaseHelper.COLUMN_LIVREUR_Y, livreur_y);
        values.put(DatabaseHelper.COLUMN_LIVREUR_DISPONIBILITE, disponibilite);
        values.put(DatabaseHelper.COLUMN_LIVREUR_PARCOURSKM, parcoursKM);
        values.put(DatabaseHelper.COLUMN_LIVREUR_STATUT, statut);

        long insertId = mDatabase.insert(DatabaseHelper.TABLE_LIVREURS, null, values);
        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_LIVREURS, mAllColumns,
                DatabaseHelper.COLUMN_LIVREUR_ID + " = " + insertId, null, null,
                null, null);

        Livreur nouveauLivreur = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                nouveauLivreur = cursorToLivreur(cursor);
            }
        }
        cursor.close();

        return nouveauLivreur;
    }

    //Method to update all "livreur" attributes that the user can modify
    public int updateLivreur(long id, String nom, Integer x, Integer y, String disponibilite) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_LIVREUR_NOM, nom);
        values.put(DatabaseHelper.COLUMN_LIVREUR_X, x);
        values.put(DatabaseHelper.COLUMN_LIVREUR_Y, y);
        values.put(DatabaseHelper.COLUMN_LIVREUR_DISPONIBILITE, disponibilite);
        return mDatabase.update(DatabaseHelper.TABLE_LIVREURS, values, DatabaseHelper.COLUMN_LIVREUR_ID + " =?", new String[]{String.valueOf(id)});
    }

    //Method to update a specific "livreur" attribute (disponibilite)
    public int updateLivreurDisponibilite(Livreur liv, String disponibilite) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_LIVREUR_DISPONIBILITE, disponibilite);

        return mDatabase.update(DatabaseHelper.TABLE_LIVREURS, values, DatabaseHelper.COLUMN_LIVREUR_ID + " =?", new String[]{String.valueOf(liv.getId())});
    }

    //Method to update a specific "livreur" attribute (distance_total)
    public double updateLivreurParcours(Livreur liv, double distance_total) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_LIVREUR_PARCOURSKM, distance_total);

        return mDatabase.update(DatabaseHelper.TABLE_LIVREURS, values, DatabaseHelper.COLUMN_LIVREUR_ID + " =?", new String[]{String.valueOf(liv.getId())});
    }

    //Method to update a specific "livreur" attribute (statut)
    public int updateLivreurStatut(Livreur liv, String statut) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_LIVREUR_STATUT, statut);

        return mDatabase.update(DatabaseHelper.TABLE_LIVREURS, values, DatabaseHelper.COLUMN_LIVREUR_ID + " =?", new String[]{String.valueOf(liv.getId())});
    }

    //Method to delete a "livreur"
    public void deleteLivreur(Livreur livreur) {
        long id = livreur.getId();
        // delete all colis of this livreur
        ColisDAO colisDao = new ColisDAO(mContext);
        List<Colis> listColis = colisDao.getColisDeLivreur(id);
        if (listColis != null && !listColis.isEmpty()) {
            for (Colis e : listColis) {
                colisDao.deleteColis(e);
            }
        }

        System.out.println("the deleted livreur has the id: " + id);
        mDatabase.delete(DatabaseHelper.TABLE_LIVREURS, DatabaseHelper.COLUMN_LIVREUR_ID
                + " = " + id, null);
    }

    //Method to get all "livreur" from the database except the first one which is the distribution center
    public List<Livreur> getAllLivreurs() {
        List<Livreur> listLivreurs = new ArrayList<Livreur>();

        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_LIVREURS, mAllColumns,
                null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            cursor.moveToNext();//avoid the first one
            while (!cursor.isAfterLast()) {
                Livreur livreur = cursorToLivreur(cursor);
                listLivreurs.add(livreur);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return listLivreurs;
    }


    //Method to get all "livreur" with the disponibility attribute to "Disponible" except the first one
    public List<Livreur> getAllLivreursDisponible() {
        List<Livreur> listLivreurs = new ArrayList<Livreur>();

        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_LIVREURS, mAllColumns,
                null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            cursor.moveToNext(); // avoid the first one
            while (!cursor.isAfterLast()) {
                Livreur livreur = cursorToLivreur(cursor);
                if (livreur.getDisponibilite().equals("Disponible")) {
                    listLivreurs.add(livreur);
                }
                cursor.moveToNext();
            }
            cursor.close();
        }
        return listLivreurs;
    }

    //Method to get all "livreur" with the disponibility attribute to "Indisponible" except the first one
    public List<Livreur> getAllLivreursIndisponible() {
        List<Livreur> listLivreurs = new ArrayList<Livreur>();

        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_LIVREURS, mAllColumns,
                null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            cursor.moveToNext(); // avoid the first one
            while (!cursor.isAfterLast()) {
                Livreur livreur = cursorToLivreur(cursor);
                if (livreur.getDisponibilite().equals("Indisponible")) {
                    listLivreurs.add(livreur);
                }
                cursor.moveToNext();
            }
            cursor.close();
        }
        return listLivreurs;
    }

    //Method to get all "livreur" with the statut attribute to "En livraison" except the first one
    public List<Livreur> getAllLivreursEnLivraison() {
        List<Livreur> listLivreurs = new ArrayList<Livreur>();

        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_LIVREURS, mAllColumns,
                null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            cursor.moveToNext(); // avoid the first one
            while (!cursor.isAfterLast()) {
                Livreur livreur = cursorToLivreur(cursor);
                if (Double.compare(livreur.getParcoursKM(), 0) == 1) {
                    listLivreurs.add(livreur);
                }
                cursor.moveToNext();
            }
            cursor.close();
        }
        return listLivreurs;
    }

    //Method to get all "livreur" with the disponibility attribute to "Disponible" AND the first one
    public List<Livreur> getAllLivreursDisponible_and_CentreDitribution() {
        List<Livreur> listLivreurs = new ArrayList<Livreur>();

        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_LIVREURS, mAllColumns,
                null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Livreur livreur = cursorToLivreur(cursor);
                if (livreur.getDisponibilite().equals("Disponible")) {
                    listLivreurs.add(livreur);
                }
                cursor.moveToNext();
            }
            cursor.close();
        }
        return listLivreurs;
    }

    //Method to get a "Livreur" by his attribute id
    public Livreur getLivreurById(long id) {
        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_LIVREURS, mAllColumns,
                DatabaseHelper.COLUMN_LIVREUR_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        Livreur livreur = cursorToLivreur(cursor);
        return livreur;
    }


    //Method to set cursor index for the "Livreur" object
    protected Livreur cursorToLivreur(Cursor cursor) {
        Livreur livreur = new Livreur();
        livreur.setId(cursor.getLong(0));
        livreur.setNom(cursor.getString(1));
        livreur.setLivreur_X(cursor.getInt(2));
        livreur.setLivreur_Y(cursor.getInt(3));
        livreur.setDisponibilite(cursor.getString(4));
        livreur.setParcoursKM(cursor.getFloat(5));
        livreur.setStatut(cursor.getString(6));
        return livreur;
    }
}
