package eu.randomcrap.explosivetraps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.tommytony.war.Team;
import com.tommytony.war.Warzone;
import com.tommytony.war.config.WarzoneConfig;
import com.tommytony.war.config.WarzoneConfigBag;

public class Traps implements Listener {
    private final static String  remoteTrapName    = "Remote Detonator Trap";
    private final static String  proximityTrapName = "Proximity Trap";
    private final static String  trapDescription   = "Explosive Trap";
    private final ExplosiveTraps plugin;
    private final long           explodeDelay;
    private final boolean        proximityExplodeOnSelf;
    private final float          proximityExplosionPower;
    private final float          explosionDelayPower;
    private final boolean        proximityExplosionFire;
    private final boolean        explosionDelayFire;
    final int                    proximityPlacingItemDurability;
    int                          proximityPlacingItem;
    int                          proximityTrapBlock;
    boolean                      lowBlock          = false;
    private final int            defuseItem;
    private final float          defuseExplosionPower;
    private final boolean        defuseExplosionFire;
    private final double         defuseDistance;
    private final int            defuseTime;
    private final boolean        defuseEnabled;
    private final boolean        consumePlacingItem;
    private final boolean        consumeDefuseItem;
    private final boolean        explodeInWG;
    private final int            defuseItemDurabilityBreak;
    private final boolean        defuseDropTrap;
    private final int            defuseDropTrapChance;
    private final int            defuseTickTolerance;
    private final boolean        defuseDisplayBar;
    private final boolean        remoteExplosionFire;
    private final float          remoteExplosionPower;
    final int                    remotePlacingItemDurability;
    final int                    remotePlacingItem;
    private final int            remoteMaxTraps;
    private final int            remoteTrapBlock;
    private final boolean        remoteSneaking;
    private final boolean        defuseSneaking;

    private final int            proximityCraftAmount;
    final int                    remoteCraftAmount;
    private final boolean        chainRections;
    private final ItemStack[]    remoteChannels;
    private final ItemStack      remoteAllStack;
    private final ItemStack      remoteSingleStack;
    private final String         channelSelectorStyle;

    // final Block[] allTraps;

    public Traps(final ExplosiveTraps explosiveTraps) {
        plugin = explosiveTraps;
        final FileConfiguration config = plugin.getConfig();

        this.explodeDelay = config.getLong("explodeDelay") * 20;
        this.explosionDelayPower = (float) config.getDouble("explosionDelayPower");
        this.explosionDelayFire = config.getBoolean("explosionDelayFire");
        this.proximityExplosionFire = config.getBoolean("explosionFire");
        this.proximityExplodeOnSelf = config.getBoolean("explodeOnSelf");
        this.proximityExplosionPower = (float) config.getDouble("explosionPower");
        this.proximityPlacingItemDurability = config.getInt("placingItemDurability");
        this.proximityPlacingItem = config.getInt("placingItem");
        this.proximityTrapBlock = config.getInt("trapBlock");
        this.remoteExplosionPower = (float) config.getDouble("remoteExplosionPower");
        this.remoteExplosionFire = config.getBoolean("remotePlacingItem");
        this.remotePlacingItemDurability = config.getInt("remotePlacingItemDurability");
        this.remotePlacingItem = config.getInt("remotePlacingItem");
        this.remoteMaxTraps = config.getInt("remoteMaxTraps");
        this.remoteTrapBlock = config.getInt("remoteTrapBlock");
        this.remoteSneaking = config.getBoolean("remoteSneaking");
        this.defuseEnabled = config.getBoolean("defuseEnabled");
        this.defuseItem = config.getInt("defuseItem");
        this.defuseItemDurabilityBreak = config.getInt("defuseItemDurabilityBreak");
        this.defuseExplosionPower = (float) config.getDouble("defuseExplosionPower");
        this.defuseExplosionFire = config.getBoolean("defuseExplosionFire");
        this.defuseDistance = config.getDouble("defuseDistance");
        this.defuseDropTrap = config.getBoolean("defuseDropTrap");
        this.defuseDropTrapChance = config.getInt("defuseDropTrapChance");
        this.defuseTickTolerance = config.getInt("defuseTickTolerance");
        this.defuseDisplayBar = config.getBoolean("defuseDisplayBar");
        this.defuseTime = config.getInt("defuseTime");
        this.defuseSneaking = config.getBoolean("defuseSneaking");
        this.consumePlacingItem = config.getBoolean("consumePlacingItem");
        this.consumeDefuseItem = config.getBoolean("consumeDefuseItem");
        this.chainRections = config.getBoolean("chainRections");
        this.explodeInWG = config.getBoolean("explodeInWG");
        this.lowBlock = plugin.isLowBlock(proximityTrapBlock);
        this.proximityCraftAmount = config.getInt("proximityCraftAmount");
        this.remoteCraftAmount = config.getInt("remoteCraftAmount");
        this.channelSelectorStyle = config.getString("channelSelectorStyle");
        // this.allTraps = new Block[1024];

        /* Crafting */
        final List<String> proximityCraftShapeL = config.getStringList("proximityCraftShape");
        final List<String> remoteCraftShapeL = config.getStringList("remoteCraftShape");

        final String[] remoteCraftShape = remoteCraftShapeL.toArray(new String[remoteCraftShapeL
            .size()]);
        final String[] proximityCraftShape = proximityCraftShapeL
            .toArray(new String[proximityCraftShapeL.size()]);
        final Map<String, Object> remoteCraftMaterials = config
            .getConfigurationSection("remoteCraftMaterials").getValues(false);
        final Map<String, Object> proximityCraftMaterials = config
            .getConfigurationSection("proximityCraftMaterials").getValues(false);

        final ItemStack proximityTrap = new ItemStack(proximityPlacingItem, proximityCraftAmount,
                                                      (short) proximityPlacingItemDurability);
        nameStack(proximityTrap, Traps.proximityTrapName, Traps.trapDescription);

        final ShapedRecipe proximityRecipe = new ShapedRecipe(proximityTrap)
            .shape(proximityCraftShape[0], proximityCraftShape[1], proximityCraftShape[2]);

        for (final Entry<String, Object> entry : proximityCraftMaterials.entrySet()) {
            proximityRecipe.setIngredient(entry.getKey().charAt(0),
                                          Material.getMaterial((Integer) entry.getValue()));
        }

        final ItemStack remoteTrap = new ItemStack(remotePlacingItem, remoteCraftAmount,
                                                   (short) remotePlacingItemDurability);
        nameStack(remoteTrap, Traps.remoteTrapName, Traps.trapDescription);

        final ShapedRecipe remoteRecipe = new ShapedRecipe(remoteTrap).shape(remoteCraftShape[0],
                                                                             remoteCraftShape[1],
                                                                             remoteCraftShape[2]);

        for (final Entry<String, Object> entry : remoteCraftMaterials.entrySet()) {
            remoteRecipe.setIngredient(entry.getKey().charAt(0),
                                       Material.getMaterial((Integer) entry.getValue()));
        }

        plugin.getServer().addRecipe(proximityRecipe);
        plugin.getServer().addRecipe(remoteRecipe);

        /* Crafting */

        final String[] colors = { ChatColor.BLACK + "Black", ChatColor.DARK_RED + "Red",
                ChatColor.DARK_GREEN + "Green", ChatColor.GOLD + "Brown", ChatColor.BLUE + "Blue",
                ChatColor.DARK_PURPLE + "Purple", ChatColor.DARK_AQUA + "Cyan",
                ChatColor.GRAY + "L.Gray", ChatColor.DARK_GRAY + "Gray",
                ChatColor.LIGHT_PURPLE + "Pink", ChatColor.GREEN + "L.Green",
                ChatColor.YELLOW + "Yellow", ChatColor.AQUA + "L.Blue", ChatColor.RED + "Magenta",
                ChatColor.GOLD + "Orange", ChatColor.WHITE + "White" };
        remoteChannels = new ItemStack[colors.length];

        final int channelItem = channelSelectorStyle.equals("dye") ? 351 : 35;
        for (int i = 0; i < colors.length; i++) {
            final ItemStack stack = new ItemStack(
                                                  channelItem,
                                                  1,
                                                  (short) (channelSelectorStyle.equals("dye") ? i
                                                                                             : 15 - i));
            nameStack(stack, colors[i], null);
            remoteChannels[i] = stack;
        }
        this.remoteAllStack = new ItemStack(362);
        nameStack(this.remoteAllStack, "All", null);
        this.remoteSingleStack = new ItemStack(368);
        nameStack(this.remoteSingleStack, "Single", null);

    }

    @EventHandler
    public void onItemChange(final PlayerItemHeldEvent event) {
        replaceTrapNames(event.getPlayer().getInventory().getItem(event.getNewSlot()));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityClick(final EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            final Player player = (Player) event.getDamager();
            final ItemStack itemInHand = player.getItemInHand();
            if (itemInHand != null && isRemotePlacingItem(itemInHand)
                && player.hasMetadata("placedRemoteTraps")) {
                if (remoteSneaking && !player.isSneaking())
                    return;
                event.setCancelled(true);
                plugin
                    .getServer()
                    .getPluginManager()
                    .callEvent(new PlayerInteractEvent(player, Action.LEFT_CLICK_AIR, itemInHand,
                                                       null, null));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerClickCancel(final PlayerInteractEvent event) {
        final Action action = event.getAction();
        final ItemStack itemInHand = event.getItem();
        if (itemInHand != null) {
            if (action == Action.LEFT_CLICK_AIR || action == Action.RIGHT_CLICK_AIR) {
                if (event.isCancelled()
                    && (isProximityPlacingItem(itemInHand) || isRemotePlacingItem(itemInHand))) {
                    event.setCancelled(false);
                }
            }
        }

    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerClick(final PlayerInteractEvent event) {
        final Action action = event.getAction();

        final ItemStack itemInHand = event.getItem();
        if (itemInHand == null)
            return;
        if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
            if (isProximityPlacingItem(itemInHand)) {
                placeTrap(event, itemInHand, "proximityTrap");
            }
            else if (isRemotePlacingItem(itemInHand)) {
                placeTrap(event, itemInHand, "remoteTrap");
            }
            else if (defuseEnabled && itemInHand.getTypeId() == defuseItem) {
                defuseTrap(event, itemInHand);
            }
        }
        else if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
            if (isRemotePlacingItem(itemInHand)) {
                detonateTrap(event);
            }
        }
    }

    @EventHandler()
    public void onInventoryClose(final InventoryCloseEvent event) {
        final Player player = (Player) event.getPlayer();
        if (player.hasMetadata("configuringTrap")) {
            player.removeMetadata("configuringTrap", plugin);
        }
    }

    @EventHandler()
    public void onChooseChannel(final InventoryClickEvent event) {
        Inventory inv = null;
        String title = null;
        if ((title = (inv = event.getInventory()).getTitle()).matches("^Trap Channel:.*$")) {
            final Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);
            final ItemStack stack = event.getCurrentItem();
            if (inv.contains(stack)) {
                final Block block = ((Location) getMetaData(player, "configuringTrap")).getBlock();
                if (stack.hasItemMeta() && block.hasMetadata("remoteTrap")) {

                    if (block.getTypeId() == remoteTrapBlock) {
                        setChannel(block, stack, player);
                    }
                    else {
                        block.removeMetadata("remoteTrap", plugin);
                        block.removeMetadata("Team", plugin);
                        block.removeMetadata("Player", plugin);
                        block.removeMetadata("defuse", plugin);
                    }

                    event.getView().close();
                }
            }
        }
        else if (title.matches("^Remote Channel:.*$")) {
            event.setCancelled(true);
            final Player player = (Player) event.getWhoClicked();
            final ItemStack stack = event.getCurrentItem();
            if (inv.contains(stack)) {
                if (stack.hasItemMeta()) {
                    final String channel = stack.getItemMeta().getDisplayName();
                    event.getWhoClicked().setMetadata("remoteTrapChannel",
                                                      new FixedMetadataValue(plugin, channel));
                    player.sendMessage("Remote channel set to " + channel);
                    event.getView().close();

                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityTouch(final EntityInteractEvent event) {
        final Block block = event.getBlock();
        if (block.hasMetadata("proximityTrap")) {
            final World world = block.getWorld();
            final Location trapLoc = block.getLocation();
            final String player = (String) getMetaData(block, "Player");
            delBlock(block);
            if (plugin.wgplugin != null
                && !explodeInWG
                && !plugin.wgplugin.getRegionManager(world).getApplicableRegions(trapLoc)
                    .allows(DefaultFlag.OTHER_EXPLOSION)) {
                world.playEffect(trapLoc, Effect.EXTINGUISH, 0);
                return;
            }
            // world.createExplosion(block.getLocation(),
            // proximityExplosionPower,
            // proximityExplosionFire);
            createCusExplosion(world, trapLoc, proximityExplosionPower, proximityExplosionFire,
                               player);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerTouch(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final Block block = lowBlock ? event.getTo().getBlock() : player.getLocation()
            .subtract(0, 1, 0).getBlock();
        // check for trapBlock, because the current setting may have changed or
        // block might be destroyed
        if (block.getTypeId() == proximityTrapBlock && block.hasMetadata("proximityTrap")) {
            final Location trapLoc = player.getLocation();
            final World world = trapLoc.getWorld();
            if (plugin.warplugin != null && block.hasMetadata("Team")) {
                String blockTeam = "";
                final Object blockTeamTmp = getMetaData(block, "Team");
                if (blockTeamTmp != null) {
                    blockTeam = (String) blockTeamTmp;
                }
                String placingPlayer = "";
                final Object placingPlayerTmp = getMetaData(block, "Player");
                if (placingPlayerTmp != null) {
                    placingPlayer = (String) placingPlayerTmp;
                }
                String playerTeam = "";
                boolean friendlyFire = false;
                boolean pvpInZone = true;
                try {
                    playerTeam = Team.getTeamByPlayerName(player.getName()).getName();
                    final WarzoneConfigBag config = Warzone.getZoneByPlayerName(player.getName())
                        .getWarzoneConfig();
                    friendlyFire = config.getBoolean(WarzoneConfig.FRIENDLYFIRE).booleanValue();
                    pvpInZone = config.getBoolean(WarzoneConfig.PVPINZONE).booleanValue();
                }
                catch (final NullPointerException e) {
                }
                if (!pvpInZone || !proximityExplodeOnSelf && player.getName().equals(placingPlayer)
                    || blockTeam.equals(playerTeam) && !friendlyFire) {
                    world.playEffect(trapLoc, Effect.EXTINGUISH, 0);
                    delBlock(block);
                    return;
                }
            }

            else if (plugin.wgplugin != null
                     && plugin.wgplugin.getRegionManager(world).getApplicableRegions(trapLoc)
                         .size() > 0) {
                final ApplicableRegionSet set = plugin.wgplugin.getRegionManager(world)
                    .getApplicableRegions(trapLoc);
                if (!set.allows(DefaultFlag.OTHER_EXPLOSION) && !explodeInWG
                    || !set.allows(DefaultFlag.PVP)) {
                    world.playEffect(trapLoc, Effect.EXTINGUISH, 0);
                    delBlock(block);
                    return;
                }
            }
            else if (world.getPVP()) {
                String placingPlayer = "";
                final Object placingPlayerTmp = getMetaData(block, "Player");
                if (placingPlayerTmp != null) {
                    placingPlayer = (String) placingPlayerTmp;
                }
                if (placingPlayer.equals(player.getName()) && !proximityExplodeOnSelf) {
                    world.playEffect(trapLoc, Effect.EXTINGUISH, 0);
                    delBlock(block);
                    return;
                }
            }
            else if (!world.getPVP()) {
                world.playEffect(trapLoc, Effect.EXTINGUISH, 0);
                delBlock(block);
                return;
            }
            // delBlock(block);
            // world.createExplosion(trapLoc, proximityExplosionPower,
            // proximityExplosionFire);
            final String dmgerPlayer = (String) getMetaData(block, "Player");
            delBlock(block);
            createCusExplosion(world, trapLoc, proximityExplosionPower, proximityExplosionFire,
                               dmgerPlayer);

        }
        return;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityExplode(final EntityDamageByEntityEvent event) {
        final Entity dmger = event.getDamager();
        if (dmger instanceof TNTPrimed && event.getEntity() instanceof LivingEntity) {

            final TNTPrimed tnt = (TNTPrimed) dmger;
            if (tnt.hasMetadata("trapper")) {
                Player player = null;

                player = plugin.getServer().getPlayerExact((String) getMetaData(tnt, "trapper"));

                final LivingEntity entity = (LivingEntity) event.getEntity();
                if (entity != null && player != null) {
                    event.setCancelled(true);
                    entity.damage(event.getDamage(), player);
                    player.sendMessage("You got hit");
                    System.out.println(player.getDisplayName());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(final BlockBreakEvent event) {
        final Block block = event.getBlock();
        boolean isProximityTrap = false;
        if ((isProximityTrap = block.hasMetadata("proximityTrap"))
            || block.hasMetadata("remoteTrap")) {
            final String type = isProximityTrap ? "proximityTrap" : "remoteTrap";
            int placingItem;
            int placingItemDurability;
            if (isProximityTrap) {
                placingItem = this.proximityPlacingItem;
                placingItemDurability = this.proximityPlacingItemDurability;
            }
            else {
                placingItem = this.remotePlacingItem;
                placingItemDurability = this.remotePlacingItemDurability;
            }
            final Location loc = block.getLocation();
            final Player player = event.getPlayer();
            final World world = block.getWorld();
            if (defuseEnabled) {
                if (block.hasMetadata("defuse")) {
                    int defuse = defuseTime;
                    final Object obj = getMetaData(block, "defuse");
                    if (obj != null) {
                        defuse = (Integer) ((Object[]) obj)[0];
                    }

                    if (defuse < 1) {
                        player.sendMessage(defuseDisplayBar ? "Defusing: [" + ChatColor.GREEN
                                                              + "==========" + ChatColor.RESET
                                                              + "]" : "Defusing: "
                                                                      + ChatColor.GREEN + " 100%");
                        removeRemoteTrap(block, getPlacingPlayer(block), type);
                        delBlock(block);
                        if (defuseDropTrap && consumePlacingItem) {
                            final Random rand = new Random();
                            if (defuseDropTrapChance >= 100
                                || rand.nextInt(100) < defuseDropTrapChance) {

                                final ItemStack droppedTrap = new ItemStack(
                                                                            placingItem,
                                                                            1,
                                                                            (short) placingItemDurability);
                                nameStack(droppedTrap, isProximityTrap ? Traps.proximityTrapName
                                                                      : Traps.remoteTrapName,
                                          Traps.trapDescription);
                                world.dropItemNaturally(loc, droppedTrap);
                            }
                        }
                        world.playEffect(block.getLocation(), Effect.EXTINGUISH, 0);
                        return;
                    }
                }
                if (loc.distance(player.getLocation()) > defuseDistance) {
                    event.setCancelled(true);
                    return;
                }
                removeRemoteTrap(block, getPlacingPlayer(block), type);
                final String dmgerPlayer = (String) getMetaData(block, "Player");
                delBlock(block);
                if (plugin.wgplugin != null
                    && !explodeInWG
                    && !plugin.wgplugin.getRegionManager(world).getApplicableRegions(loc)
                        .allows(DefaultFlag.OTHER_EXPLOSION)) {
                    world.playEffect(loc, Effect.EXTINGUISH, 0);
                    return;
                }
                // world.createExplosion(loc, defuseExplosionPower,
                // defuseExplosionFire);
                createCusExplosion(world, loc, defuseExplosionPower, defuseExplosionFire,
                                   dmgerPlayer);
            }
            else {
                world.playEffect(loc, Effect.EXTINGUISH, 0);
                removeRemoteTrap(block, getPlacingPlayer(block), type);
                delBlock(block);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onTrapExplode(final EntityExplodeEvent event) {
        final List<Block> blocks = event.blockList();
        for (final Block block : blocks) {
            final String player = (String) getMetaData(block, "Player");
            if (block.hasMetadata("proximityTrap")) {
                delBlock(block);
                if (chainRections) {

                    // block.getWorld().createExplosion(block.getLocation(),
                    // proximityExplosionPower,
                    // proximityExplosionFire);
                    createCusExplosion(block.getWorld(), block.getLocation(),
                                       proximityExplosionPower, proximityExplosionFire, player);
                }
            }
            else if (block.hasMetadata("remoteTrap")) {
                removeRemoteTrap(block, getPlacingPlayer(block), "remoteTrap");
                delBlock(block);
                if (chainRections) {
                    // block.getWorld().createExplosion(block.getLocation(),
                    // remoteExplosionPower,
                    // remoteExplosionFire);
                    createCusExplosion(block.getWorld(), block.getLocation(), remoteExplosionPower,
                                       remoteExplosionFire, player);

                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(final BlockPlaceEvent event) {
        final Block block = event.getBlockPlaced();
        boolean isProximityTrap = false;
        if ((isProximityTrap = block.hasMetadata("proximityTrap"))
            || block.hasMetadata("remoteTrap")) {
            if (event.isCancelled() || !event.canBuild()) {
                block.removeMetadata(isProximityTrap ? "proximityTrap" : "remoteTrap", plugin);
            }
            else {
                configureTrap(event, block, isProximityTrap ? "proximityTrap" : "remoteTrap");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockFade(final BlockFadeEvent event) {
        final Block block = event.getBlock();
        boolean isProximityTrap = false;
        if ((isProximityTrap = block.hasMetadata("proximityTrap"))
            || block.hasMetadata("remoteTrap")) {
            removeRemoteTrap(block, getPlacingPlayer(block), isProximityTrap ? "proximityTrap"
                                                                            : "remoteTrap");
            delBlock(block);
        }
    }

    @EventHandler
    public void onInventoryChange(final InventoryOpenEvent event) {
        final Inventory inv = event.getInventory();
        if (inv.getType() == InventoryType.ANVIL)
            return;
        for (final ItemStack stack : inv) {
            replaceTrapNames(stack);
        }
        for (final ItemStack stack : event.getPlayer().getInventory()) {
            replaceTrapNames(stack);
        }
    }

    final private void placeTrap(final PlayerInteractEvent event, final ItemStack stack,
                                 final String type) {
        final Player player = event.getPlayer();
        final Block block = event.getClickedBlock();

        // player pressing shift? configure channels
        if (type.equals("remoteTrap") && player.isSneaking()) {
            event.setCancelled(true);
            String invTitle;
            Inventory freqInv;
            // clicked block is a remote trap? configure trap channel
            if (block != null && block.hasMetadata("remoteTrap")) {
                if (!getMetaData(block, "Player").equals(player.getName())) {
                    player.sendMessage(ChatColor.RED + "That is not your trap!");
                    return;
                }

                invTitle = "Trap Channel: " + (String) getMetaData(block, "remoteTrap"); // +

                freqInv = plugin.getServer().createInventory(null, 18, invTitle);
                player.setMetadata("configuringTrap",
                                   new FixedMetadataValue(plugin, block.getLocation()));
                freqInv.setContents(remoteChannels);
            }
            // else configure remote detonator channel
            else {
                String playerChannel = null;
                if (player.hasMetadata("remoteTrapChannel")) {
                    playerChannel = (String) getMetaData(player, "remoteTrapChannel");
                }

                if (playerChannel == null || playerChannel == "") {
                    final String initChannel = "All";
                    player.setMetadata("remoteTrapChannel", new FixedMetadataValue(plugin,
                                                                                   initChannel));
                    playerChannel = initChannel;
                }
                invTitle = "Remote Channel: " + playerChannel;
                freqInv = plugin.getServer().createInventory(null, 18, invTitle);
                freqInv.setContents(remoteChannels);
                freqInv.addItem(this.remoteSingleStack);
                freqInv.addItem(this.remoteAllStack);
            }

            player.openInventory(freqInv);
            /*
             * List<Block> placedBlocks = findPlacedTraps(player); if
             * (placedBlocks != null) { for (Block block1 : placedBlocks) {
             * String blockChan = (String) getMetaData(block1, "remoteTrap");
             * for (ItemStack stack1 : freqInv) { if
             * (blockChan.equals(stack1.getItemMeta().getDisplayName())) {
             * stack1.setAmount(stack1.getAmount() + 1); } } } }
             */

            return;
        }
        // else check for air, permissions and interactive blocks, then place
        // trap
        if (block == null) {
            event.setCancelled(true);
            return;
        }
        if (!player.hasPermission("explosivetraps.place")
            || plugin.isInteractiveBlock(block.getTypeId()) && !player.isSneaking())
            return;
        event.setCancelled(true);

        final Material blockType = block.getType();
        final Block trap = blockType == Material.LONG_GRASS || blockType == Material.SNOW ? block
                                                                                         : block
                                                                                             .getLocation()
                                                                                             .add(0,
                                                                                                  1,
                                                                                                  0)
                                                                                             .getBlock();

        if ((trap.getType() == Material.AIR || trap == block) && block.getType().isSolid()) {

            final World world = trap.getWorld();
            if (plugin.wgplugin != null
                && !player.isOp()
                && !plugin.wgplugin.getRegionManager(world)
                    .getApplicableRegions(trap.getLocation()).allows(DefaultFlag.TNT))
                return;
            String blockMeta = null;
            if (type.equals("remoteTrap")) {
                if (stack.getAmount() < 2 && consumePlacingItem
                    && player.getGameMode() != GameMode.CREATIVE)
                    return;
                if (player.hasMetadata("remoteTrapChannel")) {
                    blockMeta = (String) getMetaData(player, "remoteTrapChannel");
                }
                if (blockMeta == null || blockMeta.equals("Single") || blockMeta.equals("All")
                    || blockMeta.equals("")) {
                    blockMeta = remoteChannels[0].getItemMeta().getDisplayName();
                }
            }
            else {
                blockMeta = "proximityTrap";
                setChannel(trap, remoteChannels[0], player);
            }
            trap.setMetadata(type, new FixedMetadataValue(plugin, blockMeta));
            plugin.getServer().getPluginManager()
                .callEvent(new BlockPlaceEvent(trap, trap.getState(), block, stack, player, true));

        }
    }

    final private void configureTrap(final BlockPlaceEvent event, final Block trap,
                                     final String type) {

        final int trapBlock = type.equals("proximityTrap") ? this.proximityTrapBlock
                                                          : this.remoteTrapBlock;
        final ItemStack stack = event.getItemInHand();
        final World world = trap.getWorld();
        final Player player = event.getPlayer();
        final Location trapLoc = trap.getLocation();

        if (type.equals("remoteTrap")) {
            List<Block> placedTraps = null;
            if (player.hasMetadata("placedRemoteTraps")) {
                placedTraps = findPlacedTraps(player);
            }
            if (placedTraps != null) {
                if (remoteMaxTraps > 0 && placedTraps != null
                    && placedTraps.size() >= remoteMaxTraps) {
                    trap.removeMetadata(type, plugin);
                    return;
                }
            }
            else {
                placedTraps = new ArrayList<Block>();
            }
            placedTraps.add(trap);
            player.setMetadata("placedRemoteTraps", new FixedMetadataValue(plugin, placedTraps));
        }

        trap.setTypeId(trapBlock, false);
        trap.getDrops().clear();

        if (plugin.warplugin != null) {
            final Team playerTeam = Team.getTeamByPlayerName(player.getName());
            if (playerTeam != null) {
                trap.setMetadata("Team", new FixedMetadataValue(plugin, playerTeam.getName()));
            }
        }
        if (consumePlacingItem && player.getGameMode() != GameMode.CREATIVE) {
            decreaseStack(stack, player);
        }
        trap.setMetadata("Player", new FixedMetadataValue(plugin, player.getName()));
        if (defuseEnabled) {
            trap.setMetadata("defuse", new FixedMetadataValue(plugin, new Object[] { defuseTime,
                    world.getFullTime() }));
        }
        world.playEffect(trapLoc, Effect.CLICK1, 0);

        // delayed explosion
        if (explodeDelay > 0) {
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    if (trap.hasMetadata(type)) {
                        removeRemoteTrap(trap, player, type);
                        if (trap.getTypeId() == trapBlock) {
                            delBlock(trap);
                            if (plugin.wgplugin != null
                                && !explodeInWG
                                && !plugin.wgplugin.getRegionManager(world)
                                    .getApplicableRegions(trapLoc)
                                    .allows(DefaultFlag.OTHER_EXPLOSION)) {
                                world.playEffect(trapLoc, Effect.EXTINGUISH, 0);
                                return;
                            }
                            // world.createExplosion(trapLoc,
                            // explosionDelayPower, explosionDelayFire);
                            createCusExplosion(world, trapLoc, explosionDelayPower,
                                               explosionDelayFire, player.getDisplayName());
                        }
                        else {
                            delBlock(trap);
                        }
                    }
                }
            }, explodeDelay);
        }

    }

    final private void defuseTrap(final PlayerInteractEvent event, final ItemStack stack) {
        final Player player = event.getPlayer();
        if (defuseSneaking && !player.isSneaking())
            return;
        final Block block = event.getClickedBlock();
        if (!block.hasMetadata("defuse") || block.hasMetadata("DefuseBlocked")
            || block.getLocation().distance(event.getPlayer().getLocation()) > defuseDistance)
            return;
        event.setCancelled(true);
        int defuse = defuseTime;
        long time = 0L;
        final Object obj = getMetaData(block, "defuse");
        if (obj != null) {
            defuse = (Integer) ((Object[]) obj)[0];
            time = (Long) ((Object[]) obj)[1];
        }
        if (player.getWorld().getFullTime() - time > (long) 20 + defuseTickTolerance) {
            defuse = defuseTime;
        }

        final World world = block.getWorld();
        if (defuse < 1) {
            plugin.getServer().getPluginManager().callEvent(new BlockBreakEvent(block, player));
            return;
        }
        if (consumeDefuseItem) {
            stack.setDurability((short) (stack.getDurability() + 10));
            if (stack.getDurability() > defuseItemDurabilityBreak) {
                decreaseStack(stack, player);
                stack.setDurability((short) 0);
                player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1, 1);
            }
        }
        block.setMetadata("defuse",
                          new FixedMetadataValue(plugin, new Object[] { --defuse,
                                  world.getFullTime() }));
        block.setMetadata("DefuseBlocked", new FixedMetadataValue(plugin, true));

        final int amnt = Math.round(10 - (float) (defuse + 1) / defuseTime * 10);
        final String msg = defuseDisplayBar ? "["
                                              + (amnt > defuseTime / 2 ? ChatColor.YELLOW
                                                                      : ChatColor.RED)
                                              + StringUtils.repeat("=", amnt) + ChatColor.RESET
                                              + StringUtils.repeat("_", 10 - amnt) + "]"
                                           : " "
                                             + Math.round(100 - (float) (defuse + 1) / defuseTime
                                                          * 100) + "%";
        player.sendMessage("Defusing: " + msg);
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                block.removeMetadata("DefuseBlocked", plugin);
            }
        }, 20);

    }

    final private void detonateTrap(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (remoteSneaking && !player.isSneaking())
            return;
        if (player.hasMetadata("placedRemoteTraps")) {
            event.setCancelled(true);
            String playerChannel = null;
            if (player.hasMetadata("remoteTrapChannel")) {
                playerChannel = (String) getMetaData(player, "remoteTrapChannel");
            }
            if (playerChannel == null || playerChannel == "") {
                playerChannel = "All";
            }
            final List<Block> placedTraps = findPlacedTraps(player);
            List<Block> placedTrapsTmp = null;
            if (placedTraps != null) {
                placedTrapsTmp = new ArrayList<Block>(placedTraps);
                final boolean singleChannel = playerChannel.equals("Single") ? true : false;
                final boolean allChannel = playerChannel.equals("All") ? true : false;
                for (final Block trap : placedTrapsTmp) {
                    if (trap != null && trap.getTypeId() == remoteTrapBlock
                        && trap.hasMetadata("remoteTrap")) {
                        if (allChannel || singleChannel
                            || playerChannel.equals(getMetaData(trap, "remoteTrap"))) {
                            final Location trapLoc = trap.getLocation();
                            final World world = trap.getWorld();
                            placedTraps.remove(trap);

                            delBlock(trap);
                            if (trapLoc.getChunk().isLoaded()) {
                                if (plugin.wgplugin != null
                                    && !explodeInWG
                                    && !plugin.wgplugin.getRegionManager(world)
                                        .getApplicableRegions(trapLoc)
                                        .allows(DefaultFlag.OTHER_EXPLOSION)) {
                                    world.playEffect(trapLoc, Effect.EXTINGUISH, 0);
                                    if (singleChannel) {
                                        break;
                                    }
                                    continue;
                                }
                                // world.createExplosion(trapLoc,
                                // remoteExplosionPower,
                                // remoteExplosionFire);
                                createCusExplosion(world, trapLoc, remoteExplosionPower,
                                                   remoteExplosionFire, player.getDisplayName());
                                if (singleChannel) {
                                    break;
                                }
                            }
                        }
                    }
                    else {
                        placedTraps.remove(trap);
                        delBlock(trap);
                    }
                }

                if (placedTraps.isEmpty()) {
                    player.removeMetadata("placedRemoteTraps", plugin);
                }
            }
            else {
                player.removeMetadata("placedRemoteTraps", plugin);
            }
        }
    }

    final private void delBlock(final Block block) {
        Chunk chunk = null;
        if (!(chunk = block.getChunk()).isLoaded()) {
            chunk.load(false);
            delBlock(block);
            chunk.unload();
            return;
        }
        block.setType(Material.AIR);
        block.removeMetadata("proximityTrap", plugin);
        block.removeMetadata("remoteTrap", plugin);
        block.removeMetadata("Team", plugin);
        block.removeMetadata("Player", plugin);
        block.removeMetadata("defuse", plugin);
        return;
    }

    final private void decreaseStack(final ItemStack stack, final Player player) {
        final int amount = stack.getAmount();
        if (amount > 1) {
            stack.setAmount(amount - 1);
        }
        else {
            player.setItemInHand(null);
        }
    }

    @SuppressWarnings("unchecked")
    final private List<Block> findPlacedTraps(final Player player) {
        List<Block> placedBlocks = null;
        final Object obj = getMetaData(player, "placedRemoteTraps");
        if (obj != null && obj instanceof List) {
            placedBlocks = (List<Block>) obj;
        }
        return placedBlocks;
    }

    final private void removeRemoteTrap(final Block trap, final Player player, final String type) {
        if (type.equals("remoteTrap") && player != null && player.hasMetadata("placedRemoteTraps")) {
            final List<Block> placedTraps = findPlacedTraps(player);
            if (placedTraps != null && !placedTraps.isEmpty()) {
                placedTraps.remove(trap);
            }
            else {
                player.removeMetadata("placedRemoteTraps", plugin);
            }
        }
    }

    final private Player getPlacingPlayer(final Block block) {
        final String placingPlayer = (String) getMetaData(block, "Player");
        if (placingPlayer != null)
            return plugin.getServer().getPlayerExact(placingPlayer);
        return null;
    }

    final private Object getMetaData(final Object obj, final String key) {
        final List<MetadataValue> meta = obj instanceof Block ? ((Block) obj).getMetadata(key)
                                                             : ((Entity) obj).getMetadata(key);
        final String pluginName = plugin.getDescription().getName();
        for (final MetadataValue value : meta) {
            if (value.getOwningPlugin().getDescription().getName().equals(pluginName))
                return value.value();
        }
        return null;
    }

    final private void replaceTrapNames(final ItemStack stack) {
        if (stack != null && !stack.hasItemMeta()) {
            if (isRemotePlacingItem(stack)) {
                nameStack(stack, Traps.remoteTrapName, Traps.trapDescription);
            }
            else if (isProximityPlacingItem(stack)) {
                nameStack(stack, Traps.proximityTrapName, Traps.trapDescription);
            }
        }
    }

    final void nameStack(final ItemStack stack, final String name, final String desc) {
        final ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(name);
        if (desc != null) {
            meta.setLore(Arrays.asList(desc));
        }
        stack.setItemMeta(meta);
    }

    private final boolean isProximityPlacingItem(final ItemStack stack) {
        if (stack.getDurability() == proximityPlacingItemDurability
            && stack.getTypeId() == proximityPlacingItem)
            return true;
        return false;
    }

    private final boolean isRemotePlacingItem(final ItemStack stack) {
        if (stack.getDurability() == remotePlacingItemDurability
            && stack.getTypeId() == remotePlacingItem)
            return true;
        return false;
    }

    /*
     * @EventHandler public void hallo(EntityDamageEvent e) { if (e.getCause()
     * == DamageCause.BLOCK_EXPLOSION && e.getEntityType() == EntityType.PLAYER)
     * { ((Player) e.getEntity()).damage(e.getDamage(), ); } }
     */

    private void createCusExplosion(final World world, final Location loc, final float power,
                                    final boolean fire, final String player) {
        /*
         * final TNTPrimed tnt = (TNTPrimed) world.spawnEntity(loc,
         * EntityType.PRIMED_TNT); if (tnt != null) { tnt.setMetadata("trapper",
         * new FixedMetadataValue(plugin, player)); tnt.set tnt.setFuseTicks(0);
         * tnt.setIsIncendiary(fire); }
         */
        world.createExplosion(loc, power, fire);

        // TODO: adjust methods to get player by string
        // then convert to player if necessary
        // add non dmg explosion
    }

    private void setChannel(final Block block, final ItemStack channelStack, final Player player) {
        final String channel = channelStack.getItemMeta().getDisplayName();
        block.setMetadata("remoteTrap", new FixedMetadataValue(plugin, channel));
        player.sendMessage("Trap channel set to " + channel);
        // player.sendBlockChange(block.getLocation(), 35, (byte)
        // channelStack.getDurability());

    }
}
