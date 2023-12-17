import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import java.util.LinkedList;
import java.util.TreeSet;

public class PointSET {

    // BRUTE-FORCE APPROACH

    private final TreeSet<Point2D> pSet;

    public PointSET() {
        pSet = new TreeSet<>();
    }

    public boolean isEmpty() {
        return pSet.isEmpty();
    }

    public int size() {
        return pSet.size();
    }

    public void insert(Point2D p) {
        if(p == null) throw new IllegalArgumentException();
        pSet.add(p);
    }

    public boolean contains(Point2D p) {
        if(p == null) throw new IllegalArgumentException();
        return pSet.contains(p);
    }

    public void draw() {
        for(Point2D p : pSet) {
            p.draw();
        }
    }

    public Iterable<Point2D> range(RectHV rect) {
        if(rect == null) throw new IllegalArgumentException();

        LinkedList<Point2D> list = new LinkedList<>();
        for(Point2D p : pSet) {
            if(rect.contains(p)) list.add(p);
        }
        return list;
    }

    public Point2D nearest(Point2D p) {
        if(p == null) throw new IllegalArgumentException();

        if(isEmpty()) return null;

        double minDist = Double.MAX_VALUE;
        Point2D nearest = new Point2D(0.0 , 0.0);

        for(Point2D point : pSet) {
            double dist = p.distanceSquaredTo(point);
            if(dist < minDist) {
                nearest = point;
                minDist = dist;
            }
        }

        return nearest;
    }

    public static void main(String[] args) {

        PointSET pointSET = new PointSET();

        pointSET.insert(new Point2D(0.1 , 0.3));
        pointSET.insert(new Point2D(0.4 , 0.6));
        pointSET.insert(new Point2D(0.7 , 0.5));
        pointSET.insert(new Point2D(0.3 , 0.8));
        pointSET.insert(new Point2D(0.9 , 0.5));
        pointSET.insert(new Point2D(0.5 , 0.6));
        pointSET.insert(new Point2D(0.6 , 0.1));

        System.out.println("Does pointSet contains (0.4, 0.6) : " + pointSET.contains(new Point2D(0.4 , 0.6)));
        System.out.println("Size of pointSet : " + pointSET.size());
        Point2D nearest = pointSET.nearest(new Point2D(0.1 , 0.2));
        System.out.println("Nearest point to the point (0.1, 0.2) is : (" + nearest.x() + ", " + nearest.y() + ")");

        RectHV rectHV = new RectHV(0.5 , 0.0  , 1.0 , 0.5);
        LinkedList<Point2D> list = (LinkedList<Point2D>) pointSET.range(rectHV);
        System.out.println("Points in the rectangle on right bottom quarter are : ");

        for(Point2D p : list) {
            System.out.println("(" + p.x() + ", " + p.y() + ")");
        }

        pointSET.draw();
    }
}
