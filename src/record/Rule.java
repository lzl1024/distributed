package record;

import message.Message;

public class Rule {
	public enum ACTION {
		DROP, DELAY, DUPLICATE, DEFAULT
	}

	private ACTION action = null;
	private String src = null;
	private String dest = null;
	private String kind = null; 
	private String duplicate = null;
	private int id = 0;
	private int Nth = 0;
	private int everyNth = 0;
	private int matchedTimes = 0;

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

	public int getMatchedTimes() {
		return matchedTimes;
	}
	public void setMatchedTimes(int m) {
		this.matchedTimes = m;
	}

	public String getDuplicate () {
		return duplicate;
	}

	public void setDuplicate (String dup) {
		this.duplicate = dup;
	}
	public boolean isMatch(Message message) {
		if (getDuplicate() == null || (getDuplicate().equalsIgnoreCase("true") && message.get_sendDuplicate()) ||
				(getDuplicate().equalsIgnoreCase("false") && !message.get_sendDuplicate())) {
			if (getId() == 0 || getId() == message.get_seqNumr()){
				if (getSrc() == null || getSrc().equalsIgnoreCase(message.get_source())){
					if (getDest() == null || getDest().equalsIgnoreCase(message.getDest())){
						if (getKind() == null || getKind().equalsIgnoreCase(message.getKind())){
							setMatchedTimes(getMatchedTimes() + 1);
							if (getNth() != 0) {
								if (getNth() == getMatchedTimes()) {
									return true;
								}else if (getEveryNth() == 0) {
									return false;
								}
							}
							if (getEveryNth() != 0) {
								if (getMatchedTimes()%getEveryNth() == 0) {
									return true;
								}else return false;
							}
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	public boolean equals(Rule r) {  
		if (!r.getAction().equals(getAction())) return false;
		
		if (r.getSrc() != null) {
			if (!r.getSrc().equals(getSrc())) return false;
		}else {
			if (getSrc() != null) return false;
		}
		
		if (r.getDest() != null) {
			if (!r.getDest().equals(getDest())) return false;
		}else {
			if (getDest() != null) return false;
		}

		if (r.getKind() != null) {
			if (!r.getKind().equals(getKind())) return false;
		} else {
			if (getKind() != null) return false;
		}

		if (r.getDuplicate() != null) {
			if (!r.getDuplicate().equals(getDuplicate())) return false;
		} else {
			if (getDuplicate() != null) return false;
		}

		if (r.getId() != getId()) return false;
		if (r.getNth() != getNth()) return false;
		if (r.getEveryNth() != getEveryNth()) return false;
		return true;
	}

	@Override
	public String toString() {
		return "[action=" + action + ", src=" + src + ", dest=" + dest
				+ ", kind=" + kind + ", id=" + id + ", Nth=" + Nth
				+ ", everyNth=" + everyNth + ", duplicate =" + duplicate + "]";
	}
}
