import org.jgrapht.graph.DefaultEdge;

class WeightedEdge {
    private DefaultEdge edge;
    private double a;
    private double c;
    private double reliability;
    WeightedEdge(DefaultEdge edge, double a, double c, double reliability) {
        this.edge = edge;
        this.a = a;
        this.c = c;
        this.reliability = reliability;
    }

    WeightedEdge(WeightedEdge e) {
        this.edge = e.edge;
        this.a = e.a;
        this.c = e.c;
        this.reliability = e.reliability;

    }
    boolean overflowed(int packageSize) {
        return a*packageSize > c;
    }

    DefaultEdge getEdge() {
        return edge;
    }

    double getA() {
        return a;
    }

    double getC() {
        return c;
    }

    void addA(int val) {
        a += val;
    }

    double getReliability() {
        return reliability;
    }

}