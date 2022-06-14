package map;
public class ListAdd {
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            int old = elementDataLength;
            ensureCapacityInternal(elementDataLength + 1);
            System.out.println(old + "->" + elementDataLength);
        }
    }
    static int EMPTY_ELEMENTDATA = 0;
    static int DEFAULT_CAPACITY = 10;
    static int elementDataLength = 1;
    private static void ensureCapacityInternal(int minCapacity) {
        if (elementDataLength == EMPTY_ELEMENTDATA) {
            minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity);
        }
        ensureExplicitCapacity(minCapacity);
    }
    private static void ensureExplicitCapacity(int minCapacity) {
        if (minCapacity - elementDataLength > 0)
            grow(minCapacity);
    }
    private static void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = elementDataLength;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
//        if (newCapacity - MAX_ARRAY_SIZE > 0)
//            newCapacity = hugeCapacity(minCapacity);
        // minCapacity is usually close to size, so this is a win:
//        elementData = Arrays.copyOf(elementData, newCapacity);
        elementDataLength=newCapacity;
    }
}