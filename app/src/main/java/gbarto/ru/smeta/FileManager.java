package gbarto.ru.smeta;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

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
        try {
            File path = new File(x.getFilesDir(), Project.name + ".prj");
            path.createNewFile();
            FileWriter fileWriter = new FileWriter(path);
            BufferedWriter printer = new BufferedWriter(fileWriter);
            printer.append("//This is automatically generated file, do not edit on your own.\n");
            printer.append("place:" + Project.place);
            printer.close();
            fileWriter.close();
        }
        catch (Exception e)
        {
            Toast.makeText(x.getApplicationContext(), "Could not save file, try again.", Toast.LENGTH_SHORT).show();
        }
    }

    public ArrayList <String> Load()
    {
        try {
            File[] AllNames = x.getFilesDir().listFiles();
            ArrayList <String> tmp = new ArrayList<>();
            for (int i = 0; i < AllNames.length; ++i)
                tmp.add(AllNames[i].getName());
            return tmp;
        }
        catch (Exception e) {
            return null;
        }
    }

    public ProjectClass LoadFromFile()
    {
        return new ProjectClass();
    }
}
