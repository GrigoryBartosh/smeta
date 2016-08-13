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

/**
 * Created by Григорий on 10.08.2016.
 */
public class PriceWorkCategoryPopUp extends Activity {
    public final static String NAME = "gbarto.ru.smeta.NAME";
    private Button mButtonCancel;
    private Button mButtonDone;
    private EditText mEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_work_category_popup);

        mButtonDone = (Button)findViewById(R.id.price_work_category_popup_button_done);
        mButtonCancel = (Button)findViewById(R.id.price_work_category_popup_button_cancel);
        mEditText = (EditText) findViewById(R.id.price_work_category_popup_edit_text);

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
            switch (id)
            {
                case R.id.price_work_category_popup_button_done:

                    String name = mEditText.getText().toString();
                    if (name.length() == 0){
                        Toast.makeText( getApplicationContext(),
                                        getString(R.string.price_work_category_popup_need_name),
                                        Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra(NAME, name);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                    break;

                case R.id.price_work_category_popup_button_cancel:
                    Intent intent = new Intent();
                    setResult(RESULT_CANCELED, intent);
                    finish();
                    break;
            }
        }
    };
}
