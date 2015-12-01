package rotateMatrix;

public class rotateMatrix {
	
	public static void main(String[] args){
		int[][] arr = new int[5][5];
		int val = 1;
		for(int i=0; i<arr.length;i++){
			for(int j=0;j<arr[i].length;j++){
				arr[i][j] = val++;
			}
		}
		
		for(int i=0;i<arr.length;i++){
			for(int j=0;j<arr[i].length;j++){
				System.out.print(arr[i][j]+" ");
			}
			System.out.println();
		}
		
		System.out.println("=========================================================");
		System.out.println("=========================================================");
		System.out.println("=========================================================");
		int n = arr.length;
		
		
		for(int i=0;i<n/2;i++){
			for(int j=0;j<(n+1)/2;j++){
				int temp = arr[n-1-j][i];
				arr[n-1-j][i]=arr[i][j];
				arr[i][j] =arr[n-1-i][n-1-j];
				arr[n-1-i][n-1-j] = arr[j][n-1-i];
				arr[j][n-1-i] = temp;
			}
		  }
		
		
		for(int i=0;i<arr.length;i++){
			for(int j=0;j<arr[i].length;j++){
				System.out.print(arr[i][j] +" ");
			}
			System.out.println();
		}
		
	}

}
