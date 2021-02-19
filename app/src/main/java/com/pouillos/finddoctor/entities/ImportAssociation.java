package com.pouillos.finddoctor.entities;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

@Entity
public class ImportAssociation {

    @Id
    private Long id;

    private int nbContact;
    private int compteur;
    @Generated(hash = 1923923421)
    public ImportAssociation(Long id, int nbContact, int compteur) {
        this.id = id;
        this.nbContact = nbContact;
        this.compteur = compteur;
    }
    @Generated(hash = 1940959280)
    public ImportAssociation() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public int getNbContact() {
        return this.nbContact;
    }
    public void setNbContact(int nbContact) {
        this.nbContact = nbContact;
    }
    public int getCompteur() {
        return this.compteur;
    }
    public void setCompteur(int compteur) {
        this.compteur = compteur;
    }
    
}
