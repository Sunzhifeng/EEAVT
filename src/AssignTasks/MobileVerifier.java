package AssignTasks;

import tool.DataFilter;

//移动校验者
public class MobileVerifier {
	public double PsCPU;// 静态计算功率
	public double PdCPU;// 动态增加的计算功率
	public double PsRF;// 静态传输功率
	public double PdRF;// 动态增加传输功率
	public int w; // 存活时间
	//public double st; // 校验者提供的校验服务时间
	public int c; // 分给校验者的校验任务（块数）
	// public VerificationTask verTask;//分给校验者的校验任务

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
	 * 证据检查计算时间
	 * 
	 * @param x
	 *            检查块数
	 * @return 检查时间
	 */
	public double verTime(int x) {
		return DataFilter
				.roundDouble((BaseParams.Ps0CPU + BaseParams.Pd0CPU) * BaseParams.baseVerTime(x) / (PsCPU + PdCPU), 3);
	}

	/**
	 * 给定时间内可校验的块数
	 * 
	 * @param time
	 * @param fi  计算证据的云服务器工作频率
	 * @return
	 */
	public int verBlocks(double t,double fi) {
		//f0*tver(x)/fi+(Ps0CPU+Pd0CPU)*tcsp(x)/(PsiCPU+PdiCPU)+ttran(x)
		return BaseParams.verBlocks(t, fi, PsCPU, PdCPU);
	}

	/**
	 * 校验者的能量消耗
	 * 
	 * @param t
	 *            校验过程的总耗时
	 * @param TdCPU
	 *            校验者动态CPU运行时间
	 * @param TdRF
	 *            校验者动态RF传输时间
	 * @return 消耗的总能量
	 */
	public double verEnergy(double t, double TdCPU, double TdRF) {
		return verComputeEnergy(t, TdCPU) + verTransEnergy(t, TdRF);
	}

	// 计算能耗
	private double verComputeEnergy(double t, double t2) {
		return PsCPU * t + PdCPU * t2;
	}

	// 传输能耗
	private double verTransEnergy(double t, double t2) {
		return PsRF * t + PdRF * t2;
	}

}
