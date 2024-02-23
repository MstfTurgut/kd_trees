import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import java.util.LinkedList;
import java.util.TreeSet;

public class PointSET {

    // TreeSet-based implementation for point set

    private final TreeSet<Point2D> pointSet;

    public PointSET() {
        pointSet = new TreeSet<>();
    }

    public boolean isEmpty() {
        return pointSet.isEmpty();
    }

    public int size() {
        return pointSet.size();
    }

    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Point cannot be null");
        pointSet.add(p);
    }

    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Point cannot be null");
        return pointSet.contains(p);
    }

    public void draw() {
        for (Point2D p : pointSet) {
            p.draw();
        }
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException("Rectangle cannot be null");

        LinkedList<Point2D> list = new LinkedList<>();
        for (Point2D p : pointSet) {
            if (rect.contains(p)) list.add(p);
        }
        return list;
    }

    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Point cannot be null");

        if (isEmpty()) return null;

        double minDist = Double.MAX_VALUE;
        Point2D nearest = new Point2D(0.0, 0.0);

        for (Point2D point : pointSet) {
            double dist = p.distanceSquaredTo(point);
            if (dist < minDist) {
                nearest = point;
                minDist = dist;
            }
        }

        return nearest;
    }

    public static void main(String[] args) {
        // Example usage in the main method
        PointSET pointSet = new PointSET();

        pointSet.insert(new Point2D(0.1, 0.3));
        pointSet.insert(new Point2D(0.4, 0.6));
        pointSet.insert(new Point2D(0.7, 0.5));
        pointSet.insert(new Point2D(0.3, 0.8));
        pointSet.insert(new Point2D(0.9, 0.5));
        pointSet.insert(new Point2D(0.5, 0.6));
        pointSet.insert(new Point2D(0.6, 0.1));

        System.out.println("Does pointSet contain (0.4, 0.6): " + pointSet.contains(new Point2D(0.4, 0.6)));
        System.out.println("Size of pointSet: " + pointSet.size());
        Point2D nearest = pointSet.nearest(new Point2D(0.1, 0.2));
        System.out.println("Nearest point to the point (0.1, 0.2) is: (" + nearest.x() + ", " + nearest.y() + ")");

        RectHV rectHV = new RectHV(0.5, 0.0, 1.0, 0.5);
        LinkedList<Point2D> list = (LinkedList<Point2D>) pointSet.range(rectHV);
        System.out.println("Points in the rectangle on right bottom quarter are:");

        for (Point2D p : list) {
            System.out.println("(" + p.x() + ", " + p.y() + ")");
        }

        pointSet.draw();
    }
}
