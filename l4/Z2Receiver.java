import java.net.*;
import java.util.Hashtable;

public class Z2Receiver {
    static final int datagramSize=50;
    InetAddress localHost;
    int destinationPort;
    DatagramSocket socket;
    ReceiverThread receiver;
    
    SenderThread sender;
    private Hashtable<Integer, Z2Packet> packets;
    int last = -1;
    private static final int timeout = 5000;
    
    public Z2Receiver(int myPort, int destPort) throws Exception {
        localHost = InetAddress.getByName("127.0.0.1");
        destinationPort = destPort;
        socket = new DatagramSocket(myPort);
        receiver = new ReceiverThread();    
    
        sender = new SenderThread();
        packets = new Hashtable<>();
    }

    class ReceiverThread extends Thread {
        public void run() {
            try {
        	    while(true) {
        		    byte[] data = new byte[datagramSize];
        		    DatagramPacket packet = new DatagramPacket(data, datagramSize);
        		    socket.receive(packet);
                    Z2Packet p = new Z2Packet(packet.getData());
    
                    int pNumber = p.getIntAt(0);
                    if(pNumber == last + 1) { 
                        System.out.println("R:" + pNumber + ": "+ (char) p.data[4]);
                        synchronized(this) {
                            last++;
                        }
                        while(packets.containsKey(last + 1)) {
                            synchronized(this) {
                                last++;
                            }
                            p = packets.remove(last);   
                            System.out.println("R:" + last + ": "+ (char) p.data[4]);
                        }
                    } else if (pNumber > last + 1 && !packets.containsKey(pNumber)) 
                        packets.put(pNumber, p);
        
        		}
        	} catch(Exception e) {
                System.out.println("Z2Receiver.ReceiverThread.run: "+e);
        	}
        }
    }


    class SenderThread extends Thread {
        public void run() {
            try {
                while(true) {
                    sleep(timeout);
                    if(last != -1) {
                        Z2Packet p = new Z2Packet(4);
                        p.setIntAt(last, 0);
                        DatagramPacket packet = new DatagramPacket(p.data, p.data.length, localHost, destinationPort);
                        socket.send(packet);
                    }
                }
            } catch (Exception e) {
                System.out.println("Z2Receiver.SenderThread.run: " +e);
            }
        }
    }

    public static void main(String[] args) throws Exception {
    	Z2Receiver receiver = new Z2Receiver( Integer.parseInt(args[0]), Integer.parseInt(args[1]));
    	receiver.receiver.start();
        receiver.sender.start();
    }
}
