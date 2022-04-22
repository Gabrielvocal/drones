package com.drone.single.service;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.List;
@Component
public class IsInside {

    public static boolean IsPtInPoly(Point2D.Double point, List<Point2D.Double> pts){

        int N = pts.size();
        boolean boundOrVertex = true; //如果点位于多边形的顶点或边上，也算做点在多边形内，直接返回true
        int intersectCount = 0;//cross points count of x
        double precision = 2e-10; //浮点类型计算时候与0比较时候的容差
        Point2D.Double p1, p2;//neighbour bound vertices
        Point2D.Double p = point; //当前点

        p1 = pts.get(0);//left vertex
        for(int i = 1; i <= N; ++i){//check all rays
            if(p.equals(p1)){
                return boundOrVertex;//p is an vertex
            }

            p2 = pts.get(i % N);//right vertex
            if(p.x < Math.min(p1.x, p2.x) || p.x > Math.max(p1.x, p2.x)){//ray is outside of our interests
                p1 = p2;
                continue;//next ray left point
            }

            if(p.x > Math.min(p1.x, p2.x) && p.x < Math.max(p1.x, p2.x)){//ray is crossing over by the algorithm (common part of)
                if(p.y <= Math.max(p1.y, p2.y)){//x is before of ray
                    if(p1.x == p2.x && p.y >= Math.min(p1.y, p2.y)){//overlies on a horizontal ray
                        return boundOrVertex;
                    }

                    if(p1.y == p2.y){//ray is vertical
                        if(p1.y == p.y){//overlies on a vertical ray
                            return boundOrVertex;
                        }else{//before ray
                            ++intersectCount;
                        }
                    }else{//cross point on the left side
                        double xinters = (p.x - p1.x) * (p2.y - p1.y) / (p2.x - p1.x) + p1.y;//cross point of y
                        if(Math.abs(p.y - xinters) < precision){//overlies on a ray
                            return boundOrVertex;
                        }
                        if(p.y < xinters){//before ray
                            ++intersectCount;
                        }
                    }
                }
            }else{//special case when ray is crossing through the vertex
                if(p.x == p2.x && p.y <= p2.y){//p crossing over p2
                    Point2D.Double p3 = pts.get((i+1) % N); //next vertex
                    if(p.x >= Math.min(p1.x, p3.x) && p.x <= Math.max(p1.x, p3.x)){//p.x lies between p1.x & p3.x
                        ++intersectCount;
                    }else{
                        intersectCount += 2;
                    }
                }
            }
            p1 = p2;//next ray left point
        }

        if(intersectCount % 2 == 0){//偶x数在多边形外
            return false;
        } else { //奇数在多边形内
            return true;
        }

    }
    public  Point2D.Double getCrossPoint(Line2D.Double lsegA, Line2D.Double lsegB){
        double x;
        double y;
        double x1=lsegA.getX1();
        double y1=lsegA.getY1();
        double x2=lsegA.getX2();
        double y2=lsegA.getY2();
        double x3=lsegB.getX1();
        double y3=lsegB.getY1();
        double x4=lsegB.getX2();
        double y4=lsegB.getY2();
        double k1=Float.MAX_VALUE;
        double k2=Float.MAX_VALUE;
        boolean flag1=false;
        boolean flag2=false;

        if((x1-x2)==0)
            flag1=true;
        if((x3-x4)==0)
            flag2=true;

        if(!flag1)
            k1=(y1-y2)/(x1-x2);
        if(!flag2)
            k2=(y3-y4)/(x3-x4);

        if(k1==k2)
            return null;

        if(flag1){
            if(flag2)
                return null;
            x=x1;
            if(k2==0){
                y=y3;
            }else{
                y=k2*(x-x4)+y4;
            }
        }else if(flag2){
            x=x3;
            if(k1==0){
                y=y1;
            }else{
                y=k1*(x-x2)+y2;
            }
        }else{
            if(k1==0){
                y=y1;
                x=(y-y4)/k2+x4;
            }else if(k2==0){
                y=y3;
                x=(y-y2)/k1+x2;
            }else{
                x=(k1*x2-k2*x4+y4-y2)/(k1-k2);//
                y=k1*(x-x2)+y2;
            }
        }
        if(between(x1,x2,x)&&between(y1,y2,y)&&between(x3,x4,x)&&between(y3,y4,y)){
            Point2D.Double point=new Point2D.Double();
            point.setLocation(x,y);
            if(point.equals(lsegA.getP1())||point.equals(lsegA.getP2()))
                return null;
            return point;
        }else{
            return null;
        }
    }

    public static boolean between(double a,double b,double target){
        if(target>=a-0.00001&&target<=b+0.00001||target<=a+0.00001&&target>=b-0.00001)
            return true;
        else
            return false;
    }
}


