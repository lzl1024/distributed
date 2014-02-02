package message;

import clock.LogicalTimeStamp;
import clock.TimeStamp;
import clock.VectorTimeStamp;

public class TimeStampMessage extends Message implements Comparable<TimeStampMessage>{

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
	public int compareTo(TimeStampMessage o) {
	    TimeStamp<?> stamp = o.timeStamp;
	    if (stamp instanceof LogicalTimeStamp) {
	        return ((LogicalTimeStamp)this.timeStamp).compareTo((LogicalTimeStamp)stamp);
	    } else if (stamp instanceof VectorTimeStamp) {
	        return ((VectorTimeStamp)this.timeStamp).compareTo((VectorTimeStamp)stamp);
	    }
	    return 0;
	}
}
