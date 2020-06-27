package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n.tl;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Console;
import com.earth2me.essentials.User;
import com.earth2me.essentials.messaging.IMessageRecipient;
import com.earth2me.essentials.utils.FormatUtil;

import net.milkbowl.vault.chat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.Server;

import java.util.List;

public class Commandmsg extends EssentialsLoopCommand {

    public Commandmsg() {
        super("msg");
    }

    @Override
    public void run(Server server, CommandSource sender, String commandLabel, String[] args) throws Exception {
        if (args.length < 2 || args[0].trim().length() < 2 || args[1].trim().isEmpty()) {
            throw new NotEnoughArgumentsException();
        }

        String message = getFinalArg(args, 1);
        boolean canWildcard;
        if (sender.isPlayer()) {
            User user = ess.getUser(sender.getPlayer());
            if (user.isMuted()) {
                throw new Exception(user.hasMuteReason() ? tl("voiceSilencedReason", user.getMuteReason()) : tl("voiceSilenced"));
            }
            message = FormatUtil.formatMessage(user, "essentials.msg", message);
            canWildcard = getTFMHandler().isAdmin(user);
        } else {
            message = FormatUtil.replaceFormat(message);
            canWildcard = true;
        }

        // Sending messages to console
        if (args[0].equalsIgnoreCase(Console.NAME)) {
            IMessageRecipient messageSender = sender.isPlayer() ? ess.getUser(sender.getPlayer()) : Console.getInstance();
            messageSender.sendMessage(Console.getInstance(), message);
            return;
        }

        loopOnlinePlayers(server, sender, canWildcard, canWildcard, args[0], new String[]{message});
    }

    @Override
    protected void updatePlayer(final Server server, final CommandSource sender, final User messageReceiver, final String[] args) {
        IMessageRecipient messageSender = sender.isPlayer() ? ess.getUser(sender.getPlayer()) : Console.getInstance();
        if (getTFMHandler().isVanished(messageReceiver) && !getTFMHandler().isAdmin(sender.getSender()))
        {
            messageSender.sendMessage(ChatColor.RED + "Error: " + ChatColor.RED + "Player not found.");
            return;
        }
        messageSender.sendMessage(messageReceiver, args[0]); // args[0] is the message.
    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, CommandSource sender, String commandLabel, String[] args) {
        if (args.length == 1) {
            return getPlayers(server, sender);
        } else {
            return null;  // It's a chat message, use the default chat handler
        }
    }
}
