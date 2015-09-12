package AssignTasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	public int S = BaseParams.S;
	public int M = BaseParams.M; // �ļ���С��G
	public int n = (int) (BaseParams.n * BaseParams.pr);
	public double pr = BaseParams.pr; // ̽����
	public int bs = BaseParams.bs;// ���ݿ��С
	public int T = 10; // �û��������ʱ��
	public VerificationRequest VRequest = new VerificationRequest(M, n, T, pr);
	public MobileVerifier[] verifiers = new MobileVerifier[S];

	// δ��ɴ���
	public void init() {
		for (int i = 0; i < S; i++) {
			verifiers[i] = new MobileVerifier(BaseParams.PsCPUi[i], BaseParams.PdCPUi[i], BaseParams.PsRFi[i],
					BaseParams.PdRFi[i], BaseParams.wi[i]);
		}

	}

	/**
	 * ������ʷ����㷨
	 * 
	 * @param F
	 *            У���߹���Ƶ�ʼ���
	 * @param W
	 *            У���ߵĴ��ʱ�伯��
	 * @param r1
	 *            Ƶ��Ȩ��
	 * @return �����У���ߵĵ����ݿ鼯��
	 */
	public void RandomRatioAssign(MobileVerifier[] verifiers, CloudServer[] servers, VerificationRequest VR) {
		int n = (int) (VR.n * VR.P);
		double sumW = 0.0;
		for (int i = 0; i < S; i++) {
			if (verifiers[i].w > VR.T)
				verifiers[i].w = T;
			sumW += verifiers[i].w;
		}
		// ni=min[nwi'/sum(wi'),gi(wi')]
		for (int i = 0; i < S; i++) {
			int assign1 = verifiers[i].verBlocks(verifiers[i].w, servers[1].f);
			int assign2 = (int) (n * verifiers[i].w / sumW);
			verifiers[i].c = (assign1 > assign2 ? assign2 : assign1);
		}
	}

	/**
	 * ������Ч���������
	 * 
	 * @param verifiers
	 *            У����
	 * @param VR
	 *            У������
	 */
	public void EnergyEfficientAssign(MobileVerifier[] verifiers, CloudServer[] servers, VerificationRequest VR) {
		int n = (int) (VR.n * VR.P);
		int remain = n;
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
	 * ��У���߷����㷨
	 * 
	 * @param verifiers
	 * @param VR
	 */
	public void singleAsign(MobileVerifier[] verifiers, VerificationRequest VR) {
		verifiers[0].c = n;
		verifiers[verifiers.length - 1].c = n;

	}

	/**
	 * ��ͬУ����������㷨�µ��ܺģ�У���ߺͷ�������
	 * 
	 * @param verifiers
	 * @param servers
	 * @param VR
	 * @param assignAlg
	 * @return У���ߺͷ�������������Map
	 */
	public Map<String, String> energyCost(MobileVerifier[] verifiers, CloudServer[] servers, VerificationRequest VR,
			int assignAlg) {
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
					result.put("minVerEnergy", String.valueOf(minVerEnergy));
					result.put("minServerEnergy", String.valueOf(minServerEnergy));
				} else { // ��������ܺ�
					int j = verifiers.length - 1;
					int c = verifiers[j].c;
					double TdCPU = verifiers[j].verTime(c);
					double TdRF = TransferCost.transTime(c, n, PRIME);
					double Tproof = servers[servers.length - 1].proofTime(c);
					double maxVerEnergy = verifiers[j].verEnergy(TdCPU + TdRF + Tproof, TdCPU, TdRF);
					double maxServerEnergy = servers[0].CSPEnergy(Tproof);
					result.put("maxVerEnergy", String.valueOf(maxVerEnergy));
					result.put("maxServerEnergy", String.valueOf(maxServerEnergy));
				}
			}
		} else if (assignAlg == RRAVT) {// RRAVT ����������
			this.RandomRatioAssign(verifiers, servers, VR);
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
			result.put("verEnergy", String.valueOf(verEnergy));
			result.put("serverEnergy", String.valueOf(serverEnergy));
		} else if (assignAlg == EEAVT) {// EEAVT ����������
			this.EnergyEfficientAssign(verifiers, servers, VR);
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
			result.put("verEnergy", String.valueOf(verEnergy));
			result.put("serverEnergy", String.valueOf(serverEnergy));
		}
		return result;
	}

	/**
	 * У������ʱ�䡪��������У��������Ϊ�������û�����У������
	 * 
	 * @param verifiers
	 * @param servers
	 * @param VR
	 * @param assignAlg
	 * @return �����������С���ʱ��
	 */
	public Map<String, String> timeCost(MobileVerifier[] verifiers, CloudServer[] servers, VerificationRequest VR,
			int assignAlg) {
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
					result.put("maxt", String.valueOf(maxt));
				} else { // ������С���ʱ��
					int j = verifiers.length - 1;
					int c = verifiers[j].c;
					double TdCPU = verifiers[j].verTime(c);
					double TdRF = TransferCost.transTime(c, VR.n, PRIME);
					double Tproof = servers[servers.length - 1].proofTime(c);
					double mint = TdCPU + TdRF + Tproof;
					result.put("mint", String.valueOf(mint));
				}
			}
		} else if (assignAlg == RRAVT) {// RRAVT�ļ���ʱ��
			this.RandomRatioAssign(verifiers, servers, VR);
			double t = minTimeCost(verifiers, servers);
			result.put("t", String.valueOf(t));
		} else if (assignAlg == EEAVT) {// EEAVT �ļ���ʱ��
			this.EnergyEfficientAssign(verifiers, servers, VR);
			double t = minTimeCost(verifiers, servers);
			result.put("t", String.valueOf(t));
		}
		return result;
	}

	// ��У���߶Ը���У������������С���ʱ�䡪���������û��������ʱ�������
	// ��Ҫ�ȶԵ�У���ߺͶ�У���߶Ե���У�������Ӱ�졣
	private double minTimeCost(MobileVerifier[] verifiers, CloudServer[] servers) {
		int remain = n;
		for (int i = S - 1; i >= 0; i--) {
			int assigni = verifiers[i].verBlocks(verifiers[i].w, servers[1].f);
			if (remain <= 0)
				break;
			verifiers[i].c = (remain - assigni > 0 ? assigni : remain);
			remain -= assigni;
		}
		double t = 0;
		for (int j = 0; j < S; j++) {
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
	 * @param verifiers
	 * @param VR
	 * @param assignAlg
	 * @return
	 */
	public List<Integer> taskAssign(MobileVerifier[] verifiers, CloudServer[] servers, VerificationRequest VR,
			int assignAlg) {
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

	public static void main(String[] args) {

		/*
		 * //����У��ʱ�䡢���ʱ�䷶Χ double baseVerTimen=baseVerTime(n); double
		 * max=verTime(n, F[0]); double min=verTime(n/S,F[S-1]);
		 * 
		 * //����Ƶ���µ��ܺ� double baseEnergyCost=EnergyCost.energyCost(baseVerTimen,
		 * f0); StdOut.println(baseVerTimen+"\t"+DataFilter.roundDouble(
		 * baseEnergyCost,3));
		 * 
		 * //��Ƶ�ʣ���ʱ��������ܺ� double minEnergy=EnergyCost.energyCost(max, F[0]);
		 * StdOut.println(max+"\t"+DataFilter.roundDouble(minEnergy,3));
		 * 
		 * //��Ƶ�ʣ���ʱ���ø��ܺ� double maxEnergy=EnergyCost.energyCost(max, F[S-1])*S;
		 * StdOut.println(min+"\t"+DataFilter.roundDouble(maxEnergy,3));
		 * 
		 * for(int k=0;k<1;k++){ double r1=0.1+0.4*(k+1);//�ɱ�Ƶ��Ȩ��ֵ int []
		 * ns=assignBlocks(F, W,1.5);//Ϊÿ��У���߷�����ʵ����ݿ� int sum=0;
		 * 
		 * double energyCost=0.0; for(int j=0;j<S;j++){
		 * energyCost+=EnergyCost.energyCost(fTime[j], F[j]); } }
		 */
	}
}
