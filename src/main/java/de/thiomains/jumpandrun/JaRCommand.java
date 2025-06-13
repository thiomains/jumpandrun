package de.thiomains.jumpandrun;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JaRCommand implements CommandExecutor {

    private Main main;

    public JaRCommand(Main main) {
        this.main = main;
        main.getCommand("jump").setExecutor(this);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            sender.sendMessage("Jump command");
            main.getJumpAndRun().start((Player) sender);
        }

        return true;
    }

}
