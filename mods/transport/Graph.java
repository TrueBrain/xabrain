package xabrain.mods.transport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import net.minecraft.src.World;

public class Graph {
	/** Set true to see the graph every build/remove */
	public static boolean DEBUG = false;
	public ArrayList<GraphRouter> routers;
	public ArrayList<GraphLeg> legs;

	private static HashMap<World, Graph> graphs = new HashMap<World, Graph>();
	private World world;

	public HashMap<GraphNode, GraphPoint> tiles = new HashMap<GraphNode, GraphPoint>();

	public static Graph getGraph(World world) {
		return graphs.get(world);
	}

	public Graph(World world) {
		super();
		this.world = world;
		graphs.put(world, this);

		if (DEBUG) {
			routers = new ArrayList<GraphRouter>();
			legs = new ArrayList<GraphLeg>();
		}
	}

	public GraphPoint getPoint(GraphNode node) {
		return tiles.get(node);
	}

	private void pipeAddNeighbour(int x, int y, int z, int side, ArrayList<GraphNode> nodeList, ArrayList<GraphPoint> pointList) {
		if (!mod_Transport.blockPipeSimple.canConnectPipeTo(world, x, y, z, side, null)) return;

		GraphNode node = new GraphNode(x, y, z);
		GraphPoint point = getPoint(node);
		if (point != null) {
			nodeList.add(node);
			pointList.add(point);
		}
	}

	public void printGraph() {
		if (!DEBUG) return;

		System.out.println("Routers:");
		for (GraphRouter r : routers) {
			System.out.println("  - " + r);
			for (GraphPoint p : r.links) {
				System.out.println("    * " + p);
			}
		}
		System.out.println("Legs:");
		for (GraphLeg l : legs) {
			System.out.println("  - " + l);
			System.out.println("    * " + l.head);
			System.out.println("    * " + l.tail);
			for (GraphNode n : l.route) {
				System.out.println("      * " + n);
			}
		}
		System.out.println("");
	}

	public void onPipeAdd(int x, int y, int z) {
		GraphNode nodeNew = new GraphNode(x, y, z);

		ArrayList<GraphNode> nodeList = new ArrayList<GraphNode>();
		ArrayList<GraphPoint> pointList = new ArrayList<GraphPoint>();

		/* Detect neighbour pipes */
		pipeAddNeighbour(x - 1, y, z, 5, nodeList, pointList);
		pipeAddNeighbour(x + 1, y, z, 4, nodeList, pointList);
		pipeAddNeighbour(x, y - 1, z, 1, nodeList, pointList);
		pipeAddNeighbour(x, y + 1, z, 0, nodeList, pointList);
		pipeAddNeighbour(x, y, z - 1, 3, nodeList, pointList);
		pipeAddNeighbour(x, y, z + 1, 2, nodeList, pointList);

		/* Assume we will be placing a router in the new position (most likely) */
		GraphRouter routerNew = new GraphRouter(this, nodeNew);

		/* If we have nothing around us, there is nothing to do */
		if (nodeList.size() == 0) {
			printGraph();
			return;
		}

		/* If we have neighbour(s), attach to them */
		for (int i = 0; i < nodeList.size(); i++) {
			GraphNode node = nodeList.get(i);
			GraphPoint point = pointList.get(i);

			GraphRouter router;
			if (point instanceof GraphRouter) {
				/* We attach at the end/crossing of a pipe */
				router = (GraphRouter) point;
			} else {
				/* We created a new crossing; split the current leg */
				router = ((GraphLeg) point).split(node);
			}

			/* Link the routers together */
			router.linkTo(nodeNew, routerNew);
		}

		routerNew.optimize();

		printGraph();
	}

	public void onPipeRemove(int x, int y, int z) {
		GraphNode node = new GraphNode(x, y, z);
		GraphPoint point = getPoint(node);
		if (point == null) {
			System.out.println("Removing pipe which was not in Graph");
			return;
		}

		if (point instanceof GraphLeg) {
			/* Split the leg so we have a router we can remove */
			point = ((GraphLeg) point).split(node);
		}
		/* Now we are sure the point is a router */
		GraphRouter router = (GraphRouter) point;

		/* Remove any neighbouring legs */
		for (GraphPoint p : (LinkedList<GraphPoint>) router.links.clone()) {
			if (p instanceof GraphLeg) {
				((GraphLeg) p).split(router);
			}
		}

		/* Remove the router now */
		router.destroy();
		tiles.remove(node);

		printGraph();
	}
}
