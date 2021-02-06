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
public class ContactLight implements Comparable<ContactLight>{

    @Id
    private Long id;

    @NotNull
    private String idPP;

    private String codeCivilite;
    private String nom;
    private String prenom;





    private String raisonSocial;
    private String complement;
    private String adresse;
    private String cp;
    private String ville;
    private String telephone;
    private String fax;
    private String email;

    private double latitude;
    private double longitude;



    @Generated(hash = 1453735635)
    public ContactLight(Long id, @NotNull String idPP, String codeCivilite, String nom, String prenom,
            String raisonSocial, String complement, String adresse, String cp, String ville, String telephone,
            String fax, String email, double latitude, double longitude) {
        this.id = id;
        this.idPP = idPP;
        this.codeCivilite = codeCivilite;
        this.nom = nom;
        this.prenom = prenom;
        this.raisonSocial = raisonSocial;
        this.complement = complement;
        this.adresse = adresse;
        this.cp = cp;
        this.ville = ville;
        this.telephone = telephone;
        this.fax = fax;
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Generated(hash = 1867530660)
    public ContactLight() {
    }



  /* public void setCp(String cp) {

        this.cp = cp;
        if (!cp.equalsIgnoreCase("")) {
            this.departement = Departement.find(Departement.class,"numero = ?",cp.substring(0,2)).get(0);
        } else {
            this.departement = Departement.find(Departement.class,"numero = ?","XX").get(0);
        }
        this.region = this.departement.getRegion();
    }*/


    @Override
    public String toString() {
        String affichage = "";
        if (codeCivilite != null) {
            affichage += codeCivilite + " ";
        }
        if (nom != null) {
            affichage += nom + " ";
        }
        if (prenom != null) {
            affichage += prenom;
        }


        return affichage;
    }

    @Override
    public int compareTo(ContactLight o) {
        return this.nom.compareTo(o.nom);
    }

    public void enregisterCoordonnees(Context context) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;

        try {
            address = coder.getFromLocationName(adresse+", "+cp+" "+ville+", FRANCE",1);
            if (address.size()>0) {
                Address location = address.get(0);
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public String getCodeCivilite() {
        return this.codeCivilite;
    }

    public void setCodeCivilite(String codeCivilite) {
        this.codeCivilite = codeCivilite;
    }

    public String getNom() {
        return this.nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return this.prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getRaisonSocial() {
        return this.raisonSocial;
    }

    public void setRaisonSocial(String raisonSocial) {
        this.raisonSocial = raisonSocial;
    }

    public String getComplement() {
        return this.complement;
    }

    public void setComplement(String complement) {
        this.complement = complement;
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

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }






}
