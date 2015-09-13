package AssignTasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tool.DataFilter;
import tool.Sampling;

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
	public int n = BaseParams.n;
	public int e=BaseParams.e;
	public double pr = BaseParams.pr; // ̽����
	public int bs = BaseParams.bs;// ���ݿ��С
	public int T = BaseParams.T; // �û��������ʱ��
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
		int c=Sampling.getSampleBlocks(VR.n, e, VR.P);
		VerificationTask VT = new VerificationTask(c, VR.T);
		verifiers[0].receiveTask(VT);
		verifiers[verifiers.length - 1].receiveTask((VerificationTask) VT.clone());

	}

	/**
	 * ������ʷ����㷨
	 * 
	 * @param verifiers
	 * @param servers
	 * @param VR
	 */
	public void RandomRatioAssign(MobileVerifier[] verifiers, CloudServer[] servers, VerificationRequest VR) {
		clearAssignedTasks();
		int remain=Sampling.getSampleBlocks(VR.n, e, VR.P);
		double sumW = 0.0;
		for (int i = 0; i < S; i++) {
			if (verifiers[i].w > VR.T)
				verifiers[i].w = VR.T;
			sumW += verifiers[i].w;
		}
		// ni=min[nwi'/sum(wi'),gi(wi')]
		for (int i = 0; i < S; i++) {
			int assign1 = verifiers[i].verBlocks(verifiers[i].w, servers[0].f);
			int assign2 = (int) (remain * verifiers[i].w / sumW);
			int c = (assign1 > assign2 ? assign2 : assign1);
			verifiers[i].receiveTask(new VerificationTask(c, VR.T));
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
		clearAssignedTasks();
		int remain=Sampling.getSampleBlocks(VR.n, e, VR.P);
		for (int i = 0; i < S; i++) {
			double t = (verifiers[i].w < VR.T ? verifiers[i].w : VR.T);
			int assigni = verifiers[i].verBlocks(t, servers[0].f);
			int c = ((remain - assigni) > 0 ? assigni : remain);
			verifiers[i].receiveTask(new VerificationTask(c, t));
			remain -= assigni;
			if (remain <= 0)
				break;
		}
	}

	//���������Ӧ�ķ�������� �������� ��ʼ������䵽server 0 �������⣿������
	public int ServerResourceAllocating(MobileVerifier ver) {
		int optimalServerId = 0;
		int RT1 = servers[0].responseLevel(VR);
		double eS0 = servers[0].CSPEnergy(RT1);
		double eVer0 = ver.dComputeEnergy(RT1);
		int initServerId = dispatch(ver.VT);
		optimalServerId = initServerId;
		if (optimalServerId == servers.length - 1)
			return optimalServerId;
		int RTi = servers[initServerId].responseLevel(VR);
		double eSi = servers[initServerId].CSPEnergy(RTi);
		double eVeri = ver.dComputeEnergy(RTi);
		// double eSUP = eSi - eS0;
		// double eVerDW = eVeri - eVer0;
		double emin = eSi - eS0 + eVeri - eVer0;
		//System.out.println("emin:"+emin);
		for (int j = initServerId + 1; j < servers.length; j++) {//�������Ƿ���Խ�һ�������ܺ�?
			int RTj = servers[j].responseLevel(VR);
			double eSj = servers[j].CSPEnergy(RTj);
			double eVerj = ver.dComputeEnergy(RTj);
			double emin_ = eSj - eS0 + eVerj - eVer0;
		//	System.out.println("emin_:"+emin_);
			if (emin_ < emin) {
				emin = emin_;
				optimalServerId = j;
			}
		}
		//if (emin<0)optimalServerId=initServerId;
		return optimalServerId;
	}

	// У������Ԥ����ķ��������
	private int dispatch(VerificationTask VT) {
		int serverId = 0;
		int len = servers.length;
		double ul = VT.urgentLevel(VR.P);
		if (ul >= servers[0].responseLevel(VR)) {
			serverId = 0;
			return serverId;
		}
		if (servers[servers.length - 1].responseLevel(VR) > ul) {
			serverId = len - 1;
			return serverId;
		}
		for (int j = 1; j < len - 1; j++) {
			if (servers[j].responseLevel(VR) > ul && servers[j + 1].responseLevel(VR) <= ul) {
				serverId = j + 1;
				return serverId;
			}
		}
		return serverId;
	}

	/**
	 * ��ͬУ����������㷨�µ��ܺģ�У���ߺͷ�������
	 * 
	 * @param assignAlg
	 * @return У���ߺͷ�������������Map
	 */
	public Map<String, String> energyCost(int assignAlg,boolean enableEERA) {
		Map<String, String> result = new HashMap<>();
		if (assignAlg == SAVT) {// SAVT ����������
			singleAsign(verifiers, VR);
			for (int i = 0; i < 2; i++) {
				if (i == 0) { // ��������ܺ�
					int c = verifiers[0].VT.c;
					double TdCPU = verifiers[0].verTime(c);
					double TdRF = TransferCost.transTime(c, VR.n, PRIME);
					double Tproof = servers[0].proofTime(c);
					double minVerEnergy = verifiers[0].verEnergy(TdCPU + TdRF + Tproof, TdCPU, TdRF);
					double minServerEnergy = servers[0].CSPEnergy(Tproof);
					result.put(MIN_VER_ENERGY, String.valueOf(DataFilter.roundDouble(minVerEnergy, 1)));
					result.put(MIN_SERVER_ENERGY, String.valueOf(DataFilter.roundDouble(minServerEnergy, 1)));
				} else { // ��������ܺ�
					int j = verifiers.length - 1;
					int c = verifiers[j].VT.c;
					double TdCPU = verifiers[j].verTime(c);
					double TdRF = TransferCost.transTime(c, VR.n, PRIME);
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
				int c = verifiers[i].VT.c;
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
				int c = verifiers[i].VT.c;
				if (c == 0)
					continue;
				double TdCPU = verifiers[i].verTime(c);
				double TdRF = TransferCost.transTime(c, VR.n, PRIME);
				int serverId=dispatch(verifiers[i].VT);
				//int serverId=0;
				//System.out.println(serverId);
				if(enableEERA){// ����������Ӧ�ȼ���server ID
					serverId=ServerResourceAllocating(verifiers[i]);
					//System.out.println(serverId);
				}
				double Tproof = servers[serverId].proofTime(c);
				verEnergy += verifiers[i].verEnergy(TdCPU + TdRF + Tproof, TdCPU, TdRF);
				serverEnergy += servers[serverId].CSPEnergy(Tproof);
			}
			result.put(VER_ENERGY, String.valueOf(DataFilter.roundDouble(verEnergy, 1)));
			result.put(SERVER_ENERGY, String.valueOf(DataFilter.roundDouble(serverEnergy, 1)));
		}
		return result;
	}

	/**
	 * У���������ʱ��Աȡ�������У���ߺͼ����飬�����Ǵ��ʱ��w��У������ʱ������T�� ������У��������Ϊ�������û�����У������
	 * 
	 * @param assignAlg
	 * @return �����������С���ʱ��
	 */
	public Map<String, String> timeCost(int assignAlg) {
		Map<String, String> result = new HashMap<>();
		if (assignAlg == SAVT) {// SAVT �ļ���ʱ�䣬��Ȼ����ٶ���У�������㹻�Ĵ��ʱ��
			singleAsign(verifiers, VR);
			for (int i = 0; i < 2; i++) {
				if (i == 0) {// verifier0 ��������ʱ��
					int c = verifiers[0].VT.c;
					double TdCPU = verifiers[0].verTime(c);
					double TdRF = TransferCost.transTime(c, VR.n, PRIME);
					double Tproof = servers[0].proofTime(c);
					double maxt = TdCPU + TdRF + Tproof;
					result.put(MAX_TIME, String.valueOf(DataFilter.roundDouble(maxt, 1)));
				} else { // verifier S ��������ʱ��
					int j = verifiers.length - 1;
					int c = verifiers[j].VT.c;
					double TdCPU = verifiers[j].verTime(c);
					double TdRF = TransferCost.transTime(c, VR.n, PRIME);
					double Tproof = servers[servers.length - 1].proofTime(c);
					double mint = TdCPU + TdRF + Tproof;
					result.put(MIN_TIME, String.valueOf(DataFilter.roundDouble(mint, 1)));
				}
			}
		} else if (assignAlg == RRAVT) {// RRAVT�ļ���ʱ��
			// this.RandomRatioAssign(verifiers, servers, VR);
			double t = minTimeCost();
			result.put(TIME, String.valueOf(DataFilter.roundDouble(t, 1)));
		} else if (assignAlg == EEAVT) {// EEAVT �ļ���ʱ��
			// this.EnergyEfficientAssign(verifiers, servers, VR);
			double t = minTimeCost();
			result.put(TIME, String.valueOf(DataFilter.roundDouble(t, 1)));
		}
		return result;
	}

	// ��У���߶Ը���У������������С���ʱ�䣬��Ҫ�ȶԵ�У���ߺͶ�У���߶Ե���У�������Ӱ�졣
	private double minTimeCost() {
		int c=Sampling.getSampleBlocks(VR.n, e, VR.P);
		return verifiers[S - 1].verTime((int) c / S) + TransferCost.transTime((int) c / S, c, PRIME)
				+ servers[0].proofTime((int) c / S);
	}

	// ����У�������У�����������Ǵ��ʱ�䣬����У�����ʱ��ķ�Χ����������ָ���û�T�ķ�Χ��
	public void timeCost() {

	}
	/*
	 * private double minTimeCost() { int remain = (int) (VR.n * VR.P); for (int
	 * i = S - 1; i >= 0; i--) { // ΪУ���߷������ݿ� int assigni =
	 * verifiers[i].verBlocks(verifiers[i].w, servers[0].f); if (remain <= 0)
	 * break; verifiers[i].c = (remain - assigni > 0 ? assigni : remain); remain
	 * -= assigni; } double t = 0;
	 * 
	 * for (int j = 0; j < S; j++) {// �������ʱ�� int c = verifiers[j].c; double
	 * TdCPU = verifiers[j].verTime(c); double TdRF = TransferCost.transTime(c,
	 * n, PRIME); double Tproof = servers[0].proofTime(c); double ti = TdCPU +
	 * TdRF + Tproof; if (ti > t) t = ti; } return t; }
	 */

	private void clearAssignedTasks() {
		for (int i = 0; i < verifiers.length; i++) {
			if (verifiers[i].VT != null)
				verifiers[i].VT.c=0;

		}
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
			result.add(verifiers[0].VT.c);
		} else if (assignAlg == RRAVT) { // RRAVT ��У�������������
			RandomRatioAssign(verifiers, servers, VR);
			for (int i = 0; i < verifiers.length; i++)
				result.add(verifiers[i].VT.c);
		} else if (assignAlg == EEAVT) { // EEAVT ��У�������������
			EnergyEfficientAssign(verifiers, servers, VR);
			for (int i = 0; i < verifiers.length; i++) {
				result.add(verifiers[i].VT.c);
			}
		}
		return result;
	}

}
