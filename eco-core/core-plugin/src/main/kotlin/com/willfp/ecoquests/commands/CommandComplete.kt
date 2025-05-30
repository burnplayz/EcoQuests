package com.willfp.ecoquests.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.ecoquests.quests.Quest
import com.willfp.ecoquests.quests.Quests
import com.willfp.ecoquests.tasks.Tasks
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil

class CommandComplete(plugin: EcoPlugin) : PluginCommand(
    plugin,
    "complete",
    "ecoquests.command.complete",
    false
) {
    override fun onExecute(sender: CommandSender, args: List<String>) {
        val player = notifyPlayerRequired(args.getOrNull(0), "invalid-player")
        val quest = notifyNull(Quests[args.getOrNull(1)], "invalid-quest")

        if (!quest.hasStarted(player)) {
            sender.sendMessage(plugin.langYml.getMessage("not-started"))
            return
        }
        if (quest.hasCompleted(player)) {
            sender.sendMessage(plugin.langYml.getMessage("already-finished"))
            return
        }

        if (args.size > 2) {
            val taskTemplate = notifyNull(Tasks[args.getOrNull(2)], "invalid-task")
            val task = quest.getTask(taskTemplate)
            task?.giveExperience(player, task.getExperienceRequired(player))
            sender.sendMessage(plugin.langYml.getMessage("task_completed"))
            return
        }


        for (task in quest.tasks) {
            task.giveExperience(player, task.getExperienceRequired(player))
        }
        quest.checkCompletion(player)
        sender.sendMessage(plugin.langYml.getMessage("quest_completed"))
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

        if (args.size == 2) {
            StringUtil.copyPartialMatches(
                args[1],
                Quests.values().map { it.id },
                completions
            )
        }
        if(args.size == 3){
            val quest: Quest? = Quests[args[1]]
            if(quest!=null){
                StringUtil.copyPartialMatches(
                    args[2],
                    quest.tasks.map { task-> task.template.id },
                    completions
                )
            }
        }

        return completions
    }
}
