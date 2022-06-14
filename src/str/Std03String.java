package str;
import java.util.ArrayList;
import java.util.List;
public class Std03String {
	public static void main(String[] args) throws Exception {
		List<String> byteBuffer = new ArrayList<String>();
		while (true) {
			byteBuffer.add(new String("abc") + new String("def") + new String("123"));
			Thread.sleep(1000);
		}
	}
}