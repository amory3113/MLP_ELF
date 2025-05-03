import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class DrawingPanel extends JPanel {
    private final BufferedImage canvas;
    private final Graphics2D g2;
    private Point lastPoint;
    private int drawSize = 13;
    private static final int GRID = 24;

    public DrawingPanel(BufferedImage canvas) {
        this.canvas = canvas;
        setPreferredSize(new Dimension(canvas.getWidth(), canvas.getHeight()));
        setBackground(Color.WHITE);
        g2 = canvas.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        g2.setColor(Color.BLACK);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastPoint = e.getPoint();
                drawPoint(e.getPoint());
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                Point currentPoint = e.getPoint();
                drawLine(lastPoint, currentPoint);
                lastPoint = currentPoint;
            }
        };
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    public BufferedImage getCanvas() {
        return canvas;
    }

    public double[] capture14() {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        int bg = Color.WHITE.getRGB();
        int minX = width, minY = height, maxX = -1, maxY = -1;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (canvas.getRGB(x, y) != bg) {
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
        }
        if (maxX < minX || maxY < minY) {
            return new double[GRID * GRID];
        }
        int rectW = maxX - minX + 1;
        int rectH = maxY - minY + 1;
        boolean[][] cell = new boolean[GRID][GRID];
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                if (canvas.getRGB(x, y) != bg) {
                    int i = x - minX;
                    int j = y - minY;
                    int cx = i * GRID / rectW;
                    int cy = j * GRID / rectH;
                    if (cx >= 0 && cx < GRID && cy >= 0 && cy < GRID) {
                        cell[cx][cy] = true;
                    }
                }
            }
        }
        double[] v = new double[GRID * GRID];
        for (int ix = 0; ix < GRID; ix++) {
            for (int iy = 0; iy < GRID; iy++) {
                v[iy * GRID + ix] = cell[ix][iy] ? 1.0 : 0.0;
            }
        }
        return v;
    }

    public void clear() {
        Graphics2D g = canvas.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        g.dispose();
        repaint();
    }

    private void drawPoint(Point p) {
        g2.fillOval(p.x - drawSize/2, p.y - drawSize/2, drawSize, drawSize);
        repaint();
    }

    private void drawLine(Point from, Point to) {
        g2.setStroke(new BasicStroke(drawSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(from.x, from.y, to.x, to.y);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(240, 240, 240));
        int gridSize = 20;
        for (int x = 0; x < getWidth(); x += gridSize) {
            g.drawLine(x, 0, x, getHeight());
        }
        for (int y = 0; y < getHeight(); y += gridSize) {
            g.drawLine(0, y, getWidth(), y);
        }
        g.drawImage(canvas, 0, 0, null);
    }
}
