package maximunSum;
import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;

public class modSum {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner in = new Scanner(System.in);
		int t = in.nextInt();
		TreeMap<Long, Long> tmap = new TreeMap<>();
		long res = 0;
		while (t-- > 0) {
			res = 0;
			int n = in.nextInt();
			long m = in.nextLong();
			tmap.clear();
			long[] arr = new long[n];
			res = in.nextLong();
			arr[0] = res % m;
			res = Long.MIN_VALUE;
			tmap.put(arr[0], arr[0]);
			for (int i = 1; i < arr.length; i++) {
				arr[i] = in.nextLong();
				arr[i] %= m;
				arr[i] += arr[i - 1];
				arr[i] %= m;

				if (tmap.higherEntry(arr[i]) == null) {
					res = Math.max(res, arr[i]);
					tmap.put(arr[i], arr[i]);
					continue;
				}
				long val = tmap.higherEntry(arr[i]).getValue();
				res = Math.max(res, (arr[i] - val + m) % m);
				tmap.put(arr[i], arr[i]);

			}

			System.out.println(res);

		}

	}

}
