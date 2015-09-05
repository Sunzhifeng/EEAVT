package AssignTasks;

import java.util.Random;

import tool.DataFilter;
import tool.EnergyCost;


/**
 * ���У���߷������ݿ�
 * @author MichaelSun
 * @since 2015.9.5
 *
 */
public class AssignTask {	
	public int S=BaseParams.S;
	public int M=BaseParams.M;		//�ļ���С��G
	public int n=BaseParams.n;
	public double pr=BaseParams.pr; //̽����	
	public int bs =BaseParams.bs;//���ݿ��С
	public int T=10;		     //�û��������ʱ��
	public VerificationRequest vRequest=new VerificationRequest(M,n,T,pr);
	public MobileVerifier [] verifiers=new MobileVerifier[S];
		
	//δ��ɴ���
	public void init(){
		for(int i=0;i<S;i++){
			verifiers[i]=new MobileVerifier(BaseParams.PsCPUi[i],
											BaseParams.PdCPUi[i],
											BaseParams.PsRFi[i],
											BaseParams.PdRFi[i],										
											BaseParams.wi[i]);
		}
	}
	
	/**
	 * ������ʷ����㷨
	 * @param F		У���߹���Ƶ�ʼ���
	 * @param W		У���ߵĴ��ʱ�伯��
	 * @param r1	Ƶ��Ȩ��
	 * @return		�����У���ߵĵ����ݿ鼯��
	 */
	public void RandomRatioAssign(MobileVerifier [] verifiers, VerificationRequest VR){
		double []ws=new double[S];
		double sumW=0.0;		
		for(int i=0;i<S;i++){
			if(verifiers[i].w>VR.T)verifiers[i].w=T;
			sumW+=verifiers[i].w;			
		}
		//ni=min[nwi'/sum(wi'),gi(wi')]
		for(int i=0;i<S;i++){			
			int assign1=verifiers[i].verBlocks(verifiers[i].w);
			int assign2=(int)(VR.n*verifiers[i].w/sumW);
			verifiers[i].c=(assign1>assign2?assign2:assign1);			
		}
	}
	
	/**
	 * ������Ч���������
	 * @param verifiers  У����	
	 * @param VR	 У������
	 */
	public void  EEAVT(MobileVerifier [] verifiers, VerificationRequest VR){
		int remain=n;
		for(int i=0;i<S;i++){
			double t=(verifiers[i].w<VR.T?verifiers[i].w:VR.T);
			int assigni=verifiers[i].verBlocks(t);
			if(remain<=0)break;			
			verifiers[i].c=(remain-assigni>0?assigni:remain);			
			remain-=assigni;
		}
	}
	
	/**
	 * ��У���߷����㷨
	 * @param verifiers
	 * @param VR
	 */
	public void singleAsign(MobileVerifier [] verifiers, VerificationRequest VR){
		
	}

	//�ܺ�
	//У������
	
	//����double�����е����Ԫ��
	public static double findmax(double[]a){
		double max=a[0];
		for(int i=1;i<a.length;i++){
			if(max<a[i])max=a[i];
		}
		return max;
	}

	public static void main(String []args){			
		
		/*//����У��ʱ�䡢���ʱ�䷶Χ
		double baseVerTimen=baseVerTime(n);
		double max=verTime(n, F[0]);
		double min=verTime(n/S,F[S-1]);
		
		//����Ƶ���µ��ܺ�
		double baseEnergyCost=EnergyCost.energyCost(baseVerTimen, f0);
		StdOut.println(baseVerTimen+"\t"+DataFilter.roundDouble(baseEnergyCost,3));

		//��Ƶ�ʣ���ʱ��������ܺ�
		double minEnergy=EnergyCost.energyCost(max, F[0]);
		StdOut.println(max+"\t"+DataFilter.roundDouble(minEnergy,3));

		//��Ƶ�ʣ���ʱ���ø��ܺ�
		double maxEnergy=EnergyCost.energyCost(max, F[S-1])*S;
		StdOut.println(min+"\t"+DataFilter.roundDouble(maxEnergy,3));

		for(int k=0;k<1;k++){
			double r1=0.1+0.4*(k+1);//�ɱ�Ƶ��Ȩ��ֵ			
			int [] ns=assignBlocks(F, W,1.5);//Ϊÿ��У���߷�����ʵ����ݿ�
			int sum=0;
		
			double energyCost=0.0;
			for(int j=0;j<S;j++){				
				energyCost+=EnergyCost.energyCost(fTime[j], F[j]);
			}			
		}*/
	}
}
