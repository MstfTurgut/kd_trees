import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import java.util.LinkedList;

public class KdTree {

    private Node root;
    private int size;

    private static class Node {

        private final Point2D point;
        private Node left, right;

        public Node(Point2D point) {
            this.point = point;
        }
    }

    public KdTree() {
        this.size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        root = insert(root, p, 0);
    }

    private Node insert(Node r, Point2D p, int level) {
        if (r == null) {
            size++;
            return new Node(p);
        }

        int cmp;

        if (level % 2 == 0) cmp = Double.compare(p.x(), r.point.x());
        else               cmp = Double.compare(p.y(), r.point.y());

        if      (cmp < 0) r.left  = insert(r.left,  p, level + 1);
        else if (cmp > 0)          r.right = insert(r.right, p, level + 1);
        else {
            if (level % 2 == 0) {
                if(p.y() == r.point.y()) return r;
                else  r.right = insert(r.right, p, level + 1);
            } else {
                if(p.x() == r.point.x()) return r;
                else  r.right = insert(r.right, p, level + 1);
            }
        }
        return r;
    }

    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        return contains(root, p, 0);
    }

    private boolean contains(Node r, Point2D p, int level) {
        if (r == null) return false;

        int cmp;
        if (level % 2 == 0) cmp = Double.compare(p.x(), r.point.x());
        else               cmp = Double.compare(p.y(), r.point.y());

        if      (cmp < 0) return contains(r.left, p, level + 1);
        else if (cmp > 0) return contains(r.right, p, level + 1);
        else {
            // if the x values are equal we need to check the y values and vice versa
            // if both are equal then it means we found it
            // in the insert method if the checked x or y values are equal we have moved right
            // hence we need to keep looking to the right if the one value is equal and other is not
            if (level % 2 == 0) {
                if(p.y() == r.point.y()) return true;
                else  return contains(r.right, p, level + 1);
            } else {
                if(p.x() == r.point.x()) return true;
                else  return contains(r.right, p, level + 1);
            }
        }
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException();


        LinkedList<Point2D> list = new LinkedList<>();
        range(root, rect, 0, list);
        return list;

    }

    private void range(Node r, RectHV rect, int level, LinkedList<Point2D> list) {

        if (r == null) {
            return;
        }

        // check if point is in the rectangle if it is, add to the list
        // rect.contains(p)

        if (rect.contains(r.point)) {
            list.add(new Point2D(r.point.x(), r.point.y()));
        }

        // recursive
        // check if splitting line intersects the rectangle if it is, check both sides
        // if not check which side is rectangle in , go that side.

        if (level % 2 == 0) {
            if (rect.xmin() < r.point.x() && rect.xmax() > r.point.x()) {
                range(r.left, rect, level + 1, list);
                range(r.right, rect, level + 1, list);
            } else if (rect.xmax() <= r.point.x()) {
                range(r.left, rect, level + 1, list);
            } else {
                range(r.right, rect, level + 1, list);
            }
        } else {
            if (rect.ymin() < r.point.y() && rect.ymax() > r.point.y()) {
                range(r.left, rect, level + 1, list);
                range(r.right, rect, level + 1, list);
            } else if (rect.ymax() <= r.point.y()) {
                range(r.left, rect, level + 1, list);
            } else {
                range(r.right, rect, level + 1, list);
            }
        }

    }

    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        if (isEmpty()) return null;

        Point2D champion = new Point2D(Double.MAX_VALUE, Double.MAX_VALUE);
        return nearest(root, p, champion, 0, 0.0, 0.0, 1.0, 1.0);
    }

    private Point2D nearest(Node r, Point2D p, Point2D champion, int level, double minX, double minY, double maxX, double maxY) {
        if(r == null) return champion;

        double dist = p.distanceSquaredTo(r.point);
        double minDistance = p.distanceSquaredTo(champion);

        if (dist < minDistance) {
            champion = new Point2D(r.point.x(), r.point.y());
        }


        if (level % 2 == 0) {
            // vertical line check x values
            if (p.x() < r.point.x()) {
                champion = nearest(r.left, p, champion, level + 1, minX, minY, r.point.x(), maxY);

                // if(not pruned) check right
                if (minDistance > new RectHV(r.point.x(), minY, r.point.x(), maxY).distanceSquaredTo(p)) {
                    champion = nearest(r.right, p, champion, level + 1, r.point.x(), minY, maxX, maxY);
                }

            } else {
                champion = nearest(r.right, p, champion, level + 1, r.point.x(), minY, maxX, maxY);

                // if(not pruned) check left
                if (minDistance > new RectHV(r.point.x(), minY, r.point.x(), maxY).distanceSquaredTo(p)) {
                    champion = nearest(r.left, p, champion, level + 1, minX, minY, r.point.x(), maxY);
                }
            }

        } else {

            // horizontal line check y values
            if (p.y() < r.point.y()) {
                champion = nearest(r.left, p, champion, level + 1, minX, minY, maxX, r.point.y());

                // if(not pruned) check right
                if (minDistance > new RectHV(minX, r.point.y(),maxX, r.point.y()).distanceSquaredTo(p)) {
                    champion = nearest(r.right, p, champion, level + 1, minX, r.point.y(), maxX, maxY);
                }

            } else {
                champion = nearest(r.right, p, champion, level + 1, minX, r.point.y(), maxX, maxY);

                // if(not pruned) check left
                if (minDistance > new RectHV(minX, r.point.y(),maxX, r.point.y()).distanceSquaredTo(p)) {
                    champion = nearest(r.left, p, champion, level + 1, minX, minY, maxX, r.point.y());
                }

            }
        }

        return champion;

        // search towards to query point first
        // search the other side if it is not pruned
        // update the champion
    }

    public void draw() {
        draw(root);
    }

    private void draw(Node r) {
        if (r != null) {
            draw(r.left);
            r.point.draw();
            draw(r.right);
        }
    }


    public static void main(String[] args) {

        KdTree kdTree = new KdTree();

        kdTree.insert(new Point2D(0.6, 0.0));
        kdTree.insert(new Point2D(0.0, 0.0));
        kdTree.insert(new Point2D(0.0, 0.0));


        System.out.println("Does pointSet contains (0.4, 0.6) : " + kdTree.contains(new Point2D(0.4 , 0.6)));
        System.out.println("Size of pointSet : " + kdTree.size());
        Point2D nearest = kdTree.nearest(new Point2D(0.1 , 0.2));
        System.out.println("Nearest point to the point (0.1, 0.2) is : (" + nearest.x() + ", " + nearest.y() + ")");

        RectHV rectHV = new RectHV(0.5 , 0.0  , 1.0 , 0.5);
        LinkedList<Point2D> list = (LinkedList<Point2D>) kdTree.range(rectHV);
        System.out.println("Points in the rectangle on right bottom quarter are : ");

        for(Point2D p : list) {
            System.out.println("(" + p.x() + ", " + p.y() + ")");
        }

        kdTree.draw();
    }

}
