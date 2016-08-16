package gbarto.ru.smeta;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;

/**
 * Created by Noobgam on 16.08.2016.
 */
public class FileManager
{
    Context x;
    public FileManager(Context t)
    {
        this.x = t;
    }

    public void Save(WorkClass workClass)
    {
        File root = new File(Environment.getExternalStorageDirectory(), "Works");
        File path = new File(root, workClass.name + ".work");
        try {
            FileWriter printer = new FileWriter(path);
            printer.append("//This is automatically generated file, do not edit on your own.");
        }
        catch (Exception e)
        {
            Toast.makeText(x.getApplicationContext(), "Could not save file, try again.", Toast.LENGTH_SHORT).show();
        }
    }
}
