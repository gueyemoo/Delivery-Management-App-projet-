package ws.splash.projetcandidature.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ws.splash.projetcandidature.R;
import ws.splash.projetcandidature.model.Livreur;


public class ListLivreursAdapter extends BaseAdapter {

    public static final String TAG = "ListLivreursAdapter";

    private List<Livreur> mItems;
    private LayoutInflater mInflater;

    public ListLivreursAdapter(Context context, List<Livreur> listLivreurs) {
        this.setItems(listLivreurs);
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return (getItems() != null && !getItems().isEmpty()) ? getItems().size() : 0;
    }

    @Override
    public Livreur getItem(int position) {
        return (getItems() != null && !getItems().isEmpty()) ? (Livreur) getItems().get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return (getItems() != null && !getItems().isEmpty()) ? getItems().get(position).getId() : position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder;
        if (v == null) {
            v = mInflater.inflate(R.layout.list_item_livreurs, parent, false);
            holder = new ViewHolder();
            // Initialize views
            holder.txtLivreurNom = (TextView) v.findViewById(R.id.txt_livreur_nom);
            holder.txtlivreur_x = (TextView) v.findViewById(R.id.txt_livreur_x);
            holder.txtlivreur_y = (TextView) v.findViewById(R.id.txt_livreur_y);
            holder.txtlivreurDisponibilite = (TextView) v.findViewById(R.id.txt_livreur_disponibilite);
            holder.txtLivreur_statut = (TextView) v.findViewById(R.id.txt_livreur_statut);

            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        // fill row data
        Livreur currentItem = getItem(position);
        if (currentItem != null) {
            holder.txtLivreurNom.setText(currentItem.getNom());
            holder.txtlivreur_x.setText(String.valueOf(currentItem.getLivreur_X()));
            holder.txtlivreur_y.setText(String.valueOf(currentItem.getLivreur_Y()));
            holder.txtlivreurDisponibilite.setText(currentItem.getDisponibilite());

            if ((currentItem.getDisponibilite()).equals("Disponible")) {
                holder.txtlivreurDisponibilite.setTextColor(Color.parseColor("#00FF00"));
            } else if (currentItem.getDisponibilite().equals("Indisponible")) {
                holder.txtlivreurDisponibilite.setTextColor(Color.parseColor("#ff0000"));
            }

            if (currentItem.getStatut().equals("1")) {
                holder.txtLivreur_statut.setText("À son domicile");
            } else if (currentItem.getStatut().equals("0")) {
                holder.txtLivreur_statut.setText("En livraison");
            } else if (currentItem.getStatut().equals("-1")) {
                holder.txtLivreur_statut.setText("Inconnu");
            }

        }

        //Set animation on activity apparition
        Animation animation = AnimationUtils.loadAnimation(v.getContext(), R.anim.fade_in);
        animation.setDuration(200);
        v.startAnimation(animation);
        return v;
    }

    public List<Livreur> getItems() {
        return mItems;
    }

    public void setItems(List<Livreur> mItems) {
        this.mItems = mItems;
    }

    class ViewHolder {
        TextView txtLivreurNom;
        TextView txtlivreur_x;
        TextView txtlivreur_y;
        TextView txtlivreurDisponibilite;
        TextView txtLivreur_statut;
    }

}
