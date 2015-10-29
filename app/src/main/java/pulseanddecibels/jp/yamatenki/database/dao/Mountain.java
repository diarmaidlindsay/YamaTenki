package pulseanddecibels.jp.yamatenki.database.dao;

import android.support.annotation.Nullable;

import org.joda.time.DateTime;

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
    private long coordinateId;
    private long prefectureId;
    private long areaId;
    private Integer height;
    private Integer currentMountainIndex;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient MountainDao myDao;

    private Area area;
    private Long area__resolvedKey;

    private Prefecture prefecture;
    private Long prefecture__resolvedKey;

    private Coordinate coordinate;
    private Long coordinate__resolvedKey;

    private List<Forecast> forecastList;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Mountain() {
    }

    public Mountain(Long id) {
        this.id = id;
    }

    public Mountain(Long id, String yid, String title, String titleExt, String titleEnglish, String kana, long coordinateId, long prefectureId, long areaId, Integer height, Integer currentMountainIndex) {
        this.id = id;
        this.yid = yid;
        this.title = title;
        this.titleExt = titleExt;
        this.titleEnglish = titleEnglish;
        this.kana = kana;
        this.coordinateId = coordinateId;
        this.prefectureId = prefectureId;
        this.areaId = areaId;
        this.height = height;
        this.currentMountainIndex = currentMountainIndex;
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

    public long getCoordinateId() {
        return coordinateId;
    }

    public void setCoordinateId(long coordinateId) {
        this.coordinateId = coordinateId;
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

    public Integer getCurrentMountainIndex() {
        return currentMountainIndex;
    }

    public void setCurrentMountainIndex(Integer currentMountainIndex) {
        this.currentMountainIndex = currentMountainIndex;
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

    /** To-one relationship, resolved on first access. */
    public Coordinate getCoordinate() {
        long __key = this.coordinateId;
        if (coordinate__resolvedKey == null || !coordinate__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            CoordinateDao targetDao = daoSession.getCoordinateDao();
            Coordinate coordinateNew = targetDao.load(__key);
            synchronized (this) {
                coordinate = coordinateNew;
            	coordinate__resolvedKey = __key;
            }
        }
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        if (coordinate == null) {
            throw new DaoException("To-one property 'coordinateId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.coordinate = coordinate;
            coordinateId = coordinate.getId();
            coordinate__resolvedKey = coordinateId;
        }
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

    /**
     * Forecasts are given in 3 hour blocks, so given the time now
     * find the matching 3 hour block and return the entry from the database
     * which matches the milliseconds range.
     */
    @Nullable
    public Forecast getLatestForecast() {
        DateTime now = new DateTime();
        int hourNow = now.getHourOfDay(); //0 ~ 23
        int minHour = hourNow - (hourNow % 3); //0, 3, 6, 9 etc
        int maxHour = minHour + 2; //2, 5, 8, 11 etc

        long minMillis = now.withTime(minHour, 0, 0, 0).getMillis();
        long maxMillis = now.withTime(maxHour, 59, 59, 999).getMillis();

        ForecastDao targetDao = daoSession.getForecastDao();
        List<Forecast> forecastList =
                targetDao.queryBuilder().where(ForecastDao.Properties.Timestamp.between(minMillis, maxMillis)).list();
        if (forecastList.size() == 1) {
            return forecastList.get(0);
        }
        return null;
    }

    public Forecast getMockForecast(long mountainId) {
        Long id = 1L;
        Long timestamp = new DateTime().getMillis();
        Integer peakTemp = 0;
        Integer peakVelocity = 0;
        Integer peakDirection = 0;
        Integer baseTemp = 0;
        Integer baseVelocity = 0;
        Integer baseDirection = 0;
        Integer weather = 0;
        Integer temperature = 0;
        Float precipitation = 0.0F;
        Integer temperatureHigh = 0;
        Integer temperatureLow = 0;

        return new Forecast(id, timestamp, peakTemp, peakVelocity, peakDirection, baseTemp, baseVelocity, baseDirection, weather, temperature, precipitation, temperatureHigh, temperatureLow, mountainId);
    }
    // KEEP METHODS END

}
