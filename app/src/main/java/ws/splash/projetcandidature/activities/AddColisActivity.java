package ws.splash.projetcandidature.activities;

import ws.splash.projetcandidature.R;
import ws.splash.projetcandidature.adapter.ListColisAdapter;
import ws.splash.projetcandidature.dao.ColisDAO;
import ws.splash.projetcandidature.dao.DatabaseHelper;
import ws.splash.projetcandidature.dao.LivreurDAO;
import ws.splash.projetcandidature.model.Colis;
import ws.splash.projetcandidature.model.CustomDialogClass;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

public class AddColisActivity extends Activity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener, View.OnClickListener {

    public static final String TAG = "AddColisActivity";

    public static final int REQUEST_CODE_ADD_COLIS = 40;

    public static final String EXTRA_SELECTED_LIVREUR_ID2 = "extra_key_selected_livreur_id";

    private ListView mListviewColis;
    private TextView mTxtEmptyListColis;
    private ImageButton mBtnBack;

    private ListColisAdapter mAdapter;
    private List<Colis> mListColis;
    private ColisDAO mColisDao;
    private LivreurDAO mLivreurDao;

    private long mLivreurId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_colis);

        // initialize views & Dao
        initViews();
        this.mLivreurDao = new LivreurDAO(this);
        this.mColisDao = new ColisDAO(this);

        // get "Livreur"'s ID from calling activity
        Intent intent = getIntent();
        if (intent != null) {
            this.mLivreurId = intent.getLongExtra(EXTRA_SELECTED_LIVREUR_ID2, -1);

        }

        mListColis = mColisDao.getAllColisALivre();

        // fill the listView
        if (mListColis != null && !mListColis.isEmpty()) {
            mAdapter = new ListColisAdapter(this, mListColis);
            mListviewColis.setAdapter(mAdapter);
        } else {
            mTxtEmptyListColis.setVisibility(View.VISIBLE);
            mListviewColis.setVisibility(View.GONE);
        }
        //show custom dialog box at first run
        doFirstRun();
    }

    //Method to initialize views
    private void initViews() {
        this.mListviewColis = (ListView) findViewById(R.id.list_colis);
        this.mTxtEmptyListColis = (TextView) findViewById(R.id.txt_empty_list_colis);
        this.mBtnBack = (ImageButton) findViewById(R.id.btn_back);
        this.mListviewColis.setOnItemClickListener(this);
        this.mListviewColis.setOnItemLongClickListener(this);
        this.mBtnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;

            default:
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mColisDao.close();
        mLivreurDao.close();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Colis clickedColis = mAdapter.getItem(position);

        Calendar calendar = Calendar.getInstance();
        //Get current hour of the day
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_COLIS_LIVREUR_ID, mLivreurId);

        //Set the selected "colis" to the "livreur" with mLivreurId.
        mColisDao.updateColisLivreurId(clickedColis, mLivreurId);

        //Return the distance between the "colis" and the "centre de distribution".
        double currentDistance = mColisDao.getDistanceCentreColis(clickedColis);

        //Set the "temps de livraison" of the selected "colis" in function of the variable currentDistance
        if (currentDistance < 5) {
            String addedHour = String.valueOf((hourOfDay + 1));

            switch (hourOfDay) {
                case 23:
                    addedHour = "0";
                    break;
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                    addedHour = "9";
                    break;
            }
            clickedColis.setTempsLivraison(addedHour);
            mColisDao.updateColisTempsLivraison(clickedColis, addedHour);
        } else if (isBetween(currentDistance, 5, 10)) {
            String addedHour = String.valueOf((hourOfDay + 2));


            switch (hourOfDay) {
                case 22:
                    addedHour = "0";
                    break;
                case 23:
                    addedHour = "1";
                    break;

                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                    addedHour = "10";
                    break;


            }
            clickedColis.setTempsLivraison(addedHour);
            mColisDao.updateColisTempsLivraison(clickedColis, addedHour);

        } else if (isBetween(currentDistance, 10, 20)) {
            String addedHour = String.valueOf((hourOfDay + 3));

            switch (hourOfDay) {
                case 21:
                    addedHour = "0";
                    break;
                case 22:
                    addedHour = "1";
                    break;
                case 23:
                    addedHour = "2";
                    break;
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                    addedHour = "11";
                    break;
            }
            clickedColis.setTempsLivraison(addedHour);
            mColisDao.updateColisTempsLivraison(clickedColis, addedHour);
        } else if (isBetween(currentDistance, 20, 25)) {
            String addedHour = String.valueOf((hourOfDay + 4));

            switch (hourOfDay) {
                case 20:
                    addedHour = "0";
                    break;
                case 21:
                    addedHour = "1";
                    break;
                case 22:
                    addedHour = "2";
                    break;
                case 23:
                    addedHour = "3";
                    break;
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                    addedHour = "12";
                    break;
            }
            clickedColis.setTempsLivraison(addedHour);
            mColisDao.updateColisTempsLivraison(clickedColis, addedHour);
        } else if (isBetween(currentDistance, 25, 30)) {
            String addedHour = String.valueOf((hourOfDay + 5));

            switch (hourOfDay) {
                case 19:
                    addedHour = "0";
                    break;
                case 20:
                    addedHour = "1";
                    break;
                case 21:
                    addedHour = "2";
                    break;
                case 22:
                    addedHour = "3";
                    break;
                case 23:
                    addedHour = "4";
                    break;
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                    addedHour = "13";
                    break;
            }
            clickedColis.setTempsLivraison(addedHour);
            mColisDao.updateColisTempsLivraison(clickedColis, addedHour);
        } else if (isBetween(currentDistance, 30, 35)) {
            String addedHour = String.valueOf((hourOfDay + 6));

            switch (hourOfDay) {
                case 18:
                    addedHour = "0";
                    break;
                case 19:
                    addedHour = "1";
                    break;
                case 20:
                    addedHour = "2";
                    break;
                case 21:
                    addedHour = "3";
                    break;
                case 22:
                    addedHour = "4";
                    break;
                case 23:
                    addedHour = "5";
                    break;
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                    addedHour = "14";
                    break;
            }
            clickedColis.setTempsLivraison(addedHour);
            mColisDao.updateColisTempsLivraison(clickedColis, addedHour);
        } else if (isBetween(currentDistance, 35, 40)) {
            String addedHour = String.valueOf((hourOfDay + 7));
            switch (hourOfDay) {
                case 17:
                    addedHour = "0";
                    break;
                case 18:
                    addedHour = "1";
                    break;
                case 19:
                    addedHour = "2";
                    break;
                case 20:
                    addedHour = "3";
                    break;
                case 21:
                    addedHour = "4";
                    break;
                case 22:
                    addedHour = "5";
                    break;
                case 23:
                    addedHour = "6";
                    break;
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                    addedHour = "15";
                    break;
            }
            clickedColis.setTempsLivraison(addedHour);
            mColisDao.updateColisTempsLivraison(clickedColis, addedHour);
        } else if (isBetween(currentDistance, 40, 50)) {
            String addedHour = String.valueOf((hourOfDay + 8));

            switch (hourOfDay) {
                case 16:
                    addedHour = "0";
                    break;
                case 17:
                    addedHour = "1";
                    break;
                case 18:
                    addedHour = "2";
                    break;
                case 19:
                    addedHour = "3";
                    break;
                case 20:
                    addedHour = "4";
                    break;
                case 21:
                    addedHour = "5";
                    break;
                case 22:
                    addedHour = "6";
                    break;
                case 23:
                    addedHour = "7";
                    break;
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                    addedHour = "16";
                    break;
            }
            clickedColis.setTempsLivraison(addedHour);
            mColisDao.updateColisTempsLivraison(clickedColis, addedHour);
        } else if (currentDistance > 50) {
            String addedHour = String.valueOf((hourOfDay + 9));

            switch (hourOfDay) {
                case 15:
                    addedHour = "0";
                    break;
                case 16:
                    addedHour = "1";
                    break;
                case 17:
                    addedHour = "2";
                    break;
                case 18:
                    addedHour = "3";
                    break;
                case 19:
                    addedHour = "4";
                    break;
                case 20:
                    addedHour = "5";
                    break;
                case 21:
                    addedHour = "6";
                    break;
                case 22:
                    addedHour = "7";
                    break;
                case 23:
                    addedHour = "8";
                    break;
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                    addedHour = "17";
                    break;
            }
            clickedColis.setTempsLivraison(addedHour);
            mColisDao.updateColisTempsLivraison(clickedColis, addedHour);
        }


        // Refresh listView
        mAdapter.notifyDataSetChanged();

        //Go back to the ListColisActivity of the specific "livreur" selected with the added "colis".
        Intent intent = new Intent(this, ListColisActivity.class);
        intent.putExtra(ListColisActivity.EXTRA_SELECTED_LIVREUR_ID, mLivreurId);
        intent.putExtra(ListColisActivity.EXTRA_SELECTED_LIVREUR_NOM, mLivreurDao.getLivreurById(mLivreurId).getNom());
        startActivityForResult(intent, REQUEST_CODE_ADD_COLIS);


        finish();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

    //Method to know if a value is between two others values
    public static boolean isBetween(double x, int lower, int upper) {
        return lower <= x && x <= upper;
    }

    //Method to run a custom dialog box at first run of the app
    private void doFirstRun() {
        SharedPreferences settings = getSharedPreferences("saveopenApp", MODE_PRIVATE);
        if (settings.getBoolean("isFirstRunColisALivre", true)) {

            CustomDialogClass cdd = new CustomDialogClass();
            String msg = "Dans ce menu vous pourrez voir <font color='#cccccc'>tous les colis éligible</font> pour une livraison.";
            String msg2 = "Faites votre choix ;)";


            cdd.showDialog(this, "Les colis à envoyer", Html.fromHtml(msg2));
            cdd.showDialog(this, "Les colis à envoyer", Html.fromHtml(msg));


            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("isFirstRunColisALivre", false);
            editor.commit();
        }
    }
}
