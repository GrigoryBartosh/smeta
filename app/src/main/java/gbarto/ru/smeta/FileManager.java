package gbarto.ru.smeta;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedWriter;
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

    public void Save(ProjectClass Project)
    {
        File path = new File(Environment.getExternalStorageDirectory(), Project.name + ".prj");
        try {
            FileWriter fileWriter = new FileWriter(path);
            BufferedWriter printer = new BufferedWriter(fileWriter);
            printer.append("//This is automatically generated file, do not edit on your own.\n");
            printer.append("place:" + Project.place);
            printer.close();
        }
        catch (Exception e)
        {
            Toast.makeText(x.getApplicationContext(), "Could not save file, try again.", Toast.LENGTH_SHORT).show();
        }
    }
}
