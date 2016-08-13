package gbarto.ru.smeta;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class EditRoomActivity extends AppCompatActivity {

    public final static String APP = "com.example.noobgam.button.";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_room);

        Toolbar toolbar = (Toolbar) findViewById(R.id.edit_room_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getSupportFragmentManager();
                MyDialogFragment myDialogFragment = new MyDialogFragment();
                myDialogFragment.Message = "Если вы вернётесь, то потеряете всё.";
                myDialogFragment.Title = "Вы уверены, что хотите вернуться?.";
                myDialogFragment.PositiveButtonTitle = "Да";
                myDialogFragment.NegativeButtonTitle = "Нет";
                myDialogFragment.PositiveClicked = new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        Intent temp = new Intent();
                        temp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        setResult(RESULT_CANCELED, temp);
                        finish();
                    }
                };
                myDialogFragment.show(manager, "dialog");
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        FragmentManager manager = getSupportFragmentManager();
        MyDialogFragment myDialogFragment = new MyDialogFragment();
        myDialogFragment.Message = "Если вы вернётесь, то потеряете всё.";
        myDialogFragment.Title = "Вы уверены, что хотите вернуться?.";
        myDialogFragment.PositiveButtonTitle = "Да";
        myDialogFragment.NegativeButtonTitle = "Нет";
        myDialogFragment.PositiveClicked = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                Intent temp = new Intent();
                temp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                setResult(RESULT_CANCELED, temp);
                finish();
            }
        };
        myDialogFragment.show(manager, "dialog");
    }
}
