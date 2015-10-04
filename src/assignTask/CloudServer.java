package assignTask;

import tool.DataFilter;
import tool.Sampling;

//�Ʒ�����
public class CloudServer {
	public double f; // �Ʒ������Ĺ���Ƶ��
	public static final int k = 1; // �ܺ�ϵ��
	public static final int a = 3;

	public CloudServer(double f) {
		this.f = f;
	}

	/**
	 * ����������֤��ʱ��
	 * 
	 * @param x
	 *            У��Ŀ���
	 * @return ����֤��ʱ��
	 */
	public double proofTime(int x) {
		return DataFilter.roundDouble(BaseParams.f0 * BaseParams.baseCSTime(x) / f, 3);
	}

	/**
	 * ����ʱ���ڿ�У��������
	 * 
	 * @param time
	 * @return
	 */
	public int verBlocks(double time) {
		return BaseParams.verBlocks(time, f);
	}

	/**
	 * �Ʒ������ļ�����������
	 * 
	 * @param Tcsp
	 * @return
	 */
	public double CSPEnergy(double Tcsp) {
		// t*k*(f^a)
		return Tcsp * k * Math.pow(f, a);
	}

	// ����������Ӧ�ȼ�����������ָ���û���У���������
	/*
	 * public int responseLevel(VerificationRequest VR){ return
	 * (int)Math.ceil(proofTime(VR.blocks())/(Math.log(VR.n)*VR.P)); }
	 */
	public double responseLevel(VerificationRequest VR) {
		int blocks=Sampling.getSampleBlocks(VR.n,BaseParams.e, VR.P);
		return (proofTime(VR.blocks()) / Math.log(blocks));
	}
}
