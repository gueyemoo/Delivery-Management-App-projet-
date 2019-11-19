package ws.splash.projetcandidature.activities;

import ws.splash.projetcandidature.R;
import ws.splash.projetcandidature.dao.LivreurDAO;
import ws.splash.projetcandidature.model.CustomDialogClass;
import ws.splash.projetcandidature.model.InputFilterMinMax;
import ws.splash.projetcandidature.model.Livreur;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class CreateLivreurActivity extends Activity implements View.OnClickListener {

    public static final String TAG = "CreateLivreurActivity";

    private EditText mTxtLivreurNom;
    private EditText mTxtLivreur_x;
    private EditText mTxtLivreur_y;

    private Button mBtnAdd;
    private ImageButton mBtnBack;
    private ImageButton mBtnFaq;


    private LivreurDAO mLivreurDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_livreur);

        // initialize views
        initViews();

        // initialize DAO
        mLivreurDao = new LivreurDAO(this);

        //Limit the user input for x
        mTxtLivreur_x.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mTxtLivreur_x.setFilters(new InputFilter[]{new InputFilterMinMax("-100", "100")});
            }
        });

        //Limit the user input for y
        mTxtLivreur_y.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mTxtLivreur_y.setFilters(new InputFilter[]{new InputFilterMinMax("-100", "100")});
            }
        });
        // Show custom dialog box at first run
        doFirstRun();
    }

    private void initViews() {
        this.mTxtLivreurNom = (EditText) findViewById(R.id.txt_livreur_nom);
        this.mTxtLivreur_x = (EditText) findViewById(R.id.txt_livreur_x);
        this.mTxtLivreur_y = (EditText) findViewById(R.id.txt_livreur_y);
        this.mBtnAdd = (Button) findViewById(R.id.btn_add_livreur);
        this.mBtnBack = (ImageButton) findViewById(R.id.btn_back);
        this.mBtnFaq = (ImageButton) findViewById(R.id.btn_faq);
        this.mBtnAdd.setOnClickListener(this);
        this.mBtnBack.setOnClickListener(this);
        this.mBtnFaq.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_livreur:

                Editable livreurNom = mTxtLivreurNom.getText();
                Editable strlivreur_x = mTxtLivreur_x.getText();
                Editable strlivreur_y = mTxtLivreur_y.getText();

                if (TextUtils.isEmpty(strlivreur_x)) { //default value of x if empty
                    mTxtLivreur_x.setText("0");
                }

                if (TextUtils.isEmpty(strlivreur_y)) { //default value of y if empty
                    mTxtLivreur_y.setText("0");
                }

                // Check if one field is empty, if not add the created "livreur" to database
                int livreur_x = Integer.valueOf(mTxtLivreur_x.getText().toString());
                int livreur_y = Integer.valueOf(mTxtLivreur_y.getText().toString());

                if (!TextUtils.isEmpty(livreurNom) && !TextUtils.isEmpty(strlivreur_x)
                        && !TextUtils.isEmpty(strlivreur_y)) {
                    Livreur createdLivreur = mLivreurDao.createLivreur(
                            livreurNom.toString(),
                            livreur_x,
                            livreur_y,
                            "Disponible",
                            0.0,
                            "1");
                    Toast.makeText(this, createdLivreur.getNom() + " a bien été ajouter.", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(this, R.string.empty_fields_message, Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btn_back:
                finish();
                break;

            case R.id.btn_faq:
                CustomDialogClass cdd = new CustomDialogClass();
                String msg = "Ici vous pouvez <font color='#cccccc'>ajouter</font> un nouveau livreur à votre liste.";
                cdd.showDialog(this, "Help Center", Html.fromHtml(msg));
                break;

            default:
                break;
        }
    }

    //Method to run a custom dialog box at first run of the app
    private void doFirstRun() {
        SharedPreferences settings = getSharedPreferences("saveopenApp", MODE_PRIVATE);

        if (settings.getBoolean("isFirstRunAddLivreur", true)) {

            CustomDialogClass cdd = new CustomDialogClass();
            String msg = "Ici vous pouvez <font color='#cccccc'>ajouter</font> un nouveau livreur à votre liste.";


            cdd.showDialog(this, "Une nouvelle recrue ? ", Html.fromHtml(msg));


            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("isFirstRunAddLivreur", false);
            editor.commit();
        }
    }
}
