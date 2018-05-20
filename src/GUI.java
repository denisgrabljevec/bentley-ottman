import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.text.Segment;

/**
 * Created by valen_000 on 15. 5. 2017.
 */

public class GUI extends JFrame {

    private ArrayList<Seminar2.Segment> input_data;
    private ArrayList<Seminar2.Point> intersections;
    private boolean repaint = true;

    public GUI(final ArrayList<Seminar2.Segment> input_data, final ArrayList<Seminar2.Point> intersections) {

        this.input_data = input_data;
        this.intersections = intersections;

        JPanel panel = new JPanel();
        getContentPane().add(panel);

        setSize(1000    , 600);
        setTitle("Bentley-Ottmann algorithm");

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - getHeight()) / 2);
        setLocation(x, y);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        getRootPane().registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        getRootPane().registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                repaint = !repaint;
                repaint();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        setVisible(true);
    }

    @Override
    public void paint(Graphics g) {

        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);


        for(Seminar2.Segment s : this.input_data) {
            Line2D.Double segment = new Line2D.Double(s.first().getX_coord(), s.first().getY_coord(), s.second().getX_coord(), s.second().getY_coord());

            if(s.color == 0){
                g2.setPaint(Color.BLUE);
            }
            else if(s.color == 1){
                g2.setPaint(Color.RED);
            }
            else if(s.color == 2){
                g2.setPaint(Color.GREEN);
            }

            int a = (int)(s.first().getX_coord() + s.second().getX_coord()) / 2;
            int b = (int)(s.first().getY_coord() + s.second().getY_coord()) / 2;
            g2.draw(segment);
        }

        if(repaint) {
            g2.drawString("number of intersections: " + this.intersections.size(), 40, 70);
            for (Seminar2.Point p : this.intersections) {
                double new_x = p.getX_coord() - 6 / 2.0;
                double new_y = p.getY_coord() - 6 / 2.0;
                Ellipse2D.Double point = new Ellipse2D.Double(new_x, new_y, 6, 6);
                g2.setPaint(Color.BLACK);
                g2.fill(point);
                g2.draw(point);
            }
        }

    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    class Segment {
        Point a;
        Point b;
        int color;
        double value;

        Segment(Point a, Point b, int color){
            this.a = a;
            this.b = b;
            this.color = color;
            this.calculate_value(this.first().getX_coord());
        }

        public Point first() {
            if(a.getX_coord() <= b.getX_coord()) {
                return a;
            } else {
                return b;
            }
        }

        public Point second() {
            if(a.getX_coord() <= b.getX_coord()) {
                return b;
            } else {
                return a;
            }
        }

        public void calculate_value(double value) {
            double x1 = this.first().getX_coord();
            double x2 = this.second().getX_coord();
            double y1 = this.first().getY_coord();
            double y2 = this.second().getY_coord();
            this.value = y1 + (((y2 - y1) / (x2 - x1)) * (value - x1));
        }

        public void set_value(double value) {
            this.value = value;
        }

        public double get_value() {
            return this.value;
        }
    }

    class Point {
        private double x_coord;
        private double y_coord;

        Point(){}

        Point(double x, double y){
            x_coord = x;
            y_coord = y;
        }

        public void setX_coord(int x){
            x_coord = x;
        }

        public void setY_coord(int y){
            y_coord = y;
        }

        public double getX_coord() {
            return x_coord;
        }

        public double getY_coord() {
            return y_coord;
        }
    }
}