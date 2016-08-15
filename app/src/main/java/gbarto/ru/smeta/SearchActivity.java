package gbarto.ru.smeta;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {
    private TextView mTextView;

    private ListView mListView;

    private ArrayList<DBObject> list;
    ArrayList<Integer> num = new ArrayList<Integer>();
    private HashSet<Integer> used;
    private String[] search_words;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mTextView = (TextView)findViewById(R.id.search_textView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        list = (ArrayList<DBObject>)intent.getSerializableExtra("list");
        used = new HashSet<Integer>();

        mListView = (ListView)findViewById(R.id.search_listView);
        getList();
    }

    @Override
    public void onBackPressed() {
        ArrayList<Integer> res_num = new ArrayList<Integer>(used);
        ArrayList<DBObject> res = new ArrayList<DBObject>();
        for (int i = 0; i < res_num.size(); i++){
            res.add(list.get(res_num.get(i)));
        }

        Intent intent = new Intent();
        intent.putExtra("result", res);
        setResult(RESULT_OK, intent);

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search_action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextChange(String newText){
                search_words = newText.split(" ");
                if (search_words != null)
                    for (int i = 0; i < search_words.length; i++)
                        search_words[i] = search_words[i].toLowerCase();

                getList();
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query){
                return true;
            }
        });
        searchView.setIconified(false);
        searchView.clearFocus();

        return super.onCreateOptionsMenu(menu);
    }

    private void getList()
    {
        ArrayList<HashMap<String,Object>> mCatList = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> hm;

        num = new ArrayList<Integer>();
        for (Integer i = 0; i < list.size(); i++)
        {
            if (ok_string(list.get(i).name)){
                hm = new HashMap<>();
                mCatList.add(hm);
                num.add(i);
            }
        }

        Adapter adapter = new Adapter(  SearchActivity.this,
                mCatList, R.layout.list_item_search,
                new String[]{},
                new int[]{});
        mListView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        if (adapter.getCount() == 0) mTextView.setText(getString(R.string.search_empty_list));
        else                         mTextView.setText("");
    }

    private boolean ok_string(String s){
        if (search_words == null) return true;

        String t = s.toLowerCase();
        for (int i = 0; i < search_words.length; i++)
        {
            if (t.indexOf(search_words[i]) == -1)
                return false;
        }
        return true;
    }

    private class Adapter extends SimpleAdapter{
        public Adapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Integer j = num.get(position);
            View view = convertView;
            if (view == null) view = getLayoutInflater().inflate(R.layout.list_item_search, parent, false);

            CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
            if (used.contains(j)) checkBox.setChecked(true);
            checkBox.setOnClickListener(new CheckBox.OnClickListener(){
                @Override
                public void onClick(View view) {
                    if (used.contains(j)){
                        used.remove(j);
                    } else {
                        used.add(j);
                    }
                }
            });

            TextView text = (TextView) view.findViewById(R.id.text_name);
            text.setText(list.get(j).name);

            return view;
        }
    }
}
