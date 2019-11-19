package ws.splash.projetcandidature.activities;

import ws.splash.projetcandidature.R;
import ws.splash.projetcandidature.adapter.ListLivreursAdapter;
import ws.splash.projetcandidature.dao.ColisDAO;
import ws.splash.projetcandidature.dao.LivreurDAO;
import ws.splash.projetcandidature.model.Colis;
import ws.splash.projetcandidature.model.CustomDialogClass;
import ws.splash.projetcandidature.model.Livreur;

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

public class DeleteLivreurActivity extends Activity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener, View.OnClickListener {

    public static final String TAG = "DeleteLivreurActivity";

    private ListView mListviewLivreurs;
    private TextView mTxtEmptyListLivreurs;
    private TextView header;

    private ImageButton mBtnBack;
    private ImageButton mBtnFaq;

    private ListLivreursAdapter mAdapter;
    private List<Livreur> mListLivreurs;

    private LivreurDAO mLivreurDao;
    private ColisDAO mColisDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_livreur);

        // initialize views
        initViews();

        // initialize DAO
        mLivreurDao = new LivreurDAO(this);
        mColisDao = new ColisDAO(this);


        // fill the listView
        mListLivreurs = mLivreurDao.getAllLivreurs();

        if (mListLivreurs != null && !mListLivreurs.isEmpty()) {
            mAdapter = new ListLivreursAdapter(this, mListLivreurs);
            mListviewLivreurs.setAdapter(mAdapter);
        } else {
            mTxtEmptyListLivreurs.setVisibility(View.VISIBLE);
            mListviewLivreurs.setVisibility(View.GONE);
        }
        // Show custom dialog box at first run
        doFirstRun();
    }

    //Method to initialize views
    private void initViews() {
        this.mListviewLivreurs = (ListView) findViewById(R.id.list_livreurs);
        this.mTxtEmptyListLivreurs = (TextView) findViewById(R.id.txt_empty_list_livreurs);
        this.mBtnBack = (ImageButton) findViewById(R.id.btn_back);
        this.header = (TextView) findViewById(R.id.txt_header);
        this.mBtnFaq = (ImageButton) findViewById(R.id.btn_faq);
        this.mListviewLivreurs.setOnItemClickListener(this);
        this.mListviewLivreurs.setOnItemLongClickListener(this);
        this.mBtnBack.setOnClickListener(this);
        this.mBtnFaq.setOnClickListener(this);

    }

    //Show "livreur" deleting dialog box if he's not "en cours de livraison"
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Livreur clickedLivreur = mAdapter.getItem(position);

        List<Colis> mListColisLivreur = mColisDao.getColisDeLivreur(clickedLivreur.getId());
        for (int i = 0; i < mListColisLivreur.size(); i++) {
            if (mListColisLivreur.get(i).getLivreur().getStatut().equals("0")) {
                Toast.makeText(this, clickedLivreur.getNom() + " est en cours de livraison.", Toast.LENGTH_SHORT).show();
            } else {
                showDeleteDialogConfirmation(clickedLivreur);
            }
        }

        if ((mColisDao.getColisDeLivreur(clickedLivreur.getId()).size()) == 0) {
            showDeleteDialogConfirmation(clickedLivreur);
        }
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }


    //Method to show delete dialog box confirmation before deleting the "livreur" if confirm
    private void showDeleteDialogConfirmation(final Livreur clickedLivreur) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Delete");
        alertDialogBuilder.setMessage("Êtes-vous sur de vouloir supprimer \"" + clickedLivreur.getNom() + "\" ?");

        // set positive button YES message
        alertDialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // delete the livreur and refresh the list
                if (mLivreurDao != null) {
                    mLivreurDao.deleteLivreur(clickedLivreur);
                    mListLivreurs.remove(clickedLivreur);
                    //refresh the listView
                    if (mListLivreurs.isEmpty()) {
                        mListviewLivreurs.setVisibility(View.GONE);
                        mTxtEmptyListLivreurs.setVisibility(View.VISIBLE);
                    }
                    mAdapter.setItems(mListLivreurs);
                    mAdapter.notifyDataSetChanged();
                }
                dialog.dismiss();
                Toast.makeText(DeleteLivreurActivity.this, R.string.livreur_deleted_successfully, Toast.LENGTH_LONG).show();
            }
        });

        // set neutral button OK
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
                String msg = "Ici vous pouvez <font color='#cccccc'>supprimer</font> un livreur.";
                String msg2 = "Il vous suffit de <font color='#cccccc'>choisir</font> un livreur ;) <br> <br> <u>N.B:</u> Vous ne pourrez pas supprimer un livreur <font color='#cccccc'>en cours de livraison.</font>";

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

        if (settings.getBoolean("isFirstRunDeleteLivreur", true)) {

            CustomDialogClass cdd = new CustomDialogClass();
            String msg = "Ici vous pouvez <font color='#cccccc'>supprimer</font> un livreur.";
            String msg2 = "Il vous suffit de <font color='#cccccc'>choisir</font> un livreur ;) <br> <br> <u>N.B:</u> Vous ne pourrez pas supprimer un livreur <font color='#cccccc'>en cours de livraison.</font>";

            cdd.showDialog(this, "C'est l'heure de la retraite ? ", Html.fromHtml(msg2));
            cdd.showDialog(this, "C'est l'heure de la retraite ? ", Html.fromHtml(msg));


            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("isFirstRunDeleteLivreur", false);
            editor.commit();
        }
    }
}
