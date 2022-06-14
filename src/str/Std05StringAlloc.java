package str;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;
public class Std05StringAlloc {
    public static void main(String[] args) throws Exception {
        ThreadMXBean tmx = ManagementFactory.getThreadMXBean();
        long tid = Thread.currentThread().getId();
        long b1=((com.sun.management.ThreadMXBean) tmx).getThreadAllocatedBytes(tid);
        List<String> buffer = new ArrayList<String>();
        while (true) {
            buffer.add("abc");
            long b2 = ((com.sun.management.ThreadMXBean) tmx).getThreadAllocatedBytes(tid);
            System.out.println("buffer.size=" + buffer.size() + " alloc=" + (b2-b1));
            b1=b2;
            Thread.sleep(1000);
        }
        }
}
