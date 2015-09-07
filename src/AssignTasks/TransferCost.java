package AssignTasks;

import tool.DataFilter;

public class TransferCost {
	public static final int K=1024;
	public static final int Byte=8;	
	public static final int B=BaseParams.B;
	/**
	 * ��ս�Ĵ������ġ�����Q��k1��y1,R��
	 * @param c		��ս�Ŀ���
	 * @param n		�ܿ���
	 * @param p		��������20B��
	 * @return		����B
	 */
	public static double challengeCost(int c,int n,int p){		
		return DataFilter.roundDouble((c*Math.log(n)/Byte+3*p)/K,3);
	}
	/**
	 * ��ս�Ĵ������ġ�����Q��V,y1,R��
	 * @param c		��ս�Ŀ���	 
	 * @param p		��������20B��
	 * @return		����B
	 */
	public static double challengeCost(int c,int p){		
		return (c+2)*p;
	}

	/**
	 * ֤�ݵĴ������ġ�����DP��TP��RanMul��{idi})
	 * @param c		��ս�Ŀ���
	 * @param n		�ܿ���
	 * @param p		������B
	 * @return		 ����B
	 */
	public static double proofCost(int c,int n,int p){		
		return DataFilter.roundDouble((3*p+c*(2*p+2*Math.log(n)/Byte))/K,3);
	}
	/**
	 * ֤�ݵĴ������ġ�����DP��TP��RanMul)
	 * @param c		��ս�Ŀ���
	 * @param n		�ܿ���
	 * @param p		������B
	 * @return		 ����B
	 */
	public static double proofCost(int p){		
		return (3*p);
	}
	/**
	 * IHTPADD�����Ĵ������ġ���KBΪ��λ
	 * @param c		��ս����
	 * @param n		�ܿ���
	 * @param p		��������λ����20B��
	 * @return		����KB
	 */
	public static double IHTPADDTransCost(int c,int n,int p){
		double chalLength=challengeCost(c,n,p);
		double proofLength=proofCost(c,n,p);
		double totalKB=(chalLength+proofLength);
		return DataFilter.roundDouble(totalKB, 3);
	}
	/**
	 * IHTPADD�����Ĵ������ġ���BΪ��λ
	 * @param c		��ս����
	 * @param n		�ܿ���
	 * @param p		��������λ����20B��
	 * @return		����KB
	 */
	public static double IHTPADDTransCost2(int c,int n,int p){
		double chalLength=challengeCost(c,n,p);
		double proofLength=proofCost(p);
		double totalB=(chalLength+proofLength);
		return  DataFilter.roundDouble(totalB, 3);
	}
	/**
	 * IHTPADD�����Ĵ���ռ���ļ��ı���
	 * @param c		��ս����
	 * @param n		�ܿ���
	 * @param s		ÿ��Ķ���
	 * @param p		��������λ����20B��
	 * @return		�ٷֱ�
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
