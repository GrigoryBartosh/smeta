package gbarto.ru.smeta;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class WorkActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {
    private Integer work_type;
    private WorkClass work;

    private EditText mEditName;
    private EditText mEditSum;
    private Spinner mSpinner;
    private TextView mTextSize;
    private EditText mEditSize;

    private LinearLayout mLinearLayout;
    private TextView mTextMaterial;
    private TextView mTextInstruments;
    private View mViewUnderlineMaterial;
    private View mViewUnderlineInstruments;
    private ImageView mImageNew;

    private ViewFlipper mViewFlipper;
    private ListView mListViewMaterial;
    private ListView mListViewInstrument;

    private TextView mTextListMaterialEmpty;
    private TextView mTextListInstrumentEmpty;

    private ArrayAdapter<CharSequence> spinner_adapter;
    private Boolean selected_first_window =  true;

    private ArrayList<MaterialTypeClass> all_material_types;
    private ArrayList<InstrumentClass> all_instrument;
    private HashMap<Long, MaterialTypeClass > material_types_info_from_id = new HashMap<Long, MaterialTypeClass >();
    private HashMap<Long, InstrumentClass > instrument_info_from_id = new HashMap<Long, InstrumentClass >();

    private String[] measurements_work;
    private String[] measurements_work_word;
    private String[] measurements_material;
    private String[] measurements_instrument;

    private ArrayList<MaterialClass> new_material;

    private ArrayList<String> used_name;
    private String[] bad_strings;
    private String bad_strings_to_toast = "";

    private DBAdapter dbAdapter = new DBAdapter(this);

    static final private int CHOOSE_MATERIAL_TYPE = 0;
    static final private int CHOOSE_INSSTRUMENT = 1;
    static final private int CHOOSE_MATERIAL = 2;
    private int material_line;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);

        mEditName = (EditText) findViewById(R.id.work_editText_name);
        mEditSum = (EditText) findViewById(R.id.work_editText_sum);
        mSpinner = (Spinner) findViewById(R.id.work_spinner);
        mTextSize = (TextView) findViewById(R.id.work_text_size);
        mEditSize = (EditText) findViewById(R.id.work_editText_size);
        mLinearLayout = (LinearLayout) findViewById(R.id.work_linerLayout);
        mTextMaterial = (TextView) findViewById(R.id.work_list_material);
        mTextInstruments = (TextView) findViewById(R.id.work_list_instruments);
        mViewUnderlineMaterial = findViewById(R.id.work_list_underline_material);
        mViewUnderlineInstruments = findViewById(R.id.work_list_underline_instruments);
        mImageNew = (ImageView) findViewById(R.id.work_imageView_new);
        Toolbar toolbar = (Toolbar) findViewById(R.id.work_toolbar);

        mViewFlipper = (ViewFlipper) findViewById(R.id.work_viewFlipper);
        LayoutInflater inflater = (LayoutInflater) getSystemService(getApplicationContext().LAYOUT_INFLATER_SERVICE);
        mViewFlipper.addView(inflater.inflate(R.layout.flipper_work_material, null));
        mViewFlipper.addView(inflater.inflate(R.layout.flipper_work_instrument, null));

        mListViewMaterial = (ListView) mViewFlipper.getChildAt(0).findViewById(R.id.work_listView_material);
        mListViewInstrument = (ListView) mViewFlipper.getChildAt(1).findViewById(R.id.work_listView_instrument);
        mListViewMaterial.setOnItemLongClickListener(WorkActivity.this);
        mListViewInstrument.setOnItemLongClickListener(WorkActivity.this);

        mTextListMaterialEmpty = (TextView) mViewFlipper.getChildAt(0).findViewById(R.id.work_text_list_material_empty);
        mTextListInstrumentEmpty = (TextView) mViewFlipper.getChildAt(1).findViewById(R.id.work_text_list_instrument_empty);

        dbAdapter.open();

        spinner_adapter = ArrayAdapter.createFromResource(this, R.array.measurements_work_short, android.R.layout.simple_spinner_item);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(spinner_adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mTextSize.setText(measurements_work_word[i] + ":");
                setList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mImageNew.setOnClickListener(btn_ocl);
        mTextMaterial.setOnClickListener(list_ocl);
        mTextInstruments.setOnClickListener(list_ocl);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        measurements_work = getResources().getStringArray(R.array.measurements_work_short);
        measurements_work_word = getResources().getStringArray(R.array.measurements_work_word);
        measurements_material = getResources().getStringArray(R.array.measurements_material_short);
        measurements_instrument = getResources().getStringArray(R.array.measurements_instrument_short);
        bad_strings = getResources().getStringArray(R.array.bad_strings);
        for (int i = 0; i < bad_strings.length; i++)
            bad_strings_to_toast += " " + bad_strings[i];

        Intent intent = getIntent();
        work_type = intent.getExtras().getInt("work_type");
        work = (WorkClass) intent.getExtras().getSerializable("work");
        if (work_type != 2) used_name = intent.getExtras().getStringArrayList("used_name");

        mTextSize.setText(measurements_work_word[work.measuring] + ":");

        all_material_types = getAllMaterialTypes();
        for (int i = 0; i < all_material_types.size(); i++)
            material_types_info_from_id.put(all_material_types.get(i).rowID,
                    all_material_types.get(i));
        all_instrument = getAllInstrument();
        for (int i = 0; i < all_instrument.size(); i++)
            instrument_info_from_id.put(all_instrument.get(i).rowID,
                    all_instrument.get(i));

        if (work_type != 0) {
            setTitle(work.name);
            setFromWork();
        } else {
            mTextListMaterialEmpty.setText(getString(R.string.work_empty_list_material));
            mTextListInstrumentEmpty.setText(getString(R.string.work_empty_list_instrument));
        }

        if (work_type == 2) {
            mEditName.setEnabled(false);
            mSpinner.setEnabled(false);
        } else {
            mTextSize.setVisibility(View.INVISIBLE);
            mEditSize.setVisibility(View.INVISIBLE);
            RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
            relativeParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 1);
            mLinearLayout.setLayoutParams(relativeParams);
        }

        int color = getResources().getColor(R.color.ic_menu);
        PorterDuff.Mode mMode = PorterDuff.Mode.SRC_ATOP;
        Drawable d;
        d = getResources().getDrawable(android.R.drawable.ic_menu_help);
        d.setColorFilter(color, mMode);
        d.setAlpha(255);
    }

    @Override
    protected void onDestroy() {
        dbAdapter.close();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        String name = mEditName.getText().toString();
        if (work_type == 0) {
            if (name.length() == 0) {
                save(false);
                super.onBackPressed();
            } else {
                if (ok_name()) {
                    save(true);
                    super.onBackPressed();
                }
            }
        } else {
            if (ok_name()) {
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

    View.OnClickListener list_ocl = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            setToWork();

            int id = view.getId();
            switch (id) {
                case R.id.work_list_material:
                    if (selected_first_window) return;
                    mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.go_prev_in));
                    mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.go_prev_out));
                    mViewFlipper.showPrevious();
                    selected_first_window = true;
                    break;
                case R.id.work_list_instruments:
                    if (!selected_first_window) return;
                    mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.go_next_in));
                    mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.go_next_out));
                    mViewFlipper.showNext();
                    selected_first_window = false;
                    break;
            }

            setList();
        }
    };

    View.OnClickListener btn_ocl = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            setToWork();

            if (selected_first_window) {
                HashSet<Long> used_material_types = new HashSet<Long>();
                if (work.Materials != null)
                    for (int i = 0; i < work.Materials.size(); i++)
                        used_material_types.add(work.Materials.get(i).first);

                ArrayList<MaterialTypeClass> new_material_types = new ArrayList<MaterialTypeClass>();

                for (int i = 0; i < all_material_types.size(); i++)
                {
                    final Long num = all_material_types.get(i).rowID;
                    if (!used_material_types.contains(num))
                        new_material_types.add(all_material_types.get(i));
                }

                Intent intent = new Intent(WorkActivity.this, SearchActivity.class);
                intent.putExtra("list", new_material_types);
                intent.putExtra("check_list", true);
                startActivityForResult(intent, CHOOSE_MATERIAL_TYPE);
            } else {
                HashSet<Long> used_instrument = new HashSet<Long>();
                if (work.Instruments != null)
                    for (int i = 0; i < work.Instruments.size(); i++)
                        used_instrument.add(work.Instruments.get(i).first);

                ArrayList<InstrumentClass> new_instrument = new ArrayList<InstrumentClass>();

                for (int i = 0; i < all_instrument.size(); i++)
                {
                    final Long num = all_instrument.get(i).rowID;
                    if (!used_instrument.contains(num))
                        new_instrument.add(all_instrument.get(i));
                }

                Intent intent = new Intent(WorkActivity.this, SearchActivity.class);
                intent.putExtra("list", new_instrument);
                intent.putExtra("check_list", true);
                startActivityForResult(intent, CHOOSE_INSSTRUMENT);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_MATERIAL_TYPE)
        {
            if (resultCode == RESULT_OK)
            {
                ArrayList<MaterialTypeClass> new_list = (ArrayList<MaterialTypeClass>)data.getSerializableExtra("result");
                if (new_list == null) return;

                for (int i = 0; i < new_list.size(); i++) {
                    work.addMaterial((int)new_list.get(i).rowID);
                }

                setFromWork();
            }
        }
        if (requestCode == CHOOSE_INSSTRUMENT)
        {
            if (resultCode == RESULT_OK)
            {
                ArrayList<InstrumentClass> new_list = (ArrayList<InstrumentClass>)data.getSerializableExtra("result");
                if (new_list == null) return;

                for (int i = 0; i < new_list.size(); i++) {
                    work.addInstrument((int)new_list.get(i).rowID);
                }

                setFromWork();
            }
        }
        if (requestCode == CHOOSE_MATERIAL)
        {
            if (resultCode == RESULT_OK)
            {
                Integer pos = data.getExtras().getInt("material_id");
                Long rowID = new_material.get(pos).rowID;
                work.RealMaterials.set(material_line, rowID);

                setFromWork();
            }
        }
    }

    Boolean ok_name(){
        if (work_type == 2) return true;

        String name = mEditName.getText().toString().replaceAll("[\\s]{2,}", " ");
        name = name.trim();
        String lname = name.toLowerCase();

        if (lname.length() == 0){
            Toast.makeText( getApplicationContext(),
                    getString(R.string.work_toast_need_name),
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        Boolean flag = true;
        for (int j = 0; j < bad_strings.length; j++)
            flag = flag & (lname.indexOf(bad_strings[j]) == -1);
        if (!flag) {
            Toast.makeText( getApplicationContext(),
                    getString(R.string.work_toast_bad_symbol) + bad_strings_to_toast,
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        flag = true;
        for (int i = 0; i < used_name.size(); i++) {
            flag = flag & !used_name.get(i).equals(lname);
        }
        if (!flag) {
            Toast.makeText( getApplicationContext(),
                    getString(R.string.work_toast_equal_name),
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
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
        work.name = mEditName.getText().toString().replaceAll("[\\s]{2,}", " ").trim();
        work.measuring = spinner_adapter.getPosition(mSpinner.getSelectedItem().toString());
        String price = mEditSum.getText().toString();
        if (price.equals("") || price.equals("-") || price.equals("-.")) {
            work.price = 0;
        } else {
            work.price = Float.valueOf(price);
            if (work.price < 0.0005) work.price = 0;
        }

        if (work_type == 2) {
            String size = mEditSize.getText().toString();
            if (size.equals("") || size.equals("-") || size.equals("-.") || size.equals(".")) {
                work.size = 0;
            } else {
                work.size = Float.valueOf(size);
                if (work.size < 0.0005) work.size = 0;
            }
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

        if (work_type == 2){
            if (Math.abs(work.size) < 1e-8)
                mEditSize.setText("");
            else
                mEditSize.setText(Float.toString(work.size));
        }
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
        AdapterMaterial adapter_material = new AdapterMaterial(  WorkActivity.this,
                mCatList, R.layout.list_item_work_material,
                new String[]{},
                new int[]{});
        mListViewMaterial.setAdapter(adapter_material);
        adapter_material.notifyDataSetChanged();
        if (adapter_material.getCount() == 0)   mTextListMaterialEmpty.setText(getString(R.string.work_empty_list_material));
        else                                    mTextListMaterialEmpty.setText("");

        mCatList = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < work.Instruments.size(); i++){
            hm = new HashMap<>();
            mCatList.add(hm);
        }
        AdapterInstrument adapter_instrument = new AdapterInstrument(  WorkActivity.this,
                mCatList, R.layout.list_item_work_instrument,
                new String[]{},
                new int[]{});
        mListViewInstrument.setAdapter(adapter_instrument);
        adapter_instrument.notifyDataSetChanged();
        /*if (adapter_instrument.getCount() == 0) */mTextListInstrumentEmpty.setText(getString(R.string.work_empty_list_instrument));
        //else                                    mTextListInstrumentEmpty.setText("");

        if (selected_first_window) {
            mTextMaterial.setTextColor(getResources().getColor(R.color.black));
            mTextInstruments.setTextColor(getResources().getColor(R.color.dark_gray));
            mViewUnderlineMaterial.setBackgroundColor(getResources().getColor(R.color.blue));
            mViewUnderlineInstruments.setBackgroundColor(getResources().getColor(R.color.empty));
        } else {
            mTextMaterial.setTextColor(getResources().getColor(R.color.dark_gray));
            mTextInstruments.setTextColor(getResources().getColor(R.color.black));
            mViewUnderlineMaterial.setBackgroundColor(getResources().getColor(R.color.empty));
            mViewUnderlineInstruments.setBackgroundColor(getResources().getColor(R.color.blue));
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        setToWork();

        FragmentManager manager = getSupportFragmentManager();
        MyDialogFragment myDialogFragment = new MyDialogFragment();
        myDialogFragment.setTitle(getString(R.string.work_alert_delete_title));
        myDialogFragment.setPositiveButtonTitle(getString(R.string.yes));
        myDialogFragment.setNegativeButtonTitle(getString(R.string.no));
        final int tp = position;

        if (selected_first_window) {
            myDialogFragment.setMessage(getString(R.string.work_alert_delete_summary_material));
            myDialogFragment.setPositiveClicked(new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Long rowID = work.Materials.get(tp).first;
                    MaterialTypeClass p = material_types_info_from_id.get(rowID);
                    Toast.makeText(getApplicationContext(), p.name + " - " + getString(R.string.removed), Toast.LENGTH_SHORT).show();

                    work.removeMaterial(tp);

                    setFromWork();
                    dialog.cancel();
                }
            });
        } else {
            myDialogFragment.setMessage(getString(R.string.work_alert_delete_summary_instrument));
            myDialogFragment.setPositiveClicked(new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Long rowID = work.Instruments.get(tp).first;
                    InstrumentClass p = instrument_info_from_id.get(rowID);
                    Toast.makeText(getApplicationContext(), p.name + " - " + getString(R.string.removed), Toast.LENGTH_SHORT).show();

                    work.removeInstrument(tp);

                    setFromWork();
                    dialog.cancel();
                }
            });
        }
        myDialogFragment.setNegativeClicked(new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        myDialogFragment.show(manager, "dialog");

        return true;
    }

    private class AdapterMaterial extends SimpleAdapter{
        public AdapterMaterial(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = null;
            if (view == null) view = getLayoutInflater().inflate(R.layout.list_item_work_material, parent, false);

            TextView mTextName = (TextView) view.findViewById(R.id.text_name);
            EditText mEditSum = (EditText) view.findViewById(R.id.editText_sum);
            TextView mTextMeasurement = (TextView) view.findViewById(R.id.text_measurement);

            Long rowID = work.Materials.get(position).first;
            MaterialTypeClass p = material_types_info_from_id.get(rowID);
            mTextName.setText(p.name);
            mTextMeasurement.setText(measurements_material[p.measurement] + "/" + measurements_work[spinner_adapter.getPosition(mSpinner.getSelectedItem().toString())]);
            if (Math.abs(work.Materials.get(position).second) < 1e-8)
                mEditSum.setText("");
            else
                mEditSum.setText(Float.toString(work.Materials.get(position).second));

            final View v = view;
            mEditSum.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    String cnt = charSequence.toString();
                    if (cnt.equals("") || cnt.equals("-") || cnt.equals("-.") || cnt.equals(".")) {
                        work.Materials.get(position).second = 0f;
                    } else {
                        work.Materials.get(position).second = Float.valueOf(cnt);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            mTextName.setLongClickable(true);
            mTextMeasurement.setLongClickable(true);
            mTextName.setClickable(true);
            mTextMeasurement.setClickable(true);

            if (work_type == 2){
                if (work.RealMaterials.get(position) == -1L)
                    view.setBackgroundColor(getResources().getColor(R.color.work_material_not_choose));
                else
                    view.setBackgroundColor(getResources().getColor(R.color.work_material_choose));
            }

            if (work_type == 2) {
                View.OnClickListener ocl = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setToWork();
                        material_line = position;

                        Intent intent = new Intent(WorkActivity.this, SearchActivity.class);

                        new_material = getAllMaterial(material_types_info_from_id.get(work.Materials.get(position).first));

                        Boolean flag = false;
                        for (int i = 0; i < new_material.size(); i++)
                            if (work.RealMaterials.get(position) == new_material.get(i).rowID) {
                                MaterialClass t = new_material.get(0);
                                new_material.set(0, new_material.get(i));
                                new_material.set(i, t);
                                flag = true;
                                break;
                            }

                        intent.putExtra("list", new_material);
                        intent.putExtra("check_list", false);
                        intent.putExtra("first", flag);
                        startActivityForResult(intent, CHOOSE_MATERIAL);
                    }
                };

                view.setOnClickListener(ocl);
                mTextName.setOnClickListener(ocl);
                mTextMeasurement.setOnClickListener(ocl);
            }

            return view;
        }

        @Override
        public boolean isEnabled(int position) {
            return true;
        }
    }

    private class AdapterInstrument extends SimpleAdapter{
        public AdapterInstrument(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = null;
            if (view == null) view = getLayoutInflater().inflate(R.layout.list_item_work_material, parent, false);

            TextView mTextName = (TextView) view.findViewById(R.id.text_name);
            EditText mEditSum = (EditText) view.findViewById(R.id.editText_sum);
            TextView mTextMeasurement = (TextView) view.findViewById(R.id.text_measurement);

            Long rowID = work.Instruments.get(position).first;
            InstrumentClass p = instrument_info_from_id.get(rowID);
            mTextName.setText(p.name);
            mTextMeasurement.setText(measurements_instrument[p.measuring]);
            if (Math.abs(work.Materials.get(position).second) < 1e-8)
                mEditSum.setText("");
            else
                mEditSum.setText(Float.toString(work.Materials.get(position).second));

            final View v = view;
            mEditSum.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    String cnt = charSequence.toString();
                    if (cnt.equals("") || cnt.equals("-") || cnt.equals("-.") || cnt.equals(".")) {
                        work.Instruments.get(position).second = 0f;
                    } else {
                        work.Instruments.get(position).second = Float.valueOf(cnt);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            mTextName.setLongClickable(true);
            mTextMeasurement.setLongClickable(true);
            mTextName.setClickable(true);
            mTextMeasurement.setClickable(true);

            return view;
        }

        @Override
        public boolean isEnabled(int position) {
            return true;
        }
    }

    private ArrayList<MaterialTypeClass> getAllMaterialTypes() {
        DBObject[] arr = dbAdapter.getAllRows(DBAdapter.MATERIAL_TYPES_TABLE);
        ArrayList<MaterialTypeClass> res = new ArrayList<MaterialTypeClass>();
        for (int i = 0; i < arr.length; i++){
            res.add((MaterialTypeClass) arr[i]);
        }
        return res;
    }

    private ArrayList<MaterialClass> getAllMaterial(MaterialTypeClass material_type) {
        DBObject[] arr = dbAdapter.getAllMaterials(material_type);
        ArrayList<MaterialClass> res = new ArrayList<MaterialClass>();
        for (int i = 0; i < arr.length; i++){
            res.add((MaterialClass) arr[i]);
        }
        return res;
    }

    private ArrayList<InstrumentClass> getAllInstrument() {
        DBObject[] arr = dbAdapter.getAllRows(DBAdapter.INSTRUMENT_TABLE);
        ArrayList<InstrumentClass> res = new ArrayList<InstrumentClass>();
        for (int i = 0; i < arr.length; i++){
            res.add((InstrumentClass) arr[i]);
        }
        return res;
    }
}
