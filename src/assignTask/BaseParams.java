package assignTask;

import tool.DataFilter;

public class BaseParams {
	public static final int S = 10;// 校验组大小，即校验者个数
	public static final int L = 5; // 云服务器个数
	public static final int B = 200;// 移动网络带宽KB
	public static final int n = 10000; // 待检查数据块数
	public static final double pe=0.001;//坏块率为0.1%
	public static final int e = (int)(pe* n);
	public static final int M = 1; // 文件大小，G
	public static final double pr = 0.99; // 探测率
	public static final int bs = (int) (M / n) * 1000;// 数据块大小 ,K
	public static final int SECUBIT = 50; // 校验算法的安全强度20B=160bit
	public static final int T =9; // 用户期望完成时间,单位s

	// 校验者基准功率下验证证据的计算时间函数tver(x)=ax+b
	public static double Ps0CPU = 2.5;
	public static double Pd0CPU = 5;
	public static double Ps0RF = 1.25;
	public static double Pd0RF = 1.25;
	public static double a = 0.0028;
	public static double b = 0.055;

	// 基准频率f0云服务器计算证据的时间函数参数tcsp(x)=ax+b,即为校验者等待时间.
	public static double f0 = 2.6; // 云服务器基准CPU工作频率
	public static double a2 = 0.014;
	public static double b2 = 0.6255;
	

	// 传输消耗的基本参数ttran(x)=ax+b,B=200kb/s
	public static double a3 = 0.0001;
	public static double b3 = 0.0002;

	// 实验中可变参数设置
	public static double[] PsCPUi = { 2.0, 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, 2.7, 2.8, 2.9 };
	public static double[] PdCPUi = { 4.0, 4.2, 4.4, 4.6, 4.8, 5.0, 5.2, 5.4, 5.6, 5.8 };
	public static double[] PsRFi = { 1, 1.05, 1.1, 1.15, 1.2, 1.25, 1.3, 1.35, 1.4, 1.45 };
	public static double[] PdRFi = { 1, 1.05, 1.1, 1.15, 1.2, 1.25, 1.3, 1.35, 1.4, 1.45 };
	public static double[] prs = { 0.95, 0.99, 1.00 };
	public static double[] fj = { 2.4, 2.6, 2.8, 3.0, 3.2 };
	public static int[] wi = { 100, 100, 100, 100, 100, 100, 100,100, 100, 100};

	/**
	 * 校验者基准功率验证证据的计算时间
	 * 
	 * @param x
	 *            校验块数
	 * @return 验证证据时间
	 */
	public static double baseVerTime(int x) {
		return DataFilter.roundDouble(a * x + b, 3);
	}

	/**
	 * 服务器以基准CPU工作频率f0计算证据时间
	 * 
	 * @param x
	 *            校验的块数
	 * @return 计算证据时间
	 */
	public static double baseCSTime(int x) {
		return DataFilter.roundDouble(a2 * x + b2, 3);
	}

	/**
	 * 服务器在给定时间内可校验最大块数
	 * 
	 * @param t
	 *            计算证据时间
	 * @param fi
	 *            服务器的工作频率
	 * @return 校验证据包含的最大块数
	 */
	public static int verBlocks(double t, double fi) {
		return (int) (t * (fi / (a2 * f0)) - b2 / a2);
	}

	public static int verBlocks(double t, double fi, double PsCPUi, double PdCPUi) {
		// f0*tver(x)/fi+(Ps0CPU+Pd0CPU)*tcsp(x)/(PsiCPU+PdiCPU)+ttran(x)
		double f = f0 / fi;
		double p = (Ps0CPU + Pd0CPU) / (PsCPUi + PdCPUi);
		return (int) ((t - (f * b + p * b2 + b3)) / (f * a + p * a2 + a3));

	}
	
	public static double transCost(int n){
		return DataFilter.roundDouble(a3*n+b3,3);
	}
}
