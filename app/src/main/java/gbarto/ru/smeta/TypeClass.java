package gbarto.ru.smeta;

/**
 * Created by Noobgam on 10.08.2016.
 */
public class TypeClass extends DBObject implements Comparable<TypeClass>
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

    public TypeClass(String place, String name)
    {
        this.place = place;
        this.name = name;
    }

    @Override
    public int compareTo(TypeClass typeClass)
    {
        return name.compareTo(typeClass.name);
    }
}
