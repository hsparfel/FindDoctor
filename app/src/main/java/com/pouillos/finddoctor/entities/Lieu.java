package com.pouillos.finddoctor.entities;

import com.pouillos.finddoctor.dao.DaoSession;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.Date;
import com.pouillos.finddoctor.dao.LieuEnregistreDao;
import com.pouillos.finddoctor.dao.LieuDao;

@Entity
public class Lieu {

    @Id
    private Long id;

    @NotNull
    private long lieuEnregistreId;

    @ToOne(joinProperty = "lieuEnregistreId")
    private LieuEnregistre lieuEnregistre;

    @NotNull
    private Date date;

    @NotNull
    private String dateString;

    private String detail;

    private double latitude;

    private double longitude;

    private String adresse;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 94170250)
    private transient LieuDao myDao;

    @Generated(hash = 1846571788)
    public Lieu(Long id, long lieuEnregistreId, @NotNull Date date,
            @NotNull String dateString, String detail, double latitude,
            double longitude, String adresse) {
        this.id = id;
        this.lieuEnregistreId = lieuEnregistreId;
        this.date = date;
        this.dateString = dateString;
        this.detail = detail;
        this.latitude = latitude;
        this.longitude = longitude;
        this.adresse = adresse;
    }

    @Generated(hash = 116774798)
    public Lieu() {
    }

    @Generated(hash = 1227745270)
    private transient Long lieuEnregistre__resolvedKey;

    @Override
    public String toString() {
        return "a faire ! " + detail;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getLieuEnregistreId() {
        return this.lieuEnregistreId;
    }

    public void setLieuEnregistreId(long lieuEnregistreId) {
        this.lieuEnregistreId = lieuEnregistreId;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDateString() {
        return this.dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public String getDetail() {
        return this.detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
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

    public String getAdresse() {
        return this.adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1883713647)
    public LieuEnregistre getLieuEnregistre() {
        long __key = this.lieuEnregistreId;
        if (lieuEnregistre__resolvedKey == null
                || !lieuEnregistre__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            LieuEnregistreDao targetDao = daoSession.getLieuEnregistreDao();
            LieuEnregistre lieuEnregistreNew = targetDao.load(__key);
            synchronized (this) {
                lieuEnregistre = lieuEnregistreNew;
                lieuEnregistre__resolvedKey = __key;
            }
        }
        return lieuEnregistre;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1208436051)
    public void setLieuEnregistre(@NotNull LieuEnregistre lieuEnregistre) {
        if (lieuEnregistre == null) {
            throw new DaoException(
                    "To-one property 'lieuEnregistreId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.lieuEnregistre = lieuEnregistre;
            lieuEnregistreId = lieuEnregistre.getId();
            lieuEnregistre__resolvedKey = lieuEnregistreId;
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
    @Generated(hash = 1795289354)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getLieuDao() : null;
    }
}
