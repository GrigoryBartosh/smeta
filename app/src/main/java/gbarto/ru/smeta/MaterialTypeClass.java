package gbarto.ru.smeta;

import java.util.ArrayList;

/**
 * Created by Noobgam on 15.08.2016.
 */
public class MaterialTypeClass extends DBObject
{
    public ArrayList<Long> Materials;
    public int measurement;

    public MaterialTypeClass(ArrayList<Long> materials, int measurement)
    {
        Materials = materials;
        this.measurement = measurement;
    }

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
        Materials = materials;
    }

    public MaterialTypeClass(ArrayList<Long> materials)
    {
        Materials = materials;
    }
}
