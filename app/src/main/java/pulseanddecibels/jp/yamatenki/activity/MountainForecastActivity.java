package pulseanddecibels.jp.yamatenki.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pulseanddecibels.jp.yamatenki.R;
import pulseanddecibels.jp.yamatenki.model.ForecastArrayElement;
import pulseanddecibels.jp.yamatenki.model.MountainArrayElement;
import pulseanddecibels.jp.yamatenki.model.MountainForecastJSON;
import pulseanddecibels.jp.yamatenki.model.WindAndTemperatureElement;
import pulseanddecibels.jp.yamatenki.utils.DateUtils;
import pulseanddecibels.jp.yamatenki.utils.JSONDownloader;
import pulseanddecibels.jp.yamatenki.utils.JSONParser;
import pulseanddecibels.jp.yamatenki.utils.Utils;

/**
 * Created by Diarmaid Lindsay on 2015/09/29.
 * Copyright Pulse and Decibels 2015
 */
public class MountainForecastActivity extends Activity {
    final SparseIntArray DIFFICULTY_SMALL_IMAGES = new SparseIntArray() {
        {
            append(1, R.drawable.a_difficulty_small);
            append(2, R.drawable.b_difficulty_small);
            append(3, R.drawable.c_difficulty_small);
        }
    };
    final SparseIntArray DIFFICULTY_BIG_IMAGES = new SparseIntArray() {
        {
            append(1, R.drawable.a_difficulty_big);
            append(2, R.drawable.a_difficulty_big);
            append(3, R.drawable.a_difficulty_big);
        }
    };
    final SparseIntArray WEATHER_IMAGES = new SparseIntArray() {
        {
            append(1, R.drawable.a_difficulty_small);
            append(2, R.drawable.b_difficulty_small);
            append(3, R.drawable.c_difficulty_small);
            append(4, R.drawable.a_difficulty_small);
            append(5, R.drawable.b_difficulty_small);
        }
    };
    TextView title;
    ImageView currentDifficultyImage;
    ScrollView mountainForecastScrollView;
    long mountainId;
    List<ForecastScrollViewElement> scrollViewElements = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        Bundle arguments = getIntent().getExtras();
        mountainId = arguments.getLong("mountainId"); //used to retrieve correct JSON file

        title = (TextView) findViewById(R.id.text_forecast_header);
        title.setTypeface(Utils.getTitleTypeFace(this));
        currentDifficultyImage = (ImageView) findViewById(R.id.mountain_forecast_current_difficulty);

        initialiseWidgets();
        populateWidgets(JSONParser.parseMountainForecastFromFile(
                JSONDownloader.getMockMountainForecast(this, mountainId)));
    }

    private void initialiseWidgets() {
        mountainForecastScrollView = (ScrollView) findViewById(R.id.scroll_forecasts);
        LinearLayout todayForecast = (LinearLayout) mountainForecastScrollView.findViewById(R.id.today_forecast);
        LinearLayout tomorrowForecast = (LinearLayout) mountainForecastScrollView.findViewById(R.id.tomorrow_forecast);
        //add in correct order because index matters for looking up date
        scrollViewElements.add(new ForecastScrollViewElement(todayForecast));
        scrollViewElements.add(new ForecastScrollViewElement(tomorrowForecast));
        /*
        +2Forecast
        +3Forecast
        +4Forecast
        +5Forecast
        +6Forecast
         */
    }

    private void populateWidgets(MountainForecastJSON forecasts) {
        if (forecasts == null) {
            return;
        }
        MountainArrayElement mountainInfo = forecasts.getMountainArrayElement();
        //set forecast page's title
        title.setText(mountainInfo.getTitle());
        //set forecast page's big difficulty image
        currentDifficultyImage.setImageResource(DIFFICULTY_BIG_IMAGES.get(mountainInfo.getCurrentMountainIndex()));

        Map<String, ForecastArrayElement> shortForecast = forecasts.getForecasts(); // short term (detailed)
        Map<String, ForecastArrayElement> longForecast = forecasts.getForecastsDaily(); // long term (rough)


        for (int i = 0; i < scrollViewElements.size(); i++) {
            ForecastScrollViewElement scrollViewElement = scrollViewElements.get(i);
            //set ScrollViewElement header
            scrollViewElement.getHeader().setText(DateUtils.getFormattedHeader(i));
            //set ScrollViewElement peak height and pressure
            String heightPressureTemplate = "高度 %s m付近（%shPa )";
            String peakHeightPressure = String.format(heightPressureTemplate, forecasts.getPeakHeight(), 0);
            scrollViewElement.getPeakHeightPressure().setText(peakHeightPressure);
            //set ScrollViewElement base height and pressure
            String baseHeightPressure = String.format(heightPressureTemplate, forecasts.getBaseHeight(), 0);
            scrollViewElement.getBaseHeightPressure().setText(baseHeightPressure);
            //set ScrollViewElement reference city
            scrollViewElement.getReferenceCity().setText(String.format("%sの気象情報", forecasts.getReferenceCity()));

            for (ForecastColumn forecastColumn : scrollViewElement.getColumns()) {
                //find if there is a matching forecast available from json. If not, whole column is grey.
                String key = DateUtils.timeToMapKey(i, forecastColumn.getColumnId());
                ForecastArrayElement forecastArrayElement = shortForecast.get(key);
                forecastColumn.getTime().setText(forecastColumn.getColumnId());
                if (forecastArrayElement != null) {
                    List<WindAndTemperatureElement> windAndTemperatureElements =
                            forecastArrayElement.getWindAndTemperatures();
                    forecastColumn.getDifficulty().setImageResource(DIFFICULTY_SMALL_IMAGES.get(forecastArrayElement.getWeather()));
                    forecastColumn.getPeakTemperature().setText(windAndTemperatureElements.get(0).getTemperature());
                    forecastColumn.getPeakWind().setText(windAndTemperatureElements.get(0).getWindVelocity());
                    forecastColumn.getBaseTemperature().setText(windAndTemperatureElements.get(1).getTemperature());
                    forecastColumn.getBaseWind().setText(windAndTemperatureElements.get(1).getWindVelocity());
                    forecastColumn.getCityWeather().setImageResource(WEATHER_IMAGES.get(forecastArrayElement.getWeather()));
                    forecastColumn.getCityTemperature().setText(forecastArrayElement.getTemperature());
                    forecastColumn.getCityRainChance().setText(forecastArrayElement.getPrecipitation());
                } else {
                    //set grey background and blank
                    forecastColumn.getDifficulty().setBackgroundColor(Color.GRAY);
                    forecastColumn.getPeakTemperature().setBackgroundColor(Color.GRAY);
                    forecastColumn.getPeakWind().setBackgroundColor(Color.GRAY);
                    forecastColumn.getBaseTemperature().setBackgroundColor(Color.GRAY);
                    forecastColumn.getBaseWind().setBackgroundColor(Color.GRAY);
                    forecastColumn.getCityWeather().setBackgroundColor(Color.GRAY);
                    forecastColumn.getCityTemperature().setBackgroundColor(Color.GRAY);
                    forecastColumn.getCityRainChance().setBackgroundColor(Color.GRAY);
                }
            }
        }
    }

    // For now this is used for "Today" and "Tomorrow", ie, short term forecasts
    private class ForecastScrollViewElement {
        private final String[] TIME_INCREMENTS = {"00", "03", "06", "09", "12"}; // column headers
        private TextView header;
        private List<ForecastColumn> columns = new ArrayList<>();
        private TextView peakHeightPressure;
        private TextView baseHeightPressure;
        private TextView referenceCity;

        public ForecastScrollViewElement(LinearLayout forecast) {
            header = (TextView) forecast.findViewById(R.id.forecast_header);
            TableLayout forecastTable = (TableLayout) forecast.findViewById(R.id.forecast_table);

            TableRow peakWindAM = (TableRow) forecastTable.findViewById(R.id.forecast_peak_row_wind_AM);
            TableRow peakTempAM = (TableRow) forecastTable.findViewById(R.id.forecast_peak_row_temperature_AM);
            TableRow peakHeightAM = (TableRow) forecastTable.findViewById(R.id.forecast_peak_row_height_AM);
            TableRow baseWindAM = (TableRow) forecastTable.findViewById(R.id.forecast_base_row_wind_AM);
            TableRow baseTempAM = (TableRow) forecastTable.findViewById(R.id.forecast_base_row_temperature_AM);
            TableRow baseHeightAM = (TableRow) forecastTable.findViewById(R.id.forecast_base_row_height_AM);

            for (String time : TIME_INCREMENTS) {
                columns.add(new ForecastColumn(forecastTable, peakWindAM, peakTempAM, baseWindAM, baseTempAM, time));
            }
            peakHeightPressure = (TextView) peakHeightAM.findViewById(R.id.forecast_height_pressure);
            baseHeightPressure = (TextView) baseHeightAM.findViewById(R.id.forecast_height_pressure);
            referenceCity = (TextView) forecast.findViewById(R.id.forecast_closest_city_weather_info_AM);
        }

        public TextView getHeader() {
            return header;
        }

        public List<ForecastColumn> getColumns() {
            return columns;
        }

        public TextView getPeakHeightPressure() {
            return peakHeightPressure;
        }

        public TextView getBaseHeightPressure() {
            return baseHeightPressure;
        }

        public TextView getReferenceCity() {
            return referenceCity;
        }
    }

    private class ForecastColumn {
        private String columnId;
        private TextView time;
        private ImageView difficulty;
        private TextView peakTemperature;
        private TextView peakWind;
        private TextView baseTemperature;
        private TextView baseWind;
        private ImageView cityWeather;
        private TextView cityTemperature;
        private TextView cityRainChance;

        public ForecastColumn(TableLayout forecastTable, TableRow peakWindRow, TableRow peakTempRow, TableRow baseWindRow, TableRow baseTempRow, String columnId) {
            this.columnId = columnId;
            time = (TextView) forecastTable.findViewById(getResources().getIdentifier("forecast_time_" + columnId, "id", getPackageName()));
            difficulty = (ImageView) forecastTable.findViewById(getResources().getIdentifier("forecast_difficulty_" + columnId, "id", getPackageName()));
            peakTemperature = (TextView) peakTempRow.findViewById(getResources().getIdentifier("forecast_temp_" + columnId, "id", getPackageName()));
            peakWind = (TextView) peakWindRow.findViewById(getResources().getIdentifier("forecast_wind_" + columnId, "id", getPackageName()));
            baseTemperature = (TextView) baseTempRow.findViewById(getResources().getIdentifier("forecast_temp_" + columnId, "id", getPackageName()));
            baseWind = (TextView) baseWindRow.findViewById(getResources().getIdentifier("forecast_wind_" + columnId, "id", getPackageName()));
            cityWeather = (ImageView) forecastTable.findViewById(getResources().getIdentifier("forecast_picture_weather_" + columnId, "id", getPackageName()));
            cityTemperature = (TextView) forecastTable.findViewById(getResources().getIdentifier("forecast_atmospheric_temp_" + columnId, "id", getPackageName()));
            cityRainChance = (TextView) forecastTable.findViewById(getResources().getIdentifier("forecast_rain_chance_" + columnId, "id", getPackageName()));
        }

        public String getColumnId() {
            return columnId;
        }

        public TextView getTime() {
            return time;
        }

        public ImageView getDifficulty() {
            return difficulty;
        }

        public TextView getPeakTemperature() {
            return peakTemperature;
        }

        public TextView getPeakWind() {
            return peakWind;
        }

        public TextView getBaseTemperature() {
            return baseTemperature;
        }

        public TextView getBaseWind() {
            return baseWind;
        }

        public ImageView getCityWeather() {
            return cityWeather;
        }

        public TextView getCityTemperature() {
            return cityTemperature;
        }

        public TextView getCityRainChance() {
            return cityRainChance;
        }
    }
}
