package gbarto.ru.smeta;

import java.util.ArrayList;

/**
 * Created by Noobgam on 08.08.2016.
 */
public class WorkClass extends DBObject implements Comparable<WorkClass>
{

    public boolean state;
    public ArrayList<Integer> Materials;
    public float price;
    public int measuring;

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

    public ArrayList<Integer> getMaterials()
    {
        return Materials;
    }

    public void setMaterials(ArrayList<Integer> materials)
    {
        Materials = materials;
    }

    public void addMaterial(int newMaterial)
    {
        Materials.add(newMaterial);
    }

    public WorkClass(boolean state, String type)
    {
        this.state = state;
        this.Materials = new ArrayList<>();
        this.measuring = -1;
        this.price = 0;
        this.name = type;
    }

    public WorkClass()
    {
        this.state = false;
        this.Materials = new ArrayList<>();
        this.measuring = -1;
        this.price = 0;
        this.name = "";
    }

    public WorkClass(boolean state, String name, ArrayList<Integer> materials, float price, int measuring)
    {
        this.state = state;
        Materials = materials;
        this.price = price;
        this.name = name;
        this.measuring = measuring;
    }

    public int compareTo(WorkClass a)
    {
        return name.compareTo(a.getName());
    }
}
