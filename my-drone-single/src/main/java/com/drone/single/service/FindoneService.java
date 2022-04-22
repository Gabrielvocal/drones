package com.drone.single.service;

import com.drone.single.pojo.Polygon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
@Service
public class FindoneService {
   // @Autowired
    //private Polygon polygon;

    /**
     * 获取不规则多边形重心点
     *
     * @param endpoints
     * @return
     */
    public double[] getCenterOfGravityPoint(double[][] endpoints) {
        double area = 0.0;//多边形面积
        double Gx = 0.0, Gy = 0.0;// 重心的x、y
        for (int i = 1; i <= endpoints.length; i++) {
            double iLat = endpoints[i%endpoints.length][0];
            double iLng = endpoints[i%endpoints.length][1];;
            double nextLat = endpoints[i-1][0];
            double nextLng = endpoints[i-1][1];
            double temp = (iLat * nextLng - iLng * nextLat) / 2.0;
            area += temp;
            Gx += temp * (iLat + nextLat) / 3.0;
            Gy += temp * (iLng + nextLng) / 3.0;
        }
        Gx = Gx / area;
        Gy = Gy / area;
        //polygon.setWeight(new double[]{Gx,Gy});
        return new double[]{Gx,Gy};
    }

  public List<double[]>  getLine(double[][] endpoints,double[] weight){
      List<double[]> lines=new ArrayList<>();
      for (int i = 0; i < endpoints.length; i++) {
          double k=(endpoints[i][1]-weight[1])/(endpoints[i][0]-weight[0]);
          double b;
          if(Double.isInfinite(k)) {
              b=weight[0];
          }else {
              b = weight[1] - k * weight[0];
          }
          double angle=Math.atan(k);
          if(angle>0){
              angle=endpoints[i][1]>weight[1]?angle:Math.PI+angle;
          }else if(angle<0){
              angle=endpoints[i][1]>weight[1]?Math.PI+angle:angle+2*Math.PI;
          }else{
              angle=endpoints[i][0]>weight[0]?angle:Math.PI;
          }

          double[] line=new double[]{angle,endpoints[i][0],endpoints[i][1],k,b};
          lines.add(line);
      }
      lines.sort(new Comparator<double[]>() {//按照角度排序

          @Override
          public int compare(double[] o1, double[] o2) {
              double a=o1[0]-o2[0];
              if(a>0){
                  return 1 ;
              }
              else if(a<0){
                  return -1 ;
              } else{
                  return 0 ;
              }


          }
      });
      //polygon.setLines(lines);
        return lines;

  }
  public List<double[]> getCurves(String h,String si,double[] weight,List<double[]> glines){
        double height=Double.valueOf(h);
        double sight=Double.valueOf(si);
        double diagonal=2*Math.tan(sight/2.0)*height;
        double sidelen=diagonal*Math.pow(2,0.5);
        //double[] weight=polygon.getWeight();
        List<double[]> lines=new ArrayList<>(glines);
        lines.add(lines.get(0));
      List<double[]> zlines=new ArrayList<>();
      for (int i = 0; i < lines.size()-1; i++) {
          if(i==0){
              zlines.add(weight);
              double bottemk=(lines.get(i)[2]-lines.get(i+1)[2])/(lines.get(i)[1]-lines.get(i+1)[1]);
              double bottemb;
              double dis;
              if(Double.isInfinite(bottemk)) {
                  dis=Math.abs(weight[0]-lines.get(i)[1]);
                  //bottemb=lines.get(i)[1];
              }else {
                  bottemb = lines.get(i)[2] - bottemk * lines.get(i)[1];
                  dis=Math.abs(bottemk*weight[0]+bottemb-weight[1])/(Math.pow((bottemk*bottemk)+1,0.5));
              }
              double temp=dis/diagonal;
              int num=(int)Math.floor(temp);
              int times=1;
              List<double[]>l1=new ArrayList<>();
              List<double[]>l2=new ArrayList<>();
              double xdis1=lines.get(i)[1]-weight[0];
              double ydis1=lines.get(i)[2]-weight[1];
              double xdis2=lines.get(i+1)[1]-weight[0];
              double ydis2=lines.get(i+1)[2]-weight[1];
              while(num>0){
                  double x1,y1,x2,y2;

                  x1=weight[0]+xdis1*times/temp;
                  y1=weight[1]+ydis1*times/temp;
                  x2=weight[0]+xdis2*times/temp;
                  y2=weight[1]+ydis2*times/temp;
                 zlines.add(new double[]{x2,y2});
                 zlines.add(new double[]{x1,y1});
                 num--;
                 times++;

              }
              zlines.add(new double[]{lines.get(i+1)[1],lines.get(i+1)[2]});
          } else{
              double bottemk=(lines.get(i)[2]-weight[1])/(lines.get(i)[1]-weight[0]);
              double bottemb;
              double dis;
              if(Double.isInfinite(bottemk)) {
                  dis=Math.abs(lines.get(i+1)[1]-lines.get(i)[1]);
                  //bottemb=lines.get(i)[1];
              }else {
                  bottemb = lines.get(i)[2] - bottemk * lines.get(i)[1];
                  dis=Math.abs(bottemk*lines.get(i+1)[1]+bottemb-lines.get(i+1)[2])/(Math.pow((bottemk*bottemk)+1,0.5));
              }
              double temp=dis/diagonal;
              int num=(int)Math.floor(temp);
              int times=1;
              double xdis1=lines.get(i+1)[1]-lines.get(i)[1];
              double ydis1=lines.get(i+1)[2]-lines.get(i)[2];
              double xdis2=lines.get(i+1)[1]-weight[0];
              double ydis2=lines.get(i+1)[2]-weight[1];
              while(num>0){
                  double x1,y1,x2,y2;

                  x2=weight[0]+xdis2*times/temp;
                  y2=weight[1]+ydis2*times/temp;
                  x1=lines.get(i)[1]+xdis1*times/temp;
                  y1=lines.get(i)[2]+ydis1*times/temp;
                  zlines.add(new double[]{x2,y2});
                  zlines.add(new double[]{x1,y1});
                  num--;
                  times++;

              }
              zlines.add(new double[]{lines.get(i+1)[1],lines.get(i+1)[2]});
          }
      }
  return zlines;
  }


}
