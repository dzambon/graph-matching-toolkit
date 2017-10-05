package algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import util.AssignmentAndCost;

public class GreedyMatching {



	
	

	public int[][] getMatching(double[][] cm, boolean shuffle) {
		int[][] assignment = new int[cm.length][2];
		int[] usedIndices = new int[cm.length];
//		Arrays.fill(usedIndices, 0);
		ArrayList<Integer> unusedIndices = new ArrayList<Integer>(); 
		for (int k = 0; k < cm.length; k++){
			unusedIndices.add(k);
		}
		if (shuffle){
			Collections.shuffle(unusedIndices);
		}
		for (int i = 0; i < cm.length; i++){
			int iIndex = unusedIndices.get(i);
			int greedyMatch = -1;
			double min = Double.MAX_VALUE;
			for (int j = 0; j < cm.length; j++){
				if (usedIndices[j] == 0){
					if (cm[iIndex][j] <= min){
						min = cm[iIndex][j];
						greedyMatch = j;
					}
				}
			}
			assignment[iIndex][0] = iIndex;
			assignment[iIndex][1] = greedyMatch;
			usedIndices[greedyMatch] = 1;
		}
		return assignment;
	}

	private void printAssignment(int[][] assignment, double[][] cm) {
		System.out.println("The cost matrix: ");
		for (int i = 0; i < cm.length; i++){
			for (int j = 0; j < cm.length; j++ ){
				if (cm[i][j]>Double.MAX_VALUE){
					System.out.print("inf\t");
				} else {
					System.out.print(cm[i][j]+"\t");
				}
				
			}
			System.out.println();
		}
		System.out.println("The Assignment:");
		for (int i = 0; i < assignment.length; i++){
			System.out.println(assignment[i][0] +" --> "+assignment[i][1]);
		}
		
	}



	public int[][] getRefinedMatching(double[][] cm) {
		int[][] assignment = new int[cm.length][2];
		int[] usedIndicesOfG2 = new int[cm.length];
		int[] usedIndicesOfG1 = new int[cm.length];

		LinkedList<Integer> unusedIndicesOfG1 = new LinkedList<Integer>(); 
		for (int k = 0; k < cm.length; k++){
			unusedIndicesOfG1.add(k);
		}
		
		Collections.shuffle(unusedIndicesOfG1);
		
		while (!unusedIndicesOfG1.isEmpty()){
			int iIndex = unusedIndicesOfG1.removeFirst();
			usedIndicesOfG1[iIndex]=1;
			int greedyMatch = -1;
			double min = Double.MAX_VALUE;
			for (int j = 0; j < cm.length; j++){
				if (usedIndicesOfG2[j] == 0){
					if (cm[iIndex][j] <= min){
						min = cm[iIndex][j];
						greedyMatch = j;
					}
				}
			}
			
			// iIndex-->greedyMatch is minimal is greedyMatch-->iIndex also?
			int greedyReverseMatch = -1;
			min = Double.MAX_VALUE;
			for (int i = 0; i < cm.length; i++){
				if (usedIndicesOfG1[i] == 0){
					if (cm[i][greedyMatch] < min){
						min = cm[i][greedyMatch];
						greedyReverseMatch = i;
					}
				}
			}
			if (min < cm[iIndex][greedyMatch]){
				usedIndicesOfG1[iIndex]=0;
				assignment[greedyReverseMatch][0] = greedyReverseMatch;
				assignment[greedyReverseMatch][1] = greedyMatch;
				usedIndicesOfG1[greedyReverseMatch]=1;
				usedIndicesOfG2[greedyMatch] = 1;
				unusedIndicesOfG1.remove(new Integer(greedyReverseMatch));
				unusedIndicesOfG1.add(iIndex);
			} else {
				assignment[iIndex][0] = iIndex;
				assignment[iIndex][1] = greedyMatch;
				usedIndicesOfG2[greedyMatch] = 1;
			}	
		}
		return assignment;
	}



	public int[][] greedySort(double[][] cm, int sSize, int tSize) {
		int[][] assignment = new int[sSize][2];
		int[] usedIndicesOfG2 = new int[tSize];
		int[] usedIndicesOfG1 = new int[sSize];
		LinkedList<Integer> unusedIndicesOfG1 = new LinkedList<Integer>(); 
		LinkedList<Integer> unusedIndicesOfG2 = new LinkedList<Integer>(); 
		for (int k = 0; k < sSize; k++){
			unusedIndicesOfG1.add(k);
		}
		for (int k = 0; k < tSize; k++){
			unusedIndicesOfG2.add(k);
		}
		LinkedList<AssignmentAndCost> substitutions = new LinkedList<AssignmentAndCost>();
		for (int i = 0; i < sSize; i++){
			for (int j = 0; j < tSize; j++){
				AssignmentAndCost aAndc = new AssignmentAndCost(i,j,cm[i][j]);
				substitutions.add(aAndc);
			}
		}
		Collections.sort(substitutions);
		Iterator<AssignmentAndCost> iter = substitutions.iterator();
		while (!unusedIndicesOfG1.isEmpty() && !unusedIndicesOfG2.isEmpty()){
			AssignmentAndCost aAndc = iter.next();
			int from = aAndc.getFrom();
			if (usedIndicesOfG1[from]==0){
				int to = aAndc.getTo();
				if (usedIndicesOfG2[to]==0){
					usedIndicesOfG1[from]=1;
					usedIndicesOfG2[to]=1;
					assignment[from][0] = from;
					assignment[from][1] = to;
					unusedIndicesOfG1.remove(new Integer(from));
					unusedIndicesOfG2.remove(new Integer(to));
				}
			}
		}
		while (!unusedIndicesOfG1.isEmpty()){
			int from = unusedIndicesOfG1.removeFirst();
			assignment[from][0] = from;
			assignment[from][1] = tSize+1;
		}
		
		return assignment;
	}



	public int[][] getRefinedMatching2(double[][] cm, BipartiteMatching bm) {
		int[] usedIndicesOfG2 = new int[cm.length];
		int[] usedIndicesOfG1 = new int[cm.length];

		LinkedList<Integer> unusedIndicesOfG1 = new LinkedList<Integer>(); 
		for (int k = 0; k < cm.length; k++){
			unusedIndicesOfG1.add(k);
		}
		
		Collections.shuffle(unusedIndicesOfG1);
		
		while (!unusedIndicesOfG1.isEmpty()){
			int iIndex = unusedIndicesOfG1.removeFirst();
			usedIndicesOfG1[iIndex]=1;
			int greedyMatch = -1;
			double min = Double.MAX_VALUE;
			for (int j = 0; j < cm.length; j++){
				if (usedIndicesOfG2[j] == 0){
					if (cm[iIndex][j] <= min){
						min = cm[iIndex][j];
						greedyMatch = j;
					}
				}
			}
			
			// iIndex-->greedyMatch is minimal is greedyMatch-->iIndex also?
			min = Double.MAX_VALUE;
			for (int i = 0; i < cm.length; i++){
				if (usedIndicesOfG1[i] == 0){
					if (cm[i][greedyMatch] < min){
						min = cm[i][greedyMatch];
					}
				}
			}
			if (min >= cm[iIndex][greedyMatch]){
				for (int j = 0; j < cm.length; j++){
					if (j!=greedyMatch){
						cm[iIndex][greedyMatch]=Double.POSITIVE_INFINITY;
						cm[greedyMatch][iIndex]=Double.POSITIVE_INFINITY;
					} else {
						cm[iIndex][greedyMatch]=0.;
						cm[greedyMatch][iIndex]=0.;
					}
				}
			} 
		}
		return bm.getMatching(cm);
	}

}
