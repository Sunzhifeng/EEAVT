package AssignTasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tool.DataFilter;

/**
 * ��У���߷������ݿ�
 * 
 * @author MichaelSun
 * @since 2015.9.5
 *
 */
public class AssignTask {
	public static final int SAVT = 0;
	public static final int RRAVT = 1;
	public static final int EEAVT = 2;
	public static final int PRIME = BaseParams.SECUBIT;
	public static final String VER_ENERGY = "verEnergy";
	public static final String SERVER_ENERGY = "serverEnergy";
	public static final String MAX_VER_ENERGY = "maxVerEnergy";
	public static final String MAX_SERVER_ENERGY = "maxServerEnergy";
	public static final String MIN_VER_ENERGY = "minVerEnergy";
	public static final String MIN_SERVER_ENERGY = "minServerEnergy";
	public static final String TIME = "t";
	public static final String MIN_TIME = "mint";
	public static final String MAX_TIME = "maxt";
	public int S = BaseParams.S;
	public int L = BaseParams.L;
	public int M = BaseParams.M; // �ļ���С��G
	public int n = (int) (BaseParams.n * BaseParams.pr);
	public double pr = BaseParams.pr; // ̽����
	public int bs = BaseParams.bs;// ���ݿ��С
	public int T = 10; // �û��������ʱ��
	public VerificationRequest VR = new VerificationRequest(M, n, T, pr);
	public MobileVerifier[] verifiers = new MobileVerifier[S];
	public CloudServer[] servers = new CloudServer[L];

	// δ��ɴ���
	public void init() {
		for (int i = 0; i < S; i++) {
			verifiers[i] = new MobileVerifier(BaseParams.PsCPUi[i], BaseParams.PdCPUi[i], BaseParams.PsRFi[i],
					BaseParams.PdRFi[i], BaseParams.wi[i]);
		}
		for (int j = 0; j < L; j++) {
			servers[j] = new CloudServer(BaseParams.fj[j]);
		}

	}

	/**
	 * ��У���߷����㷨
	 * 
	 * @param verifiers
	 *            �ƶ�У����
	 * @param VR
	 *            �û�У������
	 */
	public void singleAsign(MobileVerifier[] verifiers, VerificationRequest VR) {
		verifiers[0].c = (int)(VR.n*VR.P);
		verifiers[verifiers.length - 1].c = (int)(VR.n*VR.P);

	}

	/**
	 * ������ʷ����㷨
	 * 
	 * @param verifiers
	 * @param servers
	 * @param VR
	 */
	public void RandomRatioAssign(MobileVerifier[] verifiers, CloudServer[] servers, VerificationRequest VR) {
		int remain = (int) (VR.n * VR.P);
		double sumW = 0.0;
		for (int i = 0; i < S; i++) {
			if (verifiers[i].w > VR.T)
				verifiers[i].w = T;
			sumW += verifiers[i].w;
		}
		// ni=min[nwi'/sum(wi'),gi(wi')]
		for (int i = 0; i < S; i++) {
			int assign1 = verifiers[i].verBlocks(verifiers[i].w, servers[0].f);
			int assign2 = (int) (remain* verifiers[i].w / sumW);
			verifiers[i].c = (assign1 > assign2 ? assign2 : assign1);
		}
	}

	/**
	 * ������Ч���������
	 * 
	 * @param verifiers
	 *            У����
	 * @param servers
	 *            �Ʒ�����
	 * @param VR
	 *            У������
	 */
	public void EnergyEfficientAssign(MobileVerifier[] verifiers, CloudServer[] servers, VerificationRequest VR) {
		int remain = (int) (VR.n * VR.P);
		for (int i = 0; i < S; i++) {
			double t = (verifiers[i].w < VR.T ? verifiers[i].w : VR.T);
			int assigni = verifiers[i].verBlocks(t, servers[1].f);
			if (remain <= 0)
				break;
			verifiers[i].c = (remain - assigni > 0 ? assigni : remain);
			remain -= assigni;
		}
	}

	/**
	 * ��ͬУ����������㷨�µ��ܺģ�У���ߺͷ�������
	 * 
	 * @param assignAlg
	 * @return У���ߺͷ�������������Map
	 */
	public Map<String, String> energyCost(int assignAlg) {
		Map<String, String> result = new HashMap<>();
		if (assignAlg == SAVT) {// SAVT ����������
			this.singleAsign(verifiers, VR);
			for (int i = 0; i < 2; i++) {
				if (i == 0) { // ��������ܺ�
					int c = verifiers[0].c;
					double TdCPU = verifiers[0].verTime(c);
					double TdRF = TransferCost.transTime(c, n, PRIME);
					double Tproof = servers[0].proofTime(c);
					double minVerEnergy = verifiers[0].verEnergy(TdCPU + TdRF + Tproof, TdCPU, TdRF);
					double minServerEnergy = servers[0].CSPEnergy(Tproof);
					result.put(MIN_VER_ENERGY, String.valueOf(DataFilter.roundDouble(minVerEnergy, 1)));
					result.put(MIN_SERVER_ENERGY, String.valueOf(DataFilter.roundDouble(minServerEnergy, 1)));
				} else { // ��������ܺ�
					int j = verifiers.length - 1;
					int c = verifiers[j].c;
					double TdCPU = verifiers[j].verTime(c);
					double TdRF = TransferCost.transTime(c, n, PRIME);
					double Tproof = servers[servers.length - 1].proofTime(c);
					double maxVerEnergy = verifiers[j].verEnergy(TdCPU + TdRF + Tproof, TdCPU, TdRF);
					double maxServerEnergy = servers[servers.length - 1].CSPEnergy(Tproof);
					result.put(MAX_VER_ENERGY, String.valueOf(DataFilter.roundDouble(maxVerEnergy, 1)));
					result.put(MAX_SERVER_ENERGY, String.valueOf(DataFilter.roundDouble(maxServerEnergy, 1)));
				}
			}
		} else if (assignAlg == RRAVT) {// RRAVT ����������
			RandomRatioAssign(verifiers, servers, VR);
			// double totalEnergy=0.0;
			double verEnergy = 0.0;
			double serverEnergy = 0.0;
			for (int i = 0; i < S; i++) {
				int c = verifiers[i].c;
				if (c == 0)
					break;
				double TdCPU = verifiers[i].verTime(c);
				double TdRF = TransferCost.transTime(c, VR.n, PRIME);
				// Cloud Server ����͵���Ӧ�ȼ� Server0
				double Tproof = servers[0].proofTime(c);
				verEnergy += verifiers[i].verEnergy(TdCPU + TdRF + Tproof, TdCPU, TdRF);
				serverEnergy += servers[0].CSPEnergy(Tproof);
			}
			result.put(VER_ENERGY, String.valueOf(DataFilter.roundDouble(verEnergy, 1)));
			result.put(SERVER_ENERGY, String.valueOf(DataFilter.roundDouble(serverEnergy, 1)));
		} else if (assignAlg == EEAVT) {// EEAVT ����������
			EnergyEfficientAssign(verifiers, servers, VR);
			double verEnergy = 0.0;
			double serverEnergy = 0.0;
			for (int i = 0; i < S; i++) {
				int c = verifiers[i].c;
				if (c == 0)
					continue;
				double TdCPU = verifiers[i].verTime(c);
				double TdRF = TransferCost.transTime(c, VR.n, PRIME);
				// Cloud Server ����͵���Ӧ�ȼ� Server0
				double Tproof = servers[0].proofTime(c);
				verEnergy += verifiers[i].verEnergy(TdCPU + TdRF + Tproof, TdCPU, TdRF);
				serverEnergy += servers[0].CSPEnergy(Tproof);
			}
			result.put(VER_ENERGY, String.valueOf(DataFilter.roundDouble(verEnergy, 1)));
			result.put(SERVER_ENERGY, String.valueOf(DataFilter.roundDouble(serverEnergy, 1)));
		}
		return result;
	}

	/**
	 * У������ʱ�䡪��������У��������Ϊ�������û�����У������
	 * 
	 * @param assignAlg
	 * @return �����������С���ʱ��
	 */
	public Map<String, String> timeCost(int assignAlg) {
		Map<String, String> result = new HashMap<>();
		if (assignAlg == SAVT) {// SAVT �ļ���ʱ�䣬��Ȼ����ٶ���У�������㹻�Ĵ��ʱ��
			singleAsign(verifiers, VR);
			for (int i = 0; i < 2; i++) {
				if (i == 0) {// ����������ʱ��
					int c = verifiers[0].c;
					double TdCPU = verifiers[0].verTime(c);
					double TdRF = TransferCost.transTime(c, VR.n, PRIME);
					double Tproof = servers[0].proofTime(c);
					double maxt = TdCPU + TdRF + Tproof;
					result.put(MAX_TIME, String.valueOf(DataFilter.roundDouble(maxt, 1)));
				} else { // ������С���ʱ��
					int j = verifiers.length - 1;
					int c = verifiers[j].c;
					double TdCPU = verifiers[j].verTime(c);
					double TdRF = TransferCost.transTime(c, VR.n, PRIME);
					double Tproof = servers[servers.length - 1].proofTime(c);
					double mint = TdCPU + TdRF + Tproof;
					result.put(MIN_TIME, String.valueOf(DataFilter.roundDouble(mint, 1)));
				}
			}
		} else if (assignAlg == RRAVT) {// RRAVT�ļ���ʱ��
			this.RandomRatioAssign(verifiers, servers, VR);
			double t = minTimeCost();
			result.put(TIME, String.valueOf(DataFilter.roundDouble(t, 1)));
		} else if (assignAlg == EEAVT) {// EEAVT �ļ���ʱ��
			this.EnergyEfficientAssign(verifiers, servers, VR);
			double t = minTimeCost();
			result.put(TIME, String.valueOf(DataFilter.roundDouble(t, 1)));
		}
		return result;
	}

	// ��У���߶Ը���У������������С���ʱ�䡪���������û��������ʱ�������
	// ��Ҫ�ȶԵ�У���ߺͶ�У���߶Ե���У�������Ӱ�졣
	private double minTimeCost() {
		int remain = n;
		for (int i = S - 1; i >= 0; i--) { // ΪУ���߷������ݿ�
			int assigni = verifiers[i].verBlocks(verifiers[i].w, servers[1].f);
			if (remain <= 0)
				break;
			verifiers[i].c = (remain - assigni > 0 ? assigni : remain);
			remain -= assigni;
		}
		double t = 0;

		for (int j = 0; j < S; j++) {// �������ʱ��
			int c = verifiers[j].c;
			double TdCPU = verifiers[j].verTime(c);
			double TdRF = TransferCost.transTime(c, n, PRIME);
			double Tproof = servers[0].proofTime(c);
			double ti = TdCPU + TdRF + Tproof;
			if (ti > t)
				t = ti;
		}
		return t;
	}

	/**
	 * ÿ��У���ߵ�������
	 * 
	 * @param assignAlg
	 * @return
	 */
	public List<Integer> taskAssign(int assignAlg) {
		List<Integer> result = new ArrayList<>();
		if (assignAlg == SAVT) { // SAVT ��У�������������
			singleAsign(verifiers, VR);
			result.add(verifiers[0].c);
		} else if (assignAlg == RRAVT) { // RRAVT ��У�������������
			RandomRatioAssign(verifiers, servers, VR);
			for (int i = 0; i < verifiers.length; i++)
				result.add(verifiers[i].c);
		} else if (assignAlg == EEAVT) { // EEAVT ��У�������������
			EnergyEfficientAssign(verifiers, servers, VR);
			for (int i = 0; i < verifiers.length; i++)
				result.add(verifiers[i].c);
		}
		return result;
	}

}
