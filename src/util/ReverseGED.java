package util;


import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

public class ReverseGED {

	
	private Graph sourceGraph;
	private Graph targetGraph;
	private int[][] matching;
	private CostFunction costFunction;
	private EditDistance editDistance;
	private double distance;
	private double[][] costMatrix;
	private int s;
	private LinkedList<Integer> indices;
	private int[][] originalMatching;

	
	public ReverseGED(Graph sourceGraph, Graph targetGraph,
			int[][] matching, CostFunction costFunction,
			EditDistance editDistance, double[][] costMatrix, double factor, double distance, int undirected, int s) {
		this.sourceGraph = sourceGraph;
		this.targetGraph = targetGraph;
		this.matching = matching;
		this.originalMatching = matching;
		this.costFunction = costFunction;
		this.editDistance = editDistance;
		this.distance = distance;
		this.costMatrix = costMatrix;
		this.s = s;
		this.indices = new LinkedList<Integer>();
		for (int i = 0; i < this.matching.length; i++){
			this.indices.add(i);
		}
	}
	
	
	public double computeOtherApprox(){
		// approximation is zero!
		if (this.distance <= 0.){
			return 0;
		}
		// the successors of a node
		LinkedList<TreeNode> successors ;
		
		//	
		// list of edit paths (open) organized as TreeSet
		TreeSet<TreeNode> open = new TreeSet<TreeNode>();



		// each treenode represents a solution (i.e. edit path)
		TreeNode start = new TreeNode(this.sourceGraph, this.targetGraph, this.editDistance, this.costFunction, this.matching, costMatrix);
		open.add(start);

		double bestSolution = start.getCost();

//		System.out.println("START");
//		System.out.println(start);

		// main loop of the search		
		while (!open.isEmpty()){	
//			System.out.print("First: ");
			TreeNode u = open.pollFirst(); 
//			System.out.println(u);
//			System.out.println("My successors:");
			if (u.getDepth() < this.matching.length){
				successors = u.generateReverseSuccessors();	
				bestSolution = this.checkSuccessors(successors, bestSolution);
				open.addAll(successors);	
				while (open.size() > this.s){
					open.pollLast();
				}
				successors.clear();
			}
		}
		return bestSolution;
	}


	private double checkSuccessors(LinkedList<TreeNode> successors,
			double bestSolution) {
		Iterator<TreeNode> iter = successors.iterator();
		while (iter.hasNext()){
			TreeNode tn = iter.next();
//			System.out.println(tn);
			if (tn.getCost() < bestSolution){
				bestSolution = tn.getCost();
			}
		}
		return bestSolution;
		
	}
	
	
	
	
	
	


//	private void printMatching() {
//		System.out.println("Matching");
//		for (int i = 0; i < this.matching.length; i++){
//			System.out.println(this.matching[i][0] +" -> "+this.matching[i][1]);
//		}
//		System.out.println();
//	}


	private void shuffleMatching() {
		Collections.shuffle(this.indices);
		int[][] copyMatching = new int[this.matching.length][2];
		for (int i = 0; i < this.matching.length; i++){
			copyMatching[i][0] = this.originalMatching[this.indices.get(i)][0];
			copyMatching[i][1] = this.originalMatching[this.indices.get(i)][1];
		}
		this.matching = copyMatching;
	}
	
	
}
