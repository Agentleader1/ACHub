package alohacraft.achub.main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

	public final Logger logger = Logger.getLogger("Minecraft");
	HashMap<String, Boolean> pvp = new HashMap<String, Boolean>();

	@Override
	public void onDisable() {
		logger.info("ACHub is now deaf!");
	}

	@Override
	public void onEnable() {
		logger.info("ACHub Started Listening!");
		if (!new File(this.getDataFolder(), "config.yml").exists()) {
			this.saveDefaultConfig();
		}
		getServer().getPluginManager().registerEvents(this, this);
		dayTimer();
	}
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e){
		Player player = (Player) e.getPlayer();
		pvp.put(player.getName(), false);
		List<String> s = new ArrayList<String>();
		s.add("PvP: Disabled");
		ItemStack pvptoggle = new ItemStack(Material.ARROW);
		ItemMeta pvptogglemeta = pvptoggle.getItemMeta();
		pvptogglemeta.setDisplayName(ChatColor.DARK_RED + "Toggle PvP");
		pvptogglemeta.setLore(s);
		pvptoggle.setItemMeta(pvptogglemeta);
		player.getInventory().clear();
		player.getInventory().setHelmet(new ItemStack(0));
		player.getInventory().setChestplate(new ItemStack(0));
		player.getInventory().setLeggings(new ItemStack(0));
		player.getInventory().setBoots(new ItemStack(0));
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "kit starter " + player.getName());
		player.getInventory().addItem(pvptoggle);
		player.updateInventory();
	}
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR)
	public void OnPlayerJoin(PlayerJoinEvent event) {
		Player p = (Player) event.getPlayer();
		p.teleport(getServer().getWorld("world").getSpawnLocation());
		pvp.put(p.getName(), false);
		List<String> s = new ArrayList<String>();
		s.add("PvP: Disabled");
		ItemStack pvptoggle = new ItemStack(Material.ARROW);
		ItemMeta pvptogglemeta = pvptoggle.getItemMeta();
		pvptogglemeta.setDisplayName(ChatColor.DARK_RED + "Toggle PvP");
		pvptogglemeta.setLore(s);
		pvptoggle.setItemMeta(pvptogglemeta);
		
		List<String> x = new ArrayList<String>();
		x.add(ChatColor.DARK_RED + "Right Click to Fire Weapon.");
		x.add(ChatColor.YELLOW + "Shoot people with snowballs!");
		ItemStack snowgun = new ItemStack(Material.IRON_HOE);
		ItemMeta snowgunmeta = snowgun.getItemMeta();
		snowgunmeta.setDisplayName(ChatColor.AQUA + "SnowGun");
		snowgunmeta.setLore(x);
		snowgun.setItemMeta(snowgunmeta);
		
		p.getInventory().clear();
		p.getInventory().setHelmet(new ItemStack(0));
		p.getInventory().setChestplate(new ItemStack(0));
		p.getInventory().setLeggings(new ItemStack(0));
		p.getInventory().setBoots(new ItemStack(0));
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "kit starter " + p.getName());
		p.getInventory().addItem(pvptoggle);
		p.getInventory().addItem(snowgun);
		p.updateInventory();
	}
	@EventHandler
	public void noHunger(FoodLevelChangeEvent event){
		if (event.getEntity() instanceof Player) {
			Player player = (Player)event.getEntity();
			if (player.getWorld().getName().equalsIgnoreCase("world")) {
				event.setFoodLevel(20);
			}
		}
	}
	@EventHandler
	public void noDrop(PlayerDeathEvent event){
		event.getDrops().clear();
		String killer = event.getEntity().getKiller().toString();
		String player = event.getEntity().getName().toString();
		Bukkit.broadcastMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "Hub" + ChatColor.DARK_GRAY + "]: " + ChatColor.GRAY + "Player " + ChatColor.DARK_RED + killer + ChatColor.GRAY + " has finished " + ChatColor.DARK_RED + player + ChatColor.GRAY + "!");
	}
	@EventHandler
	public void hurtVoid(EntityDamageEvent e) {
		if(e.getCause() == EntityDamageEvent.DamageCause.VOID) {
			if (e.getEntity() instanceof Player) {
				Player p = (Player)e.getEntity();
				if(p.getLocation().getWorld().getName().equals(getConfig().getString("void"))) {
					p.setHealth(20.0);
					p.setFallDistance(0.0F);
					p.teleport(p.getWorld().getSpawnLocation());
					p.sendMessage(ChatColor.GREEN + "Hey You! Get back on this earth!");
				}
			}
		}
	}
	@EventHandler
	public void pvp(EntityDamageByEntityEvent e) {
		Entity entity = (Entity) e.getEntity();
		Entity hitter = (Entity) e.getDamager();
		//Physical
		if (hitter instanceof Player) {
			if (entity instanceof Player) {
				Player player = (Player) entity;
				Player damager = (Player) hitter;
				if (damager.isFlying() == true) {
					e.setCancelled(true);
					damager.setFlying(false);
					damager.sendMessage(ChatColor.RED + "You can't pvp in fly, that's really unfair!");
				} else if((pvp.get(player.getName()) == true) && (pvp.get(damager.getName()) == true)) {	
				} else {
					e.setCancelled(true);
				}
			}
		} else if (e.getDamager() instanceof Snowball) {
			Entity shooter = ((Snowball) e.getDamager()).getShooter(); //get the shooter
			if (shooter instanceof Player) {
				if (entity instanceof Player) {
					Player player = (Player) entity;
					Player damager = (Player) shooter;
					player.sendMessage(ChatColor.AQUA + damager.getName() + " has tagged you with his snowgun!" );
					damager.sendMessage(ChatColor.AQUA + "You have shot " + player.getName() + "!");
				}
			}
		}
	}
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		if ((e.getAction() == Action.RIGHT_CLICK_AIR) || (e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			Player player = (Player) e.getPlayer();
			if (player.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.DARK_RED + "Toggle PvP")) { //ENABLING PVP
				if(pvp.get(player.getName()) == false) {
					//Disables PvP
					List<String> s = new ArrayList<String>();
					s.add("PvP: Enabled");
					ItemStack pvptoggle = new ItemStack(Material.ARROW);
					ItemMeta pvptogglemeta = pvptoggle.getItemMeta();
					pvptogglemeta.setDisplayName(ChatColor.RED + "Toggle PvP");
					pvptogglemeta.setLore(s);
					pvptoggle.setItemMeta(pvptogglemeta);
					//Enables PvP
					List<String> ss = new ArrayList<String>();
					ss.add("PvP: Disabled");
					ItemStack pvpptoggle = new ItemStack(Material.ARROW);
					ItemMeta pvpptogglemeta = pvpptoggle.getItemMeta();
					pvpptogglemeta.setDisplayName(ChatColor.DARK_RED + "Toggle PvP");
					pvpptogglemeta.setLore(ss);
					pvpptoggle.setItemMeta(pvpptogglemeta);

					pvp.put(player.getName(), true);
					player.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET, 1));
					player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE, 1));
					player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS, 1));
					player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS, 1));
					player.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD, 1));
					if(player.getInventory().contains(pvpptoggle)) {
						player.getInventory().remove(pvpptoggle);
					}
					player.getInventory().addItem(pvptoggle);
					player.sendMessage(ChatColor.RED + "PvP Enabled!");
				} else {
					player.sendMessage(ChatColor.RED + "You need to wait til your pvp is disabled!");
				}
					player.updateInventory();
			} else if (player.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.RED + "Toggle PvP")) { //DISABLING PVP
				player.sendMessage(ChatColor.GREEN + "PvP is disabling in 3 seconds!");
				tpDelay(player);
			} else if (player.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.AQUA + "SnowGun")) {
				player.launchProjectile(Snowball.class);
				player.playSound(player.getLocation(), Sound.CAT_MEOW, 1, 1);;
			}
		}
	}
	public void dayTimer() {
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				getServer().getWorld("world").setTime(0L);
			}
		}, 0, 300 * 20);
	}
	@SuppressWarnings("deprecation")
	private void tpDelay(final Player player){
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			public void run() {
				player.getInventory().setHelmet(new ItemStack(0));
				player.getInventory().setChestplate(new ItemStack(0));
				player.getInventory().setLeggings(new ItemStack(0));
				player.getInventory().setBoots(new ItemStack(0));
				//Enables PvP
				List<String> s = new ArrayList<String>();
				s.add("PvP: Disabled");
				ItemStack pvptoggle = new ItemStack(Material.ARROW);
				ItemMeta pvptogglemeta = pvptoggle.getItemMeta();
				pvptogglemeta.setDisplayName(ChatColor.DARK_RED + "Toggle PvP");
				pvptogglemeta.setLore(s);
				pvptoggle.setItemMeta(pvptogglemeta);
				//Disables PvP
				List<String> ss = new ArrayList<String>();
				ss.add("PvP: Enabled");
				ItemStack pvpptoggle = new ItemStack(Material.ARROW);
				ItemMeta pvpptogglemeta = pvpptoggle.getItemMeta();
				pvpptogglemeta.setDisplayName(ChatColor.RED + "Toggle PvP");
				pvpptogglemeta.setLore(ss);
				pvpptoggle.setItemMeta(pvpptogglemeta);
				pvp.put(player.getName(), false);
				if(player.getInventory().contains(pvpptoggle)) {
					player.getInventory().remove(pvpptoggle);
				}
				if(player.getInventory().contains(new ItemStack(Material.DIAMOND_SWORD, 1))) {
					player.getInventory().remove(new ItemStack(Material.DIAMOND_SWORD, 1));
				}
				player.getInventory().addItem(pvptoggle);
				player.sendMessage(ChatColor.GREEN + "PvP Disabled!");
				player.updateInventory();
			}
		}, 3 * 20L);
	}
}
