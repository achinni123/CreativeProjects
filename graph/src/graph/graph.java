package graph;
import java.util.*;
public class graph<T> {
	List<Edge<T>> allEdges;
	Map<Long,Vertex<T>> allVertex;
	boolean isDirected=false;	
	
	graph(boolean isDirected){
		allEdges=new ArrayList<>();
		allVertex=new HashMap<>();
		this.isDirected=isDirected;
	}
	
	public void addEdge(long id1,long id2){
		addEdge(id1,id2,0);
	}
	public void addEdge(long id1,long id2,int weight){
		Vertex<T> vertex1=null;
		if(allVertex.containsKey(id1)){
			vertex1=allVertex.get(id1);
		}else{
			vertex1=new Vertex<T>(id1);
			allVertex.put(id1, vertex1);
		}
		Vertex<T> vertex2=null;
		if(allVertex.containsKey(id2)){
			vertex2=allVertex.get(id2);
		}else{
			vertex2=new Vertex<T>(id2);
			allVertex.put(id2, vertex2);
		}
		
		Edge<T> e=new Edge<T>( vertex1, vertex2, weight,  isDirected);
		allEdges.add(e);
		vertex1.addAdjacentVertex(e,vertex2);
		if(!isDirected){
			vertex2.addAdjacentVertex(e, vertex1);
		}
	}
	
	public Vertex<T> getVertex(long id){
		return allVertex.get(id);
	}
	
	public List<Edge<T>> getAllEdge(){
		return allEdges;
	}
	
	public Map<Long, Vertex<T>> getAllVertex(){
		return allVertex;
	}
	

}
