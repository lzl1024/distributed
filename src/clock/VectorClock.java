package clock;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import message.MessagePasser;
import message.TimeStampMessage;
import record.Node;

public class VectorClock extends ClockService {

    protected VectorClock() {
        MessagePasser passer = MessagePasser.getInstance();
        HashMap<String, Node> nodes = passer.nodeMap;
        VectorTimeStamp local = new VectorTimeStamp();
        
        // put all nodes into vector and start with time 0.
        for (String nodeName : nodes.keySet()) {
            local.getRealData().put(nodeName, new AtomicInteger(0));
        }
        // start the local clock to be 1
        local.getRealData().put(passer.localName, new AtomicInteger(1));
            
        // update localTime
        localTime = local;
    }

    @SuppressWarnings("unchecked")
    @Override
    public TimeStamp<?> newTime() {
        String name = MessagePasser.getInstance().localName;
        // update correspond local time
        ((HashMap<String, AtomicInteger>)localTime.getRealData()).get(name).incrementAndGet();
        
        return localTime;
    }

    @SuppressWarnings("unchecked")
    @Override
    public TimeStamp<?> updateLocalTime(TimeStampMessage message) {
        HashMap<String, AtomicInteger> data = (HashMap<String, AtomicInteger>)localTime.getRealData();
        String name = MessagePasser.getInstance().localName;
        
        // update each fields in the vector
        synchronized(localTime.getRealData()) {
            for (Entry<String, AtomicInteger> entry : 
                ((HashMap<String, AtomicInteger>)message.getTimeStamp().getRealData()).entrySet()) {
                String key = entry.getKey();
                
                // increment local time anyway
                if (key.equals(name)) {
                    data.get(name).incrementAndGet();
                } else {
                // choose the larger time        
                    int maxInt = Math.max(entry.getValue().get(), data.get(key).get());
                    data.get(key).set(maxInt);
                }
            }
        }
        return localTime;
    }

}
