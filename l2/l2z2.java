import org.jgrapht.GraphPath;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.SimpleGraph;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class l2z2 {
    private static Random random = new Random(System.currentTimeMillis());
    private Variables v = new Variables();
    public static void main(String[] args) {
        l2z2 l = new l2z2();
        try {
            l.createGraph();
        } catch (IOException e) {}
    }
    private void createGraph() throws IOException {
        SimpleGraph<Integer, WeightedEdge> graph = new SimpleGraph<Integer, WeightedEdge>(WeightedEdge.class);
        int[][] N = new int[v.vertexNumber][v.vertexNumber];                //macierz natezen
        int[][] capacity = new int[v.vertexNumber][v.vertexNumber];   //macierz pojemnosci

        File file = new File("C:\\Users\\Filip\\Documents\\IdeaProjects\\ts_zad2\\src\\graph.txt");
        BufferedReader br = new BufferedReader(new FileReader(file.getAbsolutePath()));
        String line;

        //natezenie
        for(int i=0; i < v.vertexNumber; i++) {
            if((line = br.readLine()) != null) {
                String [] split = line.split(" ,");
                for(int j=0; j < v.vertexNumber; j++) {
                    N[i][j] = Integer.parseInt(split[j]);
                }
            }
        }

        //pojemnosc maksymalna
        for(int k=0; k < v.vertexNumber; k++) {
            if((line = br.readLine()) != null) {
                String[] split = line.split(", ");
                for(int l=0; l < v.vertexNumber; l++) {
                    capacity[k][l] = Integer.parseInt(split[l]);
                }
            }
        }

        for(int i=1; i<=v.vertexNumber; i++) {
            graph.addVertex(i);
        }

        //krawedzie
        while((line = br.readLine()) != null) {
            String[] edgeS = line.split("-");
            int a = Integer.parseInt(edgeS[0]);
            int b = Integer.parseInt(edgeS[1]);
            WeightedEdge edge = new WeightedEdge(0, capacity[a-1][b-1], v.p);
            graph.addEdge(a, b, edge);
        }

        int failed = start(graph, N);
        System.out.println("Niezawodnosc:  " + (v.tests - (double)failed)/v.tests);
        System.out.println("Liczba porazek: " + failed);
    }

    private int start(SimpleGraph<Integer, WeightedEdge> graph, int[][] N) {
        int failed = 0;
        SimpleGraph<Integer, WeightedEdge> clone;
        for(int i=0; i<v.tests; i++) {
            clone = (SimpleGraph<Integer, WeightedEdge>)graph.clone();
            removeByProbability(clone);

            for(int j=1; j<=v.vertexNumber; j++) {
                for(int k=1; k<=v.vertexNumber; k++) {
                    GraphPath<Integer, WeightedEdge> path = DijkstraShortestPath.findPathBetween(clone, j, k);
                    if(path != null)
                        for(WeightedEdge pathEdge: path.getEdgeList()) {
                            pathEdge.addA(N[j-1][k-1]);
                        }
                }
            }

            ConnectivityInspector ci = new ConnectivityInspector(clone);
            if(ci.isConnected()) {
                if (!overflowed(clone)) {
                    if (!correctAvgDelay(avgDelay(clone, N)))
                        failed++;
                } else
                    failed++;
            } else
                failed++;
        }
        return failed;
    }

    private void removeByProbability(SimpleGraph<Integer, WeightedEdge> graph) {
        ArrayList<WeightedEdge> toRemove = new ArrayList<>();
        for(WeightedEdge e : graph.edgeSet()) {
            if(e.getReliability() < random.nextDouble()) {
                toRemove.add(e);
            }
        }
        for(WeightedEdge e : toRemove) {
            graph.removeEdge(e);
        }
    }

    private boolean correctAvgDelay(double T) {
        return T < v.T_max;
    }

    private double avgDelay(SimpleGraph<Integer, WeightedEdge> graph, int[][] N) {
        double SUM_e = 0;
        double G = 0;
        for(WeightedEdge e: graph.edgeSet()) {
            SUM_e += (e.getA()/e.getC()/v.m - e.getA());
        }
        for(int i=0; i<v.vertexNumber; i++) {
            for(int j=0; j<v.vertexNumber; j++) {
                G += N[i][j];
            }
        }
        return SUM_e/G;
    }


    private boolean overflowed(SimpleGraph<Integer, WeightedEdge> graph) {
        for(WeightedEdge e: graph.edgeSet()) {
            if(e.overflowed(v.m))
                return true;
        }
        return false;
    }

    private ArrayList<WeightedEdge> cloneList(ArrayList<WeightedEdge> edges) {
        ArrayList<WeightedEdge> clonedList = new ArrayList<>(edges.size());
        for(WeightedEdge e : edges) {
            clonedList.add(new WeightedEdge(e));
        }
        return clonedList;
    }
}
