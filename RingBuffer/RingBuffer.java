package RingBuffer;

public class RingBuffer {

    private final Object[] data;
    private final int capacity;
    private volatile long writeCount = 0;

    public RingBuffer(int capacity) {
        if (capacity < 1) throw new IllegalArgumentException("Capacity must be at least 1");
        this.capacity = capacity;
        this.data = new Object[capacity];
    }

    public int getCapacity() {
        return capacity;
    }

    public long getWriteCount() {
        return writeCount;
    }

    void write(Object item) {
        int index = (int) (writeCount % capacity);
        data[index] = item;
        writeCount++;
    }

    Object readAt(long sequence) {
        return data[(int) (sequence % capacity)];
    }
}