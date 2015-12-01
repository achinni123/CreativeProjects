package spiral;
import java.util.Scanner;
public class spiral {
	 public static void main(String[] args) {
	        // TODO code application logic here
	        System.out.println("Enter value");
	        Scanner in=new Scanner(System.in);
	        int n=in.nextInt();
	        int l=0,t=0,dir=0,val=n;
	        int r = 2*n-2,b=2*n-2;
	        int[][] arr=new int[2*n-1][2*n-1];
	        int count=0;
	        
	        while(l<=r&&t<=b){
	            if(count==4){
	                val--;
	                count=0;
	            }
	            if(dir==0){
	                for(int i=l;i<=r;i++){
	                    arr[t][i]=val;
	                }
	                t++;
	                dir=1;
	            }
	            else if(dir==1){
	                for(int i=t;i<=b;i++){
	                    arr[i][r]=val;
	                }
	                r--;
	                dir=2;
	            }
	             else if(dir==2){
	                for(int i=r;i>=l;i--){
	                    arr[b][i]=val;
	                }
	                b--;
	                dir=3;
	            }
	            else if(dir==3){
	                
	                for(int i=b;i>=t;i--){
	                    
	                    arr[l][i]=val;
	                }
	                l++;
	                dir=0;
	            }
	            count++;
	             for(int i=0;i<arr.length;i++){
	            for(int j=0;j<arr[i].length;j++){
	                System.out.print(arr[i][j]+" ");
	            }
	            System.out.println();
	        }
	             
	             System.out.println();
	             System.out.println("--------------------------------");
	        }
	        for(int i=0;i<arr.length;i++){
	            for(int j=0;j<arr[i].length;j++){
	                System.out.print(arr[i][j]+" ");
	            }
	            System.out.println();
	        }
	        
	    }

}
