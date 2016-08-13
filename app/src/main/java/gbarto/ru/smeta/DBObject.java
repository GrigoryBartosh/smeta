package gbarto.ru.smeta;

import java.io.Serializable;

/**
 * Created by Noobgam on 10.08.2016.
 */
public class DBObject implements Serializable
{
    long rowID;

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
        if (this instanceof WorkListView && a instanceof WorkListView)
            return ((WorkListView)this).compareTo((WorkListView)a);
        if (this instanceof MaterialClass && a instanceof MaterialClass)
            return ((MaterialClass)this).compareTo((MaterialClass)a);
        if (this instanceof TypeClass && a instanceof TypeClass)
            return ((TypeClass)this).compareTo((TypeClass)a);
        return 0;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof WorkListView)
            return ((WorkListView)this).state == ((WorkListView)obj).state &&
                    ((WorkListView)this).type.equals(((WorkListView)obj).type) &&
                    ((WorkListView)this).measuring == ((WorkListView)obj).measuring &&
                    ((WorkListView)this).price == ((WorkListView)obj).price &&
                    ((WorkListView)this).Materials.equals(((WorkListView)obj).Materials);
        
        if (obj instanceof TypeClass)
            return ((TypeClass)this).place.equals(((TypeClass)obj).place) &&
                    ((TypeClass)this).type.equals(((TypeClass)obj).type);

        if (obj instanceof MaterialClass)
            return ((MaterialClass)this).material.equals(((MaterialClass)obj).material) &&
                    ((MaterialClass)this).price == ((MaterialClass)obj).price &&
                    ((MaterialClass)this).measuring == ((MaterialClass)obj).measuring &&
                    ((MaterialClass)this).iconID == ((MaterialClass)obj).iconID;

        return super.equals(obj);
    }
//an empty class just to return it from DB and be able to cast it.
}
