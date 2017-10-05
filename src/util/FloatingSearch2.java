package util;

import java.util.Arrays;
import java.util.LinkedList;

import algorithms.BipartiteMatching;

public class FloatingSearch2 {

	private Graph sourceGraph;
	private Graph targetGraph;
	private int[][] originalMatching;
	private CostFunction costFunction;
	private EditDistance editDistance;
	private double[][] originalCostMatrix;
	private BipartiteMatching bipartiteMatching;

	public FloatingSearch2(Graph sourceGraph, Graph targetGraph,
			int[][] matching, CostFunction costFunction,
			EditDistance editDistance, double[][] costMatrix,
			BipartiteMatching bipartiteMatching) {
		this.sourceGraph = sourceGraph;
		this.targetGraph = targetGraph;
		this.originalMatching = matching;
		this.costFunction = costFunction;
		this.editDistance = editDistance;
		this.originalCostMatrix = costMatrix;
		this.bipartiteMatching = bipartiteMatching;
	}

	public double searchBetter() {
		double[] distances = new double[this.originalMatching.length+1];
		Arrays.fill(distances, Double.MAX_VALUE);
		
		double d = this.editDistance.getEditDistance(this.sourceGraph,
				this.targetGraph, this.originalMatching, this.costFunction);
		distances[0] = d;
				
		int[][] m = this.originalMatching;
		double[][] cm = this.copyMatrix(this.originalCostMatrix);
		
		LinkedList<DoubleIndex> forbiddenIndices = new LinkedList<DoubleIndex>();
		
		
		for (int i = 0; i < this.originalMatching.length; i++){
//			System.out.println("*** "+i+" Forbidden are: "+forbiddenIndices);
//			this.printDistances(distances);
			m = this.bipartiteMatching.getMatching(cm);
			this.editDistance.getEditDistance(this.sourceGraph,
			this.targetGraph, m, this.costFunction);
			int forbid = this.computeForbid(m, cm);
			if (forbid > -1) {
				forbiddenIndices.add(new DoubleIndex(forbid, m[forbid][1]));
				cm[forbid][m[forbid][1]] = Double.MAX_VALUE;

				m = this.bipartiteMatching.getMatching(cm);

				double distance = this.editDistance.getEditDistance(sourceGraph,
						targetGraph, m, costFunction);
				distances[i+1] = distance;

				boolean improved = true;
				int j = -1;
				while (improved){
					j++;
					improved = false;
					int bestRemove = -1;
					for (int k = 0; k < forbiddenIndices.size(); k++){
						int f = forbiddenIndices.get(k).getFrom();
						int t = forbiddenIndices.get(k).getTo();
						cm[f][t] = this.originalCostMatrix[f][t];
						m = this.bipartiteMatching.getMatching(cm);
						distance = this.editDistance.getEditDistance(sourceGraph,
								targetGraph, m, costFunction);
						if (distance < distances[i-j]){
							distances[i-j] = distance;
							bestRemove = k;
							improved = true;
						} else {
						}
						cm[f][t] = Double.MAX_VALUE;
					}
					if (bestRemove != -1){
						
						DoubleIndex removed = forbiddenIndices.remove(bestRemove);
						cm[removed.getFrom()][removed.getTo()] = this.originalCostMatrix[removed.getFrom()][removed.getTo()];
					}
				}
				i = i-j;
			} else {
				i = this.originalMatching.length;
			}
		}
		return this.getMinimum(distances);
	}

	private int computeForbid(int[][] m, double[][] cm) {
		double minDist = Double.MAX_VALUE;
		int minIndex = -1;
		for (int i = 0; i < m.length; i++){
			int f = m[i][0];
			int t = m[i][1];
			double temp = cm[f][t];
			cm[f][t] = Double.MAX_VALUE;
			m = this.bipartiteMatching.getMatching(cm);
			double d = this.editDistance.getEditDistance(this.sourceGraph,
			this.targetGraph, m, this.costFunction); 
			if (d < minDist){
				minDist = d;
				minIndex = i;
			}
			cm[f][t] = temp;
		}
		return minIndex;
	}

	private void printDistances(double[] distances) {
		for (int i = 0; i < distances.length; i++){
			System.out.println("d["+i+"] = "+distances[i]);
		}
		
	}

	private void printMatching(int[][] m) {
		for (int i = 0; i < m.length; i++){
			System.out.print(m[i][0]+" --> "+m[i][1]+" ; ");
		}
		
		
	}

	private void printCostMatrix(double[][] cm) {
		for (int i = 0; i < cm.length; i++){
			for (int j = 0; j < cm[0].length; j++){
				System.out.print(cm[i][j]+"\t");
			}
			System.out.println();
		}
		
	}

	private double getMinimum(double[] distances) {
		double min = Double.MAX_VALUE;
		for (int i = 0; i < distances.length; i++){
			if (distances[i] < min){
				min = distances[i];
			}
		}
		return min;
	}

	private double[][] copyMatrix(double[][] m) {
		double[][] copy = new double[m.length][m[0].length];
		for (int i = 0; i < copy.length; i++){
			for (int j = 0; j < copy[0].length; j++){
				copy[i][j] = m[i][j];
			}
		}
		return copy;
	}

	
}
