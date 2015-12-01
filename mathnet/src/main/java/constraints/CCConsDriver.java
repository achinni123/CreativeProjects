package constraints;

import java.util.List;

import structure.Problem;
import utils.Folds;
import utils.Params;
import utils.Tools;
import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.utilities.commands.InteractiveShell;
import edu.illinois.cs.cogcomp.sl.core.SLModel;

public class CCConsDriver {
	
	public static void crossVal() throws Exception {
		double acc = 0.0;
		int numFolds = Folds.getNumFolds("CC");
		for(int i=0;i<numFolds;i++) {
			acc += doTest(i);
		}
		System.out.println("CV : " + (acc/numFolds));
	}

	public static double doTest(int testFold) throws Exception {
		String prefixPair = null;
		Pair<List<Problem>, List<Problem>> split = null;
		prefixPair = Params.ccPair;
		split = Folds.getDataSplitForCC(testFold);
		SLModel pairModel = SLModel.loadModel(prefixPair+testFold+".save");
		return CCConsInfSolver.constrainedInf(split.getSecond(), pairModel);
	}
	
	public static void tunedCrossVal() throws Exception {
		double acc = 0.0;
		int numFolds = Folds.getNumFolds("CC");
		for(int i=0;i<numFolds;i++) {
			Params.printMistakes = true;
			acc += doTest(i);
		}
		System.out.println("CV : " + (acc/numFolds));
	}
	
	public static void main(String[] args) throws Exception {
		InteractiveShell<CCConsDriver> tester = new InteractiveShell<CCConsDriver>(
				CCConsDriver.class);
		if (args.length == 0) {
			tester.showDocumentation();
		} else {
			tester.runCommand(args);
		}
		Tools.pipeline.closeCache();
	}
 
}
