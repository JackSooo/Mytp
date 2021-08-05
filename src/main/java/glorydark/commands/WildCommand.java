package glorydark.commands;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import glorydark.Tools;

public class WildCommand extends Command {
    public WildCommand(){
        super("wild","Wild Command","/wild");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(commandSender instanceof Player){
            Tools.wild((Player) commandSender);
        }
        return true;
    }
}
