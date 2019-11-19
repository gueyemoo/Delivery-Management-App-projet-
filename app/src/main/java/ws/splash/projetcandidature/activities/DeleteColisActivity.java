package ws.splash.projetcandidature.activities;

import ws.splash.projetcandidature.R;
import ws.splash.projetcandidature.adapter.ListColisAdapter;
import ws.splash.projetcandidature.dao.ColisDAO;
import ws.splash.projetcandidature.model.Colis;
import ws.splash.projetcandidature.model.CustomDialogClass;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class DeleteColisActivity extends Activity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener, View.OnClickListener {

    public static final String TAG = "DeleteColisActivity";

    private ListView mListviewColis;
    private TextView mTxtEmptyListColis;
    private TextView header;

    private ImageButton mBtnBack;
    private ImageButton mBtnFaq;

    private ListColisAdapter mAdapter;
    private List<Colis> mListColis;

    private ColisDAO mColisDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_colis);

        // initialize views
        initViews();

        // initialize DAO
        mColisDao = new ColisDAO(this);

        // fill the listView
        mListColis = mColisDao.getAllColis();

        if (mListColis != null && !mListColis.isEmpty()) {
            mAdapter = new ListColisAdapter(this, mListColis);
            mListviewColis.setAdapter(mAdapter);
        } else {
            mTxtEmptyListColis.setVisibility(View.VISIBLE);
            mListviewColis.setVisibility(View.GONE);
        }
        // Show custom dialog box at first run
        doFirstRun();
    }

    //Method to initialize views
    private void initViews() {
        this.mListviewColis = (ListView) findViewById(R.id.list_colis);
        this.mTxtEmptyListColis = (TextView) findViewById(R.id.txt_empty_list_colis);
        this.header = (TextView) findViewById(R.id.txt_header);
        this.mBtnBack = (ImageButton) findViewById(R.id.btn_back);
        this.mBtnFaq = (ImageButton) findViewById(R.id.btn_faq);
        this.mListviewColis.setOnItemClickListener(this);
        this.mListviewColis.setOnItemLongClickListener(this);
        this.mBtnBack.setOnClickListener(this);
        this.mBtnFaq.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mColisDao.close();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Colis clickedColis = mAdapter.getItem(position);

        showDeleteDialogConfirmation(clickedColis);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

    //Method to show delete dialog box confirmation before deleting the "colis" if confirm
    private void showDeleteDialogConfirmation(final Colis colis) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Supprimer");
        alertDialogBuilder
                .setMessage("Êtes-vous sur de vouloir supprimer le colis \""
                        + colis.getNom() + "\" ?");

        // set positive button YES message
        alertDialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // delete the colis and refresh the list
                if (mColisDao != null) {
                    mColisDao.deleteColis(colis);

                    //refresh the listView
                    mListColis.remove(colis);
                    if (mListColis.isEmpty()) {
                        mListviewColis.setVisibility(View.GONE);
                        mTxtEmptyListColis.setVisibility(View.VISIBLE);
                    }

                    mAdapter.setItems(mListColis);
                    mAdapter.notifyDataSetChanged();
                }

                dialog.dismiss();
                Toast.makeText(DeleteColisActivity.this, R.string.colis_deleted_successfully, Toast.LENGTH_LONG).show();

            }
        });

        // Set neutral button OK
        alertDialogBuilder.setNeutralButton(android.R.string.no, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;

            case R.id.btn_faq:
                CustomDialogClass cdd = new CustomDialogClass();
                String msg = "Ici vous pouvez <font color='#cccccc'>supprimer</font> un colis.";
                String msg2 = "Il vous suffit de <font color='#cccccc'>choisir</font> un colis ;)";
                cdd.showDialog(this, "Help Center", Html.fromHtml(msg2));
                cdd.showDialog(this, "Help Center", Html.fromHtml(msg));
                break;

            default:
                break;
        }
    }

    //Method to run a custom dialog box at first run of the app
    private void doFirstRun() {
        SharedPreferences settings = getSharedPreferences("saveopenApp", MODE_PRIVATE);

        if (settings.getBoolean("isFirstRunDeleteColis", true)) {

            CustomDialogClass cdd = new CustomDialogClass();
            String msg = "Ici vous pouvez <font color='#cccccc'>supprimer</font> un colis.";
            String msg2 = "Il vous suffit de <font color='#cccccc'>choisir</font> un colis ;)";

            cdd.showDialog(this, "Une commande annuler ? ", Html.fromHtml(msg2));
            cdd.showDialog(this, "Une commande annuler ? ", Html.fromHtml(msg));


            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("isFirstRunDeleteColis", false);
            editor.commit();
        }
    }
}
