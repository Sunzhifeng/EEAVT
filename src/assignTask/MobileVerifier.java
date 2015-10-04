package assignTask;

import tool.DataFilter;

//�ƶ�У����
public class MobileVerifier {
	public double PsCPU;// ��̬���㹦��
	public double PdCPU;// ��̬���ӵļ��㹦��
	public double PsRF;// ��̬���书��
	public double PdRF;// ��̬���Ӵ��书��
	public int w; // ���ṩУ�����ʱ�䣨���ʱ�䣩
	public VerificationTask VT=new VerificationTask(0,0);// �ָ�У���ߵ�У������

	//public int  remainBlocks; 
	// public int c=0; // �ָ�У���ߵ�У�����񣨿�����

	// min constructor
	public MobileVerifier() {
	}

	// max constructor
	public MobileVerifier(double PsCPU, double PdCPU, double PsRF, double PdRF, int w) {
		this.PsCPU = PsCPU;
		this.PdCPU = PdCPU;
		this.PsRF = PsRF;
		this.PdRF = PdRF;
		this.w = w;
	}

	public void receiveTask(VerificationTask VT) {
		this.VT = VT;
	}

	/**
	 * ֤�ݼ�����ʱ��
	 * 
	 * @param x
	 *            ������
	 * @return ���ʱ��
	 */
	public double verTime(int x) {
		return DataFilter
				.roundDouble((BaseParams.Ps0CPU + BaseParams.Pd0CPU) * BaseParams.baseVerTime(x) / (PsCPU + PdCPU), 3);
	}

	/**
	 * ����ʱ���ڿ�У��Ŀ���
	 * 
	 * @param time
	 * @param fi
	 *            ����֤�ݵ��Ʒ���������Ƶ��
	 * @return
	 */
	public int verBlocks(double t, double fi) {
		// f0*tver(x)/fi+(Ps0CPU+Pd0CPU)*tcsp(x)/(PsiCPU+PdiCPU)+ttran(x)
		return BaseParams.verBlocks(t, fi, PsCPU, PdCPU);
	}

	/**
	 * У���ߵ���������
	 * 
	 * @param t
	 *            У����̵��ܺ�ʱ
	 * @param TdCPU
	 *            У���߶�̬CPU����ʱ��
	 * @param TdRF
	 *            У���߶�̬RF����ʱ��
	 * @return ���ĵ�������
	 */
	public double verEnergy(double t, double TdCPU, double TdRF) {
		return verComputeEnergy(t, TdCPU) + verTransEnergy(t, TdRF);
	}

	// �����ܺ�
	private double verComputeEnergy(double t, double TdCPU) {
		return PsCPU * t + PdCPU * TdCPU;
	}

	// �����ܺ�
	private double verTransEnergy(double t, double TdRF) {
		return PsRF * t + PdRF * TdRF;
	}

	// t is the response time
	public double dComputeEnergy(double rt) {
		
		return (PsCPU + PsRF) * rt;
	}
}
