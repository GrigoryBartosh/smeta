package gbarto.ru.smeta;

/**
 * Created by Noobgam on 12.08.2016.
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

public class MyDialogFragment extends DialogFragment {

    String s1, s2, s3, s4;

    public MyDialogFragment()
    {
        System.out.println(1);
    }

    public void use(String s1, String s2, String s3, String s4)
    {
        this.s1 = s1;
        this.s2 = s2;
        this.s3 = s3;
        this.s4 = s4;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        System.out.println(2);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(s1)
                .setTitle(s2)
                .setPositiveButton(s3, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ((EditRoomActivity) getActivity()).okClicked();
                    }
                })
                .setNegativeButton(s4, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ((EditRoomActivity) getActivity()).cancelClicked();
                    }
                });

        return builder.create();
    }
}