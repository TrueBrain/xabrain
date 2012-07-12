package xabrain.mods.transport;

import java.util.LinkedList;

public class GraphLeg extends GraphPoint {
	public LinkedList<GraphNode> route = new LinkedList<GraphNode>();
	public GraphRouter head, tail;
	private Graph graph;

	public GraphLeg(Graph graph) {
		super();
		this.graph = graph;

		if (Graph.DEBUG) graph.legs.add(this);
	}

	public GraphLeg(Graph graph, GraphRouter head, GraphRouter tail, GraphNode node) {
		super();
		this.graph = graph;

		this.head = head;
		this.tail = tail;
		head.links.add(this);
		tail.links.add(this);

		route.add(node);

		graph.tiles.put(node, this);
		if (Graph.DEBUG) graph.legs.add(this);
	}

	public void destroy() {
		head.links.remove(this);
		tail.links.remove(this);

		route.clear();

		head = null;
		tail = null;

		if (Graph.DEBUG) graph.legs.remove(this);
	}

	public void addNode(GraphRouter side, GraphNode node) {
		graph.tiles.put(node, this);

		/* See if the side is our head */
		if (side == head) {
			route.addFirst(node);
			return;
		}

		/* Otherwise it is our tail */
		if (side == tail) {
			route.addLast(node);
			return;
		}

		System.out.println("Graph corruption: adding node to leg on unconnected router");
	}

	public void replaceRouter(GraphRouter routerOld, GraphRouter routerNew) {
		routerOld.links.remove(this);
		routerNew.links.add(this);

		if (head == routerOld) {
			head = routerNew;
			return;
		}

		if (tail == routerOld) {
			tail = routerNew;
			return;
		}

		System.out.println("Graph corruption: replacing router which is not connected");
	}

	public void connect(GraphRouter router, GraphLeg other) {
		/* Don't connect when we are a cycle */
		if (tail == head) return;

		/* Straight on connection */
		if (tail == router && other.head == router) {
			/* Add the router node to the list */
			route.addLast(router.node);
			graph.tiles.put(router.node, this);

			/* Add the other route to the list */
			for (GraphNode node : other.route) {
				route.addLast(node);
				graph.tiles.put(node, this);
			}

			/* Correct the links */
			tail = other.tail;
			tail.links.add(this);
			router.links.remove(this);

			other.destroy();
			router.destroy();
			return;
		}

		/* Objects are flipped */
		if (head == router && other.tail == router) {
			other.connect(router, this);
			return;
		}

		if (head != router && tail != router) {
			System.out.println("Graph corruption: connecting on leg which doesn't link to router");
			return;
		}

		/* One of the routes is reversed; so flip one */
		LinkedList<GraphNode> r = new LinkedList<GraphNode>();
		for (GraphNode n : route) {
			r.addFirst(n);
		}
		route = r;

		/* Flip the head and tail now */
		GraphRouter t = head;
		head = tail;
		tail = t;

		connect(router, other);
	}

	public GraphRouter split(GraphRouter routerSplit) {
		if (head == routerSplit) return split(route.getFirst());
		if (tail == routerSplit) return split(route.getLast());
		System.out.println("Graph corruption: splitting leg on invalid router");
		return null;
	}

	public GraphRouter split(GraphNode nodeSplit) {
		GraphRouter routerSplit = new GraphRouter(graph, nodeSplit);

		/* If we are only one tile big, remove us */
		if (route.size() == 1) {
			routerSplit.links.add(head);
			routerSplit.links.add(tail);
			head.links.add(routerSplit);
			tail.links.add(routerSplit);

			/* Remove the leg */
			destroy();

			return routerSplit;
		}

		if (nodeSplit.equals(route.getFirst())) {
			/* Check if we get a new leg at our head */
			route.removeFirst();

			routerSplit.links.add(this);
			routerSplit.links.add(head);
			head.links.remove(this);
			head.links.add(routerSplit);
			head = routerSplit;

			return routerSplit;
		}
		if (nodeSplit.equals(route.getLast())) {
			/* Check if we get a new leg at our tail */
			route.removeLast();

			routerSplit.links.add(this);
			routerSplit.links.add(tail);
			tail.links.remove(this);
			tail.links.add(routerSplit);
			tail = routerSplit;

			return routerSplit;
		}

		/* We have to split in 2 legs */

		GraphLeg legNew = new GraphLeg(graph);

		/* Move all the tiles to the new leg */
		for (GraphNode n : route) {
			if (n.equals(nodeSplit)) break;

			legNew.route.addLast(n);
			graph.tiles.put(n, legNew);
		}
		route.removeAll(legNew.route);

		/* Remove the node we split from */
		route.removeFirst();

		/* Link the new router to the new legs */
		routerSplit.links.add(this);
		routerSplit.links.add(legNew);

		/* And link the legs to the new router */
		head.links.remove(this);
		head.links.add(legNew);
		legNew.head = head;
		legNew.tail = routerSplit;
		head = routerSplit;

		return routerSplit;
	}
}
