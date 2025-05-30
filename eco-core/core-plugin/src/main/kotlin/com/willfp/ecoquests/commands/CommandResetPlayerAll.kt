package com.willfp.ecoquests.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.util.StringUtils
import com.willfp.ecoquests.quests.Quests
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil

class CommandResetPlayerAll(plugin: EcoPlugin) : PluginCommand(
    plugin,
    "resetplayerall",
    "ecoquests.command.resetplayerall",
    false
) {
    override fun onExecute(sender: CommandSender, args: List<String>) {
        val player = notifyPlayerRequired(args.getOrNull(0), "invalid-player")
        for(quest in Quests.values()){
            if(!quest.hasStarted(player))continue
            quest.reset(player)
        }

        sender.sendMessage(
            plugin.langYml.getMessage("reset-quest-for-player-all", StringUtils.FormatOption.WITHOUT_PLACEHOLDERS)
                .replace("%player%", player.name)
        )
    }

    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> {
        val completions = mutableListOf<String>()

        if (args.size == 1) {
            StringUtil.copyPartialMatches(
                args[0],
                plugin.server.onlinePlayers.map { it.name },
                completions
            )
        }

        return completions
    }
}

