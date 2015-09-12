package AssignTasks;

import tool.DataFilter;

//�ƶ�У����
public class MobileVerifier {
	public double PsCPU;// ��̬���㹦��
	public double PdCPU;// ��̬���ӵļ��㹦��
	public double PsRF;// ��̬���书��
	public double PdRF;// ��̬���Ӵ��书��
	public int w; // ���ʱ��
	//public double st; // У�����ṩ��У�����ʱ��
	public int c; // �ָ�У���ߵ�У�����񣨿�����
	// public VerificationTask verTask;//�ָ�У���ߵ�У������

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
	 * @param fi  ����֤�ݵ��Ʒ���������Ƶ��
	 * @return
	 */
	public int verBlocks(double t,double fi) {
		//f0*tver(x)/fi+(Ps0CPU+Pd0CPU)*tcsp(x)/(PsiCPU+PdiCPU)+ttran(x)
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
	private double verComputeEnergy(double t, double t2) {
		return PsCPU * t + PdCPU * t2;
	}

	// �����ܺ�
	private double verTransEnergy(double t, double t2) {
		return PsRF * t + PdRF * t2;
	}

}
