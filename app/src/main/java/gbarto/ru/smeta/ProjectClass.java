package gbarto.ru.smeta;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by Noobgam on 16.08.2016.
 */
//This is never stored in Database, so it shouldn't extend DBObject
public class ProjectClass implements Serializable
{
    public ArrayList<Pair <RoomClass, TreeMap<WorkTypeClass, ArrayList<WorkClass> > > > works;
    int place;
    String name;

    public ProjectClass()
    {
        works = new ArrayList<>();
    }

    public ProjectClass(String name)
    {
        works = new ArrayList<>();
        this.name = new String(name);
    }

    public boolean contains(WorkTypeClass Key)
    {
        return works.get(place).second.containsKey(Key);
    }

    public ArrayList<WorkClass> get(WorkTypeClass Key)
    {
        if (!works.get(place).second.containsKey(Key))
            return null;
        return works.get(place).second.get(Key);
    }

    public void put(WorkTypeClass Key, ArrayList <WorkClass> Value)
    {
        WorkTypeClass x1 = new WorkTypeClass(Key);
        ArrayList <WorkClass> tmp = new ArrayList<>();
        for (WorkClass y : Value)
            tmp.add(new WorkClass(y));
        this.works.get(place).second.put(x1, tmp);
    }
}
