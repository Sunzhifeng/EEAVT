package test;

import java.util.List;
import java.util.Map;
import assignTask.AssignTask;

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


	public static void main(String[] args) {
		int[] T={};
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

}
