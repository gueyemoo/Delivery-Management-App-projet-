package ws.splash.projetcandidature.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ws.splash.projetcandidature.model.Colis;
import ws.splash.projetcandidature.model.Livreur;

public class ColisDAO {

    public static final String TAG = "ColisDAO";

    private Context mContext;

    // Database fields
    private SQLiteDatabase mDatabase;
    private DatabaseHelper mDbHelper;
    private String[] mAllColumns = {DatabaseHelper.COLUMN_COLIS_ID,
            DatabaseHelper.COLUMN_COLIS_NOM,
            DatabaseHelper.COLUMN_COLIS_X,
            DatabaseHelper.COLUMN_COLIS_Y,
            DatabaseHelper.COLUMN_COLIS_DATE,
            DatabaseHelper.COLUMN_COLIS_STATUT,
            DatabaseHelper.COLUMN_COLIS_TEMPSLIVRAISON,
            DatabaseHelper.COLUMN_COLIS_LIVREUR_ID};

    public ColisDAO(Context context) {
        mDbHelper = new DatabaseHelper(context);
        this.mContext = context;
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

    //Method to create a new object "colis"
    public Colis createColis(String nom, Integer colis_x,
                             Integer colis_y, String date, String statut, String tempslivraison, long livreurId) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_COLIS_NOM, nom);
        values.put(DatabaseHelper.COLUMN_COLIS_X, colis_x);
        values.put(DatabaseHelper.COLUMN_COLIS_Y, colis_y);
        values.put(DatabaseHelper.COLUMN_COLIS_DATE, date);
        values.put(DatabaseHelper.COLUMN_COLIS_STATUT, statut);
        values.put(DatabaseHelper.COLUMN_COLIS_TEMPSLIVRAISON, tempslivraison);
        values.put(DatabaseHelper.COLUMN_COLIS_LIVREUR_ID, livreurId);
        long insertId = mDatabase.insert(DatabaseHelper.TABLE_COLIS, null, values);
        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_COLIS, mAllColumns,
                DatabaseHelper.COLUMN_COLIS_ID + " = " + insertId, null, null,
                null, null);
        cursor.moveToFirst();
        Colis nouveauColis = cursorToColis(cursor);
        cursor.close();
        return nouveauColis;
    }

    //Method to delete a "colis"
    public void deleteColis(Colis colis) {
        long id = colis.getId();
        System.out.println("the deleted colis has the id: " + id);
        mDatabase.delete(DatabaseHelper.TABLE_COLIS, DatabaseHelper.COLUMN_COLIS_ID
                + " = " + id, null);
    }

    //Method to calculate the square root
    private double sqr(double x) { // retourne x²
        return x * x;
    }

    //Method to round double values
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    //Method to get the distance (in Km) between the "centre de distribution" and the "colis"'s place of delivery
    public double getDistanceCentreColis(Colis c) {
        return Math.sqrt(sqr(c.getColis_X() - 0) + sqr(c.getColis_Y() - 0)); //Pythagore's theorem
    }

    //Method to get the distance (in Km) between the "colis"'s place of delivery and the "livreur" home
    public double getDistanceColisDomicileLivreur(Colis c) {
        return Math.sqrt(sqr(c.getLivreur().getLivreur_X() - c.getColis_X()) + sqr(c.getLivreur().getLivreur_Y() - c.getColis_Y())); //Pythagore's theorem
    }

    //Method to update a specific "colis" attribute (statut)
    public int updateColisStatut(Colis c, String statut) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_COLIS_STATUT, statut);

        return mDatabase.update(DatabaseHelper.TABLE_COLIS, values, DatabaseHelper.COLUMN_COLIS_ID + " =?", new String[]{String.valueOf(c.getId())});
    }

    //Method to update a specific "colis" attribute (date)
    public int updateColisDate(Colis c, String date) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_COLIS_DATE, date);

        return mDatabase.update(DatabaseHelper.TABLE_COLIS, values, DatabaseHelper.COLUMN_COLIS_ID + " =?", new String[]{String.valueOf(c.getId())});
    }

    //Method to update a specific "colis" attribute (tempsLivraison)
    public int updateColisTempsLivraison(Colis c, String tempslivraison) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_COLIS_TEMPSLIVRAISON, tempslivraison);

        return mDatabase.update(DatabaseHelper.TABLE_COLIS, values, DatabaseHelper.COLUMN_COLIS_ID + " =?", new String[]{String.valueOf(c.getId())});
    }

    //Method to update a specific "colis" attribute (livreurId)
    public int updateColisLivreurId(Colis c, long id) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_COLIS_LIVREUR_ID, id);

        return mDatabase.update(DatabaseHelper.TABLE_COLIS, values, DatabaseHelper.COLUMN_COLIS_ID + " =?", new String[]{String.valueOf(c.getId())});
    }

    //Method to update all "colis" attributes that the user can modify
    public int updateColis(long id, String nom, Integer x, Integer y, String date) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_COLIS_NOM, nom);
        values.put(DatabaseHelper.COLUMN_COLIS_X, x);
        values.put(DatabaseHelper.COLUMN_COLIS_Y, y);
        values.put(DatabaseHelper.COLUMN_COLIS_DATE, date);
        return mDatabase.update(DatabaseHelper.TABLE_COLIS, values, DatabaseHelper.COLUMN_COLIS_ID + " =?", new String[]{String.valueOf(id)});
    }

    //Method to get all "colis" from the database
    public List<Colis> getAllColis() {
        List<Colis> listColis = new ArrayList<Colis>();

        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_COLIS, mAllColumns,
                null, null, null, null, DatabaseHelper.COLUMN_COLIS_DATE + " DESC");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Colis colis = cursorToColis(cursor);
            listColis.add(colis);
            cursor.moveToNext();
        }
        cursor.close();
        return listColis;
    }

    //Method to get all "colis" of a specific "Livreur" from the database
    public List<Colis> getColisDeLivreur(long livreurId) {
        List<Colis> listColis = new ArrayList<Colis>();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_COLIS, mAllColumns,
                DatabaseHelper.COLUMN_COLIS_LIVREUR_ID + " = ?",
                new String[]{String.valueOf(livreurId)}, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Colis colis = cursorToColis(cursor);
            if (colis.getStatut().equals("1") && (colis.getDate().equals(currentDate))) { //if the "colis" was delivered today, we add it, else we don't add it to not surcharge the listView
                listColis.add(colis);
            } else if (colis.getStatut().equals("0") || colis.getStatut().equals("-1")) { // if the "colis" will be delivered today, we add it, else we don't
                listColis.add(colis);
            }
            cursor.moveToNext();
        }
        cursor.close();
        return listColis;
    }

    //Method to get all "colis" from the distribution center
    public List<Colis> getAllColisDisponible() {
        List<Colis> listColis = new ArrayList<Colis>();

        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_COLIS, mAllColumns,
                null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Colis colis = cursorToColis(cursor);
                if (colis.getLivreur().getNom().equals("Aucun livreur attribuer")) { //equivalent to : if it's in the "centre de distribution"
                    listColis.add(colis);
                }
                cursor.moveToNext();
            }
            cursor.close();
        }
        return listColis;
    }

    //Method to get all "colis" to deliver today
    public List<Colis> getAllColisALivre() {
        List<Colis> listColis = new ArrayList<Colis>();

        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_COLIS, mAllColumns,
                null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Colis colis = cursorToColis(cursor);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
                String currentDate = sdf.format(new Date());
                Date currentDateFormat = null;
                try {
                    currentDateFormat = new SimpleDateFormat("dd/MM/yy").parse(currentDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Date dateColis = null;
                try {
                    dateColis = new SimpleDateFormat("dd/MM/yy").parse(colis.getDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (dateColis != null && currentDateFormat != null) {
                    if (colis.getStatut().equals("-1") && (dateColis.equals(currentDateFormat) || dateColis.before(currentDateFormat))) {//Check if the colis is eligible to be delivered today, so if the delivery date is past or equals to today
                        listColis.add(colis);
                    }
                }
                cursor.moveToNext();
            }
            cursor.close();
        }
        return listColis;
    }

    //Method to get all "colis" delivered today
    public List<Colis> getAllColisLivre() {
        List<Colis> listColis = new ArrayList<Colis>();


        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_COLIS, mAllColumns,
                null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Colis colis = cursorToColis(cursor);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
                String currentDate = sdf.format(new Date());
                Date currentDateFormat = null;
                try {
                    currentDateFormat = new SimpleDateFormat("dd/MM/yy").parse(currentDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Date dateColis = null;
                try {
                    dateColis = new SimpleDateFormat("dd/MM/yy").parse(colis.getDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (dateColis != null && currentDateFormat != null) {
                    if (colis.getStatut().equals("1") && dateColis.equals(currentDateFormat)) {
                        listColis.add(colis);
                    }
                }
                cursor.moveToNext();
            }
            cursor.close();
        }
        return listColis;
    }

    //Method to get all "colis" in delivery
    public List<Colis> getAllColisEnLivraison() {
        List<Colis> listColis = new ArrayList<Colis>();

        Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_COLIS, mAllColumns,
                null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Colis colis = cursorToColis(cursor);
                if (colis.getStatut().equals("0")) {
                    listColis.add(colis);
                }
                cursor.moveToNext();
            }
            cursor.close();
        }
        return listColis;
    }

    //Method to set cursor index for the "Colis" object
    private Colis cursorToColis(Cursor cursor) {
        Colis colis = new Colis();
        colis.setId(cursor.getLong(0));
        colis.setNom(cursor.getString(1));
        colis.setColis_X(cursor.getInt(2));
        colis.setColis_Y(cursor.getInt(3));
        colis.setDate(cursor.getString(4));
        colis.setStatut(cursor.getString(5));
        colis.setTempsLivraison(cursor.getString(6));

        // get The livreur by id
        long livreurId = cursor.getLong(7);
        LivreurDAO dao = new LivreurDAO(mContext);
        Livreur livreur = dao.getLivreurById(livreurId);
        if (livreur != null)
            colis.setLivreur(livreur);

        return colis;
    }
}
