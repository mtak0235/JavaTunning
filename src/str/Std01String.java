package str;
import java.util.ArrayList;
import java.util.List;
public class Std01String {
    public static void main(String[] args) throws Exception {
        List<String> list = new ArrayList<String>();
        while (true) {
            list.add("abc" + "def" + "123");
            Thread.sleep(1000);
        }
    }
}
