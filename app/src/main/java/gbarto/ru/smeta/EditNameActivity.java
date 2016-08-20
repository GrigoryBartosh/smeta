package gbarto.ru.smeta;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class EditNameActivity extends AppCompatActivity {

    LinearLayout Button_Choose_Works;
    LinearLayout Save_Button;
    LinearLayout Share_Button;
    LinearLayout Completed_Button;
    FileManager fileManager;
    EditText mText;
    int countreturned = 0;
    TextView Summary;

    private TextView ChooseRoomText;
    private int RoomResult = 1337;
    private int TypesResult = 1488;
    private int CompletedResult = 239;
    String projectname = "";
    DBAdapter adapter;
    ImageButton imageButton;
    ProjectClass Project;
    String keeper;

    //returns message
    private String check(String s)
    {
        if (s == null)
            return "INTERNAL ERROR";
        if (s.length() < 1)
            return getString(R.string.price_project_name_popup_need_name);
        String[] bad = getResources().getStringArray(R.array.bad_strings);
        String have = "";
        for (String t: bad)
            if (s.contains(t)) {
                if (have.length() != 0)
                    have += ", ";
                have += t;
            }
        FileManager fileManager = new FileManager(EditNameActivity.this);
        ArrayList<String> temp = fileManager.Load();
        s = s.trim();
        s = s.replaceAll("[\\s]{2,}", "\\s");
        s = s.replaceAll("[\n, \r]", " ");
        for (String x : temp)
                if (x.equals(s) && !x.equals(projectname))
                    return getString(R.string.such_project_already_exists);
        projectname = s;
        if (have.equals(""))
            return "OK";
        return getString(R.string.popup_name_category_toast_bad_symbol) + " " + have;
    }

    public void ProjectShare(){
        FragmentManager manager = getSupportFragmentManager();
        MyDialogFragment myDialogFragment = new MyDialogFragment();
        myDialogFragment.setTitle(getString(R.string.main_share_alert_choose));
        myDialogFragment.setUseMessage(false);
        myDialogFragment.setUsePositiveButton(false);
        myDialogFragment.setUseNegativeButton(false);
        myDialogFragment.setUseList(true);
        myDialogFragment.setList(getResources().getStringArray(R.array.main_list));

        myDialogFragment.setListClicked(new DialogInterface.OnClickListener(){
            File file;
            Intent intent = new Intent();
            public void onClick(DialogInterface dialog, int id) {
                switch (id){
                    case 0:
                        file = fileManager.createPDF(Project);
                        if (file == null) break;
                        intent.setAction(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                        intent.setType("application/pdf");
                        startActivity(intent);
                        break;
                    case 1:
                        file = fileManager.createXLS(Project);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        String checker = mText.getText().toString();
        final String message = check(checker);
        if (message.equals("OK")) {
            Project.name = checker;
            FileManager fileManager = new FileManager(EditNameActivity.this);
            fileManager.Save(Project);
            countreturned = 0;
            Toast.makeText(getApplicationContext(), getString(R.string.saved), Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void Refresh()
    {
        if (Project != null)
        {
            String temp = getString(R.string.total) + " " + String.format("%.2f", new FileManager(this).getPrice(Project));
            Summary.setText(temp);
        }
        else
            Summary.setVisibility(View.INVISIBLE);
        super.onResume();
    }

    private void ProjectView(){
        FragmentManager manager = getSupportFragmentManager();
        MyDialogFragment myDialogFragment = new MyDialogFragment();
        myDialogFragment.setTitle(getString(R.string.main_view_alert_choose));
        myDialogFragment.setUseMessage(false);
        myDialogFragment.setUsePositiveButton(false);
        myDialogFragment.setUseNegativeButton(false);
        myDialogFragment.setUseList(true);
        myDialogFragment.setList(getResources().getStringArray(R.array.main_list));
        myDialogFragment.setListClicked(new DialogInterface.OnClickListener(){
            Intent intent = new Intent();
            public void onClick(DialogInterface dialog, int id) {
                switch (id){
                    case 0:
                        fileManager.openPDF(Project);
                        break;
                    case 1:
                        fileManager.openXLS(Project);
                        break;
                }
                dialog.cancel();
            }
        });
        myDialogFragment.show(manager, "dialog");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_name_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name);
        Toolbar toolbar = (Toolbar) findViewById(R.id.edit_name_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fileManager = new FileManager(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                if (countreturned > 0) {
                    FragmentManager manager = getSupportFragmentManager();
                    MyDialogFragment myDialogFragment = new MyDialogFragment();
                    myDialogFragment.Message = getString(R.string.want_to_discard_changes);
                    myDialogFragment.Title = getString(R.string.want_to_go_back);
                    myDialogFragment.PositiveButtonTitle = getString(R.string.yes);
                    myDialogFragment.NegativeButtonTitle = getString(R.string.no);
                    myDialogFragment.PositiveClicked = new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            Intent temp = new Intent();
                            setResult(RESULT_CANCELED, temp);
                            finish();
                        }
                    };
                    myDialogFragment.show(manager, "dialog");
                }
                else
                {
                    Intent temp = new Intent();
                    setResult(RESULT_CANCELED, temp);
                    finish();
                }
            }
        });

        adapter = new DBAdapter(this);
        adapter.open();

        mText = (EditText)findViewById(R.id.Project_name_field);
        ChooseRoomText = (TextView) findViewById(R.id.place_text);
        Completed_Button = (LinearLayout)findViewById(R.id.button_mark_done);
        Completed_Button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent x = new Intent(EditNameActivity.this, CompleteActivity.class);
                x.putExtra("project", Project);
                startActivityForResult(x, CompletedResult);
            }
        });
        Share_Button = (LinearLayout)findViewById(R.id.button_share);
        Share_Button.setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View view)
                                            {
                                                ProjectShare();
                                            }
                                        });

        LinearLayout View_Button = (LinearLayout)findViewById(R.id.button_view);
        View_Button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String checker = mText.getText().toString();
                final String message = check(checker);
                if (message.equals("OK")) {
                    Project.name = checker;
                    ProjectView( );
                }
                else
                {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
            }
        });
        Save_Button = (LinearLayout) findViewById(R.id.button_save);
        Save_Button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String checker = mText.getText().toString();
                final String message = check(checker);
                if (message.equals("OK")) {
                    Project.name = checker;
                    FileManager fileManager = new FileManager(EditNameActivity.this);
                    fileManager.Save(Project);
                    countreturned = 0;
                    Toast.makeText(getApplicationContext(), getString(R.string.saved), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
            }
        });
        imageButton = (ImageButton) findViewById(R.id.choose_room_imageButton);
        imageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent x = new Intent(EditNameActivity.this, EditRoomActivity.class);
                x.putExtra("Project", Project);
                startActivityForResult(x, RoomResult);
            }
        });
        Summary = (TextView)findViewById(R.id.project_summary);
        if (getIntent().getSerializableExtra("Project") != null) {
            Project = (ProjectClass) getIntent().getSerializableExtra("Project");
            projectname = Project.name;
            mText.setText(Project.name);
        }
        else
        {
            if (Project == null) {
                Project = new ProjectClass();
                Project.place = getString(R.string.room_other_room);
            }
        }
        UpdateRoom();
        Refresh();
        keeper = mText.getText().toString();

        Button_Choose_Works = (LinearLayout)findViewById(R.id.button_new_works);
        Button_Choose_Works.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent x = new Intent(EditNameActivity.this, ChooseTypeActivity.class);
                if (Project == null)
                    Project = new ProjectClass();
                x.putExtra("Project", Project);
                startActivityForResult(x, TypesResult);
            }
        });

        mText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable)
            {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                if (charSequence.length() < keeper.length())
                    keeper = charSequence.toString();
                else if (charSequence.length() > i && charSequence.charAt(i) == '\n') {
                    mText.setText(keeper);
                    mText.setSelection(keeper.length());
                }
                else if (charSequence.length() > 100) {
                    mText.setText(keeper);
                    mText.setSelection(keeper.length());
                }
                else
                    keeper = charSequence.toString();

             }});

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
    public void onBackPressed()
    {
        if (countreturned > 0) {
            FragmentManager manager = getSupportFragmentManager();
            MyDialogFragment myDialogFragment = new MyDialogFragment();
            myDialogFragment.Message = getString(R.string.want_to_discard_changes);
            myDialogFragment.Title = getString(R.string.want_to_go_back);
            myDialogFragment.PositiveButtonTitle = getString(R.string.yes);
            myDialogFragment.NegativeButtonTitle = getString(R.string.no);
            myDialogFragment.PositiveClicked = new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    Intent temp = new Intent();
                    setResult(RESULT_CANCELED, temp);
                    finish();
                }
            };
            myDialogFragment.show(manager, "dialog");
        }
        else
        {
            Intent temp = new Intent();
            setResult(RESULT_CANCELED, temp);
            finish();
        }
    }

    void UpdateRoom()
    {
        ChooseRoomText.setText(Project.place);
        if (Project.place.equals(getString(R.string.room_kitchen)))
            imageButton.setImageResource(R.drawable.kitchen);
        else if (Project.place.equals(getString(R.string.room_dining_room)))
            imageButton.setImageResource(R.drawable.dining_room);
        else if (Project.place.equals(getString(R.string.room_bathroom)))
            imageButton.setImageResource(R.drawable.bathroom);
        else if (Project.place.equals(getString(R.string.room_bedroom)))
            imageButton.setImageResource(R.drawable.bedroom);
        else if (Project.place.equals(getString(R.string.room_other_room)))
            imageButton.setImageResource(R.drawable.ic_menu_help);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == RoomResult) {
            if (resultCode == RESULT_OK) {
                ++countreturned;
                Project.place = ((ProjectClass) data.getSerializableExtra("Project")).place;
                UpdateRoom();
            }
        } else if (requestCode == TypesResult) {
            if (resultCode == RESULT_OK)
            {
                ++countreturned;
                Project.works = ((ProjectClass) data.getSerializableExtra("Project")).works;
                Refresh();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy()
    {
        adapter.close();
        super.onDestroy();
    }
}
