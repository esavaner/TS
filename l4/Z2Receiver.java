import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

public class Z2Receiver {
    private static final int datagramSize = 50;
    private int destinationPort;

    private TreeMap<Integer, Character> received = new TreeMap<>();

    private InetAddress localHost;
    private DatagramSocket socket;
    private ReceiverThread receiver;

    public Z2Receiver(int myPort, int destPort) throws Exception {
        localHost = InetAddress.getByName("127.0.0.1");
        destinationPort = destPort;
        socket = new DatagramSocket(myPort);
        receiver = new ReceiverThread();
    }

    class ReceiverThread extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    byte[] data = new byte[datagramSize];
                    DatagramPacket packet = new DatagramPacket(data, datagramSize);
                    socket.receive(packet);
                    Z2Packet p = new Z2Packet(packet.getData());
                    if (p.getIntAt(0) == -1) {
                        System.out.println("Final message:");
                        for (Map.Entry<Integer, Character> e : received.entrySet()) {
                            System.out.print(e.getValue());
                        }
                        System.out.println();
                        break;
                    }

                    if (!received.containsKey(p.getIntAt(0))) {
                        received.put(p.getIntAt(0), (char) p.data[4]);
                        checkIntegrityAndPrint(p.getIntAt(0));
                    }
                    packet.setPort(destinationPort);
                    socket.send(packet);
                }
            } catch (Exception e) {
                System.out.println("Z2Receiver.ReceiverThread.run: " + e);
            }
        }
    }

    private void checkIntegrityAndPrint(int index) {
        for (int i = 0; i < index; i++) {
            if (!received.containsKey(i)) return;
        }
        System.out.println("CURRENT MESSAGE");
        for (int i = 0; i <= index; i++) {
            System.out.print(received.get(i).charValue());
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Z2Receiver receiver;
        try {
            receiver = new Z2Receiver(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        } catch (Exception e) {
            System.out.println("error lol");
            return;
        }
        receiver.receiver.start();
    }
}