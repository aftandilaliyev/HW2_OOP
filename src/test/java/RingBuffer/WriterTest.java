package RingBuffer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/*
 * TDD approach:
 *   RED   - wrote these tests first to define Writer behavior
 *   GREEN - existing implementation passes all of them
 *   REFACTOR - no changes needed
 */
public class WriterTest {

    @Test
    void writeSingleItemIncreasesBufferWriteCount() {
        RingBuffer buffer = new RingBuffer(5);
        Writer writer = new Writer(buffer);

        writer.write("item");

        assertEquals(1, buffer.getWriteCount());
    }

    @Test
    void writeSingleItemIsStoredInBuffer() {
        RingBuffer buffer = new RingBuffer(5);
        Writer writer = new Writer(buffer);

        writer.write("hello");

        assertEquals("hello", buffer.readAt(0));
    }

    @Test
    void writeAllStoresEachItemInOrder() {
        RingBuffer buffer = new RingBuffer(5);
        Writer writer = new Writer(buffer);

        writer.writeAll(new Object[]{"A", "B", "C"});

        assertEquals("A", buffer.readAt(0));
        assertEquals("B", buffer.readAt(1));
        assertEquals("C", buffer.readAt(2));
    }

    @Test
    void writeAllIncreasesWriteCountByArrayLength() {
        RingBuffer buffer = new RingBuffer(5);
        Writer writer = new Writer(buffer);

        writer.writeAll(new Object[]{"X", "Y", "Z"});

        assertEquals(3, buffer.getWriteCount());
    }

    @Test
    void writeAllWithEmptyArrayChangesNothing() {
        RingBuffer buffer = new RingBuffer(5);
        Writer writer = new Writer(buffer);

        writer.writeAll(new Object[]{});

        assertEquals(0, buffer.getWriteCount());
    }
}
