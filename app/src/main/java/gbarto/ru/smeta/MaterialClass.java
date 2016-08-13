package gbarto.ru.smeta;

/**
 * Created by Noobgam on 09.08.2016.
 */
public class MaterialClass extends DBObject implements Comparable <MaterialClass>
{
    public String material;
    public float price;
    public int measuring;
    public int iconID;

    public int getIconID()
    {
        return iconID;
    }

    public void setIconID(int iconID)
    {
        this.iconID = iconID;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
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

    public MaterialClass(String material, float price, int measuring, int iconID)
    {
        this.material = material;
        this.price = price;
        this.measuring = measuring;
        this.iconID = iconID;
    }

    public int compareTo(MaterialClass a)
    {
        return a.getMaterial().compareTo(a.getMaterial());
    }
}
