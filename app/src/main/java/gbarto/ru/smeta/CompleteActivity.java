package gbarto.ru.smeta;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

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

    @Override
    public void onBackPressed() {
        for (int i = 0; i < number; i++) {
            project.put(list_work_type.get(i), list_work.get(i));
        }

        Intent intent = new Intent();
        intent.putExtra("Project", project);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
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

        if (adapter.getCount() == 0) mTextEmpty.setText(getString(R.string.complete_empty_list));
        else                         mTextEmpty.setText("");
    }

    private class AdapterCategory extends SimpleAdapter {
        public AdapterCategory(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = null;
            if (view == null) view = getLayoutInflater().inflate(R.layout.list_item_complete_category, parent, false);

            RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.complete_category_relativeLayout);
            TextView mTextName = (TextView) view.findViewById(R.id.complete_category_name);
            final ImageView mImageView = (ImageView) view.findViewById(R.id.complete_category_imageView);
            final LinearLayout mLinearLayout = (LinearLayout) view.findViewById(R.id.complete_category_linerLayout);

            WorkTypeClass work_type = list_work_type.get(position);
            final ArrayList<WorkClass> works = list_work.get(position);
            final Boolean is_open = open.get(position);

            mTextName.setText(work_type.name);

            if (open.get(position)) {
                setListWork(mLinearLayout, works);
                mImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_up));
            } else {
                mImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
            }

            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (open.get(position))
                        mLinearLayout.removeAllViews();
                    else setListWork(mLinearLayout, works);

                    open.set(position, !open.get(position));
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!open.get(position)) {
                        mLinearLayout.removeAllViews();
                        open.set(position, !open.get(position));
                    }
                }
            });

            return view;
        }
    }

    void setListWork(LinearLayout mLinearLayout, ArrayList<WorkClass> list_work) {
        for (int i = 0; i < list_work.size(); i++) {
            final WorkClass work = list_work.get(i);

            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            View view = inflater.inflate(R.layout.list_item_complete_work, null);

            TextView mTextName = (TextView) view.findViewById(R.id.complete_work_name);
            final CheckBox mCheckBox = (CheckBox) view.findViewById(R.id.complete_work_checkBox);
            View mViewLine = view.findViewById(R.id.complete_work_line);

            mTextName.setText(work.name);
            mCheckBox.setChecked(work.state);
            if (i == list_work.size()-1) {
                mViewLine.setBackgroundColor(getResources().getColor(R.color.white));
            }

            View.OnClickListener ocl = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    work.state = !work.state;
                    mCheckBox.setChecked(work.state);
                }
            };

            mTextName.setOnClickListener(ocl);
            mCheckBox.setOnClickListener(ocl);
            view.setOnClickListener(ocl);

            mLinearLayout.addView(view);
        }
    }
}
