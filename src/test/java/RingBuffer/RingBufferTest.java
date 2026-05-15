package RingBuffer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/*
 * TDD approach:
 *   RED   - wrote these tests first to define what RingBuffer should do
 *   GREEN - existing implementation already makes them pass
 *   REFACTOR - no changes needed; tests are already clean
 */
public class RingBufferTest {

    // --- constructor ---

    @Test
    void constructorThrowsWhenCapacityIsZero() {
        assertThrows(IllegalArgumentException.class, () -> new RingBuffer(0));
    }

    @Test
    void constructorThrowsWhenCapacityIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new RingBuffer(-1));
    }

    @Test
    void constructorAcceptsCapacityOfOne() {
        RingBuffer buffer = new RingBuffer(1);
        assertEquals(1, buffer.getCapacity());
    }

    // --- getCapacity / getWriteCount ---

    @Test
    void getCapacityReturnsValuePassedToConstructor() {
        RingBuffer buffer = new RingBuffer(5);
        assertEquals(5, buffer.getCapacity());
    }

    @Test
    void writeCountStartsAtZero() {
        RingBuffer buffer = new RingBuffer(3);
        assertEquals(0, buffer.getWriteCount());
    }

    @Test
    void writeCountIncreasesAfterEachWrite() {
        RingBuffer buffer = new RingBuffer(3);
        buffer.write("A");
        buffer.write("B");
        assertEquals(2, buffer.getWriteCount());
    }

    // --- write / readAt ---

    @Test
    void readAtReturnsWhatWasWritten() {
        RingBuffer buffer = new RingBuffer(3);
        buffer.write("hello");
        assertEquals("hello", buffer.readAt(0));
    }

    @Test
    void multipleWritesAreStoredCorrectly() {
        RingBuffer buffer = new RingBuffer(3);
        buffer.write("A");
        buffer.write("B");
        buffer.write("C");
        assertEquals("A", buffer.readAt(0));
        assertEquals("B", buffer.readAt(1));
        assertEquals("C", buffer.readAt(2));
    }

    @Test
    void writeOverwritesOldestItemWhenFull() {
        // capacity 3: writing a 4th item wraps around to index 0
        RingBuffer buffer = new RingBuffer(3);
        buffer.write("A"); // seq 0 -> slot 0
        buffer.write("B"); // seq 1 -> slot 1
        buffer.write("C"); // seq 2 -> slot 2
        buffer.write("D"); // seq 3 -> slot 0, overwrites "A"

        assertEquals("D", buffer.readAt(3));
    }

    @Test
    void capacityOneKeepsOnlyLastWrite() {
        RingBuffer buffer = new RingBuffer(1);
        buffer.write("first");
        buffer.write("second");
        assertEquals("second", buffer.readAt(1));
    }
}
