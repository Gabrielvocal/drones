package com.drone.single.pojo;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@Data
public class Polygon {
    private double[][] endpoints;
    private double[] weight;
    private List<double[]> lines;

}
