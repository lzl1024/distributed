package clock;

import java.util.concurrent.atomic.AtomicInteger;

import message.TimeStampMessage;

public class LogicalClock extends ClockService {
    
    protected LogicalClock() {
        localTime = new LogicalTimeStamp();
    }

    @Override
    public TimeStamp<?> newTime() {
        ((AtomicInteger)(localTime.getRealData())).incrementAndGet();
        return localTime;
    }

    @Override
    public TimeStamp<?> updateLocalTime(TimeStampMessage message) {
        synchronized(localTime) {
            int messageData = ((LogicalTimeStamp)message.getTimeStamp()).getRealData().get();
            int selfData = ((AtomicInteger)(localTime.getRealData())).get();
            ((AtomicInteger)(localTime.getRealData())).set(Math.max(messageData, selfData) + 1);
        }
        return localTime;
    }

}
