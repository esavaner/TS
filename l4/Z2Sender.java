import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

class Z2Sender {
    private static final int datagramSize = 50;
    private static final int sleepTime = 500;

    private int timeout = 5000;

    private final HashMap<Integer, Thread> sent = new HashMap<>();

    private InetAddress localHost;
    private int destinationPort;
    private final DatagramSocket socket;
    private SenderThread sender;
    private ReceiverThread receiver;

    private Z2Sender(int myPort, int destPort) throws Exception {
        localHost = InetAddress.getByName("127.0.0.1");
        destinationPort = destPort;
        socket = new DatagramSocket(myPort);
        sender = new SenderThread();
        receiver = new ReceiverThread();
    }

    private Thread checkAndResend(int seq, int data) {
        return new Thread() {
            synchronized public void run() {
                try {
                    wait(timeout);
                } catch (InterruptedException e) {
                    synchronized (sent) {
                        if (sent.isEmpty()) {
                            System.out.println("Sending end of transmission");
                            //end of transmission
                            Z2Packet p = new Z2Packet(4 + 1);
                            p.setIntAt(-1, 0);
                            DatagramPacket packet = new DatagramPacket(p.data, p.data.length, localHost, destinationPort);
                            try {
                                socket.send(packet);
                            } catch (IOException ee) {/**/}
                            sent.put(-1, checkAndResend(-1, 0));
                            sent.get(-1).start();
                        }
                    }
                    return;
                }
                Z2Packet p = new Z2Packet(4 + 1);
                p.setIntAt(seq, 0);
                p.data[4] = (byte) data;
                DatagramPacket packet = new DatagramPacket(p.data, p.data.length, localHost, destinationPort);
                try {
                    synchronized (socket) {
                        socket.send(packet);
                    }
                    synchronized (sent) {
                        sent.remove(seq);
                        sent.put(seq, checkAndResend(seq, data));
                        sent.get(seq).start();
                    }
                } catch (IOException e) {/**/}
            }
        };
    }

    class SenderThread extends Thread {
        @Override
        public void run() {
            int i, x;
            try {
                for (i = 0; (x = System.in.read()) >= 0; i++) {
                    Z2Packet p = new Z2Packet(4 + 1);
                    p.setIntAt(i, 0);
                    p.data[4] = (byte) x;
                    DatagramPacket packet = new DatagramPacket(p.data, p.data.length, localHost, destinationPort);
                    socket.send(packet);
                    synchronized (sent) {
                        sent.put(i, checkAndResend(i, x));
                        sent.get(i).start();
                    }
                    sleep(sleepTime);
                }
            } catch (IOException e) {
                System.out.println("IO");
            } catch (InterruptedException e) {
                System.out.println("Interrupt");
            }
        }
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
                    synchronized (sent) {
                        if (sent.containsKey(p.getIntAt(0))) {
                            sent.get(p.getIntAt(0)).interrupt();
                            sent.remove(p.getIntAt(0));
                            System.out.println("Successfully transmitted char at " + p.getIntAt(0) + ": " + (char) p.data[4]);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("IO");
            }
        }
    }

    public static void main(String[] args) {
        Z2Sender sender;
        try {
            sender = new Z2Sender(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        } catch (Exception e) {
            System.out.println("lol error");
            return;
        }
        sender.sender.start();
        sender.receiver.start();
    }
}