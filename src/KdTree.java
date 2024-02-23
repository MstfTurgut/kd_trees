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

    // Insert a point into the KdTree
    public void insert(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("Point cannot be null");
        }
        root = insert(root, p, 0);
    }

    // Helper method for insertion
    private Node insert(Node node, Point2D point, int level) {
        if (node == null) {
            size++;
            return new Node(point);
        }

        // Alternating comparison based on level
        int cmp = (level % 2 == 0) ? Double.compare(point.x(), node.point.x()) : Double.compare(point.y(), node.point.y());

        if (cmp < 0) {
            node.left = insert(node.left, point, level + 1);
        } else if (cmp > 0) {
            node.right = insert(node.right, point, level + 1);
        } else {
            // Handle case when point is already in the tree
            if ((level % 2 == 0 && point.y() != node.point.y()) || (level % 2 != 0 && point.x() != node.point.x())) {
                node.right = insert(node.right, point, level + 1);
            }
        }
        return node;
    }

    // Check if the KdTree contains a given point
    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("Point cannot be null");
        }
        return contains(root, p, 0);
    }

    // Helper method for containment check
    private boolean contains(Node node, Point2D point, int level) {
        if (node == null) {
            return false;
        }

        int cmp = (level % 2 == 0) ? Double.compare(point.x(), node.point.x()) : Double.compare(point.y(), node.point.y());

        if (cmp < 0) {
            return contains(node.left, point, level + 1);
        } else if (cmp > 0) {
            return contains(node.right, point, level + 1);
        } else {
            // Handle case when x or y values are equal
            if ((level % 2 == 0 && point.y() == node.point.y()) || (level % 2 != 0 && point.x() == node.point.x())) {
                return true;
            } else {
                return contains(node.right, point, level + 1);
            }
        }
    }

    // Iterable of points within a given rectangle
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException("Rectangle cannot be null");
        }

        LinkedList<Point2D> list = new LinkedList<>();
        range(root, rect, 0, list);
        return list;
    }

    // Helper method for range search
    private void range(Node node, RectHV rect, int level, LinkedList<Point2D> list) {
        if (node == null) {
            return;
        }

        if (rect.contains(node.point)) {
            list.add(new Point2D(node.point.x(), node.point.y()));
        }

        if (level % 2 == 0) {
            if (rect.xmin() < node.point.x() && rect.xmax() > node.point.x()) {
                range(node.left, rect, level + 1, list);
                range(node.right, rect, level + 1, list);
            } else if (rect.xmax() <= node.point.x()) {
                range(node.left, rect, level + 1, list);
            } else {
                range(node.right, rect, level + 1, list);
            }
        } else {
            if (rect.ymin() < node.point.y() && rect.ymax() > node.point.y()) {
                range(node.left, rect, level + 1, list);
                range(node.right, rect, level + 1, list);
            } else if (rect.ymax() <= node.point.y()) {
                range(node.left, rect, level + 1, list);
            } else {
                range(node.right, rect, level + 1, list);
            }
        }
    }

    // Find the nearest point to a given query point
    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("Point cannot be null");
        }
        if (isEmpty()) {
            return null;
        }

        Point2D champion = new Point2D(Double.MAX_VALUE, Double.MAX_VALUE);
        return nearest(root, p, champion, 0, 0.0, 0.0, 1.0, 1.0);
    }

    // Helper method for nearest point search
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

    // Draw all points in the KdTree
    public void draw() {
        draw(root);
    }

    // Helper method for drawing points
    private void draw(Node node) {
        if (node != null) {
            draw(node.left);
            node.point.draw();
            draw(node.right);
        }
    }

    public static void main(String[] args) {
        // Example usage in the main method
        KdTree kdTree = new KdTree();

        kdTree.insert(new Point2D(0.6, 0.0));
        kdTree.insert(new Point2D(0.0, 0.0));
        kdTree.insert(new Point2D(0.0, 0.0));

        System.out.println("Does pointSet contain (0.4, 0.6): " + kdTree.contains(new Point2D(0.4, 0.6)));
        System.out.println("Size of pointSet: " + kdTree.size());

        Point2D nearestPoint = kdTree.nearest(new Point2D(0.1, 0.2));
        System.out.println("Nearest point to the point (0.1, 0.2) is: (" + nearestPoint.x() + ", " + nearestPoint.y() + ")");

        RectHV rectHV = new RectHV(0.5, 0.0, 1.0, 0.5);
        LinkedList<Point2D> pointList = (LinkedList<Point2D>) kdTree.range(rectHV);
        System.out.println("Points in the rectangle on the bottom right quarter are:");

        for (Point2D p : pointList) {
            System.out.println("(" + p.x() + ", " + p.y() + ")");
        }

        kdTree.draw();
    }
}
