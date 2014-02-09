package record;

import java.io.Serializable;

/**
 * 
 * Helper class to find specific multicast message
 *
 */
public class MultiMsgId implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public String group;
    public int seqNum;
    
       
    public MultiMsgId(String group, int seqNum) {
        super();
        this.group = group;
        this.seqNum = seqNum;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((group == null) ? 0 : group.hashCode());
        result = prime * result + seqNum;
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MultiMsgId other = (MultiMsgId) obj;
        if (group == null) {
            if (other.group != null)
                return false;
        } else if (!group.equals(other.group))
            return false;
        if (seqNum != other.seqNum)
            return false;
        return true;
    }
    
   
}
