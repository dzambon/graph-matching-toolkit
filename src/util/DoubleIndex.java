package util;

public class DoubleIndex {

	private int from;
	
	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public int getTo() {
		return to;
	}

	public void setTo(int to) {
		this.to = to;
	}

	private int to;
	
	public DoubleIndex(int from, int to) {
		this.from = from;
		this.to = to;
	}

	public String toString(){
		return "("+this.from+", "+this.to+")";
	}
	
}
