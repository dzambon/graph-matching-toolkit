package util;

import java.util.Collections;
import java.util.LinkedList;

import algorithms.BipartiteMatching;

public class Bunke {

	private Graph sourceGraph;
	private Graph targetGraph;
	private int[][] matching;
	private CostFunction costFunction;
	private EditDistance editDistance;
	private double[][] costMatrix;
	private BipartiteMatching bipartiteMatching;
	private MatrixGenerator matrixGenerator;
	private int[][] firstPartOfMatching;
	private int[][] secondPartOfMatching;

	public Bunke(Graph s, Graph t, int[][] m, CostFunction cf, EditDistance ed, double[][] cm, BipartiteMatching bm, MatrixGenerator mg) {
		this.sourceGraph = s;
		this.targetGraph = t;
		this.matching = m;
		this.costFunction = cf;
		this.editDistance = ed;
		this.costMatrix = cm;
		this.bipartiteMatching = bm;
		this.matrixGenerator = mg;
	}

	public double getBetterMatching(double percentage) {
		
		LinkedList<MatchingAndCost> macList = new LinkedList<MatchingAndCost>();
		for (int i = 0; i < this.matching.length;i++){
			int from = this.matching[i][0];
			int to = this.matching[i][1];
			MatchingAndCost mac = new MatchingAndCost(from, to, this.costMatrix[from][to]);
			macList.add(mac);
		}
		Collections.sort(macList);
		int f = (int) (macList.size() * percentage);
		this.firstPartOfMatching = new int[f][2];
		for (int i = 0; i < f; i++){
			MatchingAndCost mac = macList.removeFirst();
			int from =  mac.getFrom();;
			int to =  mac.getTo();
			this.firstPartOfMatching[i][0] = from;
			this.firstPartOfMatching[i][1] = to;
		}
		// debugging vvvvvvvvvvvvvvv
		System.out.println("The first part of the matching:");
		for (int i = 0; i < this.firstPartOfMatching.length; i++){
			System.out.println(this.firstPartOfMatching[i][0]+" --> "+this.firstPartOfMatching[i][1]);
		}		
		// debugging ^^^^^^^^^^^^^^^
		
//		this.costMatrix = this.matrixGenerator.getMatrix(sourceGraph, targetGraph, this.firstPartOfMatching);
//		this.secondPartOfMatching = this.bipartiteMatching.getMatching(costMatrix);
		// calculate the approximated edit-distance according to the bipartite matching 
		// TODO --> merge matchings!!!
		double d = this.editDistance.getEditDistance(sourceGraph,
				targetGraph, matching, costFunction);
		return d;
	}

}
