package com.willfp.ecoquests.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.ecoquests.quests.Quests
import org.bukkit.command.CommandSender

class CommandWipe(plugin: EcoPlugin) : PluginCommand(
    plugin,
    "wipe",
    "ecoquests.command.wipe",
    false
) {
    val verifySet: HashSet<String> = HashSet<String>();
    override fun onExecute(sender: CommandSender, args: List<String>) {
        if(!verifySet.contains(sender.name)){
            verifySet.add(sender.name)
            sender.sendMessage(
                plugin.langYml.getMessage("again-confirm")
            )

            plugin.scheduler.runLater({
                if(!verifySet.contains(sender.name))return@runLater
                verifySet.remove(sender.name)
                sender.sendMessage(
                    plugin.langYml.getMessage("confirm-time-passed")
                )
            },
                15*20L)

            return
        }

        verifySet.remove(sender.name)

        for(quest in Quests.values()){
            quest.reset()
        }
        sender.sendMessage(
            plugin.langYml.getMessage("wipe-quests")
        )
    }

    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> {
        return mutableListOf<String>()
    }
}

