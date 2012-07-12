package xabrain.mods.transport;

import net.minecraft.src.Chunk;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;
import net.minecraft.src.forge.ISaveEventHandler;

public class SaveLoadHandler implements ISaveEventHandler {

	@Override
	public void onWorldLoad(World world) {
		new Graph(world);
	}

	@Override
	public void onWorldSave(World world) {}

	@Override
	public void onChunkLoad(World world, Chunk chunk) {
		if (!chunk.isTerrainPopulated) return;

		Graph graph = Graph.getGraph(world);

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for (int y = 0; y < 256; y++) {
					if (chunk.getBlockID(x, y, z) == mod_Transport.blockPipe.blockID) {
						if ((chunk.getBlockMetadata(x, y, z) & 7) == 0) continue;
						graph.onPipeAdd(x + chunk.xPosition * 16, y, z + chunk.zPosition * 16);
					}
				}
			}
		}
	}

	@Override
	public void onChunkUnload(World world, Chunk chunk) {
		Graph graph = Graph.getGraph(world);

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for (int y = 0; y < 256; y++) {
					if (chunk.getBlockID(x, y, z) == mod_Transport.blockPipe.blockID) {
						if ((chunk.getBlockMetadata(x, y, z) & 7) == 0) continue;
						graph.onPipeRemove(x + chunk.xPosition * 16, y, z + chunk.zPosition * 16);
					}
				}
			}
		}
	}

	@Override
	public void onChunkSaveData(World world, Chunk chunk, NBTTagCompound data) {}

	@Override
	public void onChunkLoadData(World world, Chunk chunk, NBTTagCompound data) {}

}
