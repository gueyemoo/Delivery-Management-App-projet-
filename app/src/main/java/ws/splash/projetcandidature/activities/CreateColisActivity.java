package ws.splash.projetcandidature.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ws.splash.projetcandidature.R;
import ws.splash.projetcandidature.dao.ColisDAO;
import ws.splash.projetcandidature.dao.LivreurDAO;
import ws.splash.projetcandidature.model.Colis;
import ws.splash.projetcandidature.model.CustomDialogClass;
import ws.splash.projetcandidature.model.InputFilterMinMax;
import ws.splash.projetcandidature.model.Livreur;


public class CreateColisActivity extends Activity implements View.OnClickListener {

    public static final String TAG = "CreateColisActivity";

    final Calendar myCalendar = Calendar.getInstance();

    private EditText mTxtColisNom;
    private EditText mTxtColis_x;
    private EditText mTxtColis_y;
    private EditText mTxtColis_date;

    private Button mBtnAdd;
    private ImageButton mBtnBack;
    private ImageButton mBtnFaq;

    private LivreurDAO mLivreurDao;
    private ColisDAO mColisDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_colis);

        // initialize views
        initViews();

        // initialize DAO
        this.mLivreurDao = new LivreurDAO(this);
        this.mColisDao = new ColisDAO(this);

        // fill the listView & set the first "livreur" as "centre de distribution"
        List<Livreur> listLivreurs = mLivreurDao.getAllLivreursDisponible_and_CentreDitribution();
        listLivreurs.get(0).setNom("Centre de distribution");

        // Limit the user input for x
        mTxtColis_x.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mTxtColis_x.setFilters(new InputFilter[]{new InputFilterMinMax("-100", "100")});
            }
        });

        // Limit the user input for y
        mTxtColis_y.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mTxtColis_y.setFilters(new InputFilter[]{new InputFilterMinMax("-100", "100")});
            }
        });

        // Set datepicker default value to current date
        mTxtColis_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(CreateColisActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        // Show custom dialog box at first run
        doFirstRun();
    }

    //Method to initialize views
    private void initViews() {
        this.mTxtColisNom = (EditText) findViewById(R.id.txt_colis_nom);
        this.mTxtColis_x = (EditText) findViewById(R.id.txt_colis_x);
        this.mTxtColis_y = (EditText) findViewById(R.id.txt_colis_y);
        this.mTxtColis_date = (EditText) findViewById(R.id.txt_colis_date);

        this.mBtnAdd = (Button) findViewById(R.id.btn_add_colis);
        this.mBtnAdd.setOnClickListener(this);
        this.mBtnBack = (ImageButton) findViewById(R.id.btn_back);
        this.mBtnBack.setOnClickListener(this);
        this.mBtnFaq = (ImageButton) findViewById(R.id.btn_faq);
        this.mBtnFaq.setOnClickListener(this);
    }

    //Set datepicker default value to current date
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    //Check if this day isn't past and if so, Update the date of delivery of the "colis" by the choosen date
    private void updateLabel() {
        String myFormat = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);
        Date currentTime = Calendar.getInstance().getTime();

        if (myCalendar.getTime().before(currentTime)) {
            mTxtColis_date.setText(sdf.format(currentTime));
        } else if (myCalendar.getTime().after(currentTime)) {
            mTxtColis_date.setText(sdf.format(myCalendar.getTime()));
        } else {
            mTxtColis_date.setText(sdf.format(myCalendar.getTime()));
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_colis:
                Editable colisNom = mTxtColisNom.getText();
                Editable strcolis_x = mTxtColis_x.getText();
                Editable strcolis_y = mTxtColis_y.getText();
                Editable colis_date = mTxtColis_date.getText();

                if (TextUtils.isEmpty(strcolis_x)) { //default value of x if empty
                    mTxtColis_x.setText("0");
                }

                if (TextUtils.isEmpty(strcolis_y)) { //default value of y if empty
                    mTxtColis_y.setText("0");
                }

                int colis_x = Integer.valueOf(mTxtColis_x.getText().toString());
                int colis_y = Integer.valueOf(mTxtColis_y.getText().toString());

                // Check if one field is empty, if not add the created "colis" to database
                if (!TextUtils.isEmpty(colisNom) && !TextUtils.isEmpty(strcolis_x)
                        && !TextUtils.isEmpty(strcolis_y) && !TextUtils.isEmpty(colis_date)) {
                    Colis createdColis = mColisDao.createColis(colisNom.toString(), colis_x, colis_y, colis_date.toString(), "-1", "0", 1);
                    Toast.makeText(this, "Le colis " + createdColis.getNom() + " a bien été ajouter.", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(this, R.string.empty_fields_message, Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.btn_faq:
                CustomDialogClass cdd = new CustomDialogClass();
                String msg = "Ici vous pouvez <font color='#cccccc'>ajouter</font> un nouveau colis à votre liste.";
                cdd.showDialog(this, "Help Center", Html.fromHtml(msg));
                break;

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
        mLivreurDao.close();
        mColisDao.close();
    }

    //Method to run a custom dialog box at first run of the app
    private void doFirstRun() {
        SharedPreferences settings = getSharedPreferences("saveopenApp", MODE_PRIVATE);

        if (settings.getBoolean("isFirstRunAddColis", true)) {

            CustomDialogClass cdd = new CustomDialogClass();
            String msg = "Ici vous pouvez <font color='#cccccc'>ajouter</font> un nouveau colis à votre liste.";


            cdd.showDialog(this, "Une nouvelle livraison ? ", Html.fromHtml(msg));


            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("isFirstRunAddColis", false);
            editor.commit();
        }
    }
}
