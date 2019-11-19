package ws.splash.projetcandidature.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ws.splash.projetcandidature.R;
import ws.splash.projetcandidature.dao.ColisDAO;
import ws.splash.projetcandidature.model.Colis;


public class ListColisAdapter extends BaseAdapter {


    public static final String TAG = "ListColisAdapter";

    private List<Colis> mItems;
    private LayoutInflater mInflater;

    private ColisDAO mColisDao;


    public ListColisAdapter(Activity context, List<Colis> listColis) {
        this.setItems(listColis);
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return (getItems() != null && !getItems().isEmpty()) ? getItems().size() : 0;
    }

    @Override
    public Colis getItem(int position) {
        return (getItems() != null && !getItems().isEmpty()) ? getItems().get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return (getItems() != null && !getItems().isEmpty()) ? getItems().get(position).getId() : position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder;
        mColisDao = new ColisDAO(parent.getContext());

        if (v == null) {
            v = mInflater.inflate(R.layout.list_item_colis, parent, false);
            holder = new ViewHolder();
            //Initialize views
            holder.txtColisNom = (TextView) v.findViewById(R.id.txt_colis_nom);
            holder.txtColis_x = (TextView) v.findViewById(R.id.txt_colis_x);
            holder.txtColis_y = (TextView) v.findViewById(R.id.txt_colis_y);
            holder.txtColis_date = (TextView) v.findViewById(R.id.txt_colis_date);
            holder.txtColis_statut = (TextView) v.findViewById(R.id.txt_colis_statut);
            holder.txtLivreurColisNom = (TextView) v.findViewById(R.id.txt_livreur_colis_nom);
            holder.imgColis_image_statut = (ImageView) v.findViewById(R.id.img_colis_image_statut);
            holder.txtTempsLivraison = (TextView) v.findViewById(R.id.text_view_countdown);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        // fill row data
        Colis currentItem = getItem(position);
        if (currentItem != null) {
            holder.txtColisNom.setText(currentItem.getNom());
            holder.txtColis_x.setText(String.valueOf(currentItem.getColis_X()));
            holder.txtColis_y.setText(String.valueOf(currentItem.getColis_Y()));
            holder.txtColis_date.setText(currentItem.getDate());
            if (currentItem.getStatut().equals("1")) {
                holder.txtColis_statut.setText("Colis livré ");
                holder.imgColis_image_statut.setImageResource(R.drawable.livrer);
            } else if (currentItem.getStatut().equals("0")) {
                holder.txtColis_statut.setText("En cours de livraison");
                holder.imgColis_image_statut.setImageResource(R.drawable.en_livraison);

            } else if (currentItem.getStatut().equals("-1")) {
                holder.txtColis_statut.setText("Dans le centre de distribution");
                holder.imgColis_image_statut.setImageResource(R.drawable.centre);

            }
            holder.txtLivreurColisNom.setText(currentItem.getLivreur().getNom());


            if (currentItem.getStatut().equals("0")) {
                holder.txtTempsLivraison.setVisibility(View.VISIBLE);

                //Get current date
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy");
                String formattedCurrentDate = df.format(Calendar.getInstance().getTime());
                String formattedTomorrowDate = df.format(Calendar.getInstance().getTime());


                //Get current hour
                Calendar currentHour = Calendar.getInstance();
                int hour = currentHour.get(Calendar.HOUR_OF_DAY);

                //Get time limit which is "21:00"
                Calendar limit = Calendar.getInstance();
                limit.set(Calendar.HOUR_OF_DAY, Integer.parseInt("21"));
                holder.txtTempsLivraison.setVisibility(View.VISIBLE);


                if (currentItem.getTempsLivraison().equals("12") && currentItem.getDate().equals(formattedCurrentDate) && (holder.txtTempsLivraison.getText().equals("Sera livré demain"))) { //If the "colis" should be delivered the next day at 12:00
                    holder.txtTempsLivraison.setText("Sera livré avant " + currentItem.getTempsLivraison() + " heures.");
                }

                if (21 < Integer.parseInt(currentItem.getTempsLivraison()) || 8 > Integer.parseInt(currentItem.getTempsLivraison())) { //Set the "colis" delivery date to the next day, If the delivery schedule isn't between 8:00 && 21:00
                    mColisDao.updateColisTempsLivraison(currentItem, "12");
                    holder.txtTempsLivraison.setText("Sera livré demain");

                    Calendar c = Calendar.getInstance();
                    try {
                        c.setTime(df.parse(formattedTomorrowDate));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    c.add(Calendar.DATE, 1);  // number of days to add (1 here, so the next day)
                    formattedTomorrowDate = df.format(c.getTime());

                    currentItem.setDate(formattedTomorrowDate);
                    mColisDao.updateColisDate(currentItem, formattedTomorrowDate);

                } else if ((hour == Integer.parseInt(currentItem.getTempsLivraison()) || hour > Integer.parseInt(currentItem.getTempsLivraison())) && currentItem.getDate().equals(formattedCurrentDate)) { // Set "colis"'s statut to "1" so "Livré", If the "colis" delivery date and time has past
                    mColisDao.updateColisStatut(currentItem, "1");
                    notifyDataSetChanged();//update changes
                }

            } else {
                holder.txtTempsLivraison.setVisibility(View.GONE);
            }
            if ((!(holder.txtTempsLivraison.getText().equals("Sera livré demain"))) && (!(holder.txtTempsLivraison.getText().equals("Sera livré avant " + currentItem.getTempsLivraison() + " heures.priority")))) { //display time left before delivery
                holder.txtTempsLivraison.setText("Sera livré avant " + currentItem.getTempsLivraison() + " heures.");
            }


            String dateString = currentItem.getDate();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
            Date convertedDate = new Date();
            try {
                convertedDate = dateFormat.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
            String currentDate = sdf.format(new Date());
            Date currentDateFormat = null;
            try {
                currentDateFormat = new SimpleDateFormat("dd/MM/yy").parse(currentDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (convertedDate.before(currentDateFormat) && currentItem.getStatut().equals("0") && (!(currentItem.getLivreur().getNom().equals("Aucun livreur attribuer")))) { //Set "colis"'s delivery date to the current date when affected to a "livreur", if the "colis" basic delivery date has been past
                mColisDao.updateColisDate(currentItem, currentDate);
                notifyDataSetChanged();
            }

        }

        //Set animation on activity apparition
        Animation animation = AnimationUtils.loadAnimation(v.getContext(), R.anim.fade_in);
        animation.setDuration(200);
        v.startAnimation(animation);
        return v;
    }

    public List<Colis> getItems() {
        return mItems;
    }

    public void setItems(List<Colis> mItems) {
        this.mItems = mItems;
    }

    public class ViewHolder {
        TextView txtColisNom;
        TextView txtColis_x;
        TextView txtColis_y;
        TextView txtColis_date;
        TextView txtColis_statut;
        TextView txtLivreurColisNom;
        ImageView imgColis_image_statut;
        TextView txtTempsLivraison;
    }

}
