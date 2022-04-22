package com.drone.single.service.voronoi;

import java.util.*;
import java.util.function.Consumer;

public class Delaunay extends Triangulation<Vpoint> {
    
    private Triangle<Vpoint> triangleNewInsert = null;  
    public Delaunay (Triangle<Vpoint> triangle) {
        super(triangle);
        triangleNewInsert = triangle;
    }
    
    
    public Triangle<Vpoint> FindTriangle (Vpoint point) {//找出三角区域中包含该点的三角形
        Triangle<Vpoint> triangle = triangleNewInsert;
        if (!this.contains(triangle)) triangle = null;
        Set<Triangle<Vpoint>> visited = new HashSet<Triangle<Vpoint>>();
        while (triangle != null) {
            visited.add(triangle);
            Vpoint corner = point.beOutTri(triangle.toArray(new Vpoint[0]));
            if (corner == null) return triangle;
            triangle = this.neighborFace(corner, triangle);
        }
        for (Triangle<Vpoint> tri: this) {
            if (point.beOutTri(tri.toArray(new Vpoint[0])) == null) return tri;
        }
        return null;
    }
    
   
    public Set<Triangle<Vpoint>> PointInsert (Vpoint point) {//插入新加入的点
        Set<Triangle<Vpoint>> newTriangles = new HashSet<Triangle<Vpoint>>();
        Set<Triangle<Vpoint>> oldTriangles = new HashSet<Triangle<Vpoint>>();
        Set<Triangle<Vpoint>> hasSet = new HashSet<Triangle<Vpoint>>();
        Queue<Triangle<Vpoint>> queue = new LinkedList<Triangle<Vpoint>>();
        Triangle<Vpoint> triangle = FindTriangle(point);
        if (triangle == null || triangle.contains(point)) return newTriangles;
        queue.add(triangle);
        while (!queue.isEmpty()) {
            triangle = queue.remove();
            if (point.PointCircleOfTri(triangle.toArray(new Vpoint[0])) == 1) continue;
            oldTriangles.add(triangle);
            for (Triangle<Vpoint> tri: this.getNeighbors(triangle)) {
                if (hasSet.contains(tri)) continue;
                hasSet.add(tri);
                queue.add(tri);
            }
        }
        for (Set<Vpoint> facet: Triangle.boundary(oldTriangles)) {
            facet.add(point);
            newTriangles.add(new Triangle<Vpoint>(facet));
        }
        this.update(oldTriangles, newTriangles);
        if (!newTriangles.isEmpty()) triangleNewInsert = newTriangles.iterator().next();
        return newTriangles;
    }

    @Override
    public int size() {
        return super.size();
    }

    @Override
    public boolean contains(Triangle<Vpoint> triangle) {
        return super.contains(triangle);
    }

    @Override
    public Iterator<Triangle<Vpoint>> iterator() {
        return super.iterator();
    }

    @Override
    public Triangle<Vpoint> neighborFace(Object vertex, Triangle<Vpoint> triangle) {
        return super.neighborFace(vertex, triangle);
    }

    @Override
    public Set<Triangle<Vpoint>> getNeighbors(Triangle<Vpoint> triangle) {
        return super.getNeighbors(triangle);
    }

    @Override
    public void update(Set<? extends Triangle<Vpoint>> oldTri, Set<? extends Triangle<Vpoint>> newTri) {
        super.update(oldTri, newTri);
    }

    @Override
    public void forEach(Consumer<? super Triangle<Vpoint>> action) {
        super.forEach(action);
    }

    @Override
    public Spliterator<Triangle<Vpoint>> spliterator() {
        return super.spliterator();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}