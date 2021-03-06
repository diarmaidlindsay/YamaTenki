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
 * DAO for table WIND_AND_TEMPERATURE.
*/
public class WindAndTemperatureDao extends AbstractDao<WindAndTemperature, Long> {

    public static final String TABLENAME = "WIND_AND_TEMPERATURE";

    /**
     * Properties of entity WindAndTemperature.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Height = new Property(1, Integer.class, "height", false, "HEIGHT");
        public final static Property Temperature = new Property(2, Double.class, "temperature", false, "TEMPERATURE");
        public final static Property WindVelocity = new Property(3, Double.class, "windVelocity", false, "WIND_VELOCITY");
        public final static Property WindDirection = new Property(4, Double.class, "windDirection", false, "WIND_DIRECTION");
        public final static Property ForecastId = new Property(5, long.class, "forecastId", false, "FORECAST_ID");
    }

    private DaoSession daoSession;

    private Query<WindAndTemperature> forecast_WindAndTemperatureListQuery;

    public WindAndTemperatureDao(DaoConfig config) {
        super(config);
    }
    
    public WindAndTemperatureDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'WIND_AND_TEMPERATURE' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'HEIGHT' INTEGER," + // 1: height
                "'TEMPERATURE' REAL," + // 2: temperature
                "'WIND_VELOCITY' REAL," + // 3: windVelocity
                "'WIND_DIRECTION' REAL," + // 4: windDirection
                "'FORECAST_ID' INTEGER NOT NULL );"); // 5: forecastId
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'WIND_AND_TEMPERATURE'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, WindAndTemperature entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Integer height = entity.getHeight();
        if (height != null) {
            stmt.bindLong(2, height);
        }
 
        Double temperature = entity.getTemperature();
        if (temperature != null) {
            stmt.bindDouble(3, temperature);
        }
 
        Double windVelocity = entity.getWindVelocity();
        if (windVelocity != null) {
            stmt.bindDouble(4, windVelocity);
        }
 
        Double windDirection = entity.getWindDirection();
        if (windDirection != null) {
            stmt.bindDouble(5, windDirection);
        }
        stmt.bindLong(6, entity.getForecastId());
    }

    @Override
    protected void attachEntity(WindAndTemperature entity) {
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
    public WindAndTemperature readEntity(Cursor cursor, int offset) {
        WindAndTemperature entity = new WindAndTemperature( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1), // height
            cursor.isNull(offset + 2) ? null : cursor.getDouble(offset + 2), // temperature
            cursor.isNull(offset + 3) ? null : cursor.getDouble(offset + 3), // windVelocity
            cursor.isNull(offset + 4) ? null : cursor.getDouble(offset + 4), // windDirection
            cursor.getLong(offset + 5) // forecastId
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, WindAndTemperature entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setHeight(cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1));
        entity.setTemperature(cursor.isNull(offset + 2) ? null : cursor.getDouble(offset + 2));
        entity.setWindVelocity(cursor.isNull(offset + 3) ? null : cursor.getDouble(offset + 3));
        entity.setWindDirection(cursor.isNull(offset + 4) ? null : cursor.getDouble(offset + 4));
        entity.setForecastId(cursor.getLong(offset + 5));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(WindAndTemperature entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(WindAndTemperature entity) {
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
    
    /** Internal query to resolve the "windAndTemperatureList" to-many relationship of Forecast. */
    public List<WindAndTemperature> _queryForecast_WindAndTemperatureList(long forecastId) {
        synchronized (this) {
            if (forecast_WindAndTemperatureListQuery == null) {
                QueryBuilder<WindAndTemperature> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.ForecastId.eq(null));
                forecast_WindAndTemperatureListQuery = queryBuilder.build();
            }
        }
        Query<WindAndTemperature> query = forecast_WindAndTemperatureListQuery.forCurrentThread();
        query.setParameter(0, forecastId);
        return query.list();
    }

    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getForecastDao().getAllColumns());
            builder.append(" FROM WIND_AND_TEMPERATURE T");
            builder.append(" LEFT JOIN FORECAST T0 ON T.'FORECAST_ID'=T0.'_id'");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected WindAndTemperature loadCurrentDeep(Cursor cursor, boolean lock) {
        WindAndTemperature entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        Forecast forecast = loadCurrentOther(daoSession.getForecastDao(), cursor, offset);
         if(forecast != null) {
            entity.setForecast(forecast);
        }

        return entity;    
    }

    public WindAndTemperature loadDeep(Long key) {
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
    public List<WindAndTemperature> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<WindAndTemperature> list = new ArrayList<WindAndTemperature>(count);
        
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
    
    protected List<WindAndTemperature> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<WindAndTemperature> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
