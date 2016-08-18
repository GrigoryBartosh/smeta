package gbarto.ru.smeta;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity   extends AppCompatActivity
                            implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemLongClickListener {
    private ListView mListView;
    private TextView mTextEmpty;

    private FileManager fileManager;
    private ArrayList<String> list_name;
    private ArrayList<ProjectClass> list_project;

    private static final String TITLE = "title";
    private static final String SUMMARY= "summary";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.main_fab);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view); // чтобы реагировало на нажатия
        mListView = (ListView) findViewById(R.id.main_listView);
        mTextEmpty = (TextView) findViewById(R.id.main_textView_empty);

        fileManager = new FileManager(MainActivity.this);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        fab.setOnClickListener(fab_ocl);
        mListView.setOnItemLongClickListener(MainActivity.this);
    }

    @Override
    protected void onResume() {
        getList();
        setList();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

        Intent intent;
        switch (id) {
            case R.id.nav_new_project:
                NewProject();
                break;

            case R.id.nav_price_work:
                intent = new Intent(MainActivity.this, PriceWorkCategoryActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_price_material:
                break;

            case R.id.nav_menu_settings:
                intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_menu_abut:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getList() {
        ArrayList<String> list_name = fileManager.Load();

        list_project = new ArrayList<ProjectClass>();
        for (int i = 0; i < list_name.size(); i++) {
            list_project.add(fileManager.LoadFromFile(list_name.get(i)));
        }
    }

    void setList() {
        ArrayList<HashMap<String,Object>> mCatList = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> hm;

        for (int i = 0; i < list_project.size(); i++){
            hm = new HashMap<>();
            hm.put(TITLE, list_project.get(i).name);
            hm.put(SUMMARY, list_project.get(i).place);
            mCatList.add(hm);
        }

        SimpleAdapter adapter = new SimpleAdapter(  MainActivity.this,
                mCatList, R.layout.list_item_main,
                new String[]{TITLE, SUMMARY},
                new int[]{R.id.text, R.id.summary});
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(mItemListener);

        if (adapter.getCount() == 0) mTextEmpty.setText(getString(R.string.main_empty_list));
        else                         mTextEmpty.setText("");
    }

    AdapterView.OnItemClickListener mItemListener = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//            Intent intent = new Intent(MainActivity.this, EditNameActivity.class);
//            intent.putExtra("Project", list_project.get(i));
//            startActivity(intent);
            fileManager.openPDF(list_project.get(i));
        }
    };

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        FragmentManager manager = getSupportFragmentManager();
        MyDialogFragment myDialogFragment = new MyDialogFragment();
        myDialogFragment.setTitle(getString(R.string.main_alert_delete_title));
        myDialogFragment.setMessage(getString(R.string.main_alert_delete_summary));
        myDialogFragment.setPositiveButtonTitle(getString(R.string.yes));
        myDialogFragment.setNegativeButtonTitle(getString(R.string.no));
        myDialogFragment.setPositiveClicked(new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                //fileManager.delete(list_name.get(position));

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

        return true;
    }

    private void NewProject(){
        Intent intent = new Intent(MainActivity.this, EditNameActivity.class);
        startActivity(intent);
    }
}
