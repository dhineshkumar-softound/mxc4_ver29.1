package boton.c4.pisa.fmnr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.media.AudioTrack;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialogFragment;

public class SettingsDialog extends AppCompatDialogFragment {
    private String[] data_pref;
    private AudioTrack audioTrack;
    private CheckBox image_display, image_clickable, is_closed_circuit,my_image,flash_light,camera_upload,clock_screen,camera_info,ambeint_light;
    private EditText delay_time, closecircuit_code,url_text;
    private Button save_button,cancel_button;
    private RadioButton portrait_orientation,horizontal_orientation,phone_orientation;
    private Context context;
    public MainActivity mainActivity;
    public SeekBar seekbar;
    public TextView zoom_level;
    public int bar_value;
    public SettingsDialog(String[] data, Context _context, MainActivity _mainActivity) {
        data_pref = data;
        context = _context;
        mainActivity = _mainActivity;
        mainActivity.setting_dialog = true;
    }

    private SettingsDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.settings_dialog, null);
        builder.setView(view).setTitle(R.string.settings_label);
        image_display = (CheckBox) view.findViewById(R.id.checkBox10);
        delay_time    = (EditText) view.findViewById(R.id.editTextNumber7);
        my_image      = (CheckBox) view.findViewById(R.id.checkBox11);
        closecircuit_code    = (EditText) view.findViewById(R.id.closecircuit_code);
        image_clickable = (CheckBox) view.findViewById(R.id.checkBox12);
        is_closed_circuit = (CheckBox) view.findViewById(R.id.is_closed_circuit);
        cancel_button = (Button) view.findViewById(R.id.button12);
        save_button = (Button) view.findViewById(R.id.button13);

         camera_info      = (CheckBox) view.findViewById(R.id.checkBox13);
         clock_screen    = (CheckBox) view.findViewById(R.id.checkBox14);
         camera_upload   = (CheckBox) view.findViewById(R.id.checkBox17);
         ambeint_light   = (CheckBox) view.findViewById(R.id.checkBox16);

         portrait_orientation    = (RadioButton) view.findViewById(R.id.lock_orientation);
        horizontal_orientation    = (RadioButton) view.findViewById(R.id.unlock_orientation);

        zoom_level = (TextView) view.findViewById(R.id.textView3);

        seekbar = (SeekBar)view.findViewById(R.id.seekBar);


        int image_display_status = Integer.parseInt(data_pref[0]);
        String edit_delay_time = data_pref[1];
        int image_clickable_int = Integer.parseInt(data_pref[2]);
        int is_closed_circuit_int = Integer.parseInt(data_pref[3]);
        String closecircuit_code_txt = data_pref[4];
        int my_image_status       = Integer.parseInt(data_pref[5]);
        int camera_info_setting   = Integer.parseInt(data_pref[6]);
        int clock_screen_setting  = Integer.parseInt(data_pref[7]);
        int camera_upload_setting = Integer.parseInt(data_pref[8]);
        int ambeint_light_setting = Integer.parseInt(data_pref[9]);
        int screen_orientation    = Integer.parseInt(data_pref[10]);
        int zoom_val              = Integer.parseInt(data_pref[11]);

        seekbar.setProgress(zoom_val);
        zoom_level.setText(String.valueOf(zoom_val));


        mainActivity.zoom_level_change = 0;
        boolean image_display_boolean = false;
        if(image_display_status==1) {
            image_display_boolean = true;
        }

        boolean image_is_clickable = false;
        if(image_clickable_int == 1) {
            image_is_clickable = true;
        }

        boolean my_image_clickable = false;
        if(my_image_status == 1) {
            my_image_clickable = true;
        }


        boolean circuit_is_closed = false;
        if(is_closed_circuit_int == 1) {
            circuit_is_closed = true;
        }

        boolean camera_info_clickable = false;
        if(camera_info_setting == 1) {
            camera_info_clickable = true;
        }


        boolean clock_screen_clickable = false;
        if(clock_screen_setting == 1) {
            clock_screen_clickable = true;
        }

        boolean camera_upload_clickable = false;
        if(camera_upload_setting == 1) {
            camera_upload_clickable = true;
        }

        boolean ambeint_light_clickable = false;
        if(ambeint_light_setting == 1) {
            ambeint_light_clickable = true;
        }


        if( screen_orientation == 1 ) {
          portrait_orientation.setChecked(true);
        } else if( screen_orientation == 2 ) {
            horizontal_orientation.setChecked(true);
        }


        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mainActivity.zoom_level_change = 1;
                zoom_level.setText(String.valueOf(progress));
               mainActivity.update_table("ZOOM_VALUE",String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                bar_value = 1;
            }
        });

        image_display.setChecked(image_display_boolean);
        image_clickable.setChecked(image_is_clickable);
        my_image.setChecked(my_image_clickable);

        camera_info.setChecked(camera_info_clickable);
        clock_screen.setChecked(clock_screen_clickable);
        camera_upload.setChecked(camera_upload_clickable);
        ambeint_light.setChecked(ambeint_light_clickable);

        is_closed_circuit.setChecked(false);
        delay_time.setText(edit_delay_time);
        //closecircuit_code.setText(closecircuit_code_txt);
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.setting_dialog = false;
               dismiss();
            }
        });

        //TODO: Oct 29, 2201
        is_closed_circuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(is_closed_circuit.isChecked()) {
                    image_clickable.setChecked(false);
                    //Log.d("sfndaa", "circuit_closed");
                } else {
                    audioTrack=null;
                    Toast.makeText(context,R.string.protection_message, Toast.LENGTH_SHORT).show();
                    saveDialog();
                }
            }
        });
        //TODO: Oct 29, 2201

        horizontal_orientation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                portrait_orientation.setChecked(false);
                horizontal_orientation.setChecked(true);
            }
        });

        portrait_orientation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                portrait_orientation.setChecked(true);
                horizontal_orientation.setChecked(false);
            }
        });



        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDialog();
            }
        });

        return builder.create();
    }




    public void saveDialog(){
        String current_view = "0";
        String is_clicked = "0";
        String circuit_closed = "1";
        String my_image_clickable = "0";
        String camera_info_clickable = "0";
        String flash_light_clickable = "0";
        String clock_screen_clickable = "0";
        String camera_upload_clickable = "0";
        String ambeint_light_clickable = "0";
        String screen_orientation_clickable = "0";

        String zoomable = "0";

        Boolean external_open = mainActivity.keydown_long_pressed;

        String external_closed = mainActivity.read_from_table("CIRCUIT_CLOSED");
        if(image_display.isChecked()) {
            current_view = "1";
        }
        if(image_clickable.isChecked()) {
            is_clicked = "1";
        }
        if( my_image.isChecked()) {
            my_image_clickable = "1";
        }

        if( camera_info.isChecked()) {
            camera_info_clickable = "1";
        }


        if( clock_screen.isChecked()) {
            clock_screen_clickable = "1";
        }

        if( camera_upload.isChecked()) {
            camera_upload_clickable = "1";
        }

        if( ambeint_light.isChecked() ) {
            ambeint_light_clickable = "1";
        }

        if(is_closed_circuit.isChecked() && external_open == false) {
            circuit_closed = "0";
            mainActivity.update_footer_data("settings_save_mode");
        }

        if( portrait_orientation.isChecked() ) {
            screen_orientation_clickable = "1";
        }

        if( horizontal_orientation.isChecked() ) {
            screen_orientation_clickable = "2";
        }

        if( bar_value == 1 ) {
            zoomable = mainActivity.read_from_table("ZOOM_VALUE");
            Log.d("set_zoomvalue","val"+mainActivity.read_from_table("ZOOM_VALUE"));
        }

        String delay_period = delay_time.getText().toString();
        String closecircuit_code_val = closecircuit_code.getText().toString();




        listener.applySave(current_view, is_clicked, delay_period, circuit_closed, closecircuit_code_val, my_image_clickable, camera_info_clickable, clock_screen_clickable, camera_upload_clickable,ambeint_light_clickable, screen_orientation_clickable, zoomable );
        mainActivity.setting_dialog = false;
        dismiss();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (SettingsDialogListener) context;

        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement SettingsDialogListener");
        }
    }



    public interface SettingsDialogListener {
        void applySave(String current_view, String is_clicked, String delay_period, String circuit_closed, String closecircuit_code_val, String my_image_clickable, String camera_info_clickable, String clock_screen_clickable, String camera_upload_clickable, String ambeint_light_clickable, String screen_orientation_clickable,String zoomable );
    }
}
