package baseline;

import edu.illinois.cs.cogcomp.core.datastructures.IntPair;
import edu.illinois.cs.cogcomp.sl.core.IStructure;

public class OrderY implements IStructure {
	
	public IntPair ip;
	
	public OrderY(IntPair ip) {
		this.ip = ip;
	}
	
	@Override
	public String toString() {
		return ip+"";
	}

}
