package boton.c4.pisa.fmns;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialogFragment;

public class CloseCircuitDialog extends AppCompatDialogFragment {



    private Button yes_button,no_button;
    public EditText enter_confirm_code;
    public String confirm_code;
    public MainActivity obj;
    public TextView closecircuit_countdown;
    public Handler seconds_handler;
    public Runnable seconds_runnable;
    public int count_down = 30;
    public CloseCircuitDialog(MainActivity _obj) {
        obj = _obj;
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.closecircuit_dialog, null);
        builder.setView(view).setTitle(R.string.closecircuit_dialog_title);

        yes_button = (Button) view.findViewById(R.id.yes_btn);
        no_button = (Button) view.findViewById(R.id.no_btn);
        enter_confirm_code = (EditText) view.findViewById(R.id.enter_confirm_code);
        closecircuit_countdown = (TextView) view.findViewById((R.id.closecircuit_countdown));

        obj.update_footer_data("waiting_popup_open");


        //
        seconds_handler = new Handler();
        seconds_runnable = new Runnable() {
            public void run() {
                if (count_down > 0) {
                    count_down = count_down - 1;
                    closecircuit_countdown.setText(count_down + " " + getString(R.string.seconds_left));
                } else if(count_down==0){
                    count_down = count_down - 1;
                    //Log.d("ssdd", "aa: "+count_down);
                    obj.touchDownEvent("click_mode");
                    obj.touchUpEvent("click_mode");
                    obj.update_table("CIRCUIT_CLOSED", "1");
                    obj.update_table("CIRCUIT_REQUEST_CLOSED", "1");
                    //obj.stop_audio_track();
                    //obj.update_footer_data("time_expired");
                    seconds_handler.removeCallbacks(seconds_runnable);
                    dismiss();
                }
                seconds_handler.postDelayed(this, 1000);  //for interval...
            }
        };
        seconds_handler.postDelayed(seconds_runnable, 1000); //for initial delay..
        //

        enter_confirm_code.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    confirm_code = enter_confirm_code.getText().toString();
                    String closecircuit_code_val = obj.read_from_table("CIRCUIT_CLOSED_CODE");

                    if(!closecircuit_code_val.equals(confirm_code)){

                        obj.touchDownEvent("click_mode");
                        obj.touchUpEvent("click_mode");
                        obj.update_table("CIRCUIT_CLOSED", "1");
                        obj.update_table("CIRCUIT_REQUEST_CLOSED", "1");
                        //obj.stop_audio_track();
                        seconds_handler.removeCallbacks(seconds_runnable);
                        dismiss();
                    } else {
                        obj.update_table("CIRCUIT_CLOSED", "1");
                        seconds_handler.removeCallbacks(seconds_runnable);
                        obj.update_footer_data("code_verific_accepted");
                        //obj.stop_audio_track();
                        dismiss();
                    }
                    //Log.d("enter_confirm_code", "confirm_code: "+confirm_code+" - closecircuit_code_val: "+closecircuit_code_val);
                }
                return false;
            }
        });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }
}
