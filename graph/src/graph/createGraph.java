package graph;
import java.util.*;
public class createGraph {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		graph<Integer> Graph=new graph<>(false);
		System.out.println("In main");
		Graph.addEdge(0,1);
		Graph.addEdge(1, 2);
		Graph.addEdge(2, 3);
		Graph.addEdge(3, 4);
		Graph.addEdge(4, 5);
		Graph.addEdge(5, 1);
		
		List<Edge<Integer>> alist=Graph.getAllEdge();
		Map<Long,Vertex<Integer>> map=Graph.getAllVertex();
		System.out.println(map.size());
	    for(long i : map.keySet()){
	    	System.out.print(i+" ");
	    }
	    System.out.println();
		System.out.println(alist.size());
		for(Edge<Integer> e : alist){
			System.out.println(e.vertex1.getId()+" "+ e.vertex2.getId());
		}
		

	}

}
