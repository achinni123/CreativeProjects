package baseline;

import edu.illinois.cs.cogcomp.core.datastructures.IntPair;
import edu.illinois.cs.cogcomp.sl.core.IInstance;
import structure.Problem;
import structure.Schema;

public class ProbX implements IInstance {
	
	public Problem prob;
	public IntPair ip;
	public Schema schema;
	
	public ProbX(Problem prob, IntPair ip) {
		this.prob = prob;
		this.ip = ip;
		this.schema = new Schema(prob);
	}

}
