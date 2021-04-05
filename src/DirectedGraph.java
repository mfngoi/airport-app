import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;

public class DirectedGraph<T> implements GraphInterface<T> {
	
	private Map<T, VertexInterface<T>> vertices;
	private int edgeCount;
	
	public DirectedGraph() {
		vertices = new HashMap<>();
		edgeCount = 0;
	}
	
	public boolean addVertex(T vertexLabel) {
		VertexInterface<T> addOutcome = vertices.put(vertexLabel, new Vertex<>(vertexLabel));
		return addOutcome == null;
	}
	
	public boolean addEdge(T begin, T end, double edgeWeight) {
		boolean result = false;
		VertexInterface<T> beginVertex = vertices.get(begin);
		VertexInterface<T> endVertex = vertices.get(end);
		if((beginVertex != null) && (endVertex != null))
			result = beginVertex.connect(endVertex, edgeWeight);
		if(result)
			edgeCount++;
		return result;
	}
	
	public boolean addEdge(T begin, T end) {
		return addEdge(begin, end, 0);
	}
	
	public boolean removeEdge(T begin, T end) {
		boolean result = false;
		VertexInterface<T> beginVertex = vertices.get(begin);
		VertexInterface<T> endVertex = vertices.get(end);
		if((beginVertex != null) && (endVertex != null))
			result = beginVertex.remove(endVertex);
		if(result)
			edgeCount--;
		return result;
	}
	
	public boolean hasEdge(T begin, T end) {
		boolean found = false;
		VertexInterface<T> beginVertex = vertices.get(begin);
		VertexInterface<T> endVertex = vertices.get(end);
		if((beginVertex != null) && (endVertex != null)) {
			Iterator<VertexInterface<T>> neighbors = beginVertex.getNeighborIterator();
			while(!found && neighbors.hasNext()) {
				VertexInterface<T> nextNeighbor = neighbors.next();
				if(endVertex.equals(nextNeighbor))
					found = true;
			}
		}
		return found;
	}
	
	public boolean isEmpty() {
		return vertices.isEmpty();
	}
	
	public int getNumberOfVertices() {
		return vertices.size();
	}
	
	public int getNumberOfEdges() {
		return edgeCount;
	}
	
	public void clear() {
		vertices.clear();
		edgeCount = 0;
	}
	
	protected void resetVertices() {
		Iterator<Map.Entry<T, VertexInterface<T>>> verticesIterator = vertices.entrySet().iterator();
		while(verticesIterator.hasNext()) {
			Map.Entry<T, VertexInterface<T>> entry = (Map.Entry<T, VertexInterface<T>>)verticesIterator.next();
			VertexInterface<T> nextVertex = (VertexInterface<T>)entry.getValue();
			nextVertex.unvisit();
			nextVertex.setCost(0);
			nextVertex.setPredecessor(null);
		}
	}
	
	public double getCheapestPath(T begin, T end, StackInterface<T> path) {
		resetVertices();
		boolean done = false;
		PriorityQueue<Entry> vertexQueue = new PriorityQueue<>();
		VertexInterface<T> originVertex = vertices.get(begin);
		VertexInterface<T> endVertex = vertices.get(end);
		vertexQueue.add(new Entry(originVertex,0,null));
		while(!done && !vertexQueue.isEmpty()) {
			Entry frontEntry = vertexQueue.remove();
			VertexInterface<T> frontVertex = frontEntry.getVertex();
			if(!frontVertex.isVisited()) {
				frontVertex.visit();
				frontVertex.setCost(frontEntry.getCost());
				frontVertex.setPredecessor(frontEntry.getPredecessor());
				if(frontVertex.equals(endVertex)) {
					done = true;
					endVertex.setCost(frontVertex.getCost());
					endVertex.setPredecessor(frontVertex.getPredecessor());
				}
				else {
					Iterator<VertexInterface<T>> neighborIterator = frontVertex.getNeighborIterator();
					while(neighborIterator.hasNext()) {
						VertexInterface<T> nextNeighbor = neighborIterator.next();
						double weightOfEdgeToNeighbor = frontVertex.getEdgeWeight(nextNeighbor);
						if(!nextNeighbor.isVisited()) {
							double nextCost = weightOfEdgeToNeighbor + frontVertex.getCost();
							vertexQueue.add(new Entry(nextNeighbor, nextCost, frontVertex));
						}
					}
				}
			}
		}
		double pathCost = endVertex.getCost();
		path.push(endVertex.getLabel());
		VertexInterface<T> vertex = endVertex;
		while(vertex.hasPredecessor()) {
			vertex = vertex.getPredecessor();
			path.push(vertex.getLabel());
		}
		return pathCost;
	}
	
	private class Entry implements Comparable<Entry> {
		private VertexInterface<T> vertex;
		private double cost;
		private VertexInterface<T> predecessor;
		
		private Vertex<T> getVertex() {
			return (Vertex<T>)vertex;
		}
		
		private double getCost() {
			return cost;
		}
		
		private Vertex<T> getPredecessor() {
			return (Vertex<T>)predecessor;
		}
		
		Entry(VertexInterface<T> vertex, double cost, VertexInterface<T> predecessor) {
			this.vertex = vertex;
			this.cost = cost;
			this.predecessor = predecessor;
		}
		public int compareTo(Entry other) {
			return (int)(cost-other.cost);
		}
	}
	
}
