package sentenceParagraph;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class sentence {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		 String str = " Wendy was playing a video game where she scores 5 points for each treasure she finds. If she found 4 treasures on the first level and 3 on the second, what would her score be?";
		    Pattern re = Pattern.compile("[^.!?\\s][^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[.!?]?['\"]?(?=\\s|$)", Pattern.MULTILINE | Pattern.COMMENTS);
		    Matcher reMatcher = re.matcher(str);
		    ArrayList<String> alist = new ArrayList<String>();
		    while (reMatcher.find()) {
		    	alist.add(reMatcher.group());
		        //System.out.println();
		    }
		    
		    if((alist.get(alist.size()-1).contains("?"))&&(alist.get(alist.size()-1).contains(", how")||alist.get(alist.size()-1).contains(", what"))){
		    	//System.out.println("True");
		    	String[] arr= alist.get(alist.size()-1).split(",");
		    	if(arr.length==2){
		    		arr[0]+=".";
		    	}
		    	alist.remove(alist.size()-1);
		    	for(String a : arr){
		    		alist.add(a.trim());
		    	}
		    }
		    
		  		    
		    for(String s : alist){
		    	System.out.println(s);
		    }

	}

}
