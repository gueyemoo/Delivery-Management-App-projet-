package ws.splash.projetcandidature.activities;

import ws.splash.projetcandidature.R;
import ws.splash.projetcandidature.dao.ColisDAO;
import ws.splash.projetcandidature.dao.LivreurDAO;
import ws.splash.projetcandidature.model.Colis;
import ws.splash.projetcandidature.model.CustomDialogClass;
import ws.splash.projetcandidature.model.InputFilterMinMax;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class UpdateLivreurActivity extends Activity implements View.OnClickListener {

    public static final String EXTRA_SELECTED_LIVREUR_ID = "extra_key_selected_livreur_id";
    public static final String EXTRA_SELECTED_LIVREUR_NOM = "extra_key_selected_livreur_nom";
    public static final String EXTRA_SELECTED_LIVREUR_X = "extra_key_selected_livreur_x";
    public static final String EXTRA_SELECTED_LIVREUR_Y = "extra_key_selected_livreur_y";
    public static final String EXTRA_SELECTED_LIVREUR_DISPONIBILITE = "extra_key_selected_livreur_disponibilite";


    private EditText mTxtLivreurNom;
    private EditText mTxtLivreur_x;
    private EditText mTxtLivreur_y;

    private RadioGroup mRGLivreur_disponibilite;
    private RadioButton mRBDisponible;
    private RadioButton mRBIndisponible;
    private RadioButton mSelectedRadioButton;

    private Button mBtnUpdate;
    private ImageButton mBtnBack;
    private ImageButton mBtnFaq;


    private TextView mTxtheader;

    private long mLivreurId = -1;
    private String mLivreurNom;
    private Integer mLivreur_x;
    private Integer mLivreur_y;
    private String mLivreur_disponibilite;

    private ColisDAO mColisDao;
    private LivreurDAO mLivreurDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_livreur);

        // initialize views
        initViews();

        // Initialize DAO
        this.mLivreurDao = new LivreurDAO(this);
        this.mColisDao = new ColisDAO(this);

        Intent intent = getIntent();
        if (intent != null) { // getting extra value from ListColisActivity
            this.mLivreurId = intent.getLongExtra(EXTRA_SELECTED_LIVREUR_ID, -1);
            this.mLivreurNom = intent.getStringExtra(EXTRA_SELECTED_LIVREUR_NOM);
            this.mLivreur_x = intent.getIntExtra(EXTRA_SELECTED_LIVREUR_X, -1);
            this.mLivreur_y = intent.getIntExtra(EXTRA_SELECTED_LIVREUR_Y, -1);
            this.mLivreur_disponibilite = intent.getStringExtra(EXTRA_SELECTED_LIVREUR_DISPONIBILITE);
        }

        //Setting default value of edittext
        mTxtLivreurNom.setText(mLivreurNom);
        mTxtLivreur_x.setText(String.valueOf(mLivreur_x));
        mTxtLivreur_y.setText(String.valueOf(mLivreur_y));
        if (mLivreurId != -1) {
            List<Colis> mListColisLivreur = mColisDao.getColisDeLivreur(mLivreurId);
            for (int i = 0; i < mListColisLivreur.size(); i++) {
                if (!(mListColisLivreur.get(i).getStatut().equals("1"))) { //If "Livreur"'s status is "En Livraison"
                    mRBIndisponible.setEnabled(false);
                }
            }
        }

        if (mLivreur_disponibilite.equals("Disponible")) {
            mRBDisponible.setChecked(true);
            mRBIndisponible.setChecked(false);
        } else if (mLivreur_disponibilite.equals("Indisponible")) {
            mRBDisponible.setChecked(false);
            mRBIndisponible.setChecked(true);
        }

        // Limit the user input for x
        mTxtLivreur_x.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mTxtLivreur_x.setFilters(new InputFilter[]{new InputFilterMinMax("-100", "100")});
            }
        });

        // Limit the user input for y
        mTxtLivreur_y.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mTxtLivreur_y.setFilters(new InputFilter[]{new InputFilterMinMax("-100", "100")});
            }
        });

        if (!(mLivreurNom.equals(""))) {
            mTxtheader.setText("Modifier " + mLivreurNom);//update the header of the view with the "livreur" name
        }
        // Show custom dialog box at first run
        doFirstRun();
    }

    //Method to initialize views
    private void initViews() {
        this.mTxtLivreurNom = (EditText) findViewById(R.id.txt_livreur_nom);
        this.mTxtLivreur_x = (EditText) findViewById(R.id.txt_livreur_x);
        this.mTxtLivreur_y = (EditText) findViewById(R.id.txt_livreur_y);
        this.mRGLivreur_disponibilite = findViewById(R.id.radiogroup_livreur_disponibilite);

        this.mRBDisponible = findViewById(R.id.radio_disponible);
        this.mRBIndisponible = findViewById(R.id.radio_indisponible);

        this.mTxtheader = findViewById(R.id.txt_header);

        this.mBtnUpdate = (Button) findViewById(R.id.btn_update_livreur);
        this.mBtnBack = (ImageButton) findViewById(R.id.btn_back);
        this.mBtnFaq = (ImageButton) findViewById(R.id.btn_faq);
        this.mBtnBack.setOnClickListener(this);
        this.mBtnUpdate.setOnClickListener(this);
        this.mBtnFaq.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLivreurDao.close();
        mColisDao.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update_livreur:

                //Get values from editText
                Editable livreurNom = mTxtLivreurNom.getText();
                Editable strlivreur_x = mTxtLivreur_x.getText();
                Editable strlivreur_y = mTxtLivreur_y.getText();

                int radioSelectedId = mRGLivreur_disponibilite.getCheckedRadioButtonId();
                mSelectedRadioButton = findViewById(radioSelectedId);

                if (mSelectedRadioButton.getText().toString().equals("Disponible")) {
                    mLivreur_disponibilite = "Disponible";
                } else if (mSelectedRadioButton.getText().toString().equals("Indisponible")) {
                    mLivreur_disponibilite = "Indisponible";
                }

                int livreur_x = Integer.valueOf(mTxtLivreur_x.getText().toString());
                int livreur_y = Integer.valueOf(mTxtLivreur_y.getText().toString());
                if (!TextUtils.isEmpty(livreurNom) && !TextUtils.isEmpty(strlivreur_x)
                        && !TextUtils.isEmpty(strlivreur_y)) {
                    // update the "livreur"
                    mLivreurDao.updateLivreur(mLivreurId, livreurNom.toString(), livreur_x, livreur_y, mLivreur_disponibilite);
                    Toast.makeText(this, "Le livreur " + livreurNom.toString() + " a bien été modifier.", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);

                    Intent intent = new Intent(this, ChangeDatabaseChoiceActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.empty_fields_message, Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.btn_back:
                finish();
                break;

            case R.id.btn_faq:
                CustomDialogClass cdd = new CustomDialogClass();
                String msg = "Ici vous pouvez <font color='#cccccc'>modifier</font> le livreur choisi.";
                cdd.showDialog(this, "Help Center", Html.fromHtml(msg));
                break;
            default:
                break;
        }
    }

    //Method to show user which radioButton is selected
    public void checkButton(View v) {
        int radioSelectedId = mRGLivreur_disponibilite.getCheckedRadioButtonId();
        mSelectedRadioButton = findViewById(radioSelectedId);
        Toast.makeText(this, mLivreurNom + " sera : " + mSelectedRadioButton.getText(), Toast.LENGTH_LONG).show();
    }

    //Method to run a custom dialog box at first run of the app
    private void doFirstRun() {
        SharedPreferences settings = getSharedPreferences("saveopenApp", MODE_PRIVATE);

        if (settings.getBoolean("isFirstRunChangeLivreur", true)) {

            CustomDialogClass cdd = new CustomDialogClass();
            String msg = "Ici vous pouvez <font color='#cccccc'>modifier</font> le livreur choisi.";

            cdd.showDialog(this, "C'est 'Jordie' pas 'Jordi' ", Html.fromHtml(msg));

            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("isFirstRunChangeLivreur", false);
            editor.commit();
        }
    }
}
