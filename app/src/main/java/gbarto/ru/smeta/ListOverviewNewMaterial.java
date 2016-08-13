package gbarto.ru.smeta;

import android.content.Intent;
import android.os.Bundle;
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
import java.util.Arrays;

public class ListOverviewNewMaterial extends AppCompatActivity {

    private ArrayList<TypeClass> WorkSet = new ArrayList<>();
    DBAdapter adapter = new DBAdapter(this);
    private int[] using;
    private ArrayList<TypeClass> returning = new ArrayList<>();
    private static final int NAMING = 228;
    private static final int GETTING_NEW_MATERIAL = 1488;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_overview_new_material);
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.list_overview_new_material_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent temp = new Intent();
                temp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                for (int i = 0; i < WorkSet.size(); ++i)
                    if (using[i] == 1)
                        returning.add(WorkSet.get(i));

                Bundle b = new Bundle();
                b.putSerializable("added", returning);
                temp.putExtras(b);

                setResult(RESULT_CANCELED, temp);
                finish();
            }
        });
        adapter.open();
        default_values();
        AddAdapter();
    }

    @Override
    protected void onDestroy()
    {
        adapter.close();
        super.onDestroy();
    }

    private void default_values()
    {

        String [] args = new String[1];
        args[0] = getIntent().getStringExtra("room");
        DBObject[] temp = adapter.getSelectionRows(adapter.TYPES_TABLE, adapter.TYPES_KEY_PLACE + " = ?", args);
        ArrayList<DBObject> tmp3 = (ArrayList <DBObject>) (getIntent().getExtras().getSerializable("have"));
        DBObject[] tmp2 = new DBObject[tmp3.size()];
        tmp2 = tmp3.toArray(tmp2);
        Arrays.sort(tmp2);
        for (DBObject x : temp) {
            int l = 0, r = tmp2.length;
            while (l < r - 1)
            {
                int mid = (l + r) >> 1;
                if (tmp2[mid].compareTo(x) > 0)
                    r = mid;
                else
                    l = mid;
            }
            if (tmp2.length == 0)
                WorkSet.add((TypeClass) x);
            else
                if (!tmp2[l].equals(x))
                    WorkSet.add((TypeClass) x);
        }
        using = new int[temp.length];
    }

    private void AddAdapter()
    {
        ArrayAdapter<WorkClass> adapter = new MyListAdapter();
        ListView l = (ListView)findViewById(R.id.list_overview_new_material_listView);
        l.setOnItemClickListener(mItemListener);
        l.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter
    {
        public MyListAdapter() {
            super(ListOverviewNewMaterial.this, R.layout.listlayout_works, WorkSet);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View item = getLayoutInflater().inflate(R.layout.listlayout_works, parent, false);

            TypeClass w1 = WorkSet.get(position);
            TextView t1 = (TextView)item.findViewById(R.id.work_name);
            t1.setText(w1.type);

            CheckBox cb1 = (CheckBox)item.findViewById(R.id.checkBox);
            cb1.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    using[position] ^= 1;
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

}
