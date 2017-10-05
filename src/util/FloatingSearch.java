package util;

import java.util.Arrays;
import java.util.LinkedList;

import algorithms.BipartiteMatching;

public class FloatingSearch {

	private Graph sourceGraph;
	private Graph targetGraph;
	private int[][] originalMatching;
	private CostFunction costFunction;
	private EditDistance editDistance;
	private double[][] originalCostMatrix;
	private BipartiteMatching bipartiteMatching;

	public FloatingSearch(Graph sourceGraph, Graph targetGraph,
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

	public double searchBetter(int iter) {
		double[] distances = new double[this.originalMatching.length+1];
		Arrays.fill(distances, Double.MAX_VALUE);
		
		double d = this.editDistance.getEditDistance(this.sourceGraph,
				this.targetGraph, this.originalMatching, this.costFunction);
		distances[0] = d;
		
//		System.out.println("d[0] = "+distances[0]);
		
		int[][] m = this.originalMatching;
		double[][] cm = this.copyMatrix(this.originalCostMatrix);
		
		LinkedList<DoubleIndex> forbiddenIndices = new LinkedList<DoubleIndex>();
		
		boolean forbidAlreadyForbidden = false;
		int temp = Math.min(iter, this.originalMatching.length);
		
		for (int i = 0; i < temp; i++){
//			System.out.println("########ÊNEW TURN "+i+" ############\n");
//			this.printCostMatrix(cm);
//			System.out.println("*** "+i+" Forbidden are: "+forbiddenIndices);
//			this.printDistances(distances);
			m = this.bipartiteMatching.getMatching(cm);
			this.editDistance.getEditDistance(this.sourceGraph,
			this.targetGraph, m, this.costFunction);
			int forbid = this.editDistance.getMaxCostMatch();
			if (forbid > -1) {
				forbiddenIndices.add(new DoubleIndex(forbid, m[forbid][1]));
//				System.out.println("Forbidden Indices: "+forbiddenIndices);
				if (cm[forbid][m[forbid][1]] == Double.MAX_VALUE){
					forbidAlreadyForbidden = true;
				}
				cm[forbid][m[forbid][1]] = Double.MAX_VALUE;
//				System.out.println("The cost matrix: ");
//				this.printCostMatrix(cm);
//				System.out.println("\n Matching: ");
				m = this.bipartiteMatching.getMatching(cm);
//				this.printMatching(m);
				double distance = this.editDistance.getEditDistance(sourceGraph,
						targetGraph, m, costFunction);
//				System.out.println("\n d = "+distance);
//				if (distances[i+1] > distance){
					distances[i+1] = distance;
//					System.out.println("d["+(i+1)+"] = "+distances[i+1]);
//				}
				boolean improved = true;
				int j = -1;
				while (improved){
					j++;
					improved = false;
					int bestRemove = -1;
					for (int k = 0; k < forbiddenIndices.size(); k++){
						int f = forbiddenIndices.get(k).getFrom();
						int t = forbiddenIndices.get(k).getTo();
//						System.out.println("*** Remove "+f+" --> "+t+" ???");
						cm[f][t] = this.originalCostMatrix[f][t];
//						System.out.println("The cost matrix: ");
//						this.printCostMatrix(cm);
						m = this.bipartiteMatching.getMatching(cm);
//						System.out.println("\n Matching: ");
//						this.printMatching(m);
						distance = this.editDistance.getEditDistance(sourceGraph,
								targetGraph, m, costFunction);
//						System.out.println("\n d = "+distance);
						if (distance < distances[i-j]){
							distances[i-j] = distance;
							bestRemove = k;
							improved = true;
//							System.out.println("*** YES --> Removing");
						} else {
//							System.out.println("*** NO");
						}
						cm[f][t] = Double.MAX_VALUE;
					}
					if (bestRemove != -1){
						
						DoubleIndex removed = forbiddenIndices.remove(bestRemove);
//						System.out.println("Best remove = "+removed);
//						System.out.println("Fobidden Indices: "+forbiddenIndices);
						cm[removed.getFrom()][removed.getTo()] = this.originalCostMatrix[removed.getFrom()][removed.getTo()];
//						this.printCostMatrix(cm);
//						System.out.println();
					}
				}
				i = i-j;
			} else {
				i = this.originalMatching.length;
			}
			if (forbidAlreadyForbidden){
				i = this.originalMatching.length;
			}
		}
		return this.getMinimum(distances);
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
