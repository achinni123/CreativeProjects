package exceptProduct;

public class product {

	public static void main(String[] args) {
	int[] arr={1,2,3,4,5};
	double sum=0;
	for(int i=0;i<arr.length;i++){
		sum+=Math.log((double)arr[i]);
	}
	System.out.println("sum "+ sum);
	int output=4;
	System.out.println("arr[output] ="+ Math.log((double)arr[output]));
	
	System.out.println((int)Math.ceil(Math.exp(sum-Math.log((double)arr[output]))));
		
		

	}

}
