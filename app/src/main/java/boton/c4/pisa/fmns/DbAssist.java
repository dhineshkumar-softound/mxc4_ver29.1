package boton.c4.pisa.fmns;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbAssist extends SQLiteOpenHelper {
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DbInfo.DbEntry.TABLE_NAME + " (" +
                    DbInfo.DbEntry._ID + " INTEGER PRIMARY KEY," +
                    DbInfo.DbEntry.COLUMN_NAME_TITLE + " TEXT," +
                    DbInfo.DbEntry.COLUMN_NAME_DATA + " TEXT)";

    private static final String SQL_INSERT_ENTRIES =
            "INSERT INTO "+DbInfo.DbEntry.TABLE_NAME +" ("+
                    DbInfo.DbEntry.COLUMN_NAME_TITLE +","+ DbInfo.DbEntry.COLUMN_NAME_DATA+") VALUES "+
                    "('GPS_LAT','0'),('GPS_LONG','0'),('IMEI','0'),('CURRENT_VIEW','1'),('IMAGE_DELAY','1'),('MY_IMAGE','0'),('IMAGE_CLICKABLE','1'),('CURRENT_GPS_LAT','0'),('CURRENT_GPS_LONG','0'),('IS_APP_INSTALLED','0'),('CIRCUIT_CLOSED', '1'),('CIRCUIT_CLOSED_CODE', ''),('CIRCUIT_REQUEST_CLOSED', '0'),('IMAGE_PREFIX',''),('UPLOADED_STATUS','1'),('LAST_IMAGE_DATE_TIME',''),('CAMERA_INFO','1'),('FLASH_LIGHT','0'),('CLOCK_SCREEN','0'),('CAMERA_UPLOADS','1'),('AMBEINT_LIGHT','0'),('TAKEN_PICTURE','3'),('UPLOAD_IMAGES','5'),('RESPONSE_STATUS','0'),('ORIENTATION','1'),('ZOOM_VALUE','0'),('IMAGE_COUNT','0'),('URL_STRING','https://mx911.org/mxc4_pictures/SENL/save.php')";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DbInfo.DbEntry.TABLE_NAME;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 30;
    public static final String DATABASE_NAME = "mxc5.db";

    public DbAssist(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL(SQL_INSERT_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
