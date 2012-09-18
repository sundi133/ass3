import java.math.BigInteger;
import java.util.Scanner;


public class Chairs {

	private static int globalcntr;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		//System.out.print(3%1000000003);
		Scanner in = new Scanner(System.in);
		int r = in.nextInt();
		while(r--!=0){
		int n = in.nextInt();
		int k = in.nextInt();
		int rad = 2;
		if(k==1){
			System.out.println(n);
		}else if(k*2 >= n){
			
			System.out.println(1);
		}else{
			int res = counter(n,k,n);
			System.out.println(globalcntr);
		}
		
		}
	}

	private static int counter(int n, int k,int total) {
		// TODO Auto-generated method stub
		int cntr=n;
		int kcnt=k;
		if(n==total){
			while(kcnt--!=0 && cntr > 0){
				cntr=cntr-2;
				globalcntr++;
				if(cntr < 0)
					return -1;
				
			}
			while(cntr--!=1)
				globalcntr++;
			
		}else{
			while(kcnt--!=0 && cntr >= 0){
				cntr=cntr-2;
				globalcntr++;
				if(cntr < 0)
					return -1;
				
			}
			while(cntr--!=0)
				globalcntr++;
			
		}
		counter(n-1,k,total);
		return globalcntr;
	}

}
