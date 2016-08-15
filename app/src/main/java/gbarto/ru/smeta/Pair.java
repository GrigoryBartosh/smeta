package gbarto.ru.smeta;

/**
 * Created by Noobgam on 15.08.2016.
 */
public class Pair<T, X>
{
    public T first;
    public X second;

    public Pair(T first, X second)
    {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pair<?, ?> pair = (Pair<?, ?>) o;

        if (first != null ? !first.equals(pair.first) : pair.first != null) return false;
        return second != null ? second.equals(pair.second) : pair.second == null;

    }

    @Override
    public int hashCode()
    {
        int result = first != null ? first.hashCode() : 0;
        result = 31 * result + (second != null ? second.hashCode() : 0);
        return result;
    }

    public T getFirst()
    {

        return first;
    }

    public void setFirst(T first)
    {
        this.first = first;
    }

    public X getSecond()
    {
        return second;
    }

    public void setSecond(X second)
    {
        this.second = second;
    }
}
