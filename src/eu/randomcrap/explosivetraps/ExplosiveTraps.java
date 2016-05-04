package eu.randomcrap.explosivetraps;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.tommytony.war.War;

public class ExplosiveTraps extends JavaPlugin {

    War                warplugin;
    Traps              traps;
    WorldGuardPlugin   wgplugin;

    final static int[] lowBlocks         = new int[] { 0, 6, 8, 9, 10, 11, 18, 26, 27, 28, 29, 30,
            31, 32, 37, 38, 39, 40, 44, 50, 51, 55, 59, 65, 66, 69, 70, 72, 75, 76, 77, 78, 83, 90,
            92, 93, 94, 96, 104, 105, 106, 111, 115, 119, 126, 127, 131, 132, 140, 141, 142, 143,
            147, 148, 149, 150, 151, 157, 171, 175 };
    final static int[] interactiveBlocks = new int[] { 54, 69, 58, 77, 61, 23, 62, 63, 145, 143,
            26, 64, 68, 93, 94, 96, 107, 116, 117, 138, 146, 149, 150, 158, 137, 130, 154 };

    @Override
    public void onEnable() {
        saveDefaultConfig();
        final Server server = getServer();
        final PluginManager pm = server.getPluginManager();
        warplugin = (War) pm.getPlugin("War");
        wgplugin = (WorldGuardPlugin) pm.getPlugin("WorldGuard");
        traps = new Traps(this);
        pm.registerEvents(traps, this);
        return;
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label,
                             final String[] args) {
        if (cmd.getName().equalsIgnoreCase("setTrapBlock") && args.length > 0) {
            int item;
            try {
                item = Integer.parseInt(args[0]);
            }
            catch (final Exception e) {
                return false;
            }
            if (!new ItemStack(item).getType().isBlock())
                return false;
            getConfig().set("trapBlock", item);
            traps.proximityTrapBlock = item;
            traps.lowBlock = isLowBlock(item);
            sender.sendMessage("TrapBlock set to " + item);
            saveConfig();
            return true;
        }
        else if (cmd.getName().equalsIgnoreCase("setPlacingItem") && args.length > 0) {
            int item;
            try {
                item = Integer.parseInt(args[0]);
            }
            catch (final Exception e) {
                return false;
            }
            getConfig().set("placingItem", item);
            traps.proximityPlacingItem = item;
            sender.sendMessage("PlacingItem set to " + item);
            saveConfig();
            return true;
        }
        else if (cmd.getName().equalsIgnoreCase("getProximityTrap") && args.length > 0) {
            if (sender instanceof Player) {
                int amount;
                try {
                    amount = Integer.parseInt(args[0]);
                }
                catch (final Exception e) {
                    return false;
                }
                if (amount <= 0)
                    return false;
                final Player player = (Player) sender;
                final ItemStack proximityTrap = new ItemStack(
                                                              traps.proximityPlacingItem,
                                                              amount,
                                                              (short) traps.proximityPlacingItemDurability);
                traps.nameStack(proximityTrap, "Proximity Trap", "Explosive Trap");

                player.getInventory().addItem(proximityTrap);
                return true;
            }
        }

        else if (cmd.getName().equalsIgnoreCase("getRemoteTrap") && args.length > 0) {
            if (sender instanceof Player) {
                int amount;
                try {
                    amount = Integer.parseInt(args[0]);
                }
                catch (final Exception e) {
                    return false;
                }
                if (amount <= 0)
                    return false;
                final Player player = (Player) sender;
                final ItemStack remoteTrap = new ItemStack(
                                                           traps.remotePlacingItem,
                                                           amount,
                                                           (short) traps.remotePlacingItemDurability);
                traps.nameStack(remoteTrap, "Remote Detonator Trap", "Explosive Trap");

                player.getInventory().addItem(remoteTrap);
                return true;
            }
        }
        return false;
    }

    boolean isLowBlock(final int itemId) {
        for (final int lowBlock : lowBlocks) {
            if (itemId == lowBlock)
                return true;
        }
        return false;
    }

    boolean isInteractiveBlock(final int itemId) {
        for (final int interactiveBlock : interactiveBlocks) {
            if (itemId == interactiveBlock)
                return true;
        }
        return false;
    }
}
