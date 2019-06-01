package com.github.unchama.seichiassist.commands

import arrow.core.Some
import arrow.core.some
import com.github.unchama.contextualexecutor.ContextualExecutor
import com.github.unchama.contextualexecutor.asNonBlockingTabExecutor
import com.github.unchama.contextualexecutor.builder.CommandExecutionScope
import com.github.unchama.contextualexecutor.builder.CommandResponse
import com.github.unchama.contextualexecutor.builder.ContextualExecutorBuilder
import com.github.unchama.contextualexecutor.builder.Parsers.identity
import com.github.unchama.contextualexecutor.builder.Parsers.nonNegativeInteger
import com.github.unchama.contextualexecutor.builder.response.ResponseToSender
import com.github.unchama.contextualexecutor.builder.response.asResponseToSender
import com.github.unchama.contextualexecutor.executors.BranchedExecutor
import com.github.unchama.seichiassist.SeichiAssist
import com.github.unchama.util.data.merge
import org.bukkit.ChatColor
import org.bukkit.command.CommandExecutor

object ContributeCommand {
  private suspend fun CommandExecutionScope.addContributionPoint(targetPlayerName: String, point: Int): CommandResponse =
      SeichiAssist.databaseGateway.playerDataManipulator
          .addContributionPoint(targetPlayerName, point)
          .map {
            val operationResponse =
                if (point >= 0) {
                  "${ChatColor.GREEN}${targetPlayerName}に貢献度ポイントを${point}追加しました"
                } else {
                  "${ChatColor.GREEN}${targetPlayerName}の貢献度ポイントを${point}減少させました"
                }

            returnMessage(operationResponse)
          }.merge()

  private val helpMessageResponse: ResponseToSender = listOf(
      "${ChatColor.YELLOW}${ChatColor.BOLD}[コマンドリファレンス]",
      "${ChatColor.RED}/contribute add <プレイヤー名> <増加分ポイント>",
      "指定されたプレイヤーの貢献度ptを指定分増加させます",
      "${ChatColor.RED}/contribute remove <プレイヤー名> <減少分ポイント>",
      "指定されたプレイヤーの貢献度ptを指定分減少させます(入力ミス回避用)"
  ).asResponseToSender()

  private val parserConfiguredBuilder = ContextualExecutorBuilder.beginConfiguration()
      .argumentsParsers(listOf(
          identity,
          nonNegativeInteger(Some(
              "${ChatColor.RED}増加分ポイントは0以上の整数を指定してください。".asResponseToSender()
          ))
      ), onMissingArguments = { helpMessageResponse.some() })

  private val addPointExecutor: ContextualExecutor = parserConfiguredBuilder
      .execution { context ->
        val targetPlayerName = context.args.parsed[0] as String
        val point = context.args.parsed[1] as Int

        addContributionPoint(targetPlayerName, point)
      }
      .build()

  private val removePointExecutor: ContextualExecutor = parserConfiguredBuilder
      .execution { context ->
        val targetPlayerName = context.args.parsed[0] as String
        val point = context.args.parsed[1] as Int

        addContributionPoint(targetPlayerName, -point)
      }
      .build()

  private val printHelpExecutor: ContextualExecutor = ContextualExecutorBuilder.beginConfiguration()
      .execution { helpMessageResponse.some() }
      .build()

  val executor: CommandExecutor =
      BranchedExecutor(
          mapOf("add" to addPointExecutor, "remove" to removePointExecutor),
          whenArgInsufficient = printHelpExecutor, whenBranchNotFound = printHelpExecutor
      ).asNonBlockingTabExecutor()
}
