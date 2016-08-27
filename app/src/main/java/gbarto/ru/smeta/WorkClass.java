package gbarto.ru.smeta;

import java.util.ArrayList;

/**
 * Created by Noobgam on 08.08.2016.
 */
public class WorkClass extends DBObject implements Comparable<WorkClass>
{

    public boolean state;
    public long workType;
    public ArrayList<Pair <Long, Float> > Materials;
    public ArrayList<Long> RealMaterials;
    public ArrayList<Pair <Long, Float> > Instruments;
    public float price;
    public int measuring;
    public float size;

    public WorkClass(WorkClass a)
    {
        this.name = new String(a.name);
        this.rowID = a.rowID;
        this.state = a.state;
        this.workType = a.workType;
        this.measuring = a.measuring;
        this.price = a.price;
        this.Materials = new ArrayList<>();
        for (Pair <Long, Float> x : a.Materials)
            this.Materials.add(new Pair<>(Long.valueOf(x.first), Float.valueOf(x.second)));
        this.Instruments = new ArrayList<>();
        for (Pair <Long, Float> x : a.Instruments)
            this.Instruments.add(new Pair<>(Long.valueOf(x.first), Float.valueOf(x.second)));
        this.RealMaterials = new ArrayList<>();
        for (Long x : a.RealMaterials)
            this.RealMaterials.add(Long.valueOf(x));
        size = a.size;
    }

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
        RealMaterials.add(-1L);
    }

    public void removeMaterial(int index)
    {
        Materials.remove(index);
    }

    public void addInstrument(long newInstrument)
    {
        Instruments.add(new Pair(newInstrument, 0.0f));
    }

    public void removeInstrument(int index)
    {
        Instruments.remove(index);
    }

    public WorkClass(boolean state, String name)
    {
        this.state = state;
        this.Materials = new ArrayList<>();
        this.Instruments = new ArrayList<>();
        this.measuring = -1;
        this.price = 0;
        this.name = name;
        this.workType = -1;
    }

    public WorkClass()
    {
        this.state = false;
        this.Materials = new ArrayList<>();
        this.Instruments = new ArrayList<>();
        this.measuring = -1;
        this.price = 0;
        this.name = "";
        this.RealMaterials = new ArrayList<>();
        this.workType = -1;
        size = 0f;
    }

    public WorkClass(boolean state, String name, ArrayList<Pair <Long, Float>> materials, float price, int measuring, long workType)
    {
        this.state = state;
        Materials = new ArrayList<>();
        RealMaterials = new ArrayList<>();
        for (Pair <Long, Float> x : materials) {
            Materials.add(new Pair(x.first, x.second));
            RealMaterials.add(-1L);
        }
        Materials = new ArrayList<>(materials);
        this.Instruments = new ArrayList<>();
        this.price = price;
        this.name = new String(name);
        this.measuring = measuring;
        this.workType = workType;
        size = 0f;
    }

    public WorkClass(boolean state, String name, ArrayList<Pair <Long, Float>> materials, ArrayList<Long> realMaterials,  float price, int measuring, long workType)
    {
        this.state = state;
        Materials = new ArrayList<>();
        RealMaterials = new ArrayList<>();
        this.Instruments = new ArrayList<>();
        for (Pair <Long, Float> x : materials) {
            Materials.add(new Pair(x.first, x.second));
        }
        for (Long x : realMaterials) {
            RealMaterials.add(x);
        }
        Materials = new ArrayList<>(materials);
        this.price = price;
        this.name = new String(name);
        this.measuring = measuring;
        this.workType = workType;
        size = 0f;
    }


    @Override
    public String toString()
    {
        return  state +
                "&" + workType +
                "&" + Materials.toString() +
                "&" + RealMaterials.toString() +
                "&" + price +
                "&" + measuring +
                "&" + size +
                "&" + name +
                "&" + rowID +
                "&" + Instruments.toString();
    }


    public int compareTo(WorkClass a)
    {
        if (rowID == a.rowID) return 0;
        return (rowID < a.rowID) ? -1 : 1;
    }
}
