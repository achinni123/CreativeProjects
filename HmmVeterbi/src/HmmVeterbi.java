import java.io.*;
import java.util.HashMap;
public class HmmVeterbi {
	   HashMap<String,Double> stateTrans = new HashMap<String,Double>(); //would consider the transitions
	    HashMap<String,Double> stateEmit = new HashMap<String,Double>(); //would considers the emissions
	 
	    public static void printError(Double val){
	       System.out.println("The error rate for the given test set is : "+ String.format("%.2f",(val*100)) +" %");
	    	
	    }
	    public static void main(String[] args) {
	    	// TODO Auto-generated method stub
	    	HmmVeterbi posTag = new HmmVeterbi();
	    	posTag.val_hmm(); 
	        Double val = posTag.val_viterbi();
	        printError(val);
	   
	    }  
	 
	    public double probability(String para, String input){
	    	
	    	Double result,tagcount,bitagval,tagval;
	    	String asd[] = input.split(" ");
	      
	        
		if (para.equals("T")){
	            bitagval = stateTrans.get(asd[0]+" "+asd[1]);
	            tagcount = tags.get(asd[0]);            
	            result = -Math.log((bitagval+1)/(tagcount+tags.size()));
		}
		else{
	            if(stateEmit.get(asd[0]+" "+asd[1]) == null){
	            	 int size = count.size();
	                tagcount = tags.get(asd[0]);
	               
	                result = -Math.log(1/(tagcount+size));                                               
	            }
	            else{
	                tagval = stateEmit.get(asd[0]+" "+asd[1]);
	                int size = count.size();
	                tagcount = tags.get(asd[0]);   
	               
	                result = -Math.log((tagval+1)/(tagcount+size)); 
	            }
	            
		}
		return result;
	    }
       HashMap<String,Double> tags = new HashMap<String,Double>();      //would consider the tags
       
       
	    public void val_hmm(){
	        Double hashVal = 0.0;
		try {
	            File inputfile = new File("entrain.txt");
	            BufferedReader reader = new BufferedReader(new FileReader(inputfile));
	            String line = reader.readLine();
	            String words[] = line.split("/"); 
	            String word = words[0],tag = words[1],previous = null;  
	            while(line != null){
			if (word.equals("###")){
	                                    
	                    if (tags.get(tag) == null){
	                    tags.put(tag,1.0);
	                    }
	                    else {
	                        Double i = tags.get(tag);
	                        i++;
	                        tags.put(tag, i);
	                    }
	                    hashVal++; 
	                    previous = tag; 
			}				
			line = reader.readLine();
			if (line == null) {
	                    word =  "###";
			}
			else{
	                    words = line.split("/");
	                    word = words[0]; tag = words[1];
	                    count.put(word, 1.0);
	                
			}
			while(!word.equals("###") && line != null){
			
	                    if (tags.get(tag) == null){
				tags.put(tag,1.0);
	                    }
	                    else {
				Double i = tags.get(tag);
				i++;
				tags.put(tag, i);
	                    }
	                    if (stateTrans.get(previous+" "+tag) == null){
				stateTrans.put(previous+" "+tag, 1.0);
	                    }
	                    else{
				Double i = stateTrans.get(previous+" "+tag);
				i++;
				stateTrans.put(previous+" "+tag, i);
	                    }				
	                    if (stateEmit.get(tag+" "+word) == null){
	                    stateEmit.put(tag+" "+word, 1.0);
	                    }
	                    else {
				Double i = stateEmit.get(tag+" "+word);
	                        i++;
				stateEmit.put(tag+" "+word, i);
	                    }
	                    previous = tag;
	                    line = reader.readLine();
	                    words = line.split("/");
	                    word = words[0];
	                    tag = words[1];
	                    count.put(word, 1.0);
	                   
	                }
	                
			if (line != null){                    
	                    if (stateTrans.get(previous+" "+tag) == null){
				stateTrans.put(previous+" "+tag, 1.0);
	                    }
	                    else{
				Double i = stateTrans.get(previous+" "+tag);
				i++;
	                        stateTrans.put(previous+" "+tag, i);
	                    }
			}		
	            }
	           
		
	            Double ilast = tags.get("###");
	            ilast--;
	            tags.put("###", ilast);
		
	            reader.close();
		}
		catch(Exception ex){
	            ex.printStackTrace();
		}
		
	    }
	    
	    HashMap<String,Double> count = new HashMap<String,Double>();    // would be the counts
	    public Double val_viterbi(){
	        HashMap<String,String> edges = new HashMap<>(); 
	        HashMap<String,Double> scores = new HashMap<>();
	    	Double score,err = 0.0,wordcounts = 0.0;
	       
		try {
	            File inputfile = new File("entest.txt");
	            BufferedReader reader = new BufferedReader(new FileReader(inputfile));
	            String line = reader.readLine();
	            String wordtag[] = line.split("/"); 
	            int len = 0;
	            String word = wordtag[0],tag = wordtag[1];                      
	            
	            while(line!=null){
	                String tempsent = "",sentence = "";    
	                
			if(word.equals("###")){                    
	                    scores.put(0+" "+tag, 0.0); 
	                    edges.put(0+" "+tag, null);
	                }
					
			line = reader.readLine();                
			if (line != null) {                    
	                    wordtag = line.split("/");
	                    word = wordtag[0];
	                    tag = wordtag[1];
	                    while(!word.equals("###")){  
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
	                    String prev = null,next = null,last = null;
	                    String words[] = new String[1];
	                    len = sent.length;                 
	                    
	                   
	                   
	                    for(int i=0;i<=(len-1);i++){
	                        words = sent[i].split("/");
	                        for (HashMap.Entry<String,Double> pposition: tags.entrySet()){
	                            prev = pposition.getKey();
	                            for (HashMap.Entry<String,Double> nposition: tags.entrySet()){
	                                next = nposition.getKey();
	                                if((scores.get(i+" "+prev) != null) && (stateTrans.get(prev+" "+next) != null)){
	        			    score = scores.get(i+" "+prev) + (probability("T",prev+" "+next)) + (probability("E",next+" "+words[0]));			    	
	                                    int indent = i+1;                                    
	                                    if((scores.get(indent+" "+next) == null) || (scores.get(indent+" "+next) > score)){ 
	                                    	edges.put(indent+" "+next,i+" "+prev);   
	                                    	scores.put(indent+" "+next,score);                        
	                                                                             
	                                    }
	                                }
	                            }
	                        }
	                    }
	                    for (HashMap.Entry<String,Double> lposition: tags.entrySet()){
	                        last = lposition.getKey();
	                        if((scores.get(len+" "+last) != null) && (stateTrans.get(last+" "+"###") != null)){
	                            score = scores.get(len+" "+last) + (probability("T",last+" "+"###"));			    	
	                            int k = len+1;
	                            if((scores.get(k+" "+"###") == null) || (scores.get(k+" "+"###") > score)){
	                                scores.put(k+" "+"###",score);                        
	                                edges.put(k+" "+"###",len+" "+last);
	                            }
	                        }
	                    }

	                    int k = len+1;   
	                    wordcounts = wordcounts + len;                    
	                    String next_edge = edges.get(k+" "+"###");
	                    String checktag[] = new String[1],words1[] = new String[1];	                  
	                    while(!next_edge.equals(0+" "+"###")){
	                        checktag = next_edge.split(" ");                        
	                        words1 = sent[len-1].split("/");                        
	                        if(!checktag[1].equals(words1[1])){
	                            err++;
	                        }   
	                        len--;
	                        next_edge = edges.get(next_edge);                        
	                    }
	                 
	                    scores.clear(); //clearing scores
	                    edges.clear();  // clearing edges                  
	                }
					
	            }
	            reader.close();
		}
		catch(Exception ex){
	            ex.printStackTrace();
		}
	        Double error_rate = (err/wordcounts);
	        return error_rate; //return error rate 
	    }
	    
	 

	

}
