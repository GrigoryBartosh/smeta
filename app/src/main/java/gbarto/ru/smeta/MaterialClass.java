package gbarto.ru.smeta;

/**
 * Created by Noobgam on 09.08.2016.
 */
public class MaterialClass extends DBObject implements Comparable <MaterialClass>
{
    public float price;
    public int measuring;
    public int iconID;
    public float per_object;

    public float getPer_object()
    {
        return per_object;
    }

    public void setPer_object(float per_object)
    {
        this.per_object = per_object;
    }

    public int getIconID()
    {
        return iconID;
    }

    public void setIconID(int iconID)
    {
        this.iconID = iconID;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getMeasuring()
    {
        return measuring;
    }

    public void setMeasuring(int measuring)
    {
        this.measuring = measuring;
    }

    public MaterialClass(String name, float price, int measuring, int iconID, float per_object)
    {
        this.name = new String(name);
        this.price = price;
        this.measuring = measuring;
        this.iconID = iconID;
        this.per_object = per_object;
    }

    public int compareTo(MaterialClass a)
    {
        return name.compareTo(a.name);
    }
}
