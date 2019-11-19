package ws.splash.projetcandidature.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import ws.splash.projetcandidature.R;
import ws.splash.projetcandidature.dao.ColisDAO;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DashboardFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardFrag extends Fragment {

    private CardView cardTop, cardRight, cardLeft, cardLeft2;
    private TextView mcolisEnLivraison;
    private ColisDAO mColisDao;
    private Button mbuttonUpdate;
    private TextView mcurrentDate;

    public static final int REQUEST_CODE_SHOW_LIVREUR_DISPONIBLE = 5;
    public static final int REQUEST_CODE_SHOW_ALL_LIVREUR = 10;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public DashboardFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DashboardFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static DashboardFrag newInstance(String param1, String param2) {
        DashboardFrag fragment = new DashboardFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);
        // Initialize card
        cardTop = v.findViewById(R.id.cardTop);
        cardRight = v.findViewById(R.id.cardRight);
        cardLeft = v.findViewById(R.id.cardLeft);
        cardLeft2 = v.findViewById(R.id.cardLeft2);
        mcolisEnLivraison = v.findViewById(R.id.textViewColisEnLivraison);
        mbuttonUpdate = v.findViewById(R.id.buttonRefresh);
        mcurrentDate = v.findViewById(R.id.currentDate);

        // Initialize Animations

        Animation animeBottomToTop = AnimationUtils.loadAnimation(getActivity(), R.anim.anime_bottom_to_top);
        Animation animeTopToBottom = AnimationUtils.loadAnimation(getActivity(), R.anim.anime_top_to_bottom);
        Animation animeRightToleft = AnimationUtils.loadAnimation(getActivity(), R.anim.anime_right_to_left);
        Animation animeLeftToRight = AnimationUtils.loadAnimation(getActivity(), R.anim.anime_left_to_right);


        // setup Animation :
        cardLeft2.setAnimation(animeBottomToTop);
        cardTop.setAnimation(animeTopToBottom);
        cardRight.setAnimation(animeRightToleft);
        cardLeft.setAnimation(animeLeftToRight);

        Button buttonLiv = (Button) v.findViewById(R.id.button_listdeslivreurs);
        buttonLiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ListLivreursActivity.class);
                intent.putExtra("calling_card", REQUEST_CODE_SHOW_ALL_LIVREUR);
                startActivity(intent);

            }
        });

        cardLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ListLivreursActivity.class);
                intent.putExtra("calling_card", REQUEST_CODE_SHOW_LIVREUR_DISPONIBLE);
                startActivity(intent);
            }
        });

        cardLeft2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CentreDeDistribution.class);
                startActivity(intent);

            }
        });

        cardRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangeDatabaseChoiceActivity.class);
                startActivity(intent);
            }
        });

        cardTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DashboardOfTheDayActivity.class);
                startActivity(intent);
            }
        });

        //this button update the home page to get the new number of "colis en livraison" and the current date
        mbuttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mColisDao = new ColisDAO(getContext());
                mcolisEnLivraison.setText("En cours de livraison aujourd'hui\n" + mColisDao.getAllColisEnLivraison().size() + " Colis");
                String date_n = new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(new Date());
                mcurrentDate.setText("Situation du : " + date_n);
                Toast.makeText(getContext(), "Page mise à jour !", Toast.LENGTH_SHORT).show();
            }
        });

        mColisDao = new ColisDAO(getContext());
        mcolisEnLivraison.setText("En cours de livraison aujourd'hui\n" + mColisDao.getAllColisEnLivraison().size() + " Colis");

        String date_n = new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(new Date());
        mcurrentDate.setText("Situation du : " + date_n);

        // Inflate the layout for this fragment
        return v;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
