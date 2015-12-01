/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hmmtag;
import java.io.*;
import java.util.HashMap;
/**
 *
 * @author Shiva
 */


public class HmmPos {
    HashMap<String,Double> transition = new HashMap<String,Double>();
    HashMap<String,Double> emission = new HashMap<String,Double>();
    HashMap<String,Double> context = new HashMap<String,Double>();   
    HashMap<String,Double> word_count = new HashMap<String,Double>();
    Double num_words = 0.0;
    public static void main(String[] args) {
    	// TODO Auto-generated method stub
    	HmmPos tagger = new HmmPos();
    	tagger.hmmCalc();  // calculate hmm transition and observation probabilities	
        Double value = tagger.viterbiCalc();
        System.out.println("Error Rate: "+ value);
        System.out.println("Error Rate in %: "+ (value*100) +"%");
    }   
    public double modelProb(String type, String input){
        String read[] = input.split(" ");
        Double result;
        Double tagcount;
        Double bi_tagcount;
        Double tag_wordcount;
	if (type.equals("T")){
            bi_tagcount = transition.get(read[0]+" "+read[1]);
            tagcount = context.get(read[0]);            
            result = -Math.log((bi_tagcount+1)/(tagcount+context.size()));
	}
	else{
            if(emission.get(read[0]+" "+read[1]) == null){
                tagcount = context.get(read[0]);
                int n = word_count.size();
                result = -Math.log(1/(tagcount+n));                                               
            }
            else{
                tag_wordcount = emission.get(read[0]+" "+read[1]);
                tagcount = context.get(read[0]);   
                int n = word_count.size();
                result = -Math.log((tag_wordcount+1)/(tagcount+n));  // denom add x
            }
            
	}
	return result;
    }
    
    public Double viterbiCalc(){
        HashMap<String,String> best_edge = new HashMap<>();  //stores best edge -> which Qi transitioned to Qj -> while bactracking helpful
        HashMap<String,Double> best_score = new HashMap<>(); //stores best scores -> for selecting the best edge
    	Double score;
        Double errcount = 0.0;
        Double wordcounts = 0.0;
	try {
            File inputfile = new File("entest.txt");
            BufferedReader reader = new BufferedReader(new FileReader(inputfile));
            String line = reader.readLine();
            String wordtag[] = line.split("/"); 
            String word = wordtag[0];  
            String tag = wordtag[1];                    
            int length = 0;
            while(line!=null){
                String tempsent = "";    
                String sentence = "";
		if(word.equals("###")){                    
                    best_score.put(0+" "+tag, 0.0);   //start with ### 
                    best_edge.put(0+" "+tag, null);
                }
				
		line = reader.readLine();                
		if (line != null) {                    
                    wordtag = line.split("/");
                    word = wordtag[0];
                    tag = wordtag[1];
                    while(!word.equals("###")){   //fetching the sentence
                        tempsent = tempsent + line + " ";
                        line = reader.readLine();
                        if(line != null){
                            wordtag = line.split("/");
                            word = wordtag[0];
                            tag = wordtag[1];
                        }
                        else{
                            word = "###";
                            tag = "###";
                        }                        
                    }                    
                                    
                    int index = tempsent.lastIndexOf(" ");
                    sentence = tempsent.substring(0,index) + "" + tempsent.substring(index+1);                                    
                    String sent[] = sentence.split(" ");              
                    String words[] = new String[1];
                    length = sent.length;  // number of words in sentence                    
                    
                    String prev = null;
                    String next = null;
                    String last = null;
                    for(int i=0;i<=(length-1);i++){
                        words = sent[i].split("/");
                        for (HashMap.Entry<String,Double> prevpos: context.entrySet()){
                            prev = prevpos.getKey();
                            for (HashMap.Entry<String,Double> nextpos: context.entrySet()){
                                next = nextpos.getKey();
                                if((best_score.get(i+" "+prev) != null) && (transition.get(prev+" "+next) != null)){
        			    score = best_score.get(i+" "+prev) + (modelProb("T",prev+" "+next)) + (modelProb("E",next+" "+words[0]));			    	
                                    int c = i+1;                                    
                                    if((best_score.get(c+" "+next) == null) || (best_score.get(c+" "+next) > score)){ //storing the best edge & score
                                        best_score.put(c+" "+next,score);                        
                                        best_edge.put(c+" "+next,i+" "+prev);                                        
                                    }
                                }
                            }
                        }
                    }
                    for (HashMap.Entry<String,Double> lastpos: context.entrySet()){
                        last = lastpos.getKey();
                        if((best_score.get(length+" "+last) != null) && (transition.get(last+" "+"###") != null)){
                            score = best_score.get(length+" "+last) + (modelProb("T",last+" "+"###"));			    	
                            int k = length+1;
                            if((best_score.get(k+" "+"###") == null) || (best_score.get(k+" "+"###") > score)){
                                best_score.put(k+" "+"###",score);                        
                                best_edge.put(k+" "+"###",length+" "+last);
                            }
                        }
                    }
                    //calculating viterbi backkpointer
                    int k = length+1;   
                    wordcounts = wordcounts + length;                    
                    String next_edge = best_edge.get(k+" "+"###");
                    String checktag[] = new String[1];
                    String words1[] = new String[1];
                    while(!next_edge.equals(0+" "+"###")){
                        checktag = next_edge.split(" ");                        
                        words1 = sent[length-1].split("/");                        
                        if(!checktag[1].equals(words1[1])){
                            errcount++;
                        }   
                        length--;
                        next_edge = best_edge.get(next_edge);                        
                    }
                    //clearing the best edges before looping through next sentence
                    best_score.clear();
                    best_edge.clear();                    
                }
				
            }
            reader.close();
	}
	catch(Exception ex){
            ex.printStackTrace();
	}
        //calculating error rate
        Double error_rate = (errcount/wordcounts);
        return error_rate;
    }
    
    
    public void hmmCalc(){
        Double ashcount = 0.0;
	try {
            File inputfile = new File("entrain.txt");
            BufferedReader reader = new BufferedReader(new FileReader(inputfile));
            String line = reader.readLine();
            String wordtag[] = line.split("/"); 
            String word = wordtag[0];  
            String tag = wordtag[1];
            String previous = null;
            while(line != null){
		if (word.equals("###")){
                    ashcount++;                    
                    if (context.get(tag) == null){
                    context.put(tag,1.0);
                    }
                    else {
                        Double i = context.get(tag);
                        i++;
                        context.put(tag, i);
                    }
                    previous = tag;   // previous tag
		}				
		line = reader.readLine();
		// to avoid null pointer exception when last line read
		if (line == null) {
                    word =  "###";
		}
		else{
                    wordtag = line.split("/");
                    word = wordtag[0];
                    tag = wordtag[1];
                    word_count.put(word, 1.0);
                    num_words++;
		}
		while(!word.equals("###") && line != null){
		
                    if (context.get(tag) == null){
			context.put(tag,1.0);
                    }
                    else {
			Double i = context.get(tag);
			i++;
			context.put(tag, i);
                    }
                    if (transition.get(previous+" "+tag) == null){
			transition.put(previous+" "+tag, 1.0);
                    }
                    else{
			Double i = transition.get(previous+" "+tag);
			i++;
			transition.put(previous+" "+tag, i);
                    }				
                    if (emission.get(tag+" "+word) == null){
                    emission.put(tag+" "+word, 1.0);
                    }
                    else {
			Double i = emission.get(tag+" "+word);
                        i++;
			emission.put(tag+" "+word, i);
                    }
                    previous = tag;
                    line = reader.readLine();
                    wordtag = line.split("/");
                    word = wordtag[0];
                    tag = wordtag[1];
                    word_count.put(word, 1.0);
                    num_words++;
                }
                
		if (line != null){                    
                    if (transition.get(previous+" "+tag) == null){
			transition.put(previous+" "+tag, 1.0);
                    }
                    else{
			Double i = transition.get(previous+" "+tag);
			i++;
                        transition.put(previous+" "+tag, i);
                    }
		}		
            }
            num_words = num_words - ashcount + 1;
	//for ### even the last occurrence is getting counted -> discounting that -> assumed ### as <s> -> start symbol
            Double ilast = context.get("###");
            ilast--;
            context.put("###", ilast);
	
            reader.close();
	}
	catch(Exception ex){
            ex.printStackTrace();
	}
	
    }

}
