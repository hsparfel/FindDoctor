package com.pouillos.finddoctor.entities;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.pouillos.finddoctor.dao.ContactDao;
import com.pouillos.finddoctor.dao.DaoSession;
import com.pouillos.finddoctor.dao.DepartementDao;
import com.pouillos.finddoctor.dao.ProfessionDao;
import com.pouillos.finddoctor.dao.RegionDao;
import com.pouillos.finddoctor.dao.SavoirFaireDao;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToOne;

import java.io.IOException;
import java.util.List;

@Entity
public class ContactIgnore implements Comparable<ContactIgnore>{

    @Id
    private Long id;

    @NotNull
    private String idPP;

    private String fichierImport;

    private int numLigne;



    @Generated(hash = 409989920)
    public ContactIgnore(Long id, @NotNull String idPP, String fichierImport,
            int numLigne) {
        this.id = id;
        this.idPP = idPP;
        this.fichierImport = fichierImport;
        this.numLigne = numLigne;
    }

    @Generated(hash = 1137428311)
    public ContactIgnore() {
    }



    @Override
    public String toString() {
        return idPP+" - "+fichierImport;
    }

    @Override
    public int compareTo(ContactIgnore o) {
        return this.idPP.compareTo(o.idPP);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdPP() {
        return this.idPP;
    }

    public void setIdPP(String idPP) {
        this.idPP = idPP;
    }

    public String getFichierImport() {
        return this.fichierImport;
    }

    public void setFichierImport(String fichierImport) {
        this.fichierImport = fichierImport;
    }

    public int getNumLigne() {
        return this.numLigne;
    }

    public void setNumLigne(int numLigne) {
        this.numLigne = numLigne;
    }

    

}
