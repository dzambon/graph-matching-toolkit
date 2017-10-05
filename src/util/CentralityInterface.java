package util;


import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.algorithms.scoring.*;
import edu.uci.ics.jung.algorithms.shortestpath.*;

/**
 * This class encapsulates the CentraliyMeasure classes of the JUNG graph 
 * algorithm library. 
 *
*/
public class CentralityInterface {


	    
	    /**
	     * Calculates the specified centrality rank score for all nodes of a given 
	     * graph and sets the score in the node class.
	     * 
	     * @param graph
	     */
	public static void rankNodes(Graph graph, String cMeasure){
		// set up graph		
		EdgeType edgeType = graph.isDirected() ? EdgeType.DIRECTED : EdgeType.UNDIRECTED;
		SparseMultigraph<Node, Edge> jungGraph = new SparseMultigraph<Node, Edge>();
		for (Node node : graph) {
			jungGraph.addVertex(node);
		}

		Edge[][] adjacenyMatrix = graph.getAdjacenyMatrix();
		for (int i = 0; i < adjacenyMatrix.length; i++) {
			Edge[] edges = adjacenyMatrix[i];
			for (int j = 0; j < edges.length; j++) {
				Edge edge = edges[j];
				if (edge != null){
					jungGraph.addEdge(edge, edge.getStartNode(), edge.getEndNode(), edgeType);
				}
			}
		}
		if (cMeasure.equals("Barycenter")){
			BarycenterScorer<Node, Edge> centrality = new BarycenterScorer<Node, Edge>(jungGraph);
			for (Node node : graph) {
				double score = (Double) centrality.getVertexScore(node);
				node.setCentrality(score);
			}
			return;
		}
		if (cMeasure.equals("Betweenness")){
			BetweennessCentrality<Node, Edge> centrality = new BetweennessCentrality<Node, Edge>(jungGraph);
			for (Node node : graph) {
				double score = (Double) centrality.getVertexScore(node);
				node.setCentrality(score);
			}
			return;
		}
		if (cMeasure.equals("Closeness")){
			ClosenessCentrality<Node, Edge> centrality = new ClosenessCentrality<Node, Edge>(jungGraph);
			for (Node node : graph) {
				double score = (Double) centrality.getVertexScore(node);
				node.setCentrality(score);
			}
			return;
		}
		if (cMeasure.equals("Degree")){
			DegreeScorer<Node> centrality = new DegreeScorer<Node>(jungGraph);
			for (Node node : graph) {
				double score = (Integer) centrality.getVertexScore(node);
				node.setCentrality(score);
			}
			return;
		}
		if (cMeasure.equals("Eigenvector")){
			EigenvectorCentrality<Node, Edge> centrality = new EigenvectorCentrality<Node, Edge>(jungGraph);
			for (Node node : graph) {
				double score = (Double) centrality.getVertexScore(node);
				node.setCentrality(score);
			}
			return;
		}
		if (cMeasure.equals("HITS")){
			HITS<Node, Edge> hits = new HITS<Node, Edge>(jungGraph);
			hits.evaluate();
			for (Node node : graph) {
				HITS.Scores score =  hits.getVertexScore(node);
				node.setCentrality(score.hub);
				node.setCentrality(score.authority);
			}
		}
		if (cMeasure.equals("Pagerank")){
			PageRank<Node, Edge> pageRankCentrality = new PageRank<Node, Edge>(jungGraph, 0.1);
			pageRankCentrality.evaluate();
			for (Node node : graph) {
				double score = (Double) pageRankCentrality.getVertexScore(node);
				node.setCentrality(score);
			}
			return;
		}
		System.out.println("ERROR NO VALID CENTRALITY MEASURE...");
		System.exit(0);
		
	}

	  
}
