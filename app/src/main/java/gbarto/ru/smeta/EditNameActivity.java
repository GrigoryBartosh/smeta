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

public class EditNameActivity extends AppCompatActivity {

    private Button mButton;
    private EditText mText;
    private int BackRes;
    DBAdapter adapter;
    ProjectClass Project = null;

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
            mText.setText(Project.name);
        }

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mText.getText().length() >= 0) {
                    Intent x = new Intent(EditNameActivity.this, EditRoomActivity.class);
                    if (Project == null)
                        Project = new ProjectClass();
                    Project.name = new String(mText.getText().toString());
                    x.putExtra("Project", Project);

                    x.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivityForResult(x, BackRes);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), getString(R.string.price_project_name_popup_need_name), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

//                if (charSequence.charAt(charSequence.length() - 1) == '\n') {
//                    System.out.println("ACTIVATED");
//                    mButton.performClick();
                //}
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    protected void onDestroy()
    {
        adapter.close();
        super.onDestroy();
    }
}
