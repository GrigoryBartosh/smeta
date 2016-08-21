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

    ProjectClass Project;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Project = (ProjectClass)getIntent().getSerializableExtra("Project");
        setContentView(R.layout.activity_edit_room);


        Toolbar toolbar = (Toolbar) findViewById(R.id.edit_room_toolbar);

        ImageButton kitchenButton = (ImageButton)this.findViewById(R.id.kitchen_button);
        ImageButton bathroomButton = (ImageButton)this.findViewById(R.id.bathroom_button);
        ImageButton diningRoomButton = (ImageButton)this.findViewById(R.id.dining_room_button);
        ImageButton bedroomButton = (ImageButton)this.findViewById(R.id.bedroom_button);
        ImageButton otherroomButton = (ImageButton)this.findViewById(R.id.other_room_button);
        if (Project.place != null)
        {
            if (Project.place.equals(getString(R.string.room_kitchen)))
                kitchenButton.setBackgroundColor(getResources().getColor(R.color.place_chosen));
            else if (Project.place.equals(getString(R.string.room_dining_room)))
                diningRoomButton.setBackgroundColor(getResources().getColor(R.color.place_chosen));
            else if (Project.place.equals(getString(R.string.room_bathroom)))
                bathroomButton.setBackgroundColor(getResources().getColor(R.color.place_chosen));
            else if (Project.place.equals(getString(R.string.room_bedroom)))
                bedroomButton.setBackgroundColor(getResources().getColor(R.color.place_chosen));
            else if (Project.place.equals(getString(R.string.room_other_room)))
                otherroomButton.setBackgroundColor(getResources().getColor(R.color.place_chosen));
        }

        kitchenButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                Intent go = new Intent(EditRoomActivity.this, ChooseTypeActivity.class);
                go.putExtras(getIntent());
                Project.place = getString(R.string.room_kitchen);
                go.putExtra("Project", Project);
                setResult(RESULT_OK, go);
                finish();
            }
        });
        bathroomButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                Intent go = new Intent(EditRoomActivity.this, ChooseTypeActivity.class);
                Project.place = getString(R.string.room_bathroom);
                go.putExtra("Project", Project);
                setResult(RESULT_OK, go);
                finish();
            }
        });
        diningRoomButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                Intent go = new Intent(EditRoomActivity.this, ChooseTypeActivity.class);
                Project.place = getString(R.string.room_dining_room);
                go.putExtra("Project", Project);
                setResult(RESULT_OK, go);
                finish();
            }
        });
        bedroomButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                Intent go = new Intent(EditRoomActivity.this, ChooseTypeActivity.class);
                Project.place = getString(R.string.room_bedroom);
                go.putExtra("Project", Project);
                setResult(RESULT_OK, go);
                finish();
            }
        });
        otherroomButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                Intent go = new Intent(EditRoomActivity.this, ChooseTypeActivity.class);
                Project.place = getString(R.string.room_other_room);
                go.putExtra("Project", Project);
                setResult(RESULT_OK, go);
                finish();
            }
        });


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
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
