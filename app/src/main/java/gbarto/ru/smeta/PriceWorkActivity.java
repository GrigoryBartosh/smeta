package gbarto.ru.smeta;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class PriceWorkActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {
    WorkTypeClass work_type;
    ArrayList<WorkClass> work_list;
    ArrayList<String> used_name;

    private ListView mListView;
    private static final String TITLE = "title";

    static final private int UPDATE_WORK = 0;
    static final private int CREATE_WORK = 1;
    static final private int ENTER_NAME = 2;

    DBAdapter dbAdapter = new DBAdapter(this);
    int work_line;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_work);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.price_work_fab);
        mListView = (ListView)findViewById(R.id.price_work_listView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.price_work_toolbar);

        dbAdapter.open();

        fab.setOnClickListener(fab_ocl);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        work_type = (WorkTypeClass) intent.getSerializableExtra("work_type");
        used_name = intent.getExtras().getStringArrayList("used_name");
        setTitle(work_type.name);
        work_list = getAllWork();

        mListView.setOnItemLongClickListener(PriceWorkActivity.this);
        setList();
    }

    @Override
    protected void onDestroy() {
        dbAdapter.close();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        WindowManager.LayoutParams wm = getWindow().getAttributes();
        wm.alpha = 1.0f;
        getWindow().setAttributes(wm);

        super.onResume();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("new_name", work_type.name);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    private View.OnClickListener fab_ocl = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            ArrayList<String> used_name = new ArrayList<String>();
            for (int j = 0; j < work_list.size(); j++)
                used_name.add(work_list.get(j).name);

            Intent intent = new Intent(PriceWorkActivity.this, WorkActivity.class);
            intent.putExtra("work_type", 0);
            WorkClass new_work = new WorkClass();
            new_work.workType = work_type.rowID;
            intent.putExtra("work", new_work);
            intent.putExtra("used_name", used_name);
            startActivityForResult(intent, CREATE_WORK);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_price_work, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        boolean res;

        switch (id)
        {
            case R.id.menu_work_help:
                FragmentManager manager = getSupportFragmentManager();
                MyDialogFragment myDialogFragment = new MyDialogFragment();
                myDialogFragment.setTitle(getString(R.string.help));
                myDialogFragment.setMessage(getString(R.string.price_work_about_text));
                myDialogFragment.setPositiveButtonTitle(getString(R.string.ok));
                myDialogFragment.setPositiveClicked(new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                myDialogFragment.setUseNegativeButton(false);
                myDialogFragment.show(manager, "dialog");

                res = true;
                break;
            case R.id.menu_work_edit:
                WindowManager.LayoutParams wm = getWindow().getAttributes();
                wm.alpha = 0.2f;
                getWindow().setAttributes(wm);

                Intent intent = new Intent(PriceWorkActivity.this, PopUpNameCategory.class);
                intent.putExtra("name", work_type.name);
                intent.putExtra("used_name", used_name);
                startActivityForResult(intent, ENTER_NAME);

                res = true;
                break;
            default:
                res = super.onOptionsItemSelected(item);
                break;
        }

        return  res;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        FragmentManager manager = getSupportFragmentManager();
        MyDialogFragment myDialogFragment = new MyDialogFragment();
        myDialogFragment.setTitle(getString(R.string.price_work_alert_delete_title));
        myDialogFragment.setMessage(getString(R.string.price_work_alert_delete_summary));
        myDialogFragment.setPositiveButtonTitle(getString(R.string.yes));
        myDialogFragment.setNegativeButtonTitle(getString(R.string.no));
        myDialogFragment.setPositiveClicked(new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                dbAdapter.deleteRow(DBAdapter.WORKS_TABLE, work_list.get(position).rowID);

                Toast.makeText(getApplicationContext(), work_list.get(position).name + " - " + getString(R.string.removed), Toast.LENGTH_SHORT).show();

                work_list.remove(position);

                setList();
                dialog.cancel();
            }
        });
        myDialogFragment.setNegativeClicked(new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        myDialogFragment.show(manager, "dialog");

        return true;
    }

    private void setList()
    {
        ArrayList<HashMap<String,Object>> mCatList = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> hm;

        for (int i = 0; i < work_list.size(); i++)
        {
            hm = new HashMap<>();
            hm.put(TITLE, work_list.get(i).name);
            mCatList.add(hm);
        }

        SimpleAdapter adapter = new SimpleAdapter(  PriceWorkActivity.this,
                mCatList, R.layout.list_item_price_work,
                new String[]{TITLE},
                new int[]{R.id.text});
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(mItemListener);
    }

    AdapterView.OnItemClickListener mItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            work_line = i;

            ArrayList<String> used_name = new ArrayList<String>();
            for (int j = 0; j < work_list.size(); j++)
                if (i != j)
                    used_name.add(work_list.get(j).name);

            Intent intent = new Intent(PriceWorkActivity.this, WorkActivity.class);
            intent.putExtra("work_type", 1);
            intent.putExtra("work", work_list.get(i));
            intent.putExtra("used_name", used_name);
            startActivityForResult(intent, UPDATE_WORK);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UPDATE_WORK)
        {
            if (resultCode == RESULT_OK)
            {
                WorkClass work = (WorkClass)data.getSerializableExtra("work");
                work_list.set(work_line, work);
                dbAdapter.update(work);
                setList();
            }
        }
        if (requestCode == CREATE_WORK)
        {
            if (resultCode == RESULT_OK)
            {
                WorkClass new_work = (WorkClass)data.getSerializableExtra("work");
                new_work.rowID = dbAdapter.add(new_work);
                work_list.add(new_work);
                setList();
            }
        }
        if (requestCode == ENTER_NAME)
        {
            if (resultCode == RESULT_OK)
            {
                work_type.name = data.getExtras().getString("name");
                setTitle(work_type.name);
            }
        }
    }

    private ArrayList<WorkClass> getAllWork(){
        DBObject[] arr = dbAdapter.getWorksByType(work_type.rowID);
        ArrayList<WorkClass> res = new ArrayList<WorkClass>();
        for (int i = 0; i < arr.length; i++){
            res.add((WorkClass) arr[i]);
        }
        return res;
    }
}
