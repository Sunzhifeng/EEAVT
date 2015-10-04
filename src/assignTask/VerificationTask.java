package assignTask;


public class VerificationTask implements Cloneable{
	public int bs = BaseParams.bs; // 数据块的大小
	public int c; // 挑战块数
	public double t; // 该校验任务完成时间

	public VerificationTask() {
	}

	public VerificationTask(int c, double t) {
		this.c = c;
		this.t = t;
	}

	public VerificationTask(int bs, int c, double t) {
		this.bs = bs;
		this.c = c;
		this.t = t;
	}

	// 校验任务的紧迫程度
	public int urgentLevel(double p) {
		double temp = Math.log(c) * p*p;
		return (int)Math.floor(t/temp);
	}
	
	public Object clone() {   
        try {   
            return super.clone();   
        } catch (CloneNotSupportedException e) {   
            return null;   
        }   
    }   
}
