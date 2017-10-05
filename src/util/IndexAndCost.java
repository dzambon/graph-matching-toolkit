package util;

public class IndexAndCost implements Comparable<IndexAndCost>{

	private int index;
	
	private double cost;
	
	public IndexAndCost(int i, double c) {
		this.index = i;
		this.cost = c;
	}
	
	

	public int getIndex() {
		return index;
	}



	public double getCost() {
		return cost;
	}



	@Override
	public int compareTo(IndexAndCost other) {
		if (this.cost == other.cost){
			return 0;
		}
		if (this.cost > other.cost){
			return 1;
		} else {
			return -1;
		}
	}
	
	public String toString(){
		return this.index+" "+this.cost;
	}

}
