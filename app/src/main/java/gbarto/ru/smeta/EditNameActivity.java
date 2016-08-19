package gbarto.ru.smeta;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class EditNameActivity extends AppCompatActivity {

    private Button mButton;
    private EditText mText;
    private int BackRes;
    String projectname = "";
    DBAdapter adapter;
    ProjectClass Project = null;
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
        if (have.equals(""))
            return "OK";
        return getString(R.string.popup_name_category_toast_bad_symbol) + " " + have;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name);
        Toolbar toolbar = (Toolbar) findViewById(R.id.edit_name_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        adapter = new DBAdapter(this);
        adapter.open();

        mButton = (Button)findViewById(R.id.edit_name_next_button);
        mText = (EditText) findViewById(R.id.ProjectName);
        if (getIntent().getSerializableExtra("Project") != null) {
            Project = (ProjectClass) getIntent().getSerializableExtra("Project");
            projectname = Project.name;
            mText.setText(Project.name);
        }
        keeper = mText.getText().toString();

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String message = check(mText.getText().toString());
                if (message.equals("OK")) {
                    Intent x = new Intent(EditNameActivity.this, EditRoomActivity.class);
                    if (Project == null)
                        Project = new ProjectClass();
                    Project.name = mText.getText().toString();
                    Project.name = Project.name.trim();
                    Project.name = Project.name.replaceAll("[\\s]{2,}", "\\s");
                    Project.name = Project.name.replaceAll("[\n, \r]", " ");
                    x.putExtra("Project", Project);

                    x.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivityForResult(x, BackRes);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
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
                else if (charSequence.charAt(i) == '\n') {
                    mText.setText(keeper);
                    mText.setSelection(keeper.length());
                    mButton.performClick();
                }
                else if (charSequence.length() > 100) {
                    mText.setText(keeper);
                    mText.setSelection(keeper.length());
                }
                else
                    keeper = charSequence.toString();

             }});
    }

    @Override
    protected void onDestroy()
    {
        adapter.close();
        super.onDestroy();
    }
}
