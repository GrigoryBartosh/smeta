package gbarto.ru.smeta;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Noobgam on 08.08.2016.
 */
public class WorkClass extends DBObject implements Comparable<WorkClass>
{

    public boolean state;
    public long workType;

    public ArrayList<Pair <Long, Float> > Materials;
    public ArrayList<MaterialClass> RealMaterials;
    public ArrayList<Pair <Long, Float> > Instruments;
    public int coefficient;
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
        this.coefficient = a.coefficient;
        this.Materials = new ArrayList<>();
        for (Pair <Long, Float> x : a.Materials)
            this.Materials.add(new Pair<>(Long.valueOf(x.first), Float.valueOf(x.second)));
        this.Instruments = new ArrayList<>();
        for (Pair <Long, Float> x : a.Instruments)
            this.Instruments.add(new Pair<>(Long.valueOf(x.first), Float.valueOf(x.second)));
        this.RealMaterials = new ArrayList<>();
        for (MaterialClass x : a.RealMaterials)
            this.RealMaterials.add(x);
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
        RealMaterials.add(null);
    }

    public void removeMaterial(int index)
    {
        Materials.remove(index);
        RealMaterials.remove(index);
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
        this.coefficient = 1;
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
        this.coefficient = 1;
        this.RealMaterials = new ArrayList<>();
        this.workType = -1;
        size = 0f;
    }

    public WorkClass (String name)
    {
        this.state = false;
        this.Materials = new ArrayList<>();
        this.Instruments = new ArrayList<>();
        this.measuring = -1;
        this.price = 0;
        this.coefficient = 1;
        this.name = name;
        this.RealMaterials = new ArrayList<>();
        this.workType = -1;
        size = 0f;
    }

    public WorkClass(boolean state, String name, ArrayList<Pair <Long, Float>> materials, float price, int measuring, long workType, int coefficient)
    {
        this.state = state;
        this.coefficient = coefficient;
        Materials = new ArrayList<>();
        RealMaterials = new ArrayList<>();
        for (Pair <Long, Float> x : materials) {
            Materials.add(new Pair(x.first, x.second));
            RealMaterials.add(null);
        }
        Materials = new ArrayList<>(materials);
        this.Instruments = new ArrayList<>();
        this.price = price;
        this.name = new String(name);
        this.measuring = measuring;
        this.workType = workType;
        size = 0f;
    }

    public WorkClass(boolean state, String name, ArrayList<Pair <Long, Float>> materials, ArrayList<MaterialClass> realMaterials,  float price, int measuring, long workType, int coefficient)
    {
        this.state = state;
        this.coefficient = coefficient;
        Materials = new ArrayList<>();
        RealMaterials = new ArrayList<>();
        this.Instruments = new ArrayList<>();
        for (Pair <Long, Float> x : materials) {
            Materials.add(new Pair(x.first, x.second));
        }
        for (MaterialClass x : realMaterials) {
            RealMaterials.add(x);
        }
        Materials = new ArrayList<>(materials);
        this.price = price;
        this.name = new String(name);
        this.measuring = measuring;
        this.workType = workType;
        size = 0f;
    }

    final String delimeter = "&12";

    public double getAmount()
    {
        double work_total = this.size * this.price;
        for (int i = 0; i < RealMaterials.size(); ++i)
            if (RealMaterials.get(i) != null) {
                MaterialClass material = RealMaterials.get(i);
                if (RealMaterials.get(i).per_object < (1e-8)) {
                    double wasted = this.size * this.Materials.get(i).second * material.price;
                    work_total += wasted;
                } else {
                    int amount = (int) Math.ceil((double) this.size * this.Materials.get(i).second / material.per_object);
                    double wasted = amount * material.price;
                    work_total += wasted;
                }
            }
        return work_total * coefficient;
    }
    
    @Override
    public String toString()
    {
        String s1 = state +
            delimeter + workType +
            delimeter + Materials.toString() +
            delimeter;

        String temp = "";
        for (int i = 0; i < RealMaterials.size(); ++i) {
            try {
                MaterialClass materialClass = RealMaterials.get(i);
                JSONObject object = new JSONObject();
                object.put("name", materialClass.name);
                object.put("price", materialClass.price);
                object.put("measuring", materialClass.measuring);
                object.put("iconID", materialClass.iconID);
                object.put("per_object", materialClass.per_object);
                temp += object.toString();
                if (i != RealMaterials.size() - 1)
                    temp += "321&";
            }
            catch (Exception e) {
                temp += "null";
                if (i != RealMaterials.size() - 1)
                    temp += "321&";
            }
        }
        String s2 = delimeter + price +
            delimeter + measuring +
            delimeter + size +
            delimeter + name +
            delimeter + rowID +
            delimeter + coefficient +
            delimeter + Instruments.toString();
        return s1 + temp + s2;
    }


    public int compareTo(WorkClass a)
    {
        if (rowID == a.rowID) return 0;
        return (rowID < a.rowID) ? -1 : 1;
    }
}
