package host.skyone.mc.plugin.base

import org.bukkit.plugin.java.JavaPlugin

class PaperKotlin : JavaPlugin() {
    override fun onEnable() {
        logger.info("Load PaperKotlin success")
    }
}