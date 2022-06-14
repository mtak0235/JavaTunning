package heap;

import java.util.ArrayList;
import java.util.List;

public class Std01HeapEx {
	public static void main(String[] args) throws Exception {
		byte[] d = new byte[1024 * 1024 * 100];
		List<byte[]> byteBuffer = new ArrayList<byte[]>();
		while(true) {
			d = new byte[1024 * 1024 * 100];
			byteBuffer.add(new byte[1024 * 1024 * 100]);
			System.out.println("size = " + byteBuffer.size());
			Thread.sleep(1000);
		}
	}
}
