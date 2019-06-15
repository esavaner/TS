package zad2;

public class l3z2 {
    public static String[] network;
    public static void main(String[] args) {
        Variables v = new Variables();
        network = new String[v.networkLength];

        for(int i=0; i < v.networkLength; i++) {
            network[i] = "-";
        }
        AccessNode a1 = new AccessNode("A", 4, 3000, v.delay);
        AccessNode a2 = new AccessNode("B", 35, 4000, v.delay);
        AccessNode a3 = new AccessNode("C", 46, 4500, v.delay);

        a1.start();
        a2.start();
        a3.start();

        while(true) {
            for(String s : network) {
                System.out.print(s + " ");
            }
            System.out.print("\n");
            try {
                Thread.sleep(v.delay);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
