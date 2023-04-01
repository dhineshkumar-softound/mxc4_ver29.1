package boton.c4.pisa.fmns;

import android.provider.BaseColumns;

public class DbInfo {
    public static class DbEntry implements BaseColumns {
        public static final String TABLE_NAME = "mxc4_settings";
        public static final String COLUMN_NAME_TITLE = "config";
        public static final String COLUMN_NAME_DATA = "data";
    }
}
