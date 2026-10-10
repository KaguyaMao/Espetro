/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.canvas;

import com.sighs.apricityui.canvas.CanvasPath2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

final class CanvasSvgPathParser {
    private final String source;
    private int index = 0;

    private CanvasSvgPathParser(String source) {
        this.source = source == null ? "" : source;
    }

    static void parseInto(String source, CanvasPath2D path) {
        if (path == null || source == null || source.isBlank()) {
            return;
        }
        new CanvasSvgPathParser(source).parse(path);
    }

    private void parse(CanvasPath2D path) {
        int command = 32;
        double currentX = 0.0;
        double currentY = 0.0;
        double subPathX = 0.0;
        double subPathY = 0.0;
        double lastCpx = 0.0;
        double lastCpy = 0.0;
        double lastQpx = 0.0;
        double lastQpy = 0.0;
        int previousCommand = 32;
        block12: while (true) {
            this.skipSeparators();
            if (this.index >= this.source.length()) {
                return;
            }
            char ch = this.source.charAt(this.index);
            if (CanvasSvgPathParser.isCommand(ch)) {
                command = ch;
                ++this.index;
            } else if (command == 32) {
                return;
            }
            switch (command) {
                case 77: 
                case 109: {
                    List<Double> values = this.readNumberSequence();
                    int i = 0;
                    while (i + 1 < values.size()) {
                        double x = values.get(i);
                        double y = values.get(i + 1);
                        if (command == 109) {
                            x += currentX;
                            y += currentY;
                        }
                        if (i == 0) {
                            path.moveTo(x, y);
                        } else {
                            path.lineTo(x, y);
                        }
                        currentX = subPathX = x;
                        currentY = subPathY = y;
                        i += 2;
                    }
                    previousCommand = command;
                    continue block12;
                }
                case 76: 
                case 108: {
                    List<Double> values = this.readNumberSequence();
                    int i = 0;
                    while (i + 1 < values.size()) {
                        double x = values.get(i);
                        double y = values.get(i + 1);
                        if (command == 108) {
                            x += currentX;
                            y += currentY;
                        }
                        path.lineTo(x, y);
                        currentX = x;
                        currentY = y;
                        i += 2;
                    }
                    previousCommand = command;
                    continue block12;
                }
                case 72: 
                case 104: {
                    List<Double> values = this.readNumberSequence();
                    for (double value : values) {
                        double x = command == 104 ? currentX + value : value;
                        path.lineTo(x, currentY);
                        currentX = x;
                    }
                    previousCommand = command;
                    continue block12;
                }
                case 86: 
                case 118: {
                    List<Double> values = this.readNumberSequence();
                    for (double value : values) {
                        double y = command == 118 ? currentY + value : value;
                        path.lineTo(currentX, y);
                        currentY = y;
                    }
                    previousCommand = command;
                    continue block12;
                }
                case 67: 
                case 99: {
                    double y;
                    double x;
                    double cp2y;
                    List<Double> values = this.readNumberSequence();
                    int i = 0;
                    while (i + 5 < values.size()) {
                        double cp1x = values.get(i);
                        double cp1y = values.get(i + 1);
                        double cp2x = values.get(i + 2);
                        cp2y = values.get(i + 3);
                        x = values.get(i + 4);
                        y = values.get(i + 5);
                        if (command == 99) {
                            cp1x += currentX;
                            cp1y += currentY;
                            cp2x += currentX;
                            cp2y += currentY;
                            x += currentX;
                            y += currentY;
                        }
                        path.bezierCurveTo(cp1x, cp1y, cp2x, cp2y, x, y);
                        lastCpx = cp2x;
                        lastCpy = cp2y;
                        currentX = x;
                        currentY = y;
                        i += 6;
                    }
                    previousCommand = command;
                    continue block12;
                }
                case 83: 
                case 115: {
                    double y;
                    double x;
                    double cp2y;
                    List<Double> values = this.readNumberSequence();
                    int i = 0;
                    while (i + 3 < values.size()) {
                        double cp1y;
                        double cp1x;
                        if (previousCommand == 67 || previousCommand == 99 || previousCommand == 83 || previousCommand == 115) {
                            cp1x = 2.0 * currentX - lastCpx;
                            cp1y = 2.0 * currentY - lastCpy;
                        } else {
                            cp1x = currentX;
                            cp1y = currentY;
                        }
                        double cp2x = values.get(i);
                        cp2y = values.get(i + 1);
                        x = values.get(i + 2);
                        y = values.get(i + 3);
                        if (command == 115) {
                            cp2x += currentX;
                            cp2y += currentY;
                            x += currentX;
                            y += currentY;
                        }
                        path.bezierCurveTo(cp1x, cp1y, cp2x, cp2y, x, y);
                        lastCpx = cp2x;
                        lastCpy = cp2y;
                        currentX = x;
                        currentY = y;
                        i += 4;
                    }
                    previousCommand = command;
                    continue block12;
                }
                case 81: 
                case 113: {
                    double y;
                    List<Double> values = this.readNumberSequence();
                    int i = 0;
                    while (i + 3 < values.size()) {
                        double cpx = values.get(i);
                        double cpy = values.get(i + 1);
                        double x = values.get(i + 2);
                        y = values.get(i + 3);
                        if (command == 113) {
                            cpx += currentX;
                            cpy += currentY;
                            x += currentX;
                            y += currentY;
                        }
                        path.quadraticCurveTo(cpx, cpy, x, y);
                        lastQpx = cpx;
                        lastQpy = cpy;
                        currentX = x;
                        currentY = y;
                        i += 4;
                    }
                    previousCommand = command;
                    continue block12;
                }
                case 84: 
                case 116: {
                    double y;
                    List<Double> values = this.readNumberSequence();
                    int i = 0;
                    while (i + 1 < values.size()) {
                        double cpy;
                        double cpx;
                        if (previousCommand == 81 || previousCommand == 113 || previousCommand == 84 || previousCommand == 116) {
                            cpx = 2.0 * currentX - lastQpx;
                            cpy = 2.0 * currentY - lastQpy;
                        } else {
                            cpx = currentX;
                            cpy = currentY;
                        }
                        double x = values.get(i);
                        y = values.get(i + 1);
                        if (command == 116) {
                            x += currentX;
                            y += currentY;
                        }
                        path.quadraticCurveTo(cpx, cpy, x, y);
                        lastQpx = cpx;
                        lastQpy = cpy;
                        currentX = x;
                        currentY = y;
                        i += 2;
                    }
                    previousCommand = command;
                    continue block12;
                }
                case 65: 
                case 97: {
                    while (true) {
                        this.skipSeparators();
                        if (this.index >= this.source.length() || CanvasSvgPathParser.isCommand(this.source.charAt(this.index))) break;
                        Double rxValue = this.readNumber();
                        Double ryValue = this.readNumber();
                        Double angleValue = this.readNumber();
                        Integer largeArcFlag = this.readArcFlag();
                        Integer sweepFlag = this.readArcFlag();
                        Double xValue = this.readNumber();
                        Double yValue = this.readNumber();
                        if (rxValue == null || ryValue == null || angleValue == null || largeArcFlag == null || sweepFlag == null || xValue == null || yValue == null) break;
                        double rx = rxValue;
                        double ry = ryValue;
                        double angle = angleValue;
                        boolean largeArc = largeArcFlag != 0;
                        boolean sweep = sweepFlag != 0;
                        double x = xValue;
                        double y = yValue;
                        if (command == 97) {
                            x += currentX;
                            y += currentY;
                        }
                        CanvasSvgPathParser.appendSvgArc(path, currentX, currentY, rx, ry, angle, largeArc, sweep, x, y);
                        currentX = x;
                        currentY = y;
                    }
                    previousCommand = command;
                    continue block12;
                }
                case 90: 
                case 122: {
                    path.closePath();
                    currentX = subPathX;
                    currentY = subPathY;
                    previousCommand = command;
                    continue block12;
                }
            }
            ++this.index;
        }
    }

    private List<Double> readNumberSequence() {
        ArrayList<Double> values = new ArrayList<Double>();
        while (true) {
            Double value;
            char ch;
            this.skipSeparators();
            if (this.index >= this.source.length() || CanvasSvgPathParser.isCommand(ch = this.source.charAt(this.index)) || (value = this.readNumber()) == null) break;
            values.add(value);
        }
        return values;
    }

    private Double readNumber() {
        this.skipSeparators();
        if (this.index >= this.source.length()) {
            return null;
        }
        int start = this.index;
        boolean hasDigits = false;
        if (this.source.charAt(this.index) == '+' || this.source.charAt(this.index) == '-') {
            ++this.index;
        }
        while (this.index < this.source.length() && Character.isDigit(this.source.charAt(this.index))) {
            ++this.index;
            hasDigits = true;
        }
        if (this.index < this.source.length() && this.source.charAt(this.index) == '.') {
            ++this.index;
            while (this.index < this.source.length() && Character.isDigit(this.source.charAt(this.index))) {
                ++this.index;
                hasDigits = true;
            }
        }
        if (!hasDigits) {
            return null;
        }
        if (this.index < this.source.length() && (this.source.charAt(this.index) == 'e' || this.source.charAt(this.index) == 'E')) {
            int expStart = this.index++;
            if (this.index < this.source.length() && (this.source.charAt(this.index) == '+' || this.source.charAt(this.index) == '-')) {
                ++this.index;
            }
            boolean expDigits = false;
            while (this.index < this.source.length() && Character.isDigit(this.source.charAt(this.index))) {
                ++this.index;
                expDigits = true;
            }
            if (!expDigits) {
                this.index = expStart;
            }
        }
        return Double.parseDouble(this.source.substring(start, this.index));
    }

    private Integer readArcFlag() {
        this.skipSeparators();
        if (this.index >= this.source.length()) {
            return null;
        }
        char ch = this.source.charAt(this.index);
        if (ch == '0' || ch == '1') {
            ++this.index;
            return ch - 48;
        }
        Double value = this.readNumber();
        return value == null ? null : Integer.valueOf(value == 0.0 ? 0 : 1);
    }

    private void skipSeparators() {
        char ch;
        while (this.index < this.source.length() && (Character.isWhitespace(ch = this.source.charAt(this.index)) || ch == ',')) {
            ++this.index;
        }
    }

    private static boolean isCommand(char ch) {
        return "MmLlHhVvCcSsQqTtAaZz".indexOf(ch) >= 0;
    }

    private static void appendSvgArc(CanvasPath2D path, double x1, double y1, double rx, double ry, double angleDeg, boolean largeArc, boolean sweep, double x2, double y2) {
        double factor;
        double y1p;
        double dy2;
        double sin;
        double dx2;
        if (rx == 0.0 || ry == 0.0 || x1 == x2 && y1 == y2) {
            path.lineTo(x2, y2);
            return;
        }
        rx = Math.abs(rx);
        ry = Math.abs(ry);
        double angle = Math.toRadians(angleDeg % 360.0);
        double cos = Math.cos(angle);
        double x1p = cos * (dx2 = (x1 - x2) / 2.0) + (sin = Math.sin(angle)) * (dy2 = (y1 - y2) / 2.0);
        double lambda = x1p * x1p / (rx * rx) + (y1p = -sin * dx2 + cos * dy2) * y1p / (ry * ry);
        if (lambda > 1.0) {
            double scale = Math.sqrt(lambda);
            rx *= scale;
            ry *= scale;
        }
        double rx2 = rx * rx;
        double ry2 = ry * ry;
        double x1p2 = x1p * x1p;
        double y1p2 = y1p * y1p;
        double numerator = rx2 * ry2 - rx2 * y1p2 - ry2 * x1p2;
        double denominator = rx2 * y1p2 + ry2 * x1p2;
        double d = factor = denominator == 0.0 ? 0.0 : Math.sqrt(Math.max(0.0, numerator / denominator));
        if (largeArc == sweep) {
            factor = -factor;
        }
        double cxp = factor * (rx * y1p / ry);
        double cyp = factor * (-ry * x1p / rx);
        double cx = cos * cxp - sin * cyp + (x1 + x2) / 2.0;
        double cy = sin * cxp + cos * cyp + (y1 + y2) / 2.0;
        double theta1 = CanvasSvgPathParser.vectorAngle(1.0, 0.0, (x1p - cxp) / rx, (y1p - cyp) / ry);
        double deltaTheta = CanvasSvgPathParser.vectorAngle((x1p - cxp) / rx, (y1p - cyp) / ry, (-x1p - cxp) / rx, (-y1p - cyp) / ry);
        if (!sweep && deltaTheta > 0.0) {
            deltaTheta -= Math.PI * 2;
        } else if (sweep && deltaTheta < 0.0) {
            deltaTheta += Math.PI * 2;
        }
        int segments = Math.max(1, (int)Math.ceil(Math.abs(deltaTheta) / 1.5707963267948966));
        double step = deltaTheta / (double)segments;
        for (int i = 0; i < segments; ++i) {
            double start = theta1 + (double)i * step;
            double end = start + step;
            CanvasSvgPathParser.appendArcSegment(path, cx, cy, rx, ry, angle, start, end);
        }
    }

    private static void appendArcSegment(CanvasPath2D path, double cx, double cy, double rx, double ry, double phi, double start, double end) {
        double delta = end - start;
        double t = Math.tan(delta / 4.0);
        double alpha = Math.sin(delta) * (Math.sqrt(4.0 + 3.0 * t * t) - 1.0) / 3.0;
        Point2D.Double p0 = CanvasSvgPathParser.mapEllipsePoint(cx, cy, rx, ry, phi, start);
        Point2D.Double p3 = CanvasSvgPathParser.mapEllipsePoint(cx, cy, rx, ry, phi, end);
        Point2D.Double d0 = CanvasSvgPathParser.mapEllipseDerivative(rx, ry, phi, start);
        Point2D.Double d3 = CanvasSvgPathParser.mapEllipseDerivative(rx, ry, phi, end);
        double cp1x = p0.x + alpha * d0.x;
        double cp1y = p0.y + alpha * d0.y;
        double cp2x = p3.x - alpha * d3.x;
        double cp2y = p3.y - alpha * d3.y;
        path.bezierCurveTo(cp1x, cp1y, cp2x, cp2y, p3.x, p3.y);
    }

    private static Point2D.Double mapEllipsePoint(double cx, double cy, double rx, double ry, double phi, double theta) {
        double cosPhi = Math.cos(phi);
        double sinPhi = Math.sin(phi);
        double cosTheta = Math.cos(theta);
        double sinTheta = Math.sin(theta);
        return new Point2D.Double(cx + rx * cosPhi * cosTheta - ry * sinPhi * sinTheta, cy + rx * sinPhi * cosTheta + ry * cosPhi * sinTheta);
    }

    private static Point2D.Double mapEllipseDerivative(double rx, double ry, double phi, double theta) {
        double cosPhi = Math.cos(phi);
        double sinPhi = Math.sin(phi);
        double cosTheta = Math.cos(theta);
        double sinTheta = Math.sin(theta);
        return new Point2D.Double(-rx * cosPhi * sinTheta - ry * sinPhi * cosTheta, -rx * sinPhi * sinTheta + ry * cosPhi * cosTheta);
    }

    private static double vectorAngle(double ux, double uy, double vx, double vy) {
        double dot = ux * vx + uy * vy;
        double len = Math.hypot(ux, uy) * Math.hypot(vx, vy);
        if (len == 0.0) {
            return 0.0;
        }
        double angle = Math.acos(Math.max(-1.0, Math.min(1.0, dot / len)));
        return ux * vy - uy * vx < 0.0 ? -angle : angle;
    }
}

