package gbarto.ru.smeta;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Григорий on 13.08.2016.
 */
public class WorkAboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actity_price_work_category_popup);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height= dm.heightPixels;

        getWindow().setLayout((int)(width*0.8), (int)(height*0.7));
    }
}
