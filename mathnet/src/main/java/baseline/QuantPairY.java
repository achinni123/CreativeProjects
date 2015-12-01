package baseline;

import edu.illinois.cs.cogcomp.core.datastructures.IntPair;
import edu.illinois.cs.cogcomp.sl.core.IStructure;

public class QuantPairY implements IStructure {
	
	public IntPair ip;
	
	public QuantPairY(IntPair ip) {
		this.ip = ip;
	}
	
	@Override
	public String toString() {
		return ip+"";
	}

}
