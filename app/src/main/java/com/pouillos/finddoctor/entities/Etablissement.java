package com.pouillos.finddoctor.entities;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Transient;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import com.pouillos.finddoctor.dao.DaoSession;
import com.pouillos.finddoctor.dao.TypeEtablissementDao;
import org.greenrobot.greendao.annotation.NotNull;
import com.pouillos.finddoctor.dao.RegionDao;
import com.pouillos.finddoctor.dao.DepartementDao;
import com.pouillos.finddoctor.dao.EtablissementDao;

@Entity
public class Etablissement implements Comparable<Etablissement> {

    @Id
    private Long id;

    private String numeroFinessET;
    private String raisonSocial;
    private String adresse;
    private String cp;
    private String ville;
    private String telephone;
    private String fax;

    private long departementId;
    @ToOne(joinProperty = "departementId")
    private Departement departement;

   // private Departement departement;

    private long regionId;
    @ToOne(joinProperty = "regionId")
    private Region region;


    private double latitude;
    private double longitude;

    private long typeEtablissementId;
    @ToOne(joinProperty = "typeEtablissementId")
    private TypeEtablissement typeEtablissement;



    @Transient
    private double distance;

    private boolean isSelected;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 2067107635)
    private transient EtablissementDao myDao;




    @Generated(hash = 15652030)
    public Etablissement(Long id, String numeroFinessET, String raisonSocial, String adresse,
            String cp, String ville, String telephone, String fax, long departementId,
            long regionId, double latitude, double longitude, long typeEtablissementId,
            boolean isSelected) {
        this.id = id;
        this.numeroFinessET = numeroFinessET;
        this.raisonSocial = raisonSocial;
        this.adresse = adresse;
        this.cp = cp;
        this.ville = ville;
        this.telephone = telephone;
        this.fax = fax;
        this.departementId = departementId;
        this.regionId = regionId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.typeEtablissementId = typeEtablissementId;
        this.isSelected = isSelected;
    }



    @Generated(hash = 1043456960)
    public Etablissement() {
    }

    @Generated(hash = 531663746)
    private transient Long departement__resolvedKey;

    @Generated(hash = 2019302108)
    private transient Long region__resolvedKey;

    @Generated(hash = 1797044420)
    private transient Long typeEtablissement__resolvedKey;




    @Override
    public String toString() {
        return raisonSocial+" - "+ville;
    }



    @Override
    public int compareTo(Etablissement o) {
        return Double.compare(this.distance,o.distance);
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




    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
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



    public long getTypeEtablissementId() {
        return this.typeEtablissementId;
    }



    public void setTypeEtablissementId(long typeEtablissementId) {
        this.typeEtablissementId = typeEtablissementId;
    }



    public boolean getIsSelected() {
        return this.isSelected;
    }



    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
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



    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1042617487)
    public TypeEtablissement getTypeEtablissement() {
        long __key = this.typeEtablissementId;
        if (typeEtablissement__resolvedKey == null
                || !typeEtablissement__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TypeEtablissementDao targetDao = daoSession.getTypeEtablissementDao();
            TypeEtablissement typeEtablissementNew = targetDao.load(__key);
            synchronized (this) {
                typeEtablissement = typeEtablissementNew;
                typeEtablissement__resolvedKey = __key;
            }
        }
        return typeEtablissement;
    }



    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1158181216)
    public void setTypeEtablissement(@NotNull TypeEtablissement typeEtablissement) {
        if (typeEtablissement == null) {
            throw new DaoException(
                    "To-one property 'typeEtablissementId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.typeEtablissement = typeEtablissement;
            typeEtablissementId = typeEtablissement.getId();
            typeEtablissement__resolvedKey = typeEtablissementId;
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
    @Generated(hash = 138827958)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getEtablissementDao() : null;
    }


}
