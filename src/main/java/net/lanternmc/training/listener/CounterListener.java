package net.lanternmc.training.listener;

import net.lanternmc.training.Counter;
import net.lanternmc.training.Training;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import net.lanternmc.training.utils.ActionBarUtils;
import net.lanternmc.training.utils.TitleUtils;

public class CounterListener implements Listener {
    @EventHandler
    public void onBreakBlock(BlockBreakEvent e) {
        if (e.getPlayer() != null) if (!Training.isPlacedByPlayer(e.getBlock())) {
            if (e.getPlayer().getGameMode() == GameMode.CREATIVE) return;
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {

        if (e.getAction().toString().contains("CLICK")) {
            if (e.getAction() == Action.LEFT_CLICK_BLOCK) if (e.isCancelled()) return;
            Counter c = Training.getCounter(e.getPlayer());
            c.countCPS();
            if (!c.isSpeedCountEnabled()) return;
            ActionBarUtils.sendActionBar(e.getPlayer(),
                    "§c§l最大CPS - " + c.getMaxCPS() + " §d§l当前CPS - " + c.getCPS() + " §a§l| §c§l最远距离 - " + c.getMaxBridgeLength() + " §d§l当前距离 - " + c.getBridgeLength());
        }
    }

    @EventHandler
    public void onFallDown(PlayerMoveEvent e) {
        if (e.getTo().getY() < 0) {
            Counter c = Training.getCounter(e.getPlayer());
            if (c.isSpeedCountEnabled()) {
                TitleUtils.sendTitle(e.getPlayer(), "", "§cMax - " + c.getMaxBridgeSpeed() + " block/s", 1, 40, 1);
            }
            c.reset();
            Training.teleportCheckPoint(e.getPlayer());
        }
    }

    @EventHandler
    public void onLiqudFlow(BlockFromToEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent e) {
        if (e.isCancelled()) return;
        if (e.getPlayer() != null) {
            if (e.getPlayer().getGameMode() == GameMode.CREATIVE) return;
            Counter c = Training.getCounter(e.getPlayer());
            c.countBridge(e.getBlock());
            if (c.isSpeedCountEnabled()) {
                TitleUtils.sendTitle(e.getPlayer(), "", "§b" + c.getBridgeSpeed() + " block/s", 1, 40, 1);
            }
            Bukkit.getScheduler().runTaskLater(Training.getInstance(), () ->
                    e.getPlayer().getInventory().addItem(new ItemStack(e.getPlayer().getItemInHand().getType(), 1, (short) 0, e.getPlayer().getItemInHand().getData().getData())), 1);
        }
    }

    @EventHandler
    public void onPlaceLiqud(PlayerBucketEmptyEvent e) {
        if (e.isCancelled()) return;
        if (e.getPlayer() != null) {
            if (e.getPlayer().getGameMode() == GameMode.CREATIVE) return;
            Counter c = Training.getCounter(e.getPlayer());
            c.addLogBlock(e.getBlockClicked().getRelative(e.getBlockFace()));
            Bukkit.getScheduler().runTaskLater(Training.getInstance(), () -> e.getPlayer().getInventory().remove(Material.BUCKET), 1);
        }
    }
}
