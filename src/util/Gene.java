package util;

public class Gene implements Comparable<Gene>{
	
	private int[][] matching;
	
	public int[][] getMatching() {
		return matching;
	}

	private double distance;
	private double[][] cm;
	
	

	public double getDistance() {
		return distance;
	}

	public Gene(Graph sourceGraph, Graph targetGraph, int[][] m,
			CostFunction costFunction, EditDistance editDistance, double mutProb) {
		this.matching = m;
		for (int i = 0; i < this.matching.length; i++){
			if (Math.random() < mutProb){
				int j = this.matching[i][1];
				int pick = (int) Math.floor(Math.random() * this.matching.length);
				this.matching[i][1] = this.matching[pick][1];
				this.matching[pick][1] = j;
			}
		}
		this.distance = editDistance.getEditDistance(
				sourceGraph, targetGraph, this.matching, costFunction);
	
	}

	public Gene(Graph sourceGraph, Graph targetGraph, int[][] mergedMatching,
			CostFunction costFunction, EditDistance editDistance) {
		this.matching = mergedMatching;
		this.distance = editDistance.getEditDistance(
				sourceGraph, targetGraph, this.matching, costFunction);
	}

	public Gene(int[][] m, double d, double[][] cm) {
		this.matching = m;
		this.distance = d;
		this.setCm(cm);
	}
	
	public Gene(int[][] m, double d) {
		this.matching = m;
		this.distance = d;
	}
	
	public String toString(){
		return "Distance: "+this.distance; 
	}
	
	public void printMe(){
		for (int i = 0; i < this.cm.length; i++){
			for (int j = 0; j < this.cm[0].length; j++){
				System.out.print(this.cm[i][j]+" ");
			}
			System.out.println();
		}
	}

	@Override
	public int compareTo(Gene o) {
	
		if (this.distance > o.distance){
			return 1;
		} else {
			return -1;
		}
	}

	public double[][] getCm() {
		return cm;
	}

	public void setCm(double[][] cm) {
		this.cm = cm;
	}
	
	

}
