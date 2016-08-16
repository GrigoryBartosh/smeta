package gbarto.ru.smeta;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Григорий on 10.08.2016.
 */
public class PopUpNameCategory extends Activity {
    public final static String NAME = "name";
    private Button mButtonCancel;
    private Button mButtonDone;
    private EditText mEditText;

    private ArrayList<String> used_name;
    private String[] bad_strings;
    private String bad_strings_to_toast = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_name_category);

        mButtonDone = (Button)findViewById(R.id.price_work_category_popup_button_done);
        mButtonCancel = (Button)findViewById(R.id.price_work_category_popup_button_cancel);
        mEditText = (EditText) findViewById(R.id.price_work_category_popup_edit_text);

        bad_strings = getResources().getStringArray(R.array.bad_strings);
        for (int i = 0; i < bad_strings.length; i++)
            bad_strings_to_toast += " " + bad_strings[i];

        used_name = getIntent().getExtras().getStringArrayList("used_name");
        for (int i = 0; i < used_name.size(); i++)
            used_name.set(i, used_name.get(i).toLowerCase());

        mButtonDone.setOnClickListener(btn_ocl);
        mButtonCancel.setOnClickListener(btn_ocl);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height= dm.heightPixels;

        getWindow().setLayout((int)(width*0.8), (int)(height*0.7));
    }

    private View.OnClickListener btn_ocl = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();

            Intent intent = new Intent();
            switch (id)
            {
                case R.id.price_work_category_popup_button_done:

                    String name = mEditText.getText().toString().replaceAll("[\\s]{2,}", " ");
                    name = name.trim();
                    String lname = name.toLowerCase();

                    if (lname.length() == 0){
                        Toast.makeText( getApplicationContext(),
                                        getString(R.string.popup_name_category_toast_need_name),
                                        Toast.LENGTH_SHORT).show();
                        break;
                    }

                    Boolean flag = true;
                    for (int j = 0; j < bad_strings.length; j++)
                        flag = flag & (lname.indexOf(bad_strings[j]) == -1);
                    if (!flag) {
                        Toast.makeText( getApplicationContext(),
                                getString(R.string.popup_name_category_toast_bad_symbol) + bad_strings_to_toast,
                                Toast.LENGTH_SHORT).show();
                        break;
                    }

                    flag = true;
                    for (int i = 0; i < used_name.size(); i++) {
                        flag = flag & !used_name.get(i).equals(lname);
                    }
                    if (!flag) {
                        Toast.makeText( getApplicationContext(),
                                getString(R.string.popup_name_category_toast_equal_name),
                                Toast.LENGTH_SHORT).show();
                        break;
                    }

                    intent.putExtra(NAME, name);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;

                case R.id.price_work_category_popup_button_cancel:
                    setResult(RESULT_CANCELED, intent);
                    finish();
                    break;
            }
        }
    };
}
