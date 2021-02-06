package com.pouillos.finddoctor.entities;



import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToOne;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import com.pouillos.finddoctor.dao.DaoSession;
import com.pouillos.finddoctor.dao.RegionDao;
import com.pouillos.finddoctor.dao.DepartementDao;


@Entity
public class Departement implements Comparable<Departement> {

    @Id
    private Long id;

    @NotNull
    private String numero;
    @NotNull
    private String nom;

    @NotNull
    private long regionId;
    @ToOne(joinProperty = "regionId")
    private Region region;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1230795233)
    private transient DepartementDao myDao;




    @Generated(hash = 375594626)
    public Departement(Long id, @NotNull String numero, @NotNull String nom,
            long regionId) {
        this.id = id;
        this.numero = numero;
        this.nom = nom;
        this.regionId = regionId;
    }

    @Generated(hash = 165952554)
    public Departement() {
    }

    @Generated(hash = 2019302108)
    private transient Long region__resolvedKey;




    @Override
    public String toString() {
        return numero+" - "+nom;
    }

    @Override
    public int compareTo(Departement o) {
        return this.numero.compareTo(o.numero);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return this.numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getNom() {
        return this.nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public long getRegionId() {
        return this.regionId;
    }

    public void setRegionId(long regionId) {
        this.regionId = regionId;
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
    @Generated(hash = 1177682737)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getDepartementDao() : null;
    }





}
