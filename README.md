## Project Overview
- A **single writer** that continuously writes data into a fixed-size buffer
- **Multiple independent readers**, each tracking their own position
- **Automatic overwrite** of oldest data when the buffer is full
- **Silent recovery** for slow readers that fall behind — they automatically skip to the oldest available item

A Ring Buffer is a fixed-size data structure that wraps around itself. When the buffer is full, new writes overwrite the oldest data. It is commonly used in data streaming, logging systems, and producer-consumer pipelines.

## Design — Class Responsibilities
### `RingBuffer`
The core data store. Owns the physical array of slots and a global monotonic `writeCount` that **never resets**. The modulo operation (`writeCount % capacity`) maps any sequence number to a physical slot, making the buffer circular. Exposes package-private methods `write()` and `readAt()` so only `Writer` and `Reader` can access them directly.

### `Writer`
A **single-writer facade** that wraps `RingBuffer.write()`. Its existence enforces the single-writer
contract at the class level — instead of relying on comments or conventions, the design itself makes
it clear that only one `Writer` instance should exist per buffer.

### `Reader`
Each `Reader` instance holds its own `readPos` counter — completely independent of every other reader.
On every `read()` call it checks whether the writer has lapped it (i.e. `writeCount - readPos > capacity`).
If lapped, it silently recovers by jumping forward to the oldest still-available item.

## UML Class Diagram

┌──────────────────────────────────────┐
│           RingBuffer                 │
│──────────────────────────────────────│
│ - data       : Object[]              │
│ - capacity   : int                   │
│ - writeCount : long                  │
│──────────────────────────────────────│
│ + getCapacity()  : int               │
│ + getWriteCount(): long              │
│ ~ write(Object)  : void              │  ← package-private
│ ~ readAt(long)   : Object            │  ← package-private
└──────────┬───────────────────────────┘
           │ uses                    │ uses
           │                         │
┌──────────▼───────┐     ┌───────────▼──────────────┐
│     Writer       │     │         Reader            │
│──────────────────│     │──────────────────────────│
│ - buffer         │     │ - buffer   : RingBuffer   │
│──────────────────│     │ - readPos  : long         │
│ + write(Object)  │     │──────────────────────────│
│ + writeAll(      │     │ + hasNext()       : bool  │
│    Object[])     │     │ + read()          : Object│
└──────────────────┘     │ + readAvailable() : List  │
                        │ - recover()       : void  │
                         └──────────────────────────┘
                            (N instances — each
                             with its own readPos)

---

## UML Sequence Diagram — write()

  Main              Writer           RingBuffer
   │                  │                  │
   │  write("A")      │                  │
   │─────────────────►│                  │
   │                  │  write("A")      │
   │                  │─────────────────►│
   │                  │                  │ index = writeCount % capacity
   │                  │                  │ data[index] = "A"
   │                  │                  │ writeCount++
   │                  │◄─────────────────│
   │◄─────────────────│                  │

---

## UML Sequence Diagram — read()

### Case A — Normal read (reader is keeping up)

  Main              Reader           RingBuffer
   │                  │                  │
   │  read()          │                  │
   │─────────────────►│                  │
   │                  │  getWriteCount() │
   │                  │─────────────────►│
   │                  │◄─────────────────│ returns writeCount
   │                  │                  │
   │                  │ lag = writeCount - readPos
   │                  │ lag > capacity? NO
   │                  │                  │
   │                  │  readAt(readPos) │
   │                  │─────────────────►│
   │                  │◄─────────────────│ returns data[readPos % capacity]
   │                  │                  │
   │                  │ readPos++        │
   │◄─────────────────│                  │
   │   returns item   │                  │

### Case B — Slow reader (lapped by writer → auto recovery)

  Main              Reader           RingBuffer
   │                  │                  │
   │  read()          │                  │
   │─────────────────►│                  │
   │                  │  getWriteCount() │
   │                  │─────────────────►│
   │                  │◄─────────────────│ returns writeCount
   │                  │                  │
   │                  │ lag = writeCount - readPos
   │                  │ lag > capacity? YES → recover()
   │                  │ readPos = writeCount - capacity
   │                  │                  │
   │                  │  readAt(readPos) │
   │                  │─────────────────►│
   │                  │◄─────────────────│ returns oldest available item
   │                  │                  │
   │                  │ readPos++        │
   │◄─────────────────│                  │
   │  returns item    │                  │
   │  (B was missed)  │                  │

---

## How to Run / Test the Project

### Project Structure

RingBuffer/
 1 RingBuffer.java
 2  Writer.java
 3 Reader.java
 4 Main.java

### Option 1 — Run from an IDE
1. Create a new Java project
2. Create a package named `RingBuffer`
3. Add all four `.java` files into the package
4. Run `Main.java`

