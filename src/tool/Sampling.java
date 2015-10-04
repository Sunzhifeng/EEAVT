package tool;

public class Sampling {
	/**
	 * 获得采样块数
	 * @param n 总块数
	 * @param e 坏块数
	 * @param P 探测率
	 * @return	采样块数
	 */
	 public static int getSampleBlocks(int n,int e,double P){
		 if(P==1)return n-e+1;
		 double pb=(double)e/n;
		 return (int)(Math.log(1-P)/Math.log(1-pb));
	 }
	 
	 
	 public static int getSampleBlocks(double pe,double P){
		
		 return (int)(Math.log(1-P)/Math.log(1-pe));
	 }
	 /**
	  * 获得采样比例
	  * @param n 总块数
	  * @param e 坏块
	  * @param P 探测率
	  * @return 采样比例
	  */
	 public static double getSamplingRatio(int n,int e,double P){
		 double pb=(double)e/n;		
		 double w=(Math.log(1-P)/Math.log(1-pb))/n;
		 return w;
	 } 
	 /**
	  * 获得适当的校验频率
	  * @param n	总块数
	  * @param e	坏块
	  * @param t	采样块数
	  * @param Pt	周期内的探测率
	  * @param T	校验周期（天、周。。。）
	  * @return		校验频率
	  */
	 public static int getVerifyingFrequent(int n,double e,int t,double Pt,int T){
		 return (int)Math.ceil((Math.log(1-Pt)/(t*T*(Math.log(1-e/n)))));
		 
	 }
	 
	 /**
	  * 分段情况下的采样块数
	  * @param n	总块数 
	  * @param P	探测率
	  * @param s	每块段数
	  * @param es	损坏的段数
	  * @return		采样块数
	  */
	 public static int getSampleBlocks(int n,double P,int s,int es){
		 double Ps=(double)es/(n*s);
		 //StdOut.println(Ps);
		 return (int)(Math.log(1-P)/(s*Math.log(1-Ps)));
	 }
	 //分段下的采样比率
	 public static double getSamplingRatio(int n,double P,int s,int es){
		 double Ps=(double)es/(n*s);
		 //w=log(1-P)/nslog(1-Ps)	  
		 return(Math.log(1-P)/(s*n*Math.log(1-Ps)));
	 } 
	 
	 //分段下的校验频率
	 public static int getVerifyingFrequent(int n,int t,double Pt,int T,int s,int es){
		double Ps=(double)es/(n*s);
		 return (int)Math.ceil((Math.log(1-Pt)/(t*T*(Math.log(1-Ps)))));
		 
	 }
	 public static void main(String[] args){
		 int c=getSampleBlocks(1000, 1,0.99);
		 System.out.println(c);
		 
		 int cs=getSampleBlocks(10000,0.999,200,100);
		 System.out.println(cs);
	 }
}
