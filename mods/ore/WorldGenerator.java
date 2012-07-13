package xabrain.mods.ore;

import java.util.Random;

import net.minecraft.src.ChunkProviderEnd;
import net.minecraft.src.ChunkProviderHell;
import net.minecraft.src.IChunkProvider;
import net.minecraft.src.World;
import net.minecraft.src.WorldGenMinable;
import cpw.mods.fml.common.IWorldGenerator;

public class WorldGenerator implements IWorldGenerator {
	@Override
	public void generate(Random random, int chunkX, int chunkZ, Object... additionalData) {
		World w = (World) additionalData[0];
		IChunkProvider cp = (IChunkProvider) additionalData[1];

		/* We don't generator our ores in Hell or End */
		if (cp instanceof ChunkProviderHell || cp instanceof ChunkProviderEnd) return;

		for (int i = 0; i < mod_Ore.oreNames.length; i++) {
			for (int j = 0; j < 20; j++) {
				int x = (chunkX << 4) + random.nextInt(16);
				int y = 10 + random.nextInt(54);
				int z = (chunkZ << 4) + random.nextInt(16);
				new WorldGenMinable(mod_Ore.blockOre.blockID, i, 8).generate(w, random, x, y, z);
			}
		}
	}
}
