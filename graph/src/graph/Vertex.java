package graph;

import java.util.*;

public class Vertex<T> {

	long id;
	T data;
	List<Vertex<T>> adjacentVertex=new ArrayList<Vertex<T>>();
	List<Edge<T>> edges=new ArrayList<Edge<T>>();
	
	Vertex(long id){
		this.id=id;
	}
	
	public long getId(){
		return id;
	}
	
	public void setData(T data){
		this.data=data;
	}
	
	public T getData(){
		return this.data;
	}
	
	public void addAdjacentVertex(Edge<T> e, Vertex<T> v){
		edges.add(e);
		adjacentVertex.add(v);
	}
	
	public List<Vertex<T>> getVertex(){
		return this.adjacentVertex;
	}
	
	public List<Edge<T>> getEdge(){
		return this.edges;
	}
	
}
