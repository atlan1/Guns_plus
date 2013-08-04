package team.GunsPlus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.item.GenericCustomItem;
import org.getspout.spoutapi.player.SpoutPlayer;

import team.ApiPlus.API.Property.*;
import team.GunsPlus.Block.TripodData;
import team.GunsPlus.Gui.HUD;
import team.GunsPlus.Gui.TripodPopup;
import team.GunsPlus.Item.Gun;
import team.GunsPlus.Manager.TripodDataHandler;
import team.GunsPlus.Util.GunUtils;
import team.GunsPlus.Util.PlayerUtils;
import team.GunsPlus.Util.Util;

public class GunsPlusListener implements Listener {

	public GunsPlus plugin;
	public static String credit;

	public GunsPlusListener(GunsPlus instance) {
		plugin = instance;
	}

	@EventHandler
	public void onButtonInteract(ButtonClickEvent e) {
		Button b = e.getButton();
		SpoutPlayer sp = e.getPlayer();
		if(b.getPlugin().equals(plugin) && sp.getMainScreen().getActivePopup() instanceof TripodPopup) {
			TripodPopup tpp = (TripodPopup) sp.getMainScreen().getActivePopup();
			if(tpp.getId("MANU").equals(b.getId())) {
				tpp.setChooserDisabled();
			} else if(tpp.getId("AUTO").equals(b.getId())) {
				tpp.setChooserEnabled();
			} else if(tpp.getId("APPLY").equals(b.getId())) {
				tpp.applyData();
				tpp.close();
			} else if(tpp.getId("ADD").equals(b.getId()) && tpp.getMode() == -1) {
				tpp.setAddMode();
				tpp.setTargetChooser();
			} else if(tpp.getId("OK").equals(b.getId()) && tpp.getMode() != -1) {
				tpp.removeTargetChooser();
				tpp.removePlayerChooser();
				tpp.performListAction();
			} else if(tpp.getId("DEL").equals(b.getId()) && tpp.getMode() == -1) {
				tpp.setDelMode();
				tpp.setTargetChooser();
			} else if(tpp.getId("EDIT").equals(b.getId()) && tpp.getMode() == -1) {
				tpp.setEditMode();
				tpp.setTargetChooser();
			}
			tpp.getWidget(tpp.getId("LIST")).setDirty(true);
		}
		b.setDirty(true);
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		Action a = e.getAction();
		if(!PlayerUtils.hasSpoutcraft(p))
			return;
		SpoutPlayer sp;
		if(GunsPlus.mrb != null) {
			sp = (SpoutPlayer) Bukkit.getPlayer(p.getName());
		} else {
			sp = (SpoutPlayer) p;
		}
		GunsPlusPlayer gp = PlayerUtils.getPlayerBySpoutPlayer(sp);
		if(gp == null)
			return;
		switch(a){
		case RIGHT_CLICK_BLOCK:
			if(Util.isTripod(e.getClickedBlock())) {
				TripodData td = Util.loadTripodData(e.getClickedBlock());
				e.setUseInteractedBlock(Result.DENY);
				if(td.getOwner() != null && td.getOwner().equals(gp)) {
					if(td.getGun() == null && sp.isSneaking() && GunUtils.holdsGun(sp)) {
						Gun tg = GunUtils.getGunInHand(sp);
						if(GunUtils.isMountable(tg)) {
							td.setGun(tg);
							GunUtils.removeGunInHand(sp);
						} else {
							PlayerUtils.sendNotification(sp, "You can't mount a", GunUtils.getRawGunName(tg), new SpoutItemStack((GenericCustomItem) tg), 2000);
						}
					} else if(td.getGun() != null && sp.isSneaking()) {
						TripodPopup tpp = new TripodPopup(plugin, td);
						tpp.attach(sp);
					} else if(td.getGun() != null) {
						sp.openInventory(td.getInventory());
					}
				}
			}
			break;
		case LEFT_CLICK_BLOCK:
			if(Util.isTripod(e.getClickedBlock())) {
				TripodData td = Util.loadTripodData(e.getClickedBlock().getLocation());
				if(td.getOwnername().equals(gp.getPlayer().getName()) && td.getGun() != null) {
					if(!td.isAutomatic() && !td.isEntered()) {
						td.setEntered(true);
					} else if(!td.isAutomatic() && td.isEntered()) {
						td.setEntered(false);
					} else if(td.isAutomatic() && !td.isWorking()) {
						td.setWorking(true);
					} else if(td.isAutomatic() && td.isWorking()) {
						td.setWorking(false);
					}
				}
			}
			break;
		}
	}

	@EventHandler
	public void onHeldItemChange(PlayerItemHeldEvent e) {
		Player p = e.getPlayer();
		if(!PlayerUtils.hasSpoutcraft(p))
			return;
		SpoutPlayer sp = (SpoutPlayer) p;
		ItemStack preItem = p.getInventory().getItem(e.getPreviousSlot());
		ItemStack nextItem = p.getInventory().getItem(e.getNewSlot());
		if(Util.enteredTripod(sp)) {
			sp.getInventory().setItem(e.getNewSlot(), preItem);
			sp.getInventory().setItem(e.getPreviousSlot(), null);
			return;
		} else if(PlayerUtils.getPlayerBySpoutPlayer(sp).isZooming()) {
			PlayerUtils.getPlayerBySpoutPlayer(sp).setZooming(false);
			GunUtils.zoomOut(PlayerUtils.getPlayerBySpoutPlayer(sp));
		}

		if(GunUtils.isGun(preItem)) {
			sp.setWalkingMultiplier(1);
		}
		if(GunUtils.isGun(nextItem)) {
			Gun g = GunUtils.getGun(nextItem);
			sp.setWalkingMultiplier(1 - (((NumberProperty) g.getProperty("WEIGHT")).getValue().intValue() / 100));
		}
	}

	// make guns unstackable 
	@EventHandler(ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		ItemStack clicked = event.getCurrentItem();
		ItemStack cursor = event.getCursor();
		Player p = (Player) event.getView().getPlayer();
		if(Util.enteredTripod((SpoutPlayer) p)) {
			event.setCancelled(true);
		}
		if(clicked != null && GunUtils.isGun(clicked)) {
			if(cursor != null && GunUtils.isGun(cursor) && event.isLeftClick() && !event.isShiftClick() && clicked.getDurability() == cursor.getDurability()) {
				event.setCancelled(true);
			} else if(cursor != null && GunUtils.isGun(cursor) && event.isRightClick() && !event.isShiftClick() && clicked.getDurability() == cursor.getDurability()) {
				event.setCancelled(true);
			} else if(event.isShiftClick()) {
				event.setCancelled(true);
				Inventory main = event.getView().getBottomInventory();
				Inventory top = event.getView().getTopInventory();
				if(top.getType() == InventoryType.CHEST) {
					if(event.getRawSlot() < top.getSize()) {
						int slot = main.firstEmpty();
						if(slot >= 0) {
							main.setItem(slot, event.getCurrentItem().clone());
							top.setItem(event.getSlot(), null);
						}
					} else {
						int slot = top.firstEmpty();
						if(slot >= 0) {
							top.setItem(slot, event.getCurrentItem().clone());
							main.setItem(event.getSlot(), null);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent e) {
		Player p = e.getPlayer();
		ItemStack i = e.getItemDrop().getItemStack();

		if(PlayerUtils.hasSpoutcraft(p)) {
			SpoutPlayer sp = (SpoutPlayer) p;
			if(GunUtils.isGun(i)) {
				sp.setWalkingMultiplier(1.0);
			}
			if(Util.enteredTripod(sp)) {
				e.setCancelled(true);
				sp.setItemInHand(i);
			}
		}
	}

	@EventHandler
	public void onPickupItem(PlayerPickupItemEvent e) {
		Player p = e.getPlayer();
		Item i = e.getItem();
		ItemStack itemstack = i.getItemStack();
		if(!PlayerUtils.hasSpoutcraft(p))
			return;
		SpoutPlayer sp = (SpoutPlayer) p;
		// if the player entered a tripod put the stack in his normal inventory
		// (stored in the tripoddata)
		if(Util.enteredTripod(sp)) {
			e.setCancelled(true);
			i.remove();
			TripodData td = Util.getTripodDataOfEntered(sp);
			td.getOwnerInventory().setItem(td.getOwnerInventory().firstEmpty(), itemstack);
		}
		// make sure that the guns will not be stacked on pickup
		if(GunUtils.isGun(itemstack) && GunUtils.checkInvForGun(sp.getInventory(), GunUtils.getGun(itemstack))) {
			if(GunsPlus.showcase != null && !GunsPlus.showcase.isShowCaseItem(i)) {
				e.setCancelled(true);
				if(sp.getInventory().firstEmpty() >= 0) {
					sp.getInventory().setItem(sp.getInventory().firstEmpty(), itemstack);
					i.remove();
				}
			} else if(GunsPlus.showcase == null) {
				e.setCancelled(true);
				if(sp.getInventory().firstEmpty() >= 0) {
					sp.getInventory().setItem(sp.getInventory().firstEmpty(), itemstack);
					i.remove();
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityExplode(EntityExplodeEvent e) {
		if(e.getEntity() != null && (e.getEntityType().equals(EntityType.FIREBALL) && ((Fireball) e.getEntity()).getShooter() == null)) {
			Fireball fireball = (Fireball) e.getEntity();
			fireball.setIsIncendiary(false);
			e.setCancelled(true);
			e.setYield(0);
		}
		for(Block b : e.blockList()) {
			if(Util.isTripod(b)) {
				Location l = b.getLocation();
				TripodData td = Util.loadTripodData(l);
				if(td.isEntered()) {
					td.setEntered(false);
				}
				td.destroy();
				td.dropContents();
				TripodDataHandler.removeId(TripodDataHandler.getId(td.getLocation()));
				GunsPlus.allTripodBlocks.remove(td);
			}
		}
	}

	@EventHandler
	public void onBlockDestroyed(BlockBreakEvent e) {
		if(!Util.isTripod(e.getBlock()))
			return;
		Location l = e.getBlock().getLocation();
		TripodData td = Util.loadTripodData(l);
		if(td.isEntered()) {
			td.setEntered(false);
		}
		td.destroy();
		td.dropContents();
		TripodDataHandler.removeId(TripodDataHandler.getId(td.getLocation()));
		GunsPlus.allTripodBlocks.remove(td);
	}

	@EventHandler
	public void onSpoutCraftEnable(SpoutCraftEnableEvent e) {
		SpoutPlayer sp = e.getPlayer();
		HUD hud = new HUD(plugin, GunsPlus.hudX, GunsPlus.hudY, GunsPlus.hudBackground);
		GunsPlusPlayer gp = new GunsPlusPlayer(sp, hud);
		GunsPlus.GunsPlusPlayers.add(gp);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		if(PlayerUtils.hasSpoutcraft(e.getPlayer()) && PlayerUtils.getPlayerBySpoutPlayer((SpoutPlayer) e.getPlayer()) != null) {
			GunsPlusPlayer gp = PlayerUtils.getPlayerBySpoutPlayer((SpoutPlayer) e.getPlayer());
			if(Util.enteredTripod(gp.getPlayer())) {
				Util.getTripodDataOfEntered(gp.getPlayer()).setEntered(false);
			}
			for(TripodData td : GunsPlus.allTripodBlocks) {
				if(td != null) {
					if(td.getOwnername().equals(gp.getPlayer().getName())) {
						TripodDataHandler.save(td);
					}
				}
			}
			GunsPlus.GunsPlusPlayers.remove(gp);
		}

	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		creditsDelayed(p);
	}

	// @EventHandler
	// public void onCraft(CraftItemEvent event) {
	// org.getspout.spoutapi.material.Material is = new
	// SpoutItemStack(event.getRecipe().getResult()).getMaterial();
	// if(Util.isGunsPlusMaterial(is.getName())&&GunsPlus.useperms) {
	// if(!event.getWhoClicked().hasPermission("gunsplus.craft.all")) {
	// Object g = Util.getGunsPlusMaterial(is.getName());
	// if(!event.getWhoClicked().hasPermission("gunsplus.craft." + ((CustomItem)
	// g).getName().toLowerCase().replace(" ", "_")))
	// event.setResult(Result.DENY);
	// }
	// }
	// }

	public void credits(Player p) {
		credit = ("This server is running " + ChatColor.GOLD + "Guns+" + ChatColor.DARK_GREEN + " By:" + plugin.getDescription().getAuthors());
		if(PlayerUtils.hasSpoutcraft(p))
			((SpoutPlayer) p).sendNotification(ChatColor.GRAY + "Guns+", ChatColor.DARK_GREEN + "By " + plugin.getDescription().getAuthors(), Material.SULPHUR);
		else
			p.sendMessage(credit);
	}

	private void creditsDelayed(final Player p) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				credits(p);
			}
		}, 100L);
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if(event.getDamager() instanceof Player) {
			Player attacker = (Player) event.getDamager();
			org.getspout.spoutapi.material.Material is = new SpoutItemStack(attacker.getItemInHand()).getMaterial();
			if(Util.isGunsPlusMaterial(is.getName())) {
				Object g = Util.getGunsPlusMaterial(is.getName());
				if(g instanceof Gun && event.getCause().equals(DamageCause.ENTITY_ATTACK)) {
					if(!PlayerUtils.getPlayerBySpoutPlayer((SpoutPlayer) attacker).isFireing()) {
						event.setDamage(((NumberProperty) ((Gun) g).getProperty("MELEE")).getValue().intValue());
					}
				}
			}
		}
	}
}
