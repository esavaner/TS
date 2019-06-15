package zad2;

public class AccessNode extends Thread {
    private String name;
    private int position;
    private int delay;
    private int tspeed;
    private boolean transmitting = false;
    private boolean sendingName = false;
    private boolean sendingErr = false;
    private boolean sendingNull = false;
    private Variables v = new Variables();
    AccessNode(String name, int position, int delay, int tspeed) {
        this.name = name;
        this.position = position;
        this.delay = delay;
        this.tspeed = tspeed;
    }

    public void run(){
        boolean collisionEarlier = false;
        boolean collisionNow = false;
        int k = 0;
        int place = 0;
        int place2 = 0;
        while(true) {
            if(!transmitting) {
                try {
                    Thread.sleep(delay);
                    if(l3z2.network[position].equals("-")) {
                        k = 0;
                        place = 0;
                        place2 = 0;
                        transmitting = true;
                        sendingName = true;
                        sendingErr = false;
                        sendingNull = false;
                        collisionNow = false;
                    }
                    else
                        continue;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if(!l3z2.network[position].equals("-") && !l3z2.network[position].equals(name) && !l3z2.network[position].equals("=")) {
                sendingErr = true;
                collisionNow = true;
            }

            if(position-place <= 0 && position+place >= v.networkLength) {
                sendingName = false;
                if(!sendingErr)
                    sendingNull = true;
            }

            if(position-place2 <= 0 && position+place2 >= v.networkLength) {
                if(sendingErr) {
                    sendingErr = false;
                    sendingNull = true;
                    place2 = 0;
                } else {
                    if(!collisionNow && !collisionEarlier)
                        k=0;
                    else if (collisionNow && collisionEarlier) {
                        k++;
                        waitRandom(k);
                    } else if (collisionNow)
                        collisionEarlier = true;
                    else
                        collisionEarlier = false;

                    transmitting = false;

                }
            }

            if(sendingName) {
                sendSignal(place, name);
                place++;
            }

            if(sendingErr) {
                sendSignal(place2, "=");
                place2++;
            }

            if(sendingNull) {
                sendSignal(place2, "-");
                place2++;
            }

            try {
                Thread.sleep(tspeed);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendSignal(int place, String msg) {
        if (position - place >= 0)
            l3z2.network[position - place] = msg;

        if (position + place < l3z2.network.length)
            l3z2.network[position + place] = msg;
    }

    private void waitRandom(int k) {
        double r = Math.random() * Math.pow(2,k)+1;
        try {
            Thread.sleep((int)r*v.networkLength*v.delay + 1);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}