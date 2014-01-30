package clock;

import message.TimeStampMessage;

@SuppressWarnings("rawtypes")
public abstract class ClockService {
    public enum CLOCK_TYPE {
        LOGICAL, VECTOR
    }
    private static ClockService instance = null; 
    // singleton
    protected ClockService() {};
    
    public static ClockService getInstance() {
        return instance;
    }
    
    // local timestamp
    protected TimeStamp localTime;
    
    /**
     * get the current time
     * @return
     */
    public TimeStamp<?> getTime() {
        return localTime;
    }
    
    // create a new timestamp when necessary
    abstract public TimeStamp<?> newTime();
    // update time when receive messages
    abstract public TimeStamp<?> updateLocalTime(TimeStampMessage message);
    
    
    /**
     * Create clock using factory pattern
     * @param type
     * @return
     * @throws Exception
     */
    public static ClockService createClock(CLOCK_TYPE type) throws Exception {
        switch (type) {
        case LOGICAL:
            instance = new LogicalClock();
            return instance;
        case VECTOR:
            instance = new VectorClock();
            return instance;
        default:
            throw new Exception("Invalid clock type!");              
        }     
    }
}
