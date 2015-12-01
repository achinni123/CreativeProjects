package utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import reader.Reader;
import structure.Node;
import structure.Problem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonHelper {
	
	public static void generateAI2ProblemsInKushmanFormat() throws Exception {
		List<Problem> probs = Reader.readAI2ProblemsFromFile(Params.ai2MathFile);
		List<KushmanFormat> kfList = new ArrayList<>();
		for(int i=0; i<probs.size(); ++i) {
			Problem prob = probs.get(i);
			KushmanFormat kf = new KushmanFormat();
			kf.iIndex = i;
			kf.sQuestion = prob.question;
			kf.lAlignments = new ArrayList<>();
			for(Node node : prob.expr.getLeaves()) {
				kf.lAlignments.add(node.qs.start);
			}
			kf.lEquations = new ArrayList<>();
			kf.lEquations.add("X="+prob.expr.toString());
			kf.lSolutions = new ArrayList<>();
			kf.lSolutions.add(prob.answer);
			kfList.add(kf);
		}
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
	  // convert java object to JSON format,  
	  // and returned as JSON formatted string  
	  String json = gson.toJson(kfList);  
	    
	  try {  
	   //write converted json data to a file named "CountryGSON.json"  
	   FileWriter writer = new FileWriter("data/ai2Kushman.json");  
	   writer.write(json);  
	   writer.close();  
	    
	  } catch (IOException e) {  
	   e.printStackTrace();  
	  }  
	}
	
	public static void generateILProblemsInKushmanFormat() throws Exception {
		List<Problem> probs = Reader.readIllinoisProblemsFromFile(Params.illinoisMathFile);
		List<KushmanFormat> kfList = new ArrayList<>();
		Set<Integer> allFolds = new HashSet<>();
		for(int i=0; i<5; ++i) {
			for(Problem prob : Folds.getDataSplitForIL(i).getSecond()) {
				allFolds.add(prob.id);
			}
		}
		for(int i=0; i<probs.size(); ++i) {
			Problem prob = probs.get(i);
			KushmanFormat kf = new KushmanFormat();
			kf.iIndex = i;
			if(!allFolds.contains(kf.iIndex)) continue;
			kf.sQuestion = prob.question;
			kf.lAlignments = new ArrayList<>();
			for(Node node : prob.expr.getLeaves()) {
				kf.lAlignments.add(node.qs.start);
			}
			kf.lEquations = new ArrayList<>();
			kf.lEquations.add("X="+prob.expr.toString());
			kf.lSolutions = new ArrayList<>();
			kf.lSolutions.add(prob.answer);
			kfList.add(kf);
		}
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
	  // convert java object to JSON format,  
	  // and returned as JSON formatted string  
	  String json = gson.toJson(kfList);  
	    
	  try {  
	   //write converted json data to a file named "CountryGSON.json"  
	   FileWriter writer = new FileWriter("data/illinoisKushman.json");  
	   writer.write(json);  
	   writer.close();  
	    
	  } catch (IOException e) {  
	   e.printStackTrace();  
	  }  
	}
	
	public static void generateCCProblemsInKushmanFormat() throws Exception {
		List<List<Problem>> probs = Reader.readCCProblemsFromDir("data/other/");
		List<KushmanFormat> kfList = new ArrayList<>();
		int count = 0;
		for(int i=0; i<probs.size(); ++i) {
			List<Integer> fold = new ArrayList<>();
			for(int j=0; j<probs.get(i).size(); ++j) {
				Problem prob = probs.get(i).get(j);
				KushmanFormat kf = new KushmanFormat();
				fold.add(count);
				kf.iIndex = count;
				count++;
				kf.sQuestion = prob.question;
				kf.lAlignments = new ArrayList<>();
				for(Node node : prob.expr.getLeaves()) {
					kf.lAlignments.add(node.qs.start);
				}
				kf.lEquations = new ArrayList<>();
				kf.lEquations.add("X="+prob.expr.toString());
				kf.lSolutions = new ArrayList<>();
				kf.lSolutions.add(prob.answer);
				kfList.add(kf);
			}
//			FileWriter writer = new FileWriter("data/ccKushmanFold"+i);
//			for(Integer j : fold) {
//				writer.write(j+"\n");
//			}
//			writer.close();
		}
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
	  // convert java object to JSON format,  
	  // and returned as JSON formatted string  
	  String json = gson.toJson(kfList);  
	    
	  try {  
	   //write converted json data to a file named "CountryGSON.json"  
	   FileWriter writer = new FileWriter("data/ccKushman.json");  
	   writer.write(json);  
	   writer.close();  
	    
	  } catch (IOException e) {  
	   e.printStackTrace();  
	  }  
	}
	
	public static void main(String args[]) throws Exception {
//		generateAI2ProblemsInKushmanFormat();
		generateILProblemsInKushmanFormat();
//		generateCCProblemsInKushmanFormat();
		Tools.pipeline.closeCache();
	}
	

}
