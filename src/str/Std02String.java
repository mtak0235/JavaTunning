package str;

import java.util.ArrayList;
import java.util.List;
public class Std02String {
	public static void main(String[] args) throws Exception {
		List<String> byteBuffer = new ArrayList<String>();
		while (true) {
			String a="abc";
			String b="def";
			String c="123";
			byteBuffer.add(a+b+c);			
			Thread.sleep(1000);
		}
	}
}