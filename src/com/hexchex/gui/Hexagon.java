package com.hexchex.gui;

import com.hexchex.engine.board.Cell;

import java.awt.*;

public class Hexagon extends Polygon {

    public static final int SIDES = 6;

    private Point[] points = new Point[SIDES];
    private Point center;
    private int radius;
    private int rotation = 0;
    private Color color;
    private Cell cell;

    public Hexagon(Point center, int radius, Cell cell) {
        npoints = SIDES;
        xpoints = new int[SIDES];
        ypoints = new int[SIDES];

        this.center = center;
        this.radius = radius;
        this.cell = cell;
        this.color = Color.WHITE;

        updatePoints();
    }

    public Hexagon(int x, int y, int radius, Cell cell) {
        this(new Point(x, y), radius, cell);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;

        updatePoints();
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;

        updatePoints();
    }

    public Point getCenter() {
        return center;
    }

    public void setCenter(Point center) {
        this.center = center;

        updatePoints();
    }

    public void setCenter(int x, int y) {
        setCenter(new Point(x, y));
    }

    public Cell getCell() {
        return cell;
    }

    private double findAngle(double fraction) {
        return fraction * Math.PI * 2 + Math.toRadians((rotation + 180) % 360);
    }

    private Point findPoint(double angle) {
        int x = (int) (center.x + Math.cos(angle) * radius);
        int y = (int) (center.y + Math.sin(angle) * radius);

        return new Point(x, y);
    }

    protected void updatePoints() {
        for (int p = 0; p < SIDES; p++) {
            double angle = findAngle((double) p / SIDES);
            Point point = findPoint(angle);
            xpoints[p] = point.x;
            ypoints[p] = point.y;
            points[p] = point;
            //System.out.printf("%d. (%d, %d)\n", p, point.x, point.y);
        }
    }

    public void drawPolygon(Graphics2D g, int x, int y, int lineThickness, int colorValue, boolean filled) {

        Stroke tmpS = g.getStroke();
        Color tmpC = g.getColor();

        g.setColor(new Color(colorValue));
        g.setStroke(new BasicStroke(lineThickness, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));

        if (filled)
            g.fillPolygon(xpoints, ypoints, npoints);
        else
            g.drawPolygon(xpoints, ypoints, npoints);

        g.setColor(tmpC);
        g.setStroke(tmpS);
    }


}

