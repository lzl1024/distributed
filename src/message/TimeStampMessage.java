package message;

import clock.TimeStamp;

public class TimeStampMessage<E> extends Message implements Comparable<TimeStamp<E>>{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;


    public TimeStampMessage(String dest, String kind, Object data) {
        super(dest, kind, data);
    }

    // timeStamp field
    private TimeStamp<?> timeStamp;
    
    //private String destNode;
    
//    public String getDestNode() {
//		return destNode;
//	}
//
//	public void setDestNode(String destNode) {
//		this.destNode = destNode;
//	}

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

	@Override
	public int compareTo(TimeStamp<E> o) {
		return this.compareTo(o);
		// TODO Auto-generated method stub
	}
}
