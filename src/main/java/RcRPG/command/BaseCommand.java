package RcRPG.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

public abstract class BaseCommand extends Command {

    public BaseCommand(String name, String description) {
        super(name, description);
    }

    public BaseCommand(String name) {
        super(name);
    }

    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission(this.getPermission());
    }
}
