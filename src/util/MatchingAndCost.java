package util;

public class MatchingAndCost implements Comparable<MatchingAndCost>{

	private int from;
	public int getFrom() {
		return from;
	}

	public int getTo() {
		return to;
	}

	private int to;
	private double cost;

	public MatchingAndCost(int from, int to, double d) {
		this.from = from;
		this.to = to;
		this.cost = d;
	}

	@Override
	public int compareTo(MatchingAndCost other) {
		return (int) (this.cost - other.cost);
	}

}
