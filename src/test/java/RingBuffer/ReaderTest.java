package RingBuffer;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/*
 * TDD approach:
 *   RED   - wrote these tests first to define Reader behavior
 *   GREEN - existing implementation passes all of them
 *   REFACTOR - no changes needed
 */
public class ReaderTest {

    // --- constructor / initial position ---

    @Test
    void startAtBeginningInitializesReadPosToZero() {
        RingBuffer buffer = new RingBuffer(5);
        Reader reader = new Reader(buffer, true);

        assertEquals(0, reader.getReadPos());
    }

    @Test
    void startAtCurrentInitializesReadPosToWriteCount() {
        RingBuffer buffer = new RingBuffer(5);
        buffer.write("A");
        buffer.write("B");

        Reader reader = new Reader(buffer, false);

        assertEquals(2, reader.getReadPos());
    }

    @Test
    void startAtCurrentReaderSeesOnlyFutureWrites() {
        RingBuffer buffer = new RingBuffer(5);
        buffer.write("old");
        Reader reader = new Reader(buffer, false);

        assertFalse(reader.hasNext());
    }

    // --- hasNext ---

    @Test
    void hasNextReturnsFalseOnEmptyBuffer() {
        RingBuffer buffer = new RingBuffer(5);
        Reader reader = new Reader(buffer, true);

        assertFalse(reader.hasNext());
    }

    @Test
    void hasNextReturnsTrueAfterWrite() {
        RingBuffer buffer = new RingBuffer(5);
        buffer.write("A");
        Reader reader = new Reader(buffer, true);

        assertTrue(reader.hasNext());
    }

    @Test
    void hasNextReturnsFalseAfterAllItemsRead() {
        RingBuffer buffer = new RingBuffer(5);
        buffer.write("only");
        Reader reader = new Reader(buffer, true);

        reader.read();

        assertFalse(reader.hasNext());
    }

    // --- read ---

    @Test
    void readReturnsNullWhenNothingAvailable() {
        RingBuffer buffer = new RingBuffer(5);
        Reader reader = new Reader(buffer, true);

        assertNull(reader.read());
    }

    @Test
    void readReturnsFirstWrittenItem() {
        RingBuffer buffer = new RingBuffer(5);
        buffer.write("hello");
        Reader reader = new Reader(buffer, true);

        assertEquals("hello", reader.read());
    }

    @Test
    void readAdvancesReadPosition() {
        RingBuffer buffer = new RingBuffer(5);
        buffer.write("A");
        Reader reader = new Reader(buffer, true);

        reader.read();

        assertEquals(1, reader.getReadPos());
    }

    @Test
    void readReturnsItemsInWriteOrder() {
        RingBuffer buffer = new RingBuffer(5);
        buffer.write("A");
        buffer.write("B");
        buffer.write("C");
        Reader reader = new Reader(buffer, true);

        assertEquals("A", reader.read());
        assertEquals("B", reader.read());
        assertEquals("C", reader.read());
    }

    // --- readAvailable ---

    @Test
    void readAvailableReturnsEmptyListWhenNothingToRead() {
        RingBuffer buffer = new RingBuffer(5);
        Reader reader = new Reader(buffer, true);

        List<Object> result = reader.readAvailable();

        assertTrue(result.isEmpty());
    }

    @Test
    void readAvailableReturnsAllWrittenItems() {
        RingBuffer buffer = new RingBuffer(5);
        buffer.write("X");
        buffer.write("Y");
        Reader reader = new Reader(buffer, true);

        List<Object> result = reader.readAvailable();

        assertEquals(2, result.size());
        assertEquals("X", result.get(0));
        assertEquals("Y", result.get(1));
    }

    @Test
    void readAvailableLeavesNothingLeftToRead() {
        RingBuffer buffer = new RingBuffer(5);
        buffer.write("A");
        buffer.write("B");
        Reader reader = new Reader(buffer, true);

        reader.readAvailable();

        assertFalse(reader.hasNext());
    }

    // --- two independent readers ---

    @Test
    void twoReadersStartAtSamePositionIndependently() {
        RingBuffer buffer = new RingBuffer(5);
        buffer.write("A");
        buffer.write("B");
        Reader reader1 = new Reader(buffer, true);
        Reader reader2 = new Reader(buffer, true);

        assertEquals("A", reader1.read());
        // reader2 should still be at the start
        assertEquals("A", reader2.read());
    }

    @Test
    void advancingOneReaderDoesNotAffectOther() {
        RingBuffer buffer = new RingBuffer(5);
        buffer.write("A");
        buffer.write("B");
        Reader reader1 = new Reader(buffer, true);
        Reader reader2 = new Reader(buffer, true);

        reader1.readAvailable(); // drain reader1

        assertTrue(reader2.hasNext()); // reader2 still has items
    }

    // --- recovery (reader lapped by writer) ---

    @Test
    void readReturnsNonNullAfterBeingLapped() {
        // capacity 3, reader starts at 0 but never reads
        // after 4 writes the reader is lapped (writeCount - readPos = 4 > capacity 3)
        RingBuffer buffer = new RingBuffer(3);
        Reader reader = new Reader(buffer, true);

        buffer.write("A"); // seq 0
        buffer.write("B"); // seq 1
        buffer.write("C"); // seq 2
        buffer.write("D"); // seq 3 – overwrites "A", reader is lapped

        // read() should trigger recover() and return oldest still-available item
        assertNotNull(reader.read());
    }

    @Test
    void readAfterRecoveryStartsFromOldestAvailable() {
        // capacity 3, write 4 items -> oldest available is seq 1 ("B")
        RingBuffer buffer = new RingBuffer(3);
        Reader reader = new Reader(buffer, true);

        buffer.write("A"); // seq 0 -> slot 0
        buffer.write("B"); // seq 1 -> slot 1
        buffer.write("C"); // seq 2 -> slot 2
        buffer.write("D"); // seq 3 -> slot 0, overwrites "A"
        // oldest available: writeCount(4) - capacity(3) = seq 1 -> "B"

        assertEquals("B", reader.read());
    }
}
