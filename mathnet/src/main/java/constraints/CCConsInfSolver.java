package constraints;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pair.PairDriver;
import pair.PairX;
import structure.Node;
import structure.PairComparator;
import structure.Problem;
import utils.Params;
import utils.Tools;

import com.google.common.collect.MinMaxPriorityQueue;

import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.utilities.commands.InteractiveShell;
import edu.illinois.cs.cogcomp.quant.driver.QuantSpan;
import edu.illinois.cs.cogcomp.sl.core.SLModel;

public class CCConsInfSolver {
	
	public static double constrainedInf(List<Problem> testData,
			SLModel pairModel) throws Exception {
		double correct = 0.0, total = 0.0;
		for(Problem prob : testData) {
			total += 1.0;
			Map<String, Double> pairScores = new HashMap<String, Double>();
			int numQuantities = prob.quantities.size();
			for(int i=0; i<numQuantities; ++i) {
				for(int j=i+1; j<numQuantities; ++j) {
					pairScores.putAll(PairDriver.getLabelsWithScores(
							new PairX(prob, i, j), pairModel));
				}
			}
			double ans = CCConsInfSolver.getBestStructure(
					prob.ta, prob.quantities, pairScores);
			if(Tools.safeEquals(ans, prob.answer)) {
				correct += 1.0;
			} else if(Params.printMistakes) {
				System.out.println(prob.id+" : "+prob.ta.getText());
				System.out.println("Gold : "+prob.answer);
				System.out.println("Predicted : "+ans);
			}
		}
		System.out.println("Constrained Inference : "+correct+" / "+total+" = "+(correct/total));
		return (correct/total);
	}
	
	public static double getBestStructure(TextAnnotation ta, List<QuantSpan> quantities, 
			Map<String, Double> pairScores) throws Exception {
		PairComparator<List<Node>> nodePairComparator = 
				new PairComparator<List<Node>>() {};
		MinMaxPriorityQueue<Pair<List<Node>, Double>> beam1 = 
				MinMaxPriorityQueue.orderedBy(nodePairComparator)
				.maximumSize(200).create();
		MinMaxPriorityQueue<Pair<List<Node>, Double>> beam2 = 
				MinMaxPriorityQueue.orderedBy(nodePairComparator)
				.maximumSize(200).create();
		int n = quantities.size();
		List<Node> init = new ArrayList<>();
		for(int i=0; i<quantities.size(); ++i) {
			init.add(new Node(i, quantities.get(i), "NUM"));
		}
		beam1.add(new Pair<List<Node>, Double>(init, 0.0));
		for(int i=0; i<n-1; ++i) {
			for(Pair<List<Node>, Double> state : beam1) {
				beam2.addAll(enumerateSingleMerge(state, pairScores));
			}
			beam1.clear();
			beam1.addAll(beam2);
			beam2.clear();
		}
		// Constraint scores
		for(Pair<List<Node>, Double> state : beam1) {
			boolean isPositive = Constraints.isPositive(state.getFirst().get(0).getValue());
			boolean isInteger = Constraints.isInteger(ta, state.getFirst().get(0).getValue());
			if(!isPositive) continue;
			if(!isInteger) continue;
			beam2.add(new Pair<List<Node>, Double>(state.getFirst(), 
					state.getSecond()));
		}
		return beam2.element().getFirst().get(0).getValue();
	}
	
	public static List<Pair<List<Node>, Double>> enumerateSingleMerge(
			Pair<List<Node>, Double> state, Map<String, Double> pairScores) {
		List<Pair<List<Node>, Double>> nextStates = new ArrayList<>();
		List<Node> nodeList = state.getFirst();
		if(nodeList.size() == 1) {
			List<Pair<List<Node>, Double>> tmpNodeList = 
					new ArrayList<Pair<List<Node>, Double>>();
			tmpNodeList.add(state);
			return tmpNodeList;
		}
		double initScore = state.getSecond();
		for(int i=0; i<nodeList.size(); ++i) {
			for(int j=i+1; j<nodeList.size(); ++j) {
				List<Node> tmpNodeList = new ArrayList<Node>();
				tmpNodeList.addAll(nodeList);
				tmpNodeList.remove(i);
				tmpNodeList.remove(j-1);
				for(Pair<Node, Double> pair : enumerateMerge(
						nodeList.get(i), nodeList.get(j), pairScores)) {
					List<Node> newNodeList = new ArrayList<Node>();
					newNodeList.addAll(tmpNodeList);
					newNodeList.add(pair.getFirst());
					nextStates.add(new Pair<List<Node>, Double>(newNodeList, 
							initScore + pair.getSecond()));
				}
			}
		}
		return nextStates;
	}
	
	public static List<Pair<Node, Double>> enumerateMerge(
			Node node1, Node node2, Map<String, Double> pairScores) {
		List<Pair<Node, Double>> nextStates = new ArrayList<>();
		List<String> labels = Arrays.asList("ADD", "SUB", "SUB_REV" 
				,"MUL", "DIV", "DIV_REV"
				);
		double mergeScore;
		for(String label : labels) {
			if(label.endsWith("REV")) {
				label = label.substring(0,3);
				mergeScore = getScore(node2, node1, label, pairScores);
				Node node = new Node(label, Arrays.asList(node2, node1));
				nextStates.add(new Pair<Node, Double>(node, mergeScore));
			} else {
				mergeScore = getScore(node1, node2, label, pairScores);
				Node node = new Node(label, Arrays.asList(node1, node2));
				nextStates.add(new Pair<Node, Double>(node, mergeScore));
			}
		}
		return nextStates;
	}
	
	public static double getScore(Node node1, Node node2, String label, 
			Map<String, Double> pairScores) {
		double mergeScore = 0.0;
		for(Node leaf1 : node1.getLeaves()) {
			for(Node leaf2 : node2.getLeaves()) {
				if(Math.abs(leaf1.quantIndex - leaf2.quantIndex) > 1.5) continue;
				String pairKey = getPairKey(leaf1.quantIndex, leaf2.quantIndex, label);
				mergeScore += pairScores.get(pairKey);
			}
		}
		return mergeScore;
	}
	
	// Input label does not have REV in it
	public static String getPairKey(int index1, int index2, String label) {
		String key = null;
		if(index1 > index2 && (label.equals("SUB") || label.equals("DIV"))) {
			key = index2+"_"+index1+"_"+label+"_REV";
		} else if(index1 > index2) {
			key = index2+"_"+index1+"_"+label;
		} else {
			key = index1+"_"+index2+"_"+label;
		}
		return key;
	}
	
	public static void main(String[] args) throws Exception {
		InteractiveShell<CCConsInfSolver> tester = new InteractiveShell<CCConsInfSolver>(
				CCConsInfSolver.class);
		if (args.length == 0) {
			tester.showDocumentation();
		} else {
			tester.runCommand(args);
		}
	}
	
	
}