package pulseanddecibels.jp.yamatenki.database.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.internal.SqlUtils;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/**
 * DAO for table FORECAST.
*/
public class ForecastDao extends AbstractDao<Forecast, Long> {

    public static final String TABLENAME = "FORECAST";

    /**
     * Properties of entity Forecast.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Timestamp = new Property(1, Long.class, "timestamp", false, "TIMESTAMP");
        public final static Property PeakTemp = new Property(2, Integer.class, "peakTemp", false, "PEAK_TEMP");
        public final static Property PeakVelocity = new Property(3, Integer.class, "peakVelocity", false, "PEAK_VELOCITY");
        public final static Property PeakDirection = new Property(4, Integer.class, "peakDirection", false, "PEAK_DIRECTION");
        public final static Property BaseTemp = new Property(5, Integer.class, "baseTemp", false, "BASE_TEMP");
        public final static Property BaseVelocity = new Property(6, Integer.class, "baseVelocity", false, "BASE_VELOCITY");
        public final static Property BaseDirection = new Property(7, Integer.class, "baseDirection", false, "BASE_DIRECTION");
        public final static Property Weather = new Property(8, Integer.class, "weather", false, "WEATHER");
        public final static Property Temperature = new Property(9, Integer.class, "temperature", false, "TEMPERATURE");
        public final static Property Precipitation = new Property(10, Float.class, "precipitation", false, "PRECIPITATION");
        public final static Property TemperatureHigh = new Property(11, Integer.class, "temperatureHigh", false, "TEMPERATURE_HIGH");
        public final static Property TemperatureLow = new Property(12, Integer.class, "temperatureLow", false, "TEMPERATURE_LOW");
        public final static Property MountainId = new Property(13, long.class, "mountainId", false, "MOUNTAIN_ID");
    }

    private DaoSession daoSession;

    private Query<Forecast> mountain_ForecastListQuery;

    public ForecastDao(DaoConfig config) {
        super(config);
    }

    public ForecastDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'FORECAST' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'TIMESTAMP' INTEGER," + // 1: timestamp
                "'PEAK_TEMP' INTEGER," + // 2: peakTemp
                "'PEAK_VELOCITY' INTEGER," + // 3: peakVelocity
                "'PEAK_DIRECTION' INTEGER," + // 4: peakDirection
                "'BASE_TEMP' INTEGER," + // 5: baseTemp
                "'BASE_VELOCITY' INTEGER," + // 6: baseVelocity
                "'BASE_DIRECTION' INTEGER," + // 7: baseDirection
                "'WEATHER' INTEGER," + // 8: weather
                "'TEMPERATURE' INTEGER," + // 9: temperature
                "'PRECIPITATION' REAL," + // 10: precipitation
                "'TEMPERATURE_HIGH' INTEGER," + // 11: temperatureHigh
                "'TEMPERATURE_LOW' INTEGER," + // 12: temperatureLow
                "'MOUNTAIN_ID' INTEGER NOT NULL );"); // 13: mountainId
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'FORECAST'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Forecast entity) {
        stmt.clearBindings();

        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }

        Long timestamp = entity.getTimestamp();
        if (timestamp != null) {
            stmt.bindLong(2, timestamp);
        }

        Integer peakTemp = entity.getPeakTemp();
        if (peakTemp != null) {
            stmt.bindLong(3, peakTemp);
        }

        Integer peakVelocity = entity.getPeakVelocity();
        if (peakVelocity != null) {
            stmt.bindLong(4, peakVelocity);
        }

        Integer peakDirection = entity.getPeakDirection();
        if (peakDirection != null) {
            stmt.bindLong(5, peakDirection);
        }

        Integer baseTemp = entity.getBaseTemp();
        if (baseTemp != null) {
            stmt.bindLong(6, baseTemp);
        }

        Integer baseVelocity = entity.getBaseVelocity();
        if (baseVelocity != null) {
            stmt.bindLong(7, baseVelocity);
        }

        Integer baseDirection = entity.getBaseDirection();
        if (baseDirection != null) {
            stmt.bindLong(8, baseDirection);
        }

        Integer weather = entity.getWeather();
        if (weather != null) {
            stmt.bindLong(9, weather);
        }

        Integer temperature = entity.getTemperature();
        if (temperature != null) {
            stmt.bindLong(10, temperature);
        }

        Float precipitation = entity.getPrecipitation();
        if (precipitation != null) {
            stmt.bindDouble(11, precipitation);
        }

        Integer temperatureHigh = entity.getTemperatureHigh();
        if (temperatureHigh != null) {
            stmt.bindLong(12, temperatureHigh);
        }

        Integer temperatureLow = entity.getTemperatureLow();
        if (temperatureLow != null) {
            stmt.bindLong(13, temperatureLow);
        }
        stmt.bindLong(14, entity.getMountainId());
    }

    @Override
    protected void attachEntity(Forecast entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }

    /** @inheritdoc */
    @Override
    public Forecast readEntity(Cursor cursor, int offset) {
        Forecast entity = new Forecast( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // timestamp
            cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2), // peakTemp
            cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // peakVelocity
            cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4), // peakDirection
            cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5), // baseTemp
            cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6), // baseVelocity
            cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7), // baseDirection
            cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8), // weather
            cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9), // temperature
            cursor.isNull(offset + 10) ? null : cursor.getFloat(offset + 10), // precipitation
            cursor.isNull(offset + 11) ? null : cursor.getInt(offset + 11), // temperatureHigh
            cursor.isNull(offset + 12) ? null : cursor.getInt(offset + 12), // temperatureLow
            cursor.getLong(offset + 13) // mountainId
        );
        return entity;
    }

    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Forecast entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setTimestamp(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setPeakTemp(cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2));
        entity.setPeakVelocity(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setPeakDirection(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
        entity.setBaseTemp(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
        entity.setBaseVelocity(cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6));
        entity.setBaseDirection(cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7));
        entity.setWeather(cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8));
        entity.setTemperature(cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9));
        entity.setPrecipitation(cursor.isNull(offset + 10) ? null : cursor.getFloat(offset + 10));
        entity.setTemperatureHigh(cursor.isNull(offset + 11) ? null : cursor.getInt(offset + 11));
        entity.setTemperatureLow(cursor.isNull(offset + 12) ? null : cursor.getInt(offset + 12));
        entity.setMountainId(cursor.getLong(offset + 13));
     }

    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Forecast entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }

    /** @inheritdoc */
    @Override
    public Long getKey(Forecast entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }

    /** Internal query to resolve the "forecastList" to-many relationship of Mountain. */
    public List<Forecast> _queryMountain_ForecastList(long mountainId) {
        synchronized (this) {
            if (mountain_ForecastListQuery == null) {
                QueryBuilder<Forecast> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.MountainId.eq(null));
                mountain_ForecastListQuery = queryBuilder.build();
            }
        }
        Query<Forecast> query = mountain_ForecastListQuery.forCurrentThread();
        query.setParameter(0, mountainId);
        return query.list();
    }

    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getMountainDao().getAllColumns());
            builder.append(" FROM FORECAST T");
            builder.append(" LEFT JOIN MOUNTAIN T0 ON T.'MOUNTAIN_ID'=T0.'_id'");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }

    protected Forecast loadCurrentDeep(Cursor cursor, boolean lock) {
        Forecast entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        Mountain mountain = loadCurrentOther(daoSession.getMountainDao(), cursor, offset);
         if(mountain != null) {
            entity.setMountain(mountain);
        }

        return entity;
    }

    public Forecast loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();

        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);

        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }

    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<Forecast> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<Forecast> list = new ArrayList<Forecast>(count);

        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }

    protected List<Forecast> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }


    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<Forecast> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }

}
