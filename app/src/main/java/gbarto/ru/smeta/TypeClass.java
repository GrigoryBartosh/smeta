package gbarto.ru.smeta;

/**
 * Created by Noobgam on 10.08.2016.
 */
public class TypeClass extends DBObject implements Comparable<TypeClass>
{
    public String place;
    public String type;

    public String getPlace()
    {
        return place;
    }

    public void setPlace(String place)
    {
        this.place = place;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public TypeClass(String place, String type)
    {
        this.place = place;
        this.type = type;
    }

    @Override
    public int compareTo(TypeClass typeClass)
    {
        return type.compareTo(typeClass.getType());
    }
}
