package ws.splash.projetcandidature.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import ws.splash.projetcandidature.R;
import ws.splash.projetcandidature.model.CustomDialogClass;


public class MainActivity extends AppCompatActivity {

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        // Dashboard instantiate
        DashboardFrag dashboardFrag = new DashboardFrag();

        fragmentTransaction.replace(R.id.container, dashboardFrag); //replace the "MainActivity view" by the "DashboardFrag View"
        fragmentTransaction.commit();

        // Show custom dialog box at first run
        doFirstRun();
    }

    //Method to run a custom dialog box at first run of the app
    private void doFirstRun() {
        SharedPreferences settings = getSharedPreferences("saveopenApp", MODE_PRIVATE);
        if (settings.getBoolean("isFirstRun", true)) {

            CustomDialogClass cdd = new CustomDialogClass();
            String msg = "Bienvenue dans l'application de gestion des livraisons de <font color='#cccccc'>Infomaniak</font>, je vous guiderai lors de cette première utilisation. ";
            String msg2 = "Depuis ce menu vous pourrez accéder aux <font color='#cccccc'>différentes fonctionnalités</font> de l'application. ";
            cdd.showDialog(this, "Allons-y !", Html.fromHtml(msg2));
            cdd.showDialog(this, "Welcome !", Html.fromHtml(msg));

            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("isFirstRun", false);
            editor.commit();
        }
    }


}
