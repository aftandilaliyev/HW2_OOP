package RingBuffer;

public class Main {

    public static void main(String[] args) {

        RingBuffer buffer = new RingBuffer(3);
        Writer     writer = new Writer(buffer);

        Reader reader1 = new Reader(buffer, true); 
        Reader reader2 = new Reader(buffer, true); 

        writer.write("A");
        writer.write("B");
        System.out.println(reader1.read());
        System.out.println(reader2.read()); 

        writer.write("C");
        writer.write("D");
        System.out.println(reader1.read());

        writer.write("E");
        System.out.println(reader1.readAvailable()); 
        System.out.println(reader2.readAvailable()); 
    }
}

