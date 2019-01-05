package us.ait.android.weatherinfo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Date;

import us.ait.android.weatherinfo.data.City;

public class NewCityDialog extends DialogFragment{

    public interface CityHandler {
        public void cityCreated(City city);
    }

    private CityHandler cityHandler;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof CityHandler){
            cityHandler = (CityHandler) context;
        }else{
            throw new RuntimeException(getString(R.string.error));
        }
    }

    private EditText etNewCity;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.new_city);
        etNewCity = new EditText(getActivity());
        initBuilder(builder);

        return builder.create();
    }

    private void initBuilder(AlertDialog.Builder builder) {
        builder.setView(etNewCity);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cityHandler.cityCreated(new City(etNewCity.getText().toString()));
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        final AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);

            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(etNewCity.getText().toString())) {
                        cityHandler.cityCreated(new City(etNewCity.getText().toString()));
                        dialog.dismiss();
                    } else {
                        etNewCity.setError(getString(R.string.error_message_empty));
                    }
                }
            });
        }
    }

}
