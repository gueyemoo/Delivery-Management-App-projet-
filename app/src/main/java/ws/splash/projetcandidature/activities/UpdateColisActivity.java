package ws.splash.projetcandidature.activities;

import ws.splash.projetcandidature.R;
import ws.splash.projetcandidature.dao.ColisDAO;
import ws.splash.projetcandidature.model.CustomDialogClass;
import ws.splash.projetcandidature.model.InputFilterMinMax;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
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
import java.util.Locale;

public class UpdateColisActivity extends Activity implements View.OnClickListener {

    public static final String EXTRA_SELECTED_COLIS_ID = "extra_key_selected_colis_id";
    public static final String EXTRA_SELECTED_COLIS_NOM = "extra_key_selected_colis_nom";
    public static final String EXTRA_SELECTED_COLIS_X = "extra_key_selected_colis_x";
    public static final String EXTRA_SELECTED_COLIS_Y = "extra_key_selected_colis_y";
    public static final String EXTRA_SELECTED_COLIS_DATE = "extra_key_selected_colis_date";
    public static final String EXTRA_SELECTED_COLIS_STATUT = "extra_key_selected_colis_statut";


    final Calendar myCalendar = Calendar.getInstance();


    private EditText mTxtColisNom;
    private EditText mTxtColis_x;
    private EditText mTxtColis_y;
    private EditText mTxtColis_date;

    private Button mBtnUpdate;
    private ImageButton mBtnBack;
    private ImageButton mBtnFaq;


    private long mColisId = -1;
    private String mColisNom;
    private Integer mColis_x;
    private Integer mColis_y;
    private String mColis_date;
    private String mColis_statut;

    private ColisDAO mColisDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_colis);

        // initialize views
        initViews();

        // Initialize DAO
        this.mColisDao = new ColisDAO(this);


        Intent intent = getIntent();
        if (intent != null) {// getting extra value from ListColisActivity
            this.mColisId = intent.getLongExtra(EXTRA_SELECTED_COLIS_ID, -1);
            this.mColisNom = intent.getStringExtra(EXTRA_SELECTED_COLIS_NOM);
            this.mColis_x = intent.getIntExtra(EXTRA_SELECTED_COLIS_X, -1);
            this.mColis_y = intent.getIntExtra(EXTRA_SELECTED_COLIS_Y, -1);
            this.mColis_date = intent.getStringExtra(EXTRA_SELECTED_COLIS_DATE);
            this.mColis_statut = intent.getStringExtra(EXTRA_SELECTED_COLIS_STATUT);
        }

        //Setting default value of edittext
        mTxtColisNom.setText(mColisNom);
        mTxtColis_x.setText(String.valueOf(mColis_x));
        mTxtColis_y.setText(String.valueOf(mColis_y));
        mTxtColis_date.setText(mColis_date);

        //Limit the user input for x
        mTxtColis_x.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mTxtColis_x.setFilters(new InputFilter[]{new InputFilterMinMax("-100", "100")});
            }
        });

        //Limit the user input for y
        mTxtColis_y.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mTxtColis_y.setFilters(new InputFilter[]{new InputFilterMinMax("-100", "100")});
            }
        });


        //If the "colis" is in deliver, put the datepicker input to disabled.
        if (mColis_statut.equals("0")) {
            mTxtColis_date.setEnabled(false);
        }

        //Put default value when changing date to current date
        mTxtColis_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(UpdateColisActivity.this, date, myCalendar
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
        this.mBtnUpdate = (Button) findViewById(R.id.btn_update_colis);
        this.mBtnBack = (ImageButton) findViewById(R.id.btn_back);
        this.mBtnFaq = (ImageButton) findViewById(R.id.btn_faq);
        this.mBtnBack.setOnClickListener(this);
        this.mBtnUpdate.setOnClickListener(this);
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

    // Check if this day isn't past and if so, Update the date of delivery of the "colis" by the choosen date
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
    protected void onDestroy() {
        super.onDestroy();
        mColisDao.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update_colis:

                //Get values from editText
                Editable colisNom = mTxtColisNom.getText();
                Editable strcolis_x = mTxtColis_x.getText();
                Editable strcolis_y = mTxtColis_y.getText();
                Editable colisDate = mTxtColis_date.getText();

                int colis_x = Integer.valueOf(mTxtColis_x.getText().toString());
                int colis_y = Integer.valueOf(mTxtColis_y.getText().toString());
                if (!TextUtils.isEmpty(colisNom) && !TextUtils.isEmpty(strcolis_x)
                        && !TextUtils.isEmpty(strcolis_y)) {
                    // Update the "colis"
                    mColisDao.updateColis(mColisId, colisNom.toString(), colis_x, colis_y, colisDate.toString());
                    Toast.makeText(this, "Le colis " + colisNom.toString() + " a bien été modifier", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);

                    Intent intent = new Intent(this, ChangeDatabaseChoiceActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.empty_fields_message, Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.btn_faq:
                CustomDialogClass cdd = new CustomDialogClass();
                String msg = "Ici vous pouvez <font color='#cccccc'>modifier</font> le colis choisi.";
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

        if (settings.getBoolean("isFirstRunChangeColis", true)) {

            CustomDialogClass cdd = new CustomDialogClass();
            String msg = "Ici vous pouvez <font color='#cccccc'>modifier</font> le colis choisi.";

            cdd.showDialog(this, "C'est 'La Wifi' pas 'Le Wifi' ", Html.fromHtml(msg));

            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("isFirstRunChangeColis", false);
            editor.commit();
        }
    }
}
