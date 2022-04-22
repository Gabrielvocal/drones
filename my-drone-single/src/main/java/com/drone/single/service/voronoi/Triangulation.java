package com.drone.single.service.voronoi;

import java.util.*;

public class Triangulation<V> implements Iterable<Triangle<V>> {
    
    private HashMap<Triangle<V>,HashSet<Triangle<V>>> neighbors; //保存相邻的三角形
    
   
    public Triangulation (Triangle<V> triangle) {
        neighbors = new HashMap<Triangle<V>, HashSet<Triangle<V>>>();
        neighbors.put(triangle, new HashSet<Triangle<V>>());
    }
    
    
    public int size () {//返回该三角集的数量
        return neighbors.size();
    }
    
  
    public boolean contains (Triangle<V> triangle) {//是否包含该三角
        return this.neighbors.containsKey(triangle);
    }
    
    public Iterator<Triangle<V>> iterator () {//实现迭代器迭代每个三角
        return Collections.unmodifiableSet(this.neighbors.keySet()).iterator();
    }
    
  
   
    public Triangle<V> neighborFace (Object vertex, Triangle<V> triangle) {//求出每个三角顶点周围相对的三角
        if (!triangle.contains(vertex))
            throw new IllegalArgumentException("wrong vertex, not in triangle");
        TriangleLoop: for (Triangle<V> t: neighbors.get(triangle)) {
            for (V v: triangle) {
                if (v.equals(vertex)) continue;
                if (!t.contains(v)) continue TriangleLoop;
            }
            return t;
        }
        return null;
    }
    
  
    public Set<Triangle<V>> getNeighbors (Triangle<V> triangle) {//给定三角形的相邻三角形
        return new HashSet<Triangle<V>>(this.neighbors.get(triangle));
    }
  
    public void update (Set<? extends Triangle<V>> oldTri, 
                        Set<? extends Triangle<V>> newTri) {
        Set<Triangle<V>> allNeighbors = new HashSet<Triangle<V>>();
        for (Triangle<V> triangle: oldTri)
            allNeighbors.addAll(neighbors.get(triangle));
        for (Triangle<V> triangle: oldTri) {
            for (Triangle<V> n: neighbors.get(triangle))
                neighbors.get(n).remove(triangle);
            neighbors.remove(triangle);
            allNeighbors.remove(triangle);
        }
        allNeighbors.addAll(newTri);
        for (Triangle<V> t: newTri)
            neighbors.put(t, new HashSet<Triangle<V>>());
        for (Triangle<V> t1: newTri)
        for (Triangle<V> t2: allNeighbors) {
            if (!t1.isAdjacentToTriangle(t2)) continue;
            neighbors.get(t1).add(t2);
            neighbors.get(t2).add(t1);
        }
    }
}
