import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.SimpleGraph;

import java.util.ArrayList;
import java.util.Random;

public class l2z1 {
    private Random random = new Random(System.currentTimeMillis());
    public static void main(String[] args) {
        l2z1 l = new l2z1();
        l.start();
    }

    private void start() {
        SimpleGraph<Integer, WeightedEdge> graph = new SimpleGraph<>(WeightedEdge.class);

        //test 1 podpunktu
        for (int i = 1; i <= 20; i++) {
            graph.addVertex(i);
        }
        for (int j = 1; j <= 19; j++) {
            WeightedEdge we = new WeightedEdge(1, 1, 0.95);
            graph.addEdge(j, j+1, we);
        }
        System.out.println("Niezawodnosc 1 grafu: "+ monteCarlo(graph));


        //test 2 podpunktu
        WeightedEdge we = new WeightedEdge(1, 1, 0.95);
        graph.addEdge(20,1,we);
        System.out.println("Niezawodnosc 2 grafu: "+ monteCarlo(graph));


        //test 3 podpunktu
        graph.addEdge(1,10, new WeightedEdge(1,1,0.8));
        graph.addEdge(5,15,new WeightedEdge(1,1,0.7));
        System.out.println("Niezawodnosc 3 grafu: " + monteCarlo(graph));


        //test 4 podpunktu
        for (int i = 0; i < 4; i++) {
            int a = random.nextInt(19) + 1;
            int b = random.nextInt(19) + 1;
            if (!graph.containsEdge(a, b) && a != b)
                graph.addEdge(a,b,new WeightedEdge(1,1,0.4));
            else
                i--;
        }
        System.out.println("Niezawodnosc 4 grafu: " + monteCarlo(graph));
    }

    private double monteCarlo(SimpleGraph<Integer, WeightedEdge> graph) {
        SimpleGraph<Integer, WeightedEdge> clone = (SimpleGraph<Integer, WeightedEdge>)graph.clone();
        ArrayList<WeightedEdge> toRemove = new ArrayList<>();
        int tests = 10000;
        int passed = 0;
        for(int i = 0; i < tests; i++) {
            toRemove.clear();
            for(WeightedEdge e : clone.edgeSet())
                if(e.getReliability() < random.nextFloat())
                    toRemove.add(e);

            for(WeightedEdge e : toRemove)
                clone.removeEdge(e);

            ConnectivityInspector ci = new ConnectivityInspector<>(clone);

            if(ci.isConnected())
                passed++;
            clone = (SimpleGraph<Integer, WeightedEdge>)graph.clone();

        }
        return (double)passed/tests;
    }
}