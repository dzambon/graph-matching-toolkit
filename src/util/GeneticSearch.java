package util;


import java.util.Iterator;

import java.util.TreeSet;

import algorithms.BipartiteMatching;

public class GeneticSearch {

	private Graph sourceGraph;
	private Graph targetGraph;
	private int[][] originalMatching;
	private CostFunction costFunction;
	private TreeSet<Gene> pool;
	private TreeSet<Gene> iterativeGenes;
	private EditDistance editDistance;
	private int popSize;
	private double[][] originalCostMatrix;
	private BipartiteMatching bipartiteMatching;
	private double mutProb;
	
	private static final int ITERATIONS = 6;
	
	private static final double PARENTS = 0.25;
	
	private static final int NUM_OF_FORBIDS = 15;
	
	private static final int SEEDS = 3;

	public GeneticSearch(Graph s, Graph t, int[][] m, CostFunction cf, EditDistance ed, int p, double[][] c, BipartiteMatching bm, double mp) {
		this.sourceGraph = s;
		this.targetGraph = t;
		this.originalMatching = m;
		this.costFunction = cf;
		this.editDistance = ed;
		this.popSize = p;
		this.originalCostMatrix = c;
		this.bipartiteMatching = bm;
		this.mutProb = mp;
	}

	public double searchBetter(double d) {
		double bestDist = Double.MAX_VALUE;
		this.getIterativeGenes();
		for (int i = 0; i < SEEDS; i++){
			this.makeInitialPool();
			int lastChange = 0;
			int iteration = 0;
			
			while (iteration-lastChange < ITERATIONS){
//				System.out.println("Iteration "+iteration+" : best Distance: "+this.pool.first().getDistance());
//				this.printPool();
				iteration++;
				TreeSet<Gene> parents = this.selectParents(PARENTS);
				this.makeNewPopulation(parents);
				if (this.pool.first().getDistance() < bestDist){
					lastChange = iteration;
					bestDist = this.pool.first().getDistance();
				}
			}
		}	
//		System.out.println("simply the best..."+bestDist);
		return bestDist;
		
	}

	private void getIterativeGenes() {
		this.iterativeGenes = new TreeSet<Gene>();
		// add original matching as gene to pool
		double distance = this.editDistance.getEditDistance(sourceGraph,
				targetGraph, this.originalMatching, costFunction);

		Gene gene = new Gene(this.originalMatching, distance,
				this.originalCostMatrix);
		this.iterativeGenes.add(gene);
		int[][] m = this.originalMatching;
		double[][] cm = this.copyMatrix(this.originalCostMatrix);
		for (int i = 0; i < NUM_OF_FORBIDS; i++) {
			int forbid = this.editDistance.getMaxCostMatch();
			if (forbid > -1) {
				cm[forbid][m[forbid][1]] = Double.MAX_VALUE;
			}
			// compute the matching using Hungarian or VolgenantJonker (defined
			// in String matching)
			m = this.bipartiteMatching.getMatching(cm);
			// calculate the approximated edit-distance according to the
			// bipartite matching
			distance = this.editDistance.getEditDistance(sourceGraph,
					targetGraph, m, costFunction);
			gene = new Gene(m, distance, cm);

			this.iterativeGenes.add(gene);
		}
	}

	private void makeNewPopulation(TreeSet<Gene> parents) {
		this.pool.clear();
		Gene[] parentsArray = new Gene[parents.size()];
		parents.toArray(parentsArray);
		this.pool.addAll(parents); 
		for (int i = 0; i < this.popSize; i++){
			int f = (int) (Math.random()*parents.size());
			Gene father = parentsArray[f];
//			System.out.println(">>>>>>> F: "+father);
//			father.printMe();
			int m = (int) (Math.random()*parents.size());
			Gene mother = parentsArray[m];
//			System.out.println(">>>>>>> M: "+mother);
//			mother.printMe();
			double[][] mergedCM = this.mergeCM(father, mother);
			int[][] mergedMatching = this.bipartiteMatching.getMatching(mergedCM);
			 double distance = editDistance.getEditDistance(
					sourceGraph, targetGraph, mergedMatching, costFunction);
			Gene child = new Gene(mergedMatching, distance, mergedCM);
//			System.out.println(">>>>>>> C: "+child);
//			child.printMe();
			this.pool.add(child);
		}		
	}

	private double[][] mergeCM(Gene father, Gene mother) {
		double[][] mergedCM = new double[father.getCm().length][father.getCm()[0].length];
		for (int i = 0; i < mergedCM.length; i++){
			for (int j = 0; j < mergedCM[0].length; j++){
				
				mergedCM[i][j] = Math.max(father.getCm()[i][j], mother.getCm()[i][j]);
	
			}
		}
		return mergedCM;
	}

	
	

	private TreeSet<Gene> selectParents(double d) {
		TreeSet<Gene> parents = new TreeSet<Gene>();
		Iterator<Gene> poolIter = this.pool.iterator();
		int i = 0;
		int max = (int) (d*this.pool.size());
		while (poolIter.hasNext() && i < max){
			parents.add(poolIter.next());
			//i++;
		}
		return parents;		
	}

	private void makeInitialPool() {
		this.pool = new TreeSet<Gene>();
		this.pool.addAll(this.iterativeGenes);
		double distance;
		double cm[][];
		int[][] m = this.originalMatching.clone();
		Gene gene;

		for (int p = 0; p < this.popSize-NUM_OF_FORBIDS; p++){
			cm = this.copyMatrix(this.originalCostMatrix);
			for (int i = 0; i < m.length; i++){
				if (Math.random() < this.mutProb){
					if (i >= this.sourceGraph.size() && m[i][1] >= this.targetGraph.size()){
						// do nothing: this is a eps-->eps matching
					} else {
						cm[i][m[i][1]] = Double.MAX_VALUE;
					}
				}
			}
			m = this.bipartiteMatching.getMatching(cm);
			distance = this.editDistance.getEditDistance(
					sourceGraph, targetGraph, m, costFunction);
			gene = new Gene(m, distance, cm);
			this.pool.add(gene);
		}			
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

	private void printPool(){
		System.out.println("The current pool of genes:");
		System.out.print("The best gene has distance: ");
		System.out.println(this.pool.first().getDistance());
		Iterator<Gene> iter = this.pool.iterator();
		while (iter.hasNext()){
			System.out.println("Gene-distances: "+iter.next().getDistance());
		}
	}

}
