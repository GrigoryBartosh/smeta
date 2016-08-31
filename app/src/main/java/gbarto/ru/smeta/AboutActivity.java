package gbarto.ru.smeta;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.audiofx.BassBoost;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

/**
 * Created by grigory on 20.08.2016.
 */
public class AboutActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawer;
    private TextView mTextVersion;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawer = (DrawerLayout) findViewById(R.id.about_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.about_toolbar);
        mTextVersion = (TextView) findViewById(R.id.about_textView_version);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = pInfo.versionName;
        mTextVersion.setText(version);
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_projects:
                Projects();
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
                break;
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void Projects(){
        Intent intent = new Intent(AboutActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void PriceWork(){
        Intent intent = new Intent(AboutActivity.this, PriceWorkCategoryActivity.class);
        startActivity(intent);
        finish();
    }

    private void Material(){
        //finish();
    }

    private void Settings(){
        Intent intent = new Intent(AboutActivity.this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }

    private void Contacts(){
        //finish();
    }

    private void Archive(){
        //finish();
    }
}
