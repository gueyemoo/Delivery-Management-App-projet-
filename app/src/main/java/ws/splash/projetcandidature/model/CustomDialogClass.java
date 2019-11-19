package ws.splash.projetcandidature.model;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.text.Spanned;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import ws.splash.projetcandidature.R;

public class CustomDialogClass {

    private Button mOkButton;

    private TextView mTitle;
    private TextView mMsg;


    //Method to show the custom dialog box, second parameter is the title of the dialog box, third parameter is the message
    public void showDialog(Activity activity, String title, Spanned msg) {
        final Dialog dialog = new Dialog(activity, R.style.Theme_AppCompat_Light_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.customdialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);


        mTitle = (TextView) dialog.findViewById(R.id.txt_titledialog);
        mTitle.setText(title);

        mMsg = dialog.findViewById(R.id.txt_dialog);
        mMsg.setText(msg);

        mOkButton = (Button) dialog.findViewById(R.id.ok_button);
        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.ok_button:
                        dialog.dismiss();
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();
    }
}