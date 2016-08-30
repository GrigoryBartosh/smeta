package gbarto.ru.smeta;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.IOException;

public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout mDrawer;
    private ImageView mImageView;
    private EditText mEditFirstName;
    private EditText mEditSurname;
    private EditText mEditPhone;
    private EditText mEditEmail;
    private EditText mEditCompanyName;
    private EditText mEditCompanyPhone;
    private EditText mEditCompanyEmail;
    private EditText mEditCompanyAddress;

    static final int CAMERA = 1;
    static final int GALLERY = 2;

    private SettingsManager settingsManager;
    private String[] list_no_photo;
    private String[] list_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar     = (Toolbar) findViewById(R.id.settings_toolbar);
        mImageView = (ImageView) findViewById(R.id.settings_imageView);
        mEditFirstName      = (EditText) findViewById(R.id.settings_editText_first_name);
        mEditSurname        = (EditText) findViewById(R.id.settings_editText_surname);
        mEditPhone          = (EditText) findViewById(R.id.settings_editText_phone);
        mEditEmail          = (EditText) findViewById(R.id.settings_editText_email);
        mEditCompanyName    = (EditText) findViewById(R.id.settings_editText_company_name);
        mEditCompanyPhone   = (EditText) findViewById(R.id.settings_editText_company_phone);
        mEditCompanyEmail   = (EditText) findViewById(R.id.settings_editText_company_email);
        mEditCompanyAddress = (EditText) findViewById(R.id.settings_editText_company_address);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawer = (DrawerLayout) findViewById(R.id.settings_drawer);

        settingsManager = new SettingsManager(SettingsActivity.this);
        mEditFirstName.     setText(settingsManager.getFirstName());
        mEditSurname.       setText(settingsManager.getSurname());
        mEditPhone.         setText(settingsManager.getPhone());
        mEditEmail.         setText(settingsManager.getEmail());
        mEditCompanyName.   setText(settingsManager.getCompanyName());
        mEditCompanyPhone.  setText(settingsManager.getCompanyPhone());
        mEditCompanyEmail.  setText(settingsManager.getCompany_Email());
        mEditCompanyAddress.setText(settingsManager.getCompanyAddress());
        if (settingsManager.havePhoto()) {
            mImageView.setPadding(0,0,0,0);
            mImageView.setImageBitmap(settingsManager.getPhoto());
        }

        list_no_photo = getResources().getStringArray(R.array.settings_no_photo);
        list_photo = getResources().getStringArray(R.array.settings_photo);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        mImageView.setOnClickListener(imageView_ocl);
        int color = getResources().getColor(R.color.ic_menu);
        PorterDuff.Mode mMode = PorterDuff.Mode.SRC_ATOP;
        Drawable d;
        d = getResources().getDrawable(android.R.drawable.ic_menu_help);
        d.setColorFilter(color, mMode);
        d.setAlpha(255);
    }

    @Override
    protected void onDestroy() {
        settingsManager.setFirstName        (normalize(mEditFirstName       .getText().toString()));
        settingsManager.setSurname          (normalize(mEditSurname         .getText().toString()));
        settingsManager.setPhone            (normalize(mEditPhone           .getText().toString()));
        settingsManager.setEmail            (normalize(mEditEmail           .getText().toString()));
        settingsManager.setCompanyName      (normalize(mEditCompanyName     .getText().toString()));
        settingsManager.setCompanyPhone     (normalize(mEditCompanyPhone    .getText().toString()));
        settingsManager.setCompany_Email    (normalize(mEditCompanyEmail    .getText().toString()));
        settingsManager.setCompanyAddress   (normalize(mEditCompanyAddress  .getText().toString()));

        settingsManager.close();
        super.onDestroy();
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

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private String normalize(String s)
    {
        return s.replaceAll("[\\s]{2,}", " ").trim();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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
                myDialogFragment.setMessage(getString(R.string.settings_about_text));
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

    View.OnClickListener imageView_ocl = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FragmentManager manager = getSupportFragmentManager();
            MyDialogFragment myDialogFragment = new MyDialogFragment();
            myDialogFragment.setTitle(getString(R.string.settings_alert_choose));
            myDialogFragment.setUseMessage(false);
            myDialogFragment.setUsePositiveButton(false);
            myDialogFragment.setUseNegativeButton(false);
            myDialogFragment.setUseList(true);

            if (settingsManager.havePhoto()){
                myDialogFragment.setList(list_photo);
                myDialogFragment.setListClicked(new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id) {
                        switch (id){
                            case 0:
                                settingsManager.deletePhoto();
                                mImageView.setPadding(40,40,40,40);
                                mImageView.setImageResource(R.drawable.ic_add_photo);
                                break;
                            case 1:
                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                    startActivityForResult(takePictureIntent, CAMERA);
                                }
                                break;
                            case 2:
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, "Select File"), GALLERY);
                                break;
                        }
                        dialog.cancel();
                    }
                });
            } else {
                myDialogFragment.setList(list_no_photo);
                myDialogFragment.setListClicked(new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id) {
                        switch (id){
                            case 0:
                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                    startActivityForResult(takePictureIntent, CAMERA);
                                }
                                break;
                            case 1:
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);//
                                startActivityForResult(Intent.createChooser(intent, "Select File"), GALLERY);
                                break;
                        }
                        dialog.cancel();
                    }
                });
            }

            myDialogFragment.show(manager, "dialog");
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                if (imageBitmap != null)
                {
                    mImageView.setPadding(0,0,0,0);
                    mImageView.setImageBitmap(imageBitmap);
                    settingsManager.setPhoto(imageBitmap);
                }
            }
        }
        if (requestCode == GALLERY) {
            Bitmap bm=null;
            if (data != null) {
                try {
                    bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());

                    String realPath;

                    if (Build.VERSION.SDK_INT < 11)                                                 // SDK < API11
                        realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(this, data.getData());
                    else if (Build.VERSION.SDK_INT < 19)                                            // SDK >= 11 && SDK < 19
                        realPath = RealPathUtil.getRealPathFromURI_API11to18(this, data.getData());
                    else                                                                            // SDK > 19 (Android 4.4)
                        realPath = RealPathUtil.getRealPathFromURI_API19(this, data.getData());

                    ExifInterface exif = new ExifInterface(realPath);
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                    Matrix matrix = new Matrix();
                    switch (orientation) {
                        case  0: matrix.postRotate(0  ); break;
                        case  1: matrix.postRotate(0  ); break;
                        case  2: matrix.postRotate(90 ); break;
                        case  3: matrix.postRotate(180); break;
                        case  4: matrix.postRotate(180); break;
                        case  5: matrix.postRotate(90 ); break;
                        case  6: matrix.postRotate(90 ); break;
                        case  7: matrix.postRotate(270); break;
                        case  8: matrix.postRotate(270); break;
                        default: matrix.postRotate(0  ); break;
                    }
                    bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bm != null)
            {
                mImageView.setPadding(0,0,0,0);
                mImageView.setImageBitmap(bm);
                settingsManager.setPhoto(bm);
            }
        }
    }

    private void Projects(){
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void PriceWork(){
        Intent intent = new Intent(SettingsActivity.this, PriceWorkCategoryActivity.class);
        startActivity(intent);
        finish();
    }

    private void Material(){
        //finish();
    }

    private void Contacts(){
        //finish();
    }

    private void Archive(){
        //finish();
    }

    private void AboutProgram(){
        Intent intent = new Intent(SettingsActivity.this, AboutActivity.class);
        startActivity(intent);
        finish();
    }
}
