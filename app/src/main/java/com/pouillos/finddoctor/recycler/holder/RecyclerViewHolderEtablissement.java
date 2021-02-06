package com.pouillos.finddoctor.recycler.holder;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.pouillos.finddoctor.R;
import com.pouillos.finddoctor.entities.Etablissement;
import com.pouillos.finddoctor.recycler.adapter.RecyclerAdapterEtablissement;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecyclerViewHolderEtablissement extends RecyclerView.ViewHolder implements View.OnClickListener {

    @BindView(R.id.detail)
    TextView detail;

    private WeakReference<RecyclerAdapterEtablissement.Listener> callbackWeakRef;

    public RecyclerViewHolderEtablissement(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        //ButterKnife.bind(this);
    }

    public void updateWithEtablissement(Etablissement etablissement, RecyclerAdapterEtablissement.Listener callback){
       // double distance = ChercherEtablissementActivity.calculerDistance(latitude,longitude,etablissement.getLatitude(),etablissement.getLongitude());

//todo revoir l'affichage en limitan apres la virgule - FAIT OK
        NumberFormat formatter = new DecimalFormat("#0.0");
       // System.out.println(formatter.format(4.0));

      //  this.detail.setText(etablissement.getRaisonSocial() + " - " + formatter.format(distance)+ " km");
        this.detail.setText(etablissement.getRaisonSocial() + " - " + formatter.format(etablissement.getDistance())+ " km");
        this.detail.setOnClickListener(this);

        //4 - Create a new weak Reference to our Listener
        this.callbackWeakRef = new WeakReference<RecyclerAdapterEtablissement.Listener>(callback);
    }


    @Override
    public void onClick(View view) {
        // 5 - When a click happens, we fire our listener.
        RecyclerAdapterEtablissement.Listener callback = callbackWeakRef.get();
        if (callback != null) callback.onClickEtablissementButton(getAdapterPosition());
    }
}
