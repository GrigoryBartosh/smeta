package gbarto.ru.smeta;

/**
 * Created by Noobgam on 10.08.2016.
 */
public class WorkTypeClass extends DBObject implements Comparable<WorkTypeClass>
{
    public String place;

    public String getPlace()
    {
        return place;
    }

    public void setPlace(String place)
    {
        this.place = place;
    }

    public WorkTypeClass(String place, String name)
    {
        this.place = place;
        this.name = name;
    }

    @Override
    public int compareTo(WorkTypeClass workTypeClass)
    {
        return name.compareTo(workTypeClass.name);
    }
}
