package gbarto.ru.smeta;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompleteActivity extends AppCompatActivity {
    private ProjectClass project;
    private ArrayList<WorkTypeClass> list_work_type;
    private ArrayList<ArrayList <WorkClass>> list_work;
    private ArrayList<Boolean> open;
    private Integer number = 0;

    private ListView mListView;
    private TextView mTextEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete);

        Toolbar toolbar = (Toolbar) findViewById(R.id.complete_toolbar);
        mListView = (ListView) findViewById(R.id.complete_listView);
        mTextEmpty = (TextView) findViewById(R.id.complete_text_empty);

        list_work_type = new ArrayList<WorkTypeClass>();
        list_work = new ArrayList<ArrayList <WorkClass>>();
        open = new ArrayList<Boolean>();

        Intent intent = getIntent();
        project = (ProjectClass) intent.getSerializableExtra("project");
        for (Map.Entry<WorkTypeClass, ArrayList<WorkClass>> x : project.works.entrySet()) {
            WorkTypeClass wt = new WorkTypeClass(x.getKey());
            ArrayList <WorkClass> arr_w = new ArrayList<>();
            for (WorkClass y : x.getValue())
                arr_w.add(new WorkClass(y));
            if (arr_w.size() > 0) {
                list_work_type.add(wt);
                list_work.add(arr_w);
                open.add(false);
                number++;
            }
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        setListCategory();
    }

    void setListCategory() {
        ArrayList<HashMap<String,Object>> mCatList = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> hm;

        for (int i = 0; i < number; i++){
            hm = new HashMap<>();
            mCatList.add(hm);
        }

        AdapterCategory adapter = new AdapterCategory(  CompleteActivity.this,
                mCatList, R.layout.list_item_complete_category,
                new String[]{},
                new int[]{});
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(mItemListenerCategory);

        if (adapter.getCount() == 0) mTextEmpty.setText(getString(R.string.complete_empty_list));
        else                         mTextEmpty.setText("");
    }

    AdapterView.OnItemClickListener mItemListenerCategory = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        }
    };

    private class AdapterCategory extends SimpleAdapter {
        public AdapterCategory(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = null;
            if (view == null) view = getLayoutInflater().inflate(R.layout.list_item_complete_category, parent, false);

            TextView mTextName = (TextView) view.findViewById(R.id.complete_category_name);
            ListView mListView = (ListView) view.findViewById(R.id.complete_category_listView);

            WorkTypeClass work_type = list_work_type.get(position);
            ArrayList<WorkClass> works = list_work.get(position);

            mTextName.setText(work_type.name);
            setListWork(mListView, works, position);

            return view;
        }
    }

    void setListWork(ListView mListView, ArrayList<WorkClass> list_work, Integer n) {
        ArrayList<HashMap<String,Object>> mCatList = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> hm;

        for (int i = 0; i < list_work.size(); i++){
            hm = new HashMap<>();
            mCatList.add(hm);
        }

        AdapterWork adapter = new AdapterWork(  CompleteActivity.this,
                mCatList, R.layout.list_item_complete_category,
                new String[]{},
                new int[]{});
        adapter.setN(n);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(mItemListenerWork);
        //View c = mListView.getChildAt(0);
        //mListView.setDividerHeight(c.getHeight() * list_work.size());
        mListView.setDividerHeight(1000);
    }

    AdapterView.OnItemClickListener mItemListenerWork = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        }
    };

    private class AdapterWork extends SimpleAdapter {
        private Integer n;

        public AdapterWork(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        public void setN(Integer n){
            this.n = n;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = null;
            if (view == null) view = getLayoutInflater().inflate(R.layout.list_item_complete_work, parent, false);

            TextView mTextName = (TextView) view.findViewById(R.id.complete_work_name);
            CheckBox mCheckBox = (CheckBox) view.findViewById(R.id.complete_work_checkBox);

            WorkClass work = list_work.get(n).get(position);

            mTextName.setText(work.name);

            return view;
        }
    }
}
