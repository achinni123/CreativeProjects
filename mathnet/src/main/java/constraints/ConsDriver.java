package constraints;

import java.util.List;

import structure.Problem;
import utils.Folds;
import utils.Params;
import utils.Tools;
import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.utilities.commands.InteractiveShell;
import edu.illinois.cs.cogcomp.sl.core.SLModel;

public class ConsDriver {
	
	public static void crossVal(String dataset) throws Exception {
		double acc = 0.0;
		int numFolds = Folds.getNumFolds(dataset);
		for(int i=0;i<numFolds;i++) {
			acc += doTest(i, dataset);
		}
		System.out.println("CV : " + (acc/numFolds));
	}

	public static double doTest(int testFold, String dataset) throws Exception {
		String prefixRel = null;
		String prefixPair = null;
		Pair<List<Problem>, List<Problem>> split = null;
		if(dataset.equals("AI2")) {
			prefixRel = Params.ai2rel;
			prefixPair = Params.ai2pair;
			split = Folds.getDataSplitForAI2(testFold);
		}
		if(dataset.equals("IL")) {
			prefixRel = Params.ilRel;
			prefixPair = Params.ilPair;
			split = Folds.getDataSplitForIL(testFold);
		}
		SLModel relModel = SLModel.loadModel(prefixRel+testFold+".save");
		SLModel pairModel = SLModel.loadModel(prefixPair+testFold+".save");
		return ConsInfSolver.constrainedInf(split.getSecond(), relModel, pairModel, dataset);
	}
	
	public static void tune(int testFold, String dataset) throws Exception {
		String prefixRel = null;
		String prefixPair = null;
		Pair<List<Problem>, List<Problem>> split = null;
		if(dataset.equals("AI2")) {
			prefixRel = Params.ai2rel;
			prefixPair = Params.ai2pair;
			split = Folds.getDataSplitForAI2(testFold);
		}
		if(dataset.equals("IL")) {
			prefixRel = Params.ilRel;
			prefixPair = Params.ilPair;
			split = Folds.getDataSplitForIL(testFold);
		}
		SLModel relModel = SLModel.loadModel(prefixRel+testFold+".save");
		SLModel pairModel = SLModel.loadModel(prefixPair+testFold+".save");
		double vals[] = {0.000001, 0.0001, 0.01, 1.0, 100.0, 10000.0, 1000000.0};
		double bestVal = 1.0;
		double bestAcc = 0.0;
		int total = 0;
		for(int a=0; a<vals.length; ++a) {
			ConsInfSolver.wRel = vals[a];
			double acc = ConsInfSolver.constrainedInf(
					split.getFirst(), relModel, pairModel, dataset);
			if(acc > bestAcc) {
				bestAcc = acc;
				bestVal = ConsInfSolver.wRel;
			}
			total++;
			if(total%100==0) System.err.println(
					"Number of param settings checked: "+total);
		}
		ConsInfSolver.wRel = bestVal;
	}
	
	public static void tunedCrossVal(String dataset) throws Exception {
		double acc = 0.0;
		int numFolds = Folds.getNumFolds(dataset);
		for(int i=0;i<numFolds;i++) {
			System.out.println("Tuning ...");
			Params.printMistakes = false;
			tune(i, dataset);
			System.out.println("Tuned parameters");
			System.out.println("wRel : "+ConsInfSolver.wRel);
			System.out.println("Testing");
			Params.printMistakes = true;
			acc += doTest(i, dataset);
		}
		System.out.println("CV : " + (acc/numFolds));
	}
	
	public static void main(String[] args) throws Exception {
		InteractiveShell<ConsDriver> tester = new InteractiveShell<ConsDriver>(
				ConsDriver.class);
		if (args.length == 0) {
			tester.showDocumentation();
		} else {
			tester.runCommand(args);
		}
		Tools.pipeline.closeCache();
	}
	
	public static double norm(double d[]) {
		double r = 0.0;
		for(Double val : d) {
			r += Math.pow(val, 2);
		}
		return r;
	}
 
}
