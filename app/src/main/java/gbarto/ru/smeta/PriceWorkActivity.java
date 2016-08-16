package gbarto.ru.smeta;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class PriceWorkActivity extends AppCompatActivity {
    WorkTypeClass work_type;
    ArrayList<WorkClass> work_list;

    private ListView mListView;
    private static final String TITLE = "title";

    static final private int UPDATE_WORK = 0;
    static final private int CREATE_WORK = 1;

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

        work_type = (WorkTypeClass) getIntent().getSerializableExtra("work_type");
        setTitle(work_type.name);
        work_list = getAllWork();
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

    private View.OnClickListener fab_ocl = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(PriceWorkActivity.this, WorkActivity.class);
            intent.putExtra("work_type", 0);
            WorkClass new_work = new WorkClass();
            new_work.workType = work_type.rowID;
            intent.putExtra("work", new_work);
            startActivityForResult(intent, CREATE_WORK);
        }
    };

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
            Intent intent = new Intent(PriceWorkActivity.this, WorkActivity.class);
            intent.putExtra("work_type", 1);
            intent.putExtra("work", work_list.get(i));
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
