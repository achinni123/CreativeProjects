package reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Relation;
import edu.illinois.cs.cogcomp.quant.driver.QuantSpan;
import structure.Node;
import structure.Problem;
import utils.KushmanFormat;
import utils.Params;
import utils.Tools;

public class Reader {
	
	public static List<Problem> readIllinoisProblemsFromFile(String xmlFile) 
			throws Exception {
		List<Problem> problemList = new ArrayList<Problem>();
		BufferedReader br = new BufferedReader(new FileReader(xmlFile));
		while(true){
			String str = br.readLine();
			if(str==null)
				break;
			br.readLine();
			String question = br.readLine();
			br.readLine();br.readLine();
			br.readLine();
			br.readLine();br.readLine();
			br.readLine();
			br.readLine();br.readLine();
			String str1 = br.readLine();
			double val = Double.parseDouble(str1);
			br.readLine();br.readLine();
			br.readLine();
			br.readLine();br.readLine();
			Problem prob = new Problem(problemList.size(), question, val);
			prob.extractQuantities();
			prob.getRelevantQuantityIndicesAndOperationForIllinois();
			prob.extractAnnotations();
			problemList.add(prob);
		}
		br.close();
		return problemList;
	}
	
	public static List<Problem> readAI2ProblemsFromFile(String ai2File) 
			throws Exception {
		List<Problem> problemList = new ArrayList<Problem>();
		BufferedReader br = new BufferedReader(new FileReader(ai2File));
		String question = "";
		while(true){
			String str = br.readLine();
			if(str==null) {
				break;
			}
			int index = str.indexOf("?");
			if(index == -1) {
				question += str.trim() + " ";
			} else {
				Double val = null;
				String strArr[] = str.substring(index+1).split(" ");
				for(String v : strArr) {
					if(NumberUtils.isNumber(v.trim())) {
						val = Double.parseDouble(v.trim());
						break;
					}
				}
				question += str.substring(0, index+1);
				Problem prob = new Problem(problemList.size(), question, val);
				prob.extractQuantities();
				prob.getRelevantQuantityIndicesAndOperationForAI2();
				prob.extractAnnotations();
				problemList.add(prob);
				question = "";
			}
		}
		br.close();
		return problemList;
	}
	
	public static List<List<Problem>> readCCProblemsFromDir(String ccDir) 
			throws Exception {
		List<List<Problem>> problemList = new ArrayList<List<Problem>>();
		List<String> fileNames = Arrays.asList("addsub", "subadd",
				"addmul", "adddiv", "submul", "subdiv");
		for(String file : fileNames) {
			problemList.add(readCCProblemsFromFile(ccDir+file+".txt"));	
		}
		return problemList;
	}
	
	public static List<Problem> readCCProblemsFromFile(String ccFile) 
			throws Exception {
		List<Problem> problemList = new ArrayList<Problem>();
		BufferedReader br = new BufferedReader(new FileReader(ccFile));
		String question = null;
		List<Double> answers = new ArrayList<>();
		List<String> questions = new ArrayList<>();
		String str = null;
		for(int i=0; i<10; ++i) {
			for(int j=0; j<10; ++j) {
				str = br.readLine().trim();
				if(str.contains(".")) {
					str = str.split("\\.")[1].trim();
				}
				answers.add(Double.parseDouble(str));
			}
			str = br.readLine();
			for(int j=0; j<10; ++j) {
				question = "";
				while(true){
					str = br.readLine();
					int index = str.indexOf(")");
					if(index >= 0) {
						question += str.substring(index+1) + " ";
					} else {
						question += str + " ";
					}
					index = str.indexOf("?");
					if(index >= 0) break;
				}
				questions.add(question);
			}
			str = br.readLine();
		}
		br.close();
		assert questions.size() == answers.size();
		for(int i=0; i<questions.size(); ++i) {
			Problem prob = new Problem(problemList.size(), questions.get(i), answers.get(i));
			prob.extractQuantities();
			prob.getRelevantQuantityIndicesAndOperationForCC(ccFile);
			prob.extractAnnotations();
			problemList.add(prob);
		}
		return problemList;
	}
	
	public static void printIrrelevantStuffInCC() throws Exception {
		List<List<Problem>> probs = readCCProblemsFromDir(Params.ccDir);
		for(List<Problem> probss : probs) {
			for(Problem prob : probss) {
				for(int i=0; i<prob.quantities.size(); ++i) {
					boolean allow = true;
					for(Node leaf : prob.expr.getLeaves()) {
						if(leaf.quantIndex == i) {
							allow = false;
						}
					}
					if(allow) {
						System.out.println("Prob : "+prob.question);
						System.out.println("Quantity : "+i);
						System.out.println();
					}
				}
			}
		}
	}
	
	public static List<Problem> readProblemsFromJson(String dir) throws Exception {
		String json = FileUtils.readFileToString(new File(dir+"questions.json"));
		List<KushmanFormat> kushmanProbs = new Gson().fromJson(json, 
				new TypeToken<List<KushmanFormat>>(){}.getType());
		List<Problem> problemList = new ArrayList<>();
		for(KushmanFormat kushmanProb : kushmanProbs) {
			Problem prob = new Problem(kushmanProb.iIndex, kushmanProb.sQuestion, 
					kushmanProb.lSolutions.get(0));
			prob.extractQuantities();
			prob.expr = Node.parseNode(kushmanProb.lEquations.get(0));
			assert prob.expr.getLeaves().size() == kushmanProb.lAlignments.size();
			for(int j=0; j<prob.expr.getLeaves().size(); ++j) {
				Node node = prob.expr.getLeaves().get(j);
				for(int i=0; i<prob.quantities.size(); ++i) {
					QuantSpan qs = prob.quantities.get(i);
					if(kushmanProb.lAlignments.get(j) >= qs.start && 
							kushmanProb.lAlignments.get(j) < qs.end) {
						node.qs = qs;
						node.quantIndex = i;
						break;
					}
				}
			}
			prob.extractAnnotations();
			problemList.add(prob);
		}
		return problemList;
	}
	
	public static void main(String args[]) throws Exception {
		List<Problem> probs = Reader.readProblemsFromJson(Params.ai2Dir);
		probs.addAll(Reader.readProblemsFromJson(Params.ilDir));
		probs.addAll(Reader.readProblemsFromJson(Params.ccDir));
		for(Problem prob : probs) {
			System.out.print(prob.id+" : "+prob.question);
			System.out.print("Dependency : ");
			for(Constituent cons : prob.dependency) {
				for(Relation r : cons.getOutgoingRelations()) {
					System.out.print(r.getSource()+" --> "+r.getTarget() +" : ");
				}
			}
			System.out.println();
			System.out.println();
		}
		Tools.pipeline.closeCache();
	}

}
