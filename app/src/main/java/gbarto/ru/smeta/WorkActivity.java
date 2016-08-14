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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class WorkActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {
    private Boolean new_work;
    private String name;

    private EditText mEditName;
    private EditText mEditSum;
    private Button mButtonNew;

    private ListView mListView;
    private ArrayList<HashMap<String,Object>> mCatList;
    private static final String TITLE = "title";
    private static final String MEASUREMENT = "measurement";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);

        mEditName = (EditText) findViewById(R.id.work_editText_name);
        mEditSum = (EditText) findViewById(R.id.work_editText_sum);
        mButtonNew = (Button) findViewById(R.id.work_button_new);
        mButtonNew.setOnClickListener(btn_ocl);

        Intent intent = getIntent();
        new_work = intent.getExtras().getBoolean("new_work");
        if (new_work) {
            name = getString(R.string.work_new_work);
        } else {
            name = intent.getExtras().getString("name");
            mEditName.setText(name);
        }
        setTitle(name);

        Toolbar toolbar = (Toolbar) findViewById(R.id.work_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mListView = (ListView)findViewById(R.id.work_listView);
        mListView.setOnItemLongClickListener(WorkActivity.this);
        getList();
    }

    @Override
    public void onBackPressed() {
        name = mEditName.getText().toString();
        if (new_work) {
            if (name.length() != 0) save();
            super.onBackPressed();
        } else {
            if (name.length() == 0) Toast.makeText(getApplicationContext(), getString(R.string.work_toast_enter_name), Toast.LENGTH_SHORT).show();
            else {
                save();
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getList();
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
                name = mEditName.getText().toString();
                if (name.length() == 0) Toast.makeText(getApplicationContext(), getString(R.string.work_toast_enter_name), Toast.LENGTH_SHORT).show();
                else {
                    save();
                    finish();
                }
                res = true;
                break;
            case R.id.menu_work_help:

                FragmentManager manager = getSupportFragmentManager();
                MyDialogFragment myDialogFragment = new MyDialogFragment();
                myDialogFragment.setTitle(getString(R.string.help));
                myDialogFragment.setMessage(getString(R.string.work_about_text));
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

    View.OnClickListener btn_ocl = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            //----------------------------------------------------------------------------------------------------------------

            Intent intent = new Intent(WorkActivity.this, WorkNewMaterialActivity.class);
            startActivity(intent);
        }
    };

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String,Object> itemHashMap = (HashMap<String,Object>)parent.getItemAtPosition(position);
        String titleItem = itemHashMap.get(TITLE).toString();

        //----------------------------------------------------------------------------------------------------------------
        //удалить работу из бд

        Toast.makeText(getApplicationContext(), titleItem + " - " + getString(R.string.removed), Toast.LENGTH_SHORT).show();
        getList();

        return true;
    }

    private void getList()
    {
        mCatList = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> hm;

        //----------------------------------------------------------------------------------------------------------------
        //считать из БД типы работ

        for (int i = 0; i < 20; i++){
            hm = new HashMap<>();
            hm.put(TITLE, "Барсик");
            hm.put(MEASUREMENT, "кг");
            mCatList.add(hm);
        }
        hm = new HashMap<>();
        hm.put(TITLE, "Хуярсик");
        hm.put(MEASUREMENT, "кг");
        mCatList.add(hm);


        SimpleAdapter adapter = new SimpleAdapter(  WorkActivity.this,
                mCatList, R.layout.list_item_work,
                new String[]{TITLE, MEASUREMENT},
                new int[]{R.id.text_name, R.id.text_measurement});
        mListView.setAdapter(adapter);
    }

    private void save()
    {
        //----------------------------------------------------------------------------------------------------------------
        //сохранить работу в бд
    }
}
