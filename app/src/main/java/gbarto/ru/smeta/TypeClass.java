package gbarto.ru.smeta;

import java.util.ArrayList;

/**
 * Created by Noobgam on 10.08.2016.
 */
public class TypeClass extends DBObject implements Comparable<TypeClass>
{
    public String place;
    public ArrayList<Integer> availableWorks;

    public ArrayList<Integer> getAvailableWorks()
    {
        return availableWorks;
    }

    public void setAvailableWorks(ArrayList<Integer> availableWorks)
    {
        this.availableWorks = availableWorks;
    }

    public String getPlace()
    {
        return place;
    }

    public void setPlace(String place)
    {
        this.place = place;
    }

    public TypeClass(String place, String name)
    {
        this.place = place;
        this.name = name;
        this.availableWorks = new ArrayList<Integer>();
    }

    @Override
    public int compareTo(TypeClass typeClass)
    {
        return name.compareTo(typeClass.name);
    }
}
