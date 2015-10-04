package assignTask;

import tool.DataFilter;
import tool.Sampling;

//云服务器
public class CloudServer {
	public double f; // 云服务器的工作频率
	public static final int k = 1; // 能耗系数
	public static final int a = 3;

	public CloudServer(double f) {
		this.f = f;
	}

	/**
	 * 服务器计算证据时间
	 * 
	 * @param x
	 *            校验的块数
	 * @return 计算证据时间
	 */
	public double proofTime(int x) {
		return DataFilter.roundDouble(BaseParams.f0 * BaseParams.baseCSTime(x) / f, 3);
	}

	/**
	 * 给定时间内可校验最大块数
	 * 
	 * @param time
	 * @return
	 */
	public int verBlocks(double time) {
		return BaseParams.verBlocks(time, f);
	}

	/**
	 * 云服务器的计算能量消耗
	 * 
	 * @param Tcsp
	 * @return
	 */
	public double CSPEnergy(double Tcsp) {
		// t*k*(f^a)
		return Tcsp * k * Math.pow(f, a);
	}

	// 服务器的相应等级————对指定用户的校验需求而言
	/*
	 * public int responseLevel(VerificationRequest VR){ return
	 * (int)Math.ceil(proofTime(VR.blocks())/(Math.log(VR.n)*VR.P)); }
	 */
	public double responseLevel(VerificationRequest VR) {
		int blocks=Sampling.getSampleBlocks(VR.n,BaseParams.e, VR.P);
		return (proofTime(VR.blocks()) / Math.log(blocks));
	}
}
