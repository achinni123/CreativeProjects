package baseline;

import edu.illinois.cs.cogcomp.sl.core.IStructure;

public class OperationY implements IStructure {
	
	public String op;
	
	public OperationY(String op) {
		this.op = op;
	}
	
	@Override
	public String toString() {
		return op;
	}
}
