package clock;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

public class VectorTimeStamp extends TimeStamp<HashMap<String, AtomicInteger>> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public VectorTimeStamp() {
        time = new HashMap<String, AtomicInteger>();
    }

    @Override
    public int compareTo(TimeStamp<HashMap<String, AtomicInteger>> time2) {
        // flag to see if one element has already before/after the correspond element
        boolean beforeFlag = false, afterFlag = false;
        
        HashMap<String, AtomicInteger> timeStamp = time2.getRealData();
        for (Entry<String, AtomicInteger> entry : time.entrySet()) {
            // if timeStamp structure is not the same, concurrent
            if (!timeStamp.containsKey(entry.getKey())) {
                return 0;
            }
            
            int otherTime =  timeStamp.get(entry.getKey()).get();
            int ownTime = entry.getValue().get();
            
            if (ownTime < otherTime) {
                // before after flag be true, return concurrent
                if (afterFlag) {
                    return 0;
                }
                beforeFlag = true;
            }    
            if (ownTime > otherTime) {
                // before after flag be true, return concurrent
                if (beforeFlag) {
                    return 0;
                }
                afterFlag = true;
            } 
        }

        // only afterFlag is true
        if (afterFlag) {
            return 1;
        }
        return beforeFlag ? -1 : 0;
    }

    @Override
    public HashMap<String, AtomicInteger> getRealData() {
        return time;
    }

}
