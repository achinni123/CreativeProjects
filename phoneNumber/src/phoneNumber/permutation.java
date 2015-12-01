package phoneNumber;

public class permutation {
	
	public  static void main(String[] args){
		
		int[] arr = {2,2,2};
		permutePhone(arr);
	}
	public static void permutePhone(int[] arr){
		char[] val = new char[arr.length];
		permute(arr,0,val);
	}
	
	public static void permute(int[] arr, int pos, char[] val){
		if(val.length==pos){
			for(int i=0;i<val.length;i++){
				System.out.print(val[i]);
			}
			System.out.println();
			return;
		}
		char[] temp = sentence(arr[pos]);
		for(char s : temp){
			val[pos]=s;
			permute(arr,pos+1,val);
		}
		
	}
	
	public static char[] sentence(int num){
		switch(num){
		case 2:return "abc".toCharArray();
		case 3:return "def".toCharArray();
		case 4:return "ghi".toCharArray();
		case 5:return "jkl".toCharArray();
		case 6:return "mno".toCharArray();
		case 7:return "pqr".toCharArray();
		case 8:return "stuv".toCharArray();
		case 9:return "wxyz".toCharArray();
		}
		return null;
	}

}
