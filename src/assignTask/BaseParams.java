package assignTask;

import tool.DataFilter;

public class BaseParams {
	public static final int S = 10;// У�����С����У���߸���
	public static final int L = 5; // �Ʒ���������
	public static final int B = 200;// �ƶ��������KB
	public static final int n = 10000; // ��������ݿ���
	public static final double pe=0.001;//������Ϊ0.1%
	public static final int e = (int)(pe* n);
	public static final int M = 1; // �ļ���С��G
	public static final double pr = 0.99; // ̽����
	public static final int bs = (int) (M / n) * 1000;// ���ݿ��С ,K
	public static final int SECUBIT = 50; // У���㷨�İ�ȫǿ��20B=160bit
	public static final int T =9; // �û��������ʱ��,��λs

	// У���߻�׼��������֤֤�ݵļ���ʱ�亯��tver(x)=ax+b
	public static double Ps0CPU = 2.5;
	public static double Pd0CPU = 5;
	public static double Ps0RF = 1.25;
	public static double Pd0RF = 1.25;
	public static double a = 0.0028;
	public static double b = 0.055;

	// ��׼Ƶ��f0�Ʒ���������֤�ݵ�ʱ�亯������tcsp(x)=ax+b,��ΪУ���ߵȴ�ʱ��.
	public static double f0 = 2.6; // �Ʒ�������׼CPU����Ƶ��
	public static double a2 = 0.014;
	public static double b2 = 0.6255;
	

	// �������ĵĻ�������ttran(x)=ax+b,B=200kb/s
	public static double a3 = 0.0001;
	public static double b3 = 0.0002;

	// ʵ���пɱ��������
	public static double[] PsCPUi = { 2.0, 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, 2.7, 2.8, 2.9 };
	public static double[] PdCPUi = { 4.0, 4.2, 4.4, 4.6, 4.8, 5.0, 5.2, 5.4, 5.6, 5.8 };
	public static double[] PsRFi = { 1, 1.05, 1.1, 1.15, 1.2, 1.25, 1.3, 1.35, 1.4, 1.45 };
	public static double[] PdRFi = { 1, 1.05, 1.1, 1.15, 1.2, 1.25, 1.3, 1.35, 1.4, 1.45 };
	public static double[] prs = { 0.95, 0.99, 1.00 };
	public static double[] fj = { 2.4, 2.6, 2.8, 3.0, 3.2 };
	public static int[] wi = { 100, 100, 100, 100, 100, 100, 100,100, 100, 100};

	/**
	 * У���߻�׼������֤֤�ݵļ���ʱ��
	 * 
	 * @param x
	 *            У�����
	 * @return ��֤֤��ʱ��
	 */
	public static double baseVerTime(int x) {
		return DataFilter.roundDouble(a * x + b, 3);
	}

	/**
	 * �������Ի�׼CPU����Ƶ��f0����֤��ʱ��
	 * 
	 * @param x
	 *            У��Ŀ���
	 * @return ����֤��ʱ��
	 */
	public static double baseCSTime(int x) {
		return DataFilter.roundDouble(a2 * x + b2, 3);
	}

	/**
	 * �������ڸ���ʱ���ڿ�У��������
	 * 
	 * @param t
	 *            ����֤��ʱ��
	 * @param fi
	 *            �������Ĺ���Ƶ��
	 * @return У��֤�ݰ�����������
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
