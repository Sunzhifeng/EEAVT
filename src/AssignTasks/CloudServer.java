package AssignTasks;

import tool.DataFilter;

//�Ʒ�����
public class CloudServer {
	public double f; //�Ʒ������Ĺ���Ƶ��
	public static final int k=1;	//�ܺ�ϵ��
	public static final int a=3;
	public CloudServer(double f){
		this.f=f;		
	}
	
	/**
	 * ����������֤��ʱ��
	 * @param x У��Ŀ���
	 * @return  ����֤��ʱ��
	 */
	public  double proofTime(int x){
		return DataFilter.roundDouble(BaseParams.f0*BaseParams.baseCSTime(x)/f,3);
	}
	
	/**
	 * ����ʱ���ڿ�У��������
	 * @param time	 
	 * @return
	 */
	public int verBlocks(double time)
	{
		return BaseParams.verBlocks(time,f);	
	}
	
	/**
	 * ������������������ġ���t*k*(f^a)
	 * @param Tcsp
	 * @return
	 */
	public double CSPEnergy(double Tcsp){
		return Tcsp*k*Math.pow(f, a);
	}

	public double energyCost(double t,double f){
		return t*k*Math.pow(f, a);
	}

}
