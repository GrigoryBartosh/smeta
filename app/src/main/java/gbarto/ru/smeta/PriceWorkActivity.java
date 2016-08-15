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
    private ListView mListView;
    private ArrayList<HashMap<String,Object>> mCatList;
    private static final String TITLE = "title";
    private String work_category_name;

    static final private int UPDATE_WORK = 1;
    static final private int CREATE_WORK = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_work);

        work_category_name = getIntent().getExtras().getString("work_category_name");
        setTitle(work_category_name);

        Toolbar toolbar = (Toolbar) findViewById(R.id.price_work_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.price_work_fab);
        fab.setOnClickListener(fab_ocl);

        mListView = (ListView)findViewById(R.id.price_work_listView);
        getList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getList();
    }

    private View.OnClickListener fab_ocl = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(PriceWorkActivity.this, WorkActivity.class);
            intent.putExtra("new_work", true);
            intent.putExtra("work", new WorkClass());
            startActivityForResult(intent, CREATE_WORK);
        }
    };

    private void getList()
    {
        mCatList = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> hm;

        //считать из БД

        hm = new HashMap<>();
        hm.put(TITLE, "Барсик");
        mCatList.add(hm);

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
            HashMap<String,Object> itemHashMap = (HashMap<String,Object>)adapterView.getItemAtPosition(i);
            String titleItem = itemHashMap.get(TITLE).toString();

            /*Intent intent = new Intent(PriceWorkActivity.this, WorkActivity.class);
            intent.putExtra("new_work", false);
            intent.putExtra("name", titleItem);
            startActivity(intent);*/
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

            }
        }
        if (requestCode == CREATE_WORK)
        {
            if (resultCode == RESULT_OK)
            {

            }
        }
    }
}
