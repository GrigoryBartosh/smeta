package gbarto.ru.smeta;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class WorkActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {
    private ListView mListView;
    private ArrayList<HashMap<String,Object>> mCatList;
    private static final String TITLE = "title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);

        Toolbar toolbar = (Toolbar) findViewById(R.id.work_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mListView = (ListView)findViewById(R.id.work_listView);
        mListView.setOnItemLongClickListener(WorkActivity.this);
        getList();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "1", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_work, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        boolean res;

        switch (id)
        {
            case R.id.menu_work_done:
                res = true;
                break;
            case R.id.menu_work_help:
                res = true;
                break;
            default:
                res = super.onOptionsItemSelected(item);
                break;
        }

        return  res;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getList();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String,Object> itemHashMap = (HashMap<String,Object>)parent.getItemAtPosition(position);
        String titleItem = itemHashMap.get(TITLE).toString();

        //удалить работу из бд

        Toast.makeText(getApplicationContext(), titleItem + " - " + getString(R.string.removed), Toast.LENGTH_SHORT).show();
        getList();

        return true;
    }

    private void getList()
    {
        mCatList = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> hm;

        //считать из БД типы работ

        for (int i = 0; i < 20; i++){
            hm = new HashMap<>();
            hm.put(TITLE, "Барсик");
            mCatList.add(hm);
        }

        SimpleAdapter adapter = new SimpleAdapter(  WorkActivity.this,
                mCatList, R.layout.list_item_work,
                new String[]{TITLE},
                new int[]{R.id.text});
        mListView.setAdapter(adapter);
    }
}
