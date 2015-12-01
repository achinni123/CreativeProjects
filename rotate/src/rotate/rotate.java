package rotate;

import java.util.Arrays;

public class rotate {
	public static void main(String[] args){
	int k=3;
	int[] arr={0,1,2,3,4};
	int[] asd=new int[arr.length];
	{
	
	for(int i=0;i<arr.length;i++)
	{
		asd[i]=arr[(i+k)%arr.length];
	}
	System.out.println(Arrays.asList(asd));
	for(int i : arr){
		System.out.print(i+" ");
	}
	System.out.println();
	for(int i : asd){
		System.out.print(i+" ");
	}

    }
	}
}
