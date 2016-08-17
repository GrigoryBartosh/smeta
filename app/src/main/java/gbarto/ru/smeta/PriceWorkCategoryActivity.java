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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class PriceWorkCategoryActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {
    private ArrayList<WorkTypeClass> type_work_list;

    DBAdapter dbAdapter = new DBAdapter(PriceWorkCategoryActivity.this);

    int type_work_line;

    private ListView mListView;
    private TextView mTextEmpty;

    private static final String TITLE = "title";
    private static final String ICON = "icon";
    static final private int ENTER_NAME = 0;
    static final private int PRICE_WORK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_work_category);

        dbAdapter.open();

        Toolbar toolbar = (Toolbar) findViewById(R.id.price_work_category_toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.price_work_category_fab);
        mListView = (ListView)findViewById(R.id.price_work_category_listView);
        mTextEmpty = (TextView) findViewById(R.id.price_work_category_textView_empty);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        fab.setOnClickListener(fab_ocl);

        type_work_list = getAllWorkType();

        mListView.setOnItemLongClickListener(PriceWorkCategoryActivity.this);
        setList();
    }

    @Override
    protected void onDestroy() {
        dbAdapter.close();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();

        WindowManager.LayoutParams wm = getWindow().getAttributes();
        wm.alpha = 1.0f;
        getWindow().setAttributes(wm);

        setList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_price_work_category, menu);
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
                myDialogFragment.setMessage(getString(R.string.price_work_category_about_text));
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
            default:
                res = super.onOptionsItemSelected(item);
                break;
        }

        return  res;
    }

    private View.OnClickListener fab_ocl = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            WindowManager.LayoutParams wm = getWindow().getAttributes();
            wm.alpha = 0.2f;
            getWindow().setAttributes(wm);

            ArrayList<String> used_name = new ArrayList<String>();
            for (int j = 0; j < type_work_list.size(); j++)
                used_name.add(type_work_list.get(j).name);

            Intent intent = new Intent(PriceWorkCategoryActivity.this, PopUpNameCategory.class);
            intent.putExtra("used_name", used_name);
            startActivityForResult(intent, ENTER_NAME);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ENTER_NAME)
        {
            if (resultCode == RESULT_OK)
            {
                String name = data.getExtras().getString("name");

                WorkTypeClass t = new WorkTypeClass(name);
                t.rowID = dbAdapter.add(t);
                type_work_list.add(t);

                setList();
            }
        }
        if (requestCode == PRICE_WORK)
        {
            if (resultCode == RESULT_OK)
            {
                String name = data.getExtras().getString("new_name");
                type_work_list.get(type_work_line).name = name;

                setList();
            }
        }
    }

    private void setList()
    {
        ArrayList<HashMap<String,Object>> mCatList = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> hm;

        for (int i = 0; i < type_work_list.size(); i++){
            hm = new HashMap<>();
            hm.put(TITLE, type_work_list.get(i).name);
            hm.put(ICON, R.mipmap.ic_launcher);
            mCatList.add(hm);
        }

        SimpleAdapter adapter = new SimpleAdapter(  PriceWorkCategoryActivity.this,
                                                    mCatList, R.layout.list_item_price_work_category,
                                                    new String[]{TITLE, ICON},
                                                    new int[]{R.id.text, R.id.img});
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(mItemListener);

        if (adapter.getCount() == 0) mTextEmpty.setText(getString(R.string.price_work_category_empty_list));
        else                         mTextEmpty.setText("");
    }

    AdapterView.OnItemClickListener mItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            type_work_line = i;

            ArrayList<String> used_name = new ArrayList<String>();
            for (int j = 0; j < type_work_list.size(); j++)
                if (j != i)
                    used_name.add(type_work_list.get(j).name);

            Intent intent = new Intent(PriceWorkCategoryActivity.this, PriceWorkActivity.class);
            intent.putExtra("work_type", type_work_list.get(i));
            intent.putExtra("used_name", used_name);
            startActivityForResult(intent, PRICE_WORK);
        }
    };

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        FragmentManager manager = getSupportFragmentManager();
        MyDialogFragment myDialogFragment = new MyDialogFragment();
        myDialogFragment.setTitle(getString(R.string.price_work_category_alert_delete_title));
        myDialogFragment.setMessage(getString(R.string.price_work_category_alert_delete_summary));
        myDialogFragment.setPositiveButtonTitle(getString(R.string.yes));
        myDialogFragment.setNegativeButtonTitle(getString(R.string.no));
        myDialogFragment.setPositiveClicked(new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                dbAdapter.deleteWorkType(type_work_list.get(position).rowID);

                Toast.makeText(getApplicationContext(), type_work_list.get(position).name + " - " + getString(R.string.removed), Toast.LENGTH_SHORT).show();

                type_work_list.remove(position);

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

    private ArrayList<WorkTypeClass> getAllWorkType()
    {
        DBObject[] arr = dbAdapter.getAllRows(DBAdapter.TYPES_TABLE);
        ArrayList<WorkTypeClass> res = new ArrayList<WorkTypeClass>();
        for (int i = 0; i < arr.length; i++){
            res.add((WorkTypeClass) arr[i]);
        }
        return res;
    }
}
