package boton.c4.pisa.fmns;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialogFragment;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Stack;

public class LatLngDialog extends AppCompatDialogFragment {

    private Button start_again_button;
    public MainActivity obj;
    public TextView latlng_txt;
    public TableLayout latlng_table;
    public int location_id = 1;

    public LatLngDialog(MainActivity _obj) {
        obj = _obj;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.latlng_dialog, null);
        builder.setView(view).setTitle(getString(R.string.searching_location));

        latlng_table = (TableLayout) view.findViewById(R.id.latlng_table);
        //latlng_table.canScrollVertically(-1);

        /*latlng_txt = view.findViewById(R.id.latlng_txt);
        latlng_txt.setClickable(true);
        latlng_txt.setMovementMethod(new ScrollingMovementMethod());*/

        //updateTextValue("Hello");

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    public void updateTextValue(String latlng, final Stack<String> location_arr2, boolean is_restart_location_search){
        //latlng_table

        if(is_restart_location_search==true){
            location_id = 1;
            latlng_table.removeAllViews();
        }

        TableRow row_obj = new TableRow(obj.getApplicationContext());

        TextView col1 = new TextView(obj.getApplicationContext());
        TextView col2 = new TextView(obj.getApplicationContext());
        Button location_btn = new Button(obj.getApplicationContext());

        if(location_id<=100) {

            col1.setText("https://www.google.com/maps/search/?api=1&query=" + latlng);
            col1.setWidth(370);
            Linkify.addLinks(col1, Linkify.WEB_URLS);
            col1.setClickable(true);

            //Include variance in Location# button
            Stack<String> tmp_location_arr2 = new Stack<String>();
            Object locations_obj2[] = location_arr2.toArray();
            String variance_value = "0";
            if (locations_obj2.length > 2) {
                try {
                    for (int i = 0; i < location_id; i++) {
                        tmp_location_arr2.push(locations_obj2[i].toString());
                    }
                } catch (ArrayIndexOutOfBoundsException e) {

                }
                String mean_location = obj.calc_variance(tmp_location_arr2);

                double source_lat = Double.parseDouble(latlng.split(",")[0]);
                double source_lng = Double.parseDouble(latlng.split(",")[1]);

                double dest_lat = Double.parseDouble(mean_location.split(",")[0]);
                double dest_lng = Double.parseDouble(mean_location.split(",")[1]);

                double variance = obj.distance(source_lat, source_lng, dest_lat, dest_lng, "K");
                variance = variance * 1000;
                NumberFormat formatter = new DecimalFormat("#00");

                variance_value = formatter.format(variance);

                //Log.d("tmp_location_arr2", tmp_location_arr2+"");
                new WavLog("location_id: "+location_id, obj.getApplicationContext(), "LatLngDialog", "updateTextValue");
                new WavLog("tmp_location_arr2: "+tmp_location_arr2, obj.getApplicationContext(), "LatLngDialog", "updateTextValue");
                new WavLog("mean_location: "+mean_location, obj.getApplicationContext(), "LatLngDialog", "updateTextValue");
                new WavLog("latlng: "+latlng, obj.getApplicationContext(), "LatLngDialog", "updateTextValue");
                new WavLog("variance_value: "+variance_value, obj.getApplicationContext(), "LatLngDialog", "updateTextValue");
            }
            //


            obj.location_id = location_id;
            if( !variance_value.equals(null) ) {
                obj.global_variance = Integer.parseInt(variance_value);
            } else {
                obj.global_variance = 0;
            }


            location_btn.setText("Loc#" + location_id + ", V:" + variance_value);
            location_btn.setTag(location_id);

            location_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Stack<String> tmp_location_arr = new Stack<String>();
                /*
                extract the location id and lat_lng values from tab
                and loop through to get the locations from 0 to selected location id
                and find the middle position based on that
                */
                    //
                    Object tmp = ((Button) view).getTag();
                    int location_id = Integer.parseInt(tmp.toString());

                    Object locations_obj[] = location_arr2.toArray();

                    try {
                        for (int i = 0; i < location_id; i++) {
                            tmp_location_arr.push(locations_obj[i].toString());
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {

                    }

                    //String center_location = obj.GetCenterFromDegrees(tmp_location_arr);
                    String center_location = obj.calc_variance(tmp_location_arr);
                    //Log.d("sfnd123", "center_location: " + center_location + "");
                    //Log.d("sfnd123", "tmp_location_arr: " + tmp_location_arr + "");
                    //String center_location = tmp.toString();
                    //obj.open_settings_dialog();
                    obj.open_location_dialog(center_location, obj);
                }
            });

            row_obj.addView(col1);
            row_obj.addView(location_btn);

            latlng_table.addView(row_obj);


            location_id++;
        }


    }
}