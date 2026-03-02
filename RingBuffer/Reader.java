package RingBuffer;

import java.util.ArrayList;
import java.util.List;


public class Reader {
    private final RingBuffer buffer;
    private long readPos;

    public Reader(RingBuffer buffer, boolean startAtBeginning) {
        this.buffer  = buffer;
        this.readPos = startAtBeginning ? 0 : buffer.getWriteCount();
    }

    public long getReadPos() {
        return readPos;
    }

    public boolean hasNext() {
        return readPos < buffer.getWriteCount();
    }

    public Object read() {
        if (!hasNext()) {
            return null;
        }
        if (buffer.getWriteCount() - readPos > buffer.getCapacity()) {
            recover();
        }
        Object item = buffer.readAt(readPos);
        readPos++;
        return item;
    }
    public List<Object> readAvailable() {
        List<Object> results = new ArrayList<>();
        while (hasNext()) {
            results.add(read());
        }
        return results;
    }
    private void recover() {
        long oldest = buffer.getWriteCount() - buffer.getCapacity();
        readPos = Math.max(oldest, 0);
    }
}