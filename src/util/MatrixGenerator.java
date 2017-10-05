/**
 * 
 */
package util;



import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Locale;

import algorithms.HungarianAlgorithm;


/**
 * @author riesen
 * 
 */
public class MatrixGenerator {

	/**
	 * the cource and target graph whereon the cost matrix is built
	 */
	private Graph source, target;
	
	/**
	 * the cost function actually employed
	 */
	private CostFunction cf;

	/**
	 * the matching algorithm for recursive
	 * edge matchings (hungarian is used in any case!)
	 */
	private HungarianAlgorithm ha;

	/**
	 * whether or not the cost matrix is logged on the console
	 */
	private int outputCostMatrix;

	/**
	 * the decimal format for the distances found
	 */
	private DecimalFormat decFormat;

	/**
	 * whether or not adjacent edges are considered
	 */
	private String adj;

	private double beta;

	private LinkedList<Edge> edges1;

	private LinkedList<Edge> edges2;
	

	/**
	 * constructs a MatrixGenerator
	 * @param costFunction
	 * @param outputCostMatrix
	 */
	public MatrixGenerator(CostFunction costFunction, int outputCostMatrix) {
		this.cf = costFunction;
		this.ha = new HungarianAlgorithm();
		this.outputCostMatrix = outputCostMatrix;
		this.decFormat = (DecimalFormat) NumberFormat
				.getInstance(Locale.ENGLISH);
		this.decFormat.applyPattern("0.00000");
	}





	/**
	 * @param centrality 
	 * @return the cost matrix for two graphs @param sourceGraph and @param targetGraph
	 * |         |
	 * | c_i,j   | del
	 * |_________|______
	 * |         |
	 * |  ins    |	0
	 * |         |
	 * 
	 */
	public double[][] getMatrix(Graph sourceGraph, Graph targetGraph) {
		this.source = sourceGraph;
		this.target = targetGraph;

		
		int sSize = sourceGraph.size();
		int tSize = targetGraph.size();
		int dim = sSize + tSize;
		double[][] matrix = new double[dim][dim];
		double[][] edgeMatrix;
		Node u;
		Node v;

		for (int i = 0; i < sSize; i++) {
			u = (Node) this.source.get(i);
			for (int j = 0; j < tSize; j++) {
				v = (Node) this.target.get(j);
				double costs = cf.getCost(u, v);
				if (this.adj.equals("worst")){
					costs += u.getEdges().size()*cf.getEdgeCosts();
					costs += v.getEdges().size()*cf.getEdgeCosts();
				}
				if (this.adj.equals("best")){
					// adjacency information is added to the node costs
					edgeMatrix = this.getEdgeMatrix(u, v);
					costs += this.ha.hgAlgorithmOnlyCost(edgeMatrix);
				}
				
				matrix[i][j] = costs;
			}
		}
		for (int i = sSize; i < dim; i++) {
			for (int j = 0; j < tSize; j++) {
				if ((i - sSize) == j) {
					v = (Node) this.target.get(j);
					double costs = cf.getNodeCosts();
					if (this.adj.equals("worst") || this.adj.equals("best")){
						double f = v.getEdges().size();
						costs += (f * cf.getEdgeCosts());
					}
					matrix[i][j] = costs;
				} else {
					matrix[i][j] = Double.POSITIVE_INFINITY;
				}
			}
		}
		for (int i = 0; i < sSize; i++) {
			u = (Node) this.source.get(i);
			for (int j = tSize; j < dim; j++) {
				if ((j - tSize) == i) {
					double costs = cf.getNodeCosts();;
					if (this.adj.equals("worst") || this.adj.equals("best")){
						double f = u.getEdges().size();
						costs += (f * cf.getEdgeCosts());
					}
					matrix[i][j] = costs;
				} else {
					matrix[i][j] = Double.POSITIVE_INFINITY;
				}
			}
		}
		for (int i = sSize; i < dim; i++) {
			for (int j = tSize; j < dim; j++) {
				matrix[i][j] =0.0;
			}
		}
		if (this.outputCostMatrix==1){
			System.out.println("\nThe Cost Matrix:");
			for (int k = 0; k < matrix.length; k++){
				for (int l = 0; l < matrix[0].length; l++){
					if (matrix[k][l] < Double.POSITIVE_INFINITY){
						System.out.print(decFormat.format(matrix[k][l])+"\t");
						
					} else {
						System.out.print("infty\t");
					}
					
				}
				System.out.println();
			}
		}
		return matrix;
	}
	
	
	public double[][] getPureStructuralMatrix(Graph sourceGraph, Graph targetGraph) {
		this.source = sourceGraph;
		this.target = targetGraph;

		int sSize = sourceGraph.size();
		int tSize = targetGraph.size();
		int dim = sSize + tSize;
		double[][] matrix = new double[dim][dim];
		double[][] edgeMatrix;
		Node u;
		Node v;

		for (int i = 0; i < sSize; i++) {
			u = (Node) this.source.get(i);
			for (int j = 0; j < tSize; j++) {
				v = (Node) this.target.get(j);
				double costs = 0;
				if (this.adj.equals("worst")){
					costs += u.getEdges().size()*cf.getEdgeCosts();
					costs += v.getEdges().size()*cf.getEdgeCosts();
				}
				if (this.adj.equals("best")){
					// adjacency information is added to the node costs
					edgeMatrix = this.getEdgeMatrix(u, v);
					costs += this.ha.hgAlgorithmOnlyCost(edgeMatrix);
				}
				
				matrix[i][j] = costs;
			}
		}
		for (int i = sSize; i < dim; i++) {
			for (int j = 0; j < tSize; j++) {
				if ((i - sSize) == j) {
					v = (Node) this.target.get(j);
					double costs = 0;
					if (this.adj.equals("worst") || this.adj.equals("best")){
						double f = v.getEdges().size();
						costs += (f * cf.getEdgeCosts());
					}
					matrix[i][j] = costs;
				} else {
					matrix[i][j] = Double.POSITIVE_INFINITY;
				}
			}
		}
		for (int i = 0; i < sSize; i++) {
			u = (Node) this.source.get(i);
			for (int j = tSize; j < dim; j++) {
				if ((j - tSize) == i) {
					double costs = 0;
					if (this.adj.equals("worst") || this.adj.equals("best")){
						double f = u.getEdges().size();
						costs += (f * cf.getEdgeCosts());
					}
					matrix[i][j] = costs;
				} else {
					matrix[i][j] = Double.POSITIVE_INFINITY;
				}
			}
		}
		for (int i = sSize; i < dim; i++) {
			for (int j = tSize; j < dim; j++) {
				matrix[i][j] = 0.0;
			}
		}
		return matrix;
	}






	/**
	 * @return the cost matrix for the edge operations 
	 * between the nodes @param u
	 * @param v
	 */
	private double[][] getEdgeMatrix(Node u, Node v) {
		int uSize = u.getEdges().size();
		int vSize = v.getEdges().size();
		int dim = uSize + vSize;
		double[][] edgeMatrix = new double[dim][dim];
		Edge e_u;
		Edge e_v;
		for (int i = 0; i < uSize; i++) {
			e_u = (Edge) u.getEdges().get(i);
			for (int j = 0; j < vSize; j++) {
				e_v = (Edge) v.getEdges().get(j);
				double costs = cf.getCost(e_u, e_v);
				edgeMatrix[i][j] = costs;
			}
		}
		for (int i = uSize; i < dim; i++) {
			for (int j = 0; j < vSize; j++) {
				// diagonal
				if ((i - uSize) == j) {
					double costs = cf.getEdgeCosts();
					edgeMatrix[i][j] = costs;
				} else {
					edgeMatrix[i][j] = Double.POSITIVE_INFINITY;
				}
			}
		}
		for (int i = 0; i < uSize; i++) {
			for (int j = vSize; j < dim; j++) {
				// diagonal
				if ((j - vSize) == i) {
					double costs = cf.getEdgeCosts();
					edgeMatrix[i][j] = costs;
				} else {
					edgeMatrix[i][j] = Double.POSITIVE_INFINITY;
				}
			}
		}
		
		return edgeMatrix;
	}





	public void setAdj(String adj) {
		this.adj = adj;
		
	}





	public void setBeta(double beta) {
		this.beta = beta;
		
	}





	




	





	private int getMap(int[][] m, int k) {
		for (int i = 0; i < m.length; i++){
			if (m[i][0] == k ){
				return m[i][1];
			}
		}
		return -7;
	}





	private boolean matchingContains(int[][] m, int from, int to) {
		for (int i = 0; i < m.length; i++){
			if (m[i][0] == from && m[i][1] == to){
				return true;
			}
		}
		return false;
	}





	public double[][] getMatrix(Graph sourceGraph, Graph targetGraph,
			int[][] structMatching, double[][] originalCM) {
		this.source = sourceGraph;
		this.target = targetGraph;

		
		int sSize = sourceGraph.size();
		int tSize = targetGraph.size();
		int dim = sSize + tSize;
		double[][] matrix = new double[dim][dim];
		for (int i = 0; i < dim; i++){
			for (int j = 0; j < dim; j++){
				matrix[i][j] = originalCM[i][j]*10000.0;
			}
		}
		
		int[] usedOfG1 = new int[sSize];
		int[] usedOfG2 = new int[tSize];
		for (int i = 0; i < structMatching.length; i++){
			if (structMatching[i][0] < this.edges1.size()){
				Edge e1 = this.edges1.get(structMatching[i][0]);
				if (structMatching[i][1] < this.edges2.size()){
					Edge e2 = this.edges2.get(structMatching[i][1]);
					Node u1 = e1.getStartNode();
					Node u2 = e1.getEndNode();
					Node v1 = e2.getStartNode();
					Node v2 = e2.getEndNode();
					int u1Index = sourceGraph.indexOf(u1);
					int u2Index = sourceGraph.indexOf(u2);
					int v1Index = targetGraph.indexOf(v1);
					int v2Index = targetGraph.indexOf(v2);
					matrix[u1Index][v1Index] = this.cf.getCost(u1, v1);
					matrix[u2Index][v2Index] = this.cf.getCost(u2, v2);
					matrix[u1Index][v2Index] = this.cf.getCost(u1, v2);
					matrix[u2Index][v1Index] = this.cf.getCost(u2, v1);
					usedOfG1[u1Index]=1;
					usedOfG1[u2Index]=1;
					usedOfG2[v1Index]=1;
					usedOfG2[v2Index]=1;
				} else {
					Node u1 = e1.getStartNode();
					Node u2 = e1.getEndNode();
					int u1Index = sourceGraph.indexOf(u1);
					int u2Index = sourceGraph.indexOf(u2);
					usedOfG1[u1Index]=1;
					usedOfG1[u2Index]=1;
					matrix[u1Index][tSize+u1Index] = this.cf.getNodeCosts();
					matrix[u2Index][tSize+u2Index] = this.cf.getNodeCosts();
				}
			} else {
				if (structMatching[i][1] < this.edges2.size()){
					Edge e2 = this.edges2.get(structMatching[i][1]);
					Node v1 = e2.getStartNode();
					Node v2 = e2.getEndNode();
					int v1Index = targetGraph.indexOf(v1);
					int v2Index = targetGraph.indexOf(v2);
					usedOfG2[v1Index]=1;
					usedOfG2[v2Index]=1;
					matrix[sSize+v1Index][v1Index] = this.cf.getNodeCosts();
					matrix[sSize+v2Index][v2Index] = this.cf.getNodeCosts();
				}
			}	
		}
		// handle isolated nodes!
		for (int i = 0; i < usedOfG1.length; i++){
			if (usedOfG1[i]==0){
				for (int j = 0; j < tSize; j++){
					matrix[i][j] = this.cf.getCost(sourceGraph.get(i), targetGraph.get(j));
					matrix[i][tSize+i] = this.cf.getNodeCosts();
				}
			}
		}
		for (int j = 0; j < usedOfG2.length; j++){
			if (usedOfG2[j]==0){
				for (int i = 0; i < sSize; i++){
					matrix[i][j] = this.cf.getCost(sourceGraph.get(i), targetGraph.get(j));
					matrix[sSize+j][j] = this.cf.getNodeCosts();
				}
			}
		}
		
		if (sSize != tSize && this.edges1.size()==this.edges2.size()){
			for (int i = 0; i < sSize; i++){
				matrix[i][tSize+i] = this.cf.getNodeCosts();
			}
		}
		

		if (this.outputCostMatrix==1){
			System.out.println("\nThe Cost Matrix:");
			for (int k = 0; k < matrix.length; k++){
				for (int l = 0; l < matrix[0].length; l++){
					if (matrix[k][l] < Double.POSITIVE_INFINITY){
						System.out.print(decFormat.format(matrix[k][l])+"\t");
						
					} else {
						System.out.print("infty\t");
					}
					
				}
				System.out.println();
			}
		}
		return matrix;
		
	}





	public double[][] getSimpleMatrix(Graph sourceGraph, Graph targetGraph) {
		this.source = sourceGraph;
		this.target = targetGraph;

		
		int sSize = sourceGraph.size();
		int tSize = targetGraph.size();
		int dim = sSize + tSize;
		double[][] matrix = new double[dim][dim];
		double[][] edgeMatrix;
		Node u;
		Node v;

		for (int i = 0; i < sSize; i++) {
			u = (Node) this.source.get(i);
			for (int j = 0; j < tSize; j++) {
				v = (Node) this.target.get(j);
				double costs = cf.getCost(u, v);
				if (this.adj.equals("worst")){
					costs += u.getEdges().size()*cf.getEdgeCosts();
					costs += v.getEdges().size()*cf.getEdgeCosts();
				}
				if (this.adj.equals("best")){
					// adjacency information is added to the node costs
					edgeMatrix = this.getEdgeMatrix(u, v);
					costs += this.ha.hgAlgorithmOnlyCost(edgeMatrix);
				}
				
				matrix[i][j] = costs;
			}
		}
		for (int i = sSize; i < dim; i++) {
			for (int j = 0; j < tSize; j++) {
				
					v = (Node) this.target.get(j);
					double costs = cf.getNodeCosts();
					if (this.adj.equals("worst") || this.adj.equals("best")){
						double f = v.getEdges().size();
						costs += (f * cf.getEdgeCosts());
					}
					matrix[i][j] = costs;
				
			}
		}
		for (int i = 0; i < sSize; i++) {
			u = (Node) this.source.get(i);
			for (int j = tSize; j < dim; j++) {
			
					double costs = cf.getNodeCosts();;
					if (this.adj.equals("worst") || this.adj.equals("best")){
						double f = u.getEdges().size();
						costs += (f * cf.getEdgeCosts());
					}
					matrix[i][j] = costs;
				
			}
		}
		for (int i = sSize; i < dim; i++) {
			for (int j = tSize; j < dim; j++) {
				matrix[i][j] =0.0;
			}
		}
		
		return matrix;
	}





	public double[][] getMoreAdjacencyMatrix(Graph g1, Graph g2) {
		Edge[][] a1 = g1.getAdjacenyMatrix();
		Edge[][] a2 = g2.getAdjacenyMatrix();
		this.edges1 = new LinkedList<Edge>();
		this.edges2 = new LinkedList<Edge>();
		for (int i = 0; i < a1.length-1; i++){
			for (int j = i+1; j < a1.length;j++){
				if (a1[i][j]!=null){
					edges1.add(a1[i][j]);
				}
			}
		}
		for (int i = 0; i < a2.length-1; i++){
			for (int j = i+1; j < a2.length;j++){
				if (a2[i][j]!=null){
					edges2.add(a2[i][j]);
				}
			}
		}
		int sSize = edges1.size();
		int tSize = edges2.size();
		int dim = sSize + tSize;
		double[][] matrix = new double[dim][dim];
		double[][] edgeMatrix;
		
		
		Edge e1;
		Edge e2;
		Node u1, u2, v1,v2;

		for (int i = 0; i < sSize; i++) {
			e1 = (Edge) edges1.get(i);
			for (int j = 0; j < tSize; j++) {
				e2 = (Edge) edges2.get(j);
				double costs = 0;
				double alternativeCosts = 0;
				u1 = e1.getStartNode();
				u2 = e1.getEndNode();
				v1 = e2.getStartNode();
				v2 = e2.getEndNode();
				costs += this.cf.getCost(u1, v1);
				costs += this.cf.getCost(u2, v2);
				edgeMatrix = this.getEdgeMatrix(u1, v1);
				costs += this.ha.hgAlgorithmOnlyCost(edgeMatrix);
				edgeMatrix = this.getEdgeMatrix(u2, v2);
				costs += this.ha.hgAlgorithmOnlyCost(edgeMatrix);
				alternativeCosts += this.cf.getCost(u1, v2);
				alternativeCosts += this.cf.getCost(u2, v1);
				edgeMatrix = this.getEdgeMatrix(u1, v2);
				alternativeCosts += this.ha.hgAlgorithmOnlyCost(edgeMatrix);
				edgeMatrix = this.getEdgeMatrix(u2, v1);
				alternativeCosts += this.ha.hgAlgorithmOnlyCost(edgeMatrix);
				costs = Math.min(costs, alternativeCosts);
				costs+= this.cf.getCost(e1, e2);	
				matrix[i][j] = costs;
			}		
		}
		for (int i = sSize; i < dim; i++) {
			for (int j = 0; j < tSize; j++) {
				if ((i - sSize) == j) {
					e2 = (Edge) edges2.get(j);
					v1 = e2.getStartNode();
					v2 = e2.getEndNode();
					double costs = 2 * cf.getNodeCosts();
					costs += cf.getEdgeCosts();
					costs += (cf.getEdgeCosts() * v1.getEdges().size());
					costs += (cf.getEdgeCosts() * v2.getEdges().size());
					matrix[i][j] = costs;
				} else {
					matrix[i][j] = Double.POSITIVE_INFINITY;
				}
			}
		}
		for (int i = 0; i < sSize; i++) {
			for (int j = tSize; j < dim; j++) {
				if ((j - tSize) == i) {
					e1 = (Edge) edges1.get(i);
					u1 = e1.getStartNode();
					u2 = e1.getEndNode();
					double costs = 2 * cf.getNodeCosts();
					costs += cf.getEdgeCosts();
					costs += (cf.getEdgeCosts() * u1.getEdges().size());
					costs += (cf.getEdgeCosts() * u2.getEdges().size());
					matrix[i][j] = costs;
				} else {
					matrix[i][j] = Double.POSITIVE_INFINITY;
				}
			}
		}
		return matrix;		
	}
}
