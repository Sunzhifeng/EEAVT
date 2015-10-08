package test;

import java.util.List;
import java.util.Map;
import assignTask.AssignTask;
import assignTask.BaseParams;

public class TestAssignTask {
	public static void print(String s) {
		System.out.print(s);
	}

	public static <T> void print(List<T> list) {
		System.out.print(list);
	}

	public static int sum(List<Integer> list) {
		int sum = 0;
		for (int i : list) {
			sum += i;
		}
		return sum;
	}

	public static void test() {
		AssignTask t = new AssignTask();
		AssignTask e = new AssignTask();
		AssignTask ta = new AssignTask();
		t.init();
		e.init();
		ta.init();
		// ---------------------------------------------
		print("# TIME" + "\n");// 校验能力的比较，表现在给定任务下的校验完成时间范围
		Map<String, String> tSAVT = t.timeCost(AssignTask.SAVT);
		print(tSAVT.get(AssignTask.MIN_TIME) + "\t");
		print(tSAVT.get(AssignTask.MAX_TIME));
		print("\n");

		Map<String, String> tRRAVT = t.timeCost(AssignTask.RRAVT);
		print(tRRAVT.get(AssignTask.TIME));
		print("\n");

		Map<String, String> tEEAVT = t.timeCost(AssignTask.EEAVT);
		print(tEEAVT.get(AssignTask.TIME));
		print("\n\n");

		// -----------------------------------------
		print("# ENERGY" + "\n");
		Map<String, String> eSAVT = e.energyCost(AssignTask.SAVT, false);
		print(eSAVT.get(AssignTask.MIN_VER_ENERGY) + "\t" + eSAVT.get(AssignTask.MIN_SERVER_ENERGY) + "\n");
		print(eSAVT.get(AssignTask.MAX_VER_ENERGY) + "\t" + eSAVT.get(AssignTask.MAX_SERVER_ENERGY) + "\n");

		Map<String, String> eRRAVT = e.energyCost(AssignTask.RRAVT, false);
		print(eRRAVT.get(AssignTask.VER_ENERGY) + "\t" + eRRAVT.get(AssignTask.SERVER_ENERGY) + "\n");

		Map<String, String> eEEAVT = e.energyCost(AssignTask.EEAVT, true);
		print(eEEAVT.get(AssignTask.VER_ENERGY) + "\t" + eEEAVT.get(AssignTask.SERVER_ENERGY) + "\n");
		print("\n");

		// ----------------------------------------------
		print("# TASKS" + "\n");
		List<Integer> taSAVT = ta.taskAssign(AssignTask.SAVT);
		print(taSAVT + "=" + sum(taSAVT) + "\n");

		List<Integer> taRRAVT = ta.taskAssign(AssignTask.RRAVT);
		print(taRRAVT + "=" + sum(taRRAVT) + "\n");

		List<Integer> taEEAVT = ta.taskAssign(AssignTask.EEAVT);
		print(taEEAVT + "=" + sum(taEEAVT) + "\n");

		// -------------------------------------------------------
		print("# UL and RT" + "\n");
		List<Integer> ULs = ta.taskUergentLevel();
		print(ULs);

		List<Integer> RTs = ta.serverResponseTime();
		print(RTs);

	}

	public static void testTime() {
		AssignTask t = new AssignTask();
		t.init();
		//print("# TIME" + "\n");// 校验能力的比较，表现在给定任务下的校验完成时间范围
		Map<String, String> tSAVT = t.timeCost(AssignTask.SAVT);
		Map<String, String> tRRAVT = t.timeCost(AssignTask.RRAVT);
		Map<String, String> tEEAVT = t.timeCost(AssignTask.EEAVT);
		print(tSAVT.get(AssignTask.MIN_TIME) + "\t" + tSAVT.get(AssignTask.MAX_TIME) + "\t"
				+ tRRAVT.get(AssignTask.TIME) + "\t" + tEEAVT.get(AssignTask.TIME) + "\n");
	}

	public static void testEnergy() {

		AssignTask at = new AssignTask();
		at.init();
		// print("# ENERGY" + "\n");
		Map<String, String> eSAVT = at.energyCost(AssignTask.SAVT, false);
		Map<String, String> eEEAVT = at.energyCost(AssignTask.EEAVT, true);
		Map<String, String> eRRAVT = at.energyCost(AssignTask.RRAVT, false);

		print(eSAVT.get(AssignTask.MIN_VER_ENERGY) + "\t" + eSAVT.get(AssignTask.MIN_SERVER_ENERGY) + "\t"
				+ eSAVT.get(AssignTask.MAX_VER_ENERGY) + "\t" + eSAVT.get(AssignTask.MAX_SERVER_ENERGY) + "\t"
				+ eRRAVT.get(AssignTask.VER_ENERGY) + "\t" + eRRAVT.get(AssignTask.SERVER_ENERGY) + "\t"
				+ eEEAVT.get(AssignTask.VER_ENERGY) + "\t" + eEEAVT.get(AssignTask.SERVER_ENERGY) + "\n");
	}

	public static void testTask() {
		AssignTask at = new AssignTask();
		at.init();
		print("# TASKS" + "\n");
		List<Integer> taSAVT = at.taskAssign(AssignTask.SAVT);
		print(taSAVT + "=" + sum(taSAVT) + "\n");

		List<Integer> taRRAVT = at.taskAssign(AssignTask.RRAVT);
		print(taRRAVT + "=" + sum(taRRAVT) + "\n");

		List<Integer> taEEAVT = at.taskAssign(AssignTask.EEAVT);
		print(taEEAVT + "=" + sum(taEEAVT) + "\n");
	}

	public static void main(String[] args) {
		int n = 10000;
		double pr = 1;
		double[] PsCPUi = { 2.0, 2.2, 2.4, 2.6, 2.8, 3.0, 3.2, 3.4, 3.6, 3.8 };
		double[] PdCPUi = { 4.0, 4.2, 4.4, 4.6, 4.8, 5.0, 5.2, 5.4, 5.6, 5.8 };
		double[] fj = { 1.8,2.1,2.4,2.7,3.0};
		int[] w = { 500, 500, 500, 500, 500, 500, 500, 500, 500, 500 };
		int count = 1;
		int start = 19;
		int scale = 15;
		// int []T={6,10,20};
		int[] T = new int[count];
		for (int i = 0; i < count; i++) {
			T[i] = start + i * scale;
		}
		//double []prs={0.9,0.91,0.92,0.93,0.94,0.95,0.96,0.97,0.98,0.99,1};
		
		for (int t : T) {
			BaseParams.T = t;
			BaseParams.wi = w;
			BaseParams.pr = pr;
			BaseParams.n = n;
			BaseParams.PsCPUi=PsCPUi;
			BaseParams.PdCPUi=PdCPUi;
			BaseParams.fj=fj;
		    //testTime();
			testEnergy();
		    testTask();
		}
	}

}
