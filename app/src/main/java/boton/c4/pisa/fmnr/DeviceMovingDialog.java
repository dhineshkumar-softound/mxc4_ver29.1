package boton.c4.pisa.fmnr;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatDialogFragment;

public class DeviceMovingDialog extends AppCompatDialogFragment {

    private Button start_again_button;
    public MainActivity obj;
    public DeviceMovingDialog(MainActivity _obj) {
        obj = _obj;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.device_moving_dialog, null);
        builder.setView(view).setTitle(R.string.device_moving_title);

        start_again_button = (Button) view.findViewById(R.id.start_again_btn);

        start_again_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new WavLog("start again location search", obj.getApplicationContext(), "DeviceMovingDialog", "onCreateDialog");
                obj.startSearchLocation(false);
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