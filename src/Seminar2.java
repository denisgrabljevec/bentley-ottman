import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class Seminar2{

    private Queue<Event> queue;
    private NavigableSet<Segment> t;
    private ArrayList<Point> x;
    private HashSet<String> x1;

    private int eventId = 0;

    public static void main(String[] args){
        Seminar2 s = new Seminar2();

        ArrayList<Segment> list = s.init_function();
        s.benley_ottman();

        new GUI(list, s.get_x());
    }

    public ArrayList<Point> get_x(){
        return this.x;
    }

    private ArrayList<Segment> init_function(){
        this.queue = new PriorityQueue<>(new event_comparator());
        this.t = new TreeSet<>(new segment_comparator());
        this.x = new ArrayList<>();
        this.x1 = new HashSet<>();

        ArrayList<Segment> list = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader("src/podatki.txt");
            //FileReader fileReader = new FileReader("src/test.txt");
            BufferedReader br = new BufferedReader(fileReader);

            String line;
            String[] data;
            Point p = new Point();
            Point p1;

            int color = -1;
            int index = 1;
            while ((line = br.readLine()) != null) {
                data = line.split(", ");
                if(color != Integer.parseInt(data[2])){
                    color = Integer.parseInt(data[2]);
                    p = new Point(Double.parseDouble(data[0]), Double.parseDouble(data[1]));
                    continue;
                }

                p1 = new Point(Double.parseDouble(data[0]), Double.parseDouble(data[1]));
                Segment s = new Segment(p, p1, Integer.parseInt(data[2]), index++);

                list.add(s);

                this.queue.add(new Event(s.first(), s, 0, eventId++));

                this.queue.add(new Event(s.second(), s, 1, eventId++));

                //End of previous segment is start of new one
                p = p1;
            }
        }
        catch (Exception e){}

        return list;
    }

    private void benley_ottman(){
        int index = 1;
        while(!this.queue.isEmpty()){
            Event e = this.queue.poll();
            double L = e.getValue();

            switch (e.get_type()){
                case 0:
                    for(Segment s : e.get_segment()) {
                        s.set_segId(index);
                        this.t.add(s);
                        Segment s1 = this.t.lower(s);
                        int loopCount = 0;
                        while(s1 != null){
                            this.report_intersection(s1, s, L);
                            s1 = this.t.lower(s1);
                            loopCount++;
                        }
                        System.out.println("1: "+loopCount);

                        int loopCount1 = 0;
                        Segment t = this.t.higher(s);
                        while(t != null){
                            this.report_intersection(t, s, L);
                            t = this.t.higher(t);
                            loopCount1++;
                        }
                        System.out.println("2: "+loopCount1);
                    }
                    index++;
                    break;
                case 1:
                    for(Segment s : e.get_segment()) {
                        if(this.t.lower(s) != null && this.t.higher(s) != null) {
                            Segment r = this.t.lower(s);
                            Segment t = this.t.higher(s);
                            this.report_intersection(r, t, L);
                        }
                        this.t.remove(s);
                    }
                    break;
                case 2:
                    Segment s_1 = e.get_segment().get(0);
                    Segment s_2 = e.get_segment().get(1);
                    /*this.swap(s_1, s_2);
                    if(s_1.get_segId() < s_2.get_segId()){
                        if(this.t.lower(s_1) != null) {
                            Segment t = this.t.lower(s_1);
                            this.report_intersection(t, s_1, L);
                            this.remove_future(t, s_2);
                        }
                        if(this.t.higher(s_2) != null) {
                            Segment r = this.t.higher(s_2);
                            this.report_intersection(r, s_2, L);
                            this.remove_future(r, s_1);
                        }
                    }
                    else {
                        if(this.t.lower(s_2) != null) {
                            Segment t = this.t.lower(s_2);
                            this.report_intersection(t, s_2, L);
                            this.remove_future(t, s_1);
                        }
                        if(this.t.higher(s_1) != null) {
                            Segment r = this.t.higher(s_1);
                            this.report_intersection(r, s_1, L);
                            this.remove_future(r, s_2);
                        }
                    }*/

                    String a;
                    if(e.get_segment().get(0).trueId < e.get_segment().get(1).trueId){
                        a = e.get_segment().get(0).trueId + "->" + e.get_segment().get(1).trueId;
                    }
                    else {
                        a = e.get_segment().get(1).trueId + "->" + e.get_segment().get(0).trueId;
                    }

                    if(!this.x1.contains(a)) this.x.add(e.get_point());
                    this.x1.add(a);
                    break;
            }
        }
        System.out.println("Konec: "+ this.x1.size());
    }

    private boolean report_intersection(Segment s_1, Segment s_2, double L) {
        if(s_1.color == s_2.color) return false;

        double x1 = s_1.first().getX_coord();
        double y1 = s_1.first().getY_coord();
        double x2 = s_1.second().getX_coord();
        double y2 = s_1.second().getY_coord();
        double x3 = s_2.first().getX_coord();
        double y3 = s_2.first().getY_coord();
        double x4 = s_2.second().getX_coord();
        double y4 = s_2.second().getY_coord();
        double r = (x2 - x1) * (y4 - y3) - (y2 - y1) * (x4 - x3);
        if(r != 0) {
            double t = ((x3 - x1) * (y4 - y3) - (y3 - y1) * (x4 - x3)) / r;
            double u = ((x3 - x1) * (y2 - y1) - (y3 - y1) * (x2 - x1)) / r;
            if(t >= 0 && t <= 1 && u >= 0 && u <= 1) {
                double x_c = x1 + t * (x2 - x1);
                double y_c = y1 + t * (y2 - y1);
                //if(x_c > L) {
                    this.queue.add(new Event(new Point(x_c, y_c), new ArrayList<>(Arrays.asList(s_1, s_2)), 2, eventId++));
                    return true;
                //}
            }
        }
        // if points x2 is start of x1 with different colors
        else {
            if(x1 == x4 && y1 == y4){
                this.queue.add(new Event(new Point(x1, y1), new ArrayList<>(Arrays.asList(s_1, s_2)), 2, eventId++));
                return true;
            }
            else if(x2 == x3 && y2 == y3){
                this.queue.add(new Event(new Point(x2, y2), new ArrayList<>(Arrays.asList(s_1, s_2)), 2, eventId++));
                return true;
            }
        }

        double k1 = (y2 - y1) / (x2 - x1);
        double a1 = y1 - (k1 * x1);

        double k2 = (y4 - y3) / (x4 - x3);
        double a2 = y3 - (k2 * x3);

        if(k1 == k2 && a1 == a2){
            if(x3 > x1 && x3 < x2){
                this.queue.add(new Event(new Point(x3, y3), new ArrayList<>(Arrays.asList(s_1, s_2)), 2, eventId++));
            }
            else if(x1 > x3 && x1 < x4){
                this.queue.add(new Event(new Point(x1, y1), new ArrayList<>(Arrays.asList(s_1, s_2)), 2, eventId++));
            }

        }

        return false;
    }

    private boolean remove_future(Segment s_1, Segment s_2) {
        for(Event e : this.queue) {
            if(e.get_type() == 2) {
                if((e.get_segment().get(0) == s_1 && e.get_segment().get(1) == s_2) || (e.get_segment().get(0) == s_2 && e.get_segment().get(1) == s_1)) {
                    this.queue.remove(e);
                    return true;
                }
            }
        }
        return false;
    }

    private void swap(Segment s_1, Segment s_2) {
        this.t.remove(s_1);
        this.t.remove(s_2);
        int tmp_id = s_1.get_segId();
        s_1.set_segId(s_2.get_segId());
        s_2.set_segId(tmp_id);
        this.t.add(s_2);
        this.t.add(s_1);
    }


    private class event_comparator implements Comparator<Event> {
        @Override
        public int compare(Event e_1, Event e_2) {
            if(e_1.get_point().getX_coord() < e_2.get_point().getX_coord()){
                return -1;
            }
            else if(e_1.get_point().getX_coord() > e_2.get_point().getX_coord()){
                return 1;
            }

            if(e_1.get_type() == 0 && e_2.get_type() != 0) return -1;
            if(e_1.get_type() == 1 && e_2.get_type() != 1) return 1;

            if(e_1.get_point().getY_coord() < e_2.get_point().getY_coord()){
                return -1;
            }
            else if(e_1.get_point().getY_coord() > e_2.get_point().getY_coord()){
                return 1;
            }

            return 0;
        }
    }

    private class segment_comparator implements Comparator<Segment> {
        @Override
        public int compare(Segment s_1, Segment s_2) {
            /*if(s_1.first().getX_coord() < s_2.first().getX_coord()) {
                return 1;
            }
            if(s_1.first().getX_coord() > s_2.first().getX_coord()) {
                return -1;
            }
            if(s_1.first().getX_coord() == s_2.first().getX_coord()){
                if(s_1.first().getY_coord() < s_2.first().getY_coord()) {
                    return 1;
                }
                if(s_1.first().getY_coord() > s_2.first().getY_coord()) {
                    return -1;
                }
                if(s_1.first().getY_coord() == s_2.first().getY_coord()) {
                    if(s_1.second().getX_coord() < s_2.second().getX_coord()) {
                        return 1;
                    }
                    if(s_1.second().getX_coord() > s_2.second().getX_coord()) {
                        return -1;
                    }
                    if(s_1.second().getX_coord() == s_2.second().getX_coord()) {
                        if(s_1.second().getY_coord() < s_2.second().getY_coord()){
                            return 1;
                        }
                        if(s_1.second().getY_coord() > s_2.second().getY_coord()){
                            return -1;
                        }
                    }
                }
            }*/

            /*if(s_1.get_value() < s_2.get_value()) return 1;

            if(s_1.get_value() > s_2.get_value()) return -1;

            if(s_1.get_value() == s_2.get_value()){
                if(s_1.second().getY_coord() < s_2.second().getY_coord()) {
                    return 1;
                }
                if(s_1.second().getY_coord() > s_2.second().getY_coord()) {
                    return -1;
                }
            }*/

            if(s_1.get_segId() < s_2.get_segId()) return -1;
            if(s_1.get_segId() > s_2.get_segId()) return 1;

            return 0;
        }
    }


    class Event {
        private Point a;
        private ArrayList<Segment> s;
        private double value;
        private int type;
        public int event_id;

        Event(Point p, Segment s, int type, int eventId) {
            a = p;
            this.s = new ArrayList<>(Arrays.asList(s));
            this.type = type;

            this.value = p.getX_coord();
            this.event_id = eventId;
        }

        Event(Point p, ArrayList<Segment> s, int type, int eventId) {
            this.a = p;
            this.s = s;
            this.type = type;

            this.value = p.getX_coord();
            this.event_id = eventId;
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
        int trueId;
        int seg_id;

        Segment(Point a, Point b, int color, int id){
            this.a = a;
            this.b = b;
            this.color = color;
            this.calculate_value(this.first().getX_coord());
            this.trueId = id;
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
        }

        public void set_segId(int segId){
            seg_id = segId;
        }

        public int get_segId(){
            return seg_id;
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