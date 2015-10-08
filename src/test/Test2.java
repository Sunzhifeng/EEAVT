package test;

public class Test2 {

	// find the location of given a element in a array 'a' ordered by desc.
	public static int search(int[] a, int b) {
		int len = a.length;
		int serverId = 0;
		if (b >= a[0]) {
			serverId = 0;
			return serverId;
		} else if (a[len - 1] > b) {
			serverId = len - 1;
			return serverId;
		}
		for (int j = 1; j < len - 1; j++) {
			if (a[j] > b && a[j + 1] <= b) {
				serverId = j + 1;
				return serverId;
			}
		}
		return serverId;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int a[] = { 9, 8, 5, 3, 2 };
		int b[] = { 10, 6, 4, 3, 1 };
		for (int i : b) {
			int c = search(a, i);
			System.out.println(c);
		}
	}

}
