package AssignTasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 多校验者分配数据块
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
	public int M = BaseParams.M; // 文件大小，G
	public int n = BaseParams.n;
	public double pr = BaseParams.pr; // 探测率
	public int bs = BaseParams.bs;// 数据块大小
	public int T = 10; // 用户期望完成时间
	public VerificationRequest VRequest = new VerificationRequest(M, n, T, pr);
	public MobileVerifier[] verifiers = new MobileVerifier[S];

	// 未完成代码
	public void init() {
		for (int i = 0; i < S; i++) {
			verifiers[i] = new MobileVerifier(BaseParams.PsCPUi[i], BaseParams.PdCPUi[i], BaseParams.PsRFi[i],
					BaseParams.PdRFi[i], BaseParams.wi[i]);
		}

	}

	/**
	 * 随机比率分配算法
	 * 
	 * @param F
	 *            校验者工作频率集合
	 * @param W
	 *            校验者的存活时间集合
	 * @param r1
	 *            频率权重
	 * @return 分配给校验者的的数据块集合
	 */
	public void RandomRatioAssign(MobileVerifier[] verifiers, VerificationRequest VR) {
		int n = (int) (VR.n * VR.P);
		double sumW = 0.0;
		for (int i = 0; i < S; i++) {
			if (verifiers[i].w > VR.T)
				verifiers[i].w = T;
			sumW += verifiers[i].w;
		}
		// ni=min[nwi'/sum(wi'),gi(wi')]
		for (int i = 0; i < S; i++) {
			int assign1 = verifiers[i].verBlocks(verifiers[i].w);
			int assign2 = (int) (n * verifiers[i].w / sumW);
			verifiers[i].c = (assign1 > assign2 ? assign2 : assign1);
		}
	}

	/**
	 * 能量有效的任务分配
	 * 
	 * @param verifiers
	 *            校验者
	 * @param VR
	 *            校验需求
	 */
	public void EnergyEfficientAssign(MobileVerifier[] verifiers, VerificationRequest VR) {
		int n = (int) (VR.n * VR.P);
		int remain = n;
		for (int i = 0; i < S; i++) {
			double t = (verifiers[i].w < VR.T ? verifiers[i].w : VR.T);
			int assigni = verifiers[i].verBlocks(t);
			if (remain <= 0)
				break;
			verifiers[i].c = (remain - assigni > 0 ? assigni : remain);
			remain -= assigni;
		}
	}

	/**
	 * 单校验者分配算法
	 * 
	 * @param verifiers
	 * @param VR
	 */
	public void singleAsign(MobileVerifier[] verifiers, VerificationRequest VR) {
		int n = (int) (VR.n * VR.P);
		verifiers[0].c = n;
		verifiers[verifiers.length - 1].c = n;

	}

	// 能耗
	public Map<String, String> energyCost(MobileVerifier[] verifiers, CloudServer[] servers, VerificationRequest VR,
			int assignAlg) {
		Map<String, String> result = new HashMap<>();
		if (assignAlg == SAVT) {// SAVT 的能量消耗
			this.singleAsign(verifiers, VR);
			for (int i = 0; i < 2; i++) {
				if (i == 0) { // 计算最低能耗
					int c = verifiers[0].c;
					double TdCPU = verifiers[0].verTime(c);
					double TdRF = TransferCost.transTime(c, VR.n, PRIME);
					double Tproof = servers[0].proofTime(c);
					double minE = verifiers[0].verEnergy(TdCPU + TdRF + Tproof, TdCPU, TdRF);
					result.put("minE", String.valueOf(minE));
				} else { // 计算最大能耗
					int j = verifiers.length - 1;
					int c = verifiers[j].c;
					double TdCPU = verifiers[j].verTime(c);
					double TdRF = TransferCost.transTime(c, VR.n, PRIME);
					double Tproof = servers[servers.length - 1].proofTime(c);
					double maxE = verifiers[j].verEnergy(TdCPU + TdRF + Tproof, TdCPU, TdRF);
					result.put("maxE", String.valueOf(maxE));
				}
			}
		} else if (assignAlg == RRAVT) {// RRAVT 的能量消耗
			this.RandomRatioAssign(verifiers, VR);
		} else if (assignAlg == EEAVT) {// EEAVT 的能量消耗
			this.EnergyEfficientAssign(verifiers, VR);
		}

		return result;
	}

	// 校验时间
	public Map<String, String> timeCost(MobileVerifier[] verifiers, CloudServer[] servers, VerificationRequest VR,
			int assignAlg) {
		Map<String, String> result = new HashMap<>();
		if (assignAlg == SAVT) {// SAVT 的计算时间
			singleAsign(verifiers, VR);
			for (int i = 0; i < 2; i++) {
				if (i == 0) {// 计算最大完成时间
					int c = verifiers[0].c;
					double TdCPU = verifiers[0].verTime(c);
					double TdRF = TransferCost.transTime(c, VR.n, PRIME);
					double Tproof = servers[0].proofTime(c);
					double maxt = TdCPU + TdRF + Tproof;
					result.put("maxt", String.valueOf(maxt));
				} else { // 计算最小完成时间
					int j = verifiers.length - 1;
					int c = verifiers[j].c;
					double TdCPU = verifiers[j].verTime(c);
					double TdRF = TransferCost.transTime(c, VR.n, PRIME);
					double Tproof = servers[servers.length - 1].proofTime(c);
					double mint = TdCPU + TdRF + Tproof;
					result.put("mint", String.valueOf(mint));
				}
			}
		} else if (assignAlg == RRAVT) {// RRAVT 的计算时间
			this.RandomRatioAssign(verifiers, VR);
		} else if (assignAlg == EEAVT) {// EEAVT 的计算时间
			this.EnergyEfficientAssign(verifiers, VR);
		}
		return result;
	}

	// 任务量
	public List<Integer> taskAssign(MobileVerifier[] verifiers, VerificationRequest VR, int assignAlg) {
		List<Integer> result = new ArrayList<>();
		if (assignAlg == SAVT) { // SAVT 的校验者任务分配量
			singleAsign(verifiers, VR);
			result.add(verifiers[0].c);
		} else if (assignAlg == RRAVT) { // RRAVT 的校验者任务分配量
			RandomRatioAssign(verifiers, VR);
			for (int i = 0; i < verifiers.length; i++)
				result.add(verifiers[i].c);
		} else if (assignAlg == EEAVT) { // EEAVT 的校验者任务分配量
			EnergyEfficientAssign(verifiers, VR);
			for (int i = 0; i < verifiers.length; i++)
				result.add(verifiers[i].c);
		}
		return result;
	}

	public static void main(String[] args) {

		/*
		 * //基本校验时间、完成时间范围 double baseVerTimen=baseVerTime(n); double
		 * max=verTime(n, F[0]); double min=verTime(n/S,F[S-1]);
		 * 
		 * //基本频率下的能耗 double baseEnergyCost=EnergyCost.energyCost(baseVerTimen,
		 * f0); StdOut.println(baseVerTimen+"\t"+DataFilter.roundDouble(
		 * baseEnergyCost,3));
		 * 
		 * //低频率，长时间获得最低能耗 double minEnergy=EnergyCost.energyCost(max, F[0]);
		 * StdOut.println(max+"\t"+DataFilter.roundDouble(minEnergy,3));
		 * 
		 * //高频率，短时间获得高能耗 double maxEnergy=EnergyCost.energyCost(max, F[S-1])*S;
		 * StdOut.println(min+"\t"+DataFilter.roundDouble(maxEnergy,3));
		 * 
		 * for(int k=0;k<1;k++){ double r1=0.1+0.4*(k+1);//可变频率权重值 int []
		 * ns=assignBlocks(F, W,1.5);//为每个校验者分配合适的数据块 int sum=0;
		 * 
		 * double energyCost=0.0; for(int j=0;j<S;j++){
		 * energyCost+=EnergyCost.energyCost(fTime[j], F[j]); } }
		 */
	}
}
