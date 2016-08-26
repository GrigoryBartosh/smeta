package gbarto.ru.smeta;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
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
                Intent x = new Intent();
                x.putExtra("Project", Project);
                x.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                setResult(RESULT_OK, x);
                finish();
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
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_choose_types, menu);
        // Adjust the text color based on the connection
            final View decor = getWindow().getDecorView();
            decor.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
            {
                @Override
                public void onGlobalLayout()
                {
                    decor.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    decor.findViewsWithText(mMenuItems, getString(R.string.done),
                            View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
                    final TextView connected = !mMenuItems.isEmpty() ? (TextView) mMenuItems.get(0) : null;
                    if (connected != null) {
                        connected.setTextColor(getResources().getColor(R.color.ic_menu));
                    }
                }
            });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.menu_choose_types_done) {
            Intent x = new Intent();
            x.putExtra("Project", Project);
            x.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            setResult(RESULT_OK, x);
            finish();
            return super.onOptionsItemSelected(item);
        } else if (item.getItemId() == R.id.menu_choose_types_coeff) {
            if (userIsEditingNow) {
                linearLayout.removeView(seekBar);
                linearLayout.removeView(seekBartitle);
                item.setIcon(getResources().getDrawable(R.drawable.pencil));
            } else {
                linearLayout.addView(seekBar, 0);
                linearLayout.addView(seekBartitle, 0);
                seekBar.setProgress((int)Math.round(WorkSet.get(editingid).coeff * 10));
                item.setIcon(getResources().getDrawable(R.drawable.pencil_blue));
            }
            userIsEditingNow = !userIsEditingNow;
            adapt.notifyDataSetChanged();
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
        myDialogFragment.Message = getString(R.string.want_to_discard_changes);
        myDialogFragment.Title = getString(R.string.want_to_go_back);
        myDialogFragment.PositiveButtonTitle = getString(R.string.yes);
        myDialogFragment.NegativeButtonTitle = getString(R.string.no);
        myDialogFragment.PositiveClicked = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                Intent temp = new Intent();
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
        for (Map.Entry<WorkTypeClass, ArrayList<WorkClass>> x : Project.works.get(Project.place).second.entrySet()) {
            WorkTypeClass x1 = new WorkTypeClass(x.getKey());
            if (!all.contains(x1))
                deleting.add(x1);
            WorkSet.add(x1);
        }
        for (WorkTypeClass x : deleting)
                Project.works.get(Project.place).second.remove(x);
        //Arrays.sort(temp);
        for (DBObject x : temp)
            if (!Project.contains((WorkTypeClass)x))
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

            if (userIsEditingNow && position == editingid)
                item.setBackgroundColor(getResources().getColor(R.color.place_chosen));
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
        ArrayList<WorkClass> tmp = (ArrayList<WorkClass>)data.getSerializableExtra("WorkSet");
        Project.put(tmp2, tmp);
        adapt.notifyDataSetChanged();
        super.onActivityResult(requestCode, resultCode, data);
    }
}
