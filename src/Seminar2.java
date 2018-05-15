import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class Seminar2{

    private Queue<Event> queue;
    private NavigableSet<Segment> t;
    private ArrayList<Point> x;

    public static void main(String[] args){
        Seminar2 s = new Seminar2();

        s.init_function();
        s.benley_ottman();
    }

    private void init_function(){
        this.queue = new PriorityQueue<>(new event_comparator());
        this.t = new TreeSet<>(new segment_comparator());
        this.x = new ArrayList<>();

        ArrayList<Segment> list = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader("src/podatki.txt");
            BufferedReader br = new BufferedReader(fileReader);

            String line;
            String[] data = br.readLine().split(", ");
            Point p = new Point(Double.parseDouble(data[0]), Double.parseDouble(data[1]));

            Point p1;
            while ((line = br.readLine()) != null) {
                data = line.split(", ");

                p1 = new Point(Double.parseDouble(data[0]), Double.parseDouble(data[1]));
                Segment s = new Segment(p, p1, Integer.parseInt(data[2]));

                this.queue.add(new Event(s.first(), s, 0));
                this.queue.add(new Event(s.second(), s, 1));

                //End of previous segment is start of new one
                p = p1;
            }
        }
        catch (Exception e){}
    }

    private void benley_ottman(){
        while(!this.queue.isEmpty()){
            Event e = this.queue.poll();
            
        }
    }



    private class event_comparator implements Comparator<Event> {
        @Override
        public int compare(Event e_1, Event e_2) {
            if(e_1.getValue() > e_2.getValue()) {
                return 1;
            }
            if(e_1.getValue() < e_2.getValue()) {
                return -1;
            }
            return 0;
        }
    }

    private class segment_comparator implements Comparator<Segment> {
        @Override
        public int compare(Segment s_1, Segment s_2) {
            if(s_1.get_value() < s_2.get_value()) {
                return 1;
            }
            if(s_1.get_value() > s_2.get_value()) {
                return -1;
            }
            return 0;
        }
    }


    class Event {
        private Point a;
        private ArrayList<Segment> s;
        private double value;
        private int type;

        Event(Point p, Segment s, int type) {
            a = p;
            //s = new ArrayList<>(Arrays.asList(s));
            this.type = type;
        }

        Event(Point p, ArrayList<Segment> s, int type) {
            this.a = p;
            this.s = s;
            this.type = type;
        }

        public void set_point(Point a){
            this.a = a;
        }

        public Point get_point(){
            return a;
        }

        public void set_segment(Segment s){
            this.s.add(s);
        }

        public ArrayList<Segment> get_segment(){
            return s;
        }

        public void set_type(int type){
            this.type = type;
        }

        public int get_type(){
            return type;
        }

        public void setValue(double value){
            this.value = value;
        }

        public double getValue(){
            return value;
        }
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