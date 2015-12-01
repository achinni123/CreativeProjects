package graph;

public class Edge<T> {
	boolean isDirected=false;
	Vertex<T> vertex1;
	Vertex<T> vertex2;
	int weight;
	
	public Edge(Vertex<T> vertex1, Vertex<T> vertex2, int weight, boolean isDirected){
		this.vertex1=vertex1;
		this.vertex2=vertex2;
		this.weight=weight;
		this.isDirected=isDirected;
	}
	
	public Edge(Vertex<T> vertex1,Vertex<T> vertex2){
		this.vertex2=vertex2;
		this.vertex1=vertex1;
	}
	public Edge(Vertex<T> vertex1, Vertex<T> vertex2, boolean isDirected){
		this.vertex1=vertex1;
		this.vertex2=vertex2;
		this.isDirected=isDirected;
	}
	
	public Vertex<T> getVertex1(){
		return this.vertex1;
	}
	
	public Vertex<T> getVertex2(){
		return this.vertex2;
	}
	
	public int getWeight(){
		return this.weight;
		
	}
	
	public boolean isDirected(){
		return this.isDirected;
	}

}
