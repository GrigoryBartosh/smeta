package gbarto.ru.smeta;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;

public class ListOverview extends AppCompatActivity
{

    ArrayList<DBObject> x = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_overview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.list_overview_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String worktype = getIntent().getStringExtra("room_place") + " : " + getIntent().getStringExtra("room_type");
        setTitle(worktype);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent temp = new Intent();
                setResult(RESULT_CANCELED, temp);
                finish();
            }
        });
    }

}
