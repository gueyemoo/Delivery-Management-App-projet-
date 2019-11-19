package ws.splash.projetcandidature.activities;

import ws.splash.projetcandidature.R;
import ws.splash.projetcandidature.adapter.ListColisAdapter;
import ws.splash.projetcandidature.dao.ColisDAO;
import ws.splash.projetcandidature.dao.LivreurDAO;
import ws.splash.projetcandidature.model.Colis;
import ws.splash.projetcandidature.model.CustomDialogClass;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ListColisActivity extends Activity implements View.OnClickListener {

    public static final String TAG = "ListColisActivity";

    public static final int REQUEST_CODE_ADD_COLIS = 40;

    public static final String EXTRA_SELECTED_LIVREUR_ID = "extra_key_selected_livreur_id";
    public static final String EXTRA_SELECTED_LIVREUR_NOM = "extra_key_selected_livreur_nom";

    private ListView mListviewColis;
    private TextView mTxtEmptyListColis;
    private TextView header;
    private TextView mTxtParcoursKm;
    private ImageButton mBtnAddColis;
    private ImageButton mBtnBack;


    private ListColisAdapter mAdapter;
    private List<Colis> mListColis;

    private ColisDAO mColisDao;
    private LivreurDAO mLivreurDao;

    private long mLivreurId = -1;
    private String mLivreurNom;
    double total_distance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_colis);

        // initialize views
        initViews();

        // initialize DAO
        mColisDao = new ColisDAO(this);
        mLivreurDao = new LivreurDAO(this);

        // Get the "livreur"'s id & name from extras
        Intent intent = getIntent();
        if (intent != null) {
            this.mLivreurId = intent.getLongExtra(EXTRA_SELECTED_LIVREUR_ID, -1);
            this.mLivreurNom = intent.getStringExtra(EXTRA_SELECTED_LIVREUR_NOM);
        }

        if (mLivreurId != -1) {
            mListColis = mColisDao.getColisDeLivreur(mLivreurId);
            // fill the listView
            if (mListColis != null && !mListColis.isEmpty()) {
                for (int i = 0; i < mListColis.size(); i++) {
                    if (mListColis.get(i).getStatut().equals("-1")) {
                        // Set "colis"'s statut which are affected to a "livreur" to "0" (en cours de livraison)
                        mColisDao.updateColisStatut(mListColis.get(i), "0");
                    }
                }
                // Refresh the listView
                mListColis = new ArrayList<Colis>();
                mColisDao = new ColisDAO(this);
                mListColis = mColisDao.getColisDeLivreur(mLivreurId);
                mAdapter = new ListColisAdapter(this, mListColis);
                mListviewColis.setAdapter(mAdapter);
            } else {
                mTxtEmptyListColis.setVisibility(View.VISIBLE);
                mListviewColis.setVisibility(View.GONE);
                mTxtParcoursKm.setVisibility(View.GONE);
            }

        }

        if (mColisDao == null) {
            mColisDao = new ColisDAO(this);
        }
        mListColis = mColisDao.getColisDeLivreur(mLivreurId);
        for (int i = 0; i < mListColis.size(); i++) {
            double distance = mColisDao.getDistanceCentreColis(mListColis.get(i));
            if (mListColis.get(i).getStatut().equals("1")) { //If the "colis" is already delivered set the distance to 0
                distance = 0;
            }
            //Get the total distance the "livreur" as to do and update it
            total_distance = total_distance + distance;
            mLivreurDao.updateLivreurParcours(mListColis.get(i).getLivreur(), total_distance);

            float dist = 0;
            //update Livreur statut in function of the current situation
            if (mListColis.get(i).getLivreur().getDisponibilite().equals("Indisponible")) {
                mLivreurDao.updateLivreurStatut(mListColis.get(i).getLivreur(), "-1"); //statut: Inconnu
            } else if (mListColis.get(i).getLivreur().getDisponibilite().equals("Disponible") && (mListColis.get(i).getLivreur().getParcoursKM() == dist)) {
                mLivreurDao.updateLivreurStatut(mListColis.get(i).getLivreur(), "1"); //statut: A son domicile
            } else if (mListColis.get(i).getLivreur().getDisponibilite().equals("Disponible") && (mListColis.get(i).getLivreur().getParcoursKM() != dist)) {
                mLivreurDao.updateLivreurStatut(mListColis.get(i).getLivreur(), "0"); //statut: en cours de livraison
            }
        }

        //rounding the total distance to 2 (format: 0.00)
        total_distance = mColisDao.round(total_distance, 2);
        String strDistance = String.valueOf(total_distance);

        // Display the total distance
        mTxtParcoursKm.setText("Distance à parcourir : " + strDistance + " Km");
        header.setText(header.getText() + " " + mLivreurNom);

        // Show custom dialog box at first run
        doFirstRun();
    }

    //Method to initialize views
    private void initViews() {
        this.mListviewColis = (ListView) findViewById(R.id.list_colis);
        this.mTxtEmptyListColis = (TextView) findViewById(R.id.txt_empty_list_colis);
        this.mTxtParcoursKm = (TextView) findViewById(R.id.txt_parcours_km);
        this.mBtnAddColis = (ImageButton) findViewById(R.id.btn_add_colis);
        this.mBtnBack = (ImageButton) findViewById(R.id.btn_back);
        this.header = (TextView) findViewById(R.id.txt_header);
        this.mBtnAddColis.setOnClickListener(this);
        this.mBtnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_colis:
                if (total_distance <= 150) { //150 is the max distance for a "livreur" in a day
                    Intent intent = new Intent(this, AddColisActivity.class);
                    intent.putExtra(AddColisActivity.EXTRA_SELECTED_LIVREUR_ID2, mLivreurId);
                    startActivityForResult(intent, REQUEST_CODE_ADD_COLIS);
                } else {
                    Toast.makeText(ListColisActivity.this, mLivreurNom + " a déjà plus de 150km à parcourir aujourd'hui.", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.btn_back:
                finish();
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ADD_COLIS) { //When a "colis" is added to a "livreur"
            if (resultCode == RESULT_OK) {
                //refresh the listView
                if (mListColis == null || !mListColis.isEmpty()) {
                    mListColis = new ArrayList<Colis>();
                }

                if (mColisDao == null)
                    mColisDao = new ColisDAO(this);
                mListColis = mColisDao.getColisDeLivreur(mLivreurId);


                if (mAdapter == null) {
                    mAdapter = new ListColisAdapter(this, mListColis);
                    mListviewColis.setAdapter(mAdapter);

                    if (mListviewColis.getVisibility() != View.VISIBLE) {
                        mTxtEmptyListColis.setVisibility(View.GONE);
                        mListviewColis.setVisibility(View.VISIBLE);
                    }
                } else {
                    mAdapter.setItems(mListColis);
                    mAdapter.notifyDataSetChanged();
                }
            }
        } else
            super.onActivityResult(requestCode, resultCode, data);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mColisDao.close();
        mLivreurDao.close();
    }


    //Method to run a custom dialog box at first run of the app
    private void doFirstRun() {
        SharedPreferences settings = getSharedPreferences("saveopenApp", MODE_PRIVATE);

        if (settings.getBoolean("isFirstRunColisDeLivreur", true)) {

            CustomDialogClass cdd = new CustomDialogClass();
            String msg = "Ici vous pourrez voir <font color='#cccccc'>tous les colis livré ou en cours de livraison</font> de ce livreur.";
            String msg2 = "En plus de cela vous aurez des informations sur <font color='#cccccc'>le temps que mettra un colis pour être livré</font> et <font color='#cccccc'>la distance totale</font> que le livreur devra parcourir.";
            String msg3 = "Affectez à " + mLivreurNom + " son premier colis ;) <br> <br> <u>N.B:</u> Un livreur est limité à une distance de 150km par jour. ";


            cdd.showDialog(this, "Que fait " + mLivreurNom + " ? ", Html.fromHtml(msg3));
            cdd.showDialog(this, "Que fait " + mLivreurNom + " ? ", Html.fromHtml(msg2));
            cdd.showDialog(this, "Que fait " + mLivreurNom + " ? ", Html.fromHtml(msg));


            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("isFirstRunColisDeLivreur", false);
            editor.commit();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh the listView
        mListColis = new ArrayList<Colis>();
        mColisDao = new ColisDAO(this);
        mListColis = mColisDao.getColisDeLivreur(mLivreurId);
        mAdapter = new ListColisAdapter(this, mListColis);
        mListviewColis.setAdapter(mAdapter);
    }

}
