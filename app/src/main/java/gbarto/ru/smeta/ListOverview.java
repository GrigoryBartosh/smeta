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
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListOverview extends AppCompatActivity
{

    ArrayList<WorkClass> WorkSet = new ArrayList<>();
    DBAdapter adapter = new DBAdapter(this);
    ArrayAdapter<WorkClass> adapt;
    final private int GETTING_NEW_WORK = 1488;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_overview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.list_overview_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String worktype = getIntent().getStringExtra("room") + " : " + getIntent().getStringExtra("room_type");
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
        adapter.open();
        default_values();
        AddAdapter();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent x = new Intent(ListOverview.this, ListOverViewNewWork.class);
                x.putExtra("new_material", 1);
                Bundle b = new Bundle();
                b.putSerializable("have_works", WorkSet);
                x.putExtras(b);
                x.putExtras(getIntent());
                startActivityForResult(x, GETTING_NEW_WORK);
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
        DBObject[] temp = adapter.getAllRows(adapter.WORKS_TABLE);
    }

    private void AddAdapter()
    {
        adapt = new MyListAdapter();
        ListView l = (ListView)findViewById(R.id.list_overview_listview);
        l.setOnItemClickListener(mItemListener);
        l.setAdapter(adapt);
    }

    private class MyListAdapter extends ArrayAdapter
    {
        public MyListAdapter() {
            super(ListOverview.this, R.layout.listlayout_works, WorkSet);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View item = getLayoutInflater().inflate(R.layout.listlayout_works, parent, false);

            WorkClass w1 = WorkSet.get(position);
            TextView t1 = (TextView)item.findViewById(R.id.work_name);
            t1.setText(w1.type);

            CheckBox cb1 = (CheckBox)item.findViewById(R.id.checkBox);
            cb1.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    //using[position] ^= 1;
                }
            });

            return item;
        }
    }

    AdapterView.OnItemClickListener mItemListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
        {
            TypeClass tmp = (TypeClass) adapterView.getItemAtPosition(i);
            System.out.println(tmp.getType());
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == GETTING_NEW_WORK) {
            ArrayList <WorkClass> temp = (ArrayList <WorkClass>) data.getSerializableExtra("new_works");
            for (WorkClass x : temp)
                WorkSet.add(x);
            adapt.notifyDataSetChanged();
        }
    }
}
