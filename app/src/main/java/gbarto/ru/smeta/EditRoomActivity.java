package gbarto.ru.smeta;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

public class EditRoomActivity extends AppCompatActivity {

    private void returning(int room)
    {
        Intent go = new Intent(EditRoomActivity.this, ChooseTypeActivity.class);
        go.putExtras(getIntent());
        go.putExtra("new_room", getString(room));
        setResult(RESULT_OK, go);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_room);


        Toolbar toolbar = (Toolbar) findViewById(R.id.edit_room_toolbar);

        ImageButton roomButton = (ImageButton)this.findViewById(R.id.room_room_button);
        roomButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                returning(R.string.room_room);
            }
        });
        ImageButton bathroomButton = (ImageButton)this.findViewById(R.id.bathroom_button);
        bathroomButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                returning(R.string.room_bath_toilet);
            }
        });
        ImageButton kitchenButton = (ImageButton)this.findViewById(R.id.kitchen_button);
        kitchenButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                returning(R.string.room_kitchen);
            }
        });
        ImageButton corridorButton = (ImageButton)this.findViewById(R.id.corridor_button);
        corridorButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                returning(R.string.room_corridor);
            }
        });
        ImageButton balconyButton = (ImageButton)this.findViewById(R.id.balcony_button);
        balconyButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                returning(R.string.room_balcony);
            }
        });
        ImageButton otherroomButton = (ImageButton)this.findViewById(R.id.other_button);
        otherroomButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                returning(R.string.room_other);
            }
        });


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED, new Intent());
                finish();
            }
        });

        int color = getResources().getColor(R.color.ic_menu);
        PorterDuff.Mode mMode = PorterDuff.Mode.SRC_ATOP;
        Drawable d;
        d = getResources().getDrawable(android.R.drawable.ic_menu_help);
        d.setColorFilter(color, mMode);
        d.setAlpha(255);
        d = getResources().getDrawable(android.R.drawable.ic_menu_save);
        d.setColorFilter(color, mMode);
        d.setAlpha(255);
        d = getResources().getDrawable(android.R.drawable.ic_menu_edit);
        d.setColorFilter(color, mMode);
        d.setAlpha(255);
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }
}
