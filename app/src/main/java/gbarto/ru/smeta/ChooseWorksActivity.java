package gbarto.ru.smeta;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ChooseWorksActivity extends AppCompatActivity {

    private ArrayList<TypeClass> WorkSet = new ArrayList<>();
    DBAdapter adapter = new DBAdapter(this);
    ArrayAdapter<WorkClass> adapt;
    private static final int NAMING = 228;
    private static final int GETTING_NEW_MATERIAL = 1488;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_works);
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.choose_works_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent temp = new Intent();
                temp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                setResult(RESULT_CANCELED, temp);
                finish();
            }
        });
        adapter.open();
        default_values();
        AddAdapter();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent x = new Intent(ChooseWorksActivity.this, ListOverviewNewMaterial.class);
                x.putExtra("new_material", 1);
                Bundle b = new Bundle();
                b.putSerializable("have", WorkSet);
                x.putExtras(b);
                x.putExtras(getIntent());
                startActivityForResult(x, GETTING_NEW_MATERIAL);
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        adapter.close();
        super.onDestroy();
    }

    private void default_values()
    {

        DBObject[] temp = adapter.getAllRows(adapter.TYPES_TABLE);
        //Arrays.sort(temp);
        //for (DBObject x : temp)
        //WorkSet.add((TypeClass) x);
    }

    private void AddAdapter()
    {
        adapt = new MyListAdapter();
        ListView l = (ListView)findViewById(R.id.works_listView);
        l.setOnItemClickListener(mItemListener);
        l.setAdapter(adapt);
    }

    private class MyListAdapter extends ArrayAdapter
    {
        public MyListAdapter() {
            super(ChooseWorksActivity.this, R.layout.listlayout, WorkSet);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View item = getLayoutInflater().inflate(R.layout.listlayout, parent, false);

            TypeClass w1 = WorkSet.get(position);
            TextView t1 = (TextView)item.findViewById(R.id.work_name);
            t1.setText(w1.type);
            ImageView img = (ImageView) item.findViewById(R.id.icon_right);
            img.setImageResource(R.drawable.ic_button_next);
            return item;
        }
    }

    AdapterView.OnItemClickListener mItemListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
        {
            TypeClass tmp = (TypeClass) adapterView.getItemAtPosition(i);
            Intent x = new Intent(ChooseWorksActivity.this, ListOverview.class);
            x.putExtra("room_place", tmp.getPlace());
            x.putExtra("room_type", tmp.getType());
            startActivityForResult(x, NAMING);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == NAMING) {
            //adapter.add(new WorkClass())
        }
        else if (requestCode == GETTING_NEW_MATERIAL) {
            ArrayList <DBObject> cur = (ArrayList <DBObject>) data.getExtras().getSerializable("added");
            for (DBObject x : cur)
                WorkSet.add((TypeClass) x);
            adapt.notifyDataSetChanged();
        }
    }

}
