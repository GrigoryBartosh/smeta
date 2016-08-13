package gbarto.ru.smeta;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

public class EditRoomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getIntent().getStringExtra("ProjectName"));
        setContentView(R.layout.activity_edit_room);


        Toolbar toolbar = (Toolbar) findViewById(R.id.edit_room_toolbar);

        ImageButton kitchenButton = (ImageButton)this.findViewById(R.id.kitchen_button);
        ImageButton bathroomButton = (ImageButton)this.findViewById(R.id.bathroom_button);
        ImageButton diningRoomButton = (ImageButton)this.findViewById(R.id.dining_room_button);
        ImageButton bedroomButton = (ImageButton)this.findViewById(R.id.bedroom_button);
        kitchenButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                Intent go = new Intent(EditRoomActivity.this, ChooseWorksActivity.class);
                go.putExtra("room", getString(R.string.room_kitchen));
                go.putExtras(getIntent());
                startActivity(go);
            }
        });
        bathroomButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                Intent go = new Intent(EditRoomActivity.this, ChooseWorksActivity.class);
                go.putExtra("room", getString(R.string.room_bathroom));
                go.putExtras(getIntent());
                startActivity(go);
            }
        });
        diningRoomButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                Intent go = new Intent(EditRoomActivity.this, ChooseWorksActivity.class);
                go.putExtra("room", getString(R.string.room_dining_room));
                go.putExtras(getIntent());
                startActivity(go);
            }
        });
        bedroomButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                Intent go = new Intent(EditRoomActivity.this, ChooseWorksActivity.class);
                go.putExtra("room", getString(R.string.room_bedroom));
                go.putExtras(getIntent());
                startActivity(go);
            }
        });

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
