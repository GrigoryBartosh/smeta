package gbarto.ru.smeta;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.TreeMap;

public class EditNameActivity extends AppCompatActivity {

    ArrayList <String> RoomList = new ArrayList<>();
    ArrayAdapter<String> adapt;
    ArrayList <Integer> Before = new ArrayList<>();
    FloatingActionButton fab;
    boolean [] lastinflation;
    FileManager fileManager;
    String lastProjectname = "";
    EditText mText;
    boolean editing = false;
    int countreturned = 0;
    TextView Summary;

    private TextView ChooseRoomText;
    private int RoomResult = 1337;
    private int TypesResult = 1488;
    private int CompletedResult = 239;
    String projectname = "";
    boolean gaveproject = false;
    DBAdapter adapter;
    ProjectClass Project;
    String keeper = "";
    TreeMap <String, Integer> countrooms;

    void Recalc()
    {
        countrooms = new TreeMap<>();
        Before.clear();
        for (String x : RoomList)
        {
            if (countrooms.containsKey(x))
                countrooms.put(x, countrooms.get(x) + 1);
            else
                countrooms.put(x, 1);
            Before.add(countrooms.get(x));
        }
        lastinflation = new boolean[RoomList.size()];
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name);
        Toolbar toolbar = (Toolbar) findViewById(R.id.edit_name_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fileManager = new FileManager(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mText = (EditText)findViewById(R.id.Project_name_field);
        if (getIntent().getSerializableExtra("Project") != null) {
            gaveproject = true;
            Project = (ProjectClass) getIntent().getSerializableExtra("Project");
            projectname = Project.name;
            lastProjectname = projectname;
            mText.setText(Project.name);
            if (keeper.isEmpty())
                setTitle(getString(R.string.title_activity_edit_name));
            else
                setTitle(keeper);
        }
        else
        {
            if (Project == null) {
                Project = new ProjectClass();
            }
        }


        fab = (FloatingActionButton) findViewById(R.id.main_fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent x = new Intent(EditNameActivity.this, EditRoomActivity.class);
                startActivityForResult(x, RoomResult);
            }
        });

        adapter = new DBAdapter(this);
        adapter.open();
        default_values();
        lastinflation = new boolean[RoomList.size()];
        AddAdapter();


        /*LinearLayout View_Button = (LinearLayout)findViewById(R.id.button_view);
        View_Button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String checker = mText.getText().toString();
                final String message = check(checker);
                if (message.equals("OK")) {
                    Project.name = checker;
                    ProjectView( );
                }
                else
                {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
            }
        });*/
        Summary = (TextView)findViewById(R.id.project_summary);
        mText.clearFocus();
        Summary.requestFocus();
        Refresh();
        keeper = mText.getText().toString();

        /*Button_Choose_Works = (LinearLayout)findViewById(R.id.button_new_works);
        Button_Choose_Works.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent x = new Intent(EditNameActivity.this, ChooseTypeActivity.class);
                if (Project == null)
                    Project = new ProjectClass();
                x.putExtra("Project", Project);
                startActivityForResult(x, TypesResult);
            }
        });*/

        mText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable)
            {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                if (charSequence.length() < keeper.length())
                    keeper = charSequence.toString();
                else if (charSequence.length() > i && charSequence.charAt(i) == '\n') {
                    mText.setText(keeper);
                    mText.setSelection(keeper.length());
                }
                else if (charSequence.length() > 100) {
                    mText.setText(keeper);
                    mText.setSelection(keeper.length());
                }
                else
                    keeper = charSequence.toString();
            }});

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
        d = getResources().getDrawable(android.R.drawable.ic_menu_view);
        d.setColorFilter(color, mMode);
        d.setAlpha(255);
        d = getResources().getDrawable(android.R.drawable.ic_menu_share);
        d.setColorFilter(color, mMode);
        d.setAlpha(255);
    }

    private String check(String s)
    {
        if (s == null)
            return "INTERNAL ERROR";
        if (s.length() < 1)
            return getString(R.string.price_project_name_popup_need_name);
        String[] bad = getResources().getStringArray(R.array.bad_strings);
        String have = "";
        for (String t: bad)
            if (s.contains(t)) {
                if (have.length() != 0)
                    have += ", ";
                have += t;
            }
        FileManager fileManager = new FileManager(EditNameActivity.this);
        ArrayList<String> temp = fileManager.Load();
        s = s.trim();
        s = s.replaceAll("[\\s]{2,}", "\\s");
        s = s.replaceAll("[\n, \r]", " ");
        for (String x : temp)
                if (x.equals(s) && !x.equals(projectname))
                    return getString(R.string.such_project_already_exists);
        projectname = s;
        if (have.equals(""))
            return "OK";
        return getString(R.string.popup_name_category_toast_bad_symbol) + " " + have;
    }

    public void ProjectShare(){
        if (Save()) {
            FragmentManager manager = getSupportFragmentManager();
            MyDialogFragment myDialogFragment = new MyDialogFragment();
            myDialogFragment.setTitle(getString(R.string.main_share_alert_choose));
            myDialogFragment.setUseMessage(false);
            myDialogFragment.setUsePositiveButton(false);
            myDialogFragment.setUseNegativeButton(false);
            myDialogFragment.setUseList(true);
            myDialogFragment.setList(getResources().getStringArray(R.array.main_list));

            myDialogFragment.setListClicked(new DialogInterface.OnClickListener()
            {
                File file;
                Intent intent = new Intent();

                public void onClick(DialogInterface dialog, int id)
                {
                    switch (id) {
                        case 0:
                            file = fileManager.createPDF(Project);
                            if (file == null) break;
                            intent.setAction(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                            intent.setType("application/pdf");
                            startActivity(intent);
                            break;
                        case 1:
                            file = fileManager.createXLS(Project);
                            if (file == null) break;
                            intent.setAction(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                            intent.setType("application/vnd.ms-excel");
                            startActivity(intent);
                            break;
                    }
                    dialog.cancel();
                }
            });
            myDialogFragment.show(manager, "dialog");
        }
    }

    boolean Save()
    {
        String checker = mText.getText().toString();
        final String message = check(checker);
        if (message.equals("OK")) {
            fileManager.Delete(lastProjectname);
            Project.name = checker;
            lastProjectname = checker;
            FileManager fileManager = new FileManager(EditNameActivity.this);
            fileManager.Save(Project);
            countreturned = 0;
            return true;
        }
        else
        {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.menu_edit_name_view )
            ProjectView();
        else if (item.getItemId() == R.id.menu_edit_name_share)
            ProjectShare();
        else if (item.getItemId() == R.id.menu_edit_name_edit)
        {
            editing = !editing;
            adapt.notifyDataSetChanged();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void Refresh()
    {
        if (Project != null)
        {
            String temp = getString(R.string.total) + " " + String.format("%.2f", new FileManager(this).getPrice(Project));
            Summary.setText(temp);
        }
        else
            Summary.setVisibility(View.INVISIBLE);
        super.onResume();
    }

    private void ProjectView(){
        if (Save()) {
            FragmentManager manager = getSupportFragmentManager();
            MyDialogFragment myDialogFragment = new MyDialogFragment();
            myDialogFragment.setTitle(getString(R.string.main_view_alert_choose));
            myDialogFragment.setUseMessage(false);
            myDialogFragment.setUsePositiveButton(false);
            myDialogFragment.setUseNegativeButton(false);
            myDialogFragment.setUseList(true);
            myDialogFragment.setList(getResources().getStringArray(R.array.main_list));
            myDialogFragment.setListClicked(new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {
                    switch (id) {
                        case 0:
                            fileManager.openPDF(Project);
                            break;
                        case 1:
                            fileManager.openXLS(Project);
                            break;
                    }
                    dialog.cancel();
                }
            });
            myDialogFragment.show(manager, "dialog");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_name_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed()
    {
        if (!gaveproject && Project.works.isEmpty())
        {
            Intent temp = new Intent();
            setResult(RESULT_CANCELED, temp);
            finish();
        }
        if (Save()) {
            if (countreturned > 0) {
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
            } else {
                Intent temp = new Intent();
                setResult(RESULT_CANCELED, temp);
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (keeper.isEmpty())
            setTitle(getString(R.string.title_activity_edit_name));
        else
            setTitle(keeper);
        if (requestCode == RoomResult) {
            if (resultCode == RESULT_OK) {
                ++countreturned;
                Project.works.add(new Pair(new RoomClass(data.getStringExtra("new_room")), new TreeMap<>()));
                RoomList.add(data.getStringExtra("new_room"));
                Recalc();
                adapt.notifyDataSetChanged();
            }
        } else if (requestCode == TypesResult) {
            if (resultCode == RESULT_OK)
            {
                ++countreturned;
                Project.works.get(Project.place).second = ((ProjectClass) data.getSerializableExtra("Project")).works.get(Project.place).second;
                Refresh();
                Recalc();
                adapt.notifyDataSetChanged();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy()
    {
        adapter.close();
        super.onDestroy();
    }

    private void default_values()
    {
        for (Pair <RoomClass, TreeMap<WorkTypeClass, ArrayList<WorkClass> > > x : Project.works)
            RoomList.add(x.first.visible_name);
        Recalc();
    }

    private void AddAdapter()
    {
        adapt = new MyListAdapter();
        ListView l = (ListView)findViewById(R.id.edit_name_roomlist);
        l.setOnItemClickListener(mItemListener);
        l.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                FragmentManager manager = getSupportFragmentManager();
                MyDialogFragment myDialogFragment = new MyDialogFragment();
                myDialogFragment.Message = getString(R.string.edit_name_delete_room_text);
                myDialogFragment.Title = getString(R.string.edit_name_delete_room_title);
                myDialogFragment.PositiveButtonTitle = getString(R.string.yes);
                myDialogFragment.NegativeButtonTitle = getString(R.string.no);
                final int position = i;
                myDialogFragment.PositiveClicked = new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        RoomList.remove(position);
                        Project.works.remove(position);
                        Recalc();
                        adapt.notifyDataSetChanged();
                    }
                };
                myDialogFragment.show(manager, "dialog");
                return true;
            }
        });
        l.setAdapter(adapt);
    }

    AdapterView.OnItemClickListener mItemListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
        {
            Intent x = new Intent(EditNameActivity.this, ChooseTypeActivity.class);
            Project.place = i;
            x.putExtra("Project", Project);
            startActivityForResult(x, TypesResult);
        }
    };

    private class MyListAdapter extends ArrayAdapter
    {
        public MyListAdapter() {
            super(EditNameActivity.this, R.layout.listlayout, RoomList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout item = (LinearLayout) convertView;
            if (item == null)
                item = (LinearLayout)getLayoutInflater().inflate(editing ? R.layout.list_item_main_editing : R.layout.list_item_main, parent, false);
            TextView mTextPrice = (TextView) item.findViewById(R.id.price);
            mTextPrice.setText(String.format("%.2f", fileManager.getPrice(Project, position)));
            if (!editing) {
                TextView mTextName = (TextView) item.findViewById(R.id.text);
                String name = RoomList.get(position);
                if (Before.get(position) > 1 || countrooms.get(name) > 1)
                    name += " " + Before.get(position);
                Project.works.get(position).first.setName(name);
                mTextName.setText(name);
            } else {
                EditText mEditText = (EditText) item.findViewById(R.id.text);
                mEditText.setText(RoomList.get(position));
            }
            return item;
        }
    }
}
