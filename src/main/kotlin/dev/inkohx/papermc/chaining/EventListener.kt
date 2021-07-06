package dev.inkohx.papermc.chaining

import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class EventListener(plugin: Chaining) : Listener {
    private val maxBreakCount = plugin.config.getInt("maxBreakCount")

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player
        val tool = player.itemOnCursor

        if (!player.isSneaking) return
        if (!event.block.isValidTool(tool)) return
        if (!Contants.availableBlocks.contains(event.block.type)) return

        tailrec fun getTargetBlocks(
            targetBlocks: List<Block> = listOf(event.block),
            collectBlocks: List<Block> = listOf(event.block),
        ): List<Block> {
            if (collectBlocks.size >= maxBreakCount) return collectBlocks.take(maxBreakCount)

            val aroundBlocks = BlockFace.values()
                .map { blockFace -> targetBlocks.map { block -> block.getRelative(blockFace, 1) } }
                .flatten()
                .filter { block -> collectBlocks.any { collectBlock -> block.blockKey == collectBlock.blockKey }.not() }
                .filter { block -> (Contants.availableBlocks.contains(block.type)) and (block.type == event.block.type) }

            if (aroundBlocks.isEmpty()) return collectBlocks.distinctBy { it.blockKey }.take(maxBreakCount)

            val totalTargetBlocks = (aroundBlocks + collectBlocks).distinctBy { it.blockKey }

            return if (totalTargetBlocks.size >= maxBreakCount) totalTargetBlocks.take(maxBreakCount)
            else getTargetBlocks(aroundBlocks, totalTargetBlocks)
        }

        getTargetBlocks().forEach { it.breakNaturally(tool) }
    }
}