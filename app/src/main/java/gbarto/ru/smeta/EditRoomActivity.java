package gbarto.ru.smeta;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class EditRoomActivity extends AppCompatActivity {

    public final static String APP = "com.example.noobgam.button.";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_room);

        Button x = (Button)findViewById(R.id.nextButton);
        Toolbar toolbar = (Toolbar) findViewById(R.id.edit_room_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getSupportFragmentManager();
                MyDialogFragment myDialogFragment = new MyDialogFragment();
                myDialogFragment.use("Ты пидор?", "Ты пидор?", "Да", "Не");
                myDialogFragment.show(manager, "dialog");
            }
        });

        x.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent go = new Intent(EditRoomActivity.this, ChooseWorksActivity.class);
                go.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(go);
            }
        });

    }

    @Override
    public void onBackPressed()
    {
        FragmentManager manager = getSupportFragmentManager();
        MyDialogFragment myDialogFragment = new MyDialogFragment();
        myDialogFragment.show(manager, "dialog");
        super.onBackPressed();
    }

    public void okClicked() {
        Intent temp = new Intent();
        temp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        setResult(RESULT_CANCELED, temp);
        finish();
    }

    public void cancelClicked() {
    }
}
