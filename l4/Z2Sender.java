import java.net.*;
import java.util.Hashtable;

class Z2Sender {
    static final int datagramSize = 50;
    static final int sleepTime = 200;
    static final int maxPacket = 50;
    InetAddress localHost;
    int destinationPort;
    DatagramSocket socket;
    SenderThread sender;
    ReceiverThread receiver;
    Reader reader;

    int confirmed = -1;
    final int bufferSize = 10;
    static final int timeout = 5000;
    Hashtable<Integer, DatagramPacket> packets;

    public Z2Sender(int myPort, int destPort) throws Exception {
        localHost = InetAddress.getByName("127.0.0.1");
        destinationPort = destPort;
        socket = new DatagramSocket(myPort);
        sender = new SenderThread();
        receiver = new ReceiverThread();

        reader = new Reader();
        packets = new Hashtable<>();
    }

    class SenderThread extends Thread {
        public void run() {
            try {
                int toSend = 0;
                while(true) {
                    synchronized(this) {
                        toSend = confirmed + 1;
                    }
                    for(int i = 0; i < bufferSize && i < packets.size(); i++) {
                        DatagramPacket packet = packets.get(toSend + i);
                        Z2Packet p = new Z2Packet(packet.getData());
                        System.out.println("Wysylanie: " + p.getIntAt(0) + ": " + (char)p.data[4]);
    	    	        socket.send(packet);
                    }
    	    	    sleep(timeout);
    	    	}
    	    } catch(Exception e) {
                System.out.println("Z2Sender.SenderThread.run: "+e);
    	    }
        }
    }   

    class Reader extends Thread {
        public void run() {
            int x;
            try {
    	        for(int i=0; (x = System.in.read()) >= 0 ; i++) {
                    Z2Packet p = new Z2Packet(4+1);
                    p.setIntAt(i, 0);
                    p.data[4]= (byte) x;
	    	        DatagramPacket packet = new DatagramPacket(p.data, p.data.length, localHost, destinationPort);
                    packets.put(i, packet);
                }
            } catch(Exception e) {
                System.out.println("Z2Sender.Reader.run: " +e);
            }
        }
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
		            System.out.println("S:" + pNumber + " potwierdzono");
                    if(pNumber > confirmed) {
                        deleteConfirmed(confirmed, pNumber);
                        confirmed = pNumber;
                    }
		        }
	        } catch(Exception e) {
                System.out.println("Z2Sender.ReceiverThread.run: "+e);
	        }
        }

        synchronized private void deleteConfirmed(int prevConf, int newConf) {
            for(int i = prevConf + 1; i <= newConf; i++) {
                packets.remove(i);
            }
        }
    }


    public static void main(String[] args) throws Exception {
    	Z2Sender sender = new Z2Sender(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        sender.reader.start();
    	sender.sender.start();
        sender.receiver.start();
    }
}
