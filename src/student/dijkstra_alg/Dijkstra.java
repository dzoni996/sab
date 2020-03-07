package student.dijkstra_alg;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

public class Dijkstra {

	public Dijkstra() {
		// TODO Auto-generated constructor stub
	}
	
	private static Node getLowestDistanceNode(Set < Node > unsettledNodes) {
	    Node lowestDistanceNode = null;
	    int lowestDistance = Integer.MAX_VALUE;
	    for (Node node: unsettledNodes) {
	        int nodeDistance = node.getDistance();
	        if (nodeDistance < lowestDistance) {
	            lowestDistance = nodeDistance;
	            lowestDistanceNode = node;
	        }
	    }
	    return lowestDistanceNode;
	}
	
	private static void CalculateMinimumDistance(Node evaluationNode,
			  Integer edgeWeigh, Node sourceNode) {
			    Integer sourceDistance = sourceNode.getDistance();
			    if (sourceDistance + edgeWeigh < evaluationNode.getDistance()) {
			        evaluationNode.setDistance(sourceDistance + edgeWeigh);
			        LinkedList<Node> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
			        shortestPath.add(sourceNode);
			        evaluationNode.setShortestPath(shortestPath);
			    }
			}
	
	public static Graph calculateShortestPathFromSource(Graph graph, Node source) {
	    source.setDistance(0);
	 
	    Set<Node> settledNodes = new HashSet<>();
	    Set<Node> unsettledNodes = new HashSet<>();
	 
	    unsettledNodes.add(source);
	 
	    while (unsettledNodes.size() != 0) {
	        Node currentNode = getLowestDistanceNode(unsettledNodes);
	        unsettledNodes.remove(currentNode);
	        for (Entry< Node, Integer> adjacencyPair: 
	          currentNode.getAdjacentNodes().entrySet()) {
	            Node adjacentNode = adjacencyPair.getKey();
	            Integer edgeWeight = adjacencyPair.getValue();
	            if (!settledNodes.contains(adjacentNode)) {
	            	CalculateMinimumDistance(adjacentNode, edgeWeight, currentNode);
	                unsettledNodes.add(adjacentNode);
	            }
	        }
	        settledNodes.add(currentNode);
	    }
	    return graph;
	}

	/* For each city make 		Node nodeA = new Node("A");
   then connect nodes via 	nodeA.addDestination(nodeB, 10);
   and put nodes in List<Node>

   RETURN: graph with shortest paths from Node source to all others
   (Node n = graph.getNodes() -> n.getName(), n.getDistance)
*/
	public static Graph getShortestPath(List<Node> conneted_cities, Node source) {

		Graph graph = new Graph();
		for (Node n:conneted_cities)
			graph.addNode(n);

		graph = Dijkstra.calculateShortestPathFromSource(graph, source);

		return graph;
	}

	/*
	First call method getShortestPath, then use THAT graph!
	 */
	public static int getShortestPathDistance(Graph graph, String destCity){
		for (Node n: graph.getNodes())
			if (n.getName().equals(destCity))
				return n.getDistance();
		return -1;
	}

	public static Node getNodeByName(List<Node> conneted_cities, String city){
		for (Node n:conneted_cities)
			if (n.getName().equals(city))
				return n;
		return null;
	}

	public static Node getNodeByName(Graph graph, String city){
		for (Node n:graph.getNodes())
			if (n.getName().equals(city))
				return n;
		return null;
	}
}
