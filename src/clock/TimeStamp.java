package clock;

import java.io.Serializable;

public abstract class TimeStamp<E> implements Comparable<TimeStamp<E>>, Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    // data
    protected E time;
    
    abstract public E getRealData();

    @Override
    public String toString() {
        return "[time=" + time + "]";
    }
}
