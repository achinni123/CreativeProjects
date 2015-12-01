package baseline;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import reader.Reader;
import structure.Problem;
import utils.Params;
import edu.illinois.cs.cogcomp.core.utilities.commands.CommandDescription;
import edu.illinois.cs.cogcomp.core.utilities.commands.InteractiveShell;
import edu.illinois.cs.cogcomp.sl.core.IStructure;
import edu.illinois.cs.cogcomp.sl.core.SLModel;
import edu.illinois.cs.cogcomp.sl.core.SLParameters;
import edu.illinois.cs.cogcomp.sl.core.SLProblem;
import edu.illinois.cs.cogcomp.sl.learner.Learner;
import edu.illinois.cs.cogcomp.sl.learner.LearnerFactory;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;
/**
 * 
 * @author subhroroy
 *
 */
public class Experiment
{
	public static void main(String[] args) throws Exception
	{
		InteractiveShell<Experiment> tester = new InteractiveShell<Experiment>(
				Experiment.class);
		if (args.length == 0)
			tester.showDocumentation();
		else
		{
			tester.runCommand(args);
		}
	}

	@CommandDescription(description = "Trains and tests classifier")
	public static void testOperationClassifier() throws Exception
	{
//		List<Problem> problemList = Reader.readAI2ProblemsFromFile(Params.ai2MathFile);
		List<Problem> problemList = Reader.readIllinoisProblemsFromFile(Params.illinoisMathFile);
		List<Problem> train = new ArrayList<Problem>();
		List<Problem> test = new ArrayList<Problem>();
		int cv = 2;
		int l=problemList.size();
//		Collections.shuffle(problemList);
		for(int i=0;i<cv; i++){
			train.clear();
			test.clear();
			for(int j=0;j<cv;j++){
				if(i==j)
					test.addAll(problemList.subList(j*l/cv, (j+1)*l/cv));
				else
					train.addAll(problemList.subList(j*l/cv, (j+1)*l/cv));
			}
			SLProblem trainP = OperationInfSolver.getSP(train);
			SLProblem testP = OperationInfSolver.getSP(test);
			SLModel model = new SLModel();
			Lexiconer lm = new Lexiconer();
			lm.setAllowNewFeatures(true);
			model.lm = lm;
			FeatureManager fg = new FeatureManager(lm);
			model.featureGenerator = fg;
			model.infSolver = new OperationInfSolver(fg);
			SLParameters para = new SLParameters();
			para.loadConfigFile(Params.spConfigFile);
			Learner learner = LearnerFactory.getLearner(model.infSolver, fg, para);
			System.out.println("Training on "+trainP.size()+" examples");
			model.wv = learner.train(trainP);
			System.out.println("Done");
			lm.setAllowNewFeatures(false);
			
			int correct = 0;
			for(int j=0; j<test.size(); ++j) {
				IStructure pred = model.infSolver.getBestStructure(model.wv, 
						testP.instanceList.get(j));
				if(model.infSolver.getLoss(testP.instanceList.get(j), 
						testP.goldStructureList.get(j), pred) < 0.01) {
					correct++;
				}
			}
			System.out.println("Accuracy : "+ (correct*1.0/testP.size()));
		}
	}
	
	@CommandDescription(description = "Tests classifier")
	public static void testOrderClassifier(String UseGold) throws Exception
	{
//		List<Problem> problemList = Reader.readAI2ProblemsFromFile(Params.ai2MathFile);
		List<Problem> problemList = Reader.readIllinoisProblemsFromFile(Params.illinoisMathFile);
		List<Problem> train = new ArrayList<Problem>();
		List<Problem> test = new ArrayList<Problem>();
		int cv = 2;
		int l=problemList.size();
		Collections.shuffle(problemList);
		for(int i=0; i<cv; i++){
			train.clear();
			test.clear();
			for(int j=0;j<cv;j++){
				if(i==j)
					test.addAll(problemList.subList(j*l/cv, (j+1)*l/cv));
				else
					train.addAll(problemList.subList(j*l/cv, (j+1)*l/cv));
			}
			SLProblem trainP = OrderInfSolver.getSP(train);
			SLProblem testP = OrderInfSolver.getSP(test);
			SLModel model = new SLModel();
			Lexiconer lm = new Lexiconer();
			lm.setAllowNewFeatures(true);
			model.lm = lm;
			FeatureManager fg = new FeatureManager(lm);
			model.featureGenerator = fg;
			model.infSolver = new OrderInfSolver(fg);
			SLParameters para = new SLParameters();
			para.loadConfigFile(Params.spConfigFile);
			Learner learner = LearnerFactory.getLearner(model.infSolver, fg, para);
			System.out.println("Training on "+trainP.size()+" examples");
			model.wv = learner.train(trainP);
			System.out.println("Done");
			lm.setAllowNewFeatures(false);
			
			int correct = 0;
			for(int j=0; j<test.size(); ++j) {
				IStructure pred = model.infSolver.getBestStructure(model.wv, 
						testP.instanceList.get(j));
				if(model.infSolver.getLoss(testP.instanceList.get(j), 
						testP.goldStructureList.get(j), pred) < 0.01) {
					correct++;
				}
			}
			System.out.println("Accuracy : "+ (correct*1.0/testP.size()));
		}
	}
	
	
	
	@CommandDescription(description = "Trains classifier")
	public static void testQuantityPairClassifier() throws Exception
	{
//		List<Problem> problemList = Reader.readAI2ProblemsFromFile(Params.ai2MathFile);
		List<Problem> problemList = Reader.readIllinoisProblemsFromFile(Params.illinoisMathFile);
		System.out.println("Problems read : "+problemList.size());
		List<Problem> train = new ArrayList<Problem>();
		List<Problem> test = new ArrayList<Problem>();
		int cv = 3;
		int l=problemList.size();
		Collections.shuffle(problemList);
		for(int i=0;i<cv; i++){
			train.clear();
			test.clear();
			for(int j=0;j<cv;j++){
				if(i==j)
					test.addAll(problemList.subList(j*l/cv, (j+1)*l/cv));
				else
					train.addAll(problemList.subList(j*l/cv, (j+1)*l/cv));
			}
			SLProblem trainP = QuantPairInfSolver.getSP(train);
			SLProblem testP = QuantPairInfSolver.getSP(test);
			SLModel model = new SLModel();
			Lexiconer lm = new Lexiconer();
			lm.setAllowNewFeatures(true);
			model.lm = lm;
			FeatureManager fg = new FeatureManager(lm);
			model.featureGenerator = fg;
			model.infSolver = new QuantPairInfSolver(fg);
			SLParameters para = new SLParameters();
			para.loadConfigFile(Params.spConfigFile);
			Learner learner = LearnerFactory.getLearner(model.infSolver, fg, para);
			System.out.println("Training on "+trainP.size()+" examples");
			model.wv = learner.train(trainP);
			System.out.println("Done");
			lm.setAllowNewFeatures(false);
			int correct = 0;
			for(int j=0; j<testP.size(); ++j) {
				IStructure pred = model.infSolver.getBestStructure(model.wv, 
						testP.instanceList.get(j));
				if(model.infSolver.getLoss(testP.instanceList.get(j), 
						testP.goldStructureList.get(j), pred) < 0.01) {
					correct++;
				}
			}
			System.out.println("Accuracy : "+ (correct*1.0/testP.size()));
		}
	}
	
	
	@CommandDescription(description = "Trains and tests classifier")
	public static void testAllQuantity() throws Exception
	{
//		List<Problem> problemList = Reader.readAI2ProblemsFromFile(Params.ai2MathFile);
		List<Problem> problemList = Reader.readIllinoisProblemsFromFile(Params.illinoisMathFile);
		List<Problem> train = new ArrayList<Problem>();
		List<Problem> test = new ArrayList<Problem>();
		int cv = 5;
//		int l=problemList.size();
//		Collections.shuffle(problemList);
		for(int i=0; i<cv; i++){
			train.clear();
			test.clear();
			// For random split
//			for(int j=0;j<cv;j++){
//				if(i==j)
//					test.addAll(problemList.subList(j*l/cv, (j+1)*l/cv));
//				else
//					train.addAll(problemList.subList(j*l/cv, (j+1)*l/cv));
//			}
			for(int j=0; j<cv; ++j) {
				// Folds from file
				String str = FileUtils.readFileToString(new File(
						"data/illinois/fold"+j+".txt"));
				String strArr[] = str.split("\n");
				Set<Integer> indices = new HashSet<>();
				for(int k=0; k<strArr.length; ++k) {
					int index = Integer.parseInt(strArr[k].trim());
					indices.add(index);
				}
				for(Problem prob : problemList) {
					if(indices.contains(prob.id)) {
						if(j==i) {
							test.add(prob);
						} else {
							train.add(prob);
						}
					}
				}
			}
			
			System.out.println("Extracting data");
			SLProblem trainOrder = OrderInfSolver.getSP(train);
			SLProblem trainOperation = OperationInfSolver.getSP(train);
			SLProblem trainQuantPair = QuantPairInfSolver.getSP(train);
			
			// Training order classifier
			System.out.println("Training order classifier");
			SLModel modelOrder = new SLModel();
			Lexiconer lm = new Lexiconer();
			lm.setAllowNewFeatures(true);
			modelOrder.lm = lm;
			FeatureManager fg = new FeatureManager(lm);
			modelOrder.featureGenerator = fg;
			modelOrder.infSolver = new OrderInfSolver(fg);
			SLParameters para = new SLParameters();
			para.loadConfigFile(Params.spConfigFile);
			Learner learner = LearnerFactory.getLearner(modelOrder.infSolver, fg, para);
			modelOrder.wv = learner.train(trainOrder);
			lm.setAllowNewFeatures(false);
			
			// Training operation classifier
			System.out.println("Training operation classifier");
			SLModel modelOperation = new SLModel();
			lm = new Lexiconer();
			lm.setAllowNewFeatures(true);
			modelOperation.lm = lm;
			fg = new FeatureManager(lm);
			modelOperation.featureGenerator = fg;
			modelOperation.infSolver = new OperationInfSolver(fg);
			para = new SLParameters();
			para.loadConfigFile(Params.spConfigFile);
			learner = LearnerFactory.getLearner(modelOperation.infSolver, fg, para);
			modelOperation.wv = learner.train(trainOperation);
			lm.setAllowNewFeatures(false);
			
			// Training quantity pair classifier
			System.out.println("Training quantity pair classifier");
			SLModel modelQuantPair = new SLModel();
			lm = new Lexiconer();
			lm.setAllowNewFeatures(true);
			modelQuantPair.lm = lm;
			fg = new FeatureManager(lm);
			modelQuantPair.featureGenerator = fg;
			modelQuantPair.infSolver = new QuantPairInfSolver(fg);
			para = new SLParameters();
			para.loadConfigFile(Params.spConfigFile);
			learner = LearnerFactory.getLearner(modelQuantPair.infSolver, fg, para);
			modelQuantPair.wv = learner.train(trainQuantPair);
			lm.setAllowNewFeatures(false);

			System.out.println("Testing");
			int correct = 0;
			for(int j=0; j<test.size(); ++j) {
				SLProblem testOrder = OrderInfSolver.getSP(
						Arrays.asList(test.get(j)));
				SLProblem testOperation = OperationInfSolver.getSP(
						Arrays.asList(test.get(j)));
				SLProblem testQuantPair = QuantPairInfSolver.getSP(
						Arrays.asList(test.get(j)));
				IStructure pred;
				boolean c1 = false, c2 = false, c3 = false;
				if(testQuantPair.size() == 0) {
					c1 = true;
				} else {
					pred = modelQuantPair.infSolver.getBestStructure(
							modelQuantPair.wv, 
							testQuantPair.instanceList.get(0));
					if(modelQuantPair.infSolver.getLoss(
							testQuantPair.instanceList.get(0), 
							testQuantPair.goldStructureList.get(0), pred) < 0.01) {
						c1 = true;
					}
				}
				pred = modelOperation.infSolver.getBestStructure(
						modelOperation.wv, 
						testOperation.instanceList.get(0));
				if(modelOperation.infSolver.getLoss(
						testOperation.instanceList.get(0), 
						testOperation.goldStructureList.get(0), pred) < 0.01) {
					c2 = true;
				}
				pred = modelOrder.infSolver.getBestStructure(
						modelOrder.wv, 
						testOrder.instanceList.get(0));
				if(modelOrder.infSolver.getLoss(
						testOrder.instanceList.get(0), 
						testOrder.goldStructureList.get(0), pred) < 0.01) {
					c3 = true;
				}
				if(c1 && c2 && c3) {
					correct++;
				}
			}
			System.out.println("Accuracy : "+ (correct*1.0/test.size()));
		}
	}
	
	@CommandDescription(description = "Trains and tests classifier")
	public static void trainOnOneTestOnOther() throws Exception
	{
		List<Problem> train = Reader.readAI2ProblemsFromFile(Params.ai2MathFile);
		List<Problem> test = Reader.readIllinoisProblemsFromFile(Params.illinoisMathFile);
		SLProblem trainOrder = OrderInfSolver.getSP(train);
		SLProblem trainOperation = OperationInfSolver.getSP(train);
		SLProblem trainQuantPair = QuantPairInfSolver.getSP(train);
		
		// Training order classifier
		SLModel modelOrder = new SLModel();
		Lexiconer lm = new Lexiconer();
		lm.setAllowNewFeatures(true);
		modelOrder.lm = lm;
		FeatureManager fg = new FeatureManager(lm);
		modelOrder.featureGenerator = fg;
		modelOrder.infSolver = new OrderInfSolver(fg);
		SLParameters para = new SLParameters();
		para.loadConfigFile(Params.spConfigFile);
		Learner learner = LearnerFactory.getLearner(modelOrder.infSolver, fg, para);
		modelOrder.wv = learner.train(trainOrder);
		lm.setAllowNewFeatures(false);
		
		// Training operation classifier
		SLModel modelOperation = new SLModel();
		lm = new Lexiconer();
		lm.setAllowNewFeatures(true);
		modelOperation.lm = lm;
		fg = new FeatureManager(lm);
		modelOperation.featureGenerator = fg;
		modelOperation.infSolver = new OperationInfSolver(fg);
		para = new SLParameters();
		para.loadConfigFile(Params.spConfigFile);
		learner = LearnerFactory.getLearner(modelOperation.infSolver, fg, para);
		modelOperation.wv = learner.train(trainOperation);
		lm.setAllowNewFeatures(false);
		
		// Training quantity pair classifier
		SLModel modelQuantPair = new SLModel();
		lm = new Lexiconer();
		lm.setAllowNewFeatures(true);
		modelQuantPair.lm = lm;
		fg = new FeatureManager(lm);
		modelQuantPair.featureGenerator = fg;
		modelQuantPair.infSolver = new QuantPairInfSolver(fg);
		para = new SLParameters();
		para.loadConfigFile(Params.spConfigFile);
		learner = LearnerFactory.getLearner(modelQuantPair.infSolver, fg, para);
		modelQuantPair.wv = learner.train(trainQuantPair);
		lm.setAllowNewFeatures(false);
					
		int correct = 0;
		for(int j=0; j<test.size(); ++j) {
			SLProblem testOrder = OrderInfSolver.getSP(
					Arrays.asList(test.get(j)));
			SLProblem testOperation = OperationInfSolver.getSP(
					Arrays.asList(test.get(j)));
			SLProblem testQuantPair = QuantPairInfSolver.getSP(
					Arrays.asList(test.get(j)));
			IStructure pred;
			boolean c1 = false, c2 = false, c3 = false;
			if(testQuantPair.size() == 0) {
				c1 = true;
			} else {
				pred = modelQuantPair.infSolver.getBestStructure(
						modelQuantPair.wv, 
						testQuantPair.instanceList.get(0));
				if(modelQuantPair.infSolver.getLoss(
						testQuantPair.instanceList.get(0), 
						testQuantPair.goldStructureList.get(0), pred) < 0.01) {
					c1 = true;
				}
			}
			pred = modelOperation.infSolver.getBestStructure(
					modelOperation.wv, 
					testOperation.instanceList.get(0));
			if(modelOperation.infSolver.getLoss(
					testOperation.instanceList.get(0), 
					testOperation.goldStructureList.get(0), pred) < 0.01) {
				c2 = true;
			}
			pred = modelOrder.infSolver.getBestStructure(
					modelOrder.wv, 
					testOrder.instanceList.get(0));
			if(modelOrder.infSolver.getLoss(
					testOrder.instanceList.get(0), 
					testOrder.goldStructureList.get(0), pred) < 0.01) {
				c3 = true;
			}
			if(c1 && c2 && c3) {
				correct++;
			}
		}
		System.out.println("Accuracy : "+ (correct*1.0/test.size()));
	}
	
}