package com.hexchex.gui;

import com.hexchex.engine.board.Cell;

import java.awt.*;

public class Hexagon extends Polygon {

    private static final int SIDES = 6;

    private Point[] points = new Point[SIDES];
    private Point center;
    private int radius;
    private int rotation = 0;

    private Color color;
    private Color defaultColor;
    private Color selectedColor = new Color(200, 200, 100);

    private Cell cell;

    Hexagon(Point center, int radius, Cell cell) {
        npoints = SIDES;
        xpoints = new int[SIDES];
        ypoints = new int[SIDES];

        this.center = center;
        this.radius = radius;
        this.cell = cell;

        updatePoints();
    }

    public Hexagon(int x, int y, int radius, Cell cell) {
        this(new Point(x, y), radius, cell);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    Color getColor() {
        return color;
    }

    void setDefaultColor(Color color) {
        this.defaultColor = color;
    }

    Color getDefaultColor() {
        return defaultColor;
    }

    void setToDefaultColor() {
        color = defaultColor;
    }

    void setToSelectedColor() {
        color = selectedColor;
    }

    void setCell(Cell cell) {
        this.cell = cell;
    }

    int getRadius() {
        return radius;
    }

    void setRadius(int radius) {
        this.radius = radius;

        updatePoints();
    }

    int getRotation() {
        return rotation;
    }

    void setRotation(int rotation) {
        this.rotation = rotation;

        updatePoints();
    }

    Point getCenter() {
        return center;
    }

    private void setCenter(Point center) {
        this.center = center;

        updatePoints();
    }

    private void setCenter(int x, int y) {
        setCenter(new Point(x, y));
    }

    Cell getCell() {
        return cell;
    }

    /**
     * Calculates an interior angle, in radians, of the Hexagon based on a fraction (between 0 and 1) representing
     * the angle being calculated
     * @param fraction a double between 0 and 1 which denotes the angle to be calculated--will be one of six values
     *                 for the six angles of a hexagon (see updatePoints() method)
     * @return an interior angle of this Hexagon, in radians
     */
    private double findAngle(double fraction) {
        return fraction * Math.PI * 2 + Math.toRadians((rotation + 180) % 360);
    }

    /**
     * Finds a vertex of the Hexagon based on an interior angle
     * @param angle the interior angle being used to find the vertex
     * @return a Point with the coordinates of the vertex
     */
    private Point findPoint(double angle) {
        int x = (int) (center.x + Math.cos(angle) * radius);
        int y = (int) (center.y + Math.sin(angle) * radius);

        return new Point(x, y);
    }

    /**
     * Recalculates the angles and vertices of this Hexagon. Called when the radius or
     * rotation of this Hexagon is changed
     */
    private void updatePoints() {
        for (int p = 0; p < SIDES; p++) {
            double angle = findAngle((double) p / SIDES);
            Point point = findPoint(angle);
            xpoints[p] = point.x;
            ypoints[p] = point.y;
            points[p] = point;
        }
    }

}

