package com.pouillos.finddoctor.entities;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.pouillos.finddoctor.dao.DaoSession;
import com.pouillos.finddoctor.dao.DepartementDao;
import com.pouillos.finddoctor.dao.EtablissementDao;
import com.pouillos.finddoctor.dao.RegionDao;
import com.pouillos.finddoctor.dao.TypeEtablissementDao;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Transient;

import java.io.IOException;
import java.util.List;

@Entity
public class EtablissementLight implements Comparable<EtablissementLight> {

    @Id
    private Long id;

    private String numeroFinessET;
    private String raisonSocial;
    private String adresse;
    private String cp;
    private String ville;
    private String telephone;
    private String fax;

    @Generated(hash = 1295787428)
    public EtablissementLight(Long id, String numeroFinessET, String raisonSocial,
            String adresse, String cp, String ville, String telephone, String fax) {
        this.id = id;
        this.numeroFinessET = numeroFinessET;
        this.raisonSocial = raisonSocial;
        this.adresse = adresse;
        this.cp = cp;
        this.ville = ville;
        this.telephone = telephone;
        this.fax = fax;
    }

    @Generated(hash = 873956003)
    public EtablissementLight() {
    }

    @Override
    public int compareTo(EtablissementLight o) {
        return this.raisonSocial.compareTo(o.raisonSocial);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroFinessET() {
        return this.numeroFinessET;
    }

    public void setNumeroFinessET(String numeroFinessET) {
        this.numeroFinessET = numeroFinessET;
    }

    public String getRaisonSocial() {
        return this.raisonSocial;
    }

    public void setRaisonSocial(String raisonSocial) {
        this.raisonSocial = raisonSocial;
    }

    public String getAdresse() {
        return this.adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getCp() {
        return this.cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public String getVille() {
        return this.ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getFax() {
        return this.fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

}
