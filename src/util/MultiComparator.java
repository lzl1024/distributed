package util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;

import message.MulticastMessage;

public class MultiComparator implements Comparator<MulticastMessage>{

    @Override
    public int compare(MulticastMessage arg0, MulticastMessage arg1) {
        // flag to see if one element has already before/after the correspond element
        boolean beforeFlag = false, afterFlag = false;
        
        HashMap<String, Integer> vector1 = arg0.getGrpSeqVector();
        for (Entry<String, Integer> entry : arg1.getGrpSeqVector().entrySet()) {
            // if timeStamp structure is not the same, concurrent
            if (!vector1.containsKey(entry.getKey())) {
                return 0;
            }
            
            int otherVector =  vector1.get(entry.getKey());
            int ownVector = entry.getValue();
            
            if (ownVector < otherVector) {
                // before after flag be true, return concurrent
                if (afterFlag) {
                    return 0;
                }
                beforeFlag = true;
            }    
            if (ownVector > otherVector) {
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

}
