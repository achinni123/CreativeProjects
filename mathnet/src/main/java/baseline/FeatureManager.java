package baseline;

import java.io.Serializable;
import java.util.*;

import javatools.parsers.PlingStemmer;
import structure.Problem;
import utils.FeatGen;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Sentence;
import edu.illinois.cs.cogcomp.quant.driver.QuantSpan;
import edu.illinois.cs.cogcomp.sl.core.AbstractFeatureGenerator;
import edu.illinois.cs.cogcomp.sl.core.IInstance;
import edu.illinois.cs.cogcomp.sl.core.IStructure;
import edu.illinois.cs.cogcomp.sl.util.IFeatureVector;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

public class FeatureManager extends AbstractFeatureGenerator 
implements Serializable{

	private static final long serialVersionUID = -5902462551801564955L;
	public Lexiconer lm = null;
	
	public FeatureManager(Lexiconer lm) {
		this.lm = lm;
	}

	public List<String> addWordFeatures(ProbX x, QuantSpan s1, QuantSpan s2) {
		List<String> features = new ArrayList<String>();
		Problem prob = x.prob;
		int tokenpos1 = prob.ta.getTokenIdFromCharacterOffset(s1.start);
		int tokenpos2 = prob.ta.getTokenIdFromCharacterOffset(s1.end);
		for(int i=tokenpos1-1;i>=Math.max(0,tokenpos1-3);i--){
			features.add("1_Word_"+(i-tokenpos1)+"_"+prob.stems.get(i));
		}
		for(int i=tokenpos2+1;i<=Math.min(tokenpos2+3,prob.ta.size()-1);i++){
			features.add("1_Word_"+(i-tokenpos2)+"_"+prob.stems.get(i));
		}
		tokenpos1 = prob.ta.getTokenIdFromCharacterOffset(s2.start);
		tokenpos2 = prob.ta.getTokenIdFromCharacterOffset(s2.end);
		for(int i=tokenpos1-1;i>=Math.max(0,tokenpos1-3);i--){
			features.add("2_Word_"+(i-tokenpos1)+"_"+prob.stems.get(i));
		}

		for(int i=tokenpos2+1;i<=Math.min(tokenpos2+3,prob.ta.size()-1);i++){
			features.add("2_Word_"+(i-tokenpos2)+"_"+prob.stems.get(i));
		}
		return features;
		
	}
	
	public List<String> addPOSFeatures(ProbX x, QuantSpan s1, QuantSpan s2) {
		List<String> features = new ArrayList<String>();
		Problem prob = x.prob;
		int tokenpos1 = prob.ta.getTokenIdFromCharacterOffset(s1.start);
		int tokenpos2 = prob.ta.getTokenIdFromCharacterOffset(s2.end);
		for(int i=tokenpos1-1;i>=Math.max(0,tokenpos1-3);i--){
			features.add("1_POS_"+(i-tokenpos1)+"_"+prob.posTags.get(i).getLabel());
		}
		for(int i=tokenpos2+1;i<=Math.min(tokenpos2+3,prob.ta.size()-1);i++){
			features.add("1_POS_"+(i-tokenpos2)+"_"+prob.posTags.get(i).getLabel());
		}
		tokenpos1 = prob.ta.getTokenIdFromCharacterOffset(s1.start);
		tokenpos2 = prob.ta.getTokenIdFromCharacterOffset(s2.end);
		for(int i=tokenpos1-1;i>=Math.max(0,tokenpos1-3);i--){
			features.add("2_POS_"+(i-tokenpos1)+"_"+prob.posTags.get(i).getLabel());
		}
		for(int i=tokenpos2+1;i<=Math.min(tokenpos2+3,prob.ta.size()-1);i++){
			features.add("2_POS_"+(i-tokenpos2)+"_"+prob.posTags.get(i).getLabel());
		}
		return features;
	}
	
	
	public List<String> addLastQuestionFeatures(ProbX x, QuantSpan s1, QuantSpan s2) {
		List<String> features = new ArrayList<String>();		
		Problem prob = x.prob;
		Sentence sent = prob.ta.getSentence(prob.ta.getNumberOfSentences()-1);
		int start = prob.ta.getTokenIdFromCharacterOffset(sent.getStartSpan());
		int end = prob.ta.getTokenIdFromCharacterOffset(sent.getEndSpan());
		for(int i=start;i<end;i++){
			features.add("Q_WORD_"+prob.stems.get(i));
		}
		for(int i=start;i<end-1;i++){
			features.add("Q_WORD_"+prob.stems.get(i)+"_"+prob.stems.get(i+1));
			features.add("Q_POS_"+prob.posTags.get(i).getLabel()+"_"+prob.posTags.get(i+1).getLabel());
			features.add("Q_POSWORD_"+prob.posTags.get(i).getLabel()+"_"+prob.stems.get(i+1));
			features.add("Q_WORDPOS_"+prob.stems.get(i)+"_"+prob.posTags.get(i+1).getLabel());
		}
		return features;
	}
	
	public List<String> addQuantUnitFeatures(ProbX x, int s1, int s2) {
		List<String> features = new ArrayList<String>();
		String unit1 = x.schema.quantSchemas.get(s1).unit;
		String unit2 = x.schema.quantSchemas.get(s2).unit;
		if(PlingStemmer.stem(unit1).equals(PlingStemmer.stem(unit2))){
			features.add("Units same");
		}
		Sentence sent = x.prob.ta.getSentence(x.prob.ta.getNumberOfSentences()-1);
		if(sent.getText().contains(unit1)||sent.getText().contains(PlingStemmer.stem(unit1))){
			features.add("1_Unit_In_Question");
		}
		if(sent.getText().contains(unit2) ||sent.getText().contains(PlingStemmer.stem(unit2))){
			features.add("2_Unit_In_Question");
		}
		return features;
	}
	
	public IFeatureVector getFeatureVector(IInstance x, IStructure y) {
		ProbX prob = (ProbX) x;
		QuantSpan q1 = null, q2 = null;
		int index1, index2;
		if(y instanceof QuantPairY) {
			index1 = ((QuantPairY)y).ip.getFirst();
			index2 = ((QuantPairY)y).ip.getSecond();
		} else if(y instanceof OrderY) {
			index1 = ((OrderY)y).ip.getFirst();
			index2 = ((OrderY)y).ip.getSecond();
		} else {
			index1 = prob.ip.getFirst();
			index2 = prob.ip.getSecond();
		}
		q1 = prob.prob.quantities.get(index1);
		q2 = prob.prob.quantities.get(index2);
		List<String> features = new ArrayList<String>();
		features.addAll(addWordFeatures(prob, q1, q2));
		features.addAll(addPOSFeatures(prob, q1, q2));
		features.addAll(addLastQuestionFeatures(prob, q1, q2));
		features.addAll(addQuantUnitFeatures(prob, index1, index2));
		if(y instanceof OperationY) {
			for(int i=0; i<features.size(); ++i) {
				features.set(i, ((OperationY)y).op+"_"+features.get(i));
			}
		}
		return FeatGen.getFeatureVectorFromListString(features, lm);
	}
	
}

