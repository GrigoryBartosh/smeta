package gbarto.ru.smeta;

/**
 * Created by Noobgam on 10.08.2016.
 */
public class WorkTypeClass extends DBObject implements Comparable<WorkTypeClass>
{
    WorkTypeClass(WorkTypeClass a)
    {
        this.name = new String(a.name);
        this.rowID = a.rowID;
    }

    public WorkTypeClass(String name)
    {
        this.name = new String(name);
    }

    @Override
    public String toString()
    {
        return name + "&" + rowID;
    }

    @Override
    public int compareTo(WorkTypeClass workTypeClass)
    {
        if (rowID == workTypeClass.rowID) return 0;
        return (rowID < workTypeClass.rowID) ? -1 : 1;
    }
}
