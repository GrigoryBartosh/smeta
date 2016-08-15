package gbarto.ru.smeta;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class ListOverViewNewWork extends AppCompatActivity {

    private ArrayList<WorkClass> WorkSet = new ArrayList<>();
    DBAdapter adapter = new DBAdapter(this);
    private int[] using;
    private ArrayList<WorkClass> returning = new ArrayList<>();
    private static final int NAMING = 228;
    private static final int GETTING_NEW_MATERIAL = 1488;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_overview_new_work);
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.list_overview_new_material_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent temp = new Intent();
                temp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                Bundle b = new Bundle();
                b.putSerializable("new_works", returning);
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
    public void onBackPressed()
    {
        Intent temp = new Intent();
        temp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Bundle b = new Bundle();
        b.putSerializable("new_works", returning);
        temp.putExtras(b);

        setResult(RESULT_CANCELED, temp);
        finish();
    }

    @Override
    protected void onDestroy()
    {
        adapter.close();
        super.onDestroy();
    }

    private void default_values()
    {

        DBObject[] temp = adapter.getWorksByType(((WorkTypeClass) getIntent().getSerializableExtra("keep_type")).rowID);
        //DBObject[] temp = adapter.getSelectionRows(adapter.TYPES_TABLE, adapter.TYPES_KEY_PLACE + " = ?", args);
        ArrayList<WorkClass> tmp3 = (ArrayList <WorkClass>) (getIntent().getExtras().getSerializable("have_works"));
        WorkClass[] tmp2 = new WorkClass[tmp3.size()];
        tmp2 = tmp3.toArray(tmp2);
        Arrays.sort(tmp2);
        for (DBObject x : temp) {
            WorkClass y = (WorkClass) x;
            int l = 0, r = tmp2.length;
            while (l < r - 1)
            {
                int mid = (l + r) >> 1;
                if (tmp2[mid].compareTo(y) > 0)
                    r = mid;
                else
                    l = mid;
            }
            if (tmp2.length == 0)
                WorkSet.add(y);
            else
                if (!tmp2[l].equals(x))
                    WorkSet.add(y);
        }
        using = new int[WorkSet.size()];
    }

    private void AddAdapter()
    {
        ArrayAdapter<WorkClass> adapter = new MyListAdapter();
        ListView l = (ListView)findViewById(R.id.list_overview_new_material_listView);
        l.setOnItemClickListener(mItemListener);
        l.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent temp = new Intent();
        temp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        for (int i = 0; i < WorkSet.size(); ++i)
            if (using[i] == 1)
                returning.add(WorkSet.get(i));

        Bundle b = new Bundle();
        b.putSerializable("new_works", returning);
        temp.putExtras(b);

        setResult(RESULT_CANCELED, temp);
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list_overview_new_works, menu);
        return true;
    }

    private class MyListAdapter extends ArrayAdapter
    {
        public MyListAdapter() {
            super(ListOverViewNewWork.this, R.layout.listlayout_works, WorkSet);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View item = getLayoutInflater().inflate(R.layout.listlayout_works, parent, false);

            WorkClass w1 = WorkSet.get(position);
            TextView t1 = (TextView)item.findViewById(R.id.work_name);
            t1.setText(w1.name);

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
            WorkTypeClass tmp = (WorkTypeClass) adapterView.getItemAtPosition(i);
            System.out.println(tmp.getName());
        }
    };

}
