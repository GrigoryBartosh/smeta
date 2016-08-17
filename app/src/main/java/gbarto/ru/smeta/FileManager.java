package gbarto.ru.smeta;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Noobgam on 16.08.2016.
 */
public class FileManager
{
    Context x;
    final private String extension = ".prj";
    public FileManager(Context t)
    {
        this.x = t;
    }


    public void Save(ProjectClass Project)
    {
        try {
            File path = new File(x.getFilesDir(), Project.name + extension);
            path.createNewFile();
            FileWriter fileWriter = new FileWriter(path);
            BufferedWriter printer = new BufferedWriter(fileWriter);
            printer.append("//This is automatically generated file, do not edit on your own.\n");
            printer.append("place&" + Project.place + '\n');
            for (Map.Entry<WorkTypeClass, ArrayList<WorkClass>> x : Project.works.entrySet()) {
                WorkTypeClass x1 = new WorkTypeClass(x.getKey());
                printer.append("Object&" + x1.toString() + '\n');
                ArrayList <WorkClass> tmp = new ArrayList<>();
                for (WorkClass y : x.getValue())
                    printer.append("Thing&" + y.toString() + '\n');
            }
            printer.append("_END_OF_FILE_");
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
            for (int i = 0; i < AllNames.length; ++i) {
                String s = AllNames[i].getName();
                if (s.substring(s.length() - 4).equals(extension))
                    tmp.add(s.substring(0, s.length() - 4));
            }
            return tmp;
        }
        catch (Exception e) {
            return null;
        }
    }

    private JSONObject FromString(String s)
    {
        try {
            return new JSONObject(s);
        }
        catch (JSONException e) {
            return null;
        }
    }

    String normalize (String x)
    {
        StringBuilder tmp = new StringBuilder("");
        for (int i = 0; i < x.length(); ++i) {
            if (x.charAt(i) != '[' && x.charAt(i) != ']' && x.charAt(i) != '\n' && x.charAt(i) != '\r')
                tmp.append(x.charAt(i));
        }
        return tmp.toString();
    }

    public Boolean Delete(String path)
    {
        try
        {
            File file = new File(x.getFilesDir(), path + extension);
            return file.delete();
        }
        catch (Exception e)
        {
            return false;
        }
    }

    String nospaces (String x)
    {
        StringBuilder tmp = new StringBuilder("");
        for (int i = 0; i < x.length(); ++i) {
            if (x.charAt(i) != ' ')
                tmp.append(x.charAt(i));
        }
        return tmp.toString();
    }

    private WorkClass WorkFromString(String s)
    {
        /*
        state +
                "&" + workType +
                "&" + Materials.toString() +
                "&" + RealMaterials.toString() +
                "&" + price +
                "&" + measuring +
                "&" + size;
         */
        String[] temp = s.split("&");
        WorkClass ans = new WorkClass();
        ans.state = Boolean.valueOf(temp[0]);
        ans.workType = Long.valueOf(temp[1]);
        String[] temp2 = nospaces(temp[2]).split(",");
        for (int i = 0; i < temp2.length; ++i) {
            String[] lmao = temp2[i].split(";");
            ans.Materials.add(new Pair(Long.valueOf(lmao[0]), Float.valueOf(lmao[1])));
        }
        temp2 = nospaces(temp[3]).split(",");
        for (int i = 0; i < temp2.length; ++i)
            ans.RealMaterials.add(Long.valueOf(temp2[i]));
        ans.price = Float.valueOf(temp[4]);
        ans.measuring = Integer.valueOf(temp[5]);
        ans.size = Float.valueOf(temp[6]);
        ans.name = new String(temp[7]);
        ans.rowID = Long.valueOf(temp[8]);
        return ans;
    }

    public ProjectClass LoadFromFile(String path)
    {
        try
        {
            File file = new File(x.getFilesDir(), path + extension);
            FileReader fileReader = new FileReader(file);
            ProjectClass Project = new ProjectClass(path);
            BufferedReader reader = new BufferedReader(fileReader);
            String s = "";
            WorkTypeClass temp = null;
            while (true)
            {
                s = reader.readLine();
                s = normalize(s);
                if (s.equals("_END_OF_FILE_"))
                    return Project;
                if (s.substring(0, 2).equals("//"))
                    continue;
                String tmp[] = s.split("&", 2);
                if (tmp[0].equals("place"))
                    Project.place = tmp[1];
                else {
                    if (tmp[0].equals("Object")) //then this is new KEY, otherwise whole string is VALUE
                    {
                        String[] crap = tmp[1].split("&", 3);
                        String name = crap[0];
                        Long rowID = Long.valueOf(crap[1]);
                        temp = new WorkTypeClass(name);
                        temp.rowID = rowID;
                    }
                    else {
                        if (!Project.contains(temp))
                            Project.put(temp, new ArrayList<WorkClass>());
                        Project.get(temp).add(WorkFromString(tmp[1]));
                    }
                }

            }
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
