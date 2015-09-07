package AssignTasks;

import tool.DataFilter;

public class TransferCost {
	public static final int K=1024;
	public static final int Byte=8;	
	public static final int B=BaseParams.B;
	/**
	 * 挑战的传输消耗——（Q，k1，y1,R）
	 * @param c		挑战的块数
	 * @param n		总块数
	 * @param p		大素数（20B）
	 * @return		多少B
	 */
	public static double challengeCost(int c,int n,int p){		
		return DataFilter.roundDouble((c*Math.log(n)/Byte+3*p)/K,3);
	}
	/**
	 * 挑战的传输消耗——（Q，V,y1,R）
	 * @param c		挑战的块数	 
	 * @param p		大素数（20B）
	 * @return		多少B
	 */
	public static double challengeCost(int c,int p){		
		return (c+2)*p;
	}

	/**
	 * 证据的传输消耗——（DP，TP，RanMul，{idi})
	 * @param c		挑战的块数
	 * @param n		总块数
	 * @param p		大素数B
	 * @return		 多少B
	 */
	public static double proofCost(int c,int n,int p){		
		return DataFilter.roundDouble((3*p+c*(2*p+2*Math.log(n)/Byte))/K,3);
	}
	/**
	 * 证据的传输消耗——（DP，TP，RanMul)
	 * @param c		挑战的块数
	 * @param n		总块数
	 * @param p		大素数B
	 * @return		 多少B
	 */
	public static double proofCost(int p){		
		return (3*p);
	}
	/**
	 * IHTPADD方案的传输消耗——KB为单位
	 * @param c		挑战块数
	 * @param n		总块数
	 * @param p		大素数的位数（20B）
	 * @return		多少KB
	 */
	public static double IHTPADDTransCost(int c,int n,int p){
		double chalLength=challengeCost(c,n,p);
		double proofLength=proofCost(c,n,p);
		double totalKB=(chalLength+proofLength);
		return DataFilter.roundDouble(totalKB, 3);
	}
	/**
	 * IHTPADD方案的传输消耗——B为单位
	 * @param c		挑战块数
	 * @param n		总块数
	 * @param p		大素数的位数（20B）
	 * @return		多少KB
	 */
	public static double IHTPADDTransCost2(int c,int n,int p){
		double chalLength=challengeCost(c,n,p);
		double proofLength=proofCost(p);
		double totalB=(chalLength+proofLength);
		return  DataFilter.roundDouble(totalB, 3);
	}
	/**
	 * IHTPADD方案的传输占总文件的比例
	 * @param c		挑战块数
	 * @param n		总块数
	 * @param s		每块的段数
	 * @param p		大素数的位数（20B）
	 * @return		百分比
	 */
	public static double IHTPADDTransCostRatio(int c, int n,int s,int p){
		double transCost=IHTPADDTransCost(c,n,p);
		double total=n*s*p/K;
		return DataFilter.roundDouble(transCost/total, 3)*100;
	}

	public static double IHTTransCost(int c,int n,int s,int p){		
		double result=(c*(2*Math.log(n)+3*p)+4*p)/K;		
		return DataFilter.roundDouble(result,2);
	}

	public static double MHTTransCost(int c,int n,int s,int p){
		double q=Math.log(n);
		double result=(c*(q*p+q+p)+s*p)/K;
		return DataFilter.roundDouble(result,2);
	}

	public static double transTime(int x,int n,int p){
		return DataFilter.roundDouble(TransferCost.IHTPADDTransCost(x, n, p)/B, 3);
	}
	
	public static void main(String []args){

	}
}
