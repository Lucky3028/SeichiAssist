package com.github.unchama.seichiassist.data.button;

import com.github.unchama.seichiassist.data.descrptions.PlayerInfomationDescriptions;
import com.github.unchama.seichiassist.data.itemstack.builder.SkullItemStackBuilder;
import com.github.unchama.seichiassist.data.slot.button.Button;
import com.github.unchama.seichiassist.data.slot.button.ButtonBuilder;
import com.github.unchama.seichiassist.data.slot.handler.SlotActionHandler;
import com.github.unchama.seichiassist.data.slot.handler.Triggers;
import com.github.unchama.seichiassist.text.Text;
import org.bukkit.ChatColor;

/**
 * @author karayuu
 */
public class PlayerDataButtons {
    public static Button playerInfo = ButtonBuilder
        .from(
            SkullItemStackBuilder
                .of()
                .playerSkull()
                .title(data -> Text.of(data.name + "の統計データ", ChatColor.BOLD, ChatColor.YELLOW, ChatColor.UNDERLINE))
                .lore(PlayerInfomationDescriptions.playerInfoLore)
        )
        .at(0)
        .handler(new SlotActionHandler(
            Triggers.RIGHT_CLICK,
            //TODO: WIP
            event -> {}
        ))
        .build();
}
