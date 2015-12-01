package reader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import structure.Node;
import structure.Problem;
import utils.Params;

public class DatasetAnalysis {
	
	public static List<List<Integer>> getClustersFromIllinoisDataset(List<Problem> problems) 
			throws Exception {
		List<List<Integer>> clusters = new ArrayList<>();
		double threshold = 0.9;
		for(int i=0; i<problems.size(); ++i) {
			int bestNeighbor = -1;
			double bestScore = threshold;
			for(int j=0; j<i; ++j) {
				double score = getMatchScore(problems.get(i), problems.get(j));
				if(score > bestScore) {
					bestNeighbor = j;
					bestScore = score;
				}
			}
			if(bestNeighbor == -1) {
				List<Integer> set = new ArrayList<>();
				set.add(i);
				clusters.add(set);
			} else {
				for(List<Integer> set : clusters) {
					if(set.contains(bestNeighbor)) {
						set.add(i);
						break;
					}
				}
			}
		}
		return clusters;
	}
	
	public static List<String> getNormalizedText(Problem prob) {
		List<String> normText = new ArrayList<>();
		for(int i=0; i<prob.ta.size(); ++i) {
			if(prob.posTags.get(i).getLabel().equals("CD")) {
				normText.add("CD");
			} else if(prob.posTags.get(i).getLabel().startsWith("N")) {
				normText.add("NN");
			} else {
				normText.add(prob.ta.getToken(i));
			}
		}
		return normText;
	}
	
	public static double getMatchScore(Problem prob1, Problem prob2) {
		int total = 0, match = 0;
		List<String> tokens1 = getNormalizedText(prob1);
		List<String> tokens2 = getNormalizedText(prob2);
		for(String str1 : tokens1) {
			total++;
			for(String str2 : tokens2) {
				if(str1.equals(str2)) {
					match++;
					break;
				}
			}
		}
		for(String str1 : tokens2) {
			total++;
			for(String str2 : tokens1) {
				if(str1.equals(str2)) {
					match++;
					break;
				}
			}
		}
		for(int i=0; i<tokens1.size()-1; ++i) {
			total++;
			for(int j=0; j<tokens2.size()-1; ++j) {
				if(tokens1.get(i).equals(tokens2.get(j)) && 
						tokens1.get(i+1).equals(tokens2.get(j+1))) {
					match++;
					break;
				}
			}
		}
		for(int i=0; i<tokens2.size()-1; ++i) {
			total++;
			for(int j=0; j<tokens1.size()-1; ++j) {
				if(tokens2.get(i).equals(tokens1.get(j)) && 
						tokens2.get(i+1).equals(tokens1.get(j+1))) {
					match++;
					break;
				}
			}
		}
		return match*1.0/total;	
	}
	
	public static boolean isOp(Problem prob, String op) {
		for(Node node : prob.expr.getAllSubNodes()) {
			if(node.label.equals(op)) {
				return true;
			}
		}
		return false;
	}
	
	public static List<List<Integer>> createFolds(List<Problem> problems) 
			throws Exception {
		List<List<Integer>> clusters = getClustersFromIllinoisDataset(problems);
		List<List<Integer>> types = new ArrayList<>();
		System.out.println("Clusters : "+clusters.size());
		for(String label : Arrays.asList("ADD", "SUB", "MUL", "DIV")) {
			List<Integer> fold1 = new ArrayList<>();
			List<Integer> fold2 = new ArrayList<>();
			for(int i=0; i<clusters.size(); ++i) {
				List<Integer> cluster = clusters.get(i);
				for(Integer j : cluster) {
					if(isOp(problems.get(j), label)) {
						if(problems.get(j).quantities.size() > 2) {
							fold1.add(i);
						} else {
							fold2.add(i);			
						}
					}
					break;
				}
			}
			types.add(fold1);
			types.add(fold2);
		}
		List<List<Integer>> folds = new ArrayList<>(); 
		int numFolds = 5;
		for(int i=0; i<numFolds; ++i) {
			folds.add(new ArrayList<Integer>());
		}
		for(List<Integer> type : types) {
			int totalType = 0, fold0 = 0;
			for(Integer j : type) totalType += Math.min(5, clusters.get(j).size());
			for(Integer j : type) {
				if(fold0 < totalType*1.0/numFolds) {
					folds.get(0).addAll(clusters.get(j).subList(0, 
							Math.min(5, clusters.get(j).size())));
				} else if(fold0 >= totalType*1.0/numFolds && 
						fold0 < totalType*2.0/numFolds){
					folds.get(1).addAll(clusters.get(j).subList(0, 
							Math.min(5, clusters.get(j).size())));
				} else if(fold0 >= totalType*2.0/numFolds && 
						fold0 < totalType*3.0/numFolds){
					folds.get(2).addAll(clusters.get(j).subList(0, 
							Math.min(5, clusters.get(j).size())));
				} else if(fold0 >= totalType*3.0/numFolds && 
						fold0 < totalType*4.0/numFolds){
					folds.get(3).addAll(clusters.get(j).subList(0, 
							Math.min(5, clusters.get(j).size())));
				} else if(fold0 >= totalType*4.0/numFolds && 
						fold0 < totalType*5.0/numFolds){
					folds.get(4).addAll(clusters.get(j).subList(0, 
							Math.min(5, clusters.get(j).size())));
				}
				fold0 += Math.min(5, clusters.get(j).size());
			}
		}
		return folds;
	}
	
	public static void createFoldFiles() throws Exception {
		List<Problem> problems = Reader.readIllinoisProblemsFromFile(
				Params.illinoisMathFile);
		List<List<Integer>> folds = createFolds(problems);
		for(int i=0; i<folds.size(); ++i) {
			String str = "";
			for(Integer j : folds.get(i)) {
				str += j + "\n";
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(
					new File("fold"+i+".txt")));
			bw.write(str);
			bw.close();
		}
	}

	public static void main(String args[]) throws Exception {
		createFoldFiles();
	}
}
