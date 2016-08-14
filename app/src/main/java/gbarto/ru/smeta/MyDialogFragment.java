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

    String Message;
    String Title;
    String PositiveButtonTitle;
    String NegativeButtonTitle;
    DialogInterface.OnClickListener PositiveClicked, NegativeClicked;
    Boolean UseNegativeButton = true;

    public MyDialogFragment()
    {

    }

    public String getMessage()
    {
        return Message;
    }

    public void setMessage(String message)
    {
        Message = message;
    }

    public String getTitle()
    {
        return Title;
    }

    public void setTitle(String title)
    {
        Title = title;
    }

    public String getPositiveButtonTitle()
    {
        return PositiveButtonTitle;
    }

    public void setPositiveButtonTitle(String positiveButtonTitle)
    {
        PositiveButtonTitle = positiveButtonTitle;
    }

    public String getNegativeButtonTitle()
    {
        return NegativeButtonTitle;
    }

    public void setNegativeButtonTitle(String negativeButtonTitle)
    {
        NegativeButtonTitle = negativeButtonTitle;
    }

    public DialogInterface.OnClickListener getPositiveClicked()
    {
        return PositiveClicked;
    }

    public void setPositiveClicked(DialogInterface.OnClickListener positiveClicked)
    {
        PositiveClicked = positiveClicked;
    }

    public DialogInterface.OnClickListener getNegativeClicked()
    {
        return NegativeClicked;
    }

    public void setNegativeClicked(DialogInterface.OnClickListener negativeClicked)
    {
        NegativeClicked = negativeClicked;
    }

    public Boolean getUseNegativeButton()
    {
        return UseNegativeButton;
    }

    public void setUseNegativeButton(Boolean useNegativeButton)
    {
        UseNegativeButton = useNegativeButton;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(Message)
                .setTitle(Title)
                .setPositiveButton(PositiveButtonTitle, PositiveClicked);
        if (UseNegativeButton)
            builder.setNegativeButton(NegativeButtonTitle, NegativeClicked);
        return builder.create();
    }
}