package ws.splash.projetcandidature.activities;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import ws.splash.projetcandidature.R;
import ws.splash.projetcandidature.adapter.ListLivreursAdapter;
import ws.splash.projetcandidature.dao.ColisDAO;
import ws.splash.projetcandidature.dao.LivreurDAO;
import ws.splash.projetcandidature.model.Colis;
import ws.splash.projetcandidature.model.CustomDialogClass;
import ws.splash.projetcandidature.model.Livreur;


public class ListLivreursActivity extends Activity implements OnItemLongClickListener, OnItemClickListener, OnClickListener {

    public static final String TAG = "ListLivreursActivity";

    public static final int REQUEST_CODE_ADD_LIVREUR = 40;

    public static final String EXTRA_ADDED_LIVREUR = "extra_key_added_livreur";

    private ListView mListviewLivreurs;
    private TextView mTxtEmptyListLivreurs;
    private TextView mtxt_header;

    private ImageButton mBtnBack;
    private ImageButton mBtnFaq;

    private ListLivreursAdapter mAdapter;
    private List<Livreur> mListLivreurs;

    private LivreurDAO mLivreurDao;
    private ColisDAO mColisDao;

    public final int REQUEST_CODE_SHOW_LIVREUR_DISPONIBLE = 5;
    public final int REQUEST_CODE_SHOW_ALL_LIVREUR = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_livreurs);


        // initialize views
        initViews();

        // initialize DAO
        mLivreurDao = new LivreurDAO(this);
        mColisDao = new ColisDAO(this);


        // fill the listView
        mListLivreurs = mLivreurDao.getAllLivreurs();
        for (int i = 0; i < mListLivreurs.size(); i++) {
            float dist = 0;
            //update "livreur statut"
            if (mListLivreurs.get(i).getDisponibilite().equals("Indisponible")) {
                mLivreurDao.updateLivreurStatut(mListLivreurs.get(i), "-1"); //statut: inconnu
            } else if (mListLivreurs.get(i).getDisponibilite().equals("Disponible") && (mListLivreurs.get(i).getParcoursKM() == dist)) {
                mLivreurDao.updateLivreurStatut(mListLivreurs.get(i), "1"); // statut: A son domicile
            } else if (mListLivreurs.get(i).getDisponibilite().equals("Disponible") && (mListLivreurs.get(i).getParcoursKM() != dist)) {
                mLivreurDao.updateLivreurStatut(mListLivreurs.get(i), "0"); // statut: En livraison
            }
        }

        //Check if the "livreur" have "colis" to deliver, if not set his "parcoursKM" to 0
        for (int i = 0; i < mListLivreurs.size(); i++) {
            List<Colis> mListColis = mColisDao.getColisDeLivreur(mListLivreurs.get(i).getId());
            if (mListColis.size() == 0) {
                mLivreurDao.updateLivreurParcours(mListLivreurs.get(i), 0);
            }
        }


        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        int lastTimeStarted = settings.getInt("last_time_started", -1);
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_YEAR);

        // Set random the disponibility of the "livreur" every new day, if the "livreur" does not have any "Colis" to deliver
        if (today != lastTimeStarted) { // Below code is executed once per day

            mLivreurDao = new LivreurDAO(this);
            mColisDao = new ColisDAO(this);

            mListLivreurs = mLivreurDao.getAllLivreurs();

            String[] disponibilite = {"Disponible", "Indisponible"};
            for (int i = 2; i < mLivreurDao.getAllLivreurs().size(); i++) { // i = 2, With that we will have at least 2 "livreur" always "disponible" so the user doesn't have a day with no "livreur"
                List<Colis> mListColisLivreur = mColisDao.getColisDeLivreur(mListLivreurs.get(i).getId());
                for (int j = 0, t = 0; j < mListColisLivreur.size(); j++) { //for every "colis" of a "livreur"
                    if (!(mListColisLivreur.get(j).getStatut().equals("1"))) { //if a "colis" isn't delivered
                        t++;
                    } else if (t == 0) { //choose a random value between "Disponible" & "Indisponible" and update the "livreur" disponibility
                        Random random = new Random();
                        int select = random.nextInt(disponibilite.length);
                        mListLivreurs.get(i).setDisponibilite(disponibilite[select]);
                        mLivreurDao.updateLivreurDisponibilite(mListLivreurs.get(i), mListLivreurs.get(i).getDisponibilite());
                    }
                }
            }

            for (int i = 1; i < mLivreurDao.getAllLivreurs().size(); i++) {
                if (mColisDao.getColisDeLivreur(mListLivreurs.get(i).getId()).size() == 0) { // If the "livreur" doesn't have any "colis" to deliver
                    Random random = new Random();
                    int select = random.nextInt(disponibilite.length);
                    mListLivreurs.get(i).setDisponibilite(disponibilite[select]);
                    mLivreurDao.updateLivreurDisponibilite(mListLivreurs.get(i), mListLivreurs.get(i).getDisponibilite());
                }
            }

            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("last_time_started", today);
            editor.commit();
        }


        int callingCard = getIntent().getIntExtra("calling_card", 0);
        switch (callingCard) { //check which activity is calling this one

            case REQUEST_CODE_SHOW_LIVREUR_DISPONIBLE:
                // Activity is started from CardLeft from DashboardFrag
                mListLivreurs = new ArrayList<Livreur>();
                mLivreurDao = new LivreurDAO(this);
                mListLivreurs = mLivreurDao.getAllLivreursDisponible();
                if (mListLivreurs != null && !mListLivreurs.isEmpty()) {
                    mAdapter = new ListLivreursAdapter(this, mListLivreurs);
                    mListviewLivreurs.setAdapter(mAdapter);
                    mtxt_header.setText("Tous les livreurs disponible");
                } else {
                    mTxtEmptyListLivreurs.setVisibility(View.VISIBLE);
                    mListviewLivreurs.setVisibility(View.GONE);
                }
                // Show custom dialog box at first run
                doFirstRunAllLivreurDisponible();
                break;

            case REQUEST_CODE_SHOW_ALL_LIVREUR:
                // Activity is started from ButtonCard "Les Livreurs" from DashboardFrag
                mListLivreurs = new ArrayList<Livreur>();
                mListLivreurs = mLivreurDao.getAllLivreurs();
                if (mListLivreurs != null && !mListLivreurs.isEmpty()) {
                    mAdapter = new ListLivreursAdapter(this, mListLivreurs);
                    mListviewLivreurs.setAdapter(mAdapter);

                } else {
                    mTxtEmptyListLivreurs.setVisibility(View.VISIBLE);
                    mListviewLivreurs.setVisibility(View.GONE);
                }
                // Show custom dialog box at first run
                doFirstRunAllLivreur();
                break;
        }
    }

    //Method to initialize views
    private void initViews() {
        this.mListviewLivreurs = (ListView) findViewById(R.id.list_livreurs);
        this.mTxtEmptyListLivreurs = (TextView) findViewById(R.id.txt_empty_list_livreurs);
        this.mtxt_header = (TextView) findViewById(R.id.txt_header);
        this.mBtnBack = (ImageButton) findViewById(R.id.btn_back);
        this.mBtnFaq = (ImageButton) findViewById(R.id.btn_faq);
        this.mListviewLivreurs.setOnItemClickListener(this);
        this.mListviewLivreurs.setOnItemLongClickListener(this);
        this.mBtnBack.setOnClickListener(this);
        this.mBtnFaq.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;

            case R.id.btn_faq:
                CustomDialogClass cdd = new CustomDialogClass();
                String msg = "Ici vous pouvez voir une liste de <font color='#cccccc'>livreurs.</font> <br> Vous pouvez en choisissant un livreur disponible l'affecter à un colis.";
                cdd.showDialog(this, "Help Center", Html.fromHtml(msg));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ADD_LIVREUR) {//If a new "livreur" is created
            if (resultCode == RESULT_OK) {
                // Add the created "livreur" to the listLivreurs and refresh the listView
                if (data != null) {
                    Livreur createdLivreur = (Livreur) data.getSerializableExtra(EXTRA_ADDED_LIVREUR);
                    if (createdLivreur != null) {
                        if (mListLivreurs == null)
                            mListLivreurs = new ArrayList<Livreur>();
                        mListLivreurs.add(createdLivreur);

                        if (mAdapter == null) {
                            if (mListviewLivreurs.getVisibility() != View.VISIBLE) {
                                mListviewLivreurs.setVisibility(View.VISIBLE);
                                mTxtEmptyListLivreurs.setVisibility(View.GONE);
                            }

                            mAdapter = new ListLivreursAdapter(this, mListLivreurs);
                            mListviewLivreurs.setAdapter(mAdapter);
                        } else {
                            mAdapter.setItems(mListLivreurs);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLivreurDao.close();
        mColisDao.close();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Livreur clickedLivreur = mAdapter.getItem(position);

        if (clickedLivreur.getDisponibilite().equals("Disponible")) { //Go to the list of "colis" of the selected "livreur", if his disponibility equals "disponible"
            Intent intent = new Intent(this, ListColisActivity.class);
            intent.putExtra(ListColisActivity.EXTRA_SELECTED_LIVREUR_ID, clickedLivreur.getId());
            intent.putExtra(ListColisActivity.EXTRA_SELECTED_LIVREUR_NOM, clickedLivreur.getNom());
            startActivity(intent);
        } else {
            Toast.makeText(ListLivreursActivity.this, clickedLivreur.getNom() + " n'est pas disponible", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

    //Method to run a custom dialog box at first run of the app
    private void doFirstRunAllLivreur() {
        SharedPreferences settings = getSharedPreferences("saveopenApp", MODE_PRIVATE);
        if (settings.getBoolean("isFirstRunLesLivreurs", true)) {

            CustomDialogClass cdd = new CustomDialogClass();
            String msg = "Cette liste comprend <font color='#cccccc'>tous les livreurs.</font> <br> <br> <u>N.B:</u> Cette liste change tout les jours en fonction de la disponibilité des livreurs.";
            cdd.showDialog(this, "Tous vos livreurs", Html.fromHtml(msg));

            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("isFirstRunLesLivreurs", false);
            editor.commit();
        }
    }

    //Method to run a custom dialog box at first run of the app
    private void doFirstRunAllLivreurDisponible() {
        SharedPreferences settings = getSharedPreferences("saveopenApp", MODE_PRIVATE);
        if (settings.getBoolean("isFirstRunLesLivreursDisponible", true)) {

            CustomDialogClass cdd = new CustomDialogClass();
            String msg = "Cette liste comprend <font color='#cccccc'>tous les livreurs disponible.</font> <br> Vous pouvez à travers ce menu affecter des colis au livreur de votre choix.";
            String msg2 = "Il ne vous reste plus qu'à <font color='#cccccc'>choisir</font> un livreur ;)";


            cdd.showDialog(this, "Tous vos livreurs disponible", Html.fromHtml(msg2));
            cdd.showDialog(this, "Tous vos livreurs disponible", Html.fromHtml(msg));

            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("isFirstRunLesLivreursDisponible", false);
            editor.commit();
        }
    }
}
