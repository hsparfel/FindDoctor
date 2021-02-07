package com.pouillos.finddoctor.entities;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToOne;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import com.pouillos.finddoctor.dao.DaoSession;
import com.pouillos.finddoctor.dao.RegionDao;
import com.pouillos.finddoctor.dao.DepartementDao;
import com.pouillos.finddoctor.dao.SavoirFaireDao;
import com.pouillos.finddoctor.dao.ProfessionDao;
import com.pouillos.finddoctor.dao.ContactDao;

@Entity
public class Contact implements Comparable<Contact>{

    @Id
    private Long id;

    @NotNull
    private String idPP;

    private String codeCivilite;
    private String nom;
    private String prenom;


    private long professionId;
    @ToOne(joinProperty = "professionId")
    private Profession profession;


    private long savoirFaireId;
    @ToOne(joinProperty = "savoirFaireId")
    private SavoirFaire savoirFaire;

    private String raisonSocial;
    private String complement;
    private String adresse;
    private String cp;
    private String ville;
    private String telephone;
    private String fax;
    private String email;


    private long departementId;
    @ToOne(joinProperty = "departementId")
    private Departement departement;


    private long regionId;
    @ToOne(joinProperty = "regionId")
    private Region region;

    private double latitude;
    private double longitude;

    private boolean isSelected;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 2046468181)
    private transient ContactDao myDao;








    @Generated(hash = 1103072865)
    public Contact(Long id, @NotNull String idPP, String codeCivilite, String nom,
            String prenom, long professionId, long savoirFaireId, String raisonSocial,
            String complement, String adresse, String cp, String ville, String telephone,
            String fax, String email, long departementId, long regionId, double latitude,
            double longitude, boolean isSelected) {
        this.id = id;
        this.idPP = idPP;
        this.codeCivilite = codeCivilite;
        this.nom = nom;
        this.prenom = prenom;
        this.professionId = professionId;
        this.savoirFaireId = savoirFaireId;
        this.raisonSocial = raisonSocial;
        this.complement = complement;
        this.adresse = adresse;
        this.cp = cp;
        this.ville = ville;
        this.telephone = telephone;
        this.fax = fax;
        this.email = email;
        this.departementId = departementId;
        this.regionId = regionId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isSelected = isSelected;
    }

    @Generated(hash = 672515148)
    public Contact() {
    }

    @Generated(hash = 2049070167)
    private transient Long profession__resolvedKey;

    @Generated(hash = 680256263)
    private transient Long savoirFaire__resolvedKey;

    @Generated(hash = 531663746)
    private transient Long departement__resolvedKey;

    @Generated(hash = 2019302108)
    private transient Long region__resolvedKey;








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
        if (savoirFaire != null) {
            affichage += " - " + savoirFaire;
        } else {
            if (profession != null) {
                affichage +=  " - " + profession;
            }
        }

        return affichage;
    }

    @Override
    public int compareTo(Contact o) {
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

    public long getProfessionId() {
        return this.professionId;
    }

    public void setProfessionId(long professionId) {
        this.professionId = professionId;
    }

    public long getSavoirFaireId() {
        return this.savoirFaireId;
    }

    public void setSavoirFaireId(long savoirFaireId) {
        this.savoirFaireId = savoirFaireId;
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

    public long getDepartementId() {
        return this.departementId;
    }

    public void setDepartementId(long departementId) {
        this.departementId = departementId;
    }

    public long getRegionId() {
        return this.regionId;
    }

    public void setRegionId(long regionId) {
        this.regionId = regionId;
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

    public boolean getIsSelected() {
        return this.isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1918544418)
    public Profession getProfession() {
        long __key = this.professionId;
        if (profession__resolvedKey == null || !profession__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ProfessionDao targetDao = daoSession.getProfessionDao();
            Profession professionNew = targetDao.load(__key);
            synchronized (this) {
                profession = professionNew;
                profession__resolvedKey = __key;
            }
        }
        return profession;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 372447209)
    public void setProfession(@NotNull Profession profession) {
        if (profession == null) {
            throw new DaoException(
                    "To-one property 'professionId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.profession = profession;
            professionId = profession.getId();
            profession__resolvedKey = professionId;
        }
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 920177871)
    public SavoirFaire getSavoirFaire() {
        long __key = this.savoirFaireId;
        if (savoirFaire__resolvedKey == null || !savoirFaire__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            SavoirFaireDao targetDao = daoSession.getSavoirFaireDao();
            SavoirFaire savoirFaireNew = targetDao.load(__key);
            synchronized (this) {
                savoirFaire = savoirFaireNew;
                savoirFaire__resolvedKey = __key;
            }
        }
        return savoirFaire;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1969075350)
    public void setSavoirFaire(@NotNull SavoirFaire savoirFaire) {
        if (savoirFaire == null) {
            throw new DaoException(
                    "To-one property 'savoirFaireId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.savoirFaire = savoirFaire;
            savoirFaireId = savoirFaire.getId();
            savoirFaire__resolvedKey = savoirFaireId;
        }
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1052582015)
    public Departement getDepartement() {
        long __key = this.departementId;
        if (departement__resolvedKey == null || !departement__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DepartementDao targetDao = daoSession.getDepartementDao();
            Departement departementNew = targetDao.load(__key);
            synchronized (this) {
                departement = departementNew;
                departement__resolvedKey = __key;
            }
        }
        return departement;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 695126866)
    public void setDepartement(@NotNull Departement departement) {
        if (departement == null) {
            throw new DaoException(
                    "To-one property 'departementId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.departement = departement;
            departementId = departement.getId();
            departement__resolvedKey = departementId;
        }
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1627127032)
    public Region getRegion() {
        long __key = this.regionId;
        if (region__resolvedKey == null || !region__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            RegionDao targetDao = daoSession.getRegionDao();
            Region regionNew = targetDao.load(__key);
            synchronized (this) {
                region = regionNew;
                region__resolvedKey = __key;
            }
        }
        return region;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 667718594)
    public void setRegion(@NotNull Region region) {
        if (region == null) {
            throw new DaoException(
                    "To-one property 'regionId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.region = region;
            regionId = region.getId();
            region__resolvedKey = regionId;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 2088270543)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getContactDao() : null;
    }






}
