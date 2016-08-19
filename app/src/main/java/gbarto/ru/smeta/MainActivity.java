package gbarto.ru.smeta;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.PopupMenu;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        fab = (FloatingActionButton) findViewById(R.id.main_fab);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
        fab.setOnClickListener(fab_ocl);
        mListView.setOnItemLongClickListener(MainActivity.this);

        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                View c = mListView.getChildAt(0);
                float scrolly = -c.getTop() + mListView.getFirstVisiblePosition() * (c.getHeight());
                fab.setTranslationY(scrolly * 2);
                return false;
            }
        });
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
            case R.id.nav_new_project:
                NewProject();
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

            case R.id.nav_menu_abut:
                AboutProgram();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getList() {
        list_name = fileManager.Load();

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
            ProjectView(i);
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
            TextView mTextSummary = (TextView) view.findViewById(R.id.summary);
            TextView mTextPrice = (TextView) view.findViewById(R.id.price);
            ImageView mImageView = (ImageView) view.findViewById(R.id.imageView_menu);
            ImageView mImageViewClear = (ImageView) view.findViewById(R.id.imageView_clear);

            mTextName.setText(list_project.get(position).name);
            mTextSummary.setText(list_project.get(position).place);
            mTextPrice.setText(Double.toString(fileManager.getPrice(list_project.get(position))));

            View.OnClickListener ocl = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popup = new PopupMenu(MainActivity.this, view);
                    popup.getMenuInflater().inflate(R.menu.popup_menu_main, popup.getMenu());
                    popup.show();

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();
                            boolean res = false;

                            switch (id)
                            {
                                case R.id.main_popup_menu_view:
                                    ProjectView(position);
                                    res = true;
                                    break;
                                case R.id.main_popup_menu_edit:
                                    ProjectEdit(position);
                                    res = true;
                                    break;
                                case R.id.main_popup_menu_share:
                                    ProjectShare(position);
                                    res = true;
                                    break;
                                case R.id.main_popup_menu_delete:
                                    ProjectDelete(position);
                                    res = true;
                                    break;
                            }

                            return  res;
                        }
                    });
                }
            };

            mImageView.setOnClickListener(ocl);
            mImageViewClear.setOnClickListener(ocl);
            mTextPrice.setOnClickListener(ocl);

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
    }

    private void Material(){

    }

    private void Settings(){
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    private void AboutProgram(){

    }

    private void ProjectView(final int position){
        FragmentManager manager = getSupportFragmentManager();
        MyDialogFragment myDialogFragment = new MyDialogFragment();
        myDialogFragment.setTitle(getString(R.string.main_view_alert_choose));
        myDialogFragment.setUseMessage(false);
        myDialogFragment.setUsePositiveButton(false);
        myDialogFragment.setUseNegativeButton(false);
        myDialogFragment.setUseList(true);
        myDialogFragment.setList(list_alert);
        myDialogFragment.setListClicked(new DialogInterface.OnClickListener(){
            File file;
            Intent intent = new Intent();
            public void onClick(DialogInterface dialog, int id) {
                switch (id){
                    case 0:
                        fileManager.openPDF(list_project.get(position));
                        break;
                    case 1:
                        fileManager.openXLS(list_project.get(position));
                        break;
                }
                dialog.cancel();
            }
        });
        myDialogFragment.show(manager, "dialog");
    }

    private void ProjectEdit(final int position){
        Intent intent = new Intent(MainActivity.this, EditNameActivity.class);
        intent.putExtra("Project", list_project.get(position));
        startActivity(intent);
    }

    private void ProjectShare(final int position){
        FragmentManager manager = getSupportFragmentManager();
        MyDialogFragment myDialogFragment = new MyDialogFragment();
        myDialogFragment.setTitle(getString(R.string.main_share_alert_choose));
        myDialogFragment.setUseMessage(false);
        myDialogFragment.setUsePositiveButton(false);
        myDialogFragment.setUseNegativeButton(false);
        myDialogFragment.setUseList(true);
        myDialogFragment.setList(list_alert);

        myDialogFragment.setListClicked(new DialogInterface.OnClickListener(){
            File file;
            Intent intent = new Intent();
            public void onClick(DialogInterface dialog, int id) {
                switch (id){
                    case 0:
                        file = fileManager.createPDF(list_project.get(position));
                        if (file == null) break;
                        intent.setAction(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                        intent.setType("application/pdf");
                        startActivity(intent);
                        break;
                    case 1:
                        file = fileManager.createXLS(list_project.get(position));
                        if (file == null) break;
                        intent.setAction(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                        intent.setType("application/vnd.ms-excel");
                        startActivity(intent);
                        break;
                }
                dialog.cancel();
            }
        });
        myDialogFragment.show(manager, "dialog");
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
