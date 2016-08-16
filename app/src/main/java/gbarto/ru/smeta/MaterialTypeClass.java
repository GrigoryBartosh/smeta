package gbarto.ru.smeta;

import java.util.ArrayList;

/**
 * Created by Noobgam on 15.08.2016.
 */
public class MaterialTypeClass extends DBObject
{
    public ArrayList<Long> Materials;
    public int measurement;

    public int getMeasurement()
    {
        return measurement;
    }

    public void setMeasurement(int measurement)
    {
        this.measurement = measurement;
    }

    public ArrayList<Long> getMaterials()
    {

        return Materials;
    }

    public void setMaterials(ArrayList<Long> materials)
    {
        Materials = new ArrayList<>();
        for (Long x : materials)
            Materials.add(x);
    }

    public MaterialTypeClass(ArrayList<Long> materials)
    {
        Materials = new ArrayList<>();
        for (Long x : materials)
            Materials.add(x);
    }

    public MaterialTypeClass(String name, ArrayList<Long> materials, int measurement)
    {
        this.name = name;
        Materials = new ArrayList<>();
        for (Long x : materials)
            Materials.add(x);
        this.measurement = measurement;
    }
}
