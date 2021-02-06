package com.pouillos.finddoctor.recycler.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pouillos.finddoctor.R;
import androidx.recyclerview.widget.RecyclerView;


import com.pouillos.finddoctor.entities.Etablissement;
import com.pouillos.finddoctor.recycler.holder.RecyclerViewHolderEtablissement;

import java.util.List;

public class RecyclerAdapterEtablissement extends RecyclerView.Adapter<RecyclerViewHolderEtablissement> {

        private List<Etablissement> listEtablissement;
        double latitude;
        double longitude;

    public interface Listener {
        void onClickEtablissementButton(int position);
    }

    private final Listener callback;

        /*public RecyclerAdapterEtablissement(List<Etablissement> listEtablissement,double latitude, double longitude, Listener callback) {
            this.listEtablissement = listEtablissement;
            this.latitude = latitude;
            this.longitude = longitude;
            this.callback = callback;
        }*/

    public RecyclerAdapterEtablissement(List<Etablissement> listEtablissement, Listener callback) {
        this.listEtablissement = listEtablissement;
        this.callback = callback;
    }

        @Override
        public RecyclerViewHolderEtablissement onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.recycler_list_etablissement, parent, false);

            return new RecyclerViewHolderEtablissement(view);
        }

        @Override
        public void onBindViewHolder(RecyclerViewHolderEtablissement viewHolder, int position) {
              //  viewHolder.updateWithEtablissement(this.listEtablissement.get(position), this.callback);
            viewHolder.updateWithEtablissement(this.listEtablissement.get(position),this.callback);

        }

        @Override
        public int getItemCount() {
            return this.listEtablissement.size();
        }

    public Etablissement getEtablissement(int position){
        return this.listEtablissement.get(position);
    }

}
