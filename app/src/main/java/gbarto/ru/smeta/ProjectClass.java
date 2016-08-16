package gbarto.ru.smeta;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Noobgam on 16.08.2016.
 */
//This is never stored in Database, so it shouldn't extend DBObject
public class ProjectClass implements Serializable
{
    private TreeMap<WorkTypeClass, ArrayList<WorkClass>> works;
    String place;
    String name;

    public ProjectClass()
    {
        works = new TreeMap<>();
    }

    public boolean contains(WorkTypeClass Key)
    {
        return works.containsKey(Key);
    }

    public ArrayList<WorkClass> get(WorkTypeClass Key)
    {
        return works.get(Key);
    }

    public void put(WorkTypeClass Key, ArrayList <WorkClass> Value)
    {
        WorkTypeClass x1 = new WorkTypeClass(Key);
        ArrayList <WorkClass> tmp = new ArrayList<>();
        for (WorkClass y : Value)
            tmp.add(new WorkClass(y));
        this.works.put(x1, tmp);
    }

    public ProjectClass(TreeMap<WorkTypeClass, ArrayList<WorkClass>> works)
    {
        for (Map.Entry<WorkTypeClass, ArrayList<WorkClass>> x : works.entrySet()) {
            WorkTypeClass x1 = new WorkTypeClass(x.getKey());
            ArrayList <WorkClass> tmp = new ArrayList<>();
            for (WorkClass y : x.getValue())
                tmp.add(new WorkClass(y));
            this.works.put(x1, tmp);
        }
    }
}
