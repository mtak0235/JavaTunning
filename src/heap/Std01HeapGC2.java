package heap;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Std01HeapGC2 {
	public static void main(String[] args) throws Exception {
		List<byte[]> byteBuffer = new ArrayList<byte[]>();
		MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
		List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
		long tid = Thread.currentThread().getId();
		while(true) {
			add(byteBuffer, 1024*1024*100);
			byteBuffer.add(new byte[1024 * 1024 * 100]);
			MemoryUsage mu = mbean.getHeapMemoryUsage();
			System.out.println(new Date() + "init=" + g(mu.getInit()) + "use=" + g(mu.getUsed()) + "commit" + g(mu.getCommitted()) + "max=" + g(mu.getMax()));
			for (GarbageCollectorMXBean mx : gcBeans) {
				String name = mx.getName();
				long c = mx.getCollectionCount();
				long t = mx.getCollectionTime();
				System.out.println("\t" + name + " " + c + " " + t +"ms");
				}
			Thread.sleep(1000);
		}
	}
	
	private static void add(List<byte []> bf, int i) {
		bf.add(new byte[i]);
	}
	
	private static String g(long m) {
		double d = m/1024.0/1024.0;
		return String.format("%5.2fm", d);
	}
}
