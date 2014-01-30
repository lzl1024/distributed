package message;

import clock.TimeStamp;

public class TimeStampMessage extends Message{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;


    public TimeStampMessage(String dest, String kind, Object data) {
        super(dest, kind, data);
    }

    // timeStamp field
    private TimeStamp<?> timeStamp;

    public TimeStamp<?> getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(TimeStamp<?> timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "[timeStamp=" + timeStamp + ", header="
                + header + ", payload=" + payload + ", sendDuplicate="
                + sendDuplicate + "]";
    }
}
