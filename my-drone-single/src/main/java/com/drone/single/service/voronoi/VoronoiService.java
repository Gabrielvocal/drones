package com.drone.single.service.voronoi;

import com.drone.single.service.IsInside;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;

@Service
public class VoronoiService {
    @Autowired
    private IsInside isInside;
    List<Line2D.Double> sides=new ArrayList<>();
    // Map<Point2D.Double, HashSet<Point2D.Double>> map=new HashMap<>();
     //Delaunay delaunay;
    //private Triangle<Vpoint> initialSuperTriangle;
    private int initialSize = 10000;
    private boolean displayVoronoi=true;
   // public Color backgroundColor = Color.green;
    //public int radius =4;
    //private Graphics g;
    int num=0;
    public VoronoiService () {
        /*initialSuperTriangle = new Triangle<Vpoint>(new Vpoint(-initialSize, -initialSize),
                new Vpoint( initialSize, -initialSize),
                new Vpoint(           0,  initialSize));*/
       // delaunay = new Delaunay(initialSuperTriangle);
    }
    public Map<Point2D.Double, HashSet<Point2D.Double>> pointInsert (String[] drones,List<Point2D.Double> endpoints,int time) {
        Triangle<Vpoint> initialSuperTriangle = new Triangle<Vpoint>(new Vpoint(-initialSize, -initialSize),
                new Vpoint( initialSize, -initialSize),
                new Vpoint(           0,  initialSize));
        Delaunay delaunay= new Delaunay(initialSuperTriangle);
        Map<Point2D.Double, HashSet<Point2D.Double>> map=new HashMap<>();
        for (String s : drones) {

            String[] str=s.split(",");
            Vpoint point = new Vpoint(Double.valueOf(str[0]),Double.valueOf(str[1]));
            Point2D.Double pd=new Point2D.Double(point.getXY(0),point.getXY(1));
            if(!isInside.IsPtInPoly(pd,endpoints)) return null;
            map.put(pd,new HashSet<>());
            delaunay.PointInsert(point);
            displayVoronoi=false;
            num++;
        }
        map=paintVoronoi(endpoints,map,delaunay, time);

        return map;


    }
    public Map<Point2D.Double, HashSet<Point2D.Double>> paintVoronoi (List<Point2D.Double> endpoints,Map<Point2D.Double, HashSet<Point2D.Double>> map,Delaunay delaunay,int time) {
        List<Point2D.Double> pts=new ArrayList<>();//记录端点；
        for (int i = 0; i < endpoints.size(); i++) {
            pts.add(endpoints.get(i));


        }

        for (Triangle<Vpoint> triangle: delaunay)
            for (Triangle<Vpoint> anotherTriangle: delaunay.getNeighbors(triangle)) {
                Vpoint t = Vpoint.circlecenterOfTri(triangle.toArray(new Vpoint[0]));
                Vpoint a = Vpoint.circlecenterOfTri(anotherTriangle.toArray(new Vpoint[0]));

                //pts.remove(pts.size()-1);
            boolean resa= isInside.IsPtInPoly(new Point2D.Double(a.getXY(0),a.getXY(1)),pts);
            boolean rest= isInside.IsPtInPoly(new Point2D.Double(t.getXY(0),t.getXY(1)),pts);
                if(resa&&rest){
                    Point2D.Double t1=new Point2D.Double(t.getXY(0),t.getXY(1));
                    Point2D.Double a1=new Point2D.Double(a.getXY(0),a.getXY(1));
                    if(!isContain(endpoints,t1))endpoints.add(t1);
                    if(!isContain(endpoints,a1))endpoints.add(a1);
                    List<Point2D.Double> pts11=new ArrayList<>(pts);
                    pts11.add(endpoints.get(0));
                    endpoints=addCross(pts11,a,t,endpoints);
                }else if(resa){
                    Point2D.Double a1=new Point2D.Double(a.getXY(0),a.getXY(1));
                    if(!!isContain(endpoints,a1))endpoints.add(a1);
                    List<Point2D.Double> pts11=new ArrayList<>(pts);
                    pts11.add(endpoints.get(0));
                    endpoints=addCross(pts11,a,t,endpoints);
                }else if(rest){
                    Point2D.Double t1=new Point2D.Double(t.getXY(0),t.getXY(1));
                    //Point2D.Double a1=new Point2D.Double(a.getXY(0),a.getXY(1));
                    if(!!isContain(endpoints,t1))endpoints.add(t1);
                    List<Point2D.Double> pts11=new ArrayList<>(pts);
                    pts11.add(endpoints.get(0));
                    endpoints=addCross(pts11,a,t,endpoints);
                   // endpoints=addCross(pts,a,t,endpoints);
                }else{
                    List<Point2D.Double> pts11=new ArrayList<>(pts);
                    pts11.add(endpoints.get(0));
                    endpoints=addCross(pts11,a,t,endpoints);
                    //endpoints=addCross(pts,a,t,endpoints);
                }
           /* if(resa&&rest) {
                sides.add(new Line2D.Double(new Point2D.Double(t.getXY(0),t.getXY(1)),
                        new Point2D.Double(a.getXY(0), a.getXY(1))));
                for (Vpoint vpoint : triangle) {

                    //if(map.containsKey())
                    double x=vpoint.getXY(0);
                    double y=vpoint.getXY(1);
                    boolean in= isInside.IsPtInPoly(new Point2D.Double(vpoint.getXY(0),vpoint.getXY(1)),pts);
                    if(in){
                        for (Point2D.Double aDouble : map.keySet()) {
                            if(aDouble.getX()==x&&aDouble.getY()==y){
                                map.get(aDouble).add(new Point2D.Double(t.getXY(0),t.getXY(1)));
                                //map.get(aDouble).add(new Point2D.Double(a.getXY(0),a.getXY(1)));
                            }
                        }
                    }

                }
                for (Vpoint vpoint : anotherTriangle) {
                    double x=vpoint.getXY(0);
                    double y=vpoint.getXY(1);
                    boolean in= isInside.IsPtInPoly(new Point2D.Double(vpoint.getXY(0),vpoint.getXY(1)),pts);
                    if(in){
                        for (Point2D.Double aDouble : map.keySet()) {
                            if(aDouble.getX()==x&&aDouble.getY()==y){
                                //map.get(aDouble).add(new Point2D.Double(t.getXY(0),t.getXY(1)));
                                map.get(aDouble).add(new Point2D.Double(a.getXY(0),a.getXY(1)));
                            }
                        }
                    }

                }
                continue;
            }
                pts.add(endpoints.get(0));

            if(!resa&&rest){
                for (int i = 0; i < pts.size()-1; i++) {
                    Point2D.Double crossPoint = isInside.getCrossPoint(new Line2D.Double(pts.get(i), pts.get(i + 1)),
                            new Line2D.Double(new Point2D.Double(a.getXY(0), a.getXY(1)),
                                    new Point2D.Double(t.getXY(0), t.getXY(1))));
                    if(crossPoint!=null){
                        sides.add(new Line2D.Double(crossPoint,new Point2D.Double(t.getXY(0), t.getXY(1))));
                        for (Vpoint vpoint : triangle) {
                            //if(map.containsKey())
                            double x=vpoint.getXY(0);
                            double y=vpoint.getXY(1);
                            ArrayList<Point2D.Double> doubles = new ArrayList<>(pts);
                            doubles.remove(doubles.size()-1);

                            boolean in= isInside.IsPtInPoly(new Point2D.Double(vpoint.getXY(0),vpoint.getXY(1)),doubles);
                            if (in) {
                                for (Point2D.Double aDouble : map.keySet()) {
                                if(aDouble.getX()==x&&aDouble.getY()==y){
                                    map.get(aDouble).add(new Point2D.Double(t.getXY(0),t.getXY(1)));
                                    //map.get(aDouble).add(crossPoint);
                                    //map.get(aDouble).add(new Point2D.Double(a.getXY(0),a.getXY(1)));
                                }
                            }
                            }


                        }
                        for (Vpoint vpoint : anotherTriangle) {
                            double x=vpoint.getXY(0);
                            double y=vpoint.getXY(1);
                            ArrayList<Point2D.Double> doubles = new ArrayList<>(pts);
                            doubles.remove(doubles.size()-1);

                            boolean in= isInside.IsPtInPoly(new Point2D.Double(vpoint.getXY(0),vpoint.getXY(1)),doubles);
                            if (in){
                                for (Point2D.Double aDouble : map.keySet()) {
                                    if(aDouble.getX()==x&&aDouble.getY()==y){
                                        map.get(aDouble).add(new Point2D.Double(t.getXY(0),t.getXY(1)));
                                        //map.get(aDouble).add(new Point2D.Double(a.getXY(0),a.getXY(1)));
                                        map.get(aDouble).add(crossPoint);
                                    }
                                }
                            }

                        }
                    }else {
                        for (Vpoint vpoint : triangle) {
                            //if(map.containsKey())
                            double x=vpoint.getXY(0);
                            double y=vpoint.getXY(1);
                            ArrayList<Point2D.Double> doubles = new ArrayList<>(pts);
                            doubles.remove(doubles.size()-1);

                            boolean in= isInside.IsPtInPoly(new Point2D.Double(vpoint.getXY(0),vpoint.getXY(1)),doubles);
                            if (in){
                                for (Point2D.Double aDouble : map.keySet()) {
                                    if(aDouble.getX()==x&&aDouble.getY()==y){
                                        map.get(aDouble).add(new Point2D.Double(t.getXY(0),t.getXY(1)));
                                        //map.get(aDouble).add(crossPoint);
                                        //map.get(aDouble).add(new Point2D.Double(a.getXY(0),a.getXY(1)));
                                    }
                                }
                            }

                        }
                        for (Vpoint vpoint : anotherTriangle) {
                            double x=vpoint.getXY(0);
                            double y=vpoint.getXY(1);
                            ArrayList<Point2D.Double> doubles = new ArrayList<>(pts);
                            doubles.remove(doubles.size()-1);

                            boolean in= isInside.IsPtInPoly(new Point2D.Double(vpoint.getXY(0),vpoint.getXY(1)),doubles);
                            if (in){
                                for (Point2D.Double aDouble : map.keySet()) {
                                    if(aDouble.getX()==x&&aDouble.getY()==y){
                                        //map.get(aDouble).add(new Point2D.Double(t.getXY(0),t.getXY(1)));
                                        map.get(aDouble).add(new Point2D.Double(t.getXY(0),t.getXY(1)));
                                        //map.get(aDouble).add(crossPoint);
                                    }
                                }
                            }

                        }
                    }
                }
            }
                if(!rest&&resa){
                    for (int i = 0; i < pts.size()-1; i++) {
                        Point2D.Double crossPoint = isInside.getCrossPoint(new Line2D.Double(pts.get(i), pts.get(i + 1)),
                                new Line2D.Double(new Point2D.Double(a.getXY(0), a.getXY(1)),
                                        new Point2D.Double(t.getXY(0), t.getXY(1))));
                        if(crossPoint!=null){
                            sides.add(new Line2D.Double(crossPoint,new Point2D.Double(a.getXY(0), a.getXY(1))));
                            for (Vpoint vpoint : triangle) {
                                //if(map.containsKey())
                                double x=vpoint.getXY(0);
                                double y=vpoint.getXY(1);
                                ArrayList<Point2D.Double> doubles = new ArrayList<>(pts);
                                doubles.remove(doubles.size()-1);

                                boolean in= isInside.IsPtInPoly(new Point2D.Double(vpoint.getXY(0),vpoint.getXY(1)),doubles);
                                if (in){
                                    for (Point2D.Double aDouble : map.keySet()) {
                                        if(aDouble.getX()==x&&aDouble.getY()==y){
                                            // map.get(aDouble).add(new Point2D.Double(t.getXY(0),t.getXY(1)));
                                            map.get(aDouble).add(crossPoint);
                                            map.get(aDouble).add(new Point2D.Double(a.getXY(0),a.getXY(1)));
                                        }
                                    }
                                }

                            }
                            for (Vpoint vpoint : anotherTriangle) {
                                double x=vpoint.getXY(0);
                                double y=vpoint.getXY(1);
                                ArrayList<Point2D.Double> doubles = new ArrayList<>(pts);
                                doubles.remove(doubles.size()-1);

                                boolean in= isInside.IsPtInPoly(new Point2D.Double(vpoint.getXY(0),vpoint.getXY(1)),doubles);
                                if (in){
                                    for (Point2D.Double aDouble : map.keySet()) {
                                        if(aDouble.getX()==x&&aDouble.getY()==y){
                                            //map.get(aDouble).add(new Point2D.Double(t.getXY(0),t.getXY(1)));
                                            map.get(aDouble).add(new Point2D.Double(a.getXY(0),a.getXY(1)));
                                            // map.get(aDouble).add(crossPoint);
                                        }
                                    }
                                }

                            }
                            //nodes.add(new Point2D.Double(a.getXY(0), a.getXY(1)));

                        }else{
                            for (Vpoint vpoint : triangle) {
                                //if(map.containsKey())
                                double x=vpoint.getXY(0);
                                double y=vpoint.getXY(1);
                                ArrayList<Point2D.Double> doubles = new ArrayList<>(pts);
                                doubles.remove(doubles.size()-1);

                                boolean in= isInside.IsPtInPoly(new Point2D.Double(vpoint.getXY(0),vpoint.getXY(1)),doubles);
                                if (in){
                                    for (Point2D.Double aDouble : map.keySet()) {
                                        if(aDouble.getX()==x&&aDouble.getY()==y){
                                            map.get(aDouble).add(new Point2D.Double(a.getXY(0),a.getXY(1)));
                                            //map.get(aDouble).add(crossPoint);
                                            //map.get(aDouble).add(new Point2D.Double(a.getXY(0),a.getXY(1)));
                                        }
                                    }
                                }

                            }
                            for (Vpoint vpoint : anotherTriangle) {
                                double x=vpoint.getXY(0);
                                double y=vpoint.getXY(1);
                                ArrayList<Point2D.Double> doubles = new ArrayList<>(pts);
                                doubles.remove(doubles.size()-1);

                                boolean in= isInside.IsPtInPoly(new Point2D.Double(vpoint.getXY(0),vpoint.getXY(1)),doubles);
                                if (in){
                                    for (Point2D.Double aDouble : map.keySet()) {
                                        if(aDouble.getX()==x&&aDouble.getY()==y){
                                            //map.get(aDouble).add(new Point2D.Double(t.getXY(0),t.getXY(1)));
                                            map.get(aDouble).add(new Point2D.Double(a.getXY(0),a.getXY(1)));
                                            // map.get(aDouble).add(crossPoint);
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
                else{
                    List<Point2D.Double> cross=new ArrayList<>();
                    for (int i = 0; i < pts.size()-1; i++) {
                        Point2D.Double crossPoint = isInside.getCrossPoint(new Line2D.Double(pts.get(i), pts.get(i + 1)),
                                new Line2D.Double(new Point2D.Double(a.getXY(0), a.getXY(1)),
                                        new Point2D.Double(t.getXY(0), t.getXY(1))));
                        if(crossPoint!=null)cross.add(crossPoint);
                    }
                    if(cross.size()>1){
                        for (Point2D.Double aDouble : cross) {
                            if(!endpoints.contains(aDouble))endpoints.add(aDouble);

                        }
                    }
                }*/


              //pts.remove(pts.size()-1);

            }
        map=endPointVoronoi(endpoints,map, pts.size(),time);
        return map;
    }
    public Map<Point2D.Double, HashSet<Point2D.Double>> endPointVoronoi(List<Point2D.Double> endpoints,Map<Point2D.Double, HashSet<Point2D.Double>> map,int angel,int time){
        Set<Point2D.Double> po = map.keySet();
        double mis=0.001+0.006*time;
        for (int i = 0; i < endpoints.size(); i++) {
            List<Point2D.Double> minps=new ArrayList<>();

            Point2D.Double minp=new Point2D.Double();//不知是否有效，先尝试。
            // BigDecimal bigDecimal=new BigDecimal(String.valueOf(minp));
            double minlen=Double.MAX_VALUE;
            for (Point2D.Double aDouble : po) {
                double temp=minlen;
                minlen=Math.min(minlen,endpoints.get(i).distance(aDouble));
                if(minlen!=temp) minp=aDouble;
            }
            for (Point2D.Double aDouble : po) {
                BigDecimal e=new BigDecimal(String.valueOf(endpoints.get(i).distance(aDouble)));
                BigDecimal m=new BigDecimal(String.valueOf(minlen));
                boolean flag=true;

               if(endpoints.get(i).distance(aDouble)-minlen<=mis&&endpoints.get(i).distance(aDouble)-minlen>=-mis){
                //if(e.subtract(m).doubleValue()<=0.01&&e.subtract(m).doubleValue()>=-0.01){
                //if(e.subtract(m).equals(0.0)){
                   minps.add(aDouble);
                   flag=false;

               }

                //if(minlen!=temp) minp=aDouble;
            }
            for (Point2D.Double minp1 : minps) {
                map.get(minp1).add(endpoints.get(i));
            }


        }
        Map<Point2D.Double, HashSet<Point2D.Double>> remap=new HashMap<>(map);
        //map.clear();
        return remap;
    }
    public List<Point2D.Double> addCross(List<Point2D.Double> pts,Vpoint a,Vpoint t,List<Point2D.Double> endpoints){
        List<Point2D.Double> cross=new ArrayList<>();
        for (int i = 0; i < pts.size()-1; i++) {
            Point2D.Double crossPoint = isInside.getCrossPoint(new Line2D.Double(pts.get(i), pts.get(i + 1)),
                    new Line2D.Double(new Point2D.Double(a.getXY(0), a.getXY(1)),
                            new Point2D.Double(t.getXY(0), t.getXY(1))));
            if(crossPoint!=null)cross.add(crossPoint);
        }
        if(cross.size()==2){
            for (Point2D.Double aDouble : cross) {
                if(!isContain(endpoints,aDouble))endpoints.add(aDouble);

            }
        }
        return endpoints;
    }
    public boolean isContain(List<Point2D.Double> endpoints,Point2D.Double p){
        for (Point2D.Double endpoint : endpoints) {
            if((endpoint.getX()-p.getX()<=0.001&&endpoint.getX()-p.getX()>=-0.001)&&(endpoint.getY()-p.getY()<=0.001&&endpoint.getY()-p.getY()>=-0.001))
                return true;
        }
        return false;

    }

}
