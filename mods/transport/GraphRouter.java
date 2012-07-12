package xabrain.mods.transport;

import java.util.LinkedList;

public class GraphRouter extends GraphPoint {
	public GraphNode node;
	public LinkedList<GraphPoint> links = new LinkedList<GraphPoint>();
	private Graph graph;

	public GraphRouter(Graph graph, GraphNode node) {
		super();
		this.graph = graph;

		this.node = node;

		graph.tiles.put(node, this);
		if (Graph.DEBUG) graph.routers.add(this);
	}

	public void destroy() {
		/* Remove all links to us */
		for (GraphPoint p : links) {
			if (p instanceof GraphRouter) {
				((GraphRouter) p).links.remove(this);
				((GraphRouter) p).optimize();
				continue;
			}

			System.out.println("Graph corruption: dangling link in router");
		}

		links.clear();
		node = null;

		if (Graph.DEBUG) graph.routers.remove(this);
	}

	public boolean isRouter() {
		return true;
	}

	public void linkTo(GraphNode nodeNew, GraphRouter router) {
		/*
		 * If we have no links of more than one, the new router will remain a
		 * router, so just link it in.
		 */
		if (links.size() != 1) {
			links.add(router);
			router.links.add(this);
			return;
		}

		GraphPoint link = links.get(0);

		if (link instanceof GraphRouter) {
			/*
			 * We have one link to a router, so create a leg between the link
			 * and the new node
			 */
			new GraphLeg(graph, (GraphRouter) link, router, this.node);
		} else {
			/* We have one link to a leg, so increase the size of that leg */
			((GraphLeg) link).addNode(this, this.node);

			/* Link the leg */
			((GraphLeg) link).replaceRouter(this, router);
		}

		/* We are no longer required */
		destroy();
	}

	public void optimize() {
		/*
		 * Only points with 2 neighboughs can be optimized (but, they can in all
		 * cases)
		 */
		if (links.size() != 2) return;

		GraphPoint left = links.get(0);
		GraphPoint right = links.get(1);

		/* If we have 2 legs, we can make 1 large leg */
		if (left instanceof GraphLeg && right instanceof GraphLeg) {
			((GraphLeg) left).connect(this, (GraphLeg) right);
			return;
		}

		/* If we have 2 routers, we can become a leg */
		if (left instanceof GraphRouter && right instanceof GraphRouter) {
			new GraphLeg(graph, (GraphRouter) left, (GraphRouter) right, node);

			destroy();
			return;
		}

		/* If right is a router, flip the variables */
		if (right instanceof GraphRouter) {
			GraphPoint t = left;
			left = right;
			right = t;
		}

		/* Left is now always a router, and right a leg; so extend the leg */
		((GraphLeg) right).addNode(this, node);

		/* Replace our router with the neighbour */
		((GraphLeg) right).replaceRouter(this, (GraphRouter) left);

		/* And we are no longer needed */
		destroy();
	}

}
