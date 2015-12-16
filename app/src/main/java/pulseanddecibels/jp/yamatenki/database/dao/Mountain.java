package pulseanddecibels.jp.yamatenki.database.dao;

import java.util.List;

import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table MOUNTAIN.
 */
public class Mountain {

    private Long id;
    private String yid;
    private String title;
    private String titleExt;
    private String titleEnglish;
    private String kana;
    private long prefectureId;
    private long areaId;
    private Integer height;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient MountainDao myDao;

    private Area area;
    private Long area__resolvedKey;

    private Prefecture prefecture;
    private Long prefecture__resolvedKey;

    private List<Pressure> pressureList;
    private List<Forecast> forecastList;
    private List<MyMemo> myMemoList;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Mountain() {
    }

    public Mountain(Long id) {
        this.id = id;
    }

    public Mountain(Long id, String yid, String title, String titleExt, String titleEnglish, String kana, long prefectureId, long areaId, Integer height) {
        this.id = id;
        this.yid = yid;
        this.title = title;
        this.titleExt = titleExt;
        this.titleEnglish = titleEnglish;
        this.kana = kana;
        this.prefectureId = prefectureId;
        this.areaId = areaId;
        this.height = height;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMountainDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getYid() {
        return yid;
    }

    public void setYid(String yid) {
        this.yid = yid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleExt() {
        return titleExt;
    }

    public void setTitleExt(String titleExt) {
        this.titleExt = titleExt;
    }

    public String getTitleEnglish() {
        return titleEnglish;
    }

    public void setTitleEnglish(String titleEnglish) {
        this.titleEnglish = titleEnglish;
    }

    public String getKana() {
        return kana;
    }

    public void setKana(String kana) {
        this.kana = kana;
    }

    public long getPrefectureId() {
        return prefectureId;
    }

    public void setPrefectureId(long prefectureId) {
        this.prefectureId = prefectureId;
    }

    public long getAreaId() {
        return areaId;
    }

    public void setAreaId(long areaId) {
        this.areaId = areaId;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    /** To-one relationship, resolved on first access. */
    public Area getArea() {
        long __key = this.areaId;
        if (area__resolvedKey == null || !area__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            AreaDao targetDao = daoSession.getAreaDao();
            Area areaNew = targetDao.load(__key);
            synchronized (this) {
                area = areaNew;
            	area__resolvedKey = __key;
            }
        }
        return area;
    }

    public void setArea(Area area) {
        if (area == null) {
            throw new DaoException("To-one property 'areaId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.area = area;
            areaId = area.getId();
            area__resolvedKey = areaId;
        }
    }

    /** To-one relationship, resolved on first access. */
    public Prefecture getPrefecture() {
        long __key = this.prefectureId;
        if (prefecture__resolvedKey == null || !prefecture__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PrefectureDao targetDao = daoSession.getPrefectureDao();
            Prefecture prefectureNew = targetDao.load(__key);
            synchronized (this) {
                prefecture = prefectureNew;
            	prefecture__resolvedKey = __key;
            }
        }
        return prefecture;
    }

    public void setPrefecture(Prefecture prefecture) {
        if (prefecture == null) {
            throw new DaoException("To-one property 'prefectureId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.prefecture = prefecture;
            prefectureId = prefecture.getId();
            prefecture__resolvedKey = prefectureId;
        }
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<Pressure> getPressureList() {
        if (pressureList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PressureDao targetDao = daoSession.getPressureDao();
            List<Pressure> pressureListNew = targetDao._queryMountain_PressureList(id);
            synchronized (this) {
                if(pressureList == null) {
                    pressureList = pressureListNew;
                }
            }
        }
        return pressureList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetPressureList() {
        pressureList = null;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<Forecast> getForecastList() {
        if (forecastList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ForecastDao targetDao = daoSession.getForecastDao();
            List<Forecast> forecastListNew = targetDao._queryMountain_ForecastList(id);
            synchronized (this) {
                if(forecastList == null) {
                    forecastList = forecastListNew;
                }
            }
        }
        return forecastList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetForecastList() {
        forecastList = null;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<MyMemo> getMyMemoList() {
        if (myMemoList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MyMemoDao targetDao = daoSession.getMyMemoDao();
            List<MyMemo> myMemoListNew = targetDao._queryMountain_MyMemoList(id);
            synchronized (this) {
                if(myMemoList == null) {
                    myMemoList = myMemoListNew;
                }
            }
        }
        return myMemoList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetMyMemoList() {
        myMemoList = null;
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

    // KEEP METHODS - put your custom methods here
    public Integer getStatus() {
        StatusDao statusDao = daoSession.getStatusDao();
        Status status = statusDao.queryBuilder().where(StatusDao.Properties.MountainId.eq(getId())).unique();
        return status.getStatus();
    }

    public ETag getETag() {
        ETagDao eTagDao = daoSession.getETagDao();
        return eTagDao.queryBuilder().where(ETagDao.Properties.MountainId.eq(getId())).unique();
    }
    // KEEP METHODS END

}
