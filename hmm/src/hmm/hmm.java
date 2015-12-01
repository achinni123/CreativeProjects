package hmm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

public class hmm {

	 HashMap<String,Double> transmap = new HashMap<String,Double>();

	    
	    public double prob(String type, String input){
	    	    Double result;
		        Double tagcount;
		        Double bicount;
		        Double tagval;
	    	String read[] = input.split(" ");
	       
		if (type.equals("T")){
	            bicount = transmap.get(read[0]+" "+read[1]);
	            tagcount = content.get(read[0]);            
	            result = -Math.log((bicount+1)/(tagcount+content.size()));
		}
		else{
	            if(emission.get(read[0]+" "+read[1]) == null){
	                tagcount = content.get(read[0]);
	                int n = words.size();
	                result = -Math.log(1/(tagcount+n));                                               
	            }
	            else{
	                tagval = emission.get(read[0]+" "+read[1]);
	                tagcount = content.get(read[0]);   
	                int n = words.size();
	                //System.out.println();
	                result = -Math.log((tagval+1)/(tagcount+n)); 
	            }
	            
		}
		return result;
	    }
	    public static void main(String[] args) {
	    	// TODO Auto-generated method stub
	    	hmm hmm = new hmm();
	    	hmm.cal(); 
	        Double value = hmm.errorCalc();
	        System.out.println("Accuracy Percentage : "+ (100-(value*100)) + "%");
	        System.out.println("Error percentage : "+ (value*100) +"%");
	    }
	    public Double errorCalc(){
	        HashMap<String,String> a = new HashMap<>();  
	        HashMap<String,Double> best = new HashMap<>(); 
	    	Double score;
	        Double errcount = 0.0;
	        Double wordcounts = 0.0;
		try {
	            File inputfile = new File("entest.txt");
	            BufferedReader reader = new BufferedReader(new FileReader(inputfile));
	            String line = reader.readLine();
	            String arr[] = line.split("/"); 
	            String word = arr[0];  
	            String tag = arr[1];                    
	            int length = 0;
	            while(line!=null){
	                String tempsent = "";    
	                String sentence = "";
			if(word.equals("###")){                    
	                    best.put(0+" "+tag, 0.0);   
	                    a.put(0+" "+tag, null);
	                }
					
			line = reader.readLine();                
			if (line != null) {                    
	                    arr = line.split("/");
	                    word = arr[0];
	                    tag = arr[1];
	                    while(!word.equals("###")){  
	                        tempsent = tempsent + line + " ";
	                        line = reader.readLine();
	                        if(line != null){
	                            arr = line.split("/");
	                            word = arr[0];
	                            tag = arr[1];
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
	                    length = sent.length;  
	                    String end = null;
	                    String prev = null;
	                    String next = null;
	                   
	                    for(int i=0;i<=(length-1);i++){
	                        words = sent[i].split("/");
	                        for (HashMap.Entry<String,Double> prevs: content.entrySet()){
	                            prev = prevs.getKey();
	                            for (HashMap.Entry<String,Double> nexts: content.entrySet()){
	                                next = nexts.getKey();
	                                if((best.get(i+" "+prev) != null) && (transmap.get(prev+" "+next) != null)){
	        			    score = best.get(i+" "+prev) + (prob("T",prev+" "+next)) + (prob("E",next+" "+words[0]));			    	
	                                    int c = i+1;                                    
	                                    if((best.get(c+" "+next) == null) || (best.get(c+" "+next) > score)){
	                                        best.put(c+" "+next,score);                        
	                                        a.put(c+" "+next,i+" "+prev);                                        
	                                    }
	                                }
	                            }
	                        }
	                    }
	                    for (HashMap.Entry<String,Double> x: content.entrySet()){
	                        end = x.getKey();
	                        if((best.get(length+" "+end) != null) && (transmap.get(end+" "+"###") != null)){
	                            score = best.get(length+" "+end) + (prob("T",end+" "+"###"));			    	
	                            int n1 = length+1;
	                           
	                            //System.out.println();
	                            //System.out.println();
	                            
	                            if((best.get(n1+" "+"###") == null) || (best.get(n1+" "+"###") > score)){
	                                best.put(n1+" "+"###",score);                        
	                                a.put(n1+" "+"###",length+" "+end);
	                            }
	                        }
	                    }
	                    int d1 = length+1;   
	                    wordcounts = wordcounts + length-1;                    
	                    String next_edge = a.get(d1+" "+"###");
	                    String checktag[] = new String[1];
	                    String words1[] = new String[1];
	                    while(!next_edge.equals(0+" "+"###")){
	                        checktag = next_edge.split(" ");                        
	                        words1 = sent[length-1].split("/");                        
	                        if(!checktag[1].equals(words1[1])){
	                            errcount++;
	                        }   
	                        length--;
	                        next_edge = a.get(next_edge);                        
	                    }
	              
	                    best.clear();
	                    a.clear();                    
	                }
					
	            }
	            reader.close();
		}
		catch(Exception ex){
	            ex.printStackTrace();
		}
	       
	        Double error_rate = (errcount/wordcounts);
	        return error_rate;
	    }
	    
	    
	    HashMap<String,Double> emission = new HashMap<String,Double>();
	 
	    
	    public void cal(){
	        Double counting = 0.0;
		try {
	            File inputfile = new File("entrain.txt");
	            BufferedReader reader = new BufferedReader(new FileReader(inputfile));
	            String line = reader.readLine();
	            String arr[] = line.split("/"); 
	            //System.out.println();
	            String word = arr[0];  
	            //System.out.println();
	            String tag = arr[1];
	            String previous = null;
	            while(line != null){
			if (word.equals("###")){
	                    counting++;                    
	                    if (content.get(tag) == null){
	                    content.put(tag,1.0);
	                    }
	                    else {
	                        Double i = content.get(tag);
	                        i++;
	                        content.put(tag, i);
	                    }
	                    previous = tag;  
			}				
			line = reader.readLine();
			
			if (line == null) {
	                    word =  "###";
			}
			else{
	                    arr = line.split("/");
	                    word = arr[0];
	                    tag = arr[1];
	                    words.put(word, 1.0);
	                  //  num_words++;
			}
			while(!word.equals("###") && line != null){
			
	                    if (content.get(tag) == null){
				content.put(tag,1.0);
	                    }
	                    else {
				Double i = content.get(tag);
				i++;
				content.put(tag, i);
	                    }
	                    if (transmap.get(previous+" "+tag) == null){
				transmap.put(previous+" "+tag, 1.0);
	                    }
	                    else{
				Double i = transmap.get(previous+" "+tag);
				i++;
				transmap.put(previous+" "+tag, i);
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
	                    arr = line.split("/");
	                    word = arr[0];
	                    tag = arr[1];
	                    words.put(word, 1.0);
	                  //  num_words++;
	                }
	                
			if (line != null){                    
	                    if (transmap.get(previous+" "+tag) == null){
				transmap.put(previous+" "+tag, 1.0);
	                    }
	                    else{
				Double i = transmap.get(previous+" "+tag);
				i++;
	                        transmap.put(previous+" "+tag, i);
	                    }
			}		
	            }
	    
	            Double ilast = content.get("###");
	            ilast--;
	            content.put("###", ilast);
		
	            reader.close();
		}
		catch(Exception ex){
	            ex.printStackTrace();
		}
		
	    }
	    HashMap<String,Double> content = new HashMap<String,Double>();   
	    HashMap<String,Double> words = new HashMap<String,Double>();

}
