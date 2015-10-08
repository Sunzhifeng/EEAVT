package assignTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tool.DataFilter;
import tool.Sampling;
import tool.TransferCost;

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
	public int M = BaseParams.M; // 文件大小，G
	public int n = BaseParams.n;
	public int e = BaseParams.e;
	public double pr = BaseParams.pr; // 探测率
	public int bs = BaseParams.bs;// 数据块大小
	public int T = BaseParams.T; // 用户期望完成时间
	public VerificationRequest VR = new VerificationRequest(M, n, T, pr);
	public MobileVerifier[] verifiers = new MobileVerifier[S];
	public CloudServer[] servers = new CloudServer[L];

	// 未完成代码
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
	 * 单校验者分配算法
	 * 
	 * @param verifiers
	 *            移动校验组
	 * @param VR
	 *            用户校验需求
	 */
	public void singleAsign(MobileVerifier[] verifiers, VerificationRequest VR) {
		int c = Sampling.getSampleBlocks(VR.n, e, VR.P);
		VerificationTask VT = new VerificationTask(c, VR.T);
		verifiers[0].receiveTask(VT);
		verifiers[verifiers.length - 1].receiveTask((VerificationTask) VT.clone());

	}

	/**
	 * 随机比率分配算法
	 * 
	 * @param verifiers
	 * @param servers
	 * @param VR
	 */
	public void RandomRatioAssign(MobileVerifier[] verifiers, CloudServer[] servers, VerificationRequest VR) {
		clearAssignedTasks();
		int allblocks = Sampling.getSampleBlocks(VR.n, e, VR.P);
		double sumW = 0.0;
		for (int i = 0; i < S; i++) {
			if (verifiers[i].w > VR.T)
				verifiers[i].w = VR.T;
			sumW += verifiers[i].w;
		}
		int remain=0;
		int[] remainBlocks=new int [S];//每个校验者的剩余校验块数
		int remainAllBlocks=0;//校验组总剩余校验块数
		// ni=min[nwi'/sum(wi'),gi(wi')]
		for (int i = 0; i < S; i++) {
			int assign1 = verifiers[i].verBlocks(verifiers[i].w, servers[0].f);//the max verifying blocks of verifier[i] 
			int assign2 = (int)(allblocks * verifiers[i].w / sumW);//the blocks pre-allocated to verifier[i]
			int c=0;
			if(assign1>assign2){//verifier[i] is capable.
				c=assign2;
				remainBlocks[i]=assign1-assign2;
				remainAllBlocks+=assign1-assign2;
			}else{// the pre-allocated blocks beyond to the capacity of verifier[i].
				c=assign1;
				remain+=(assign2-assign1);
				remainBlocks[i]=0;
			}
						
			verifiers[i].receiveTask(new VerificationTask(c, VR.T));
		}
		//System.out.println("remain:"+remain);
		//System.out.println("remainAllBlocks:"+remainAllBlocks);
		//allocating the remain blocks.
		if(remain>10){
			//System.out.println("re-allocating the remain blocks!");
			for(int i=0;i<S;i++){
				int temp=remainBlocks[i];
				if(temp!=0){
					int appendBlocks=(int)Math.floor((double)temp*remain/remainAllBlocks);
					verifiers[i].VT.c+=appendBlocks;
				}
				
			}
		}
	}

	/**
	 * 能量有效的任务分配
	 * 
	 * @param verifiers
	 *            校验者
	 * @param servers
	 *            云服务器
	 * @param VR
	 *            校验需求
	 */
	public void EnergyEfficientAssign(MobileVerifier[] verifiers, CloudServer[] servers, VerificationRequest VR) {
		clearAssignedTasks();
		int remain = Sampling.getSampleBlocks(VR.n, e, VR.P);
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
	
	//清除分配给校验者的数据块
	private void clearAssignedTasks() {
		for (int i = 0; i < verifiers.length; i++) {
			if (verifiers[i].VT != null)
				verifiers[i].VT.c = 0;
			
		}
	}

	// 返回最佳响应的服务器编号 ———— 初始情况分配到server 0 会有问题？？？？
	public int ServerResourceAllocating2(MobileVerifier ver) {
		int optimalServerId = 0;
		double RT1 = servers[0].responseLevel(VR);
		double eS0 = servers[0].CSPEnergy(RT1);
		double eVer0 = ver.dComputeEnergy(RT1);
		int initServerId = dispatch(ver.VT);
		optimalServerId = initServerId;
		if (optimalServerId == servers.length - 1)
			return optimalServerId;
		double RTi = servers[initServerId].responseLevel(VR);
		double eSi = servers[initServerId].CSPEnergy(RTi);
		double eVeri = ver.dComputeEnergy(RTi);
		double emin = (eSi - eS0) + (eVeri - eVer0);
		for (int j = initServerId + 1; j < servers.length; j++) {// 修正，是否可以进一步降低能耗?
			double RTj = servers[j].responseLevel(VR);
			double eSj = servers[j].CSPEnergy(RTj);
			double eVerj = ver.dComputeEnergy(RTj);
			double emin_ = (eSj - eS0) + (eVerj - eVer0);
			if (emin_ < emin) {
				emin = emin_;
				optimalServerId = j;
				System.out.println("Server save Energy!!");
			}
		}
		return optimalServerId;
	}

	public int ServerResourceAllocating(MobileVerifier ver) {
		int optimalServerId = 0;
		int initServerId = dispatch(ver.VT);
		optimalServerId = initServerId;
		if (optimalServerId == servers.length - 1)
			return optimalServerId;
		double RTi = servers[initServerId].responseLevel(VR);
		double eSi = servers[initServerId].CSPEnergy(RTi);
		double eVeri = ver.dComputeEnergy(RTi);
		double ei = eSi + eVeri;
		double emin=0;
		for (int j = initServerId + 1; j < servers.length; j++) {// 修正，是否可以进一步降低能耗?
			double RTj = servers[j].responseLevel(VR);
			double eSj = servers[j].CSPEnergy(RTj);
			double eVerj = ver.dComputeEnergy(RTj);
			double ej= (eSj+eVerj);
			double e=ej-ei;
			if (e<emin) {
				emin = e;
				optimalServerId = j;
				System.out.println("Server save Energy!!");
			}
		}
		return optimalServerId;
	}

	// 校验任务预分配的服务器编号
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
	 * 不同校验任务分配算法下的能耗（校验者和服务器）
	 * 
	 * @param assignAlg
	 * @return 校验者和服务器能量消耗Map
	 */
	public Map<String, String> energyCost(int assignAlg, boolean enableEERA) {
		Map<String, String> result = new HashMap<>();
		if (assignAlg == SAVT) {// SAVT 的能量消耗
			singleAsign(verifiers, VR);
			for (int i = 0; i < 2; i++) {
				if (i == 0) { // 计算最低能耗
					int c = verifiers[0].VT.c;
					double TdCPU = verifiers[0].verTime(c);
					double TdRF = TransferCost.transTime(c, VR.n, PRIME);
					double Tproof = servers[0].proofTime(c);
					double minVerEnergy = verifiers[0].verEnergy(TdCPU + TdRF + Tproof, TdCPU, TdRF);
					double minServerEnergy = servers[0].CSPEnergy(Tproof);
					result.put(MIN_VER_ENERGY, String.valueOf(DataFilter.roundDouble(minVerEnergy, 1)));
					result.put(MIN_SERVER_ENERGY, String.valueOf(DataFilter.roundDouble(minServerEnergy, 1)));
				} else { // 计算最大能耗
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
		} else if (assignAlg == RRAVT) {// RRAVT 的能量消耗
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
				int serverId=0;// Cloud Server 以最低的响应等级 Server0
				//int serverId=servers.length-1;// Cloud Server 以最高的响应等级 Server0
				double Tproof = servers[serverId].proofTime(c);
				verEnergy += verifiers[i].verEnergy(TdCPU + TdRF + Tproof, TdCPU, TdRF);
				serverEnergy += servers[serverId].CSPEnergy(Tproof);
			}
			result.put(VER_ENERGY, String.valueOf(DataFilter.roundDouble(verEnergy, 1)));
			result.put(SERVER_ENERGY, String.valueOf(DataFilter.roundDouble(serverEnergy, 1)));
		} else if (assignAlg == EEAVT) {// EEAVT 的能量消耗
			EnergyEfficientAssign(verifiers, servers, VR);
			double verEnergy = 0.0;
			double serverEnergy = 0.0;
			for (int i = 0; i < S; i++) {
				int c = verifiers[i].VT.c;
				if (c == 0)
					continue;
				double TdCPU = verifiers[i].verTime(c);
				double TdRF = TransferCost.transTime(c, VR.n, PRIME);
				
				int serverId=0;//服务器server0计算证据，不考虑任务的等级及响应等级			
				/* int serverId = dispatch(verifiers[i].VT);
				   if (enableEERA) {// 计算最优响应等级的server ID
					System.out.println("the orignal serverId:"+serverId);
					serverId = ServerResourceAllocating2(verifiers[i]);
					System.out.println("the optimal serverId:"+serverId);
				}*/
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
	 * 校验最少完成时间对比———单校验者和检验组，不考虑存活时间w和校验的完成时间限制T。 体现了校验能力，为了满足用户弹性校验需求
	 * 
	 * @param assignAlg
	 * @return 给定任务的最小完成时间
	 */
	public Map<String, String> timeCost(int assignAlg) {
		Map<String, String> result = new HashMap<>();
		//int cblocks = Sampling.getSampleBlocks(VR.n, e, VR.P);
		if (assignAlg == SAVT) {// SAVT 的计算时间，当然这里假定单校验者有足够的存活时间
			singleAsign(verifiers, VR);
			for (int i = 0; i < 2; i++) {
				if (i == 0) {// verifier0 的最快完成时间
					int c = verifiers[0].VT.c;
					System.out.println("c="+c);
					double TdCPU = verifiers[0].verTime(c);
					double TdRF = BaseParams.transCost(c);
					double Tproof = servers[0].proofTime(c);
					double maxt = TdCPU + TdRF + Tproof;
					result.put(MAX_TIME, String.valueOf(DataFilter.roundDouble(maxt, 1)));
				} else { // verifier S 的最快完成时间
					int j = verifiers.length - 1;
					int c = verifiers[j].VT.c;
					double TdCPU = verifiers[j].verTime(c);
					double TdRF = BaseParams.transCost(c);
					double Tproof = servers[servers.length - 1].proofTime(c);
					double mint = TdCPU + TdRF + Tproof;
					result.put(MIN_TIME, String.valueOf(DataFilter.roundDouble(mint, 1)));
				}
			}
		} else if (assignAlg == RRAVT) {// RRAVT的计算时间
			// this.RandomRatioAssign(verifiers, servers, VR);
			double t = minTimeCost();
			result.put(TIME, String.valueOf(DataFilter.roundDouble(t, 1)));
		} else if (assignAlg == EEAVT) {// EEAVT 的计算时间
			// this.EnergyEfficientAssign(verifiers, servers, VR);
			double t = minTimeCost();
			result.put(TIME, String.valueOf(DataFilter.roundDouble(t, 1)));
		}
		return result;
	}

	// 多校验者对给定校验任务量的最小完成时间，主要比对单校验者和多校验者对弹性校验需求的影响。
	private double minTimeCost() {
		int c = Sampling.getSampleBlocks(VR.n, e, VR.P);
		return verifiers[S - 1].verTime((int) c / S) + BaseParams.transCost(c)
				+ servers[0].proofTime((int) c / S);
	}

	// 考虑校验组的校验能力，考虑存活时间w，看其校验完成时间的范围，这样可以指导用户T的范围。
	public double minTimeCost2() {
	//	int c = Sampling.getSampleBlocks(VR.n, e, VR.P);
		for(int i=0;i<S;i++){
	
		}
		return 0;
	}
	

	/**
	 * 每个校验者的任务量
	 * 
	 * @param assignAlg
	 * @return
	 */
	public List<Integer> taskAssign(int assignAlg) {
		List<Integer> result = new ArrayList<>();
		if (assignAlg == SAVT) { // SAVT 的校验者任务分配量
			singleAsign(verifiers, VR);
			result.add(verifiers[0].VT.c);
		} else if (assignAlg == RRAVT) { // RRAVT 的校验者任务分配量
			RandomRatioAssign(verifiers, servers, VR);
			for (int i = 0; i < verifiers.length; i++)
				result.add(verifiers[i].VT.c);
		} else if (assignAlg == EEAVT) { // EEAVT 的校验者任务分配量
			EnergyEfficientAssign(verifiers, servers, VR);
			for (int i = 0; i < verifiers.length; i++) {
				result.add(verifiers[i].VT.c);
			}
		}
		return result;
	}
	
	//得到任务的紧急程度
	public List<Integer> taskUergentLevel(){
		List<Integer> ULs=new ArrayList<>();
		for(MobileVerifier mv:verifiers)
			ULs.add(mv.VT.urgentLevel(VR.P));
		return ULs;
	}
	
	//servers 的响应等级，使用了取整,不是很精确
	public List<Integer> serverResponseTime(){
		List<Integer> RTs=new ArrayList<>();
		for (CloudServer cs :servers)
			RTs.add((int)Math.ceil(cs.responseLevel(VR)));
		return RTs;
	}

}
