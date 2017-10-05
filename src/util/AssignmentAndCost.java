package util;

public class AssignmentAndCost implements Comparable<AssignmentAndCost> {

	private double cost;
	
	private int from, to;
	
	public int getFrom() {
		return from;
	}

	public int getTo() {
		return to;
	}

	public AssignmentAndCost(int f, int t, double cost) {
		this.from = f;
		this.to = t;
		this.cost = cost;
	}
	
	@Override
	public int compareTo(AssignmentAndCost other) {
		if (this.cost == other.cost){
			return 0;
		}
		if (this.cost > other.cost){
			return 1;
		} else {
			return -1;
		}
	}

}
