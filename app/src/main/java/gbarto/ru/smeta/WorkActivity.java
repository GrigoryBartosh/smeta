package gbarto.ru.smeta;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class WorkActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {
    private Boolean new_work;
    private WorkClass work;

    private EditText mEditName;
    private Spinner mSpinner;
    private EditText mEditSum;
    private Button mButtonNew;
    private TextView mTextListEmpty;
    ArrayAdapter<CharSequence> spinner_adapter;

    private ListView mListView;
    static final private int CHOOSE_MATERIAL = 0;

    private ArrayList<MaterialClass> all_material;
    private HashMap<Long, Pair<String, Integer> > material_info_from_id = new HashMap<Long, Pair<String, Integer> >();
    String[] measurements_material;

    DBAdapter dbAdapter = new DBAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);

        mEditName = (EditText) findViewById(R.id.work_editText_name);
        mSpinner = (Spinner) findViewById(R.id.work_spinner);
        mEditSum = (EditText) findViewById(R.id.work_editText_sum);
        mButtonNew = (Button) findViewById(R.id.work_button_new);
        mTextListEmpty = (TextView) findViewById(R.id.work_text_list_empty);
        Toolbar toolbar = (Toolbar) findViewById(R.id.work_toolbar);

        dbAdapter.open();

        spinner_adapter = ArrayAdapter.createFromResource(this, R.array.measurements_work, android.R.layout.simple_spinner_item);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(spinner_adapter);
        mButtonNew.setOnClickListener(btn_ocl);
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

        all_material = getAllMaterial();
        for (int i = 0; i < all_material.size(); i++)
            material_info_from_id.put(  all_material.get(i).rowID,
                                        new Pair(all_material.get(i).name, all_material.get(i).measuring));

        measurements_material = getResources().getStringArray(R.array.measurements_material_short);

        Intent intent = getIntent();
        new_work = intent.getExtras().getBoolean("new_work");
        work = (WorkClass) intent.getExtras().getSerializable("work");
        if (!new_work) {
            setFromWork();
        } else {
            mTextListEmpty.setText(getString(R.string.work_empty_list));
        }
    }

    @Override
    protected void onDestroy() {
        dbAdapter.close();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        String name = mEditName.getText().toString();
        if (new_work) {
            save(name.length() != 0);
            super.onBackPressed();
        } else {
            if (name.length() == 0) Toast.makeText(getApplicationContext(), getString(R.string.work_toast_enter_name), Toast.LENGTH_SHORT).show();
            else {
                save(true);
                super.onBackPressed();
            }
        }
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
                String name = mEditName.getText().toString();
                if (name.length() == 0) Toast.makeText(getApplicationContext(), getString(R.string.work_toast_enter_name), Toast.LENGTH_SHORT).show();
                else {
                    save(true);
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
            setToWork();

            HashSet<Long> used_material = new HashSet<Long>();
            if (work.Materials != null)
                for (int i = 0; i < work.Materials.size(); i++)
                    used_material.add(work.Materials.get(i).first);

            ArrayList<MaterialClass> new_material = new ArrayList<MaterialClass>();

            for (int i = 0; i < all_material.size(); i++)
            {
                final Long num = all_material.get(i).rowID;
                if (!used_material.contains(num))
                    new_material.add(all_material.get(i));
            }

            Intent intent = new Intent(WorkActivity.this, SearchActivity.class);
            intent.putExtra("list", new_material);
            startActivityForResult(intent, CHOOSE_MATERIAL);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_MATERIAL)
        {
            if (resultCode == RESULT_OK)
            {
                ArrayList<MaterialClass> new_list = (ArrayList<MaterialClass>)data.getSerializableExtra("result");
                if (new_list == null) return;

                for (int i = 0; i < new_list.size(); i++) {
                    work.addMaterial((int)new_list.get(i).rowID);
                }

                setFromWork();
            }
        }
    }

    private void save(Boolean complete)
    {
        Intent intent = new Intent();
        if (complete) {
            setToWork();
            intent.putExtra("work", work);
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED, intent);
        }
    }

    private void setToWork()
    {
        work.name = mEditName.getText().toString();
        work.measuring = spinner_adapter.getPosition(mSpinner.getSelectedItem().toString());
        String price = mEditSum.getText().toString();
        if (price.equals("") || price.equals("-") || price.equals("-."))
        {
            work.price = 0;
        } else {
            work.price = Float.valueOf(price);
            if (work.price < 0.5) work.price = 0;
        }
    }

    private void setFromWork()
    {
        mEditName.setText(work.name);

        mSpinner.setSelection(work.measuring);

        if (Math.abs(work.price) < 1e-8)
            mEditSum.setText("");
        else
            mEditSum.setText(Float.toString(work.price));
        setList();
    }

    private void setList()
    {
        ArrayList<HashMap<String,Object>> mCatList = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> hm;

        for (int i = 0; i < work.Materials.size(); i++){
            hm = new HashMap<>();
            mCatList.add(hm);
        }

        Adapter adapter = new Adapter(  WorkActivity.this,
                mCatList, R.layout.list_item_work,
                new String[]{},
                new int[]{});
        mListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if (adapter.getCount() == 0)
        {
            mTextListEmpty.setText(getString(R.string.work_empty_list));
        } else {
            mTextListEmpty.setText("");
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        FragmentManager manager = getSupportFragmentManager();
        MyDialogFragment myDialogFragment = new MyDialogFragment();
        myDialogFragment.setTitle(getString(R.string.work_alert_delete_title));
        myDialogFragment.setMessage(getString(R.string.work_alert_delete_summary));
        myDialogFragment.setPositiveButtonTitle(getString(R.string.yes));
        myDialogFragment.setNegativeButtonTitle(getString(R.string.no));
        final int tp = position;
        myDialogFragment.setPositiveClicked(new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Long rowID = work.Materials.get(tp).first;
                Pair<String, Integer> p = material_info_from_id.get(rowID);
                Toast.makeText(getApplicationContext(), p.first + " - " + getString(R.string.removed), Toast.LENGTH_SHORT).show();

                work.Materials.remove(tp);

                setFromWork();
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

    private class Adapter extends SimpleAdapter{
        public Adapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) view = getLayoutInflater().inflate(R.layout.list_item_work, parent, false);

            TextView mTextName = (TextView) view.findViewById(R.id.text_name);
            EditText mEditSum = (EditText) view.findViewById(R.id.editText_sum);
            TextView mTextMeasurement = (TextView) view.findViewById(R.id.text_measurement);

            Long rowID = work.Materials.get(position).first;
            Pair<String, Integer> p = material_info_from_id.get(rowID);
            mTextName.setText(p.first);
            mTextMeasurement.setText(measurements_material[p.second]);
            mEditSum.setText(Float.toString(work.Materials.get(position).second));

            final View v = view;
            mEditSum.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view_edit, int i, KeyEvent keyEvent) {
                    final int tp = position;
                    System.out.println("----------------");

                    EditText mEditSum = (EditText) v.findViewById(R.id.editText_sum);

                    String cnt = mEditSum.getText().toString();
                    if (cnt.equals("") || cnt.equals("-") || cnt.equals("-.")) {
                        work.Materials.get(tp).second = 0f;
                    } else {
                        work.Materials.get(tp).second = Float.valueOf(cnt);
                    }

                    return true;
                }
            });

            mTextName.setLongClickable(true);
            mTextMeasurement.setLongClickable(true);

            return view;
        }

        @Override
        public boolean isEnabled(int position) {
            return true;
        }
    }

    private ArrayList<MaterialClass> getAllMaterial() {
        DBObject[] arr = dbAdapter.getAllRows(DBAdapter.MATERIAL_TABLE);
        ArrayList<MaterialClass> res = new ArrayList<MaterialClass>();
        for (int i = 0; i < arr.length; i++){
            res.add((MaterialClass) arr[i]);
        }
        return res;
    }

}
