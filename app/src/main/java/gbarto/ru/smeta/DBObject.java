package gbarto.ru.smeta;

import java.io.Serializable;

/**
 * Created by Noobgam on 10.08.2016.
 */
public class DBObject implements Serializable
{
    long rowID;
    String name;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public long getRowID()
    {
        return rowID;
    }

    public void setRowID(long rowID)
    {
        this.rowID = rowID;
    }

    public int compareTo(DBObject a)
    {
        if (this instanceof WorkClass && a instanceof WorkClass)
            return ((WorkClass)this).compareTo((WorkClass)a);
        if (this instanceof MaterialClass && a instanceof MaterialClass)
            return ((MaterialClass)this).compareTo((MaterialClass)a);
        if (this instanceof WorkTypeClass && a instanceof WorkTypeClass)
            return ((WorkTypeClass)this).compareTo((WorkTypeClass)a);
        return 0;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof WorkClass)
            return ((WorkClass)this).state == ((WorkClass)obj).state &&
                    ((WorkClass)this).name.equals(((WorkClass)obj).name) &&
                    ((WorkClass)this).measuring == ((WorkClass)obj).measuring &&
                    ((WorkClass)this).price == ((WorkClass)obj).price &&
                    ((WorkClass)this).Materials.equals(((WorkClass)obj).Materials);
        
        if (obj instanceof WorkTypeClass)
            return ((WorkTypeClass)this).name.equals(((WorkTypeClass)obj).name);

        if (obj instanceof MaterialClass)
            return ((MaterialClass)this).name.equals(((MaterialClass)obj).name) &&
                    ((MaterialClass)this).price == ((MaterialClass)obj).price &&
                    ((MaterialClass)this).measuring == ((MaterialClass)obj).measuring &&
                    ((MaterialClass)this).iconID == ((MaterialClass)obj).iconID;

        return super.equals(obj);
    }
//an empty class just to return it from DB and be able to cast it.
}
