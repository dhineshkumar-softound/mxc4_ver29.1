package boton.c4.pisa.fmns;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialogFragment;

public class LocationDialog extends AppCompatDialogFragment {



    private Button yes_button,no_button;
    public TextView location_info_txt;
    public String center_location;
    public MainActivity obj;
    public LocationDialog(String _center_location, MainActivity _obj) {
        center_location = _center_location;
        obj = _obj;
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.location_dialog, null);
        builder.setView(view).setTitle(R.string.location_title);

        yes_button = (Button) view.findViewById(R.id.yes_btn);
        no_button = (Button) view.findViewById(R.id.no_btn);
        location_info_txt = (TextView) view.findViewById(R.id.location_info_txt);

        location_info_txt.setText(getString(R.string.verify_your_location)+": https://www.google.com/maps/search/?api=1&query="+center_location);
        Linkify.addLinks(location_info_txt, Linkify.WEB_URLS);

        no_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //is_search_location = false;
                obj.startSearchLocation(false);
               dismiss();
            }
        });

        yes_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(obj.location_id<100){
                    Toast.makeText(obj.getApplicationContext(),R.string.need_enough_samples, Toast.LENGTH_SHORT).show();
                } else {
                    if(obj.global_variance>49){
                        Toast.makeText(obj.getApplicationContext(),R.string.invalid_location, Toast.LENGTH_SHORT).show();
                        obj.startSearchLocation(true);

                    } else {
                        String[] lat_lng  = center_location.split(",");
                        obj.update_table("GPS_LAT", lat_lng[0]);
                        obj.update_table("GPS_LONG", lat_lng[1]);

                        obj.txtRunningSeconds.setText(getString(R.string.saved_location));

                        //Hide the location search dialog
                        obj.dialogLatLng.dismiss();

                        //Hide the seconds countdown
                        obj.digiClock.setVisibility(View.INVISIBLE);

                        //Stop updating the dialog latlng
                        obj.update_latlng_dialog = false;

                        //Log.d("sfnd", "updated center location");
                    }
                }
                dismiss();
            }
        });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }
}
