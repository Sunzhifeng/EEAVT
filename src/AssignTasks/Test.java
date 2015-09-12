package AssignTasks;

import java.util.List;
import java.util.Map;

public class Test {
	public static void print(String s) {
		System.out.print(s);
	}
	public static <T> void print(List<T> list){
		System.out.print(list);
	}

	
	public static void main(String[] args) {
		AssignTask AT = new AssignTask();
		AT.init();
		
		//校验时间
		print("# Time"+"\n");
		Map<String, String> tSAVT = AT.timeCost(AssignTask.SAVT);
		print(tSAVT.get(AssignTask.MIN_TIME)+"\t");
		print(tSAVT.get(AssignTask.MAX_TIME));
		print("\n");
		
		Map<String, String> tRRAVT = AT.timeCost(AssignTask.RRAVT);
		print(tRRAVT.get(AssignTask.TIME));
		print("\n");

		Map<String, String> tEEAVT = AT.timeCost(AssignTask.EEAVT);
		print(tEEAVT.get(AssignTask.TIME));
		print("\n");
		
		//校验能耗
		print("# Energy"+"\n");
		Map<String,String> eSAVT = AT.energyCost(AssignTask.SAVT);
		print(eSAVT.get(AssignTask.MAX_VER_ENERGY)+"\t"+eSAVT.get(AssignTask.MAX_SERVER_ENERGY)+"\n");
		print(eSAVT.get(AssignTask.MIN_VER_ENERGY)+"\t"+eSAVT.get(AssignTask.MIN_SERVER_ENERGY)+"\n");
		
		Map<String,String> eRRAVT = AT.energyCost(AssignTask.RRAVT);
		print(eRRAVT.get(AssignTask.VER_ENERGY)+"\t"+eRRAVT.get(AssignTask.SERVER_ENERGY)+"\n");
		
		Map<String,String> eEEAVT = AT.energyCost(AssignTask.EEAVT);
		print(eEEAVT.get(AssignTask.VER_ENERGY)+"\t"+eEEAVT.get(AssignTask.SERVER_ENERGY)+"\n");
		
		//校验的任务量
		print("# Time"+"\n");
		List<Integer> taSAVT = AT.taskAssign(AssignTask.SAVT);
		print(taSAVT);
		print("\n");

		List<Integer> taRRAVT = AT.taskAssign(AssignTask.RRAVT);
		print(taRRAVT);
		print("\n");
		
		List<Integer> taEEAVT = AT.taskAssign(AssignTask.EEAVT);
		print(taEEAVT);
		print("\n");

	}

}
