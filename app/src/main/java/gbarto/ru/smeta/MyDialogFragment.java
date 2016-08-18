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
    DialogInterface.OnClickListener PositiveClicked, NegativeClicked, ListClicked;
    String[] List;
    Boolean UseMessage = true;
    Boolean UsePositiveButton = true;
    Boolean UseNegativeButton = true;
    Boolean UseList = false;

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

    public String[] getList()
    {
        return List;
    }

    public void setList(String[] list)
    {
        List = list;
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

    public DialogInterface.OnClickListener getListClicked()
    {
        return ListClicked;
    }

    public void setListClicked(DialogInterface.OnClickListener listClicked)
    {
        ListClicked = listClicked;
    }

    public Boolean getUseMessage()
    {
        return UseMessage;
    }

    public void setUseMessage(Boolean useMessage)
    {
        UseMessage = useMessage;
    }

    public Boolean getUsePositiveButton()
    {
        return UsePositiveButton;
    }

    public void setUsePositiveButton(Boolean usePositiveButton)
    {
        UsePositiveButton = usePositiveButton;
    }

    public Boolean getUseNegativeButton()
    {
        return UseNegativeButton;
    }

    public void setUseNegativeButton(Boolean useNegativeButton)
    {
        UseNegativeButton = useNegativeButton;
    }

    public Boolean getUseList()
    {
        return UseList;
    }

    public void setUseList(Boolean useList)
    {
        UseList = useList;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(Title);
        if (UseMessage)
            builder.setMessage(Message);
        if (UseList)
            builder.setItems(List, ListClicked);
        if (UsePositiveButton)
            builder.setPositiveButton(PositiveButtonTitle, PositiveClicked);
        if (UseNegativeButton)
            builder.setNegativeButton(NegativeButtonTitle, NegativeClicked);
        return builder.create();
    }
}