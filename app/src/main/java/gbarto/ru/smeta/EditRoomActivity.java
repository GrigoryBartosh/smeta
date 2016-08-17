package gbarto.ru.smeta;

import android.content.Intent;
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
        setTitle(Project.name);
        setContentView(R.layout.activity_edit_room);


        Toolbar toolbar = (Toolbar) findViewById(R.id.edit_room_toolbar);

        ImageButton kitchenButton = (ImageButton)this.findViewById(R.id.kitchen_button);
        ImageButton bathroomButton = (ImageButton)this.findViewById(R.id.bathroom_button);
        ImageButton diningRoomButton = (ImageButton)this.findViewById(R.id.dining_room_button);
        ImageButton bedroomButton = (ImageButton)this.findViewById(R.id.bedroom_button);
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
                startActivity(go);
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
                startActivity(go);
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
                startActivity(go);
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
                startActivity(go);
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
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }
}
