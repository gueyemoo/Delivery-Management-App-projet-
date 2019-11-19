package ws.splash.projetcandidature.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TAG = "DatabaseHelper";

    // columns of the livreurs table
    public static final String TABLE_LIVREURS = "livreurs";
    public static final String COLUMN_LIVREUR_ID = "id";
    public static final String COLUMN_LIVREUR_NOM = "nom";
    public static final String COLUMN_LIVREUR_X = "livreur_x";
    public static final String COLUMN_LIVREUR_Y = "livreur_y";
    public static final String COLUMN_LIVREUR_DISPONIBILITE = "disponibilite";
    public static final String COLUMN_LIVREUR_PARCOURSKM = "distance_parcouru";
    public static final String COLUMN_LIVREUR_STATUT = "livreur_statut";


    // columns of the colis table
    public static final String TABLE_COLIS = "colis";
    public static final String COLUMN_COLIS_ID = COLUMN_LIVREUR_ID;
    public static final String COLUMN_COLIS_NOM = "nom";
    public static final String COLUMN_COLIS_X = "colis_x";
    public static final String COLUMN_COLIS_Y = "colis_y";
    public static final String COLUMN_COLIS_DATE = "colis_date";
    public static final String COLUMN_COLIS_STATUT = "colis_statut";
    public static final String COLUMN_COLIS_TEMPSLIVRAISON = "colis_tempslivraison";
    public static final String COLUMN_COLIS_LIVREUR_ID = "livreur_id";

    private static final String DATABASE_NAME = "Database.db";
    private static final int DATABASE_VERSION = 3;

    // SQL statement of the livreur table creation
    private static final String SQL_CREATE_TABLE_LIVREURS = "CREATE TABLE " + TABLE_LIVREURS + "("
            + COLUMN_LIVREUR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_LIVREUR_NOM + " TEXT NOT NULL, "
            + COLUMN_LIVREUR_X + " INTEGER NOT NULL, "
            + COLUMN_LIVREUR_Y + " INTEGER NOT NULL, "
            + COLUMN_LIVREUR_DISPONIBILITE + " TEXT NOT NULL, "
            + COLUMN_LIVREUR_PARCOURSKM + " REAL NOT NULL, "
            + COLUMN_LIVREUR_STATUT + " TEXT NOT NULL "
            + ");";

    // SQL statement of the colis table creation
    private static final String SQL_CREATE_TABLE_COLIS = "CREATE TABLE " + TABLE_COLIS + "("
            + COLUMN_COLIS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_COLIS_NOM + " TEXT NOT NULL, "
            + COLUMN_COLIS_X + " INTEGER NOT NULL, "
            + COLUMN_COLIS_Y + " INTEGER NOT NULL, "
            + COLUMN_COLIS_DATE + " TEXT NOT NULL, "
            + COLUMN_COLIS_STATUT + " TEXT NOT NULL, "
            + COLUMN_COLIS_TEMPSLIVRAISON + " TEXT NOT NULL, "
            + COLUMN_COLIS_LIVREUR_ID + " INTEGER NOT NULL"
            + ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(SQL_CREATE_TABLE_LIVREURS);
        database.execSQL(SQL_CREATE_TABLE_COLIS);

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy");
        String formattedCurrentDate = df.format(Calendar.getInstance().getTime());
        String formattedTomorrowDate = df.format(Calendar.getInstance().getTime());
        String formattedAfterTomorrowDate = df.format(Calendar.getInstance().getTime());

        Calendar c = Calendar.getInstance();
        try {
            c.setTime(df.parse(formattedTomorrowDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, 1);  // add 1 to get the next day
        formattedTomorrowDate = df.format(c.getTime());

        Calendar cBis = Calendar.getInstance();
        try {
            cBis.setTime(df.parse(formattedAfterTomorrowDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cBis.add(Calendar.DATE, 2);  // add 2 to get two days after
        formattedAfterTomorrowDate = df.format(cBis.getTime());

        //adding 10 livreurs by default to the database
        database.execSQL("INSERT INTO " + TABLE_LIVREURS + "(nom, livreur_x, livreur_y, disponibilite, distance_parcouru, livreur_statut) VALUES ('Aucun livreur attribuer', 0, 0, 'Disponible', 0.0, '-1'), ('Jerome', 6, -11, 'Disponible', 0.0, '-1'), ('Barthelemy', 13, 22, 'Indisponible', 0.0, '-1'), ('Marion', 21, 11, 'Disponible', 0.0, '-1'), ('Adam', -22, 18, 'Indisponible', 0.0, '-1'), ('Nemir', 14, -21, 'Indisponible', 0.0, '-1'), ('Zephyr', -25, -1, 'Disponible', 0.0, '-1'), ('Alicia', 16, -9, 'Disponible', 0.0, '-1'), ('Conrad', 3, 13, 'Disponible', 0.0, '-1'), ('Cynthia', 4, 2, 'Disponible', 0.0, '-1')");

        //adding 10 colis by default to the database with current date
        database.execSQL("INSERT INTO " + TABLE_COLIS + "(nom, colis_x, colis_y, colis_date, colis_statut, colis_tempslivraison, livreur_id) VALUES ('APEMAN Lecteur DVD Portable', 11, 8, '" + formattedCurrentDate + "', '-1', '0', 1), ('Lenovo Ideapad C340-14IWL', 10, 18, '" + formattedCurrentDate + "', '-1', '0', 1), ('UGREEN CAT Câble Ethernet', 22, -1, '" + formattedCurrentDate + "', '-1', '0', 1), ('HP DeskJet Imprimante', -2, 24, '" + formattedCurrentDate + "', '-1', '0', 1), ('Asus ZenBook UX533FD', 4, 7, '" + formattedCurrentDate + "', '-1', '0', 1), ('Samsung U28E590D Écran', 7, 14, '" + formattedCurrentDate + "', '-1', '0', 1),('Carte Graphique Quadro P620', -2, -9, '" + formattedCurrentDate + "', '-1', '0', 1), ('HUAWEI MediaPad M5', -6, -6, '" + formattedCurrentDate + "', '-1', '0', 1), ('Canon 2996C010 Scanner', -13, 9, '" + formattedCurrentDate + "', '-1', '0', 1), ('Beexcellent Micro Casque', 23, 12, '" + formattedCurrentDate + "', '-1', '0', 1) ");

        //adding 10 colis by default to the database with tomorrow date
        database.execSQL("INSERT INTO " + TABLE_COLIS + "(nom, colis_x, colis_y, colis_date, colis_statut, colis_tempslivraison, livreur_id) VALUES ('HUAWEI P20 PRO', -32, -18, '" + formattedTomorrowDate + "', '-1', '0', 1), ('Thinkpad Lenovo', 40, -78, '" + formattedTomorrowDate + "', '-1', '0', 1), ('Microsoft Surface Pro 3', 8, -52, '" + formattedTomorrowDate + "', '-1', '0', 1), ('Playsation 4', -23, 54, '" + formattedTomorrowDate + "', '-1', '0', 1), ('MacBook Air', 32, -83, '" + formattedTomorrowDate + "', '-1', '0', 1), ('Canon EOS 4000D', 33, 26, '" + formattedTomorrowDate + "', '-1', '0', 1),('Sony DSC-HX350', -28, -29, '" + formattedTomorrowDate + "', '-1', '0', 1), ('Echo Dot', -26, 43, '" + formattedTomorrowDate + "', '-1', '0', 1), ('Ampoule LED WiFi E27', -2, 2, '" + formattedTomorrowDate + "', '-1', '0', 1), ('JBL Flip 5', 6, 4, '" + formattedTomorrowDate + "', '-1', '0', 1) ");

        //adding 5 colis by default to the database with "date" set after tomorrow date
        database.execSQL("INSERT INTO " + TABLE_COLIS + "(nom, colis_x, colis_y, colis_date, colis_statut, colis_tempslivraison, livreur_id) VALUES ('NinkBox Android TV', 12, 8, '" + formattedAfterTomorrowDate + "', '-1', '0', 1), ('EasySMX Manette', 18, -24, '" + formattedAfterTomorrowDate + "', '-1', '0', 1), ('Apple Watch', -34, -5, '" + formattedAfterTomorrowDate + "', '-1', '0', 1), ('Focusrite Scarlett Solo', -55, 4, '" + formattedAfterTomorrowDate + "', '-1', '0', 1), ('Acer Nitro 5', 69, -8, '" + formattedAfterTomorrowDate + "', '-1', '0', 1) ");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG,
                "Upgrading the database from version " + oldVersion + " to " + newVersion);
        // clear all data
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIVREURS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COLIS);

        // recreate the tables
        onCreate(db);
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }
}
