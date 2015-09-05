package AssignTasks;

import tool.DataFilter;

//�ƶ�У����
public class MobileVerifier {	
	public double PsCPU;//��̬���㹦��
	public double PdCPU;//��̬���ӵļ��㹦��
	public double PsRF;//��̬���书��
	public double PdRF;//��̬���Ӵ��书��
	public int w;	   //���ʱ��
	public double st; //У�����ṩ��У�����ʱ��
	public int c;	//�ָ�У���ߵ�У�����񣨿�����
	//public VerificationTask verTask;//�ָ�У���ߵ�У������
	
	//min constructor
	public MobileVerifier(){}
	
	//max constructor
	public MobileVerifier(double PsCPU, double PdCPU, double PsRF,double PdRF,int w){
		this.PsCPU=PsCPU;
		this.PdCPU=PdCPU;
		this.PsRF=PsRF;
		this.PdRF=PdRF;
		this.w=w;		
	}
		
	/**
	 * ֤�ݼ�����ʱ��
	 * @param x		������	 
	 * @return  	���ʱ��
	 */
	public double verTime(int x){
		return DataFilter.roundDouble((BaseParams.Ps0CPU+BaseParams.Pd0CPU)*BaseParams.baseVerTime(x)/(PsCPU+PdCPU),3);
	}	
	
	/**
	 * ����ʱ���ڿ�У���
	 * @param time
	 * @return
	 */
	public int verBlocks(double t){
		return 0;
	}
	
	/**
	 * ����ʱ�����ļ���У���ߵ���������(�������ĺʹ������ģ�
	 * @param TsCPU		��̬CPU����ʱ��
	 * @param TdCPU		��̬CPU����ʱ��
	 * @param TsRF		��̬RF����ʱ��
	 * @param TdRF		��̬RF����ʱ��
	 * @return			���ĵ�������
	 */
	public  double verEnergy(double TsCPU,double TdCPU,double TsRF,double TdRF){
		return verComputeEnergy(TsCPU,TdCPU)+verTransEnergy(TsRF,TdRF);
	}
	
	//�����ܺ�
	private  double verComputeEnergy(double t1,double t2){
		return PsCPU*t1+PdCPU*t2;
	}

	//�����ܺ�
	private  double verTransEnergy(double t1,double t2){
		return PsRF*t1+PdRF*t2;
	}

	
/*	public VerificationTask getVerTask() {
		return verTask;
	}

	public void setVerTask(VerificationTask verTask) {
		this.verTask = verTask;
	}
*/
}
