package io.github.jersmith.MazeGenerator;

import java.util.LinkedList;    
import java.util.ListIterator;    
import java.util.HashMap;    
    
class Point {    
    public int x;    
    public int y;    
    
    public Point(int x, int y) {    
        this.x = x;    
        this.y = y;    
    }    
}    
    
class Edge {    
    public int v1;    
    public int v2;    
    
    public Edge(int v1, int v2) {    
        this.v1 = v1;    
        this.v2 = v2;    
    }    
    
    public int other(int v) {    
        if (v == this.v1) return this.v2;    
    
        return this.v1;    
    }    
    
    public int either() {    
        return this.v1;    
    }    
}    

class GridGraph {
    public int LENGTH;
    public int vertexCount;

    public GridGraph(int length) {
        this.LENGTH = (2 * length) - 1;
        this.vertexCount = this.LENGTH * this.LENGTH;
    }

    public int vertexForPoint(Point p) {
        if (p.x >= 0 && p.x < this.LENGTH && p.y >= 0 && p.y < this.LENGTH) {
            return (p.y * this.LENGTH) + p.x;
        }

        return -1;
    }

    public Point pointForVertex(int v) {
        if (v >= 0 && v < this.vertexCount) {
            int y = (int) Math.floor(v / this.LENGTH);
            int x = v % this.LENGTH;

            return new Point(x, y);
        }

        return new Point(-1, -1);
    }

    public LinkedList<Edge> adjacentEdges(int v) {
        LinkedList<Edge> adj = new LinkedList<Edge>();
        Point p = this.pointForVertex(v);

        if (p.x > 1) adj.push(new Edge(v, this.vertexForPoint(new Point(p.x - 2, p.y))));
        if (p.x < this.LENGTH - 2) adj.push(new Edge(v, this.vertexForPoint(new Point(p.x + 2, p.y))));
        if (p.y > 1) adj.push(new Edge(v, this.vertexForPoint(new Point(p.x, p.y - 2))));
        if (p.y < this.LENGTH - 2) adj.push(new Edge(v, this.vertexForPoint(new Point(p.x, p.y + 2))));

        return adj;
    }

    public Point between(Point p1, Point p2) {
        if (p1.x == p2.x) {
            return new Point(p1.x, Math.max(p1.y, p2.y) - 1);
        } else if (p1.y == p2.y) {
            return new Point(Math.max(p1.x, p2.x) - 1, p1.y);
        }

        return new Point(-1, -1);
    }
}

public class PrimsMaze {
    GridGraph G;
    LinkedList<Edge> collectedEdges;
    LinkedList<Edge> pendingEdges;
    HashMap<Integer,Boolean> markedVertices;

    public PrimsMaze(int length) {
        this.G = new GridGraph(length);
        this.collectedEdges = new LinkedList<Edge>();
        this.pendingEdges = new LinkedList<Edge>();
        this.markedVertices = new HashMap<Integer,Boolean>();

        // The maze generates better if you pick a vertex in the middle. The 24th vertex
        // requires a maze at least 5 x 5
        this.visit(24);
        while(this.pendingEdges.size() > 0) {
            Edge e = this.choose(this.pendingEdges);
            int v = e.either();
            int w = e.other(v);

            if (this.markedVertices.containsKey(v) && this.markedVertices.containsKey(w)) continue;

            this.collectedEdges.push(e);

            if (!this.markedVertices.containsKey(v)) this.visit(v);
            if (!this.markedVertices.containsKey(w)) this.visit(w);
        }
    }

    Edge choose(LinkedList<Edge> list) {
        int choiceIndex = (int) Math.floor(Math.random() * list.size());
        return list.remove(choiceIndex);
    }

    void visit(int vertex) {
        this.markedVertices.put(vertex, true);

        ListIterator<Edge> iterator = this.G.adjacentEdges(vertex).listIterator(0);
        while(iterator.hasNext()) {
            Edge e = iterator.next();

            if (!this.markedVertices.containsKey(e.other(vertex))) this.pendingEdges.push(e);
        }
    }

    public boolean[][] grid() {
        boolean[][] mazeGrid = new boolean[this.G.LENGTH][this.G.LENGTH];

        ListIterator<Edge> iterator = this.collectedEdges.listIterator(0);
        while(iterator.hasNext()) {
            Edge e = iterator.next();

            Point v = this.G.pointForVertex(e.v1);
            Point w = this.G.pointForVertex(e.v2);
            Point b = this.G.between(v, w);

            mazeGrid[v.y][v.x] = true;
            mazeGrid[w.y][w.x] = true;
            mazeGrid[b.y][b.x] = true;
        }

        return mazeGrid;
    }
}

