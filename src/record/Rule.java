package record;

public class Rule {
    public enum ACTION {
        DROP, DELAY, DUPLICATE, DEFAULT
    }
    
    private ACTION action = null;
    private String src = null;
    private String dest = null;
    private String kind = null;
    private int id = 0;
    private int Nth = 0;
    private int everyNth = 0;

    public ACTION getAction() {
        return action;
    }
    public void setAction(String action) {
        String str = action.toLowerCase();
        if (str.equals("duplicate")) {
            this.action = ACTION.DUPLICATE;
        } else if (str.equals("delay")) {
            this.action = ACTION.DELAY;
        } else if (str.equals("drop")) {
            this.action = ACTION.DROP;
        }
    }
    public void setAction(ACTION action) {
        this.action = action;
    }
    public String getSrc() {
        return src;
    }
    public void setSrc(String src) {
        this.src = src;
    }
    public String getDest() {
        return dest;
    }
    public void setDest(String dest) {
        this.dest = dest;
    }
    public String getKind() {
        return kind;
    }
    public void setKind(String kind) {
        this.kind = kind;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getNth() {
        return Nth;
    }
    public void setNth(int nth) {
        Nth = nth;
    }
    public int getEveryNth() {
        return everyNth;
    }
    public void setEveryNth(int everyNth) {
        this.everyNth = everyNth;
    }
    @Override
    public String toString() {
        return "[action=" + action + ", src=" + src + ", dest=" + dest
                + ", kind=" + kind + ", id=" + id + ", Nth=" + Nth
                + ", everyNth=" + everyNth + "]";
    }
}
