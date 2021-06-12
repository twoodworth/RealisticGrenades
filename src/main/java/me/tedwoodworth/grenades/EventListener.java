package me.tedwoodworth.grenades;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class EventListener implements Listener {
    private final Set<Player> grenadeDropList = new HashSet<>();
    public Set<Item> grenadeSet = new HashSet<>();
    private final ItemManager manager = ItemManager.getInstance();
    private final RealisticGrenades plugin = RealisticGrenades.getInstance();
    private final ItemStack air = new ItemStack(Material.AIR);

    public Player getGrenadeThrower(Item item) {
        var stack = item.getItemStack();
        var meta = stack.getItemMeta();
        var container = meta.getPersistentDataContainer();
        var strUUID = container.get(Constants.THROWER_KEY, PersistentDataType.STRING);
        var uuid = UUID.fromString(strUUID);
        return Bukkit.getPlayer(uuid);
    }

    public void setGrenadeThrower(Item item, Player player) {
        var stack = item.getItemStack();
        var meta = stack.getItemMeta();
        var container = meta.getPersistentDataContainer();
        container.set(Constants.THROWER_KEY, PersistentDataType.STRING, player.getUniqueId().toString());
        stack.setItemMeta(meta);
        item.setItemStack(stack);
    }

    private Location getNextLocation(Item item) {
        var location = item.getLocation();
        var velocity = item.getVelocity();
        return new Location(location.getWorld(), location.getX() + velocity.getX(), location.getY() + velocity.getY(), location.getZ() + velocity.getZ());
    }

    private boolean entityCollision(Item item, Location location, Location nextLocation, Player player, long remainingTime, long initialTime) {
        var xDist = Math.abs(location.getX() - nextLocation.getX());
        var yDist = Math.abs(location.getY() - nextLocation.getY());
        var zDist = Math.abs(location.getZ() - nextLocation.getZ());

        var nearby = item.getNearbyEntities(xDist, yDist, zDist);
        if (nearby.size() == 0) return false;
        else {
            var x = location.getX();
            var y = location.getY();
            var z = location.getZ();
            var v = item.getVelocity();
            var vX = v.getX();
            var vY = v.getY();
            var vZ = v.getZ();
            for (int i = 1; i <= 10; i++) {
                x += vX / 10.0;
                y += vY / 10.0;
                z += vZ / 10.0;
                for (var entity : nearby) {
                    if (initialTime - remainingTime < 30L && entity.equals(player)) continue;
                    var box = entity.getBoundingBox();
                    if (box.contains(x, y, z)) {
                        var bX = Math.min(
                                Math.abs(x - box.getMinX()),
                                Math.abs(x - box.getMaxX())
                        );
                        var bY = Math.min(
                                Math.abs(y - box.getMinY()),
                                Math.abs(y - box.getMaxY())
                        );
                        var bZ = Math.min(
                                Math.abs(z - box.getMinZ()),
                                Math.abs(z - box.getMaxZ())
                        );
                        var b = new double[3];
                        b[0] = bX;
                        b[1] = bY;
                        b[2] = bZ;

                        var vLength = item.getVelocity().length();
                        Arrays.sort(b);
                        if (b[0] == bX) v.setX(-1 * v.getX());
                        else if (b[0] == bY) v.setY(-1 * v.getY());
                        else v.setZ(-1 * v.getZ());


                        if ((entity instanceof LivingEntity && !(entity instanceof Bee))
                                || entity instanceof EnderCrystal
                                || entity instanceof ArmorStand) {
                            v.add(entity.getVelocity());
                            v.setX(0.15 * v.getX());
                            v.setY(0.15 * v.getY());
                            v.setZ(0.15 * v.getZ());
                        } else if (
                                entity instanceof Item
                                        || entity instanceof Explosive
                                        || entity instanceof Projectile
                                        || entity instanceof Bee
                                        || entity instanceof FallingBlock) {
                            var half = v.clone();
                            half.setX(v.getX() / 2);
                            half.setY(v.getY() / 2);
                            half.setZ(v.getZ() / 2);

                            var eV = entity.getVelocity();
                            var half2 = eV.clone();
                            half2.setX(eV.getX() / 2);
                            half2.setY(eV.getY() / 2);
                            half2.setZ(eV.getZ() / 2);
                            v.add(half2);
                            eV.add(half);
                            entity.setVelocity(eV);

                        } else if (
                                entity instanceof Minecart
                                        || entity instanceof Boat) {
                            v.add(entity.getVelocity());
                        }

                        item.setVelocity(v);

                        if (vLength > 0.05) {
                            item.getWorld().playSound(item.getLocation(), Sound.BLOCK_CHAIN_STEP, 1, 1);
                        }

                        if (entity instanceof LivingEntity && vLength > 0.25) {
                            player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
                            ((LivingEntity) entity).damage(1);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean createFireExplosion(Location location, float radius, Entity source) {
        var event = new EntityExplodeEvent(source, location, new ArrayList<>(), radius);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {

            location.getWorld().playSound(location, Sound.BLOCK_GLASS_BREAK, 1, 1);
            location.getWorld().playSound(location, Sound.ITEM_FIRECHARGE_USE, 1, 1);
            for (int i = 0; i < 30; i++) {
                location.getWorld().spawnParticle(Particle.LAVA, location, 3, Math.random(), Math.random(), Math.random());
            }
            int delay = 0;
            for (double h = radius / 5.0; h <= radius; h += radius / 5.0) {
                var nearby = source.getNearbyEntities(h, h, h);
                for (var entity : nearby) {
                    if (entity instanceof LivingEntity) {
                        (entity).setFireTicks((int) Math.max(0, 20 * (Math.pow(h, 2) - location.distanceSquared(entity.getLocation()))));
                    }
                }
                double finalH = h;
                Bukkit.getScheduler().runTaskLater(RealisticGrenades.getInstance(), () -> {
                    var volume = (4.0 / 3.0 * Math.PI * Math.pow(finalH, 3.0)) * 0.25;
                    var volumeWhole = (int) volume;
                    var volumeRemainer = volume - volumeWhole;
                    if (Math.random() < volumeRemainer) volumeWhole++;
                    var x = location.getX();
                    var y = location.getY();
                    var z = location.getZ();
                    var radiusWhole = (int) finalH;
                    var remainder = finalH - radiusWhole;
                    for (int i = 0; i < volumeWhole; i++) {
                        var r = radiusWhole;
                        if (Math.random() < remainder) r++;
                        var tempX = x + Math.random() * 2 * r - r;
                        var tempY = y + Math.random() * 2 * r - r;
                        var tempZ = z + Math.random() * 2 * r - r;

                        var nLoc = new Location(location.getWorld(), tempX, tempY, tempZ);
                        var block = nLoc.getBlock();
                        if (!block.getType().isAir()) continue;
                        location.getWorld().spawnParticle(Particle.LAVA, nLoc, 2, Math.random(), Math.random(), Math.random());
                        int j = 0;
                        while (j < r && block.getLocation().getY() > 0 && block.getRelative(BlockFace.DOWN).getType().isAir()) {
                            block = block.getRelative(BlockFace.DOWN);
                            j++;
                        }
                        block.setType(Material.FIRE);
                    }
                    location.getWorld().playSound(location, Sound.BLOCK_FIRE_AMBIENT, 1, 1);
                }, delay);
                delay += 5L;
            }
            return true;
        }
        return false;
    }

    private void explodeGrenade(Item item) {
        var stack = item.getItemStack();
        var blastRadius = manager.getBlastRadius(stack);
        var fireRadius = manager.getFireRadius(stack);
        var destructionRadius = manager.getDestructionRadius(stack);
        var smokeRadius = manager.getSmokeRadius(stack);
        var primeEvent = new ExplosionPrimeEvent(item, Math.max(Math.max(Math.max(blastRadius, fireRadius), destructionRadius), smokeRadius), fireRadius > 0F);
        Bukkit.getPluginManager().callEvent(primeEvent);
        if (!primeEvent.isCancelled()) {
            blastRadius = Math.min(blastRadius, primeEvent.getRadius());
            fireRadius = Math.min(fireRadius, primeEvent.getRadius());
            destructionRadius = Math.min(destructionRadius, primeEvent.getRadius());
            smokeRadius = Math.min(smokeRadius, primeEvent.getRadius());
            if (blastRadius > 0F)
                item.getWorld().createExplosion(item.getLocation(), blastRadius, false, false, item);
            if (destructionRadius > 0F)
                item.getWorld().createExplosion(item.getLocation(), destructionRadius, false, true, item);
            if (fireRadius > 0F) {
                createFireExplosion(item.getLocation(), fireRadius, item);
            }
//              if (smokeRadius > 0F) item.getWorld().createExplosion(item.getLocation(), smokeRadius, true, false, item); todo smoke
        }
        grenadeSet.remove(item);
        item.remove();
    }

    private void grenadeTick(Item item) {
        if (!grenadeSet.contains(item)) return;
        var stack = item.getItemStack();
        var remainingTime = manager.getRemainingTime(stack);
        var remainingDespawnTime = manager.getRemainingDespawnTime(stack);
        var initialTime = manager.getInitialTime(stack);
        var player = getGrenadeThrower(item);
        if (remainingTime == 0) {
            explodeGrenade(item);
            return;
        } else if (remainingDespawnTime == 0) {
            grenadeSet.remove(item);
            item.remove();
            return;
        }
        var location = item.getLocation();
        var location2 = location.clone();
        var velocity = item.getVelocity();
        location2.setY(item.getLocation().getY() + 0.25);
        if (manager.hasSmokeTrail(stack) && remainingTime % 2 == 0 && initialTime - remainingTime > 2) {
            item.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, location2, 1, 0.05, 0.05, 0.05, 0);
        }

        var nextLocation = getNextLocation(item);
        if (location.getBlock().isLiquid()) {
            velocity.setX(velocity.getX() * 0.5);
            velocity.setY(velocity.getY() * 0.5);
            velocity.setY(velocity.getY() * 0.5);
        }

        if (!entityCollision(item, location, nextLocation, player, remainingTime, initialTime)) {
            var vLength = velocity.length();

            // X-axis check
            var magnitude = Math.abs(nextLocation.getBlockX() - location.getBlockX());
            if (velocity.getX() > 0
                    && nextLocation.getBlockX() - location.getBlockX() >= 1) {
                var colliding = true;
                for (int i = 1; i <= magnitude; i++) {
                    if (item.getWorld().getBlockAt(location.getBlockX() + i, location.getBlockY(), location.getBlockZ()).isPassable()) {
                        colliding = false;
                        break;
                    }
                }
                if (colliding)
                    velocity.setX(-0.5 * velocity.getX());
            } else if (velocity.getX() < 0
                    && nextLocation.getBlockX() - location.getBlockX() <= -1) {
                var colliding = false;
                for (int i = 1; i <= magnitude; i++) {
                    if (!item.getWorld().getBlockAt(location.getBlockX() - i, location.getBlockY(), location.getBlockZ()).isPassable()) {
                        colliding = true;
                        break;
                    }
                }
                if (colliding)
                    velocity.setX(-0.5 * velocity.getX());
            }
            var overallColliding = false;

            // Y-axis check
            magnitude = Math.abs(nextLocation.getBlockY() - location.getBlockY());
            if (velocity.getY() > 0
                    && nextLocation.getBlockY() - location.getBlockY() >= 1) {
                var colliding = true;
                for (int i = 1; i <= magnitude; i++) {
                    if (item.getWorld().getBlockAt(location.getBlockX(), location.getBlockY() + i, location.getBlockZ()).isPassable()) {
                        colliding = false;
                        break;
                    }
                }
                if (colliding) {
                    velocity.setY(-0.5 * velocity.getY());
                    overallColliding = true;
                }

            } else if (velocity.getY() < 0
                    && nextLocation.getBlockY() - location.getBlockY() <= -1) {
                var colliding = false;
                for (int i = 1; i <= magnitude; i++) {
                    if (!item.getWorld().getBlockAt(location.getBlockX(), location.getBlockY() - i, location.getBlockZ()).isPassable()) {
                        colliding = true;
                        break;
                    }
                }
                if (colliding) {
                    velocity.setY(-0.5 * velocity.getY());
                    overallColliding = true;
                }
            }

            // Z-axis check
            magnitude = Math.abs(nextLocation.getBlockZ() - location.getBlockZ());
            if (velocity.getZ() > 0
                    && nextLocation.getBlockZ() - location.getBlockZ() >= 1) {
                var colliding = true;
                for (int i = 1; i <= magnitude; i++) {
                    if (item.getWorld().getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ() + i).isPassable()) {
                        colliding = false;
                        break;
                    }
                }
                if (colliding) {
                    velocity.setZ(-0.5 * velocity.getZ());
                    overallColliding = true;
                }
            } else if (velocity.getZ() < 0
                    && nextLocation.getBlockZ() - location.getBlockZ() <= -1) {
                var colliding = false;
                for (int i = 1; i <= magnitude; i++) {
                    if (!item.getWorld().getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ() - i).isPassable()) {
                        colliding = true;
                        break;
                    }
                }
                if (colliding) {
                    overallColliding = true;
                    velocity.setZ(-0.5 * velocity.getZ());
                }
            }
            if (overallColliding) {
                if (vLength > 0.05)
                    item.getWorld().playSound(item.getLocation(), Sound.BLOCK_CHAIN_STEP, 1, 1);
                item.setVelocity(velocity);
                if (manager.getExplodeOnCollision(stack)) {
                    explodeGrenade(item);
                    return;
                }
            }
        } else {
            if (manager.getExplodeOnCollision(stack)) {
                explodeGrenade(item);
                return;
            }
        }

        manager.setRemainingTime(stack, remainingTime - 1);
        manager.setRemainingDespawnTime(stack, remainingDespawnTime - 1);
        Bukkit.getScheduler().runTaskLater(plugin, () -> grenadeTick(item), 1L);
    }

    private void throwGrenade(Player player, ItemStack grenade, boolean isOverhand) {
        var remainingTime = manager.getRemainingTime(grenade);
        if (player.isSneaking() && remainingTime != 0) { // if sneaking, don't throw yet
            manager.setRemainingTime(grenade, remainingTime - 1);
            Bukkit.getScheduler().runTaskLater(plugin,
                    () -> throwGrenade(player, grenade, isOverhand), 1L);
            return;
        }

        if (isOverhand) { // play sound
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_SNOWBALL_THROW, 1, 1);
        } else {
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_SNOWBALL_THROW, 0.5f, 2.0f);
        }

        ItemManager.getInstance().primeGrenade(grenade);


        var velocity = player.getEyeLocation().getDirection();
        if (isOverhand) velocity.multiply(7.48331477 / Math.sqrt(manager.getWeight(grenade)));
        else velocity.multiply(2.24499443 / Math.sqrt(manager.getWeight(grenade)));
        var loc = player.getEyeLocation();
        if (isOverhand)
            loc.setY(loc.getY() + 0.33);
        else
            loc.setY(loc.getY() - 0.6);

        velocity.add(player.getVelocity());
        velocity.setY(velocity.getY() + (Math.random() - 0.5) * 0.025);
        velocity.setX(velocity.getX() + (Math.random() - 0.5) * 0.025);
        velocity.setZ(velocity.getZ() + (Math.random() - 0.5) * 0.025);

        var drop = player.getWorld().dropItem(loc, grenade);
        var stack = drop.getItemStack();
        manager.setRemainingDespawnTime(stack, 20L * manager.getDespawnTime(stack));
        setGrenadeThrower(drop, player);
        grenadeSet.add(drop);
        drop.setVelocity(velocity);
        drop.setPickupDelay(1000);
        grenadeTick(drop);
    }

    private void cancelPrime(Player player) {
        grenadeDropList.add(player);
        Bukkit.getScheduler().runTaskLater(plugin, () -> grenadeDropList.remove(player), 2L);
    }

    private void grenadeUse(ItemStack item, Action action, Player player, EquipmentSlot hand) {
        boolean isOverhand = (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK);

        if (!isOverhand && grenadeDropList.contains(player)) {
            grenadeDropList.remove(player);
            return;
        }

        var amount = item.getAmount();

        if (amount > 1) item.setAmount(amount - 1);
        else player.getEquipment().setItem(hand, air);

        var grenade = item.clone();
        grenade.setAmount(1);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_CHAIN_STEP, 1, 1);
        manager.setRemainingTime(grenade, manager.getFuseTime(grenade) * 20);
        throwGrenade(player, grenade, isOverhand);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onEntityExplode(EntityExplodeEvent event) {
        var entity = event.getEntity();
        if (entity instanceof Item && manager.isGrenade(((Item) entity).getItemStack())) {
            var blockList = event.blockList();
            if (blockList.size() > 0) {
                event.setCancelled(true);
                for (var block : blockList) {
                    block.breakNaturally();
                }
            }
        }
    }

    @EventHandler
    private void onPlayerItemDrop(PlayerDropItemEvent event) {
        var drop = event.getItemDrop();
        var item = drop.getItemStack();
        if (!manager.isPrimed(item)) {
            cancelPrime(event.getPlayer());
        }
    }

    @EventHandler
    private void onChunkLoad(ChunkLoadEvent event) {
        var entities = event.getChunk().getEntities();
        for (var entity : entities) {
            if (entity instanceof Item) {
                var item = (Item) entity;
                if (ItemManager.getInstance().isPrimed(item.getItemStack())) {
                    grenadeSet.add(item);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> grenadeTick(item), 1L);
                }
            }
        }
    }

    @EventHandler
    private void onChunkUnload(ChunkUnloadEvent event) {
        var entities = event.getChunk().getEntities();
        for (var entity : entities) {
            if (entity instanceof Item) {
                grenadeSet.remove(entity);
            }
        }
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        var item = event.getCurrentItem();
        if (item == null) return;
        var player = event.getView().getPlayer();
        var equip = player.getEquipment();
        var item2 = air;
        if (equip != null) item2 = equip.getItemInMainHand();
        if ((!manager.isPrimed(item)) || (!manager.isPrimed(item2))) {
            cancelPrime((Player) player);
        }
    }

    @EventHandler
    private void onItemUse(PlayerInteractEvent event) {
        var item = event.getItem();
        if (item == null) return;
        var action = event.getAction();

        if (manager.isGrenade(item) && (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK)) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> grenadeUse(item, action, event.getPlayer(), event.getHand()), 2L);
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onInventoryPickupItem(InventoryPickupItemEvent event) {
        var item = event.getItem();
        if (manager.isPrimed(item.getItemStack())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onItemMerge(ItemMergeEvent event) {
        var item = event.getEntity();
        if (manager.isPrimed(item.getItemStack())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onEntityPickupItem(EntityPickupItemEvent event) {
        var drop = event.getItem();
        var item = drop.getItemStack();
        if (manager.isPrimed(item)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onDamage(EntityDamageEvent event) {
        var entity = event.getEntity();
        if (entity instanceof Item) {
            var item = (Item) entity;
            if (manager.isPrimed(item.getItemStack())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void onEntityCombust(EntityCombustEvent event) {
        var entity = event.getEntity();
        if (entity instanceof Item) {
            var item = (Item) entity;
            if (manager.isPrimed(item.getItemStack())) {
                event.setCancelled(true);
            }
        }
    }
}
