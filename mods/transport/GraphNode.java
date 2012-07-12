package xabrain.mods.transport;

import net.minecraft.src.World;

public class GraphNode {
	public int x;
	public int y;
	public int z;

	public GraphNode(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public int hashCode() {
		return ((x & 0xFFFF) << 16) | (z & 0xFFFF);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;

		GraphNode other = (GraphNode) obj;

		if (other.x != x) return false;
		if (other.z != z) return false;
		if (other.y != y) return false;

		return true;
	}
}
