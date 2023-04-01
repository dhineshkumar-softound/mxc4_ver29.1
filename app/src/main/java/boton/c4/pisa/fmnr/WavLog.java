package boton.c4.pisa.fmnr;


import android.content.Context;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WavLog {

    public WavLog(String content, Context context, String class_name, String method_name){

        /*String log_dir = getWavLogDir(context);


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());

        SimpleDateFormat log_sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDateandTimeLogFile = log_sdf.format(new Date());

        //Log.d("log_dir", log_dir);
        String wav_log_file_path = log_dir + "/log-" + currentDateandTimeLogFile + ".txt";

        content = currentDateandTime + " ver " + boton.c4.pisa.fmnr.BuildConfig.VERSION_NAME + " : " + class_name + " : " + method_name + " : " + content + "\n";
        content = content + "\n";

        try {
               OutputStreamWriter file_writer = new OutputStreamWriter(new FileOutputStream(wav_log_file_path, true));
               BufferedWriter buffered_writer = new BufferedWriter(file_writer);
               buffered_writer.write(content);
               buffered_writer.close();
        } catch (FileNotFoundException e) {

        } catch (IOException e) {
        }*/
    }

    public static String getWavLogDir(Context context){
        //String external_storage_path = Environment.getExternalStorageDirectory().getAbsolutePath();
        String external_storage_path = context.getExternalFilesDir("MxLog").getAbsolutePath();
        final File dir = new File(external_storage_path);

        if(!dir.exists()) {
            dir.mkdir();
        }

        return dir.getAbsolutePath();
    }
}
