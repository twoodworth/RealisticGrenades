package me.tedwoodworth.grenades;

import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;

import java.util.*;

public class EventListener implements Listener {
    private final Set<Player> grenadeDropList = new HashSet<>();
    public Set<Item> grenadeSet = new HashSet<>();
    public Set<Player> throwerSet = new HashSet<>();
    private final ItemManager manager = ItemManager.getInstance();
    private final RealisticGrenades plugin = RealisticGrenades.getInstance();
    private final ItemStack air = new ItemStack(Material.AIR);
    private static final HashMap<Item, HashSet<Entity>> hitList = new HashMap<>();

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
            for (double h = radius / 5.0; h < radius; h += radius / 5.0) {
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
                        if (!block.getType().isAir() || nLoc.distanceSquared(location) > Math.pow(finalH, 2)) continue;
                        location.getWorld().spawnParticle(Particle.LAVA, nLoc, 1, Math.random(), Math.random(), Math.random());
                        int j = 0;
                        while (j < r && block.getLocation().getY() > 0 && block.getRelative(BlockFace.DOWN).getType().isAir()) {
                            block = block.getRelative(BlockFace.DOWN);
                            j++;
                        }
                        block.setType(Material.FIRE);
                    }
                    location.getWorld().playSound(location, Sound.BLOCK_FIRE_AMBIENT, 1, 1);
                }, delay);
                delay += 3L;
            }
            return true;
        }
        return false;
    }

    private boolean createSmokeExplosion(Location location, float radius, Entity source) {
        var event = new EntityExplodeEvent(source, location, new ArrayList<>(), radius);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            var fraction = radius / 20.0F;
            Bukkit.getScheduler().runTask(plugin, () -> createSmoke(source, fraction, 0, 6));
            Bukkit.getScheduler().runTaskLater(plugin, () -> createSmoke(source, fraction * 2, 0, 6), 5L);
            Bukkit.getScheduler().runTaskLater(plugin, () -> createSmoke(source, fraction * 3, 0, 6), 10L);
            Bukkit.getScheduler().runTaskLater(plugin, () -> createSmoke(source, fraction * 4, 0, 6), 15L);
            Bukkit.getScheduler().runTaskLater(plugin, () -> createSmoke(source, fraction * 5, 0, 6), 20L);
            Bukkit.getScheduler().runTaskLater(plugin, () -> createSmoke(source, fraction * 6, 0, 6), 25L);
            Bukkit.getScheduler().runTaskLater(plugin, () -> createSmoke(source, fraction * 7, 0, 6), 30L);
            Bukkit.getScheduler().runTaskLater(plugin, () -> createSmoke(source, fraction * 8, 0, 6), 35L);
            Bukkit.getScheduler().runTaskLater(plugin, () -> createSmoke(source, fraction * 9, 0, 6), 40L);
            Bukkit.getScheduler().runTaskLater(plugin, () -> createSmoke(source, fraction * 10, 0, 6), 45L);
            Bukkit.getScheduler().runTaskLater(plugin, () -> createSmoke(source, fraction * 11, 0, 6), 50L);
            Bukkit.getScheduler().runTaskLater(plugin, () -> createSmoke(source, fraction * 12, 0, 6), 55L);
            Bukkit.getScheduler().runTaskLater(plugin, () -> createSmoke(source, fraction * 13, 0, 6), 60L);
            Bukkit.getScheduler().runTaskLater(plugin, () -> createSmoke(source, fraction * 14, 0, 6), 65L);
            Bukkit.getScheduler().runTaskLater(plugin, () -> createSmoke(source, fraction * 15, 0, 6), 70L);
            Bukkit.getScheduler().runTaskLater(plugin, () -> createSmoke(source, fraction * 16, 0, 6), 75L);
            Bukkit.getScheduler().runTaskLater(plugin, () -> createSmoke(source, fraction * 17, 0, 6), 80L);
            Bukkit.getScheduler().runTaskLater(plugin, () -> createSmoke(source, fraction * 18, 0, 6), 85L);
            Bukkit.getScheduler().runTaskLater(plugin, () -> createSmoke(source, fraction * 19, 0, 6), 90L);
            Bukkit.getScheduler().runTaskLater(plugin, () -> createSmoke(source, radius, 0, 420), 95L);

            return true;
        }
        return false;
    }

    private void createSmoke(Entity source, float radius, int i, final int max) {
        var count = ConfigManager.SMOKE_THICKNESS * 0.8 * Math.pow(radius, 3);

        var loc = source.getLocation();
        var x = loc.getX();
        var y = loc.getY();
        var z = loc.getZ();

        loc.getWorld().playSound(loc, Sound.BLOCK_LAVA_EXTINGUISH, 0.02F, 1.3F);

        var nearby = source.getNearbyEntities(radius, radius, radius);
        for (var entity : nearby) {
            if (entity instanceof Player && loc.distanceSquared(entity.getLocation()) < Math.pow(radius, 2)) {
                ((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 0, true, false, false));
            }
        }

        for (int j = 0; j < count; j++) {
            var tempX = x + Math.random() * 2 * radius - radius;
            var tempY = y + Math.random() * 2 * radius - radius;
            var tempZ = z + Math.random() * 2 * radius - radius;
            var dustOptions = new Particle.DustOptions(Color.fromRGB(100, 100, 100), (float) (7 + Math.random() * 12.0));
            var tempLoc = new Location(loc.getWorld(), tempX, tempY, tempZ);
            if (tempLoc.distanceSquared(loc) > Math.pow(radius, 2)) continue;
            int k = 0;
            var block = tempLoc.getBlock();
            while (k < radius && !block.getType().isAir()) {
                block = block.getRelative(BlockFace.UP);
                k++;
            }
            if (!block.getType().isAir()) continue;


            tempLoc.getWorld().spawnParticle(Particle.REDSTONE, tempLoc, 1, dustOptions);
        }
        i++;
        if (i != max) {
            int finalI = i;
            Bukkit.getScheduler().runTaskLater(plugin, () -> createSmoke(source, radius, finalI, max), 1L);
        }
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
            if (blastRadius > 0F) item.getWorld().createExplosion(item.getLocation(), blastRadius / 2.0F, false, false, item);
            if (destructionRadius > 0F)
                item.getWorld().createExplosion(item.getLocation(), destructionRadius, false, true, item);
            if (fireRadius > 0F && primeEvent.getFire()) createFireExplosion(item.getLocation(), fireRadius + 2.0F, item);
            if (smokeRadius > 0F) createSmokeExplosion(item.getLocation(), smokeRadius, item);
        }
        grenadeSet.remove(item);
        item.remove();
    }

    private void grenadeTick(Item item) {
        if (!grenadeSet.contains(item)) return;
        var stack = item.getItemStack();
        var remainingTime = manager.getRemainingTime(stack);
        var remainingDespawnTime = manager.getRemainingDespawnTime(stack);
        if (remainingTime == 0) {
            hitList.remove(item);
            explodeGrenade(item);
            return;
        } else if (remainingDespawnTime == 0) {
            hitList.remove(item);
            grenadeSet.remove(item);
            item.remove();
            return;
        }
        var location = item.getLocation();
        var velocity = item.getVelocity();
        if (manager.hasSmokeTrail(stack)) {
            var location2 = location.clone();
            location2.setY(item.getLocation().getY() + 0.25);
            item.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, location2, 1, 0.05, 0.05, 0.05, 0);
            location2.subtract(velocity.clone().multiply(0.5));
            item.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, location2, 1, 0.05, 0.05, 0.05, 0);
        }


        if (manager.beeps(stack)) {
            if (remainingTime <= 40 && remainingTime % 2 == 0) {
                item.getWorld().playSound(item.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 0.5f + (40 - remainingTime) / 26.666666f);
            } else if (remainingTime <= 80 && remainingTime % 4 == 0) {
                item.getWorld().playSound(item.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1f);
            } else if (remainingTime <= 160 && remainingTime % 8 == 0) {
                item.getWorld().playSound(item.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1f);
            } else if (remainingTime <= 320 && remainingTime % 16 == 0) {
                item.getWorld().playSound(item.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1f);
            } else if (remainingTime <= 480 && remainingTime % 32 == 0) {
                item.getWorld().playSound(item.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1f);
            } else if (remainingTime <= 640 && remainingTime % 64 == 0) {
                item.getWorld().playSound(item.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1f);
            } else if (remainingTime <= 1280 && remainingTime % 128 == 0) {
                item.getWorld().playSound(item.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1f);
            } else if (remainingTime <= 2560 && remainingTime % 256 == 0) {
                item.getWorld().playSound(item.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1f);
            } else if (remainingTime < 5120 && remainingTime % 512 == 0) {
                item.getWorld().playSound(item.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1f);
            } else if (remainingTime < 10240 && remainingTime % 1024 == 0) {
                item.getWorld().playSound(item.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1f);
            } else if (remainingTime % 4096 == 0) {
                item.getWorld().playSound(item.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1f);
            }
        }


        var calculations = ConfigManager.CALCULATIONS_PER_TICK;


        var interval = velocity.clone().multiply(1.0 / calculations);
        var curLoc = location.clone();
        var world = location.getWorld();
        var bounciness = manager.getBounciness(stack);
        var airRes = manager.getAirResistance(stack);
        var waterRes = manager.getWaterResistance(stack);
        var m2 = manager.getWeight(stack);
        var alreadyCollidedX = new HashSet<Entity>();
        var alreadyCollidedY = new HashSet<Entity>();
        var alreadyCollidedZ = new HashSet<Entity>();
        hitList.putIfAbsent(item, new HashSet<>());
        for (int i = 0; i < calculations; i++) {
            var collides = false;
            var tempLocX = curLoc.clone().add(interval.getX(), 0, 0);
            var tempLocY = curLoc.clone().add(0, interval.getY(), 0);
            var tempLocZ = curLoc.clone().add(0, 0, interval.getZ());

            var boxX = new BoundingBox(tempLocX.getX() - 0.125, tempLocX.getY(), tempLocX.getZ() - 0.125, tempLocX.getX() + 0.125, tempLocX.getY() + 0.25, tempLocX.getZ() + 0.125);
            var boxY = new BoundingBox(tempLocY.getX() - 0.125, tempLocY.getY(), tempLocY.getZ() - 0.125, tempLocY.getX() + 0.125, tempLocY.getY() + 0.25, tempLocY.getZ() + 0.125);
            var boxZ = new BoundingBox(tempLocZ.getX() - 0.125, tempLocZ.getY(), tempLocZ.getZ() - 0.125, tempLocZ.getX() + 0.125, tempLocZ.getY() + 0.25, tempLocZ.getZ() + 0.125);

            var nearbyX = world.getNearbyEntities(boxX);
            var nearbyY = world.getNearbyEntities(boxY);
            var nearbyZ = world.getNearbyEntities(boxZ);

            nearbyX.remove(item);
            nearbyY.remove(item);
            nearbyZ.remove(item);

            nearbyX.remove(getGrenadeThrower(item));
            nearbyY.remove(getGrenadeThrower(item));
            nearbyZ.remove(getGrenadeThrower(item));

            var entityX = nearbyX.size() > 0;
            var entityY = nearbyY.size() > 0;
            var entityZ = nearbyZ.size() > 0;

            var blockX = tempLocX.getBlock();
            var blockY = tempLocY.getBlock();
            var blockZ = tempLocZ.getBlock();

            var xCollide = !blockX.isPassable() && !blockX.isLiquid();
            var yCollide = !blockY.isPassable() && !blockY.isLiquid();
            var zCollide = !blockZ.isPassable() && !blockZ.isLiquid();

            var nXcollide = xCollide;
            var nYcollide = yCollide;
            var nZcollide = zCollide;

            if (!xCollide && !yCollide) {
                var blockXY = curLoc.clone().add(interval.getX(), interval.getY(), 0).getBlock();
                if (!blockXY.isPassable() && !blockXY.isLiquid()) {
                    nXcollide = true;
                    nYcollide = true;
                }
            }
            if (!xCollide && !zCollide) {
                var blockXZ = curLoc.clone().add(interval.getX(), 0, interval.getZ()).getBlock();
                if (!blockXZ.isPassable() && !blockXZ.isLiquid()) {
                    nXcollide = true;
                    nZcollide = true;
                }
            }
            if (!yCollide && !zCollide) {
                var blockYZ = curLoc.clone().add(0, interval.getY(), interval.getZ()).getBlock();
                if (!blockYZ.isPassable() && !blockYZ.isLiquid()) {
                    nYcollide = true;
                    nZcollide = true;
                }
            }
            if (entityX) {
                var entity = nearbyX.toArray()[0];
                if (!hitList.get(item).contains(entity)) {
                    if (entity instanceof LivingEntity) {
                        var living = ((LivingEntity) entity);
                        var livingVel = living.getVelocity();
                        var m1 = living.getBoundingBox().getVolume() * 100;
                        var v1 = livingVel.getX();
                        var v2 = velocity.getX();
                        var p1 = m1 * v1;
                        var p2 = m2 * v2;

                        var p1prime = .7 * p1 + .4 * p2;
                        var p2prime = .3 * p1 + .6 * p2;

                        var v1prime = p1prime / m1;
                        var v2prime = p2prime / m2;

                        velocity.setX(v2prime);
                        interval.setX(velocity.getX() / calculations);
                        livingVel.setX(v1prime);
                        ((LivingEntity) entity).setVelocity(livingVel);
                    } else {
                        velocity.setX(-bounciness * velocity.getX());
                        interval.setX(-bounciness * interval.getX());
                    }
                    alreadyCollidedX.add((Entity) entity);
                    collides = true;
                }
            } else if (nXcollide) {
                velocity.setX(-bounciness * velocity.getX());
                interval.setX(-bounciness * interval.getX());
                collides = true;
            }

            if (entityY) {
                var entity = nearbyY.toArray()[0];
                if (!hitList.get(item).contains(entity)) {
                    if (entity instanceof LivingEntity) {
                        var living = ((LivingEntity) entity);
                        var livingVel = living.getVelocity();
                        var m1 = living.getBoundingBox().getVolume() * 100;
                        var v1 = livingVel.getY();
                        var v2 = velocity.getY();
                        var p1 = m1 * v1;
                        var p2 = m2 * v2;

                        var p1prime = .7 * p1 + .4 * p2;
                        var p2prime = .3 * p1 + .6 * p2;

                        var v1prime = p1prime / m1;
                        var v2prime = p2prime / m2;

                        velocity.setY(v2prime);
                        interval.setY(velocity.getY() / calculations);
                        livingVel.setY(v1prime);
                        ((LivingEntity) entity).setVelocity(livingVel);
                    } else {
                        velocity.setY(-bounciness * velocity.getY());
                        interval.setY(-bounciness * interval.getY());
                    }
                    collides = true;
                    alreadyCollidedY.add((Entity) entity);
                }
            } else if (nYcollide) {
                velocity.setY(-bounciness * velocity.getY());
                interval.setY(-bounciness * interval.getY());
                collides = true;
            }

            if (entityZ) {
                var entity = nearbyZ.toArray()[0];
                if (!hitList.get(item).contains(entity)) {
                    if (entity instanceof LivingEntity) {
                        var living = ((LivingEntity) entity);
                        var livingVel = living.getVelocity();
                        var m1 = living.getBoundingBox().getVolume() * 100;
                        var v1 = livingVel.getZ();
                        var v2 = velocity.getZ();
                        var p1 = m1 * v1;
                        var p2 = m2 * v2;

                        var p1prime = .7 * p1 + .4 * p2;
                        var p2prime = .3 * p1 + .6 * p2;

                        var v1prime = p1prime / m1;
                        var v2prime = p2prime / m2;

                        velocity.setZ(v2prime);
                        interval.setZ(velocity.getZ() / calculations);
                        livingVel.setZ(v1prime);
                        ((LivingEntity) entity).setVelocity(livingVel);
                    } else {
                        velocity.setZ(-bounciness * velocity.getZ());
                        interval.setZ(-bounciness * interval.getZ());
                    }
                    collides = true;
                    alreadyCollidedZ.add((Entity) entity);
                }
            } else if (nZcollide) {
                velocity.setZ(-bounciness * velocity.getZ());
                interval.setZ(-bounciness * interval.getZ());
                collides = true;
            }

            curLoc.add(interval);
            var block = curLoc.getBlock();
            if (block.isLiquid()) {
                velocity.multiply(1 - waterRes);
                interval.multiply(1 - waterRes);
            } else if (block.isPassable()) {
                velocity.multiply(1 - airRes);
                interval.multiply(1 - airRes);
            }
            item.setVelocity(velocity);
            var player = getGrenadeThrower(item);
            var contains = false;
            var set = hitList.get(item);
            var damage = manager.getDirectHitDamage(stack);
            for (var entity : alreadyCollidedX) {
                if (entity instanceof LivingEntity && !entity.equals(player) && !set.contains(entity)) {
                    contains = true;
                    if (damage > 0.0) {
                        ((LivingEntity) entity).damage(damage);
                    }
                }
                set.add(entity);
            }
            for (var entity : alreadyCollidedY) {
                if (entity instanceof LivingEntity && !entity.equals(player) && !set.contains(entity)) {
                    contains = true;
                    if (damage > 0.0) {
                        ((LivingEntity) entity).damage(damage);
                    }
                }
                set.add(entity);
            }
            for (var entity : alreadyCollidedZ) {
                if (entity instanceof LivingEntity && !entity.equals(player) && !set.contains(entity)) {
                    contains = true;
                    if (damage > 0.0) {
                        ((LivingEntity) entity).damage(damage);
                    }
                }
                set.add(entity);
            }
            if (player != null && player.isOnline() && contains) {
                getGrenadeThrower(item).playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.5f, 1f);
            }

            if (collides) {
                if (velocity.length() > 0.05) {
                    item.getWorld().playSound(item.getLocation(), Sound.BLOCK_CHAIN_STEP, 0.3f, 1);
                }
                if (manager.getExplodeOnCollision(stack)) {
                    explodeGrenade(item);
                    return;
                }
            }
        }

        manager.setRemainingTime(stack, remainingTime - 1);
        manager.setRemainingDespawnTime(stack, remainingDespawnTime - 1);
        Bukkit.getScheduler().runTaskLater(plugin, () -> grenadeTick(item), 1L);
    }

    private void throwGrenade(Player player, ItemStack grenade, boolean isOverhand) {
        var remainingTime = manager.getRemainingTime(grenade);
        if (player.isOnline() && !player.isDead() && player.isSneaking() && remainingTime != 0) { // if sneaking, don't throw yet
            manager.setRemainingTime(grenade, remainingTime - 1);
            Bukkit.getScheduler().runTaskLater(plugin,
                    () -> throwGrenade(player, grenade, isOverhand), 1L);
            return;
        }

        throwerSet.remove(player);
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


        var pBox = player.getBoundingBox();
        var interval = velocity.clone().normalize().multiply(0.25);
        while (true) {
            var block = loc.getBlock();
            if (!block.isPassable() && !block.isLiquid()) break; // if loc is solid block, stop

            var gBox = new BoundingBox(loc.getX() - 0.125, loc.getY(), loc.getZ() - 0.125, loc.getX() + 0.125, loc.getY() + 0.25, loc.getZ() + 0.125);
            loc.add(interval);
            if (!pBox.contains(gBox)) {
                loc.add(interval);
                break;
            }
        }

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
        drop.setGravity(manager.getHasGravity(stack));
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
        throwerSet.add(player);
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
        var player = event.getPlayer();
        if (manager.isGrenade(item) && (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK)) {
            if (!throwerSet.contains(player)) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> grenadeUse(item, action, player, event.getHand()), 2L);
            }
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
    private void onEntityCombust(EntityCombustEvent event) {
        var entity = event.getEntity();
        if (entity instanceof Item) {
            var item = (Item) entity;
            if (manager.isPrimed(item.getItemStack())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();
        plugin.updateCheck(player); // send message if Op & new update is available
        var inv = player.getInventory();
        var slots = inv.getSize();
        for (int i = 0; i < slots; i++) {
            var content = inv.getItem(i);
            if (content != null && manager.isGrenade(content)) {
                var id = manager.getID(content);
                var grenade = manager.getGrenade(id);
                if (grenade != null && !grenade.isSimilar(content)) {
                    grenade.setAmount(content.getAmount());
                    inv.setItem(i, grenade);
                }
            }
        }
    }

    @EventHandler
    private void onPickupItem(InventoryPickupItemEvent event) {
        var item = event.getItem();
        var stack = item.getItemStack();
        if (manager.isGrenade(stack)) {
            var id = manager.getID(stack);
            var grenade = manager.getGrenade(id);
            if (grenade != null && !grenade.isSimilar(stack)) {
                grenade.setAmount(stack.getAmount());
                item.setItemStack(grenade);
            }
        }
    }

    @EventHandler
    private void onPickupItem(EntityPickupItemEvent event) {
        var item = event.getItem();
        var stack = item.getItemStack();
        if (manager.isGrenade(stack)) {
            var id = manager.getID(stack);
            var grenade = manager.getGrenade(id);
            if (grenade != null && !grenade.isSimilar(stack)) {
                grenade.setAmount(stack.getAmount());
                item.setItemStack(grenade);
            }
        }
    }

    @EventHandler
    private void onEntityDropItem(EntityDropItemEvent event) {
        var item = event.getItemDrop();
        var stack = item.getItemStack();
        if (manager.isGrenade(stack)) {
            var id = manager.getID(stack);
            var grenade = manager.getGrenade(id);
            if (grenade != null && !grenade.isSimilar(stack)) {
                grenade.setAmount(stack.getAmount());
                item.setItemStack(grenade);
            }
        }
    }

    @EventHandler
    private void onInventoryOpen(InventoryOpenEvent event) {
        var inv = event.getInventory();
        var slots = inv.getSize();
        for (int i = 0; i < slots; i++) {
            var content = inv.getItem(i);
            if (content != null && manager.isGrenade(content)) {
                var id = manager.getID(content);
                var grenade = manager.getGrenade(id);
                if (grenade != null && !grenade.isSimilar(content)) {
                    grenade.setAmount(content.getAmount());
                    inv.setItem(i, grenade);
                }
            }
        }
    }
}
