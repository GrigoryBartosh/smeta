package gbarto.ru.smeta;

import java.io.Serializable;

/**
 * Created by Noobgam on 30.08.2016.
 */
public class RoomClass implements Serializable
{
    String name;
    String visible_name;
    int id;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getVisible_name()
    {
        return visible_name;
    }

    public void setVisible_name(String visible_name)
    {
        this.visible_name = visible_name;
    }

    public RoomClass(String name)
    {
        this.name = name;
        this.visible_name = new String(name);
    }

    public RoomClass(String visible_name, String name)
    {
        this.visible_name = visible_name;
        this.name = name;
    }

    @Override
    public String toString()
    {
        return super.toString();
    }
}
