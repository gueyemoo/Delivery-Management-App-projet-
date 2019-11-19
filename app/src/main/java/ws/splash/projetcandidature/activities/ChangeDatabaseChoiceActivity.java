package ws.splash.projetcandidature.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;


import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import ws.splash.projetcandidature.R;
import ws.splash.projetcandidature.model.CustomDialogClass;

public class ChangeDatabaseChoiceActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView cardTop, cardTop2, cardRight, cardRight2, cardLeft, cardLeft2;
    private ImageButton mBtnBack;
    private ImageButton mBtnFaq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_database_choice);


        // initialize button
        this.mBtnBack = findViewById(R.id.btn_back);
        this.mBtnBack.setOnClickListener(this);

        this.mBtnFaq = findViewById(R.id.btn_faq);
        this.mBtnFaq.setOnClickListener(this);

        // initialize cardView
        cardTop = findViewById(R.id.cardTop);
        this.cardTop.setOnClickListener(this);

        cardTop2 = findViewById(R.id.cardTop2);
        this.cardTop2.setOnClickListener(this);

        cardRight = findViewById(R.id.cardRight);
        this.cardRight.setOnClickListener(this);

        cardLeft = findViewById(R.id.cardLeft);
        this.cardLeft.setOnClickListener(this);

        cardRight2 = findViewById(R.id.cardRight2);
        this.cardRight2.setOnClickListener(this);

        cardLeft2 = findViewById(R.id.cardLeft2);
        this.cardLeft2.setOnClickListener(this);


        // initialize Animations

        Animation animeBottomToTop = AnimationUtils.loadAnimation(this, R.anim.anime_bottom_to_top);
        Animation animeTopToBottom = AnimationUtils.loadAnimation(this, R.anim.anime_top_to_bottom);
        Animation animeRightToleft = AnimationUtils.loadAnimation(this, R.anim.anime_right_to_left);
        Animation animeLeftToRight = AnimationUtils.loadAnimation(this, R.anim.anime_left_to_right);


        // setup Animation :
        cardTop.setAnimation(animeTopToBottom);
        cardTop2.setAnimation(animeTopToBottom);
        cardLeft.setAnimation(animeLeftToRight);
        cardRight.setAnimation(animeRightToleft);
        cardLeft2.setAnimation(animeBottomToTop);
        cardRight2.setAnimation(animeBottomToTop);

        //show custom dialog box at first run
        doFirstRun();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cardTop2:
                Intent intent = new Intent(this, CreateColisActivity.class);
                startActivity(intent);
                break;

            case R.id.cardTop:
                Intent intent2 = new Intent(this, CreateLivreurActivity.class);
                startActivity(intent2);
                break;

            case R.id.cardRight:
                Intent intent3 = new Intent(this, ListColisUpdate.class);
                startActivity(intent3);
                break;

            case R.id.cardLeft:
                Intent intent4 = new Intent(this, ListLivreursUpdate.class);
                startActivity(intent4);
                break;

            case R.id.cardRight2:
                Intent intent5 = new Intent(this, DeleteColisActivity.class);
                startActivity(intent5);
                break;

            case R.id.cardLeft2:
                Intent intent6 = new Intent(this, DeleteLivreurActivity.class);
                startActivity(intent6);
                break;

            case R.id.btn_faq:
                CustomDialogClass cdd = new CustomDialogClass();
                String msg = "Ce menu vous permet d'accéder aux différents <font color='#cccccc'>choix de modification</font> possible.";
                cdd.showDialog(this, "Help Center", Html.fromHtml(msg));
                break;

            case R.id.btn_back:
                finish();
                break;

            default:
                break;
        }
    }

    //Method to run a custom dialog box at first run of the app
    private void doFirstRun() {
        SharedPreferences settings = getSharedPreferences("saveopenApp", MODE_PRIVATE);

        if (settings.getBoolean("isFirstRunDatabaseChange", true)) {

            CustomDialogClass cdd = new CustomDialogClass();
            String msg = "Ce menu vous permet d'accéder aux différents <font color='#cccccc'>choix de modification</font> possible.";


            cdd.showDialog(this, "Des changements ? ", Html.fromHtml(msg));


            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("isFirstRunDatabaseChange", false);
            editor.commit();
        }

    }
}
