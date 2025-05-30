package com.willfp.ecoquests.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.util.StringUtils
import com.willfp.ecoquests.quests.Quest
import com.willfp.ecoquests.quests.Quests
import com.willfp.ecoquests.tasks.Tasks
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil
import kotlin.math.min

class CommandAdvance(plugin: EcoPlugin) : PluginCommand(
    plugin,
    "advance",
    "ecoquests.command.advance",
    false
) {
    // /ecoquests advance {player} {amount} {quest} {task}
    override fun onExecute(sender: CommandSender, args: List<String>) {
        val player = notifyPlayerRequired(args.getOrNull(0), "invalid-player") ?: return
        val quest = notifyNull(Quests[args.getOrNull(1)], "invalid-quest") ?: return
        val task = args.getOrNull(2)?.let { Tasks[it] } ?: run {
            sender.sendMessage(plugin.langYml.getMessage("invalid-task"))
            return
        }
        val amount = notifyNull(args.getOrNull(3), "amount_required") ?: return

        when {
            !quest.hasStarted(player) -> {
                sender.sendMessage(plugin.langYml.getMessage("not-started_quest"))
                return
            }
            quest.hasCompleted(player) -> {
                sender.sendMessage(plugin.langYml.getMessage("already-completed"))
                return
            }
        }

        quest.getTask(task)?.let { actualTask ->
            val validAmount = min(amount.toDouble(), actualTask.getExperienceRequired(player))
            actualTask.gainExperience(player, validAmount)

        } ?: sender.sendMessage(plugin.langYml.getMessage("invalid-task"))


        sender.sendMessage(
            plugin.langYml.getMessage("exp-quest_progressed", StringUtils.FormatOption.WITHOUT_PLACEHOLDERS)
                .replace("%xp%", amount.toString())
                .replace("%quest%", quest.name)
                .replace("%task%", task.id)
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
