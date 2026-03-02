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

    /**
     * Read the next item and advance the reader's position.
     * If the reader was lapped by the writer, it automatically
     * recovers to the oldest available item (silent skip).
     * Returns null if no new data is available.
     */
    public Object read() {
        if (!hasNext()) {
            return null;
        }

        // If lapped by the writer, silently jump to oldest valid slot
        if (buffer.getWriteCount() - readPos > buffer.getCapacity()) {
            recover();
        }

        Object item = buffer.readAt(readPos);
        readPos++;
        return item;
    }

    /**
     * Drain all currently available items into a list.
     */
    public List<Object> readAvailable() {
        List<Object> results = new ArrayList<>();
        while (hasNext()) {
            results.add(read());
        }
        return results;
    }

    /**
     * Fast-forward to the oldest sequence number still in the buffer.
     */
    private void recover() {
        long oldest = buffer.getWriteCount() - buffer.getCapacity();
        readPos = Math.max(oldest, 0);
    }
}