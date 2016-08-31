package gbarto.ru.smeta;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.ss.formula.functions.T;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity   extends AppCompatActivity
                            implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemLongClickListener {
    private FloatingActionButton fab;
    private ListView mListView;
    private TextView mTextEmpty;

    private FileManager fileManager;
    private ArrayList<String> list_name;
    private ArrayList<ProjectClass> list_project;

    private String[] list_alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        fab = (FloatingActionButton) findViewById(R.id.main_fab);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_drawer);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mListView = (ListView) findViewById(R.id.main_listView);
        mTextEmpty = (TextView) findViewById(R.id.main_textView_empty);

        list_alert = getResources().getStringArray(R.array.main_list);

        fileManager = new FileManager(MainActivity.this);


        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        fab.setOnClickListener(fab_ocl);
        mListView.setOnItemLongClickListener(MainActivity.this);

        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (mListView.getCount() == 0) return false;

                View c = mListView.getChildAt(0);
                float scrolly = -c.getTop() + mListView.getFirstVisiblePosition() * (c.getHeight());
                fab.setTranslationY(scrolly * 2);
                return false;
            }
        });

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
    protected void onResume() {
        getList();
        setList();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_drawer);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
                About();
                res = true;
                break;
            default:
                res = super.onOptionsItemSelected(item);
                break;
        }

        return  res;
    }

    View.OnClickListener fab_ocl = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            NewProject();
        }
    };

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_projects:
                break;

            case R.id.nav_price_work:
                PriceWork();
                break;

            case R.id.nav_price_material:
                Material();
                break;

            case R.id.nav_menu_settings:
                Settings();
                break;

            case R.id.nav_menu_contacts:
                Contacts();
                break;

            case R.id.nav_menu_archive:
                Archive();
                break;

            case R.id.nav_menu_abut:
                AboutProgram();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_drawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getList() {
        list_name = fileManager.Load();

        list_project = new ArrayList<ProjectClass>();
        for (int i = 0; i < list_name.size(); i++) {
            ProjectClass p = fileManager.LoadFromFile(list_name.get(i));
            if (p != null)
                list_project.add(p);
        }
    }

    void setList() {
        ArrayList<HashMap<String,Object>> mCatList = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> hm;

        for (int i = 0; i < list_project.size(); i++){
            hm = new HashMap<>();
            mCatList.add(hm);
        }

        Adapter adapter = new Adapter(  MainActivity.this,
                mCatList, R.layout.list_item_main,
                new String[]{},
                new int[]{});
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(mItemListener);

        if (adapter.getCount() == 0) mTextEmpty.setText(getString(R.string.main_empty_list));
        else                         mTextEmpty.setText("");
    }

    AdapterView.OnItemClickListener mItemListener = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            ProjectEdit(i);
        }
    };

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        ProjectDelete(position);
        return true;
    }

    private class Adapter extends SimpleAdapter{
        public Adapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = null;
            if (view == null) view = getLayoutInflater().inflate(R.layout.list_item_main, parent, false);

            TextView mTextName = (TextView) view.findViewById(R.id.text);
            TextView mTextPrice = (TextView) view.findViewById(R.id.price);

            mTextName.setText(list_project.get(position).name);
            mTextPrice.setText(String.format("%.2f", fileManager.getPrice(list_project.get(position))));

            return view;
        }

        @Override
        public boolean isEnabled(int position) {
            return true;
        }
    }

    private void NewProject(){
        Intent intent = new Intent(MainActivity.this, EditNameActivity.class);
        startActivity(intent);
    }

    private void About(){
        FragmentManager manager = getSupportFragmentManager();
        MyDialogFragment myDialogFragment = new MyDialogFragment();
        myDialogFragment.setTitle(getString(R.string.help));
        myDialogFragment.setMessage(getString(R.string.main_about_text));
        myDialogFragment.setPositiveButtonTitle(getString(R.string.ok));
        myDialogFragment.setPositiveClicked(new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        myDialogFragment.setUseNegativeButton(false);
        myDialogFragment.show(manager, "dialog");
    }

    private void PriceWork(){
        Intent intent = new Intent(MainActivity.this, PriceWorkCategoryActivity.class);
        startActivity(intent);
        finish();
    }

    private void Material(){
        //finish();
    }

    private void Settings(){
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }

    private void Contacts(){
        //finish();
    }

    private void Archive(){
        //finish();
    }

    private void AboutProgram(){
        Intent intent = new Intent(MainActivity.this, AboutActivity.class);
        startActivity(intent);
        finish();
    }

    private void ProjectEdit(final int position){
        Intent intent = new Intent(MainActivity.this, EditNameActivity.class);
        intent.putExtra("Project", list_project.get(position));
        startActivity(intent);
    }

    private void ProjectDelete(final int position){
        FragmentManager manager = getSupportFragmentManager();
        MyDialogFragment myDialogFragment = new MyDialogFragment();
        myDialogFragment.setTitle(getString(R.string.main_alert_delete_title));
        myDialogFragment.setMessage(getString(R.string.main_alert_delete_summary));
        myDialogFragment.setPositiveButtonTitle(getString(R.string.yes));
        myDialogFragment.setNegativeButtonTitle(getString(R.string.no));
        myDialogFragment.setPositiveClicked(new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                fileManager.Delete(list_name.get(position));

                Toast.makeText(getApplicationContext(), list_project.get(position).name + " - " + getString(R.string.removed), Toast.LENGTH_SHORT).show();

                getList();
                setList();
                dialog.cancel();
            }
        });
        myDialogFragment.setNegativeClicked(new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        myDialogFragment.show(manager, "dialog");
    }
}
