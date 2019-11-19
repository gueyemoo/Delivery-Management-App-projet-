package ws.splash.projetcandidature.activities;

import androidx.appcompat.app.AppCompatActivity;
import ws.splash.projetcandidature.R;
import ws.splash.projetcandidature.adapter.ListColisAdapter;
import ws.splash.projetcandidature.dao.ColisDAO;
import ws.splash.projetcandidature.model.Colis;
import ws.splash.projetcandidature.model.CustomDialogClass;

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

public class CentreDeDistribution extends AppCompatActivity implements View.OnClickListener {

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
        setContentView(R.layout.activity_centre_de_distribution);

        // initialize views
        initViews();

        //Initialize List & Dao if not null
        if (mListColis == null || !mListColis.isEmpty()) {
            mListColis = new ArrayList<Colis>();
        }

        if (mColisDao == null)
            mColisDao = new ColisDAO(this);
        mListColis = mColisDao.getAllColisDisponible();
        if (mAdapter == null) {
            mAdapter = new ListColisAdapter(this, mListColis);
            mListviewColis.setAdapter(mAdapter);
            if (mListColis != null && !mListColis.isEmpty()) {

            } else {
                mTxtEmptyListColis.setVisibility(View.VISIBLE);
                mListviewColis.setVisibility(View.GONE);
            }
        } else {
            mListColis = new ArrayList<Colis>();
            mColisDao = new ColisDAO(this);
            mListColis = mColisDao.getAllColisDisponible();
            mAdapter = new ListColisAdapter(this, mListColis);
            mListviewColis.setAdapter(mAdapter);
            mAdapter.setItems(mListColis);
            mAdapter.notifyDataSetChanged();
        }
        //show custom dialog box at first run
        doFirstRun();
    }

    //Method to initialize views
    private void initViews() {
        this.mListviewColis = (ListView) findViewById(R.id.list_colis);
        this.mTxtEmptyListColis = (TextView) findViewById(R.id.txt_empty_list_colis);
        this.mBtnBack = (ImageButton) findViewById(R.id.btn_back);
        this.mBtnFaq = (ImageButton) findViewById(R.id.btn_faq);
        mBtnBack.setOnClickListener(this);
        mBtnFaq.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;

            case R.id.btn_faq:
                CustomDialogClass cdd = new CustomDialogClass();
                String msg = "Ce menu vous permet de voir <font color='#cccccc'>tous les colis </font> se trouvant dans le centre de distribution.";
                cdd.showDialog(this, "Help Center", Html.fromHtml(msg));
                break;

            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mColisDao.close();
    }

    //Refresh listView when we comeback in the activity
    @Override
    public void onResume() {
        super.onResume();
        mListColis = new ArrayList<Colis>();
        mColisDao = new ColisDAO(this);
        mListColis = mColisDao.getAllColisDisponible();
        mAdapter = new ListColisAdapter(this, mListColis);
        mListviewColis.setAdapter(mAdapter);
        mAdapter.setItems(mListColis);
        mAdapter.notifyDataSetChanged();
    }

    //Method to run a custom dialog box at first run of the app
    private void doFirstRun() {
        SharedPreferences settings = getSharedPreferences("saveopenApp", MODE_PRIVATE);
        if (settings.getBoolean("isFirstRunCentre", true)) {

            CustomDialogClass cdd = new CustomDialogClass();
            String msg = "Ce menu vous permet de voir <font color='#cccccc'>tous les colis </font> se trouvant dans le centre de distribution.";
            cdd.showDialog(this, "Vos colis à envoyer", Html.fromHtml(msg));

            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("isFirstRunCentre", false);
            editor.commit();
        }
    }
}
