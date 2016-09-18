package gbarto.ru.smeta;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class ListOverview extends AppCompatActivity
{
    private TextView mTextEmpty;

    ArrayList<WorkClass> WorkSet = new ArrayList<>();
    DBAdapter adapter = new DBAdapter(this);
    private WorkTypeClass TypeHere;
    ArrayAdapter<WorkClass> adapt;
    final private int GETTING_NEW_WORK = 1488;
    final private int SOSU_PENISI_ZA_200_RUBLEY = 200;
    final private int SOSU_PENISI_ZA_2000_RUBLEY = 2000;
    int id;
    ProjectClass Project;

    private String worktype;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_overview);

        mTextEmpty = (TextView) findViewById(R.id.list_overview_text_empty);

        Toolbar toolbar = (Toolbar) findViewById(R.id.list_overview_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TypeHere = (WorkTypeClass) getIntent().getSerializableExtra("keep_type");
        Project = (ProjectClass) getIntent().getSerializableExtra("Project");
        setTitle(TypeHere.name);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                onBackPressed();
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
                Intent intent = new Intent(ListOverview.this, SearchActivity.class);
                intent.putExtra("check_list", true);
                DBObject[] temp = adapter.getWorksByType(TypeHere.rowID);
                ArrayList<WorkClass> tmp3 = new ArrayList <>();
                for (WorkClass x : WorkSet)
                    tmp3.add(x);
                WorkClass[] tmp2 = new WorkClass[tmp3.size()];
                tmp2 = tmp3.toArray(tmp2);
                tmp3.clear();
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
                        tmp3.add(y);
                    else {
                        boolean bool = tmp2[l].rowID != x.rowID;
                        bool |= !tmp2[l].name.equals(x.name);
                        bool |= tmp2[l].measuring != ((WorkClass) x).measuring;
                        if (bool)
                            tmp3.add(y);
                    }
                }
                WorkClass x = new WorkClass(false, getString(R.string.unique_work), new ArrayList<Pair<Long, Float>>(), 0, 2, 0, 1);
                x.size = 1;
                tmp3.add(x);
                intent.putExtra("list", tmp3);
                startActivityForResult(intent, SOSU_PENISI_ZA_2000_RUBLEY);
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        Intent temp = new Intent();
        temp.putExtra("WorkSet", WorkSet);
        setResult(RESULT_OK, temp);
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
        if (Project.contains(TypeHere)) {
            ArrayList<WorkClass> tmp = Project.get(TypeHere);
            for (WorkClass w1 : tmp) {
                WorkClass w2 = (WorkClass) adapter.getRow(DBAdapter.WORKS_TABLE, w1.rowID);
                //if (w2 != null)
                //{
                    //w1.name = new String(w2.name);
                    //w1.measuring = w2.measuring;
                    WorkSet.add(w1);
                //}
            }
        }
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
                        x.putExtra("work_type", 2);
                        id = i;
                        startActivityForResult(x, SOSU_PENISI_ZA_200_RUBLEY);
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
                        return true;
                    }
                });
        l.setAdapter(adapt);

        if (adapt.getCount() == 0) mTextEmpty.setText(getString(R.string.list_overview_empty));
        else mTextEmpty.setText("");
    }

    private class MyListAdapter extends ArrayAdapter
    {
        public MyListAdapter() {
            super(ListOverview.this, R.layout.listlayout_works, WorkSet);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View item = getLayoutInflater().inflate(R.layout.listlayout_works, parent, false);

            WorkClass work = WorkSet.get(position);
            TextView t1 = (TextView)item.findViewById(R.id.work_name);
            t1.setText(work.name);
            {
                TextView t2 = (TextView)item.findViewById(R.id.price);
                double sum = work.getAmount();
                t2.setText(String.format("%.2f", sum));
            }

            boolean lmao = false;
            for (int i = 0; !lmao && i < work.Materials.size(); ++i)
                if (work.RealMaterials.get(i) == null)
                {
                    lmao = true;
                    break;
                }
            if (lmao) {
                //item.setBackgroundColor(getResources().getColor(R.color.work_material_not_choose));
                //вот этой пдсветко больше быть не должно
            } else {
                //item.setBackgroundColor(getResources().getColor(R.color.work_material_choose));
                //вот этой пдсветко больше быть не должно
            }
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
        }
        else if (requestCode == SOSU_PENISI_ZA_200_RUBLEY)
        {
            WorkSet.set(id, (WorkClass)data.getSerializableExtra("work"));
        }
        else if (requestCode == SOSU_PENISI_ZA_2000_RUBLEY)
        {
            ArrayList <WorkClass> tmp = (ArrayList <WorkClass>) data.getSerializableExtra("result");
            for (WorkClass x : tmp)
                WorkSet.add(x);
        }
        adapt.notifyDataSetChanged();

        if (adapt.getCount() == 0) mTextEmpty.setText(getString(R.string.list_overview_empty));
        else mTextEmpty.setText("");
    }
}
