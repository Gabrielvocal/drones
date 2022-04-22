package com.drone.single.controller;

import com.alibaba.fastjson.JSONArray;
import com.drone.single.service.FindoneService;

import com.drone.single.service.draw.DrawRoutines;
import com.drone.single.service.voronoi.VoronoiService;
import com.drone.single.vo.ErrorResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("find")
public class FillController {
    @Autowired
    private FindoneService findoneService;
    @Autowired
    private VoronoiService voronoiService;
    @Autowired
    private DrawRoutines drawRoutines;

    @PostMapping("one")
    public ResponseEntity<Object> findone(@RequestBody Map<String,String> param){
        String s=param.get("points");
        String points[]=s.split(";");
        try{
            double[][] endpoints=new double[points.length][2];
            for (int i = 0; i < endpoints.length; i++) {
                String[] p=points[i].split(",");
                endpoints[i][0]=Double.valueOf(p[0]);
                endpoints[i][1]=Double.valueOf(p[1]);
            }
            String h=param.get("height");
            String si=param.get("sight");
            double[] weight = findoneService.getCenterOfGravityPoint(endpoints);
            List<double[]> line = findoneService.getLine(endpoints, weight);
            List<double[]> zcurves = findoneService.getCurves(h, si,weight,line);
            return ResponseEntity.ok(zcurves);
        }catch (Exception e) {
            e.printStackTrace();
        }
        ErrorResult errorResult = ErrorResult.builder().errCode("000002").errMessage("失败！").build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
    }
    @PostMapping("multiple")
    public ResponseEntity<Object> findMultiple(@RequestBody Map<String,String> param){
        String s=param.get("points");
        String points[]=s.split(";");
        String d=param.get("drones");
        String drones[]=d.split(";");
        List<List<double[]>> zc=new ArrayList<>();
        try{
            //double[][] endpoints=new double[points.length][2];
            List<Point2D.Double> endpoints=new ArrayList<>();
            for (int i = 0; i < points.length; i++) {
                String[] p=points[i].split(",");
                endpoints.add(new Point2D.Double(Double.valueOf(p[0]),Double.valueOf(p[1])));
            }
            String h=param.get("height");
            String si=param.get("sight");
            /*for (String drone : drones) {
                voronoiService.pointInsert(drone);
            }*/
            int time=0;
            ArrayList<Point2D.Double> e1 = new ArrayList<>(endpoints);
            Map<Point2D.Double, HashSet<Point2D.Double>> pointsmap = voronoiService.pointInsert(drones,endpoints,time);
            if(pointsmap==null){
                ErrorResult errorResult = ErrorResult.builder().errCode("000003").errMessage("点不在多边形内").build();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
            }
            for (Point2D.Double aDouble : pointsmap.keySet()) {
                if (pointsmap.get(aDouble).size()<3) time++;
                if(time!=0) break;
            }
            if(time!=0){
                pointsmap=voronoiService.pointInsert(drones,e1,time);
            }
            //pmap=voronoiService.paintVoronoi(endpoints,pmap);
           // Map<Point2D.Double, HashSet<Point2D.Double>> pointsmap = voronoiService.endPointVoronoi(endpoints,pmap);
            //double[] weight = findoneService.getCenterOfGravityPoint(endpoints);
            //findoneService.getLine(endpoints,weight);
            //List<double[]> zcurves = findoneService.getCurves(h, si);
            for (Point2D.Double aDouble : pointsmap.keySet()) {
                HashSet<Point2D.Double> votopoints= pointsmap.get(aDouble);
                List<List<Double>> endpoints1=new ArrayList<>(1);
                //double[][] endpoints1=new double[points.length][2];
                //int index=0;
                for (Point2D.Double votopoint : votopoints) {
                    List<Double> p=new ArrayList<>();
                    p.add((votopoint.getX()));
                    p.add((votopoint.getY()));
                    endpoints1.add(p);
                    //endpoints1.get(index).add(votopoint.getX());
                    //endpoints1.get(index).add(votopoint.getY());
                   // index++;
                }
                double[][] endpoints2=new double[endpoints1.size()][2];
                for (int i = 0; i <endpoints1.size() ; i++) {
                    endpoints2[i][0]=endpoints1.get(i).get(0);
                    endpoints2[i][1]=endpoints1.get(i).get(1);
                }
                List<double[]> ls1 = findoneService.getLine(endpoints2, new double[]{aDouble.getX(), aDouble.getY()});
                for (int i = 0; i < endpoints1.size(); i++) {
                    endpoints2[i][0]=ls1.get(i)[1];
                    endpoints2[i][1]=ls1.get(i)[2];
                }
                double[] weight = findoneService.getCenterOfGravityPoint(endpoints2);
                List<double[]> lines = findoneService.getLine(endpoints2, weight);
                List<double[]> zcurves = findoneService.getCurves(h, si, weight, lines);
                zc.add(zcurves);


                /*for (int i = 0; i < endpoints1.length; i++) {
                    Point2D.Double point=votopoints;
                    endpoints1[i][0]=point.getX();
                    endpoints1[i][1]=point.getY();
                    double[] weight = findoneService.getCenterOfGravityPoint(endpoints1);
                    findoneService.getLine(endpoints1,weight);
                    List<double[]> zcurves = findoneService.getCurves(h, si);
                    zc.add(zcurves);
                }*/
            }
            //voronoiService.map.clear();

            return ResponseEntity.ok(zc);
        }catch (Exception e) {
            e.printStackTrace();
        }
        ErrorResult errorResult = ErrorResult.builder().errCode("000002").errMessage("失败！").build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
    }

    @PostMapping("draw")
    public ResponseEntity<Object> drawRoutine(@RequestBody List<double[][]> list){
        if(list.size()==0){
            ErrorResult errorResult = ErrorResult.builder().errCode("000003").errMessage("无参数").build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResult);
        }
        try {
            drawRoutines.getData(list);
            return ResponseEntity.ok(null);
        }catch (Exception e){
            e.printStackTrace();
        }
        ErrorResult errorResult = ErrorResult.builder().errCode("000002").errMessage("失败！").build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);

    }



    }

