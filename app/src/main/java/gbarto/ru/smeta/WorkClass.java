package gbarto.ru.smeta;

import java.util.ArrayList;

/**
 * Created by Noobgam on 08.08.2016.
 */
public class WorkClass extends DBObject implements Comparable<WorkClass>
{

    public boolean state;
    public long workType;

    public WorkClass(WorkClass a)
    {
        this.name = new String(a.name);
        this.rowID = a.rowID;
        this.state = a.state;
        this.workType = a.workType;
    }

    public ArrayList<Pair <Long, Float> > Materials;
    public ArrayList<Pair <Long, Float> > RealMaterials;

    public ArrayList<Pair<Long, Float>> getRealMaterials()
    {
        return RealMaterials;
    }

    public void setRealMaterials(ArrayList<Pair<Long, Float>> realMaterials)
    {
        RealMaterials = realMaterials;
    }

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

    public ArrayList<Pair <Long, Float> > getMaterials()
    {
        return Materials;
    }

    public void setMaterials( ArrayList<Pair <Long, Float>> materials)
    {
        Materials = materials;
    }

    public void addMaterial(long newMaterial)
    {
        Materials.add(new Pair(newMaterial, 0.0f));
        RealMaterials.add(new Pair(-1L, -1.0f));
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
        this.RealMaterials = new ArrayList<>();
        this.workType = -1;
    }

    public WorkClass(boolean state, String name, ArrayList<Pair <Long, Float>> materials, float price, int measuring, long workType)
    {
        this.state = state;
        Materials = new ArrayList<>();
        RealMaterials = new ArrayList<>();
        for (Pair <Long, Float> x : materials) {
            Materials.add(new Pair(x.first, x.second));
            RealMaterials.add(new Pair(-1, -1));
        }
        Materials = new ArrayList<>(materials);
        this.price = price;
        this.name = new String(name);
        this.measuring = measuring;
        this.workType = workType;
    }

    public WorkClass(boolean state, String name, ArrayList<Pair <Long, Float>> materials, ArrayList<Pair <Long, Float>> realMaterials,  float price, int measuring, long workType)
    {
        this.state = state;
        Materials = new ArrayList<>();
        RealMaterials = new ArrayList<>();
        for (Pair <Long, Float> x : materials) {
            Materials.add(new Pair(x.first, x.second));
        }
        for (Pair <Long, Float> x : realMaterials) {
            RealMaterials.add(new Pair(x.first, x.second));
        }
        Materials = new ArrayList<>(materials);
        this.price = price;
        this.name = new String(name);
        this.measuring = measuring;
        this.workType = workType;
    }


    public int compareTo(WorkClass a)
    {
        return name.compareTo(a.getName());
    }
}
