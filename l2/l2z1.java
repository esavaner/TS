import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
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
        ArrayList<WeightedEdge> edges = new ArrayList<>();
        SimpleGraph<Integer, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);

        //test 1 podpunktu
        for (int i = 1; i <= 20; i++) {
            graph.addVertex(i);
        }
        for (int j = 1; j <= 19; j++) {
            DefaultEdge de = graph.addEdge(j, j + 1);
            edges.add(new WeightedEdge(de, 1, 1, 0.95));
        }
        System.out.println("Niezawodnosc 1 grafu: "+ monteCarlo(edges, graph));


        //test 2 podpunktu
        DefaultEdge de = graph.addEdge(20, 1);
        edges.add(new WeightedEdge(de,1, 1, 0.95));
        System.out.println("Niezawodnosc 2 grafu: "+ monteCarlo(edges, graph));


        //test 3 podpunktu
        edges.add(new WeightedEdge(graph.addEdge(1, 10), 1, 1, 0.8));
        edges.add(new WeightedEdge(graph.addEdge(5, 15), 1, 1, 0.7));
        System.out.println("Niezawodnosc 3 grafu: " + monteCarlo(edges, graph));


        //test 4 podpunktu
        for (int i = 0; i < 4; i++) {
            int a = random.nextInt(19) + 1;
            int b = random.nextInt(19) + 1;
            if (!graph.containsEdge(a, b) && a != b)
                edges.add(new WeightedEdge(graph.addEdge(a, b), 1, 1, 0.4));
            else
                i--;
        }
        System.out.println("Niezawodnosc 4 grafu: " + monteCarlo(edges, graph));
    }

    private double monteCarlo(ArrayList<WeightedEdge> edges, SimpleGraph<Integer, DefaultEdge> graph) {
        SimpleGraph<Integer, DefaultEdge> clone = (SimpleGraph<Integer, DefaultEdge>)graph.clone();
        int tests = 10000;
        int passed = 0;
        for(int i = 0; i < tests; i++) {
            for(WeightedEdge e: edges)
                if(e.getReliability() < random.nextFloat())
                    clone.removeEdge(e.getEdge());
            ConnectivityInspector ci = new ConnectivityInspector<>(clone);
            if(ci.isConnected())
                passed++;
            clone = (SimpleGraph<Integer, DefaultEdge>)graph.clone();
        }
        return (double)passed/tests;
    }
}