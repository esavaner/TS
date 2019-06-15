import org.jgrapht.graph.DefaultEdge;

class WeightedEdge extends DefaultEdge {
    private double a;
    private double c;
    private double reliability;
    WeightedEdge( double a, double c, double reliability) {
        this.a = a;
        this.c = c;
        this.reliability = reliability;
    }

    WeightedEdge(WeightedEdge e) {
        this.a = e.a;
        this.c = e.c;
        this.reliability = e.reliability;

    }
    boolean overflowed(int packageSize) {
        return a * packageSize >= c;
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