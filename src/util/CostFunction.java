/**
 * 
 */
package util;

/**
 * @author riesen
 * 
 */
public class CostFunction {

	/**
	 * the constant cost for node and edge deletions/insertions
	 */
	private double nodeCost;
	private double edgeCost;

	/**
	 * the weighting factor alpha measures the relative importance of node and
	 * edge operations
	 */
	private double alpha;

	/**
	 * all identifiers (names) of node and edge attributes
	 */
	private String[] nodeAttributes;
	private String[] edgeAttributes;
	
	/**
	 * all types of node and edge cost functions
	 */
	private String[] nodeCostTypes;
	private String[] edgeCostTypes;
	
	/**
	 * the weighting factors for the node and edge attributes
	 */
	private double[] nodeAttrImportance;
	private double[] edgeAttrImportance;

	/**
	 * combined costs are "p-rooted"
	 */
	private double pNode;
	private double pEdge;

	/**
	 * whether the individual costs are multiplied (=1) instead of added
	 */
	private int multiplyNodeCosts;
	private int multiplyEdgeCosts;
	
	/**
	 * mu and nu are non-negative real values to be defined by the user if cost-function=discrete
	 * mu = cost for substitution of equal labels
	 * nu = cost for substitution of unequal labels
	 */
	private double[] nodeCostMu;
	private double[] nodeCostNu;
	private double[] edgeCostMu;
	private double[] edgeCostNu;

	/**
	 * initializes the costfunction according to the properties read in the properties file
	 * @param edgeCostNu 
	 * @param edgeCostMu 
	 */
	public CostFunction(double n, double e, double a, String[] nodeAttributes,
			String[] nodeCostTypes, double[] nodeAttrImportance,
			String[] edgeAttributes, String[] edgeCostTypes,
			double[] edgeAttrImportance, double pNode,
			int multiplyNodeCosts, double pEdge,
			int multiplyEdgeCosts, double[] nodeCostMu, double[] nodeCostNu, double[] edgeCostMu, double[] edgeCostNu) {
		this.nodeCost = n;
		this.edgeCost = e;
		this.alpha = a;
		this.nodeAttributes = nodeAttributes;
		this.nodeCostTypes = nodeCostTypes;
		this.nodeAttrImportance = nodeAttrImportance;
		this.edgeAttributes = edgeAttributes;
		this.edgeCostTypes = edgeCostTypes;
		this.edgeAttrImportance = edgeAttrImportance;
		this.pNode = pNode;
		this.multiplyNodeCosts = multiplyNodeCosts;
		this.pEdge = pEdge;
		this.multiplyEdgeCosts = multiplyEdgeCosts;
		this.nodeCostMu = nodeCostMu;
		this.nodeCostNu = nodeCostNu;
		this.edgeCostMu = edgeCostMu;
		this.edgeCostNu = edgeCostNu;
	}

	/**
	 * @return the substitution cost between node @param u
	 * and node @param v according to their attribute values and the cost functions.
	 * The individual costs are softened by the importance factors and finally
	 * added or multiplied (and possibly the result is "square rooted")
	 * The final result is multiplied by alpha
	 */
	public double getCost(Node u, Node v) {
		double cost = 0;
		for (int i = 0; i < this.nodeAttributes.length; i++) {

			boolean costTypeFound = false;
			
			if (this.nodeCostTypes[i].equals("coil")) {
				double u_x = Double.parseDouble(u
						.getValue("histo"+(i+1)));
				double v_x = Double.parseDouble(v
						.getValue("histo"+(i+1)));
				if (this.multiplyNodeCosts == 1) {
					cost *= Math.abs((u_x - v_x));
				} else {
					cost += (Math.abs((u_x - v_x)));
				}
				costTypeFound = true;
			}
			if (this.nodeCostTypes[i].equals("squared")) {
				double u_x = Double.parseDouble(u
						.getValue(this.nodeAttributes[i]));
				double v_x = Double.parseDouble(v
						.getValue(this.nodeAttributes[i]));
				if (this.multiplyNodeCosts == 1) {
					cost *= Math.pow((u_x - v_x), 2.)
							* this.nodeAttrImportance[i];
				} else {
					cost += (Math.pow((u_x - v_x), 2.)
							* this.nodeAttrImportance[i]);
				}
				costTypeFound = true;
			}
			if (this.nodeCostTypes[i].equals("absolute")) {
				double u_x = Double.parseDouble(u
						.getValue(this.nodeAttributes[i]));
				double v_x = Double.parseDouble(v
						.getValue(this.nodeAttributes[i]));
				if (this.multiplyNodeCosts == 1) {
					cost *= Math.abs((u_x - v_x)) * this.nodeAttrImportance[i];
				} else {
					cost += (Math.abs((u_x - v_x)) * this.nodeAttrImportance[i]);
				}

				costTypeFound = true;
			}
			if (this.nodeCostTypes[i].equals("discrete")) {
				String u_x = u.getValue(this.nodeAttributes[i]);
				String v_x = v.getValue(this.nodeAttributes[i]);
				if (u_x.equals(v_x)) {
					if (this.multiplyNodeCosts == 1) {
						cost *= this.nodeCostMu[i]* this.nodeAttrImportance[i];
					} else {
						cost += (this.nodeCostMu[i]* this.nodeAttrImportance[i]);
					}

				} else {
					if (this.multiplyNodeCosts == 1) {
						cost *= this.nodeCostNu[i] * this.nodeAttrImportance[i];
					} else {
						cost += (this.nodeCostNu[i] * this.nodeAttrImportance[i]);
					}
				}
				costTypeFound = true;
			}
			if (this.nodeCostTypes[i].equals("discreteGREC")) {
				String u_x = u.getValue(this.nodeAttributes[i]);
				String v_x = v.getValue(this.nodeAttributes[i]);
				if (!u_x.equals(v_x)) {
					cost = Math.pow(2*this.nodeCost, this.pNode); // replace euclidean distance by (2*nodeCosts)^2
				} 
				costTypeFound = true;
			}
			
			if (this.nodeCostTypes[i].equals("sed")) {
				String u_x = u.getValue(this.nodeAttributes[i]);
				String v_x = v.getValue(this.nodeAttributes[i]);
				if (this.multiplyNodeCosts == 1) {
					cost *= this.stringEditDistance(u_x, v_x)
							* this.nodeAttrImportance[i];
				} else {
					cost += (this.stringEditDistance(u_x, v_x)
							* this.nodeAttrImportance[i]);
				}
				costTypeFound = true;
			}
			
			//dz: coma separated double values 
			if (this.nodeCostTypes[i].equals("csvDouble")) {
				double[] u_x= dzParseCSVDouble(u
						.getValue(this.nodeAttributes[i]));
				double[] v_x= dzParseCSVDouble(v
						.getValue(this.nodeAttributes[i]));
				double sum = 0;
				for (int s = 0; s < u_x.length; s++) {
				    sum += Math.pow((u_x[s] - v_x[s]), 2.);   
				}
				if (this.multiplyNodeCosts == 1) {
					cost *= sum * this.nodeAttrImportance[i];
				} else {
					cost += sum * this.nodeAttrImportance[i];
				}
				costTypeFound = true;
			}
			
			if(!costTypeFound)
				throw new IllegalArgumentException("cost type <"+this.nodeCostTypes[i]+"> not found.");
			

		}

		cost = Math.pow(cost, (1/this.pNode));
		cost *= this.alpha;
		return cost;
	}

	

	/**
	 * @return the substitution cost between edge @param u
	 * and edge @param v according to their attribute values and the cost functions.
	 * The individual costs are softened by the importance factors and finally
	 * added or multiplied (and possibly the result is "square rooted")
	 * The final result is multiplied by (1-alpha)
	 */
	public double getCost(Edge u, Edge v) {
		double cost = 0;
		for (int i = 0; i < this.edgeAttributes.length; i++) {
			
			
			boolean costTypeFound = false;
			
			if (this.edgeCostTypes[i].equals("dummy")) {
				// nothing to do
			}
			if (this.edgeCostTypes[i].equals("squared")) {
				double u_x = Double.parseDouble(u
						.getValue(this.edgeAttributes[i]));
				double v_x = Double.parseDouble(v
						.getValue(this.edgeAttributes[i]));
				if (this.multiplyEdgeCosts == 1) {
					cost *= Math.pow((u_x - v_x), 2.)
							* this.edgeAttrImportance[i];
				} else {
					cost += Math.pow((u_x - v_x), 2.)
							* this.edgeAttrImportance[i];
				}
				costTypeFound = true;
			}
			if (this.edgeCostTypes[i].equals("absolute")) {
				double u_x = Double.parseDouble(u
						.getValue(this.edgeAttributes[i]));
				double v_x = Double.parseDouble(v
						.getValue(this.edgeAttributes[i]));
				if (this.multiplyEdgeCosts == 1) {
					cost *= Math.abs((u_x - v_x)) * this.edgeAttrImportance[i];
				} else {
					cost += Math.abs((u_x - v_x)) * this.edgeAttrImportance[i];
				}

				costTypeFound = true;
			}
			if (this.edgeCostTypes[i].equals("discrete")) {
				String u_x = u.getValue(this.edgeAttributes[i]);
				String v_x = v.getValue(this.edgeAttributes[i]);
				if (u_x.equals(v_x)) {
					if (this.multiplyEdgeCosts == 1) {
						cost *= this.edgeCostMu[i] * this.edgeAttrImportance[i];
					} else {
						cost += this.edgeCostMu[i] * this.edgeAttrImportance[i];;
					}

				} else {
					if (this.multiplyEdgeCosts == 1) {
						cost *= this.edgeCostNu[i] * this.edgeAttrImportance[i];
					} else {
						cost += this.edgeCostNu[i] * this.edgeAttrImportance[i];
					}
				}
				costTypeFound = true;
			}
			
			if (this.edgeCostTypes[i].equals("sed")) {
				String u_x = u.getValue(this.edgeAttributes[i]);
				String v_x = v.getValue(this.edgeAttributes[i]);
				if (this.multiplyEdgeCosts == 1) {
					cost *= this.stringEditDistance(u_x, v_x)
							* this.edgeAttrImportance[i];
				} else {
					cost += this.stringEditDistance(u_x, v_x)
							* this.edgeAttrImportance[i];
				}
				costTypeFound = true;
			}
			if (this.edgeCostTypes[i].equals("fingerprint")) {
				
				double u_x = Double.parseDouble(u.getValue(this.edgeAttributes[i]));
				double v_x = Double.parseDouble(v.getValue(this.edgeAttributes[i]));
				double abs = Math.abs(u_x - v_x);
				double dist = Math.min(2*Math.PI - abs, abs);
				if (this.multiplyEdgeCosts == 1) {
					cost *= dist * this.edgeAttrImportance[i];
				} else {
					cost += dist * this.edgeAttrImportance[i];
				}
				
				costTypeFound = true;
			}
			//dz: coma separated double values 
			if (this.edgeCostTypes[i].equals("csvDouble")) {
				double[] u_x= dzParseCSVDouble(u
						.getValue(this.edgeAttributes[i]));
				double[] v_x= dzParseCSVDouble(v
						.getValue(this.edgeAttributes[i]));
				double sum = 0;
				for (int s = 0; s < u_x.length; s++) {
				    sum += Math.pow((u_x[s] - v_x[s]), 2.);   
			        }
				if (this.multiplyEdgeCosts == 1) {
					cost *= sum * this.edgeAttrImportance[i];
				} else {
					cost += sum * this.edgeAttrImportance[i];
				}
				costTypeFound = true;
			}

			if(!costTypeFound)
				throw new IllegalArgumentException("cost type <"+this.edgeCostTypes[i]+"> not found.");

		}
		
		
		
		cost = Math.pow(cost, (1/this.pEdge));
		cost *= (1 - this.alpha);
		return cost;
	}
	
	/**
	 * @return the string edit distance between strings
	 * @param s1 and @param s2
	 * 
	 */
	private double stringEditDistance(String s1, String s2) {
		int n = s1.length();
		int m = s2.length();
		double[][] stringMatrix = new double[n + 1][m + 1];
		stringMatrix[0][0] = 0;
		for (int i = 1; i <= n; i++) {
			stringMatrix[i][0] = stringMatrix[i - 1][0] + 1.;
		}
		for (int j = 1; j <= m; j++) {
			stringMatrix[0][j] = stringMatrix[0][j - 1] + 1.;
		}

		for (int i = 1; i <= n; i++) {
			for (int j = 1; j <= m; j++) {
				double subst = 0.;
				if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
					subst = 0.;
				} else {
					subst = 2;
				}
				double m1 = stringMatrix[i - 1][j - 1] + subst;
				double m2 = stringMatrix[i - 1][j] + 1;
				double m3 = stringMatrix[i][j - 1] + 1;
				stringMatrix[i][j] = Math.min(m1, Math.min(m2, m3));
			}
		}
		return stringMatrix[n][m];
	}

	
	/**
	 * @return the constant cost for node deletion/insertion
	 * multiplied by alpha
	 */
	public double getNodeCosts() {
		return this.alpha * this.nodeCost;
	}

	/**
	 * @return the constant cost for edge deletion/insertion
	 * multiplied by (1-alpha)
	 */
	public double getEdgeCosts() {
		return (1 - this.alpha) * this.edgeCost;
	}

	

	static public double[] dzParseCSVDouble(String str){
		String delims = "[\\[,\\]]";
		String[] tokens = str.split(delims);
	
		int dim = tokens.length-1;
		double[] x;
		x = new double[dim];
	
		for (int i=0; i<dim; i++){
			x[i] = Double.parseDouble(tokens[i+1]);
		}
		return x;
	}

}
