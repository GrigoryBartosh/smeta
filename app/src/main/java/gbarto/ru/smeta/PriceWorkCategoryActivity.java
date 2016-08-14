package gbarto.ru.smeta;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class PriceWorkCategoryActivity extends AppCompatActivity {
    private ListView mListView;
    private ArrayList<HashMap<String,Object>> mCatList;
    private static final String TITLE = "title";
    private static final String ICON = "icon";
    static final private int ENTER_NAME = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_work_category);

        Toolbar toolbar = (Toolbar) findViewById(R.id.price_work_category_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.price_work_category_fab);
        fab.setOnClickListener(fab_ocl);

        mListView = (ListView)findViewById(R.id.price_work_category_listView);
        getList();
    }

    @Override
    protected void onResume() {
        super.onResume();

        WindowManager.LayoutParams wm = getWindow().getAttributes();
        wm.alpha = 1.0f;
        getWindow().setAttributes(wm);

        getList();
    }

    private View.OnClickListener fab_ocl = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            WindowManager.LayoutParams wm = getWindow().getAttributes();
            wm.alpha = 0.2f;
            getWindow().setAttributes(wm);

            Intent intent = new Intent(PriceWorkCategoryActivity.this, PriceWorkCategoryPopUp.class);
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
                String name = data.getExtras().getString(PriceWorkCategoryPopUp.NAME);
                //добавить в БД
                Toast.makeText(getApplicationContext(), name, Toast.LENGTH_SHORT).show();
                getList();
            }
        }
    }

    private void getList()
    {
        mCatList = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> hm;

        //считать из БД

        hm = new HashMap<>();
        hm.put(TITLE, "Барсик");
        hm.put(ICON, R.mipmap.ic_launcher);
        mCatList.add(hm);

        SimpleAdapter adapter = new SimpleAdapter(  PriceWorkCategoryActivity.this,
                                                    mCatList, R.layout.list_item_price_work_category,
                                                    new String[]{TITLE, ICON},
                                                    new int[]{R.id.text, R.id.img});
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(mItemListener);
    }

    AdapterView.OnItemClickListener mItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            HashMap<String,Object> itemHashMap = (HashMap<String,Object>)adapterView.getItemAtPosition(i);
            String titleItem = itemHashMap.get(TITLE).toString();
            //int imageItem = (int)itemHashMap.get(ICON);

            Intent intent = new Intent(PriceWorkCategoryActivity.this, PriceWorkActivity.class);
            intent.putExtra("work_category_name", titleItem);
            startActivity(intent);
        }
    };
}
