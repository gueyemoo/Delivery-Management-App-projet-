package ws.splash.projetcandidature.activities;

import ws.splash.projetcandidature.R;
import ws.splash.projetcandidature.adapter.ListColisAdapter;
import ws.splash.projetcandidature.dao.ColisDAO;
import ws.splash.projetcandidature.model.Colis;
import ws.splash.projetcandidature.model.CustomDialogClass;

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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ListColisUpdate extends Activity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener, View.OnClickListener {

    public static final String TAG = "ListColisUpdateActivity";

    private ListView mListviewColis;
    private TextView mTxtEmptyListColis;

    private ImageButton mBtnBack;
    private ImageButton mBtnFaq;


    private ListColisAdapter mAdapter;
    private List<Colis> mListColis;
    private ColisDAO mColisDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_colis_update);

        // initialize views
        initViews();

        // Initialize DAO
        if (mColisDao == null) {
            mColisDao = new ColisDAO(this);
        }

        // Get the listView with all colis
        if (mListColis == null || !mListColis.isEmpty()) {
            mListColis = new ArrayList<Colis>();
        }
        mListColis = mColisDao.getAllColis();

        // fill the listView
        if (mAdapter == null) {
            mAdapter = new ListColisAdapter(this, mListColis);
            mListviewColis.setAdapter(mAdapter);
            if (mListviewColis.getVisibility() != View.VISIBLE) {
                mTxtEmptyListColis.setVisibility(View.GONE);
                mListviewColis.setVisibility(View.VISIBLE);
            }
            mAdapter = new ListColisAdapter(this, mListColis);
            mListviewColis.setAdapter(mAdapter);
        } else {
            mAdapter.setItems(mListColis);
            mAdapter.notifyDataSetChanged();
        }
        // Show custom dialog box at first run
        doFirstRun();
    }

    //Method to initialize views
    private void initViews() {
        this.mListviewColis = (ListView) findViewById(R.id.list_colis);
        this.mTxtEmptyListColis = (TextView) findViewById(R.id.txt_empty_list_colis);
        this.mBtnBack = (ImageButton) findViewById(R.id.btn_back);
        this.mBtnFaq = (ImageButton) findViewById(R.id.btn_faq);
        this.mListviewColis.setOnItemClickListener(this);
        this.mListviewColis.setOnItemLongClickListener(this);
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
                String msg = "Ce menu vous permet de <font color='#cccccc'>choisir </font> un colis a modifié.";
                String msg2 = "Il vous suffit de choisir un colis ;) <br> <br> <u>N.B:</u> Les colis déjà livré ne sont pas modifiable.";
                cdd.showDialog(this, "Help Center", Html.fromHtml(msg2));
                cdd.showDialog(this, "Help Center", Html.fromHtml(msg));
                break;

            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Colis clickedColis = mAdapter.getItem(position);

        if (clickedColis.getStatut().equals("1")) { //if the "colis" is already delivered
            Toast.makeText(this, "Le colis a déjà été livré, il ne peut plus être modifier", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(this, UpdateColisActivity.class);
            //Send extra information to UpdateColisActivity
            intent.putExtra(UpdateColisActivity.EXTRA_SELECTED_COLIS_ID, clickedColis.getId());
            intent.putExtra(UpdateColisActivity.EXTRA_SELECTED_COLIS_NOM, clickedColis.getNom());
            intent.putExtra(UpdateColisActivity.EXTRA_SELECTED_COLIS_X, clickedColis.getColis_X());
            intent.putExtra(UpdateColisActivity.EXTRA_SELECTED_COLIS_Y, clickedColis.getColis_Y());
            intent.putExtra(UpdateColisActivity.EXTRA_SELECTED_COLIS_DATE, clickedColis.getDate());
            intent.putExtra(UpdateColisActivity.EXTRA_SELECTED_COLIS_STATUT, clickedColis.getStatut());

            mAdapter.setItems(mListColis);
            mAdapter.notifyDataSetChanged();

            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mColisDao.close();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

    //Method to run a custom dialog box at first run of the app
    private void doFirstRun() {
        SharedPreferences settings = getSharedPreferences("saveopenApp", MODE_PRIVATE);

        if (settings.getBoolean("isFirstRunChangeListColis", true)) {

            CustomDialogClass cdd = new CustomDialogClass();
            String msg = "Ce menu vous permet de <font color='#cccccc'>choisir </font> un colis a modifié.";
            String msg2 = "Il vous suffit de choisir un colis ;) <br> <br> <u>N.B:</u> Les colis déja livré ne sont pas modifiable.";

            cdd.showDialog(this, "C'est 'Le Wifi' pas 'La Wifi' ", Html.fromHtml(msg2));
            cdd.showDialog(this, "C'est 'Le Wifi' pas 'La Wifi' ", Html.fromHtml(msg));


            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("isFirstRunChangeListColis", false);
            editor.commit();
        }
    }
}
