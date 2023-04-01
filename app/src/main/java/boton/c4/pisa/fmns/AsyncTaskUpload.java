package boton.c4.pisa.fmns;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class AsyncTaskUpload extends AsyncTask<String, String, String> {

    String sourceFileUri, response, wavFilePath,image_path,FilenameTime;
    public Context local_context;
    public Button localBtnUpload;
    public String fft_values;
    public MainActivity obj;
    public String upload_status,screen_orientation,picture_orientation;
    public int image_count_status;



    //public String target_url = "https://devi.softound.com/mxc4/save.php";

    //public String target_url = "https://devi.softound.com/geo-in-simple-website/audio_uaf.php";


    public void setFilePath(String file_path_arg,String screen,int orientation ) { wavFilePath = file_path_arg; screen_orientation = screen; picture_orientation= String.valueOf(orientation);}
    public void setUploadStatus(MainActivity _obj,int k) {
        obj = _obj;
        image_count_status = k;
    }


    public String serverResponseContent = "";
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {

        //Log.d("sfnd","doInBackground");
        upload_status = obj.read_from_table("UPLOADED_STATUS");
        //Log.d("source_file",upload_status+","+image_count_status);
        try {

            String file_name_array[] = wavFilePath.split("/");
            int file_parts_length = file_name_array.length;
            String fileName = file_name_array[file_parts_length-1];
            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            File sourceFile = new File(wavFilePath);
            int serverResponseCode = 0;
            String serverResponseContent = "";
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            formatter.setTimeZone(TimeZone.getTimeZone("GMT-5"));
            String timeStamp = formatter.format(new Date());
            String deviceName = android.os.Build.MANUFACTURER + android.os.Build.BRAND + android.os.Build.MODEL;
            File paths = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), obj.IMAGE_DIRECTORY_NAME);
            Log.d("update_paths","paths"+sourceFile);
            int c = 0;

                try {
                    // open a URL connection to the Servlet
                    if( sourceFile.exists() ) {
                        FileInputStream fileInputStream = new FileInputStream(
                                sourceFile);
                        String target_url = obj.read_from_table("URL_STRING");
                        if( target_url.equals(null) ) {
                            target_url = "https://devi.softound.com/mxc4sfnd/save.php";
                        } else {
                            target_url = target_url;
                        }
                        Log.d("target_url",target_url);
                        URL url = new URL(target_url);
                        Log.d("url", String.valueOf(url));
                        // Open a HTTP connection to the URL
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setDoInput(true); // Allow Inputs
                        conn.setDoOutput(true); // Allow Outputs
                        conn.setUseCaches(false); // Don't use a Cached Copy
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Connection", "Keep-Alive");
                        conn.setRequestProperty("ENCTYPE",
                                "multipart/form-data");
                        conn.setRequestProperty("Content-Type",
                                "multipart/form-data;boundary=" + boundary);
                        conn.setRequestProperty("file", wavFilePath);
                        conn.setRequestProperty("screen", screen_orientation);
                        conn.setRequestProperty("images", picture_orientation);
                        conn.setRequestProperty("version", boton.c4.pisa.fmns.BuildConfig.VERSION_NAME);
                        conn.setRequestProperty("model", deviceName);


                        dos = new DataOutputStream(conn.getOutputStream());

                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"screen\"" + lineEnd);
                        dos.writeBytes("Content-Type: text/plain; charset=US-ASCII" + lineEnd);
                        dos.writeBytes("Content-Transfer-Encoding: 8bit" + lineEnd);
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(screen_orientation + lineEnd);
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"phone\"" + lineEnd);
                        dos.writeBytes("Content-Type: text/plain; charset=US-ASCII" + lineEnd);
                        dos.writeBytes("Content-Transfer-Encoding: 8bit" + lineEnd);
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(picture_orientation + lineEnd);
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes(boton.c4.pisa.fmns.BuildConfig.VERSION_NAME + lineEnd);
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes(deviceName + lineEnd);
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\""
                                + wavFilePath + "\"" + lineEnd);
                        dos.writeBytes(lineEnd);
                        new WavLog("upload_image_url" + wavFilePath, obj.getApplicationContext(), "Async upload image", "set_filepath");
                        // create a buffer of maximum size
                        bytesAvailable = fileInputStream.available();

                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        buffer = new byte[bufferSize];

                        // read file and write it into form...
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                        while (bytesRead > 0) {

                            dos.write(buffer, 0, bufferSize);
                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math.min(bytesAvailable, maxBufferSize);
                            bytesRead = fileInputStream.read(buffer, 0,
                                    bufferSize);

                        }

                        // send multipart form data necesssary after file
                        // data...
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + twoHyphens
                                + lineEnd);

                        // Responses from the server (code and message)
                        serverResponseCode = conn.getResponseCode();

                        if (serverResponseCode == 200) {
                            // messageText.setText(msg);
                            //Toast.makeText(ctx, "File Upload Complete.",
                            //      Toast.LENGTH_SHORT).show();

                            // recursiveDelete(mDirectory1);

                        }

                        InputStream in = conn.getInputStream();

                        while ((c = in.read()) != -1) {
                            serverResponseContent += (char) c;
                        }

                        JSONObject jsonObject = new JSONObject(serverResponseContent);
                        response = jsonObject.getString("message");
                        image_path = jsonObject.getString("fileName");
                        int year = Calendar.getInstance().get(Calendar.YEAR);
                        //String filenameTime = image_path.substring(11, 16);
                        String file_name[] = image_path.split("-");
                        if( !file_name.equals(null) ) {
                            if( file_name[0].length() == 4 ) {
                                FilenameTime = image_path.substring(0,19);
                            } else {
                                FilenameTime = image_path.substring(11,image_path.indexOf("."));
                            }
                        }
                        if (jsonObject.has("id")) {
                            obj.update_table("UPLOADED_STATUS", jsonObject.getString("id"));
                        } else {
                            obj.update_table("UPLOADED_STATUS", "1");
                        }
                        obj.update_table("LAST_IMAGE_DATE_TIME", FilenameTime);

                        //close the streams //
                        new WavLog("json object response" + jsonObject + "request_time" + timeStamp, obj.getApplicationContext(), "Async upload image", "jsonobject response");
                        Log.d("camera_response", "path" + serverResponseContent);
                        image_path = paths.toString() + File.separator + image_path;

                        fileInputStream.close();
                        dos.flush();
                        dos.close();

                        if( !image_path.equals(null) && obj.check_upload_server) {
                            obj.check_upload_server = false;
                            obj.is_manullay_upload_images = false;
                            obj.update_table("IMAGE_COUNT","0");
                        }
                        //Delete the file from mobile after uploaded to serve

                        File file = new File(image_path);
                        if (file.exists()) {
                            try {
                                obj.getApplicationContext().deleteFile(file.getName());
                                new WavLog("File name Delete Success Step 1:" + image_path, obj.getApplicationContext(), "Async upload image", "Response File Exists");
                            } catch (Exception e) {
                                new WavLog("File name Delete Failed :" + image_path, obj.getApplicationContext(), "Async upload image", "Response File Exists");
                            }
                            if (file.exists()) {
                                try {
                                    file.getAbsoluteFile().delete();
                                    new WavLog("File name Delete Success Step 2 :" + image_path, obj.getApplicationContext(), "Async upload image", "Response File Exists");
                                } catch (Exception e) {
                                    new WavLog("File name Delete Failed :" + e.getMessage() + "Files :" + image_path, obj.getApplicationContext(), "Async upload image", "Response File Exists");
                                }

                            } else {
                                new WavLog("File name Delete Failed :" + image_path, obj.getApplicationContext(), "Async upload image", "Response File Not exists");
                            }
                        } else {
                            new WavLog("File name Delete Failed :" + image_path, obj.getApplicationContext(), "Async upload image", "Response File Not Exists");
                        }
                    }

                } catch (Exception e) {

                    // dialog.dismiss();
                    new WavLog("json response error: "+e.getMessage()+"request_time"+timeStamp+"files"+wavFilePath, obj.getApplicationContext(), "Async upload image", "jsonobject response");
                    Log.d("camera_response1",e.getMessage());
                    obj.check_upload_server = true;
                    e.printStackTrace();

                }
                // dialog.dismiss();
                // End else block


        } catch (Exception ex) {
            new WavLog("json response error"+ex.getMessage()+"files"+wavFilePath, obj.getApplicationContext(), "Async upload image", "jsonobject response");
            Log.d("camera_response2",ex.getMessage());
            ex.printStackTrace();
        }
        return "Executed";
    }

    @Override
    protected void onPostExecute(String filename) {

        super.onPostExecute(filename);
        obj.post_execute = obj.post_execute + 1;
    }

}