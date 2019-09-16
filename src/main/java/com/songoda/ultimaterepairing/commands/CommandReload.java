package com.songoda.ultimaterepairing.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.ultimaterepairing.UltimateRepairing;
import java.util.List;
import org.bukkit.command.CommandSender;

public class CommandReload extends AbstractCommand {

    public CommandReload() {
        super(false, "reload");
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        UltimateRepairing.getInstance().reloadConfig();
        UltimateRepairing.getInstance().getLocale().getMessage("&7Configuration and Language files reloaded.").sendPrefixedMessage(sender);
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender cs, String... strings) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "ultimaterepairing.admin";
    }

    @Override
    public String getSyntax() {
        return "/ur reload";
    }

    @Override
    public String getDescription() {
        return "Reload the Configuration and Language files.";
    }
}
