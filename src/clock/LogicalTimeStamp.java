package clock;

import java.util.concurrent.atomic.AtomicInteger;

public class LogicalTimeStamp extends TimeStamp<AtomicInteger> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public LogicalTimeStamp() {
        time = new AtomicInteger(0);
    }

    @Override
    public int compareTo(TimeStamp<AtomicInteger> time2) {
        int ownTime = time.get();
        int otherTime = ((LogicalTimeStamp)time2).getRealData().get();
        if (ownTime < otherTime) {
            return -1;
        }      
        return ownTime > otherTime ? 1 : 0;
    }

    @Override
    public AtomicInteger getRealData() {
        return time;
    }
}
