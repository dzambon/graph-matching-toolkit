/**
 * 
 */
package util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * @author riesen
 *
 */
public class Swap {
	
	private Graph sourceGraph;
	private Graph targetGraph;
	private int[][] originalMatching;
	private CostFunction costFunction;
	private EditDistance editDistance;
	private double[][] originalCostMatrix;
	
	private double theta = 0.3;
	private TreeSet<Gene> pool;
	private int popSize = 100;
	
	private static final int ITERATIONS = 6;
	
	private static final double PARENTS = 0.25;
	
	private double mutProb = 0.5;
	
	private static final int SEEDS = 3;

	/**
	 * 
	 */
	public Swap(Graph sourceGraph, Graph targetGraph,
			int[][] matching, CostFunction costFunction,
			EditDistance editDistance, double[][] costMatrix) {
		this.sourceGraph = sourceGraph;
		this.targetGraph = targetGraph;
		this.originalMatching = matching;
		this.costFunction = costFunction;
		this.editDistance = editDistance;
		this.originalCostMatrix = costMatrix;
	}
	
	public double greedySearch(){
		double d = this.editDistance.getEditDistance(this.sourceGraph,
				this.targetGraph, this.originalMatching, this.costFunction);
		int[] bestSwap = new int[4];
		Arrays.fill(bestSwap, -1);
		boolean swapped = true;
		while (swapped){
			swapped = false;
			for (int i = 0; i < this.originalMatching.length-1; i++){
				int p = this.originalMatching[i][1];
//				System.out.println("We investigate on "+i +" --> "+p);
				double c_ip = this.originalCostMatrix[i][p];
				for (int j = i+1; j < this.originalMatching.length; j++){
					int q = this.originalMatching[j][1];
//					System.out.println("swap with "+j +" --> "+q);
					double c_jq = this.originalCostMatrix[j][q];
					double c_iq = this.originalCostMatrix[i][q];
					double c_jp = this.originalCostMatrix[j][p];
					double totalCostOrig = c_ip + c_jq;
					double totalCostSwap = c_iq + c_jp; 
//					System.out.println("The costs before the swap: "+totalCostOrig+" "+totalCostSwap+"");
					if (Math.abs(totalCostOrig - totalCostSwap) <= this.theta*totalCostOrig){
//						System.out.println("Yes , we swap");
						this.originalMatching[i][1] = q;
						this.originalMatching[j][1] = p;
						double d2 = this.editDistance.getEditDistance(this.sourceGraph,
								this.targetGraph, this.originalMatching, this.costFunction);
						if (d2 < d){
//							System.out.println("Distance after swap is improved from "+d+" to "+d2);
							d = d2;
							bestSwap[0]=i;
							bestSwap[1]=p;
							bestSwap[2]=j;
							bestSwap[3]=q;
							swapped = true; 
							
						}
						this.originalMatching[i][1] = p;
						this.originalMatching[j][1] = q;
					}
				}
			}
			if (swapped){
				this.originalMatching[bestSwap[0]][1] = bestSwap[3];
				this.originalMatching[bestSwap[2]][1] = bestSwap[1];
			}
		}
		return d;
	}
	
	public double geneticSearch(){
		double bestDist = Double.MAX_VALUE;
		for (int i = 0; i < SEEDS; i++){
			this.makeInitialPool();
			int lastChange = 0;
			int iteration = 0;
//			System.out.println("SEED: "+i);
			while (iteration-lastChange < ITERATIONS){
				iteration++;
				TreeSet<Gene> parents = this.selectParents(PARENTS);
				this.makeNewPopulation(parents);
//				System.out.println("Iteration "+iteration+" ; best distance: "+this.pool.first().getDistance());
				if (this.pool.first().getDistance() < bestDist){
					lastChange = iteration;
					
					bestDist = this.pool.first().getDistance();
//					System.out.println("Improved "+bestDist);
				}
			}
		}	
		return this.pool.first().getDistance();
	}

	private void makeNewPopulation(TreeSet<Gene> parents) {
		this.pool.clear();
		this.pool.addAll(parents);
//		System.out.println("New Population is built: "+this.pool.size() +" are already in!");
		Gene[] parentsArray = new Gene[parents.size()];
		parents.toArray(parentsArray);
		while (this.pool.size() < this.popSize){
			int f = (int) (Math.random()*parents.size());
			Gene father = parentsArray[f];
			int[][] m = this.copyMatrix(father.getMatching());
			for (int i = 0; i < m.length-1; i++){
				int p = m[i][1];
				for (int j = i+1; j <m.length; j++){
					int q = m[j][1];
					double c_iq = this.originalCostMatrix[i][q];
					double c_jp = this.originalCostMatrix[j][p];
					double totalCostSwap = c_iq + c_jp; 
					if (totalCostSwap < Double.MAX_VALUE){
						if (Math.random() < this.mutProb){
							m[i][1] = q;
							m[j][1] = p;
							j = m.length;
						}
					}
				}
			}
//			System.out.println("*** Gene ");
//			this.printMatching(m);
			double d2 = this.editDistance.getEditDistance(this.sourceGraph,
					this.targetGraph, m, this.costFunction);
//			System.out.println("---> distance = "+d2);
			Gene gene = new Gene(m,d2);
			this.pool.add(gene);
		}	
	}

	private void makeInitialPool() {
//		System.out.println("Making initial pool!");
		this.pool = new TreeSet<Gene>();
		int[][] m = this.copyMatrix(this.originalMatching);
		double d = this.editDistance.getEditDistance(this.sourceGraph,
				this.targetGraph, this.originalMatching, this.costFunction);
		Gene original = new Gene(this.originalMatching, d);
		this.pool.add(original);
		for (int s = 0; s < this.popSize; s++){
			m =  this.copyMatrix(this.originalMatching);
			for (int i = 0; i < this.originalMatching.length-1; i++){
				int p = m[i][1];
				double c_ip = this.originalCostMatrix[i][p];
				for (int j = i+1; j < this.originalMatching.length; j++){
					int q = m[j][1];
					double c_jq = this.originalCostMatrix[j][q];
					double c_iq = this.originalCostMatrix[i][q];
					double c_jp = this.originalCostMatrix[j][p];
					double totalCostOrig = c_ip + c_jq;
					double totalCostSwap = c_iq + c_jp; 
					if (Math.abs(totalCostOrig - totalCostSwap) < this.theta*totalCostOrig){
						if (Math.random() < this.mutProb){
//							System.out.println("Swapped!");
							m[i][1] = q;
							m[j][1] = p;
							j = m.length;
						}
					}
				}
			}
//			System.out.println("*** Gene "+s);
//			this.printMatching(m);
			double d2 = this.editDistance.getEditDistance(this.sourceGraph,
					this.targetGraph, m, this.costFunction);
//			System.out.println("---> distance = "+d2);
			Gene gene = new Gene(m,d2);
			this.pool.add(gene);
		}			
	}
	
	private void printMatching(int[][] m) {
		for (int i = 0;  i < m.length; i++){
			System.out.println(m[i][0] +" --> "+m[i][1]);
		}
	}
	
	private int[][] copyMatrix(int[][] m) {
		int[][] copy = new int[m.length][m[0].length];
		for (int i = 0; i < copy.length; i++){
			for (int j = 0; j < copy[0].length; j++){
				copy[i][j] = m[i][j];
			}
		}
		return copy;
	}

	private TreeSet<Gene> selectParents(double d) {
		TreeSet<Gene> parents = new TreeSet<Gene>();
		Iterator<Gene> poolIter = this.pool.iterator();
		int i = 0;
		int max = (int) (d*this.pool.size());
		while (poolIter.hasNext() && i < max){
			Gene g = poolIter.next();
			parents.add(g);
			i++;
		}
		return parents;		
	}
	
	

	

}
