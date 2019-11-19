package ws.splash.projetcandidature.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import ws.splash.projetcandidature.R;
import ws.splash.projetcandidature.dao.ColisDAO;
import ws.splash.projetcandidature.dao.LivreurDAO;
import ws.splash.projetcandidature.model.CustomDialogClass;
import ws.splash.projetcandidature.model.Livreur;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashboardOfTheDayActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView cardTop, cardTop2, cardRight, cardRight2, cardLeft,cardLeft2;

    private ImageButton mBtnBack;
    private ImageButton mBtnFaq;

    private Button mRefreshBtn;

    private TextView mtxt_header;
    private TextView mLivreurDisponible;
    private TextView mLivreurIndisponible;
    private TextView mColisALivre;
    private TextView mLivreurEnLivraison;
    private TextView mColisLivre;
    private TextView mColisEnLivraison;

    private List<Livreur> mListLivreurs;

    private LivreurDAO mLivreurDao;
    private ColisDAO mColisDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_of_the_day);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String currentTime = df.format(new Date());

        // initialize views
        this.mBtnBack = findViewById(R.id.btn_back);
        this.mBtnBack.setOnClickListener(this);

        this.mRefreshBtn = findViewById(R.id.buttonRefreshDay);
        this.mRefreshBtn.setOnClickListener(this);

        this.mBtnFaq = findViewById(R.id.btn_faq);
        this.mBtnFaq.setOnClickListener(this);

        mColisDao = new ColisDAO(this);
        mLivreurDao = new LivreurDAO(this);

        cardTop = findViewById(R.id.cardTop);

        cardTop2 = findViewById(R.id.cardTop2);

        cardRight = findViewById(R.id.cardRight);

        cardLeft = findViewById(R.id.cardLeft);

        cardRight2 = findViewById(R.id.cardRight2);

        cardLeft2 = findViewById(R.id.cardLeft2) ;

        mLivreurDisponible = findViewById(R.id.nb_livreur_disponible);

        mLivreurIndisponible = findViewById(R.id.nb_livreur_indisponible);

        mColisALivre = findViewById(R.id.nb_colis_a_livre);

        mLivreurEnLivraison = findViewById(R.id.nb_livreur_en_livraison);

        mColisLivre = findViewById(R.id.nb_colis_livre);

        mColisEnLivraison = findViewById(R.id.nb_colis_en_livraison);

        mtxt_header = findViewById(R.id.txt_header_dashboardday);


        // Set values in textViews

        mtxt_header.setText("Situation du : " +currentDate+ " à " + currentTime );

        mLivreurDisponible.setText(String.valueOf(mLivreurDao.getAllLivreursDisponible().size()));

        mLivreurIndisponible.setText(String.valueOf(mLivreurDao.getAllLivreursIndisponible().size()));

        mColisALivre.setText(String.valueOf(mColisDao.getAllColisALivre().size()));

        mLivreurEnLivraison.setText(String.valueOf(mLivreurDao.getAllLivreursEnLivraison().size()));

        mColisLivre.setText(String.valueOf(mColisDao.getAllColisLivre().size()));

        mColisEnLivraison.setText(String.valueOf(mColisDao.getAllColisEnLivraison().size()));



        // initialize Animations

        Animation animeBottomToTop = AnimationUtils.loadAnimation(this,R.anim.anime_bottom_to_top);
        Animation animeTopToBottom = AnimationUtils.loadAnimation(this,R.anim.anime_top_to_bottom);
        Animation animeRightToleft = AnimationUtils.loadAnimation(this,R.anim.anime_right_to_left);
        Animation animeLeftToRight = AnimationUtils.loadAnimation(this,R.anim.anime_left_to_right);


        // setup Animation :
        cardTop.setAnimation(animeTopToBottom);
        cardTop2.setAnimation(animeTopToBottom);
        cardLeft.setAnimation(animeLeftToRight);
        cardRight.setAnimation(animeRightToleft);
        cardLeft2.setAnimation(animeBottomToTop);
        cardRight2.setAnimation(animeBottomToTop);

        mListLivreurs = mLivreurDao.getAllLivreurs();
        //reset the "parcourskm" of a "livreur" to 0 if he doesn't have any "colis" to deliver
        for (int i = 1; i<mListLivreurs.size();i++){
            if(mColisDao.getColisDeLivreur(mListLivreurs.get(i).getId()).size() == 0){
                double distance_reset = 0;
                mLivreurDao.updateLivreurParcours(mListLivreurs.get(i), distance_reset);
            }
        }
        // Show custom dialog box at first run
        doFirstRun();

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;

             //This button refresh the dashboard with the new values
            case R.id.buttonRefreshDay:
                Toast.makeText(this,"Mise à jour !",Toast.LENGTH_SHORT).show();
                mColisDao = new ColisDAO(this);
                mLivreurDao = new LivreurDAO(this);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
                String currentDate = sdf.format(new Date());

                SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                String currentTime = df.format(new Date());

                mtxt_header.setText("Situation du : " +currentDate+ " à " + currentTime );
                mLivreurDisponible.setText(String.valueOf(mLivreurDao.getAllLivreursDisponible().size()));
                mLivreurIndisponible.setText(String.valueOf(mLivreurDao.getAllLivreursIndisponible().size()));
                mColisALivre.setText(String.valueOf(mColisDao.getAllColisALivre().size()));
                mLivreurEnLivraison.setText(String.valueOf(mLivreurDao.getAllLivreursEnLivraison().size()));
                mColisLivre.setText(String.valueOf(mColisDao.getAllColisLivre().size()));
                mColisEnLivraison.setText(String.valueOf(mColisDao.getAllColisEnLivraison().size()));
                break;

            case R.id.btn_faq:
                CustomDialogClass cdd=new CustomDialogClass();
                String msg = "Dans ce menu vous pourrez trouver toutes <font color='#cccccc'>les informations resumant</font> la situation actuelle.";
                cdd.showDialog(this, "Help Center", Html.fromHtml(msg));
                break;

            default:
                break;
        }
    }

    //Method to run a custom dialog box at first run of the app
    private void doFirstRun() {
        SharedPreferences settings = getSharedPreferences("saveopenApp", MODE_PRIVATE);
        if (settings.getBoolean("isFirstRunDashboard", true)) {

            CustomDialogClass cdd=new CustomDialogClass();
            String msg = "Dans ce menu vous pourrez trouver toutes <font color='#cccccc'>les informations resumant</font> la situation actuelle.";
            cdd.showDialog(this, "Votre tableau de bord !", Html.fromHtml(msg));

            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("isFirstRunDashboard", false);
            editor.commit();
        }
    }
}
