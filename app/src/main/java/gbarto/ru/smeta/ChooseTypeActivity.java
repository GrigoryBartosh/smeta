package gbarto.ru.smeta;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class ChooseTypeActivity extends AppCompatActivity {

    private ArrayList<WorkTypeClass> WorkSet = new ArrayList<>();
    DBAdapter adapter = new DBAdapter(this);
    ArrayAdapter<WorkClass> adapt;
    private static final int NAMING = 228;
    private static final int GETTING_NEW_MATERIAL = 1488;
    ProjectClass Project;
    WorkTypeClass tmp2;
    TreeSet<WorkTypeClass> incompleteTypes = new TreeSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_type);
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.choose_works_toolbar);
        Project = (ProjectClass) getIntent().getSerializableExtra("Project");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getSupportFragmentManager();
                MyDialogFragment myDialogFragment = new MyDialogFragment();
                myDialogFragment.Message = "Если вы вернётесь, то потеряете всё.";
                myDialogFragment.Title = "Вы уверены, что хотите вернуться?.";
                myDialogFragment.PositiveButtonTitle = "Да";
                myDialogFragment.NegativeButtonTitle = "Нет";
                myDialogFragment.PositiveClicked = new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        Intent temp = new Intent();
                        temp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        setResult(RESULT_CANCELED, temp);
                        finish();
                    }
                };
                myDialogFragment.show(manager, "dialog");
            }
        });
        adapter.open();
        default_values();
        AddAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_choose_types, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (incompleteTypes.size() != 0)
            Toast.makeText(getApplicationContext(), getString(R.string.makeSureAllTypesAreComplete), Toast.LENGTH_SHORT).show();
        else {
            FileManager fileManager = new FileManager(this);
            fileManager.Save(Project);
            Intent x = new Intent(ChooseTypeActivity.this, MainActivity.class);
            x.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(x);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy()
    {
        adapter.close();
        super.onDestroy();
    }

    @Override
    public void onBackPressed()
    {
        FragmentManager manager = getSupportFragmentManager();
        MyDialogFragment myDialogFragment = new MyDialogFragment();
        myDialogFragment.Message = "Если вы вернётесь, то потеряете всё.";
        myDialogFragment.Title = "Вы уверены, что хотите вернуться?.";
        myDialogFragment.PositiveButtonTitle = "Да";
        myDialogFragment.NegativeButtonTitle = "Нет";
        myDialogFragment.PositiveClicked = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                Intent temp = new Intent();
                temp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                setResult(RESULT_CANCELED, temp);
                finish();
            }
        };
        myDialogFragment.show(manager, "dialog");
    }

    private void default_values()
    {
        DBObject[] temp = adapter.getAllRows(DBAdapter.TYPES_TABLE);
        Set<WorkTypeClass> all = new TreeSet<>();
        for (int i = 0; i < temp.length; ++i)
            all.add((WorkTypeClass)temp[i]);
        ArrayList <WorkTypeClass> deleting = new ArrayList<>();
        for (Map.Entry<WorkTypeClass, ArrayList<WorkClass>> x : Project.works.entrySet()) {
            WorkTypeClass x1 = new WorkTypeClass(x.getKey());
            if (!all.contains(x1))
                deleting.add(x1);
        }
        for (WorkTypeClass x : deleting)
                Project.works.remove(x);
        //Arrays.sort(temp);
        for (DBObject x : temp)
            WorkSet.add((WorkTypeClass) x);
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
            super(ChooseTypeActivity.this, R.layout.listlayout, WorkSet);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View item = getLayoutInflater().inflate(R.layout.listlayout, parent, false);

            WorkTypeClass w1 = WorkSet.get(position);
            TextView t1 = (TextView)item.findViewById(R.id.work_name);
            t1.setText(w1.name);
            ImageView img = (ImageView) item.findViewById(R.id.icon_right);
            img.setImageResource(R.drawable.ic_button_next);
            boolean bad = false;
            if (Project.contains(w1))
            {
                ArrayList<WorkClass> temp = Project.get(w1);
                for (WorkClass work : temp)
                    for (int i = 0; !bad && i < work.RealMaterials.size(); ++i)
                        if (work.RealMaterials.get(i) == -1L)
                            bad = true;
            }
            if (bad) {
                item.setBackgroundColor(getResources().getColor(R.color.work_material_not_choose));
                incompleteTypes.add(w1);
            }
            else
                if (incompleteTypes.contains(w1))
                    incompleteTypes.remove(w1);
            return item;
        }
    }

    AdapterView.OnItemClickListener mItemListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
        {
            WorkTypeClass tmp = (WorkTypeClass) adapterView.getItemAtPosition(i);
            Intent x = new Intent(ChooseTypeActivity.this, ListOverview.class);
            x.putExtra("keep_type", tmp);
            x.putExtra("Project", Project);
            tmp2 = tmp;
            startActivityForResult(x, NAMING);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        ArrayList<WorkClass> tmp = (ArrayList<WorkClass>)data.getSerializableExtra("WorkSet");
        Project.put(tmp2, tmp);
        adapt.notifyDataSetChanged();
        super.onActivityResult(requestCode, resultCode, data);
    }
}
