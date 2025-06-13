package de.thiomains.jumpandrun;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;

public class JumpAndRun implements Listener {

    public static String prefix = "§8§l» §x§5§4§D§A§F§4J§x§5§4§C§F§E§Eu§x§5§4§C§3§E§9m§x§5§4§B§8§E§3p §x§5§4§A§2§D§8A§x§5§4§9§6§D§2n§x§5§4§8§B§C§Dd §x§5§4§7§5§C§1R§x§5§4§6§9§B§Cu§x§5§4§5§E§B§6n §r§8§l● §r§7";

    public JumpAndRun(Main main) {
        Bukkit.getPluginManager().registerEvents(this, main);
    }

    private ArrayList<JaRData> players = new ArrayList<>();

    public void startScheduler() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), new Runnable() {
            @Override
            public void run() {
                for (JaRData data : players) {
                    data.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§7Streak: §e" + data.getStreak()));
                }
            }
        }, 0, 20);
    }

    public void start(Player player) {
        if (!players.isEmpty()) {
            for (JaRData data : players) {
                if (data.getPlayer() == player) {
                    cancel(player);
                    return;
                }
            }
        }

        JaRData data = new JaRData(player);
        player.setAllowFlight(false);

        int randomX = (int) (Math.random() * 85)-40;
        int randomZ = (int) (Math.random() * 100)+25;
        Location start = new Location(player.getWorld(), randomX, 150, randomZ);
        Block startBlock = start.getBlock();
        data.setLastBlock(start);
        data.setCurrentBlock(start);
        data = placeNewBlock(data);
        players.add(data);
        player.teleport(start.clone().add(0.5, 1, 0.5));
        player.sendMessage(prefix + "Jump and Run §agestartet§7!");
    }

    public void cancel(Player player) {
        if (!players.isEmpty()) {
            for (JaRData data : players) {
                if (data.getPlayer() == player) {
                    data.getCurrentBlock().getBlock().setType(Material.AIR);
                    data.getCurrentBlock().clone().add(0, 1, 0).getBlock().setType(Material.AIR);
                    data.getLastBlock().getBlock().setType(Material.AIR);
                    data.getLastBlock().clone().add(0, 1, 0).getBlock().setType(Material.AIR);
                    players.remove(data);
                    player.setAllowFlight(true);
                    player.sendMessage(prefix + "Jump and Run §cabgebrochen§7!");
                    return;
                }
            }
        }

    }

    private JaRData placeNewBlock(JaRData data) {
        if (data.getLastBlock() != null) {
            data.getLastBlock().getBlock().setType(Material.AIR);
        }
        data.setLastBlock(data.getCurrentBlock());
        data.getLastBlock().getBlock().setType(Material.valueOf(data.getColor().name() + "_CONCRETE"));
        data.getLastBlock().clone().add(0, 1, 0).getBlock().setType(Material.AIR);

        Location lastLoc = data.getLastBlock();
        Location newBlock = lastLoc;
        while (lastLoc.distance(newBlock) < 2 || lastLoc.distance(newBlock) > 4.5 || !locationIsOk(newBlock)) {
            int randomX = lastLoc.getBlockX() + (int) (Math.random() * 8)-4;
            int randomY = lastLoc.getBlockY() + (int) (Math.random() * 3)-1;
            int randomZ = lastLoc.getBlockZ() + (int) (Math.random() * 8)-4;
            newBlock = new Location(data.getPlayer().getWorld(), randomX, randomY, randomZ);
        }
        Block block = newBlock.getBlock();
        block.setType(Material.valueOf(data.getColor().name() + "_STAINED_GLASS"));
        newBlock.clone().add(0, 1, 0).getBlock().setType(Material.LIGHT);
        data.setCurrentBlock(newBlock);
        data.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§7Streak: §e" + data.getStreak()));
        return data;
    }

    private boolean locationIsOk(Location loc) {

        if (loc.clone().getBlock().getType() != Material.AIR) {
            return false;
        } else if (loc.clone().add(0, 1, 0).getBlock().getType() != Material.AIR) {
            return false;
        } else if (loc.clone().add(0, 2, 0).getBlock().getType() != Material.AIR) {
            return false;
        }

        return true;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {

        for (int i = 0; i < players.size(); i++) {
            JaRData data = players.get(i);
            if (data.getPlayer() == event.getPlayer()) {
                Location targetLoc = data.getCurrentBlock();
                Location playerLoc = event.getTo();

                // Next Block
                if (playerLoc.getBlockX() == targetLoc.getBlockX() && playerLoc.getBlockY() -1 == targetLoc.getBlockY() && playerLoc.getBlockZ() == targetLoc.getBlockZ()) {
                    data.setStreak(data.getStreak() + 1);
                    JaRData newData = placeNewBlock(data);
                    players.remove(i);
                    i--;
                    players.add(newData);
                    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.1f, 2);

                    // Particle effects at every 100 jumps milestone
                    if (data.getStreak() % 100 == 0) {
                        Bukkit.getWorld("world").spawnParticle(Particle.END_ROD, playerLoc.getX(), playerLoc.getY(), playerLoc.getZ(), 1000, 0, 0, 0, 0.8, null, true);
                        Bukkit.getWorld("world").playSound(data.getPlayer(), Sound.ENTITY_FIREWORK_ROCKET_BLAST_FAR, 1000, 1);
                        for (Player allPlayers : Bukkit.getOnlinePlayers()) {
                            allPlayers.sendMessage(prefix + "§e" + data.getPlayer().getName() + " §7hat eine Streak von §e" + data.getStreak() + " §7erreicht!");
                        }
                    }

                    // Failing
                } else if (event.getPlayer().getLocation().getY() < data.getCurrentBlock().getY()) {
                    // event.getPlayer().teleport(new Location(Bukkit.getWorld("world"), 0.5, 51, 0.5));
                    data.getCurrentBlock().getBlock().setType(Material.AIR);
                    data.getCurrentBlock().clone().add(0, 1, 0).getBlock().setType(Material.AIR);
                    data.getLastBlock().getBlock().setType(Material.AIR);
                    event.getPlayer().setAllowFlight(true);
                    String text = "";
                    if (data.getStreak() == 1) {
                        text = "Block";
                    } else {
                        text = "Blöcke";
                    }
                    event.getPlayer().sendMessage(prefix + "§7Du hast §e" + data.getStreak() + " §7" + text + " geschafft!");
                    players.remove(i);
                    i--;
                    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        for (int i = 0; i < players.size(); i++) {
            JaRData data = players.get(i);
            if (data.getPlayer() == event.getPlayer()) {
                data.getCurrentBlock().getBlock().setType(Material.AIR);
                data.getLastBlock().getBlock().setType(Material.AIR);
                players.remove(i);
                i--;
            }
        }
    }

}

