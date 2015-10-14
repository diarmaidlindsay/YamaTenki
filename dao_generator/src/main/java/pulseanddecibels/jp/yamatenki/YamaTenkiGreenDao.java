package pulseanddecibels.jp.yamatenki;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class YamaTenkiGreenDao {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "pulseanddecibels.jp.yamatenki.database.dao");
        Entity mountain = schema.addEntity("Mountain");
        Entity coordinate = schema.addEntity("Coordinate");
        Entity area = schema.addEntity("Area");
        Entity forecast = schema.addEntity("Forecast");

        /**
        Mountain
         **/
        mountain.addIdProperty();
        mountain.addStringProperty("yid");
        mountain.addStringProperty("title");
        mountain.addStringProperty("kana");
        Property areaId = mountain.addLongProperty("areaId").notNull().getProperty();
        //one mountain to one area
        mountain.addToOne(area, areaId);

        /**
         Coordinate
         **/
        coordinate.addIdProperty();
        Property mountainIdC = coordinate.addLongProperty("mountainId").notNull().getProperty();
        //one mountain to one coordinate
        coordinate.addToOne(mountain, mountainIdC);
        coordinate.addFloatProperty("latitude");
        coordinate.addFloatProperty("longitude");

        /**
         Forecast
         **/
        forecast.addIdProperty();
        forecast.addIntProperty("timestamp");
        forecast.addIntProperty("peakTemp");
        forecast.addIntProperty("peakVelocity");
        forecast.addIntProperty("peakDirection");
        forecast.addIntProperty("baseTemp");
        forecast.addIntProperty("baseVelocity");
        forecast.addIntProperty("baseDirection");
        forecast.addIntProperty("weather");
        forecast.addIntProperty("temperature");
        forecast.addIntProperty("precipitation");
        forecast.addIntProperty("temperatureHigh");
        forecast.addIntProperty("temperatureLow");
        Property mountainIdF = forecast.addLongProperty("mountainId").notNull().getProperty(); //which mountain it belongs to
        //One mountain, Many forecasts
        forecast.addToOne(mountain, mountainIdF);
        mountain.addToMany(forecast, mountainIdF);

        /**
         Area
         **/
        area.addIdProperty();
        area.addStringProperty("name");

        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));
        new DaoGenerator().generateAll(schema, "app/src/main/java");
    }
}
