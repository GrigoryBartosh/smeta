package gbarto.ru.smeta;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ListView mListView;
    private FileManager fileManager = new FileManager(MainActivity.this);
    ArrayList<String> list_name;
    ArrayList<ProjectClass> list_project;

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

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        fab.setOnClickListener(fab_ocl);
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

    View.OnClickListener fab_ocl = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this, EditNameActivity.class);
            startActivity(intent);
        }
    };

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_new_project:
                break;

            case R.id.nav_price_work:
                Intent intent = new Intent(MainActivity.this, PriceWorkCategoryActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_price_material:
                break;

            case R.id.nav_menu_settings:
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
    }
}
