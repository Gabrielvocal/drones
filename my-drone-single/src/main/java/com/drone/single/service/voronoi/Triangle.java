package com.drone.single.service.voronoi;

import java.util.*;

class Triangle<V> extends AbstractSet<V> implements Set<V> {
    
    private List<V> vertices;
    
    public Triangle (Collection<? extends V> collection) {
        this.vertices = Collections.unmodifiableList(new ArrayList<V>(collection));
    }
    
    public Triangle (V... ver) {
        this(Arrays.asList(ver));
    }
    
    
   
    public boolean isAdjacentToTriangle (Triangle<V> triangle) {//判断是否是相邻
        HashSet<V> h = new HashSet<V>(this);
        h.removeAll(triangle);
        return (this.size() == triangle.size()) && (h.size() == 1);
    }
    
    
    public List<Set<V>> facetsOfTriangle () {//记录边
        List<Set<V>> theFacets = new LinkedList<Set<V>>();
        for (V v: this) {
            Set<V> facet = new HashSet<V>(this);
            facet.remove(v);
            theFacets.add(facet);
        }
        return theFacets;
    }
    
    
    public static <V> Set<Set<V>> boundary (Set<? extends Triangle<V>> simplexSet) {
        Set<Set<V>> boundary = new HashSet<Set<V>>();
        for (Triangle<V> simplex: simplexSet) {
            for (Set<V> facet: simplex.facetsOfTriangle()) {
                if (boundary.contains(facet)) boundary.remove(facet);
                else boundary.add(facet);
            }
        }
        return boundary;
    }
    
   
    public Iterator<V> iterator () {
        return this.vertices.iterator();
    }
   
    public int size () {
        return this.vertices.size();
    }
    
   
    public boolean equals (Object o) {
        return (this == o);
    }
}