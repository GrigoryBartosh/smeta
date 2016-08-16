package gbarto.ru.smeta;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
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
    private WorkTypeClass TypeHere;
    ArrayAdapter<WorkClass> adapt;
    final private int GETTING_NEW_WORK = 1488;
    private String worktype;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_overview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.list_overview_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TypeHere = (WorkTypeClass) getIntent().getSerializableExtra("keep_type");
        worktype = getIntent().getStringExtra("room") + ":" + getIntent().getStringExtra("room_type");
        setTitle(worktype);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent temp = new Intent();
                Bundle b = new Bundle();
                b.putSerializable(worktype + ":WorkSet", WorkSet);
                temp.putExtras(b);
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
                x.putExtra("have_works", WorkSet);
                x.putExtra("keep_type", TypeHere);
                startActivityForResult(x, GETTING_NEW_WORK);
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        Intent temp = new Intent();
        Bundle b = new Bundle();
        b.putSerializable(worktype + ":WorkSet", WorkSet);
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
        if (getIntent().getSerializableExtra(worktype + ":WorkSet") != null)
            WorkSet = (ArrayList <WorkClass>)getIntent().getSerializableExtra(worktype + ":WorkSet");
    }

    private void AddAdapter()
    {
        adapt = new MyListAdapter();
        ListView l = (ListView)findViewById(R.id.list_overview_listview);
        l.setOnItemClickListener(
                new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
                    {
                        WorkClass tmp = (WorkClass) adapterView.getItemAtPosition(i);
                        System.out.println(tmp.getName());
                        Intent x = new Intent(ListOverview.this, WorkActivity.class);
                        x.putExtra("work", new WorkClass(tmp));
                    }
                });
        l.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener()
                {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
                    {
                        final int position = i;
                        FragmentManager manager = getSupportFragmentManager();
                        MyDialogFragment myDialogFragment = new MyDialogFragment();
                        myDialogFragment.Message = "Уверены, что хотите удалить работу?.";
                        myDialogFragment.Title = "";
                        myDialogFragment.PositiveButtonTitle = "Да";
                        myDialogFragment.NegativeButtonTitle = "Нет";
                        myDialogFragment.PositiveClicked = new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                WorkSet.remove(position);
                                adapt.notifyDataSetChanged();
                            }
                        };
                        myDialogFragment.show(manager, "dialog");
                        return false;
                    }
                });
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
            t1.setText(w1.name);

            boolean lmao = false;
            for (int i = 0; !lmao && i < w1.Materials.size(); ++i)
                if (w1.RealMaterials.get(i) == -1L)
                {
                    lmao = true;
                    break;
                }
            if (lmao)
                item.setBackgroundColor(Color.RED);
            else
                item.setBackgroundColor(Color.GREEN);
            CheckBox cb1 = (CheckBox)item.findViewById(R.id.checkBox);
            cb1.setVisibility(View.INVISIBLE);
            return item;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == GETTING_NEW_WORK) {
            ArrayList <WorkClass> temp = (ArrayList <WorkClass>) data.getSerializableExtra("new_works");
            for (WorkClass x : temp)
                WorkSet.add(x);
            if (getIntent().getSerializableExtra(worktype + ":WorkSet") != null)
                getIntent().removeExtra(worktype + ":WorkSet");
            getIntent().putExtra(worktype + ":WorkSet", WorkSet);
            adapt.notifyDataSetChanged();
        }
    }
}
