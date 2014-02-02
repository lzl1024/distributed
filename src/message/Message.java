package message;

import java.io.Serializable;

public class Message implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    protected Header header = null;
    protected Object payload = null;
    protected boolean sendDuplicate;
    protected boolean rcvDuplicate;

	public enum Type {
	    INFO, SEVERE, ERROR, DEFAULT
	}

    public class Header implements Serializable {
        /**
         * 
         */

        private static final long serialVersionUID = 1L;
        private int sequenceNumber;
        private String source;
        private String dest;
        private String kind;
        private Type type;
        public Header(String dest, String kind, Type type) {
            this.dest = dest;
            this.kind = kind;
            this.type = type;
        }
        
        public Header(String dest, String kind) {
            this.dest = dest;
            this.kind = kind;
            this.type = Type.DEFAULT;
        }
        
        public Type getType() {
			return type;
		}

		public void setType(Type type) {
			this.type = type;
		}

		@Override
        public String toString() {
            return "[sequenceNumber=" + sequenceNumber + ", source="
                    + source + ", dest=" + dest + ", kind=" + kind + "]";
        }   
    }
    
    public Message(String dest, String kind, Object data) {
        this.header = new Header(dest, kind);
        this.payload = data;
        this.sendDuplicate = false;
        this.rcvDuplicate = false;
    }

    public Message(String dest, String kind, Object data, Type type) {
        this.header = new Header(dest, kind, type);
        this.payload = data;
        this.sendDuplicate = false;
        this.rcvDuplicate = false;
    }
    
    public void set_seqNum(int sequenceNumber) {
        this.header.sequenceNumber = sequenceNumber;
    }
    
    public int get_seqNumr() {
        return this.header.sequenceNumber;
    }

    public String get_source() {
        return this.header.source;
    }

    public void set_source(String source) {
        this.header.source = source;
    }

    public String getDest() {
        return header.dest;
    }

    public void setDest(String dest) {
        this.header.dest = dest;
    }

    public String getKind() {
        return header.kind;
    }

    public void setKind(String kind) {
        this.header.kind = kind;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public void set_sendDuplicate(Boolean dupe) {
        this.sendDuplicate = dupe;
    }
    
    public boolean get_sendDuplicate() {
        return this.sendDuplicate;
    }
    
    public void set_rcvDuplicate(Boolean dupe) {
        this.rcvDuplicate = dupe;
    }
    
    public boolean get_rcvDuplicate() {
        return this.rcvDuplicate;
    }


    public Type getType() {
		return this.header.getType();
	}

	public void setType(Type type) {
		this.header.setType(type);
	}
    @Override
    public String toString() {
        return "[header=" + header + ", payload=" + payload
                + ", sendDuplicate=" + sendDuplicate + "]";
    }
}