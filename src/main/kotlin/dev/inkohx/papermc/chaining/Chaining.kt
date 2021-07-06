package dev.inkohx.papermc.chaining

import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused")
class Chaining : JavaPlugin() {
    override fun onLoad() {
        saveDefaultConfig()
    }

    override fun onEnable() {
        server.pluginManager.registerEvents(EventListener(this), this)
    }
}