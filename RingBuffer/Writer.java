package RingBuffer;

/**
 * Single-writer facade.
 * Enforces the single-writer contract at the class level.
 * Only one instance of this class should exist per RingBuffer.
 */
public class Writer {

    private final RingBuffer buffer;

    public Writer(RingBuffer buffer) {
        this.buffer = buffer;
    }

    public void write(Object item) {
        buffer.write(item);
    }

    public void writeAll(Object[] items) {
        for (Object item : items) {
            buffer.write(item);
        }
    }
}