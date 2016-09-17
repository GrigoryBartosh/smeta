package gbarto.ru.smeta;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeSet;

public class ChooseTypeActivity extends AppCompatActivity {

    private ArrayList<WorkTypeClass> WorkSet = new ArrayList<>();
    DBAdapter adapter = new DBAdapter(this);
    LinearLayout linearLayout;
    TextView seekBartitle;
    SeekBar seekBar;
    boolean userIsEditingNow = false;
    int editingid;
    ArrayAdapter<WorkClass> adapt;
    private static final int NAMING = 228;
    private static final int GETTING_NEW_TYPE = 1488;
    ProjectClass Project;
    WorkTypeClass tmp2;
    TreeSet<WorkTypeClass> incompleteTypes = new TreeSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_type);
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.choose_works_toolbar);
        Project = (ProjectClass) getIntent().getSerializableExtra("Project");
        setTitle(Project.works.get(Project.place).first.getVisible_name() + " : " + getString(R.string.work_types));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.main_fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(ChooseTypeActivity.this, SearchActivity.class);
                intent.putExtra("check_list", true);
                DBObject[] temp = adapter.getAllRows(DBAdapter.TYPES_TABLE);
                ArrayList<WorkTypeClass> tmp3 = new ArrayList <>();
                for (WorkTypeClass x : WorkSet)
                    tmp3.add(x);
                WorkTypeClass[] tmp2 = new WorkTypeClass[tmp3.size()];
                tmp2 = tmp3.toArray(tmp2);
                tmp3.clear();
                Arrays.sort(tmp2);
                for (DBObject x : temp) {
                    WorkTypeClass y = (WorkTypeClass) x;
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
                        if (bool)
                            tmp3.add(y);
                    }
                }
                intent.putExtra("list", tmp3);
                startActivityForResult(intent, GETTING_NEW_TYPE);
            }
        });
        seekBar = new SeekBar(this);
        seekBar.setMax(50);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                WorkTypeClass workTypeClass = WorkSet.get(editingid);
                workTypeClass.coeff = i * 0.1;
                if (!Project.works.get(Project.place).second.containsKey(workTypeClass))
                    Project.put(workTypeClass, new ArrayList<WorkClass>());
                else {
                    ArrayList<WorkClass> works = Project.get(workTypeClass);
                    Project.works.remove(workTypeClass);
                    Project.put(workTypeClass, works);
                }
                String temp = getString(R.string.current_coeff) + " " + String.format("%.1f", i * 0.1);
                seekBartitle.setText(temp);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });
        seekBartitle = new TextView(this);

        linearLayout = (LinearLayout)findViewById(R.id.works_linear_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        d = getResources().getDrawable(R.drawable.pencil_blue);
        d.setColorFilter(color, mMode);
        d.setAlpha(255);
    }

    private final ArrayList<View> mMenuItems = new ArrayList<>();

    @Override
    protected void onDestroy()
    {
        adapter.close();
        super.onDestroy();
    }

    @Override
    public void onBackPressed()
    {
        Intent x = new Intent();
        x.putExtra("Project", Project);
        x.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        setResult(RESULT_OK, x);
        finish();
    }

    private void default_values()
    {
        for (Map.Entry<WorkTypeClass, ArrayList<WorkClass>> x : Project.works.get(Project.place).second.entrySet())
        {
            if (x.getValue() == null)
                continue;
            WorkSet.add(x.getKey());
        }
    }

    private void AddAdapter()
    {
        adapt = new MyListAdapter();
        ListView l = (ListView)findViewById(R.id.works_listView);
        l.setOnItemClickListener(mItemListener);
        l.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l)
            {
                FragmentManager manager = getSupportFragmentManager();
                MyDialogFragment myDialogFragment = new MyDialogFragment();
                myDialogFragment.setTitle(getString(R.string.choose_type_delete_title));
                myDialogFragment.setMessage(getString(R.string.choose_type_delete_summary));
                myDialogFragment.setPositiveButtonTitle(getString(R.string.ok));
                myDialogFragment.setPositiveClicked(new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Project.works.get(Project.place).second.remove(WorkSet.get(i));
                        WorkSet.remove(i);
                    }
                });
                myDialogFragment.setUseNegativeButton(false);
                myDialogFragment.show(manager, "dialog");
                return true;
            }
        });
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

            if (userIsEditingNow && position == editingid)
                item.setBackgroundColor(getResources().getColor(R.color.place_chosen));
            WorkTypeClass w1 = WorkSet.get(position);
            TextView t1 = (TextView)item.findViewById(R.id.work_name);
            {
                TextView t2 = (TextView)item.findViewById(R.id.price);
                double sum = 0;
                ArrayList<WorkClass> temp = Project.get(w1);
                if (temp != null) {
                    for (WorkClass work : temp) {
                        sum += work.size * work.price;
                        for (int i = 0; i < work.RealMaterials.size(); ++i)
                            if (work.RealMaterials.get(i) != null) {
                                MaterialClass material = work.RealMaterials.get(i);
                                if (material.per_object < (1e-8)) {
                                    double wasted = work.size * work.Materials.get(i).second * material.price;
                                    sum += wasted;
                                } else {
                                    int amount = (int) Math.ceil((double) work.size * work.Materials.get(i).second / material.per_object);
                                    double wasted = amount * material.price;
                                    sum += wasted;
                                }
                            }
                        for (int i = 0; i < work.Instruments.size(); ++i) {
                            InstrumentClass tool = (InstrumentClass) adapter.getRow(DBAdapter.INSTRUMENT_TABLE, work.Instruments.get(i).first);
                            sum += tool.price * work.Instruments.get(i).second;
                        }
                        sum *= work.coefficient;
                    }
                }
                t2.setText(String.format("%.2f", sum));
            }

            t1.setText(w1.name);
            boolean bad = false;
            if (Project.contains(w1))
            {
                ArrayList<WorkClass> temp = Project.get(w1);
                for (WorkClass work : temp)
                    for (int i = 0; !bad && i < work.RealMaterials.size(); ++i)
                        if (work.RealMaterials.get(i) == null)
                            bad = true;
            }
            if (bad) {
                //item.setBackgroundColor(getResources().getColor(R.color.work_material_not_choose));
                //вот этой пдсветко больше быть не должно
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
            if (!userIsEditingNow) {
                WorkTypeClass tmp = (WorkTypeClass) adapterView.getItemAtPosition(i);
                Intent x = new Intent(ChooseTypeActivity.this, ListOverview.class);
                x.putExtra("keep_type", tmp);
                x.putExtra("Project", Project);
                tmp2 = tmp;
                startActivityForResult(x, NAMING);
            } else {
                editingid = i;
                seekBar.setProgress((int)Math.round(WorkSet.get(i).coeff * 10));
                adapt.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == NAMING) {
            ArrayList<WorkClass> tmp = (ArrayList<WorkClass>) data.getSerializableExtra("WorkSet");
            Project.put(tmp2, tmp);
            adapt.notifyDataSetChanged();
        } else if (requestCode == GETTING_NEW_TYPE) {
            ArrayList <WorkTypeClass> tmp = (ArrayList <WorkTypeClass>) data.getSerializableExtra("result");
            for (WorkTypeClass x : tmp) {
                WorkSet.add(x);
                Project.works.get(Project.place).second.put(x, new ArrayList<WorkClass>());
            }
            adapt.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
