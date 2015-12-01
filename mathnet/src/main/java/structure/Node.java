package structure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import utils.Tools;
import edu.illinois.cs.cogcomp.quant.driver.QuantSpan;

public class Node implements Serializable {
	
	private static final long serialVersionUID = -1127009463482561785L;
	public String label;
	public List<Node> children;
	public int quantIndex;
	public QuantSpan qs;
	public double val;
	
	public Node() {
		children = new ArrayList<>();
	}
	
	// For leaves
	public Node(int quantindex, QuantSpan qs, String label) {
		this();
		this.quantIndex = quantindex;
		this.qs = qs;
		this.label = label;
	}
	
	// For internal nodes
	public Node(String label, List<Node> children) {
		this();
		this.label = label;
		this.children = children;
	}
	
	public Node(Node other) {
		this();
		this.quantIndex = other.quantIndex;
		this.qs = other.qs;
		for(Node child : other.children) {
			this.children.add(new Node(child));			
		}
	}
	
	@Override
	public String toString() {
		if(children.size() == 0) return "" + Tools.getValue(qs);
		String labelSym = null;
		switch(label) {
		case "ADD" : labelSym = "+"; break;
		case "SUB" : labelSym = "-"; break;
		case "MUL" : labelSym = "*"; break;
		case "DIV" : labelSym = "/"; break;
		}
		return "("+children.get(0).toString() + labelSym + children.get(1).toString()+")";
	}
	
	public List<Node> getLeaves() {
		List<Node> leaves = new ArrayList<Node>();
		if(children.size() == 0) {
			leaves.add(this);
		} else {
			leaves.addAll(children.get(0).getLeaves());
			leaves.addAll(children.get(1).getLeaves());
		}
		return leaves;
	}
	
	public List<Node> getAllSubNodes() {
		List<Node> all = new ArrayList<Node>();
		all.add(this);
		if(children.size() == 2) {
			all.addAll(children.get(0).getAllSubNodes());
			all.addAll(children.get(1).getAllSubNodes());
		}
		return all;
	}
	
	public boolean hasLeaf(int index) {
		if(label.equals("NUM")) {
			if(quantIndex == index) return true;
			else return false;
		} else {
			return (children.get(0).hasLeaf(index) || 
					children.get(1).hasLeaf(index));
		}
	}
	
	public String findLabelofLCA(int i, int j) {
		for(Node node : getAllSubNodes()) {
			if(!node.label.equals("NUM") && node.children.get(0).hasLeaf(i)
					&& node.children.get(1).hasLeaf(j)) {
				return node.label;
			}
			if((node.label.equals("SUB") || node.label.equals("DIV")) 
					&& node.children.get(0).hasLeaf(j)
					&& node.children.get(1).hasLeaf(i)) {
				return node.label+"_REV";
			}
			if((node.label.equals("ADD") || node.label.equals("MUL")) 
					&& node.children.get(0).hasLeaf(j)
					&& node.children.get(1).hasLeaf(i)) {
				return node.label;
			}
		}
		return "NONE";
	}
	
	public boolean findRelevanceLabel(int i) {
		for(Node node : getLeaves()) {
			if(node.quantIndex == i) {
				return true;
			}
		}
		return false;
	}
	
	public double getValue() {
		if(label.equals("NUM")) return Tools.getValue(qs);
		if(label.equals("ADD")) return children.get(0).getValue() + 
				children.get(1).getValue();
		if(label.equals("SUB")) return children.get(0).getValue() - 
						children.get(1).getValue();
		if(label.equals("MUL")) return children.get(0).getValue() * 
				children.get(1).getValue();
		if(label.equals("DIV")) return children.get(0).getValue() / 
				children.get(1).getValue();
		return 0.0;
	}
	
	public static Node parseNode(String eqString) {
		eqString = eqString.trim();
//		System.out.println("EqString : "+eqString);
		int index = eqString.indexOf("=");
		if(index > 0) eqString = eqString.substring(index+1).trim();
		if(eqString.charAt(0)=='(' && eqString.charAt(eqString.length()-1)==')') {
			eqString = eqString.substring(1, eqString.length()-1).trim();
		}
		index = indexOfMathOp(eqString, Arrays.asList('+', '-', '*', '/'));
		Node node = new Node();
		if(index > 0) {
			if(eqString.charAt(index) == '+') node.label = "ADD";
			else if(eqString.charAt(index) == '-') node.label = "SUB";
			else if(eqString.charAt(index) == '*') node.label = "MUL";
			else if(eqString.charAt(index) == '/') node.label = "DIV";
			else node.label = "ISSUE";
			node.children.add(parseNode(eqString.substring(0, index)));
			node.children.add(parseNode(eqString.substring(index+1)));
			return node;
		} else {
			node.label = "NUM";
			node.val = Double.parseDouble(eqString.trim());
		}
		return node;
	}
	
	public static int indexOfMathOp(String equationString, List<Character> keys) {
		for(int index=0; index<equationString.length(); ++index) {
			if(keys.contains(equationString.charAt(index))) {
				int open = 0, close = 0;
				for(int i=index; i>=0; --i) {
					if(equationString.charAt(i) == ')') close++;
					if(equationString.charAt(i) == '(') open++;
				}
				if(open==close) {
					return index;
				}
			}
		}
		return -1;
	}
}
