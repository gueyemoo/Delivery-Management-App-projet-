package ws.splash.projetcandidature.activities;

import ws.splash.projetcandidature.R;
import ws.splash.projetcandidature.adapter.ListLivreursAdapter;
import ws.splash.projetcandidature.dao.LivreurDAO;
import ws.splash.projetcandidature.model.CustomDialogClass;
import ws.splash.projetcandidature.model.Livreur;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ListLivreursUpdate extends Activity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener, View.OnClickListener {

    public static final String TAG = "ListColisUpdateActivity";

    private ListView mListviewLivreur;
    private TextView mTxtEmptyListLivreur;

    private ImageButton mBtnBack;
    private ImageButton mBtnFaq;


    private ListLivreursAdapter mAdapter;
    private List<Livreur> mListLivreur;

    private LivreurDAO mLivreurDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_livreurs_update);

        // initialize views
        initViews();


        if (mLivreurDao == null) {
            mLivreurDao = new LivreurDAO(this);
        }

        // Get the listView with all livreurs
        if (mListLivreur == null || !mListLivreur.isEmpty()) {
            mListLivreur = new ArrayList<Livreur>();
        }
        mListLivreur = mLivreurDao.getAllLivreurs();
        if (mAdapter == null) {
            mAdapter = new ListLivreursAdapter(this, mListLivreur);
            mListviewLivreur.setAdapter(mAdapter);
            if (mListviewLivreur.getVisibility() != View.VISIBLE) {
                mTxtEmptyListLivreur.setVisibility(View.GONE);
                mListviewLivreur.setVisibility(View.VISIBLE);
            }
            mAdapter = new ListLivreursAdapter(this, mListLivreur);
            mListviewLivreur.setAdapter(mAdapter);
        } else {
            mAdapter.setItems(mListLivreur);
            mAdapter.notifyDataSetChanged();
        }
        // Show custom dialog box at first run
        doFirstRun();
    }

    //Method to initialize views
    private void initViews() {
        this.mListviewLivreur = (ListView) findViewById(R.id.list_livreurs);
        this.mTxtEmptyListLivreur = (TextView) findViewById(R.id.txt_empty_list_livreurs);
        this.mBtnBack = (ImageButton) findViewById(R.id.btn_back);
        this.mBtnFaq = (ImageButton) findViewById(R.id.btn_faq);
        this.mListviewLivreur.setOnItemClickListener(this);
        this.mListviewLivreur.setOnItemLongClickListener(this);
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
                String msg = "Ce menu vous permet de <font color='#cccccc'>choisir </font> un livreur a modifié.";
                String msg2 = "Il vous suffit de choisir un livreur ;)";
                cdd.showDialog(this, "Help Center", Html.fromHtml(msg2));
                cdd.showDialog(this, "Help Center", Html.fromHtml(msg));
                break;

            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLivreurDao.close();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Livreur clickedLivreur = mAdapter.getItem(position);

        Intent intent = new Intent(this, UpdateLivreurActivity.class);
        //Send extra information to UpdateLivreurActivity
        intent.putExtra(UpdateLivreurActivity.EXTRA_SELECTED_LIVREUR_ID, clickedLivreur.getId());
        intent.putExtra(UpdateLivreurActivity.EXTRA_SELECTED_LIVREUR_NOM, clickedLivreur.getNom());
        intent.putExtra(UpdateLivreurActivity.EXTRA_SELECTED_LIVREUR_X, clickedLivreur.getLivreur_X());
        intent.putExtra(UpdateLivreurActivity.EXTRA_SELECTED_LIVREUR_Y, clickedLivreur.getLivreur_Y());
        intent.putExtra(UpdateLivreurActivity.EXTRA_SELECTED_LIVREUR_DISPONIBILITE, clickedLivreur.getDisponibilite());

        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

    //Method to run a custom dialog box at first run of the app
    private void doFirstRun() {
        SharedPreferences settings = getSharedPreferences("saveopenApp", MODE_PRIVATE);

        if (settings.getBoolean("isFirstRunChangeListLivreur", true)) {

            CustomDialogClass cdd = new CustomDialogClass();
            String msg = "Ce menu vous permet de <font color='#cccccc'>choisir </font> un livreur a modifié.";
            String msg2 = "Il vous suffit de choisir un livreur ;)";

            cdd.showDialog(this, "C'est 'Jordie' pas 'Jordi' ", Html.fromHtml(msg2));
            cdd.showDialog(this, "C'est 'Jordie' pas 'Jordi' ", Html.fromHtml(msg));


            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("isFirstRunChangeListLivreur", false);
            editor.commit();
        }
    }
}
