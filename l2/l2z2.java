import org.jgrapht.GraphPath;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
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
        SimpleGraph<Integer, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
        ArrayList<WeightedEdge> edges = new ArrayList<>();
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
            edges.add(new WeightedEdge(graph.addEdge(a, b), 0, capacity[a-1][b-1], v.p));
        }

        int failed = start(graph, edges, N);
        System.out.println("Niezawodnosc:  " + (v.tests - (double)failed)/v.tests);
        System.out.println("Liczba porazek: " + failed);
    }

    private int start(SimpleGraph<Integer, DefaultEdge> graph, ArrayList<WeightedEdge> edges, int[][] N) {
        int failed = 0;
        SimpleGraph<Integer, DefaultEdge> clone;
        ArrayList<WeightedEdge> edgesClone;
        ArrayList<WeightedEdge> remove = new ArrayList<>();
        for(int i=0; i<v.tests; i++) {
            remove.clear();
            clone = (SimpleGraph<Integer, DefaultEdge>)graph.clone();
            edgesClone = cloneList(edges);
            for(WeightedEdge e : edgesClone) {
                if(e.getReliability() < random.nextFloat()) {
                    remove.add(e);
                }
            }
            for(WeightedEdge e : remove) {
                edgesClone.remove(e);
                clone.removeEdge(e.getEdge());
            }
            for(int j=1; j<=v.vertexNumber; j++) {
                for(int k=1; k<=v.vertexNumber; k++) {
                    GraphPath<Integer, DefaultEdge> path = DijkstraShortestPath.findPathBetween(clone, j, k);
                    if(path != null)
                        for(DefaultEdge pathEdge: path.getEdgeList())
                            for(WeightedEdge e: edgesClone)
                                if(e.getEdge().equals(pathEdge)) {
                                    e.addA(N[j-1][k-1]);
                                    break;
                                }
                }
            }

            ConnectivityInspector ci = new ConnectivityInspector(clone);
            if(!ci.isConnected() || overflowed(edgesClone, v.m) || !correctAvgDelay(avgDelay(edgesClone, N)))
                failed++;
        }
        return failed;
    }


    private boolean correctAvgDelay(double T) {
        return T < v.T_max;
    }

    private double avgDelay(ArrayList<WeightedEdge> edges, int[][] N) {
        double SUM_e = 0;
        double G = 0;
        for(WeightedEdge e: edges) {
            SUM_e += (e.getA()/e.getC()/v.m - e.getA());
        }
        for(int i=0; i<v.vertexNumber; i++) {
            for(int j=0; j<v.vertexNumber; j++) {
                G += N[i][j];
            }
        }
        return SUM_e/G;
    }


    private boolean overflowed(ArrayList<WeightedEdge> edges, int packageSize) {
        for(WeightedEdge e: edges) {
            if(e.overflowed(packageSize))
                return true;
        }
        return false;
    }

    public ArrayList<WeightedEdge> cloneList(ArrayList<WeightedEdge> edges) {
        ArrayList<WeightedEdge> clonedList = new ArrayList<>(edges.size());
        for(WeightedEdge e : edges) {
            clonedList.add(new WeightedEdge(e));
        }
        return clonedList;
    }
}
