package pulseanddecibels.jp.yamatenki.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import pulseanddecibels.jp.yamatenki.R;
import pulseanddecibels.jp.yamatenki.database.Database;
import pulseanddecibels.jp.yamatenki.database.dao.Coordinate;
import pulseanddecibels.jp.yamatenki.database.dao.CoordinateDao;
import pulseanddecibels.jp.yamatenki.database.dao.Mountain;
import pulseanddecibels.jp.yamatenki.database.dao.MountainDao;
import pulseanddecibels.jp.yamatenki.database.dao.MyMountain;
import pulseanddecibels.jp.yamatenki.database.dao.MyMountainDao;
import pulseanddecibels.jp.yamatenki.model.ForecastArrayElement;
import pulseanddecibels.jp.yamatenki.model.MountainArrayElement;
import pulseanddecibels.jp.yamatenki.model.MountainForecastJSON;
import pulseanddecibels.jp.yamatenki.model.WindAndTemperatureElement;
import pulseanddecibels.jp.yamatenki.utils.DateUtils;
import pulseanddecibels.jp.yamatenki.utils.JSONDownloader;
import pulseanddecibels.jp.yamatenki.utils.JSONParser;
import pulseanddecibels.jp.yamatenki.utils.Settings;
import pulseanddecibels.jp.yamatenki.utils.Utils;

/**
 * Created by Diarmaid Lindsay on 2015/09/29.
 * Copyright Pulse and Decibels 2015
 */
public class MountainForecastActivity extends Activity {
    private final SparseIntArray DIFFICULTY_SMALL_IMAGES = new SparseIntArray() {
        {
            append(1, R.drawable.difficulty_small_a);
            append(2, R.drawable.difficulty_small_b);
            append(3, R.drawable.difficulty_small_c);
        }
    };
    private final SparseIntArray DIFFICULTY_BIG_IMAGES = new SparseIntArray() {
        {
            append(1, R.drawable.difficulty_large_a);
            append(2, R.drawable.difficulty_large_b);
            append(3, R.drawable.difficulty_large_c);
        }
    };
    private final List<ForecastScrollViewElement> scrollViewElements = new ArrayList<>();
    private TextView title;
    private ImageView currentDifficultyImage;
    private Button addMyMountainButton;
    private long mountainId;
    private MapView mMapView;
    private CallbackManager callbackManager;
    private LoginManager loginManager;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        Bundle arguments = getIntent().getExtras();
        mountainId = arguments.getLong("mountainId");
        MountainDao mountainDao = Database.getInstance(this).getMountainDao();
        final Mountain mountain = mountainDao.load(mountainId);

        title = (TextView) findViewById(R.id.text_forecast_header);
        title.setTypeface(Utils.getHannariTypeFace(this));
        title.setText(mountain.getTitle());
        currentDifficultyImage = (ImageView) findViewById(R.id.mountain_forecast_current_difficulty);
        TextView currentDifficultyText = (TextView) findViewById(R.id.mountain_forecast_current_difficulty_text);
        currentDifficultyText.setTypeface(Utils.getHannariTypeFace(this));
        ImageView helpIconDifficulty = (ImageView) findViewById(R.id.mountain_forecast_difficulty_help);
        helpIconDifficulty.setOnClickListener(
                getHelpDialogOnClickListener(R.string.help_text_difficulty));
        ImageView iconFacebook = (ImageView) findViewById(R.id.icon_facebook);
        iconFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayFacebookDialog();
            }
        });
        ImageView iconLine = (ImageView) findViewById(R.id.icon_line);
        ImageView iconTwitter = (ImageView) findViewById(R.id.icon_twitter);
        ImageView iconMail = (ImageView) findViewById(R.id.icon_mail);
        ImageView iconMaps = (ImageView) findViewById(R.id.icon_maps);
        iconMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayMapDialog(savedInstanceState);
            }
        });
        addMyMountainButton = (Button) findViewById(R.id.button_add_my_mountain);
        addMyMountainButton.setOnClickListener(getAddMyMountainListener());
        addMyMountainButton.setText(getMyMountainForMountainId() == null ?
                getResources().getString(R.string.button_add_my_mountain) :
                getResources().getString(R.string.button_remove_my_mountain));
        addMyMountainButton.setTypeface(Utils.getHannariTypeFace(this));
        Button addMemoButton = (Button) findViewById(R.id.button_add_memo);
        addMemoButton.setTypeface(Utils.getHannariTypeFace(this));
        addMemoButton.setOnClickListener(getAddMemoListener());
        initialiseWidgets();
        populateWidgets(JSONParser.parseMountainForecastFromFile(
                JSONDownloader.getMockMountainForecast(this, mountain.getYid())));
        if (new Settings(this).getSetting("setting_display_warning")) {
            displayWarningDialog();
        }
    }

    private void initialiseWidgets() {
        ScrollView mountainForecastScrollView = (ScrollView) findViewById(R.id.scroll_forecasts);
        LinearLayout todayAMForecast = (LinearLayout) mountainForecastScrollView.findViewById(R.id.today_am_forecast);
        LinearLayout todayPMForecast = (LinearLayout) mountainForecastScrollView.findViewById(R.id.today_pm_forecast);
        LinearLayout tomorrowAMForecast = (LinearLayout) mountainForecastScrollView.findViewById(R.id.tomorrow_am_forecast);
        LinearLayout tomorrowPMForecast = (LinearLayout) mountainForecastScrollView.findViewById(R.id.tomorrow_pm_forecast);
        //add in correct order because index matters for looking up date
        scrollViewElements.add(new ForecastScrollViewElement(todayAMForecast));
        scrollViewElements.add(new ForecastScrollViewElement(todayPMForecast));
        scrollViewElements.add(new ForecastScrollViewElement(tomorrowAMForecast));
        scrollViewElements.add(new ForecastScrollViewElement(tomorrowPMForecast));
        /*
        +2Forecast
        +3Forecast
        +4Forecast
        +5Forecast
        +6Forecast
         */
    }

    private int getRandomDifficulty() {
        final int MIN = 1;
        final int MAX = 3;
        return Utils.getRandomInRange(MIN, MAX);
    }

    /**
     * Return any entries from the MyMountains table
     */
    private MyMountain getMyMountainForMountainId() {
        MyMountainDao myMountainDao = Database.getInstance(MountainForecastActivity.this).getMyMountainDao();
        return myMountainDao.queryBuilder().where(MyMountainDao.Properties.MountainId.eq(mountainId)).unique();
    }

    /**
     * When the user deletes, the button becomes an add button.
     */
    private View.OnClickListener getAddMyMountainListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyMountainDao myMountainDao = Database.getInstance(MountainForecastActivity.this).getMyMountainDao();
                MyMountain myMountain = getMyMountainForMountainId();

                //if the mountain is in the my mountain table, disable the add button
                if (myMountain != null) {
                    myMountainDao.delete(myMountain);
                    addMyMountainButton.setText(getResources().getString(R.string.button_add_my_mountain));
                } else {
                    myMountainDao.insert(new MyMountain(null, mountainId));
                    addMyMountainButton.setText(getResources().getString(R.string.button_remove_my_mountain));
                }

                Intent intent = getIntent();
                intent.putExtra("changedMountain", mountainId);
                setResult(RESULT_OK, intent);
            }
        };
    }

    private View.OnClickListener getAddMemoListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MemoDetailActivity.class);
                intent.putExtra("mountainId", mountainId); //which mountain to make a memo for
                startActivity(intent);
            }
        };
    }

    private void populateWidgets(MountainForecastJSON forecasts) {
        if (forecasts == null) {
            return;
        }
        MountainArrayElement mountainInfo = forecasts.getMountainArrayElement();
        //set forecast page's title
        title.setText(mountainInfo.getTitle());
        //set forecast page's big difficulty image
        //TEMPORARY - using random big image
        currentDifficultyImage.setImageResource(DIFFICULTY_BIG_IMAGES.get(getRandomDifficulty()));

        Map<String, ForecastArrayElement> forecastMap = forecasts.getForecasts();
        //TEMPORARY until we get realtime forecasts
        ForecastArrayElement[] forecastArray = forecastMap.values().toArray(new ForecastArrayElement[forecastMap.values().size()]);
        //iterate through... today morning, today afternoon, tomorrow morning, tomorrow afternoon... etc
        for (int i = 0; i < scrollViewElements.size(); i++) {
            ForecastScrollViewElement scrollViewElement = scrollViewElements.get(i);
            //set ScrollViewElement header
            scrollViewElement.getHeader().setText(DateUtils.getFormattedHeader(i));

            //now depending on the amount of heights, we will display a different amount of rows...
            SparseArray<Integer> heights = forecasts.getHeights();
            String heightPressureTemplate = "高度 %s m付近（%shPa )";
            if (heights.size() == 3 || heights.size() == 2 || heights.size() == 1) {
                int height = heights.keyAt(0);
                String lowHeightPressure = String.format(heightPressureTemplate, height, heights.get(height));
                scrollViewElement.getLowHeightPressure().setText(lowHeightPressure);
            }
            if (heights.size() == 3 || heights.size() == 2) {
                int height = heights.keyAt(1);
                String midHeightPressure = String.format(heightPressureTemplate, height, heights.get(height));
                scrollViewElement.getMidHeightPressure().setText(midHeightPressure);
                scrollViewElement.showMidHeightRow(true);
            }
            if (heights.size() == 3) {
                int height = heights.keyAt(2);
                String highHeightPressure = String.format(heightPressureTemplate, height, heights.get(height));
                scrollViewElement.getHighHeightPressure().setText(highHeightPressure);
                scrollViewElement.showHighHeightRow(true);
            }

            int j = 0; //TEMPORARY until we get realtime forecasts

            for (ForecastColumn forecastColumn : scrollViewElement.getColumns()) {
                //find if there is a matching forecast available from json. If not, whole column is grey.
                String key = DateUtils.timeToMapKey(i, forecastColumn.getColumnId());
//                ForecastArrayElement forecastArrayElement = forecastMap.get(key); //when we get realtime forecasts
                int forecastIndex = j + (i * 4); //TEMPORARY until we get realtime forecasts
                ForecastArrayElement forecastArrayElement = forecastArray[forecastIndex]; //TEMPORARY until we get realtime forecasts
                forecastColumn.getTime().setText(forecastColumn.getColumnId());

                if (forecastArrayElement != null) {
                    List<WindAndTemperatureElement> windAndTemperatureElements =
                            forecastArrayElement.getWindAndTemperatures();
                    //again depending on the amount of heights, we will display a different amount of rows...
                    if (heights.size() == 3 || heights.size() == 2 || heights.size() == 1) {
                        int direction = getDirectionFromDegrees(windAndTemperatureElements.get(0).getWindDirection());
                        int velocity = windAndTemperatureElements.get(0).getWindVelocity();
                        forecastColumn.getLowHeightTemperature().setText(windAndTemperatureElements.get(0).getTemperature());
                        forecastColumn.getLowHeightWindDirection().setImageResource(getWindImage(direction, velocity));
                        forecastColumn.getLowHeightWind().setText(windAndTemperatureElements.get(0).getWindVelocityString());

                    }
                    if (heights.size() == 3 || heights.size() == 2) {
                        int direction = getDirectionFromDegrees(windAndTemperatureElements.get(1).getWindDirection());
                        int velocity = windAndTemperatureElements.get(1).getWindVelocity();
                        forecastColumn.getMidHeightTemperature().setText(windAndTemperatureElements.get(1).getTemperature());
                        forecastColumn.getMidHeightWindDirection().setImageResource(getWindImage(direction, velocity));
                        forecastColumn.getMidHeightWind().setText(windAndTemperatureElements.get(1).getWindVelocityString());
                    }
                    if (heights.size() == 3) {
                        int direction = getDirectionFromDegrees(windAndTemperatureElements.get(2).getWindDirection());
                        int velocity = windAndTemperatureElements.get(2).getWindVelocity();
                        forecastColumn.getHighHeightTemperature().setText(windAndTemperatureElements.get(2).getTemperature());
                        forecastColumn.getHighHeightWindDirection().setImageResource(getWindImage(direction, velocity));
                        forecastColumn.getHighHeightWind().setText(windAndTemperatureElements.get(2).getWindVelocityString());
                    }

                    forecastColumn.getDifficulty().setImageResource(DIFFICULTY_SMALL_IMAGES.get(forecastArrayElement.getMountainStatus()));
                    forecastColumn.getRainLevel().setText(forecastArrayElement.getPrecipitation());
                    forecastColumn.getCloudCover().setText(forecastArrayElement.getTotalCloudCover());
                } else {
                    //set grey background and blank
                    forecastColumn.getDifficulty().setBackgroundColor(Color.GRAY);
                    forecastColumn.getLowHeightTemperature().setBackgroundColor(Color.GRAY);
                    forecastColumn.getLowHeightWind().setBackgroundColor(Color.GRAY);
                    forecastColumn.getMidHeightTemperature().setBackgroundColor(Color.GRAY);
                    forecastColumn.getMidHeightWind().setBackgroundColor(Color.GRAY);
                    forecastColumn.getHighHeightTemperature().setBackgroundColor(Color.GRAY);
                    forecastColumn.getHighHeightWind().setBackgroundColor(Color.GRAY);
                    forecastColumn.getRainLevel().setBackgroundColor(Color.GRAY);
                    forecastColumn.getCloudCover().setBackgroundColor(Color.GRAY);
                }
                j++; //TEMPORARY until we get realtime forecasts
            }
        }
    }

    private int getWindImage(int direction, int velocity) {
        final SparseIntArray GREEN_ARROWS = new SparseIntArray() {
            {
                append(1, R.drawable.arrow_green01);
                append(2, R.drawable.arrow_green02);
                append(3, R.drawable.arrow_green03);
                append(4, R.drawable.arrow_green04);
                append(5, R.drawable.arrow_green05);
                append(6, R.drawable.arrow_green06);
                append(7, R.drawable.arrow_green07);
                append(8, R.drawable.arrow_green08);
            }
        };
        final SparseIntArray BLUE_ARROWS = new SparseIntArray() {
            {
                append(1, R.drawable.arrow_blue01);
                append(2, R.drawable.arrow_blue02);
                append(3, R.drawable.arrow_blue03);
                append(4, R.drawable.arrow_blue04);
                append(5, R.drawable.arrow_blue05);
                append(6, R.drawable.arrow_blue06);
                append(7, R.drawable.arrow_blue07);
                append(8, R.drawable.arrow_blue08);
            }
        };
        final SparseIntArray RED_ARROWS = new SparseIntArray() {
            {
                append(1, R.drawable.arrow_red01);
                append(2, R.drawable.arrow_red02);
                append(3, R.drawable.arrow_red03);
                append(4, R.drawable.arrow_red04);
                append(5, R.drawable.arrow_red05);
                append(6, R.drawable.arrow_red06);
                append(7, R.drawable.arrow_red07);
                append(8, R.drawable.arrow_red08);
            }
        };

        if (velocity < 7) {
            return GREEN_ARROWS.get(direction);
        } else if (velocity < 15) {
            return BLUE_ARROWS.get(direction);
        } else {
            return RED_ARROWS.get(direction);
        }
    }

    private int getDirectionFromDegrees(int degrees) {
        if (((degrees >= 337.5) && (degrees <= 360)) || (degrees >= 0 & degrees < 22.5)) {
            return 1;
        } else if (degrees >= 22.5 && degrees < 67.5) {
            return 2;
        } else if (degrees >= 67.5 && degrees < 112.5) {
            return 3;
        } else if (degrees >= 112.5 && degrees < 157.5) {
            return 4;
        } else if (degrees >= 157.5 && degrees < 202.5) {
            return 5;
        } else if (degrees >= 202.5 && degrees < 247.5) {
            return 6;
        } else if (degrees >= 247.5 && degrees < 292.5) {
            return 7;
        } else if (degrees >= 292.5 && degrees < 337.5) {
            return 8;
        }
        return 0;
    }

    public View.OnClickListener getHelpDialogOnClickListener(final int stringId) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String text = MountainForecastActivity.this.getResources().getString(stringId);
                final Dialog dialog = new Dialog(MountainForecastActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_help);
                TextView helpText = (TextView) dialog.findViewById(R.id.help_text);
                helpText.setText(text);
                dialog.setCanceledOnTouchOutside(true);
                Drawable d = new ColorDrawable(getResources().getColor(R.color.yama_brown));
                d.setAlpha(200);
                dialog.getWindow().setBackgroundDrawable(d);
                dialog.show();
            }
        };
    }

    private void displayWarningDialog() {
        final String text = MountainForecastActivity.this.getResources().getString(R.string.text_forecast_warning);
        final Dialog dialog = new Dialog(MountainForecastActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_help);
        TextView helpText = (TextView) dialog.findViewById(R.id.help_text);
        helpText.setText(text);
        dialog.setCanceledOnTouchOutside(true);
        Drawable d = new ColorDrawable(getResources().getColor(R.color.yama_brown));
        d.setAlpha(200);
        dialog.getWindow().setBackgroundDrawable(d);
        dialog.show();
    }

    private void displayMapDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(MountainForecastActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_map);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mMapView = (MapView) dialog.findViewById(R.id.mapview);
        //must call the whole lifecycle http://www.matt-reid.co.uk/blog_post.php?id=93
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        MapsInitializer.initialize(MountainForecastActivity.this);
        GoogleMap map = mMapView.getMap();

        MountainDao mountainDao = Database.getInstance(this).getMountainDao();
        CoordinateDao coordinateDao = Database.getInstance(this).getCoordinateDao();
        Mountain mountain = mountainDao.load(mountainId);
        Coordinate coordinate =
                coordinateDao.queryBuilder().where(CoordinateDao.Properties.MountainId.eq(mountainId)).unique();

        LatLng latLng = new LatLng(coordinate.getLatitude(), coordinate.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        map.moveCamera(cameraUpdate);
        map.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mountain_axe_pin))
                .snippet(mountain.getHeight() + "m")
                .title(mountain.getTitleExt())).showInfoWindow();

        dialog.show();
    }

    private void displayFacebookDialog() {
        final Dialog dialog = new Dialog(MountainForecastActivity.this, R.style.YamaDialog);
        dialog.setContentView(R.layout.dialog_facebook);
        dialog.setTitle(getResources().getString(R.string.dialog_facebook));
        dialog.setCanceledOnTouchOutside(true);

        Button postButton = (Button) dialog.findViewById(R.id.facebook_post_button);
        final EditText postText = (EditText) dialog.findViewById(R.id.facebook_post_text);
        ImageView postPreview = (ImageView) dialog.findViewById(R.id.facebook_image_preview);

        final Bitmap composite = createForecastComposite();
        postPreview.setImageBitmap(composite);

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postToFacebook(postText.getText().toString(), composite);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private Bitmap createForecastComposite() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        LinearLayout snLayout = (LinearLayout) inflater.inflate(R.layout.social_network_forecast, null);
        snLayout.setDrawingCacheEnabled(true);
        snLayout.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        final String[] TIME_INTERVAL = {"00", "03", "06", "09", "12", "15", "18", "21"};

        MountainDao mountainDao = Database.getInstance(this).getMountainDao();
        final Mountain mountain = mountainDao.load(mountainId);

        TextView snForecastHeader = (TextView) snLayout.findViewById(R.id.sn_forecast_header);
        snForecastHeader.setText(mountain.getTitle());
        snForecastHeader.setTypeface(Utils.getHannariTypeFace(this));

        TextView snCurrentDifficultyText = (TextView) snLayout.findViewById(R.id.sn_current_difficulty_text);
        snCurrentDifficultyText.setTypeface(Utils.getHannariTypeFace(this));

        TextView snPoweredByText = (TextView) snLayout.findViewById(R.id.text_powered_by);
        snPoweredByText.setTypeface(Utils.getHannariTypeFace(this));

        ImageView snCurrentDifficulty = (ImageView) snLayout.findViewById(R.id.sn_current_difficulty);
        //TEMPORARY - using random difficulty image
        snCurrentDifficulty.setImageResource(DIFFICULTY_BIG_IMAGES.get(getRandomDifficulty()));

        TextView snDate = (TextView) snLayout.findViewById(R.id.sn_date);
        DateTime now = new DateTime();
        snDate.setText(String.format("%s月%s日　本日の山行指数", Utils.num2DigitString(now.getMonthOfYear()), Utils.num2DigitString(now.getDayOfMonth())));

        MountainForecastJSON forecastJSON = JSONParser.parseMountainForecastFromFile(
                JSONDownloader.getMockMountainForecast(this, mountain.getYid()));
        if (forecastJSON != null) {
            Map<String, ForecastArrayElement> forecasts = forecastJSON.getForecasts();
            //TEMPORARY until we get realtime forecasts
            ForecastArrayElement[] forecastArray = forecasts.values().toArray(new ForecastArrayElement[forecasts.values().size()]);
            for (int i = 0; i < TIME_INTERVAL.length; i++) {
                ForecastArrayElement forecast = forecastArray[i];
                TextView time = (TextView) snLayout.findViewById(getResources().getIdentifier("sn_time_" + TIME_INTERVAL[i], "id", getPackageName()));
                time.setText(TIME_INTERVAL[i]);
                ImageView image = (ImageView) snLayout.findViewById(getResources().getIdentifier("sn_image_" + TIME_INTERVAL[i], "id", getPackageName()));
                image.setImageResource(DIFFICULTY_SMALL_IMAGES.get(forecast.getMountainStatus()));
            }
        }
        snLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        snLayout.layout(0, 0, snLayout.getMeasuredWidth(), snLayout.getMeasuredHeight());
        snLayout.buildDrawingCache(true);
        Bitmap composite = Bitmap.createBitmap(snLayout.getDrawingCache(true));

        snLayout.setDrawingCacheEnabled(false);
        return composite;
    }

    private void postToFacebook(final String text, final Bitmap image) {
        callbackManager = CallbackManager.Factory.create();
        List<String> permissionNeeds = Collections.singletonList("publish_actions");
        //this loginManager helps you eliminate adding a LoginButton to your UI
        loginManager = LoginManager.getInstance();
        loginManager.logInWithPublishPermissions(this, permissionNeeds);
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                SharePhoto photo = new SharePhoto.Builder()
                        .setBitmap(image)
                        .setCaption(text)
                        .build();
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();
                ShareApi.share(content, null);
            }

            @Override
            public void onCancel() {
                System.out.println("sharePictureToFacebook.onCancel()");
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(MountainForecastActivity.this,
                        "Could not post to Facebook now, please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMapView != null) {
            mMapView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMapView != null) {
            mMapView.onDestroy();
        }

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView != null) {
            mMapView.onLowMemory();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        callbackManager.onActivityResult(requestCode, responseCode, data);
    }

    // For now this is used for "Today" and "Tomorrow", ie, short term forecasts
    private class ForecastScrollViewElement {
        private final String[] TIME_INCREMENTS = {"00", "03", "06", "09"}; // column headers
        private final TextView header;
        private final List<ForecastColumn> columns = new ArrayList<>();
        private final TextView lowHeightPressure; //1000m
        private final TextView midHeightPressure; //2000m
        private final TextView highHeightPressure; //3000m

        private final TableRow lowHeightPressureRow;
        private final TableRow midHeightPressureRow;
        private final TableRow highHeightPressureRow;

        private final TableRow lowTempRow;
        private final TableRow lowWindRow;
        private final ImageView lowWindHelp;
        private final TableRow midTempRow;
        private final TableRow midWindRow;
        private final ImageView midWindHelp;
        private final TableRow highTempRow;
        private final TableRow highWindRow;
        private final ImageView highWindHelp;
        private final ImageView cloudCoverHelp;

        public ForecastScrollViewElement(LinearLayout forecast) {
            header = (TextView) forecast.findViewById(R.id.forecast_header);
            TableLayout forecastTable = (TableLayout) forecast.findViewById(R.id.forecast_table);

            lowTempRow = (TableRow) forecastTable.findViewById(R.id.forecast_low_row_temperature);
            lowWindRow = (TableRow) forecastTable.findViewById(R.id.forecast_low_row_wind);
            lowWindHelp = (ImageView) lowWindRow.findViewById(R.id.help_icon_wind_speed);
            lowWindHelp.setOnClickListener(getHelpDialogOnClickListener(R.string.help_text_wind_speed));

            midTempRow = (TableRow) forecastTable.findViewById(R.id.forecast_mid_row_temperature);
            midWindRow = (TableRow) forecastTable.findViewById(R.id.forecast_mid_row_wind);
            midWindHelp = (ImageView) midWindRow.findViewById(R.id.help_icon_wind_speed);
            midWindHelp.setOnClickListener(getHelpDialogOnClickListener(R.string.help_text_wind_speed));

            highTempRow = (TableRow) forecastTable.findViewById(R.id.forecast_high_row_temperature);
            highWindRow = (TableRow) forecastTable.findViewById(R.id.forecast_high_row_wind);
            highWindHelp = (ImageView) highWindRow.findViewById(R.id.help_icon_wind_speed);
            highWindHelp.setOnClickListener(getHelpDialogOnClickListener(R.string.help_text_wind_speed));
            cloudCoverHelp = (ImageView) forecastTable.findViewById(R.id.help_icon_cloud_cover);
            cloudCoverHelp.setOnClickListener(getHelpDialogOnClickListener(R.string.help_text_cloud_cover));

            for (String time : TIME_INCREMENTS) {
                columns.add(new ForecastColumn(forecastTable, lowTempRow, lowWindRow, midTempRow, midWindRow, highTempRow, highWindRow, time));
            }

            lowHeightPressureRow = (TableRow) forecastTable.findViewById(R.id.forecast_low_row_height);
            lowHeightPressure = (TextView) lowHeightPressureRow.findViewById(R.id.forecast_height_pressure);
            midHeightPressureRow = (TableRow) forecastTable.findViewById(R.id.forecast_mid_row_height);
            midHeightPressure = (TextView) midHeightPressureRow.findViewById(R.id.forecast_height_pressure);
            highHeightPressureRow = (TableRow) forecastTable.findViewById(R.id.forecast_high_row_height);
            highHeightPressure = (TextView) highHeightPressureRow.findViewById(R.id.forecast_height_pressure);

            showMidHeightRow(false); //hide by default (assume small mountain)
            showHighHeightRow(false); //hide by default (assume small mountain)
        }

        public void showMidHeightRow(boolean show) {
            int visibility;
            if (show) {
                visibility = View.VISIBLE;
            } else {
                visibility = View.GONE;
            }
            midTempRow.setVisibility(visibility);
            midWindRow.setVisibility(visibility);
            midHeightPressureRow.setVisibility(visibility);
        }

        public void showHighHeightRow(boolean show) {
            int visibility;
            if (show) {
                visibility = View.VISIBLE;
            } else {
                visibility = View.GONE;
            }
            highTempRow.setVisibility(visibility);
            highWindRow.setVisibility(visibility);
            highHeightPressureRow.setVisibility(visibility);
        }

        public TextView getHeader() {
            return header;
        }

        public List<ForecastColumn> getColumns() {
            return columns;
        }

        public TextView getLowHeightPressure() {
            return lowHeightPressure;
        }

        public TextView getMidHeightPressure() {
            return midHeightPressure;
        }

        public TextView getHighHeightPressure() {
            return highHeightPressure;
        }
    }

    private class ForecastColumn {
        private final String columnId;
        private final TextView time;
        private final ImageView difficulty;
        private final TextView lowHeightTemperature;
        private final TextView lowHeightWind;
        private final ImageView lowHeightWindDirection;
        private final TextView midHeightTemperature;
        private final TextView midHeightWind;
        private final ImageView midHeightWindDirection;
        private final TextView highHeightTemperature;
        private final TextView highHeightWind;
        private final ImageView highHeightWindDirection;
        private final TextView rainLevel;
        private final TextView cloudCover;

        public ForecastColumn(TableLayout forecastTable, TableRow lowTemp, TableRow lowWind, TableRow midTemp, TableRow midWind, TableRow highTemp, TableRow highWind, String columnId) {
            this.columnId = columnId;
            time = (TextView) forecastTable.findViewById(getResources().getIdentifier("forecast_time_" + columnId, "id", getPackageName()));
            difficulty = (ImageView) forecastTable.findViewById(getResources().getIdentifier("forecast_difficulty_" + columnId, "id", getPackageName()));
            lowHeightTemperature = (TextView) lowTemp.findViewById(getResources().getIdentifier("forecast_temp_" + columnId, "id", getPackageName()));
            lowHeightWindDirection = (ImageView) lowWind.findViewById(getResources().getIdentifier("forecast_wind_direction_" + columnId, "id", getPackageName()));
            lowHeightWind = (TextView) lowWind.findViewById(getResources().getIdentifier("forecast_wind_" + columnId, "id", getPackageName()));
            midHeightTemperature = (TextView) midTemp.findViewById(getResources().getIdentifier("forecast_temp_" + columnId, "id", getPackageName()));
            midHeightWindDirection = (ImageView) midWind.findViewById(getResources().getIdentifier("forecast_wind_direction_" + columnId, "id", getPackageName()));
            midHeightWind = (TextView) midWind.findViewById(getResources().getIdentifier("forecast_wind_" + columnId, "id", getPackageName()));
            highHeightTemperature = (TextView) highTemp.findViewById(getResources().getIdentifier("forecast_temp_" + columnId, "id", getPackageName()));
            highHeightWindDirection = (ImageView) highWind.findViewById(getResources().getIdentifier("forecast_wind_direction_" + columnId, "id", getPackageName()));
            highHeightWind = (TextView) highWind.findViewById(getResources().getIdentifier("forecast_wind_" + columnId, "id", getPackageName()));
            rainLevel = (TextView) forecastTable.findViewById(getResources().getIdentifier("forecast_rain_level_" + columnId, "id", getPackageName()));
            cloudCover = (TextView) forecastTable.findViewById(getResources().getIdentifier("forecast_cloud_cover_" + columnId, "id", getPackageName()));
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

        public TextView getLowHeightTemperature() {
            return lowHeightTemperature;
        }

        public TextView getLowHeightWind() {
            return lowHeightWind;
        }

        public TextView getMidHeightTemperature() {
            return midHeightTemperature;
        }

        public TextView getMidHeightWind() {
            return midHeightWind;
        }

        public TextView getHighHeightTemperature() {
            return highHeightTemperature;
        }

        public TextView getHighHeightWind() {
            return highHeightWind;
        }

        public TextView getRainLevel() {
            return rainLevel;
        }

        public TextView getCloudCover() {
            return cloudCover;
        }

        public ImageView getLowHeightWindDirection() {
            return lowHeightWindDirection;
        }

        public ImageView getMidHeightWindDirection() {
            return midHeightWindDirection;
        }

        public ImageView getHighHeightWindDirection() {
            return highHeightWindDirection;
        }
    }
}
