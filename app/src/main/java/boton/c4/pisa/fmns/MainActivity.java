package boton.c4.pisa.fmns;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import static androidx.camera.core.ImageCapture.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.ZoomState;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import com.android.volley.AuthFailureError;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.util.Log;
import android.util.Size;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import java.util.TimeZone;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;

import static java.lang.Integer.parseInt;
import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity implements SettingsDialog.SettingsDialogListener{
    private ImageView img_background,img_touch_background;
    private ImageView white_tick;
    private View decorView;
    private TextView downloading_details;
    private Button footerText,footerText1;
    private ImageButton imgcapture;
    public TextClock digiClock;
    public TextView milliSecondClock;
    public Preview preview;
    public ImageCapture imgCap;
    private ProgressBar progressBar;
    private String storage_path = "";
    private String upload_image_storage_path = "";
    private String path = "";
    private String gallery_path = "";

    public static final String IMAGE_DIRECTORY_NAME = "dev1";

    private Uri fileUri; // file url to store image/video
    public static final int MEDIA_TYPE_IMAGE = 1;

    //private final String IMG_URL_PATH = "https://devi.softound.com/mx/botonc4/";
    //private final String UPLOAD_IMG_URL_PATH = "https://devi.softound.com/mx/upload_images/";
    //private final String IMEI_URL_PATH = "https://devi.softound.com/mx/IMEIgps.php";
    private final String IMG_URL_PATH = "https://mx911.org/SENL/";
    private final String IMEI_URL_PATH = "https://mx911.org/SENL/IMEIgps_nl.php?imei=";
    private final String[] IMAGES = new String[]{"nlc5_green.jpg","nlc5_red.jpg"};
    private final String[] UPLOAD_IMAGES = new String[]{"timer_image.jpg"};
    private final ArrayList<String> listofAllimages = new ArrayList<>();
    public String footer_hyper_link = "";
    public String tracking_id ;
    public String track_id;
    public String upload_track_id;
    private long download_id;
    public DbAssist dbHelper;
    public int current_view_mode=0;
    public int current_image_mode=0;
    public SettingsDialog dialogContainer;
    public LocationDialog dialogLocation;
    public CloseCircuitDialog closeCircuitDialog;
    public DeviceMovingDialog dialogDeviceMoving;
    public LatLngDialog dialogLatLng;
    public double gps_latitude;
    public double gps_longitude;
    public Handler image_handler;
    public Handler non_image_handler;
    public Handler gallery_image_handler;
    public Runnable image_runnable;
    public Runnable non_image_runnable;
    public Runnable gallery_image_runnable;
    public int is_image_clicked = 0;

    public Handler exit_handler;
    public Runnable exit_runnable;
    public long exit_start_time=0, exit_end_time=0;
    public int EXIT_MAX_DELAY = 500;
    public volatile int click_count = 0, is_previous_request_done = 0;

    public Handler startup_handler;
    public Runnable startup_runnable;

    public Handler seconds_handler;
    public Runnable seconds_runnable;

    public Handler milli_seconds_handler;
    public Runnable milli_seconds_runnable;

    public Handler minute_handler;
    public Runnable minute_runnable;

    public Handler camera_handler;
    public Runnable camera_runnable;

    public Handler manually_upload_images_handler;
    public Runnable manually_upload_images_runnable;

    public Handler upload_images_handler;
    public Runnable upload_images_runnable;

    public Handler check_request_handler;
    public Runnable check_request_runnable;

    public Handler deletewavlog_handler;
    public Runnable deletewavlog_runnable;

    public Handler dev_log_handler;
    public Runnable dev_log_runnable;

    public boolean stop_count_seconds = true;
    public int location_id;


    public long imei_value = 0;

    public int count_down_seconds = 40;

    public int count_down;

    public int pre_execute = 0;
    public int post_execute = 0;

    public String event_id;

    public int j = 0;

    public boolean is_gps_enabled = false;

    public TextView txtRunningSeconds;

    private FusedLocationProviderClient client;
    int PERMISSION_ID = 44;

    public int device_moving_speed = 50; //in meter

    public boolean update_latlng_dialog = false;

    public boolean request_completed;
    public boolean is_headset_connected = false;

    public int zoom_level_change;



    /*
     * Radious range in meter to show alert and prevent sending request,
     * if the device is in beyound the specified range from the location when the app is installed
     */
    public int RADIUS_RANGE = 80;//30; //in meter

    public int ACCURACY = 50;

    // Location manager
    private LocationManager manager;
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1001;

    public static final int MY_PERMISSIONS_REQUEST_ACCESS_GALLERY = 1002;

    public Stack<String> location_arr = new Stack<String>();
    public Stack<String> location_arr2 = new Stack<String>();
    public Stack<String> current_location_arr = new Stack<String>();
    public String start_time = "";
    public long difference_In_Minutes = 0;
    public boolean is_search_location = true;

    int update_latlng_dialog_count = 0;
    public int global_variance = 0;
    public boolean is_restart_location_search = false;
    public boolean is_on_touch_active = false;
    public boolean action_up;
    public boolean action_up_running = false;
    public int white_tick_active_seconds = 6;//60;
    public int total_milli_seconds_running = 0;
    public boolean key_down_pressed = false;
    public boolean keydown_long_pressed = false;
    public boolean setting_dialog = false;
    public boolean is_audio_stop = false;
    public boolean is_alarm_start = false;
    public boolean is_camera_open = false;
    public boolean downTouch = true;
    public boolean is_check_network = true;
    public boolean is_flash_light = false;
    public boolean is_charge_light = false;
    public boolean is_ambient_light = false;
    public boolean is_charger_level = true;
    public boolean is_on_pause = false;
    public String msg_response;

    public Date currentDate;
    public Date imageDate;
    public String startDate;



    private AudioTrack audioTrack;
    public  AudioManager audioManager;
    public int CONNECTION_TIMEOUT_SECONDS = 5000; //milli seconds
    TextureView textureView;
    private TextView mTextViewPercentage;
    private ProgressBar mProgressBar;
    private int mProgressStatus = 0;
    private Float zoom_val= Float.valueOf(0);
    private boolean is_upload_clickable = true;

    public boolean is_manullay_upload_images = false;

    public boolean check_upload_server = false;

    public Long totalram,availableMegs;

    public String update_footer_response;
    public int images_count = 0;
    public int fifo_image_length = 0;
    public boolean bmp_file_exists = true;

    private RequestQueue requestQueue;
    private RequestQueue check_evnt_nine_Queue;
    private RequestQueue check_upload_request_Queue;
    public RequestQueue dev_log_Queue;

    public boolean event_11 = false;
    public boolean event_10 = false;

    public boolean is_takenpicture = true;

    Executor cameraExecutor = Executors.newSingleThreadExecutor();

    private PreviewView mpreviewView;
    private ImageCapture imageCapture;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    private  CameraSelector cameraSelector;
    private int mPicOrientation,image_orientation,hor_orientation;
    private OrientationEventListener mOrientationEventListener;
    private ImageAnalysis imageAnalysis;

    private Camera cam;
    private String picorientation;

    public String batterylevel;

    public String check_power_status;

    public float batteryTemp;

    public String event_nine = "";

    public String upload_event;

    public int upload_start_length = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        //setFinishOnTouchOutside(false);
        //Log.d("launching","on_create");

        decorView = getWindow().getDecorView();
        dbHelper = new DbAssist(getApplicationContext());
        img_background = (ImageView) findViewById(R.id.imageView);
        white_tick = (ImageView) findViewById(R.id.white_tick);
        digiClock = (TextClock) findViewById(R.id.timeClock);
        milliSecondClock = (TextView) findViewById(R.id.milliSecondClock);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        footerText  = (Button) findViewById(R.id.footer_val);
        footerText1 = (Button) findViewById(R.id.footer_val1);
        imgcapture = (ImageButton) findViewById(R.id.imgCapture);

        mTextViewPercentage = (TextView) findViewById(R.id.tv_percentage);
        mProgressBar = (ProgressBar) findViewById(R.id.pb);


        textureView = findViewById(R.id.view_finder);
        mpreviewView = findViewById(R.id.camerapreview);
        img_touch_background = (ImageView) findViewById(R.id.image_touch_view);



        count_down = count_down_seconds;

        downloading_details = (TextView) findViewById(R.id.downloading_info);

        txtRunningSeconds = (TextView) findViewById(R.id.txtRunningSeconds);

        storage_path = getApplicationContext().getExternalFilesDir("images").getAbsolutePath() + File.separator;
        upload_image_storage_path = getApplicationContext().getExternalFilesDir("upload_images").getAbsolutePath() + File.separator;
        img_background.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        img_background.setLongClickable(true);
        digiClock.setFormat12Hour("kk:mm:ss");
        digiClock.setFormat24Hour(null);


        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        //formatter.setTimeZone(TimeZone.getTimeZone("GMT-5"));
        startDate = formatter.format(new Date());

        update_table("IMAGE_COUNT", "0");
        //update_table("CIRCUIT_CLOSED", "0");

        //Double dis = distance(11.470610481764655, 77.16981418430396, 11.470781343103312, 77.17003278432684, "K");




        // GPS init
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        // For Android 6.0 ask for All permissions in runtime
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            new WavLog("requestPermissions: "+Build.VERSION.SDK_INT+"=="+Build.VERSION_CODES.M, getApplicationContext(), "MainActivity", "onCreate");
            requestPermissions(new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.SEND_SMS,
            }, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        } else {
            new WavLog("requestPermissions else", getApplicationContext(), "MainActivity", "onCreate");
        }

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG).show();
        }
        //Check if location is enabled in mobile
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            new WavLog("buildAlertMessageNoGps", getApplicationContext(), "MainActivity", "onCreate");
            buildAlertMessageNoGps();
        }

        if(exit_handler!=null) {
            if(exit_runnable!=null) {
                exit_handler.removeCallbacks(exit_runnable);
            }
        }

        if(camera_handler!=null) {
            if(camera_runnable!=null) {
                camera_handler.removeCallbacks(camera_runnable);
            }
        }

        if(startup_handler!=null) {
            if(startup_runnable!=null) {
                startup_handler.removeCallbacks(startup_runnable);
            }
        }

        if(image_handler!=null) {
            if(image_runnable!=null) {
                image_handler.removeCallbacks(image_runnable);
            }
        }

        if(non_image_handler!=null) {
            if(non_image_runnable!=null) {
                non_image_handler.removeCallbacks(non_image_runnable);
            }
        }

        if(gallery_image_handler!=null) {
            if(gallery_image_runnable!=null) {
                gallery_image_handler.removeCallbacks(gallery_image_runnable);
            }
        }

        if(minute_handler!=null) {
            if(minute_handler!=null) {
                minute_handler.removeCallbacks(minute_runnable);
            }
        }

        if(milli_seconds_handler!=null) {
            if(milli_seconds_runnable!=null) {
                milli_seconds_handler.removeCallbacks(milli_seconds_runnable);
            }
        }

        if(upload_images_handler!=null) {
            if(upload_images_runnable!=null) {
                upload_images_handler.removeCallbacks(upload_images_runnable);
            }
        }

        if(manually_upload_images_handler!=null) {
            if(manually_upload_images_runnable!=null) {
                manually_upload_images_handler.removeCallbacks(manually_upload_images_runnable);
            }
        }

        if(deletewavlog_handler!=null) {
            if(deletewavlog_runnable!=null) {
                deletewavlog_handler.removeCallbacks(deletewavlog_runnable);
            }
        }


        if(check_request_handler!=null) {
            if(check_request_runnable!=null) {
                check_request_handler.removeCallbacks(check_request_runnable);
            }
        }

        if( dev_log_handler !=null ) {
            if( dev_log_runnable!=null) {
                dev_log_handler.removeCallbacks(dev_log_runnable);
            }
        }

        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if(visibility ==0 ){
                    decorView.setSystemUiVisibility(hideSystemBars());
                }
            }

        });





        imei_value = Long.parseLong(read_from_table("IMEI"));




        startup_handler = new Handler();
        startup_runnable = new Runnable() {
            @Override
            public void run() {
                Double gps_lat_info = Double.parseDouble(read_from_table("GPS_LAT"));
                Double gps_long_info = Double.parseDouble(read_from_table("GPS_LONG"));
                if(gps_lat_info>0) {
                    //Log.d("sfnd", "Getting Info");
                    downloading_details.setText(getString(R.string.getting_info));
                    imei_value = Long.parseLong(read_from_table("IMEI"));
                    if(imei_value==0) {
                        //Log.d("sfnd", "update_imei");
                        update_imei();
                    }
                    if(get_status_of_files()==0 || get_status_of_files() ==1) {
                        //Log.d("sfnd", "download_image_files");
                        download_image_files();

                    } else if(get_status_of_files()==2) {
                        current_view_mode = parseInt(read_from_table("CURRENT_VIEW"));
                        current_image_mode = parseInt(read_from_table("MY_IMAGE"));
                        //current_view_mode = 0;

                        String current_gps_lat_info = read_from_table("CURRENT_GPS_LAT");
                        //Allow to execute "set_current_view_mode" after the location is retrieved
                        if(!current_gps_lat_info.equals("0")) {
                            //Log.d("sfnd", "set_current_view_mode");
                            set_current_view_mode(current_view_mode,current_image_mode);
                        } else {
                            //Log.d("sfnd", "startup_handler.postDelayed");
                            startup_handler.postDelayed(startup_runnable,100);
                        }
                    }
                    return;
                } else {
                    startup_handler.postDelayed(this,2000);
                }
            }
        };
        startup_handler.postDelayed(startup_runnable,100);


        camera_handler = new Handler();
            camera_runnable = new Runnable() {
                public void run() {
                    String camera_upload = read_from_table("CAMERA_UPLOADS");
                    int taken_picture = parseInt(read_from_table("TAKEN_PICTURE"));
                    int upload_periodtime = parseInt(read_from_table("UPLOAD_IMAGES"));
                    if( is_camera_open == true && read_from_table("IS_APP_INSTALLED").equals("1") && is_on_pause == false ) {
                        takenpicture();
                    }
                    //Log.d("camera_uploads","uploads"+is_camera_open);
                    if( is_camera_open == false && read_from_table("IS_APP_INSTALLED").equals("1") && is_on_pause == false ) {
                        startCamera();
                    }
                    camera_handler.postDelayed(this, upload_periodtime*1000);
                }
            };
            camera_handler.postDelayed(camera_runnable, 5000); //for initial delay..*/


        upload_images_handler = new Handler();
            upload_images_runnable = new Runnable() {
                @Override
                public void run() {
                    int upload_images = parseInt(read_from_table("UPLOAD_IMAGES"));

                    int taken_picture = parseInt(read_from_table("TAKEN_PICTURE"));
                    String response_status = read_from_table("RESPONSE_STATUS");
                    if(  read_from_table("IS_APP_INSTALLED").equals("1") && is_upload_clickable == true ) {
                        check_event_nine("upload_images");
                        if( upload_images == 3 ) {
                            new WavLog("Event #9 Upload Images seconds "+ upload_images +" Not register user", getApplicationContext(), "MainActivity", "upload_images");
                        } else {
                            new WavLog("Event #9 Upload Images seconds " + upload_images +" register user", getApplicationContext(), "MainActivity", "upload_images");
                        }
                    }
                    Log.d("event_nine",event_nine);
                    if( !event_nine.equals("") && read_from_table("IS_APP_INSTALLED").equals("1") ) {
                        upload_images_handler.postDelayed(this,parseInt(event_nine)*1000);
                    } else {
                        upload_images_handler.postDelayed(this,5*upload_images*1000);
                    }
                }
            };
            upload_images_handler.postDelayed(upload_images_runnable,5*1000);


           manually_upload_images_handler = new Handler();
                manually_upload_images_runnable = new Runnable() {
                    @Override
                    public void run() {
                        String camera_upload = read_from_table("CAMERA_UPLOADS");
                        Log.d("upload_images", String.valueOf(is_manullay_upload_images));
                        /*if( read_from_table("IS_APP_INSTALLED").equals("1") && is_manullay_upload_images == false && camera_upload.equals("1") && is_on_pause == false && is_check_network == true ) {
                            upload_all_images();
                            manually_upload_images_handler.postDelayed(this,1000*5);
                        } else if( read_from_table("IS_APP_INSTALLED").equals("1") && is_manullay_upload_images == true && camera_upload.equals("1") && is_on_pause == false && is_check_network == true ) {
                            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);
                            File files[];
                            files = mediaStorageDir.listFiles();
                            manually_upload_images(fifo_image_length,files,mediaStorageDir);
                            manually_upload_images_handler.postDelayed(this,1000*2);
                        }*/

                        if( read_from_table("IS_APP_INSTALLED").equals("1")  && camera_upload.equals("1") && is_on_pause == false && is_check_network == true ) {
                            upload_folder_image();
                        }
                        manually_upload_images_handler.postDelayed(this,1000);
                    }
                };
                manually_upload_images_handler.postDelayed(manually_upload_images_runnable,3000);

            deletewavlog_handler = new Handler();
                deletewavlog_runnable = new Runnable() {
                    @Override
                    public void run() {
                        deleteWavlog();
                        deletewavlog_handler.postDelayed(this,24*60*60*1000);
                    }

                };
                deletewavlog_handler.postDelayed(deletewavlog_runnable,24*60*60*1000);

                /*check_request_handler = new Handler();
                    check_request_runnable= new Runnable() {
                        @Override
                        public void run() {
                            if( pre_execute - post_execute < 3 && event_11 == true ) {
                                event_11 = false;
                                //check_upload_request("event_11");
                            }

                            if( pre_execute - post_execute > 3 ) {
                                event_11 = true;
                                //check_upload_request("event_10");
                            }
                            check_request_handler.postDelayed(this, 15*1000);
                        }
                    };
                    check_request_handler.postDelayed(check_request_runnable,15*1000);*/


        img_touch_background.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                is_image_clicked = parseInt(read_from_table("IMAGE_CLICKABLE"));
                //Log.d("camera_event", "event" + event.getActionIndex());
                new WavLog("background Image touch request", getApplicationContext(), "MainActivity", "serontouchlistener");
                if (is_image_clicked == 1 && read_from_table("IS_APP_INSTALLED").equals("1")) {
                    //if (is_headset_connected == false)
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            downTouch = true;
                            touchDownEvent("touch_mode");
                            return true;

                        case MotionEvent.ACTION_UP:
                            if (downTouch) {
                                downTouch = false;
                                touchUpEvent("touch_mode");
                                return true;
                            }
                    }
                }
                return false;
            }
        });

        footerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_browser_info();
            }
        });


        //Show running seconds while searching locations
        seconds_handler = new Handler();
        seconds_runnable = new Runnable() {
            public void run() {
                String ambeint_light = read_from_table("AMBEINT_LIGHT");
                //Log.d("sfnd", "stop_count_seconds: "+stop_count_seconds);
                //Log.d("sfnd", "count_down: "+count_down);


                if( ambeint_light.equals("0") && is_ambient_light == true ) {
                    //preview.enableTorch(false);
                    if ( cam.getCameraInfo().hasFlashUnit() ) {
                        cam.getCameraControl().enableTorch(false); // or false
                    }
                    is_ambient_light = false;
                }


                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    txtRunningSeconds.setText("GPS Disabled!");
                } else {
                    if (stop_count_seconds == false) {
                        if (count_down > 0) {
                            count_down = count_down - 1;
                            txtRunningSeconds.setText(count_down + " " + getString(R.string.seconds_left));
                        }
                    }
                }
                seconds_handler.postDelayed(this, 1000);  //for interval...
            }
        };
        seconds_handler.postDelayed(seconds_runnable, 1000); //for initial delay.

        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        String deviceName = android.os.Build.MANUFACTURER + android.os.Build.BRAND + android.os.Build.MODEL;
        new WavLog("DEVICE-BRAND"+deviceName, getApplicationContext(), "MainActivity", "Device_Name");
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        totalram = mi.totalMem / 1048576L;
        availableMegs = mi.availMem / 1048576L;
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread thread, Throwable ex) {
                handleUncaughtException(thread, ex);
            }
        });
        picorientation = read_from_table("ORIENTATION");
        mOrientationEventListener = new OrientationEventListener(
                this, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {


                if (orientation >= 315 || orientation < 45) {  // portrait upright
                        mPicOrientation = Surface.ROTATION_90;
                        image_orientation = 0;
                } else if (orientation >= 45 && orientation < 135) {  // CW 90
                        mPicOrientation = Surface.ROTATION_270;
                        image_orientation = 90;
                } else if (orientation >= 135 && orientation < 225) { // portrait upside down
                        mPicOrientation = Surface.ROTATION_180;
                        image_orientation = 180;
                } else if (orientation >= 225 && orientation < 315) {
                        mPicOrientation = Surface.ROTATION_90;
                        image_orientation = 270;
                }
                Log.d("orientation_lister","val"+orientation+","+image_orientation);
            }
        };
        if (mOrientationEventListener.canDetectOrientation()) {
            mOrientationEventListener.enable();
        }

        String Zoomvalue = read_from_table("ZOOM_VALUE");
        Float zoom_final_val = Float.parseFloat(Zoomvalue) /100;
        zoom_val = zoom_final_val;
        check_file_length();

       /* dev_log_handler = new Handler();
        dev_log_runnable = new Runnable() {
            public void run() {
                if( read_from_table("IS_APP_INSTALLED").equals("1") ) {
                    //savelogdev();
                }
                dev_log_handler.postDelayed(this, 4000);
            }
        };
        dev_log_handler.postDelayed(dev_log_runnable, 3000); //for initial delay..*/
    }

    public void stop_audio_track() {
        if(audioTrack!=null) {
          if (audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
             audioTrack.stop();
          }
        }
        is_audio_stop = true;
        is_alarm_start = false;
    }

    public void check_file_length(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);
        if( mediaStorageDir.exists() ) {
            File files[];
            files = mediaStorageDir.listFiles();
            Log.d("Exists",mediaStorageDir.getAbsolutePath());
            if( read_from_table("IS_APP_INSTALLED").equals("1") ) {
                if (files.length < 0) {
                    pre_execute = 0;
                } else  {
                    pre_execute = files.length;
                }
            }
        }
    }

    public void delete_previous_ver_images() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);
        if (mediaStorageDir.exists()) {
            File files[];
            files = mediaStorageDir.listFiles();
            Log.d("previous_ver",String.valueOf(files.length));
            if (files != null) {
                if( files.length == 0 ) {
                    mediaStorageDir.delete();
                } else {
                    for(int i = 0; i < files.length; i++) {
                        String Name = files[i].getName();
                        String[] split_name = Name.split("-");
                        if( split_name.length == 7 ) {
                            String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
                            if (split_name[0].length() == 10 && split_name[1].equals(year)) {
                                files[i].delete();
                            }
                        }
                        if( i == files.length-1 ) {
                            mediaStorageDir.delete();
                        }
                    }
                }
            } else {
                mediaStorageDir.delete();
            }
        }
        File mediaStorageDir1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);
        if( mediaStorageDir1.exists() ) {
            File files1[];
            files1 = mediaStorageDir.listFiles();
            if( files1 != null ) {
                Log.d("previous_ver_after", String.valueOf(files1.length));
            } else {
                Log.d("previous_ver_after", "Dhinesh");
            }
        }

    }

    public void upload_folder_image() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);
        String camera_upload = read_from_table("CAMERA_UPLOADS");
        String orientation   = read_from_table("ORIENTATION");
        String image_count = read_from_table("IMAGE_COUNT");
        if( mediaStorageDir.exists() ) {
            File files[];
            files = mediaStorageDir.listFiles();
            if( files.length >= 1 ) {
                if( camera_upload.equals("1") && is_on_pause == false && files[files.length-1].length() > 0 ) {
                    File current_file = new File(files[files.length-1].getAbsolutePath());
                    if( current_file.canWrite() ) {
                        AsyncTaskUpload asyncTask = new AsyncTaskUpload();
                        asyncTask.setFilePath(files[files.length-1].getAbsolutePath(),orientation,image_orientation);
                        asyncTask.setUploadStatus(MainActivity.this, j);
                        asyncTask.execute();
                    }
                }

            }
        }
    }

    /*public void upload_all_images() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);
        String camera_upload = read_from_table("CAMERA_UPLOADS");
        String image_count = read_from_table("IMAGE_COUNT");
        //Log.d("upload_all_images",image_count+","+is_manullay_upload_images+","+check_upload_server);
        if( mediaStorageDir.exists() ) {
            File files[];
            files = mediaStorageDir.listFiles();
            if( files.length <= 2 ) {
                update_table("IMAGE_COUNT","0");
                is_manullay_upload_images = false;
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                //formatter.setTimeZone(TimeZone.getTimeZone("GMT-5"));
                startDate = formatter.format(new Date());
            }
            if( files.length >= 2 && is_manullay_upload_images == false && camera_upload.equals("1") && image_count.equals("0")  ) {
                is_manullay_upload_images = true;
                manually_upload_images( files.length, files,mediaStorageDir );
            }
        }
    }*/

    /*public void manually_upload_images( int images_length, File files[],File Dir ) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if( images_length > 1 ) {
                    fifo_image_length = images_length;
                    if (files[images_count].isFile()) {
                        try {
                            String orientation   = read_from_table("ORIENTATION");
                            String camera_upload = read_from_table("CAMERA_UPLOADS");
                            String name = files[images_count].getName();
                            File path = new File(Dir +"/"+name);
                            Log.d("dir_name","value"+path.exists());
                            int year = Calendar.getInstance().get(Calendar.YEAR);
                            String time = name.substring(name.indexOf(String.valueOf(year)), name.indexOf("."));
                            Log.d("manually_upload","val"+images_length+","+images_count);
                            if (startDate.compareTo(time) > 0 && path.exists()) {
                                new WavLog("Manually_upload_images"+name+"manually_images_count"+images_count+"total_count"+files.length, getApplicationContext(), "MainActivity", "upload_all_images");
                                if( camera_upload.equals("1") && is_on_pause == false && files[images_count].length() > 0 ) {
                                   // Log.d("manually_upload","val upload_images suceessfully"+files[images_count].getAbsolutePath());
                                    AsyncTaskUpload asyncTask = new AsyncTaskUpload();
                                    asyncTask.setFilePath(files[images_count].getAbsolutePath(),orientation,image_orientation);
                                    asyncTask.setUploadStatus(MainActivity.this, j);
                                    asyncTask.execute();
                                    fifo_image_length--;
                                }
                            }
                        } catch (Exception e) {
                            new WavLog("Manually_upload_images_error"+e.getMessage()+"manually_images_count"+files.length, getApplicationContext(), "MainActivity", "upload_all_images");
                        }
                    }
                } else {
                    images_count = 0;
                    fifo_image_length = 0;
                    is_manullay_upload_images = false;
                    update_table("IMAGE_COUNT","0");
                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                    //formatter.setTimeZone(TimeZone.getTimeZone("GMT-5"));
                    startDate = formatter.format(new Date());
                }
            }
        }).start();

    }*/




    public void takenpicture() {
        int taken_picture = parseInt(read_from_table("TAKEN_PICTURE"));
        if( is_camera_open == true && read_from_table("IS_APP_INSTALLED").equals("1") && is_on_pause == false ) {
            is_takenpicture = false;
            getLocationOnClick(imgcapture);
            new WavLog("Device_total_ram" +totalram+"MB"+ "avaliable_ram"+availableMegs+"MB", getApplicationContext(), "MainActivity", "camera_handler");
        }
    }


    public void touchDownEvent(final String mode){
        //Log.d("TouchTest", "Touch down11"+mode);
        new WavLog("downEvent_requestmode"+mode, getApplicationContext(), "MainActivity", "touchDownEvent");
        if(action_up_running==false) {
            //action_up_running = true;
            img_background.setImageDrawable(null);
            //img_background.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            is_on_touch_active = true;
            //milliSecondClock.setVisibility(View.VISIBLE);

            request_completed = false;

            milliSecondClock.setVisibility(View.VISIBLE);

            //Show running milli seconds while on touch is active
            milli_seconds_handler = new Handler();
            milli_seconds_runnable = new Runnable() {
                int total_milli_seconds = 2000;

                public void run() {

                    if (request_completed == false) {
                        //total_milli_seconds = 2000;

                        //2000 milli seconds count down is only for touch event
                        if(mode == "touch_mode") {
                            if ((total_milli_seconds > 0) && (is_on_touch_active == true)) {
                                total_milli_seconds = (total_milli_seconds - 20);
                                total_milli_seconds_running = total_milli_seconds;
                                //Log.d("test", total_milli_seconds + "");
                                milliSecondClock.setText(total_milli_seconds + "");
                            }
                        } else {
                            total_milli_seconds = 0;
                        }

                        if (total_milli_seconds == 0) {
                            is_previous_request_done = 0;
                            if (is_previous_request_done == 0) {
                                //sfnd #4852 - #26824
                                //if (is_image_clicked == 1) {
                                if (get_status_of_files() == 2) {
                                    update_footer_data(mode);
                                    request_completed = true;
                                    milli_seconds_handler.removeCallbacks(milli_seconds_runnable);
                                }
                                //}
                            }
                        }
                    }
                    //Log.d("milli", "Milli Seconds: "+(total_milli_seconds));

                    milli_seconds_handler.postDelayed(this, 1);  //for interval...
                }
            };
            milli_seconds_handler.postDelayed(milli_seconds_runnable, 1); //for initial delay..
            //

        }
    }

    public void touchUpEvent(String mode){
        new WavLog("upevent_requestMode"+mode, getApplicationContext(), "MainActivity", "touchUpEvent");
        if(mode=="click_mode"){
            if(total_milli_seconds_running>0){
                total_milli_seconds_running = 0;
            }
        }

        //Log.d("TouchTest", "Touch up "+total_milli_seconds_running);
        if(total_milli_seconds_running>0) {
            is_on_touch_active = false;
            milliSecondClock.setVisibility(View.INVISIBLE);
            milli_seconds_handler.removeCallbacks(milli_seconds_runnable);
        } else {
            if (action_up_running == false) {
                action_up = false;
                action_up_running = true;

                minute_handler = new Handler();
                minute_runnable = new Runnable() {
                    int total_seconds = white_tick_active_seconds;

                    public void run() {
                        if (action_up == false) {
                            //Log.d("aab", "aab " + total_seconds);

                            if ((total_seconds > 0)) {
                                total_seconds = (total_seconds - 1);
                                milliSecondClock.setText(total_seconds + "");
                            }

                            if ((total_seconds == 0)) {

                                is_on_touch_active = false;
                                milliSecondClock.setVisibility(View.INVISIBLE);
                                milli_seconds_handler.removeCallbacks(milli_seconds_runnable);
                                white_tick.setVisibility(View.INVISIBLE);
                                action_up = true;
                                total_seconds = white_tick_active_seconds;
                                minute_handler.removeCallbacks(minute_runnable);
                                action_up_running = false;
                                stop_audio_track();
                                //takenpicture();
                            }
                        }

                        minute_handler.postDelayed(this, 1000);  //for interval...
                    }
                };
                minute_handler.postDelayed(minute_runnable, 1000); //for initial delay..
            }
        }
    }

    //Check if location is enabled in mobile
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.enable_gps_setting))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes_btn), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        Intent i = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(i);
                    }
                })
                .setNegativeButton(getString(R.string.no_btn), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        new WavLog("init", getApplicationContext(), "MainActivity", "onRequestPermissionsResult");

        new WavLog("requestCode: "+requestCode, getApplicationContext(), "MainActivity", "onRequestPermissionsResult");



        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                new WavLog("case1", getApplicationContext(), "MainActivity", "onRequestPermissionsResult");
                if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED
                        && grantResults[0] == PERMISSION_GRANTED
                        && grantResults[1] == PERMISSION_GRANTED
                        && grantResults[2] == PERMISSION_GRANTED
                        && grantResults[3] == PERMISSION_GRANTED
                        && grantResults[4] == PERMISSION_GRANTED
                ) {
                    new WavLog("init_startLocation", getApplicationContext(), "MainActivity", "onRequestPermissionsResult");

                    new WavLog("grantResults[0]: "+grantResults[0], getApplicationContext(), "MainActivity", "onRequestPermissionsResult");

                    new WavLog("PackageManager.PERMISSION_GRANTED: "+PackageManager.PERMISSION_GRANTED, getApplicationContext(), "MainActivity", "onRequestPermissionsResult");
                    if( read_from_table("IS_APP_INSTALLED").equals("0")) {
                        delete_previous_ver_images();
                    }
                    this.startLocation(false);
                    stop_count_seconds = false;
                }
            }


            default: {

                new WavLog("default: grantResults[0]: "+grantResults[0], getApplicationContext(), "MainActivity", "onRequestPermissionsResult");

                new WavLog("default: PackageManager.PERMISSION_GRANTED: "+PackageManager.PERMISSION_GRANTED, getApplicationContext(), "MainActivity", "onRequestPermissionsResult");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Log.d("launching","on_resume");
        new WavLog("onResume: "+PackageManager.PERMISSION_GRANTED, getApplicationContext(), "MainActivity", "onResume");
    }

    private void startCamera() {


        String orientation   = read_from_table("ORIENTATION");
        Log.d("screen_orientation",orientation);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    cameraProvider.unbindAll();

                        imageCapture = new Builder()
                                .setCaptureMode(CAPTURE_MODE_MINIMIZE_LATENCY)
                                .setTargetResolution(new Size(textureView.getWidth(),textureView.getHeight()))
                                .build();


                    bind(cameraProvider);

                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(this));
        //String flash_light = read_from_table("FLASH_LIGHT");
        /*CameraX.unbindAll();

        Rational aspectRatio = new Rational (textureView.getWidth(), textureView.getHeight());
        Size screen = new Size(textureView.getWidth(), textureView.getHeight()); //size of the screen

        if( camera_info.equals("1")) {
            PreviewConfig pConfig = new PreviewConfig.Builder().setLensFacing(CameraX.LensFacing.FRONT).setTargetResolution(screen).build();
            preview = new Preview(pConfig);
        } else {
            PreviewConfig pConfig = new PreviewConfig.Builder().setTargetResolution(screen).build();
            preview = new Preview(pConfig);
        }


        preview.setOnPreviewOutputUpdateListener(
                new Preview.OnPreviewOutputUpdateListener() {
                    //to update the surface texture we  have to destroy it first then re-add it
                    @Override
                    public void onUpdated(Preview.PreviewOutput output){
                        ViewGroup parent = (ViewGroup) textureView.getParent();
                        parent.removeView(textureView);
                        parent.addView(textureView, 0);

                        textureView.setSurfaceTexture(output.getSurfaceTexture());
                        updateTransform();
                    }
                });
        if( camera_info.equals("1") ) {
            ImageCaptureConfig imageCaptureConfig = new ImageCaptureConfig.Builder().setLensFacing(CameraX.LensFacing.FRONT).setTargetResolution(screen).setCaptureMode(ImageCapture.CaptureMode.MAX_QUALITY)
                    .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation()).build();
            imgCap = new ImageCapture(imageCaptureConfig);
        } else {
            ImageCaptureConfig imageCaptureConfig = new ImageCaptureConfig.Builder().setTargetResolution(screen).setCaptureMode(ImageCapture.CaptureMode.MAX_QUALITY)
                    .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation()).build();
            imgCap = new ImageCapture(imageCaptureConfig);
        }



        CameraX.bindToLifecycle((LifecycleOwner)this, preview, imgCap);*/


        is_camera_open = true;

    }

    private void bind(@NonNull ProcessCameraProvider cameraProvider) {

        String camera_info = read_from_table("CAMERA_INFO");
        String orientation = read_from_table("ORIENTATION");
        Preview preview = new Preview.Builder().build();
        Log.d("screen_value","value"+hor_orientation+mPicOrientation);
        if( camera_info.equals("1") ) {
           cameraSelector = new CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_FRONT).build();
        } else {
            cameraSelector = new CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        }
        preview.setSurfaceProvider(mpreviewView.createSurfaceProvider());

        cam = cameraProvider.bindToLifecycle((LifecycleOwner)this,
                cameraSelector,
                preview,
                imageCapture);

        if( zoom_val != 0.0 ) {
            CameraControl control = cam.getCameraControl();
            CameraInfo info = cam.getCameraInfo();
            LiveData<ZoomState> zoomstate = info.getZoomState();
            Log.d("zoom_state", String.valueOf(zoom_val));
            control.setLinearZoom((float) zoom_val);
        }

    }

       private void startLocation(boolean is_on_resume) {
        //Show the latlng dialog only when there is no lat and lng saved in DB
        Double gps_lat_info = Double.parseDouble(read_from_table("GPS_LAT"));
        if(gps_lat_info<=0) {
            if (is_on_resume == false) {
                update_latlng_dialog = true;
                //Show popup to list the location details for each location change
                dialogLatLng = new LatLngDialog(MainActivity.this);
                dialogLatLng.setCancelable(false);
                dialogLatLng.show(getSupportFragmentManager(), "LatLng dialog");
                //
            }
        }


        new WavLog("startLocation: "+PackageManager.PERMISSION_GRANTED, getApplicationContext(), "MainActivity", "startLocation");
        try {
            new WavLog("try", getApplicationContext(), "MainActivity", "startLocation");
            if (manager.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
                new WavLog("GPS_PROVIDER", getApplicationContext(), "MainActivity", "startLocation");
            }
            /*if (manager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) {
                manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locListener);
                new WavLog("NETWORK_PROVIDER", getApplicationContext(), "MainActivity", "startLocation");
            }*/
        } catch (SecurityException e) {
            new WavLog("catch: "+e.getMessage(), getApplicationContext(), "MainActivity", "startLocation");
        }

    }

    //Function to get the center location among multiple locations
    public String GetCenterFromDegrees(Stack<String> data) {
        int num_coords = data.size();

        double X = 0.0;
        double Y = 0.0;
        double Z = 0.0;
        double lat = 0.0;
        double lng = 0.0;

        Iterator value = data.iterator();


        while (value.hasNext()) {
            {
                String coord_obj = value.next() + "";
                String coord[] = coord_obj.split(",");

                double coord_lat = Double.parseDouble(coord[0]);
                double coord_lng = Double.parseDouble(coord[1]);

                lat = (coord_lat * Math.PI) / 180;
                lng = (coord_lng * Math.PI) / 180;

                double a = Math.cos(lat) * Math.cos(lng);
                double b = Math.cos(lat) * Math.sin(lng);
                double c = Math.sin(lat);

                X += a;
                Y += b;
                Z += c;
            }

            X = X / num_coords;
            Y = Y / num_coords;
            Z = Z / num_coords;

            lng = Math.atan2(Y, X);
            double hyp = Math.sqrt(X * X + Y * Y);
            lat = Math.atan2(Z, hyp);

        }
        return lat * 180 / Math.PI + "," + lng * 180 / Math.PI;
    }

    public void startSearchLocation(boolean is_restart){
        is_search_location = true;
        difference_In_Minutes = 0;
        start_time = "";
        location_arr = new Stack<String>();
        if(is_restart==true) {
            location_arr2 = new Stack<String>();
            is_restart_location_search = true;
        }
        count_down = count_down_seconds;
        stop_count_seconds = false;
        new WavLog("startSearchLocation", getApplicationContext(), "MainActivity", "startSearchLocation");
    }

    //Refer: https://www.calculatorsoup.com/calculators/statistics/variance-calculator.php
	/*
    Calculate distance between last two locations to detect,
    if device is moving fast (like moving in car or bus)
    */
    public String calc_variance(Stack<String> location_arr){

        Stack<Double> distance_arr_lat = new Stack<Double>();
        Stack<Double> distance_arr_lng = new Stack<Double>();
        double total_distance_meter_lat = 0;
        double total_distance_meter_lng = 0;
        int total_distance_count_lat = 0;
        int total_distance_count_lng = 0;
        double mean_lat = 0;
        double mean_lng = 0;
        double squares = 0;
        double squares_sum = 0;
        double variance = 0;

        Object location_obj[] = location_arr.toArray();

        for(int i=0;i<(location_obj.length);i++) {
            String tmp_loc = location_obj[i].toString();
            String tmp_loc_arr[] = tmp_loc.split(",");

            double lat = Double.parseDouble(tmp_loc_arr[0]);
            double lng = Double.parseDouble(tmp_loc_arr[1]);

            distance_arr_lat.push(lat);
            total_distance_meter_lat = total_distance_meter_lat + lat;

            distance_arr_lng.push(lng);
            total_distance_meter_lng = total_distance_meter_lng + lng;
        }

        Object distance_obj_lat[] = distance_arr_lat.toArray();
        total_distance_count_lat = distance_obj_lat.length;
        mean_lat = total_distance_meter_lat / total_distance_count_lat;

        Object distance_obj_lng[] = distance_arr_lng.toArray();
        total_distance_count_lng = distance_obj_lng.length;
        mean_lng = total_distance_meter_lng / total_distance_count_lng;

        return mean_lat+","+mean_lng;
    }

    // Location events (we use GPS only)
    private LocationListener locListener = new LocationListener() {


        public void onLocationChanged(Location argLocation) {
            Location loc = argLocation;

            new WavLog("start", getApplicationContext(), "MainActivity", "onLocationChanged");

            new WavLog("getProvider: "+loc.getProvider(), getApplicationContext(), "MainActivity", "onLocationChanged");

            //Log.d("sfnd", "onLocationChanged");

            if (loc != null) {
                if (loc.getProvider().equals("gps")) {
                    new WavLog("if gps", getApplicationContext(), "MainActivity", "onLocationChanged");
                    final String la = String.format(Locale.US, "%2.9f", loc.getLatitude());
                    final String lo = String.format(Locale.US, "%3.9f", loc.getLongitude());

                    //Log.d("sfnd", "Update the current location");


                    //Append the lat and lng in LatLng Dialog table
                    if(update_latlng_dialog==true) {
                        location_arr2.push(la + "," + lo);
                        //Log.d("sfnd12345", "length: "+location_arr2.toArray().length);
                        dialogLatLng.updateTextValue(la + "," + lo, location_arr2, is_restart_location_search);
                        is_restart_location_search = false;
                    }
                    //


                    new WavLog("la: "+la+", lo: "+lo, getApplicationContext(), "MainActivity", "onLocationChanged");

                    //Update the current location every second
                    //Log.d("sfnd-count", current_location_arr.size()+"");
                    if(current_location_arr.size()>60){
                        current_location_arr.clear();
                    }
                    //Log.d("current_location_arr", current_location_arr+"");
                    current_location_arr.push(la + "," + lo);
                    String current_center_location = calc_variance(current_location_arr);
                    String[] tmp_current_center_location_arr = current_center_location.split(",");
                    update_table("CURRENT_GPS_LAT", tmp_current_center_location_arr[0]);
                    update_table("CURRENT_GPS_LONG", tmp_current_center_location_arr[1]);

                    //Search the location only for first time when app is installed
                    final String gps_lat_info = read_from_table("GPS_LAT");
                    final String gps_long_info = read_from_table("GPS_LONG");

                    if( gps_lat_info.equals("0") || gps_long_info.equals("0") ) {
                        //Log.d("sfnd", "device location not exists");
                        //Log.d("sfnd", "before is_search_location");
                        if (is_search_location == true) {

                            new WavLog("inside is_search_location", getApplicationContext(), "MainActivity", "onLocationChanged");

                           //Log.d("sfnd", "inside is_search_location");

                            final DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
                            final Calendar cal = Calendar.getInstance();
                            final DateFormat dateTimeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

                            String current_time = dateTimeFormat.format(cal.getTime());

                            /*try {
                                if (start_time.isEmpty() == false) {
                                    Log.d("sfnd", "start_time not empty");
                                    Date t1 = dateTimeFormat.parse(start_time);
                                    Date t2 = dateTimeFormat.parse(current_time);
                                    long difference_In_Time = t2.getTime() - t1.getTime();
                                    difference_In_Minutes = (difference_In_Time / (1000 * 60)) % 60;
                                }
                            } catch (ParseException e) {

                            }*/
                            //Log.d("sfnd", "count_down.push = "+count_down);

                            new WavLog("count_down: "+count_down, getApplicationContext(), "MainActivity", "onLocationChanged");

                            if (count_down!=0) {
                                location_arr.push(la + "," + lo);

                                new WavLog("count_down_if_not_zero", getApplicationContext(), "MainActivity", "onLocationChanged");

                                //Log.d("sfnd", "location_arr = "+location_arr);

                                /*new Thread(new Runnable() {
                                    public void run() {
                                        if (location_arr.size() == 0) {
                                            start_time = dateTimeFormat.format(cal.getTime());
                                            Log.d("sfnd", "set start_time");
                                        }

                                        location_arr.push(la + "," + lo);
                                    }
                                }).start();*/

                            } else {

                                //Added locations list popup to select the location, so commented out these codes
                                /*
                                double variance = calc_variance(location_arr);
                                new WavLog("location_arr: "+location_arr, getApplicationContext(), "MainActivity", "onLocationChanged");
                                new WavLog("variance: "+variance, getApplicationContext(), "MainActivity", "onLocationChanged");

                                //Check if device is moving
                                if(variance>device_moving_speed) {
                                    new WavLog("device moving", getApplicationContext(), "MainActivity", "onLocationChanged");
                                    open_device_moving_dialog();
                                    //Stop the location search if detected the device moving
                                    is_search_location = false;
                                } else {
                                    String center_location = GetCenterFromDegrees(location_arr);
                                    Log.d("sfnd", "found center_location = "+center_location);

                                    new WavLog("found center_location: "+center_location, getApplicationContext(), "MainActivity", "onLocationChanged");

                                    open_location_dialog(center_location, MainActivity.this);
                                    is_search_location = false;
                                    stop_count_seconds = true;
                                }
                                */



                            }
                        }
                    } else {
                        //Log.d("sfnd", "device location exists "+gps_lat_info+", "+gps_long_info);

                        new WavLog("device location exists: "+gps_lat_info+", "+gps_long_info, getApplicationContext(), "MainActivity", "onLocationChanged");


                    }
                }
            } else {
                new WavLog("device location not exists:" , getApplicationContext(), "MainActivity", "onLocationChanged");
            }
        }

        /*
        public void onLocationChanged(Location argLocation) {
            Location loc = argLocation;
            if (loc != null) {

                String gps_lat = String.format(Locale.US, "%2.9f", loc.getLatitude());
                String gps_long = String.format(Locale.US, "%3.9f", loc.getLongitude());



                //new WavLog(getApplicationContext(), "", "",  "getAccuracy: "+loc.getAccuracy() );

                if(loc.getProvider().equals("gps")) {
                    //if(loc.getAccuracy()<=ACCURACY)
                    {
                        //Update the current location every second
                        update_table("CURRENT_GPS_LAT", gps_lat);
                        update_table("CURRENT_GPS_LONG", gps_long);

                        //Update the init location
                        String db_gps_lat_info = read_from_table("GPS_LAT");
                        if(db_gps_lat_info.equals("0") && loc.getProvider().equals("gps")) {
                            update_table("GPS_LAT", gps_lat);
                            update_table("GPS_LONG", gps_long);
                        }
                    }
                }

            } else {

            }
        }*/

        @Override
        public void onProviderDisabled(String arg0) {
            new WavLog("onProviderDisabled", getApplicationContext(), "MainActivity", "onProviderDisabled");
        }

        @Override
        public void onProviderEnabled(String arg0) {
            new WavLog("onProviderEnabled", getApplicationContext(), "MainActivity", "onProviderEnabled");
        }

        @Override
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
            new WavLog("onStatusChanged", getApplicationContext(), "MainActivity", "onStatusChanged");
        }
    };

    public void open_browser_info() {
        String footer_text = footerText.toString();
        if( !footer_text.trim().equals("null") ) {
            if ( footer_text.trim() != "Processing..." || footer_text.trim() != "Procesando..." ) {
                if (!footer_hyper_link.isEmpty()) {
                    Log.d("hyper_link","link"+Uri.parse(footer_hyper_link));
                    footerText.setTextColor(getResources().getColor(R.color.colorHyperLink));
                    footerText.setPaintFlags(footerText.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(footer_hyper_link));
                    startActivity(intent);
                }
            }
        }
    }



    public String extractMessage(String response, String value){

        String[] response_split = response.split("&");

        for(String element:response_split){
            String[] element_array = element.split("=");

            if(element_array[0].equals(value)){
                return element_array[1];
            }
        }
        return "";
    }

    public void check_upload_request(String id) {
        if(!checkInternet(getApplicationContext())) {
            Toast.makeText(getApplicationContext(),R.string.network_not_available, Toast.LENGTH_SHORT).show();
            return;
        }
        final String gps_lat_info = read_from_table("GPS_LAT");
        final String gps_long_info = read_from_table("GPS_LONG");
        String last_image_date_time = read_from_table("LAST_IMAGE_DATE_TIME");
        final String imei_info = read_from_table("IMEI");
        if( id == "event_10") {
            upload_track_id = "10"+","+last_image_date_time+","+gps_lat_info+","+gps_long_info;
        } else if( id == "event_11" ) {
            upload_track_id = "11"+","+last_image_date_time+","+gps_lat_info+","+gps_long_info;
        }

        Log.d(" upload_track_id"," upload_track_id: "+ upload_track_id );


        if(imei_info == "0" || gps_lat_info == "0" || gps_long_info == "0") {
            Toast.makeText(getApplicationContext(),"IMEI is null",Toast.LENGTH_SHORT).show();
            return;
        }

        if (!check_distance(gps_lat_info, gps_long_info)) {
            Toast.makeText(getApplicationContext(), R.string.device_position_alert, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if( check_upload_request_Queue == null ) {
                        check_upload_request_Queue = Volley.newRequestQueue(MainActivity.this);
                    }
                    String url = IMEI_URL_PATH;
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("start_message",response);
                            new WavLog("check_upload_response: "+response+"tracking_id"+ upload_track_id, getApplicationContext(), "MainActivity", "check_upload_request_queue");



                            final String[] response_split = response.split("&b=");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                }
                            });
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "response_error: "+error, Toast.LENGTH_LONG).show();
                        }
                    }){
                        protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("imei",imei_info+","+gps_lat_info+","+gps_long_info+","+ upload_track_id);
                            return params;
                        };
                    };
                    check_upload_request_Queue.add(stringRequest);
                } catch(final Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new WavLog("check_upload_response: Error"+e.getMessage()+"tracking_id"+ upload_track_id, getApplicationContext(), "MainActivity", "check_upload_request_queue");
                        }
                    });
                }
            }
        }).start();
    }


    public void savelogdev() {

        if( read_from_table("IS_APP_INSTALLED").equals("1") ) {

            Log.d("log_response", "value_response");
            if (!checkInternet(getApplicationContext())) {
                Toast.makeText(getApplicationContext(), R.string.network_not_available, Toast.LENGTH_SHORT).show();
                return;
            }
            final String gps_lat_info = read_from_table("GPS_LAT");
            final String gps_long_info = read_from_table("GPS_LONG");
            String last_image_date_time = read_from_table("LAST_IMAGE_DATE_TIME");
            final String imei_info = read_from_table("IMEI");
            float result = 0;
            int file_length = 0;
            String devlog;
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            //formatter.setTimeZone(TimeZone.getTimeZone("GMT-5"));
            String timeStamp = formatter.format(new Date());
            File paths = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);
            if (paths.exists()) {
                File files[];
                files = paths.listFiles();
                if (files != null) {
                    for (int i = 0; i < files.length; i++) {
                        result += files[i].length();
                    }
                }
                file_length = files.length;
            }

            devlog = event_id + "," + totalram + "," + availableMegs + "," + timeStamp + "," + batterylevel + "," + String.valueOf(file_length) + "," + result / 1000000 + "," + checkInternet(getApplicationContext()) + "," + post_execute;

            if (imei_info == "0" || gps_lat_info == "0" || gps_long_info == "0") {
                Toast.makeText(getApplicationContext(), "IMEI is null", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!check_distance(gps_lat_info, gps_long_info)) {
                Toast.makeText(getApplicationContext(), R.string.device_position_alert, Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (dev_log_Queue == null) {
                            dev_log_Queue = Volley.newRequestQueue(MainActivity.this);
                        }
                        String url = "https://devi.softound.com/mxc4sfnd/devlog.php";
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                new WavLog("check_evnent_nine_response: " + response + "tracking_id" + track_id, getApplicationContext(), "MainActivity", "check_event_nine");
                                Log.d("log_response", response);

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), "response_error: " + error, Toast.LENGTH_LONG).show();
                            }
                        }) {
                            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("imei", imei_info + "," + gps_lat_info + "," + gps_long_info + "," + devlog);
                                return params;
                            }

                            ;
                        };
                        dev_log_Queue.add(stringRequest);
                    } catch (final Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new WavLog("check_event_nine_response: Error" + e.getMessage() + "tracking_id" + track_id, getApplicationContext(), "MainActivity", "check_event_nine");
                            }
                        });
                        Log.d("time_check_event_nine", e.getMessage() + track_id);
                    }
                }
            }).start();
        }
    }

    public void check_event_nine( String id ) {
        if(!checkInternet(getApplicationContext())) {
            Toast.makeText(getApplicationContext(),R.string.network_not_available, Toast.LENGTH_SHORT).show();
            return;
        }
        event_id = id;
        final String gps_lat_info = read_from_table("GPS_LAT");
        final String gps_long_info = read_from_table("GPS_LONG");
        String last_image_date_time = read_from_table("LAST_IMAGE_DATE_TIME");
        final String imei_info = read_from_table("IMEI");
        String deviceName = android.os.Build.BRAND + android.os.Build.MODEL;
        float result = 0;
        File paths = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);
        if( paths.exists() ) {
            File files[];
            files = paths.listFiles();
            if (files != null) {
                for(int i = 0; i < files.length; i++) {
                    result += files[i].length();
                }
            }
            pre_execute = files.length;
        }
        if( id == "upload_images" ) {
            track_id = "9"+","+last_image_date_time+","+gps_lat_info+","+gps_long_info+","+batterylevel+","+String.valueOf(pre_execute)+","+result/1000000+","+check_power_status+","+batteryTemp+"°C"+","+deviceName+","+boton.c4.pisa.fmns.BuildConfig.VERSION_NAME;
            result = 0;
        }

        Log.d("track_id","track_id: "+track_id );


        if(imei_info == "0" || gps_lat_info == "0" || gps_long_info == "0") {
            Toast.makeText(getApplicationContext(),"IMEI is null",Toast.LENGTH_SHORT).show();
            return;
        }

        if (!check_distance(gps_lat_info, gps_long_info)) {
            Toast.makeText(getApplicationContext(), R.string.device_position_alert, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if( check_evnt_nine_Queue == null ) {
                        check_evnt_nine_Queue = Volley.newRequestQueue(MainActivity.this);
                    }
                    //String url = IMEI_URL_PATH;
                    String url = IMEI_URL_PATH;

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("start_message",response);
                            String final_response = "";
                            String[] arrOfStr = response.split("");
                            if(  !arrOfStr[0].equals('h') ) {
                                event_nine = arrOfStr[0]+arrOfStr[1];
                            }

                            if( !arrOfStr[2].equals("t") ) {
                                update_table("UPLOAD_IMAGES",arrOfStr[2]);
                            }

                            String url_text = read_from_table("URL_STRING");
                             for(int i =3;i<arrOfStr.length;i++) {
                                 final_response+=arrOfStr[i];
                             }

                            new WavLog("check_evnent_nine_response: "+response+"tracking_id"+track_id, getApplicationContext(), "MainActivity", "check_event_nine");
                            if( !response.equals("ok")  && !url_text.equals(final_response)) {
                                update_table("URL_STRING",final_response);
                            }

                            if( extractMessage(response,"b").equals("URL-Imagenes") ) {
                                String c = response.substring(response.indexOf("&c=")+3,response.indexOf("&d="));
                                String d = response.substring(response.indexOf("&d=")+3);
                                update_table("TAKEN_PICTURE",c);
                                update_table("UPLOAD_IMAGES",d);

                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "response_error: "+error, Toast.LENGTH_LONG).show();
                        }
                    }){
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("imei",imei_info+","+gps_lat_info+","+gps_long_info+","+track_id);
                            return params;
                        };
                    };
                    check_evnt_nine_Queue.add(stringRequest);
                } catch(final Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new WavLog("check_event_nine_response: Error"+e.getMessage()+"tracking_id"+track_id, getApplicationContext(), "MainActivity", "check_event_nine");
                        }
                    });
                    Log.d("time_check_event_nine", e.getMessage()+track_id);
                }
            }
        }).start();
    }


    public void update_footer_data(String id) {
        if(!checkInternet(getApplicationContext())) {
            Toast.makeText(getApplicationContext(),R.string.network_not_available, Toast.LENGTH_SHORT).show();
            return;
        }



        is_previous_request_done = 1;
        event_id = id;
        final String circuit_request_closed = read_from_table("CIRCUIT_REQUEST_CLOSED");
        final String imei_info = read_from_table("IMEI");
        final String gps_lat_info = read_from_table("GPS_LAT");
        final String gps_long_info = read_from_table("GPS_LONG");
        String last_image_date_time = read_from_table("LAST_IMAGE_DATE_TIME");
        String camera_upload = read_from_table("CAMERA_UPLOADS");

        //Log.d("circuit_read",circuit_request_closed);
        //Log.d("last_image",last_image_date_time);

        if( camera_upload.equals("0") ) {
            last_image_date_time = "0";
        }

        if( id == "touch_mode" ) {
            tracking_id = "1"+","+last_image_date_time+","+gps_lat_info+","+gps_long_info;
            is_upload_clickable = false;
            generate_pure_tone(1500,8.0);
        } else if(id == "settings_save_mode") {
            tracking_id = "6" + "," + last_image_date_time+","+gps_lat_info+","+gps_long_info;
            is_alarm_start = true;
            is_upload_clickable = false;
        } else if(id == "code_verific_accepted"){
           tracking_id = "4"+","+last_image_date_time+","+gps_lat_info+","+gps_long_info;
            is_upload_clickable = false;
        } else if(id == "time_expired") {
            tracking_id = "5" + "," + last_image_date_time+","+gps_lat_info+","+gps_long_info;
            is_upload_clickable = false;
        } else if(id == "waiting_popup_open"){
            tracking_id = "3"+","+last_image_date_time+","+gps_lat_info+","+gps_long_info;
            is_upload_clickable = false;
        } else if( (id == "click_mode" ) && (circuit_request_closed.equals("0")) ) {
            tracking_id = "2"+","+last_image_date_time+","+gps_lat_info+","+gps_long_info;
            is_upload_clickable = false;
        } else if( (id == "click_mode") && (circuit_request_closed.equals("1")) ) {
             tracking_id = "5"+","+last_image_date_time+","+gps_lat_info+","+gps_long_info;
             update_table("CIRCUIT_REQUEST_CLOSED","0");
            is_upload_clickable = false;
        } else if(id == "power_disconnectd"){
            tracking_id = "7"+","+last_image_date_time+","+gps_lat_info+","+gps_long_info;
            is_upload_clickable = false;
        } else if( id == "chargerLevel" ) {
            tracking_id = "8"+","+last_image_date_time+","+gps_lat_info+","+gps_long_info;
            is_upload_clickable = false;
        } else if( id=="event_12" ) {
            tracking_id = "12"+","+last_image_date_time+","+gps_lat_info+","+gps_long_info;
        }

        Log.d("tracking_id","tracking_id: "+tracking_id );


        if(imei_info == "0" || gps_lat_info == "0" || gps_long_info == "0") {
            Toast.makeText(getApplicationContext(),"IMEI is null",Toast.LENGTH_SHORT).show();
            return;
        }

        if (!check_distance(gps_lat_info, gps_long_info)) {
            Toast.makeText(getApplicationContext(), R.string.device_position_alert, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        String response_status = read_from_table("RESPONSE_STATUS");
        footerText.setText(R.string.processing);
        footerText.setPaintFlags(0);
        footerText.setTextColor(getResources().getColor(R.color.colorBlack));
            //upload_images_handler.removeCallbacks(upload_images_runnable);


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if( requestQueue == null ) {
                        requestQueue = Volley.newRequestQueue(MainActivity.this);
                    }
                    String url = IMEI_URL_PATH;
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("start_message",response);
                            new WavLog("update_footer_data_response: "+response+"tracking_id"+tracking_id, getApplicationContext(), "MainActivity", "update_footer_data");

                            //stop_audio_track();

                            final String[] response_split = response.split("&b=");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    footerText.setText(extractMessage(response,"b"));
                                    footerText.setTextColor(getResources().getColor(R.color.colorHyperLink));
                                    footerText.setPaintFlags(footerText.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
                                    footer_hyper_link = response_split[0];
                                    is_previous_request_done = 0;

                                }
                            });


                            if( read_from_table("IS_APP_INSTALLED").equals("1") ) {
                                int upload_images = parseInt(read_from_table("UPLOAD_IMAGES"));
                            }
                            if( extractMessage(response,"b").equals("REGISTRA-TU-ESCUELA") ) {
                                is_charger_level = false;
                            } else if( id == "chargerLevel" ) {
                                is_charger_level = false;
                            }

                            //Thread.sleep(1000);
                            if( is_alarm_start == true  ) {
                                //String response1 = "https%3A%2F%2Fmx911.org%2Fbotonc4_senl.php%3Feste%3D2517010572%26b%3DREGISTRA-TU-ESCUELA%26c%3Dclick_mode_test%26d%3D8072723036%2C8940722636%26e%3Dsecond_mesage";
                                //String response2 = URLDecoder.decode(response1, "UTF-8");
                                msg_response = response;
                            }
                            if( is_audio_stop == true && read_from_table("IS_APP_INSTALLED").equals("1") && !extractMessage(response,"b").equals("REGISTRA-TU-ESCUELA")) {
                                is_audio_stop = false;
                                sendSMS( msg_response );
                                sendSMS( response );
                            } else if( is_alarm_start == false && is_audio_stop == false && read_from_table("IS_APP_INSTALLED").equals("1") && !extractMessage(response,"b").equals("REGISTRA-TU-ESCUELA")) {
                                //String response1 = "https://mx911.org/botonc4_senl.php?este=2517010572&b=REGISTRA-TU-ESCUELA&c=touch_mode_test&d=8072723036,8940722636";
                                sendSMS( response );
                            }
                            is_upload_clickable = true;

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "response_error: "+error, Toast.LENGTH_LONG).show();
                        }
                    }){
                        protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("imei",imei_info+","+gps_lat_info+","+gps_long_info+","+tracking_id);
                            return params;
                        };
                    };
                    requestQueue.add(stringRequest);
                    /*BufferedReader reader = null;
                    URL url;

                    // Defined URL  where to send data
                    url = new URL(IMEI_URL_PATH);

                    // Send POST data request
                    URLConnection conna = url.openConnection();
                    conna.setDoOutput(true);
                    conna.setConnectTimeout(CONNECTION_TIMEOUT_SECONDS);
                    OutputStreamWriter wr = new OutputStreamWriter(conna.getOutputStream());
                    new WavLog("update_footer_data_request: "+IMEI_URL_PATH+"?"+"imei=" +imei_info+","+gps_lat_info+","+gps_long_info+","+tracking_id, getApplicationContext(), "MainActivity", "update_footer_data");

                    wr.write("imei=" +imei_info+","+gps_lat_info+","+gps_long_info+","+tracking_id);
                    wr.flush();
                    //Log.d("start_message",is_audio_stop+","+is_alarm_start);
                    // Get the server response
                    reader = new BufferedReader(new InputStreamReader(conna.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        // Append server response in string
                        sb.append(line + "\n");
                    }

                    final String response = sb.toString().trim();*/


                } catch(final Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new WavLog("update_footer_data_response: Error"+e.getMessage()+"tracking_id"+tracking_id, getApplicationContext(), "MainActivity", "update_footer_data");
                            //footerText.setText(e.getMessage());
                        }
                    });
                    Log.d("time_update_footer", e.getMessage()+tracking_id);


                }
            }
        }).start();

    }

    public boolean check_distance(String gps_lat_info, String gps_long_info){
        return true;
        //Commented the following code as per client request
        /*Double _gps_lat_info = Double.parseDouble(gps_lat_info);
        Double _gps_long_info = Double.parseDouble(gps_long_info);
        String current_gps_lat_info = read_from_table("CURRENT_GPS_LAT");
        String current_gps_long_info = read_from_table("CURRENT_GPS_LONG");

        if(!current_gps_lat_info.equals("0")) {

            Double _current_gps_lat_info = Double.parseDouble(current_gps_lat_info);
            Double _current_gps_long_info = Double.parseDouble(current_gps_long_info);
            Double total_distance = distance(_gps_lat_info, _gps_long_info, _current_gps_lat_info, _current_gps_long_info, "K");
            float total_distance_in_meter = (float)(total_distance*1000);

            //Toast.makeText(getApplicationContext(), "Distance: "+total_distance_in_meter, Toast.LENGTH_LONG).show();

            //50 meter radious
            if(total_distance_in_meter<=RADIUS_RANGE){
                return true;
            } else {
                return false;
            }
        }
        return false;*/
    }

    public double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        }
        else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            if (unit.equals("K")) {
                dist = dist * 1.609344;
            } else if (unit.equals("N")) {
                dist = dist * 0.8684;
            }
            return (dist);
        }
    }

    private  ArrayList<String> get_select_image_gallery(MainActivity mainActivity) {

        Uri uri;
        Cursor mImagecursor;
        int column_index_data,column_index_folder_name;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Images.Media.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
        };

        mImagecursor = mainActivity.getContentResolver().query(uri, projection, null, null, null );
        column_index_data = mImagecursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = mImagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

        while(mImagecursor.moveToNext()) {
            String absolutePathOfImage = mImagecursor.getString(column_index_data);
            listofAllimages.add(absolutePathOfImage);
        }
        return listofAllimages;
    }

    public void set_current_view_mode(int mode, final int clickable) {

        //Log.d("sfnd_location","mode"+mode+"clickable"+clickable);f
        footerText.setVisibility(View.VISIBLE);
        footerText1.setVisibility(View.VISIBLE);
        footerText1.setBackgroundColor(getResources().getColor(R.color.colorFooter));
        footerText.setText("");
        progressBar.setVisibility(View.GONE);
        txtRunningSeconds.setVisibility(View.GONE);
        downloading_details.setVisibility(View.GONE);
        if(image_handler!=null) {
            if(image_runnable!=null) {
                image_handler.removeCallbacks(image_runnable);
            }
        }

        if(non_image_handler!=null) {
            if(non_image_runnable!=null) {
                non_image_handler.removeCallbacks(non_image_runnable);
            }
        }

        if(gallery_image_handler!=null) {
            if(gallery_image_runnable!=null) {
                gallery_image_handler.removeCallbacks(gallery_image_runnable);
            }
        }

        final int imgg_delay = parseInt(read_from_table("IMAGE_DELAY")) * 1000;


        if( mode==1 ) {
            if(clickable == 1) {
                get_select_image_gallery(this);
            }
            img_background.setScaleType(ImageView.ScaleType.FIT_XY);
            progressBar.setVisibility(View.GONE);
            txtRunningSeconds.setVisibility(View.GONE);
            downloading_details.setVisibility(View.GONE);
            image_handler = new Handler();
            image_runnable = new Runnable() {
                int i=0;
                public void run() {

                    if(clickable == 0 ) {
                         path = storage_path + IMAGES[i];
                    }
                    if( clickable == 1 ) {
                        path = listofAllimages.get(i);
                    }
                    new WavLog("Flashing red and green image"+path, getApplicationContext(), "MainActivity", "Set_current_view_mode");
                    //Log.d("path",path );

                    clock_screen();

                    Bitmap myBitmap = BitmapFactory.decodeFile( path );

                    //Not to change the image when touch is in hold
                    if(is_on_touch_active==false) {
                        img_background.setImageBitmap(myBitmap);
                    }

                    i++;
                    if(clickable == 0 ) {
                        if (i > IMAGES.length - 1) {
                            i = 0;
                        }
                    }
                    if( clickable == 1 ) {
                        if (i > listofAllimages.size() - 1) {
                            i = 0;
                        }
                    }
                    image_handler.postDelayed(this, imgg_delay);  //for interval...
                }
            };
            image_handler.postDelayed(image_runnable, 100); //for initial delay..

            //Show this popup after images are downloaded
            if(read_from_table("IS_APP_INSTALLED").equals("0")) {
                Toast.makeText(getApplicationContext(), R.string.successfully_downloaded, Toast.LENGTH_LONG).show();
            }
            //

            try {
                //Added sleep to avoid database connection issue when request DB when app opened
                sleep(1000);
                String imei_info = read_from_table("IMEI");
                String gps_lat_info = read_from_table("GPS_LAT");
                String gps_long_info = read_from_table("GPS_LONG");
                if (!check_distance(gps_lat_info, gps_long_info)) {
                    Toast.makeText(getApplicationContext(), R.string.device_position_alert, Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
            } catch(InterruptedException e){

            }

            //Send first request to get registration link when app installed
            if(read_from_table("IS_APP_INSTALLED").equals("0")) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    footerText.setVisibility(View.VISIBLE);
                                    footerText.setText(R.string.processing);
                                }
                            });


                            //Added sleep to avoid database connection issue when request DB when app opened
                            sleep(1000);

                            String imei_info = read_from_table("IMEI");
                            String gps_lat_info = read_from_table("GPS_LAT");
                            String gps_long_info = read_from_table("GPS_LONG");
                            BufferedReader readera = null;
                            URL urla;
                            // Defined URL  where to send data

                            urla = new URL(IMEI_URL_PATH);

                            // Send POST data request
                            URLConnection connaa = urla.openConnection();
                            connaa.setDoOutput(true);
                            connaa.setConnectTimeout(CONNECTION_TIMEOUT_SECONDS);
                            OutputStreamWriter wra = new OutputStreamWriter(connaa.getOutputStream());

                            wra.write("imei=" + imei_info + "," + gps_lat_info + "," + gps_long_info);
                            wra.flush();

                            // Get the server response
                            readera = new BufferedReader(new InputStreamReader(connaa.getInputStream()));
                            StringBuilder sba = new StringBuilder();
                            String linea = null;

                            // Read Server Response
                            while ((linea = readera.readLine()) != null) {
                                // Append server response in string
                                sba.append(linea + "\n");
                            }

                            final String response = sba.toString().trim();

                            final String image_prefix =  response.substring(response.indexOf("?este=")+6,response.indexOf("&b="));

                            if( image_prefix != "" ) {
                                update_table("IMAGE_PREFIX",image_prefix);
                            }

                            final String[] response_split = response.split("&b=");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    footerText.setVisibility(View.VISIBLE);

                                    footerText.setText(extractMessage(response,"b"));
                                    footerText.setTextColor(getResources().getColor(R.color.colorHyperLink));
                                    footerText.setPaintFlags(footerText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

                                    update_table("IS_APP_INSTALLED", "1");
                                    //Log.d("Exists",read_from_table("IS_APP_INSTALLED"));


                                    /*footerText.setText("aaaaa");
                                    footerText.setPaintFlags(0);
                                    footerText.setTextColor(getResources().getColor(R.color.colorBlack));*/

                                    /*footerText.setText(response_split[0]);
                                    footerText.setTextColor(getResources().getColor(R.color.colorHyperLink));
                                    footerText.setPaintFlags(footerText.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);*/
                                }
                            });


                            footer_hyper_link = response_split[0];
                            is_previous_request_done = 0;
                        } catch (final Exception e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    footerText.setText(e.getMessage());
                                }
                            });
                            Log.d("time1", e.getMessage());
                        }
                    }
                }).start();
            }
        }

        if( mode==0 && clickable ==1 ) {
                if( clickable ==1 ) {
                    get_select_image_gallery(this);
                }
                img_background.setScaleType(ImageView.ScaleType.FIT_XY);
                progressBar.setVisibility(View.GONE);
                txtRunningSeconds.setVisibility(View.GONE);
                downloading_details.setVisibility(View.GONE);
                gallery_image_handler = new Handler();
                gallery_image_runnable = new Runnable() {
                    int i = 0;
                    public void run() {
                        //Log.d("sfnd_location","location_path"+listofAllimages.get(i)+i);
                        if (clickable == 1) {
                            gallery_path = listofAllimages.get(i);
                        }
                        Bitmap myBitmap = BitmapFactory.decodeFile(gallery_path);
                        clock_screen();
                        //Not to change the image when touch is in hold
                        if (is_on_touch_active == false) {
                            img_background.setImageBitmap(myBitmap);
                        }
                        i++;
                        if (clickable == 1) {
                            if (i > listofAllimages.size() - 1) {
                                i = 0;
                            }
                        }
                        gallery_image_handler.postDelayed(this, imgg_delay);  //for interval...
                    }
                };
                gallery_image_handler.postDelayed(gallery_image_runnable, 100); //for initial delay..
            }

        if(mode==0 && clickable == 0) {


            digiClock.setVisibility(View.VISIBLE);
            img_background.setImageDrawable(null);
            non_image_handler = new Handler();
            non_image_runnable = new Runnable() {
                int i = 0;
                public void run() {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH");
                    int hours = parseInt(sdf.format(new Date()));
                    new WavLog("Background Screensaver Clock TImer", getApplicationContext(), "MainActivity", "Set_current_view_mode");

                    clock_screen();
                    i++;
                    /*if(hours >= 12) {
                        if( is_on_touch_active == false ) {
                            img_background.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                        }
                    } else {
                        if( is_on_touch_active == false ) {
                            img_background.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                        }
                    }*/
                    //Log.d("clock_screen","clickable"+i);
                    non_image_handler.postDelayed(this, 1000);  //for interval...
                }
            };
            non_image_handler.postDelayed(non_image_runnable, 100); //for initial delay..
        } else {
            digiClock.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            decorView.setSystemUiVisibility(hideSystemBars());
        }
    }

    private int hideSystemBars() {
        return View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
    }

    public void clock_screen() {

        final String clock_screen = read_from_table("CLOCK_SCREEN");

        if( clock_screen.equals("1")) {
            img_background.setBackgroundColor(getResources().getColor(R.color.colorBlack));
            digiClock.setTextColor(Color.parseColor("#ffffff"));
            milliSecondClock.setTextColor(Color.parseColor("#ffffff"));
            footerText1.setBackgroundColor(getResources().getColor(R.color.colorBlack));
            mTextViewPercentage.setTextColor(getResources().getColor(R.color.colorWhite));
            int color = 000000;
            mProgressBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
            mProgressBar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
        if( clock_screen.equals("0") ) {
            img_background.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            digiClock.setTextColor(Color.parseColor("#000000"));
            milliSecondClock.setTextColor(Color.parseColor("#000000"));
            footerText1.setBackgroundColor(getResources().getColor(R.color.colorFooter));
            mTextViewPercentage.setTextColor(getResources().getColor(R.color.colorBlack));
            int color =  Color.parseColor("#ffffff");
            mProgressBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
            mProgressBar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
    }
    public void startDownloading(final String request_url, final String save_name ) {

        File f_dir = new File(storage_path);
        if (!f_dir.exists()) {
            f_dir.mkdirs();
        }
        //@issues: automatically running
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(request_url+'/'+save_name));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setDescription("Download Assets....");
        request.setTitle("Mxc5 "+save_name);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        String updatePath = storage_path+save_name;
        request.setDestinationUri(Uri.fromFile(new File(updatePath)));
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        download_id = manager.enqueue(request);

    }



    private BroadcastReceiver downloadActionChange = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if(download_id == id) {
                if(get_status_of_files()==2) {
                    current_view_mode = parseInt(read_from_table("CURRENT_VIEW"));
                    current_image_mode = parseInt(read_from_table("MY_IMAGE"));
                    set_current_view_mode(current_view_mode,current_image_mode);
                }
                download_image_files();
            }

        }
    };

    public double getBatteryCapacity(Context context) {
        Object mPowerProfile;
        double batteryCapacity = 0;
        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";

        try {
            mPowerProfile = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class)
                    .newInstance(context);

            batteryCapacity = (double) Class
                    .forName(POWER_PROFILE_CLASS)
                    .getMethod("getBatteryCapacity")
                    .invoke(mPowerProfile);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return batteryCapacity;

    }

    private BroadcastReceiver chargerActionChange = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
           int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
           int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
           int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE,-1);
           batteryTemp = (float)(intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0))/10;

            double totalcapacity = getBatteryCapacity(context);


           batterylevel = level+".0%";
            mTextViewPercentage.setText("" + level + ".0%");
            mProgressBar.setProgress(mProgressStatus);
            if( level <= 10 && is_charger_level == true && read_from_table("IS_APP_INSTALLED").equals("1") ) {
               update_footer_data("chargerLevel");
           }
            if( level > 10 ) {
                is_charger_level = true;
            }
           Log.d("charger_level","levl"+level);
        }
    };

    private BroadcastReceiver HeadsetActionChange = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case 0:
                        //Toast.makeText(context,"Headset Disconnected",Toast.LENGTH_LONG).show();
                        new WavLog("HeadSetDisConnected", getApplicationContext(), "MainActivity", "HeadsetActionChange");
                        break;
                    case 1:
                        // Toast.makeText(context,"Headset Connected",Toast.LENGTH_LONG).show();
                        new WavLog("HeadSetConnected", getApplicationContext(), "MainActivity", "HeadsetActionChange");
                        is_headset_connected = true;
                        //Nov 12, 2021 TODO: Commented this line only
                        //open_settings_dialog();
                        break;
                }
            }
        }
    };

    private BroadcastReceiver PowerActionChange = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String ambeint_light = read_from_table("AMBEINT_LIGHT");
            if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
                new WavLog("PowerConnected", getApplicationContext(), "MainActivity", "PowerActionChange");
                update_footer_data("event_12");
                if( ambeint_light.equals("1")  && is_ambient_light == true ) {
                    //preview.enableTorch(false);
                    if ( cam.getCameraInfo().hasFlashUnit() ) {
                        cam.getCameraControl().enableTorch(false); // or false
                    }
                    is_ambient_light = false;
                }
                Log.d("Power_status","1");
                check_power_status = "1";
                //Toast.makeText(context,"Power Connected",Toast.LENGTH_LONG).show();
            }

            if (intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)) {
                if( ambeint_light.equals("1") && is_ambient_light == false ) {
                    //preview.enableTorch(true);
                    if ( cam.getCameraInfo().hasFlashUnit() ) {
                        cam.getCameraControl().enableTorch(true); // or false
                    }
                    is_ambient_light = true;
                }
                Log.d("Power_status","2");
                check_power_status = "0";
                //Toast.makeText(context,"Power Disconnected",Toast.LENGTH_LONG).show();
                new WavLog("PowerDisConnected", getApplicationContext(), "MainActivity", "PowerActionChange");
                update_footer_data("power_disconnectd");
            }
        }
    };


    private BroadcastReceiver NetworkActionChange = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (checkInternetConnection(context)) {
                is_check_network = true;
            } else {
                is_check_network = false;
                if( is_on_pause == false ) {
                   Toast.makeText(getApplicationContext(),R.string.network_not_available, Toast.LENGTH_SHORT).show();
                }
            }
        }

        boolean checkInternetConnection (Context context){
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        Log.d("launching","on_start");
        audioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM,AudioManager.ADJUST_MUTE,0);
        this.startLocation(true);
        is_on_pause = false;
        is_camera_open = false;
        is_takenpicture = true;
        picorientation = read_from_table("ORIENTATION");
        storage_path = getApplicationContext().getExternalFilesDir("images").getAbsolutePath() + File.separator;
        IntentFilter  network_filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        IntentFilter headset_filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        IntentFilter power_connected_filter = new IntentFilter(Intent.ACTION_POWER_CONNECTED);
        IntentFilter power_disconnected_filter = new IntentFilter(Intent.ACTION_POWER_DISCONNECTED);
        IntentFilter download_filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        IntentFilter charger_level_filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        try {
            registerReceiver(NetworkActionChange,network_filter);
        } catch(IllegalArgumentException e) {
            //  e.printStackTrace();
        }
        try {
            registerReceiver(HeadsetActionChange,headset_filter);
        } catch(IllegalArgumentException e) {
            //  e.printStackTrace();
        }

        try {
            registerReceiver(PowerActionChange,power_connected_filter);
        } catch(IllegalArgumentException e) {
            //  e.printStackTrace();
        }

        try {
            registerReceiver(PowerActionChange,power_disconnected_filter);
        } catch(IllegalArgumentException e) {
            //  e.printStackTrace();
        }
        try {
            registerReceiver(downloadActionChange,download_filter);
        } catch (IllegalArgumentException e) {
            //e.printStackTrace();
        }
        try {
            registerReceiver(chargerActionChange,charger_level_filter);
        } catch (IllegalArgumentException e) {
            //e.printStackTrace();
        }
        mOrientationEventListener.enable();
    }


    public void generate_pure_tone(final double freq, final double duration) {

                double sampleRate = 44100.0;
                double frequency = freq;
                double amplitude = 0.8;
                double seconds = duration;
                double twoPiF = 2 * Math.PI * frequency;
                float[] buffer = new float[(int) (seconds * sampleRate)];
                for (int sample = 0; sample < buffer.length; sample++) {
                    double time = sample / sampleRate;
                    buffer[sample] = (float)(amplitude * Math.sin(twoPiF * time));
                }
                final byte[] byteBuffer = new byte[buffer.length * 2];
                int bufferIndex = 0;
                for (int i = 0; i < byteBuffer.length; i++) {
                    final int x = (int) (buffer[bufferIndex++] * 32767.0);
                    byteBuffer[i] = (byte) x;
                    i++;
                    byteBuffer[i] = (byte) (x >>> 8);
                }
                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                audioManager.setSpeakerphoneOn(true);
                audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                        (int)sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT, byteBuffer.length,
                        AudioTrack.MODE_STATIC);
                audioTrack.write(byteBuffer,0,byteBuffer.length);
                if(audioTrack.getPlayState() != AudioTrack.PLAYSTATE_PLAYING) {
                    audioTrack.play();
                }

    }


    @Override
    public void onStop() {
        new WavLog("OnStop", getApplicationContext(), "MainActivity", "onPause");
        super.onStop();
        audioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM,AudioManager.ADJUST_UNMUTE,0);
        is_on_pause = true;
        Log.d("launching","on_stop");
        try {
            unregisterReceiver(NetworkActionChange);
        } catch(IllegalArgumentException e) {
            // e.printStackTrace();
        }
        try {
            unregisterReceiver(HeadsetActionChange);
        } catch(IllegalArgumentException e) {
            //e.printStackTrace();
        }
        try {
            unregisterReceiver(PowerActionChange);
        } catch(IllegalArgumentException e) {
            //e.printStackTrace();
        }


        try {
            unregisterReceiver(downloadActionChange);
        } catch(IllegalArgumentException e) {
            //e.printStackTrace();
        }

        try {
            unregisterReceiver(chargerActionChange);
        } catch(IllegalArgumentException e) {
            //e.printStackTrace();
        }

        try {
            dbHelper.close();
        }   catch(IllegalArgumentException e) {
            //e.printStackTrace();
        }
        if(dialogContainer!=null) {
            try {
                dialogContainer.dismiss();
            } catch(Exception e) {
                //
            }


        }
        mOrientationEventListener.disable();
    }

    @Override
    public void onPause() {
        new WavLog("OnPause", getApplicationContext(), "MainActivity", "onPause");
        super.onPause();
        //Log.d("launching","on_pause");
        if(dialogContainer!=null) {
            try {
                dialogContainer.dismiss();
            } catch (Exception e) {
                //
            }
        }
    }

    @Override
    public void onDestroy() {
        new WavLog("Manual Destroy", getApplicationContext(), "MainActivity", "onDestroy");
        super.onDestroy();
        //savelogdev();
        //Log.d("launching","on_destroy");
        try {
            unregisterReceiver(NetworkActionChange);
        } catch(IllegalArgumentException e) {
            //  e.printStackTrace();
        }
        try {
            unregisterReceiver(HeadsetActionChange);
        } catch(IllegalArgumentException e) {
            // e.printStackTrace();
        }
        try {
            unregisterReceiver(PowerActionChange);
        } catch(IllegalArgumentException e) {
            // e.printStackTrace();
        }
        try {
            unregisterReceiver(downloadActionChange);
        } catch(IllegalArgumentException e) {
            //e.printStackTrace();
        }
        try {
            dbHelper.close();
        }   catch(IllegalArgumentException e) {
            //e.printStackTrace();
        }
        if(audioTrack!=null) {
            if(audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                audioTrack.stop();
            }
        }
        if( camera_handler != null) {
            if(camera_handler!=null) {
                camera_handler.removeCallbacks(camera_runnable);
            }
        }

        if(image_handler!=null) {
            if(image_runnable!=null) {
                image_handler.removeCallbacks(image_runnable);
            }
        }

        if(non_image_handler!=null) {
            if(non_image_runnable!=null) {
                non_image_handler.removeCallbacks(non_image_runnable);
            }
        }

        if(seconds_handler!=null) {
            if(seconds_runnable!=null) {
                seconds_handler.removeCallbacks(seconds_runnable);
            }
        }

        if(gallery_image_handler!=null) {
            if(gallery_image_runnable!=null) {
                gallery_image_handler.removeCallbacks(gallery_image_runnable);
            }
        }

        if(upload_images_handler != null) {
            if(upload_images_runnable!=null) {
                upload_images_handler.removeCallbacks(upload_images_runnable);
            }
        }

        if(manually_upload_images_handler!=null) {
            if(manually_upload_images_runnable!=null) {
                manually_upload_images_handler.removeCallbacks(manually_upload_images_runnable);
            }
        }

        if(deletewavlog_handler!=null) {
            if(deletewavlog_runnable!=null) {
                deletewavlog_handler.removeCallbacks(deletewavlog_runnable);
            }
        }

        if(check_request_handler!=null) {
            if(check_request_runnable!=null) {
                check_request_handler.removeCallbacks(check_request_runnable);
            }
        }

        if( dev_log_handler !=null ) {
            if( dev_log_runnable!=null) {
                dev_log_handler.removeCallbacks(dev_log_runnable);
            }
        }

        /*File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);
        if( mediaStorageDir.exists() ) {
            String filename = read_from_table("LAST_IMAGE_DATE_TIME");
            String last_upload_filename = mediaStorageDir.toString()+'/'+filename;
            File newfile = new File(last_upload_filename);
            Log.d("ondestroy",newfile.getAbsolutePath());
        }*/
        mOrientationEventListener.disable();
        audioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM,AudioManager.ADJUST_UNMUTE,0);
    }

    public boolean update_table(String config, String data) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbInfo.DbEntry.COLUMN_NAME_DATA, data);
        String selection = DbInfo.DbEntry.COLUMN_NAME_TITLE + " LIKE ?";
        String[] selectionArgs = { config };
        int update_count = db.update(
                DbInfo.DbEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        if(update_count>0) {
            return true;
        }
        return false;
    }

    public String read_from_table(String config) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String data = "";

        String[] projection = {
                BaseColumns._ID,
                DbInfo.DbEntry.COLUMN_NAME_TITLE,
                DbInfo.DbEntry.COLUMN_NAME_DATA
        };

        String selection =  DbInfo.DbEntry.COLUMN_NAME_TITLE + " = ?";
        String[] selectionArgs = { config };

        Cursor cursor = db.query(
                DbInfo.DbEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );
        if( cursor != null && cursor.getCount() > 0 ) {
            while(cursor.moveToNext()) {
                data =  cursor.getString(
                        cursor.getColumnIndexOrThrow(DbInfo.DbEntry.COLUMN_NAME_DATA));
                return data;
            }
        }

        return data;
    }

    private boolean check_if_file_exists(String FileName) {
        String file_path = storage_path+FileName;
        File file = new File(file_path);
        if(file.exists()) {
            return true;
        }
        return false;
    }

    private int get_status_of_files() {
        if(check_if_file_exists(IMAGES[0])) {
            if(check_if_file_exists(IMAGES[1])) {
                return 2;
            }
            return 1;
        }
        return 0;
    }



    public int download_image_files() {
        imei_value = Long.parseLong(read_from_table("IMEI"));
        downloading_details.setText(R.string.downloading_assets);

        if(get_status_of_files()==0) {
            startDownloading(IMG_URL_PATH,IMAGES[0]);
            return 1;

        } else if (get_status_of_files() == 1) {
            startDownloading(IMG_URL_PATH, IMAGES[1]);
            return 2;
        }
        return 0;
    }

    public void open_location_dialog(String center_location, MainActivity obj){
        dialogLocation = new LocationDialog(center_location, obj);
        dialogLocation.setCancelable(false);
        dialogLocation.show(getSupportFragmentManager(), "Settings dialog");
    }

    public void open_closecircuit_dialog(MainActivity obj){
        new WavLog("openClosed Circuit Dialog", getApplicationContext(), "MainActivity", "open_closecircuit_dialog");
        closeCircuitDialog = new CloseCircuitDialog(MainActivity.this);
        closeCircuitDialog.setCancelable(false);
        closeCircuitDialog.show(getSupportFragmentManager(), "Closed circuit dialog");
    }


    /*public void open_device_moving_dialog(){
        dialogDeviceMoving = new DeviceMovingDialog(MainActivity.this);
        dialogDeviceMoving.setCancelable(false);
        dialogDeviceMoving.show(getSupportFragmentManager(), "Device moving dialog");
    }*/

    public void open_settings_dialog() {

        /* if(dialogContainer==null) {*/
        if(dialogContainer!=null) {
            try {
                dialogContainer.dismiss();
            } catch(Exception e) {
                //
            }
        }
        new WavLog("open Setting Dialog menu", getApplicationContext(), "MainActivity", "onkeyDown");
        String image_display = read_from_table("CURRENT_VIEW");
        String image_delay   = read_from_table("IMAGE_DELAY");
        String image_clickable = read_from_table("IMAGE_CLICKABLE");
        String my_image = read_from_table("MY_IMAGE");
        String camera_info = read_from_table("CAMERA_INFO");
        String clock_screen = read_from_table("CLOCK_SCREEN");
        String camera_upload = read_from_table("CAMERA_UPLOADS");
        String ambeint_light = read_from_table("AMBEINT_LIGHT");
        String circuit_closed = read_from_table("CIRCUIT_CLOSED");
        String closecircuit_code_val = read_from_table("CIRCUIT_CLOSED_CODE");
        String orientation           = read_from_table("ORIENTATION");
        String zoom_val             = read_from_table("ZOOM_VALUE");
        String data_pref[] = new String[]{image_display,image_delay,image_clickable,circuit_closed,closecircuit_code_val,my_image,camera_info,clock_screen,camera_upload,ambeint_light,orientation,zoom_val};
        dialogContainer = new SettingsDialog(data_pref, getApplicationContext(), MainActivity.this);
        dialogContainer.setCancelable(false);
        dialogContainer.show(getSupportFragmentManager(), "Settings dialog");


       /* } else {
            dialogContainer.dismiss();
        }*/
    }

    public void update_imei() {
        new Thread(new Runnable(){

            public void run(){
                try {
                    String gps_lat_info = read_from_table("GPS_LAT");
                    String gps_long_info = read_from_table("GPS_LONG");
                    String params = "imei=02:00:00:00:00:00,"+gps_lat_info+","+gps_long_info;


                    BufferedReader reader = null;
                    URL url;
                    // Defined URL  where to send data

                    url = new URL(IMEI_URL_PATH);

                    // Send POST data request
                    URLConnection conna = url.openConnection();
                    conna.setDoOutput(true);
                    conna.setConnectTimeout(CONNECTION_TIMEOUT_SECONDS);
                    OutputStreamWriter wr = new OutputStreamWriter(conna.getOutputStream());

                    wr.write(params);
                    wr.flush();

                    // Get the server response
                    reader = new BufferedReader(new InputStreamReader(conna.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        // Append server response in string
                        sb.append(line + "\n");
                    }
                    String response_imei = sb.toString().trim();

                    if(Long.parseLong(read_from_table("IMEI"))==0) {
                        update_table("IMEI", response_imei);
                    }
                } catch (Exception e) {
                    //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }).start();
    }

    public void applySave(String cv, String is_clicked, String delay, String circuit_closed, String closecircuit_code_val, String my_image_clickable, String camera_info_clickable, String clock_screen_clickable, String camera_upload_clickable, String ambeint_light_clickable, String orientation,String Zoomvalue ) {
        if(delay.isEmpty()) {
            if(parseInt(delay)>99) {
                cv = "99";
            }else if(parseInt(delay)<1) {
                cv ="1";
            }
        }
        if( zoom_level_change == 1 ) {
            Float zoom_final_val = Float.parseFloat(Zoomvalue) /100;
            zoom_val = zoom_final_val;
            Log.d("bar_value","valuefinal"+zoom_final_val+Zoomvalue);
            is_camera_open = false;
        }

        String camera_info = read_from_table("CAMERA_INFO");
        if( !camera_info.equals(camera_info_clickable) ) {
            //update_footer_data("event_12");
            is_camera_open = false;
        }

        String orientation_info = read_from_table("ORIENTATION");
        if( !orientation_info.equals(orientation) ) {
            is_camera_open = false;
        }
        String response_status = read_from_table("RESPONSE_STATUS");
        if( response_status.equals("0") ) {
            update_table("RESPONSE_STATUS","1");
        }

        if(parseInt(circuit_closed) == 0 ) {
            generate_pure_tone(1500,360.0);
        }
        new WavLog("Setting Dialog menu Saved"+circuit_closed, getApplicationContext(), "MainActivity", "applysave");
        new WavLog("Setting Dialog menu Current_View"+cv, getApplicationContext(), "MainActivity", "applysave");
        new WavLog("Setting Dialog menu Image_clickable"+is_clicked, getApplicationContext(), "MainActivity", "applysave");
        new WavLog("Setting Dialog menu Image_delay"+delay, getApplicationContext(), "MainActivity", "applysave");
        new WavLog("Setting Dialog menu My_image"+my_image_clickable, getApplicationContext(), "MainActivity", "applysave");

        update_table("CURRENT_VIEW",cv);
        update_table("IMAGE_CLICKABLE",is_clicked);
        update_table("IMAGE_DELAY",delay);
        update_table("MY_IMAGE",my_image_clickable);
        update_table("CAMERA_INFO",camera_info_clickable);
        update_table("CLOCK_SCREEN",clock_screen_clickable);
        update_table("CAMERA_UPLOADS",camera_upload_clickable);
        update_table("AMBEINT_LIGHT",ambeint_light_clickable);
        update_table("CIRCUIT_CLOSED",circuit_closed);
        update_table("CIRCUIT_CLOSED_CODE",closecircuit_code_val);
        update_table("ORIENTATION",orientation);
        update_table("ZOOM_VALUE",Zoomvalue);
        Log.d("camera_uploads",camera_upload_clickable);
        set_current_view_mode(parseInt(cv), parseInt(my_image_clickable));
        Toast.makeText(getApplicationContext(),R.string.saved_preferences,Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Log.d("sfndkey", "onKeyDown222: "+keyCode);

        String circuit_closed = read_from_table("CIRCUIT_CLOSED");
        Log.d("circuit_closed","value"+circuit_closed);
        if(circuit_closed.equals("0")) {
            if (keyCode == 79) {
                stop_audio_track();
            }
        }


        if(keyCode==79) {
            //Log.d("sfndkey", "aaa");
            if(key_down_pressed == false) {
                //Log.d("sfndkey", "xyz");
                key_down_pressed = true;
                if (is_headset_connected == true) {
                    //Log.d("sfndkey", "abc");
                    //String circuit_closed = read_from_table("CIRCUIT_CLOSED");
                    if (circuit_closed.equals("1") )
                    {
                        new WavLog("External Circuit open on Headset connnected", getApplicationContext(), "MainActivity", "onkeyDown");
                        //Log.d("sfndkey", "onKeyDown11: "+keyCode);
                        touchDownEvent("click_mode");
                        touchUpEvent("click_mode");

                    }
                }
            }
            keydown_long_pressed = true;
        }


        /*if(keyCode==79) {
            if(key_down_pressed == false) {
                key_down_pressed = true;
                if (is_headset_connected == true) {
                    //open_closecircuit_dialog( MainActivity.this);

                    //Disabled due to confirm code popup
                    //TODO Nov 21, 2021 - implement after verification of code
                    Log.d("sfndkey", "onKeyDown: " + keyCode);
                    String circuit_closed = read_from_table("CIRCUIT_CLOSED");
                    Log.d("circuit_closed", "1: " + circuit_closed);
                    if (circuit_closed.equals("1")) {
                        touchDownEvent("click_mode");
                    }
                }
            }
        }*/
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.d("sfndkey", "onKeyUp: "+keyCode);



        String circuit_closed = read_from_table("CIRCUIT_CLOSED");
        //Log.d("circuit_closeda", circuit_closed);
        //Log.d("circuit_closeda", circuit_closed+keyCode);
        if(circuit_closed.equals("0")) {
            if (keyCode == 79) {

                open_closecircuit_dialog(MainActivity.this);
                //takenpicture();
            }
        }

        //Disabled due to confirm code popup
        //TODO Nov 21, 2021 - implement after verification of code
        if(keyCode==79) {
            if(key_down_pressed==false) {
                //Log.d("sfnd3", " showpopup");
            /*if (circuit_closed.equals("1")) {
                touchUpEvent("click_mode");
            } else*/
                if (circuit_closed.equals("1")  ) {
                    //Log.d("sfnd3", " showpopup");
                    touchDownEvent("click_mode");
                    touchUpEvent("click_mode");

                    //takenpicture();
                }
            }
            key_down_pressed = false;
            keydown_long_pressed = false;
        }


        //Close app when double press volume down
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            //double click
            if(click_count==1) {
                if ((exit_end_time - exit_start_time) < EXIT_MAX_DELAY) {
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        new WavLog("KeyDown Manual Destroy And Remove Task", getApplicationContext(),"MainActivity", "onkyeup");
                        finishAndRemoveTask();
                    } else {
                        new WavLog("KeyDown Manual Destroy", getApplicationContext(),"MainActivity", "onkeyup");
                        finish();
                    }
                }
            }
            if(exit_handler!=null) {
                if(exit_runnable!=null) {
                    exit_handler.removeCallbacks(exit_runnable);
                }
            }
            exit_handler = new Handler();
            exit_runnable = new Runnable() {
                @Override
                public void run() {
                    click_count = 0;
                }
            };
            exit_handler.postDelayed(exit_runnable,EXIT_MAX_DELAY);

            if(click_count>0) {
                exit_end_time = SystemClock.elapsedRealtime();
                click_count++;
            }

            if(click_count == 0 ) {
                exit_start_time = SystemClock.elapsedRealtime();
                click_count++;
            }
        }

        //Open settings dialog when press volume up
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP && keydown_long_pressed == false) {
            open_settings_dialog();
        }
        return true;
    }

    boolean checkInternet (Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    protected void sendloopSMS( String message1, String message2, String phoneNo, String response ) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (phoneNo != "" &&  message1 != "" ) {
                    String number[] = phoneNo.split(",");
                    if (number[0].length() >= 10) {
                        for (int i = 0; i < number.length; i++) {
                            if (number[i].length() >= 10) {
                                SmsManager smsManager = SmsManager.getDefault();
                                if( message1 != "" ) {
                                    smsManager.sendTextMessage(number[0], null, message1, null, null);
                                }
                                if( message2 != "" ) {
                                    smsManager.sendTextMessage(number[0], null, message2, null, null);
                                }
                            }
                        }
                        return;
                    } else {
                        return;
                    }
                }
            }
        }).start();

    }


    protected void sendSMS( String response ) {

        //String response_split_c = response.substring(response.indexOf("&b=")+3);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if( String.valueOf(response.indexOf("&c=")).equals("-1") ) {
                    return;
                } else {
                    String message1 = response.substring(response.indexOf("&c=")+3,response.indexOf("&d="));
                    String message2 = response.substring(response.indexOf("&e=")+3);
                    String phoneNo = extractMessage(response,"d");
                    if (response != phoneNo && response != message1) {
                        String number[] = phoneNo.split(",");
                        if (number.length == 1 && number[0].length() >= 10) {
                            SmsManager smsManager = SmsManager.getDefault();
                            if( message1 != "" ) {
                                smsManager.sendTextMessage(number[0], null, message1, null, null);
                            }
                            if( message2 != "" ) {
                                smsManager.sendTextMessage(number[0], null, message2, null, null);
                            }
                            return;
                        } else {
                            sendloopSMS( message1 , message2, phoneNo, response);
                            return;

                        }
                    }
                }
            }
        }).start();
    }

    public void getLocationOnClick(View view) {

        final File file = getOutputMediaFile(MEDIA_TYPE_IMAGE);
        imageCapture.takePicture(cameraExecutor, new OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {

                int rotatedegree = image.getImageInfo().getRotationDegrees();
                Bitmap bmp = imageProxyToBitmap(image);
                Matrix matrix = new Matrix();
                int position_orientation = getrotrationorientation(rotatedegree);
                matrix.postRotate(position_orientation);
                Bitmap bitmapFinal = null;
                //Log.d("Getbmp",bmp.getHeight()+","+bmp.getWidth());
                bitmapFinal = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
                Log.d("rotationde","va;l"+rotatedegree);
                new WavLog("On rotationdegree"+rotatedegree, getApplicationContext(), "MainActivity", "ImageRotateDegree");
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);
                    bmp_file_exists = true;
                } catch (FileNotFoundException e) {
                    bmp_file_exists = false;
                    new WavLog("On captureSuccess Error"+e.getMessage(), getApplicationContext(), "MainActivity", "On Capture Sucess");
                }
                if( bmp != null && bmp_file_exists == true ) {
                    bitmapFinal.compress(Bitmap.CompressFormat.JPEG, 10, fos);
                    //bitmapFinal.compress(Bitmap.CompressFormat.JPEG, 1, fos);
                    bitmapFinal.recycle();

                    /*if( is_check_network == true && is_on_pause == false && file.length() > 0 ) {
                        sendtoimage(file);
                    }*/
                }
                super.onCaptureSuccess(image);
            }

            @Override
            public void onError(@NonNull ImageCaptureException error) {
                is_camera_open = false;
                error.printStackTrace();
            }
        });
    }
        /*imageCapture.takePicture( cameraExecutor,new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(ImageProxy imageProxy) {
                int RotateDegree = imageProxy.getImageInfo().getRotationDegrees();
                //Image image = imageProxy.getImage();
                Log.d("rotationdegree","val"+RotateDegree);
                Bitmap bmp = imageProxyToBitmap(imageProxy);
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                bmp.compress(Bitmap.CompressFormat.JPEG,20,fos);
                imageProxy.close();
                //sendtoimage(file);
                //();
            }

            @Override
            public void onError(ImageCaptureException error) {
                Log.d("captureerror", String.valueOf(error));
            }*/


    private int getrotrationorientation(int degree ) {
        String orientation = read_from_table("ORIENTATION");
        if( degree == 0 ) {
            if( orientation.equals('2') ) {
                if(image_orientation == 0 ) {
                    return 0;
                } else if(image_orientation == 90 ) {
                    return -90;
                } else if( image_orientation == 180 ) {
                    return -180;
                } else if( image_orientation == 270 ) {
                    return 90;
                }
            } else {
                if(image_orientation == 0 ) {
                    return -90;
                } else if(image_orientation == 90 ) {
                    return -180;
                } else if( image_orientation == 180 ) {
                    return -270;
                } else if( image_orientation == 270 ) {
                    return 0;
                }
            }
        }
        if( degree == 90 ) {
            if( orientation.equals('2') ) {
                if(image_orientation == 0 ) {
                    return 0;
                } else if(image_orientation == 90 ) {
                    return -270;
                } else if( image_orientation == 180 ) {
                    return -180;
                } else if( image_orientation == 270 ) {
                    return -90;
                }
            } else {
                if(image_orientation == 0 ) {
                    return 90;
                } else if(image_orientation == 90 ) {
                    return -180;
                } else if( image_orientation == 180 ) {
                    return -90;
                } else if( image_orientation == 270 ) {
                    return 0;
                }
            }

        }
        if( degree == 180 ) {
            if( orientation.equals('2') ) {
                if(image_orientation == 0 ) {
                    return -270;
                } else if(image_orientation == 90 ) {
                    return 0;
                } else if( image_orientation == 180 ) {
                    return -90;
                } else if( image_orientation == 270 ) {
                    return 90;
                }
            } else {
                if(image_orientation == 0 ) {
                    return -180;
                } else if(image_orientation == 90 ) {
                    return 90;
                } else if( image_orientation == 180 ) {
                    return 0;
                } else if( image_orientation == 270 ) {
                    return 180;
                }
            }

        }
        if( degree == 270 ) {
            if( orientation.equals('2') ) {
                if(image_orientation == 0 ) {
                    return -180;
                } else if(image_orientation == 90 ) {
                    return -270;
                } else if( image_orientation == 180 ) {
                    return 0;
                } else if( image_orientation == 270 ) {
                    return -90;
                }
            } else {
                if(image_orientation == 0 ) {
                    return -90;
                } else if(image_orientation == 90 ) {
                    return -180;
                } else if( image_orientation == 180 ) {
                    return 90;
                } else if( image_orientation == 270 ) {
                    return 0;
                }
            }

        }
        return 0;
    }

    /*private void sendtoimage(File file) {
        Log.d("filename","file:"+file.getAbsoluteFile());
        String camera_upload = read_from_table("CAMERA_UPLOADS");
        String orientation   = read_from_table("ORIENTATION");
        Log.d("orientation_onscreen",orientation+","+image_orientation);
        new Thread(new Runnable() {
            public void run() {
                if ( camera_upload.equals("1") && is_on_pause == false && file.length() > 0) {
                    AsyncTaskUpload asyncTask = new AsyncTaskUpload();
                    asyncTask.setFilePath(file.getAbsolutePath(),orientation,image_orientation);
                    asyncTask.setUploadStatus(MainActivity.this, j);
                    asyncTask.execute();
                    j++;
                }
            }
        }).start();
    }*/

    private File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);
        final String img_prefix = read_from_table("IMAGE_PREFIX");
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        //formatter.setTimeZone(TimeZone.getTimeZone("GMT-5"));
        Date now = new Date();
        //long seconds = now.getTime();
        //seconds = seconds + (-3 * 1000); //add 15 seconds * 1000 because in millis
        //Date then = new Date(seconds);
        String timeStamp = formatter.format(now);
        Log.d("currenttime",timeStamp+","+formatter.format(new Date()));
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            String imei_info = read_from_table("IMEI");
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + img_prefix +"-"+ timeStamp +".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }


    private Bitmap imageProxyToBitmap(final ImageProxy image)
    {
        ByteBuffer byteBuffer = image.getPlanes()[0].getBuffer();
        byteBuffer.rewind();
        byte[] bytes = new byte[byteBuffer.capacity()];
        byteBuffer.get(bytes);
        byte[] clonedBytes = bytes.clone();
        return BitmapFactory.decodeByteArray(clonedBytes, 0, clonedBytes.length);
    }

    private boolean allPermissionsGranted(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.CAMERA) != PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void deleteWavlog() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());

        SimpleDateFormat log_sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDateandTimeLogFile = log_sdf.format(new Date());

        String external_storage_path = getApplicationContext().getExternalFilesDir("MxLog").getAbsolutePath()+"/log-"+currentDateandTimeLogFile+".txt";
        final File dir = new File(external_storage_path);

        if(dir.exists()) {
            dir.delete();
        }
    }

    public void handleUncaughtException (Thread thread, Throwable e)
    {
        String stackTrace = Log.getStackTraceString(e);
        String message = e.getMessage();
        new WavLog("App Carshes:Error "+message+stackTrace, getApplicationContext(), "MainActivity", "app_crashes_error");
        /*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        String currentDateandTime = sdf.format(new Date());

        SimpleDateFormat log_sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDateandTimeLogFile = log_sdf.format(new Date());

        String external_storage_path = getApplicationContext().getExternalFilesDir("MxLog").getAbsolutePath();
        final File dir = new File(external_storage_path);

        if(!dir.exists()) {
            dir.mkdir();
        }

        String wav_log_file_path = external_storage_path+"/log-"+currentDateandTimeLogFile+".txt";

        try {
            OutputStreamWriter file_writer = new OutputStreamWriter(new FileOutputStream(wav_log_file_path,true));
            BufferedWriter buffered_writer = new BufferedWriter(file_writer);
            buffered_writer.write(message);
            buffered_writer.close();

        } catch(FileNotFoundException err){

        } catch(IOException err){

        }
        //Log.d("Dhinesh",message);
        onDestroy();*/
    }

}
