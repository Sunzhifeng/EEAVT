package assignTask;


public class VerificationTask implements Cloneable{
	public int bs = BaseParams.bs; // ���ݿ�Ĵ�С
	public int c; // ��ս����
	public double t; // ��У���������ʱ��

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

	// У������Ľ��ȳ̶�
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
