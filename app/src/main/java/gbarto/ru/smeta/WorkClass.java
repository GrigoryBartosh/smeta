package gbarto.ru.smeta;

import java.util.ArrayList;

/**
 * Created by Noobgam on 08.08.2016.
 */
public class WorkClass extends DBObject implements Comparable<WorkClass>
{

    public boolean state;
    public long workType;

    public ArrayList<Long> Materials;
    public float price;
    public int measuring;

    public long getWorkType()
    {
        return workType;
    }

    public void setWorkType(long workType)
    {
        this.workType = workType;
    }

    public boolean isState()
    {
        return state;
    }

    public void setState(boolean state)
    {
        this.state = state;
    }

    public float getPrice()
    {
        return price;
    }

    public void setPrice(float price)
    {
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

    public ArrayList<Long> getMaterials()
    {
        return Materials;
    }

    public void setMaterials(ArrayList<Long> materials)
    {
        Materials = materials;
    }

    public void addMaterial(long newMaterial)
    {
        Materials.add(newMaterial);
    }

    public WorkClass(boolean state, String name)
    {
        this.state = state;
        this.Materials = new ArrayList<>();
        this.measuring = -1;
        this.price = 0;
        this.name = name;
        this.workType = -1;
    }

    public WorkClass()
    {
        this.state = false;
        this.Materials = new ArrayList<>();
        this.measuring = -1;
        this.price = 0;
        this.name = "";
        this.workType = -1;
    }

    public WorkClass(boolean state, String name, ArrayList<Long> materials, float price, int measuring, long workType)
    {
        this.state = state;
        Materials = materials;
        this.price = price;
        this.name = name;
        this.measuring = measuring;
        this.workType = workType;
    }

    public int compareTo(WorkClass a)
    {
        return name.compareTo(a.getName());
    }
}
