package gbarto.ru.smeta;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {
    private TextView mTextView;
    private ListView mListView;

    private ArrayList<DBObject> list;
    private Boolean check_list = new Boolean(false);
    private Boolean is_first = new Boolean(false);
    private Boolean first_in_list = new Boolean(false);
    private Integer ans = new Integer(-1);

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
        check_list = intent.getExtras().getBoolean("check_list");
        if (!check_list) is_first = intent.getExtras().getBoolean("first");

        used = new HashSet<Integer>();

        mListView = (ListView)findViewById(R.id.search_listView);
        setList();
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

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        if (check_list) {
            ArrayList<Integer> res_num = new ArrayList<Integer>(used);
            ArrayList<DBObject> res = new ArrayList<DBObject>();
            for (int i = 0; i < res_num.size(); i++){
                res.add(list.get(res_num.get(i)));
            }

            intent.putExtra("result", res);
            setResult(RESULT_OK, intent);
        } else {
            if (ans != -1L) {
                intent.putExtra("material_id", ans);
                setResult(RESULT_OK, intent);
            } else {
                setResult(RESULT_CANCELED, intent);
            }
        }

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

                setList();
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

    private void setList()
    {
        ArrayList<HashMap<String,Object>> mCatList = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> hm;

        first_in_list = false;
        num = new ArrayList<Integer>();
        for (Integer i = 0; i < list.size(); i++)
        {
            if (ok_string(list.get(i).name)){

                if (!check_list && is_first && i == 0)
                    first_in_list = true;

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
        mListView.setOnItemClickListener(mItemListener);

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

            final CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
            TextView text = (TextView) view.findViewById(R.id.text_name);

            if (!check_list){
                checkBox.setVisibility(View.INVISIBLE);
            } else {
                if (used.contains(j)) checkBox.setChecked(true);
                View.OnClickListener ocl = new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        if (used.contains(j)){
                            used.remove(j);
                            checkBox.setChecked(false);
                        } else {
                            used.add(j);
                            checkBox.setChecked(true);
                        }
                    }
                };

                checkBox.setOnClickListener(ocl);
                text.setOnClickListener(ocl);
                view.setOnClickListener(ocl);
            }

            text.setText(list.get(j).name);

            if (!check_list && is_first && first_in_list && position == 0)
                view.setBackgroundColor(getResources().getColor(R.color.search_choose));

            return view;
        }
    }

    AdapterView.OnItemClickListener mItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            ans = num.get(i);
            onBackPressed();
        }
    };

}
