package baseline;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import structure.Node;
import structure.Problem;
import edu.illinois.cs.cogcomp.core.datastructures.IntPair;
import edu.illinois.cs.cogcomp.sl.core.AbstractInferenceSolver;
import edu.illinois.cs.cogcomp.sl.core.IInstance;
import edu.illinois.cs.cogcomp.sl.core.IStructure;
import edu.illinois.cs.cogcomp.sl.core.SLProblem;
import edu.illinois.cs.cogcomp.sl.util.WeightVector;

public class QuantPairInfSolver extends AbstractInferenceSolver 
implements Serializable {

	private static final long serialVersionUID = 5253748728743334706L;
	private FeatureManager featGen;
	
	public QuantPairInfSolver(FeatureManager featGen) throws Exception {
		this.featGen = featGen;
	}
	
	public static SLProblem getSP(List<Problem> problemList) throws Exception{
		SLProblem problem = new SLProblem();
		for(Problem prob:problemList){
			if(prob.quantities.size()>2){
				List<Integer> relevant = new ArrayList<>();
				for(Node node : prob.expr.getLeaves()) {
					relevant.add(node.quantIndex);
				}
				if(relevant.size() != 2) {
					System.out.println("Something wrong"+prob);
				}
				ProbX x = new ProbX(prob, null);
				QuantPairY y = null;
				if(relevant.get(0) < relevant.get(1)) {
					y = new QuantPairY(new IntPair(
						relevant.get(0), relevant.get(1)));
				} else {
					y = new QuantPairY(new IntPair(
							relevant.get(1), relevant.get(0)));
				}
				problem.addExample(x, y);
			}
		}
		return problem;
	}

	@Override
	public IStructure getBestStructure(WeightVector weight, IInstance ins)
			throws Exception {
		return getLossAugmentedBestStructure(weight, ins, null);
	}


	@Override
	public IStructure getLossAugmentedBestStructure(WeightVector weight,
			IInstance ins, IStructure goldStructure) throws Exception {
//		System.out.println("InfSolver called");
		QuantPairY y = new QuantPairY(new IntPair(0, 1));
		double bestScore = weight.dotProduct(featGen.getFeatureVector(ins, y));
		QuantPairY bestY = y;
		
		y = new QuantPairY(new IntPair(0, 2));
		if(bestScore < weight.dotProduct(featGen.getFeatureVector(ins, y))) {
			bestScore = weight.dotProduct(featGen.getFeatureVector(ins, y));
			bestY = y;
		}
		
		y = new QuantPairY(new IntPair(1, 2));
		if(bestScore < weight.dotProduct(featGen.getFeatureVector(ins, y))) {
			bestScore = weight.dotProduct(featGen.getFeatureVector(ins, y));
			bestY = y;
		}
//		System.out.println("Returned");
		return bestY;
	}


	@Override
	public float getLoss(IInstance ins, IStructure gold, IStructure pred) {
		if(gold.toString().equals(pred.toString())) {
			return 0.0f;
		}
		return 1.0f;
	}
	
	
}
