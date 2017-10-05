/**
 * 
 */
package util;





import java.util.LinkedList;
import java.util.TreeSet;







/**
 * @author riesen
 *
 */
public class EditDistance {
	/**
	 * whether or not the edges are directed
	 */
	private boolean undirected;
	
	/**
	 * whether or not the editpath found is outputted on the console
	 */
	private int outputEditpath;
	
	private int maxCostMatch;
	
	private double[] impliedEdgeCosts;


	
	public double[] getImpliedEdgeCosts() {
		return impliedEdgeCosts;
	}

	/**
	 * constructor
	 * @param undirected
	 * @param outputEditpath
	 * @param bipartiteMatching 
	 * @param matrixGenerator 
	 */
	public EditDistance(int undirected, int outputEditpath) {
		if (undirected == 1){
			this.undirected = true;
		} else {
			this.undirected = false;
		}
		this.outputEditpath = outputEditpath;	
	}

	/**
	 * 
	 * @return the approximated edit distance between graph @param g1
	 * and graph @param g2 according to the @param matching using the cost function @param cf
	 */
	public double getEditDistance(Graph g1, Graph g2, int[][] matching, CostFunction cf) {
		// if the edges are undirected
		// all of the edge operations have to be multiplied by 0.5
		// since all edge operations are performed twice 
		double factor = 1.0;
		if (this.undirected){
			factor = 0.5;
		}
		if (this.outputEditpath ==1){
			System.out.println("\nThe Edit Path:");
		}
		double ed = 0.;
		// the edges of graph g1 and graph g2
		Edge[][] edgesOfG1 = g1.getAdjacenyMatrix();
		Edge[][] edgesOfG2 = g2.getAdjacenyMatrix();
		
		this.impliedEdgeCosts = new double[matching.length];
		
		this.maxCostMatch = -1;
		double maxEdgeCost = 0;
		for (int i = 0; i < matching.length; i++){
			if (matching[i][0] < g1.size()){
				if (matching[i][1] < g2.size()){
					// i-th node substitution with node from g2 with index matching[i][1]
					ed += cf.getCost(g1.get(matching[i][0]), g2.get(matching[i][1]));
					double impliedEdgeCosts = 0;
					if (this.outputEditpath ==1){
						System.out.println(g1.get(matching[i][0]).getNodeID()+"\t-->\t"+ g2.get(matching[i][1]).getNodeID()+"\t"+cf.getCost(g1.get(matching[i][0]), g2.get(matching[i][1])));
					}
					// edge handling when i-th node is substituted with node with index matching[i][1];
					// iterating through all possible edges e_ij of node i
					for (int j = 0; j < matching.length; j++){
						if (j < edgesOfG1[0].length){
							Edge e_ij = edgesOfG1[matching[i][0]][j];
							if (matching[j][1] < g2.size()){
								// node with index j is NOT deleted but substituted
								Edge e_ij_mapped = edgesOfG2[matching[i][1]][matching[j][1]];
								if (e_ij != null){
									if (e_ij_mapped != null){
										// case 1:
										// there is an edge between the i-th and j-th node (e_ij)
										// AND there is an edge between the mappings of the i-th and j-th node (e_ij_mapped)
										ed += factor * cf.getCost(e_ij, e_ij_mapped); // substitute the edge
										impliedEdgeCosts += cf.getCost(e_ij, e_ij_mapped);
										if (this.outputEditpath==1){
											System.out.println(e_ij.getEdgeID() +"\t-->\t"+e_ij_mapped.getEdgeID()+"\t"+factor * cf.getCost(e_ij, e_ij_mapped));
										}
									} else {
										// case 2:
										// there is an edge between the i-th and j-th node (e_ij)
										// BUT there is NO edge between the mappings of the i-th and j-th node (e_ij_mapped=null)
										ed += factor * cf.getEdgeCosts(); // delete the edge
										impliedEdgeCosts += cf.getEdgeCosts();
										if (this.outputEditpath==1){
											System.out.println(e_ij.getEdgeID()+"\t-->\teps\t"+factor * cf.getEdgeCosts());
										}
									}
								} else {
									if (e_ij_mapped != null){
										// case 3:
										// there is NO edge between the i-th and j-th node
										// BUT there is an edge between the mappings
										if (this.outputEditpath==1){
											System.out.println("eps\t-->\t"+e_ij_mapped.getEdgeID()+"\t"+factor * cf.getEdgeCosts());
										}
										ed += factor * cf.getEdgeCosts(); // insert the edge
										impliedEdgeCosts += cf.getEdgeCosts();
									} 
								}
								
							} else {
								// node with index j is deleted
								if (e_ij != null){
									// case 4:
									// there is an edge between the i-th and j-th node and the j-th node is deleted
									ed += factor * cf.getEdgeCosts(); // delete the edge
									impliedEdgeCosts += cf.getEdgeCosts();
									if (this.outputEditpath==1){
										System.out.println(e_ij.getEdgeID()+"\t-->\teps\t"+factor * cf.getEdgeCosts());
									}
								}
							}
						} else {
							// node with index j is inserted
							if (matching[j][1] < g2.size()){
								Edge e_ij_mapped = edgesOfG2[matching[i][1]][matching[j][1]];
								if (e_ij_mapped != null){
									// case 5:
									// there is an edge between the mappings of i and j
									if (this.outputEditpath==1){
										System.out.println("eps\t-->\t"+e_ij_mapped.getEdgeID()+"\t"+factor * cf.getEdgeCosts());
									}
									ed += factor * cf.getEdgeCosts(); // insert the edge
									impliedEdgeCosts += cf.getEdgeCosts();
								} 
							}
						}
					}
					this.impliedEdgeCosts[i] = impliedEdgeCosts;
					if (impliedEdgeCosts > maxEdgeCost){
						maxEdgeCost = impliedEdgeCosts;
						this.maxCostMatch = i;
					}
				} else{
					// i-th node deletion
					ed += cf.getNodeCosts();
					if (this.outputEditpath==1){
						System.out.println(g1.get(matching[i][0]).getNodeID()+"\t-->\t"+ "eps\t"+cf.getNodeCosts());
					}
					double impliedEdgeCosts = 0;
					// edge handling
					for (int j = 0; j < edgesOfG1[0].length; j++){
						Edge e_ij = edgesOfG1[matching[i][0]][j];
						if (e_ij != null){
							// case 6:
							// there is an edge between the i-th and j-th node
							ed += factor * cf.getEdgeCosts(); // delete the edge
							impliedEdgeCosts += cf.getEdgeCosts();
							if (this.outputEditpath==1){
								System.out.println(e_ij.getEdgeID()+"\t-->\teps\t"+ factor *cf.getEdgeCosts());
							}	
						}
					}
					this.impliedEdgeCosts[i] = impliedEdgeCosts;
					if (impliedEdgeCosts > maxEdgeCost){
						maxEdgeCost = impliedEdgeCosts;
						this.maxCostMatch = i;
					}
				}
			} else{
				if (matching[i][1] < g2.size()){
					// i-th node insertion
					ed += cf.getNodeCosts();
					if (this.outputEditpath==1){
						System.out.println("eps\t-->\t"+ g2.get(matching[i][1]).getNodeID()+"\t"+cf.getNodeCosts());
					}
					double impliedEdgeCosts = 0;
					// edge handling
					for (int j = 0; j < edgesOfG2[0].length; j++){
						Edge e_ij_mapped = edgesOfG2[matching[i][1]][j];
						if (e_ij_mapped != null){
							// case 7:
							// there is an edge between the mapping of the i-th and j-th node
							ed += factor * cf.getEdgeCosts(); // insert the edge
							impliedEdgeCosts += cf.getEdgeCosts();
							if (this.outputEditpath==1){
								System.out.println("eps\t-->\t"+e_ij_mapped.getEdgeID()+"\t"+ factor *cf.getEdgeCosts());
							}
						}
					}
					this.impliedEdgeCosts[i] = impliedEdgeCosts;
					if (impliedEdgeCosts > maxEdgeCost){
						maxEdgeCost = impliedEdgeCosts;
						this.maxCostMatch = i;
					}
				}
			}
		}
		
		if (this.outputEditpath==1){
			System.out.println("The Edit Distance: "+ed);
		}
		return ed;
	}
	
	/**
	 * 
	 * @return the exact edit distance between graph @param g1
	 * and graph @param g2 using the cost function @param cf
	 * s is the maximum number of open paths used in beam-search
	 */
	public double getEditDistance(Graph g1, Graph g2, CostFunction cf, int s, double bound) {
		if (bound <= 0.){
			return 0;
		}
		// list of partial edit paths (open) organized as TreeSet
		TreeSet<TreeNode> open = new TreeSet<TreeNode>();
	
		

		// if the edges are undirected
		// all of the edge operations have to be multiplied by 0.5
		// since all edge operations are performed twice 
		double factor = 1.0;
		if (this.undirected){
			factor = 0.5;
		}
				
		// each treenode represents a (partial) solution (i.e. edit path)
		// start is the first (empty) partial solution
		TreeNode start = new TreeNode(g1, g2, cf, factor);
		open.add(start);
		
		// time out handler
		long MAX_TIME = 30000;
		long sTime = System.currentTimeMillis();
		long eTime = sTime+1;
		
		// the successors of a node
		LinkedList<TreeNode> successors ;
		
		// main loop of the tree search
		while (!open.isEmpty()){	
//			System.out.println("*** OPEN: \n"+open);
			eTime = System.currentTimeMillis();
			if (eTime-sTime > MAX_TIME){
				return -1; // max time has elapsed
			}
			TreeNode u = open.pollFirst(); 
			if (u.allNodesUsed()){
				//u.printMatching();
				return u.getCost();
			}
			// generates all successors of node u in the search tree
			// and add them to open
			successors = u.generateSuccessors(bound);
			open.addAll(successors);
			successors.clear();
			// in beam search the maximum number of open paths
			// is limited to s
			while (open.size() > s){
				open.pollLast();
			}		
		}
		// error case 
		System.out.println("***ERROR CASE: Edit Distance is corrupt");
		System.exit(0);
		return -1;
	}

	public int getMaxCostMatch() {
	
		return this.maxCostMatch;
	}


	
	/**
	 * 
	 * @return an pproximate edit distance between graph @param g1
	 * and graph @param g2 using the cost function @param cf
	 * s is the maximum number of open paths used in beam-search
	 */
	public double getEditDistance(Graph g1, Graph g2, CostFunction cf, TreeNode start) {
		
		// list of partial edit paths (open) organized as TreeSet
		TreeSet<TreeNode> open = new TreeSet<TreeNode>();
		open.add(start);
		
		// time out handler
		long MAX_TIME = 30000;
		long sTime = System.currentTimeMillis();
		long eTime = sTime+1;
		
		// the successors of a node
		LinkedList<TreeNode> successors ;
		
		// main loop of the tree search
		while (!open.isEmpty()){	
//			System.out.println("*** OPEN: \n"+open);
			eTime = System.currentTimeMillis();
			if (eTime-sTime > MAX_TIME){
				return -1; // max time has elapsed
			}
			TreeNode u = open.pollFirst(); 
			if (u.allNodesUsed()){
				//u.printMatching();
				return u.getCost();
			}
			// generates all successors of node u in the search tree
			// and add them to open
			successors = u.generateSuccessors(Double.MAX_VALUE);
			open.addAll(successors);
			successors.clear();
		}
		// error case 
		System.out.println("***ERROR CASE: Edit Distance is corrupt");
		System.exit(0);
		return -1;
	}
	
	


}
