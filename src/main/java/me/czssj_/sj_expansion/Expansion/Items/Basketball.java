package me.czssj_.sj_expansion.Expansion.Items;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.groups.SubItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import me.czssj_.sj_expansion.sj_Expansion;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class Basketball extends SlimefunItem implements NotPlaceable, Listener
{
    private Random random = new Random();
    private Map<UUID, Long> lastShotTime = new HashMap<>();
    private static final long COOLDOWN_MILLIS = 500;

    public Basketball(SubItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe)
    {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    public void preRegister() 
    {
        ItemUseHandler itemUseHandler = this::itemRightClick;
        addItemHandler(itemUseHandler);
        Bukkit.getPluginManager().registerEvents(this, sj_Expansion.getInstance());
    }

    private void itemRightClick(PlayerRightClickEvent event)
    {
        Player p = event.getPlayer();
        long currentTime = System.currentTimeMillis();
        if (lastShotTime.containsKey(p.getUniqueId()) && currentTime - lastShotTime.get(p.getUniqueId()) < COOLDOWN_MILLIS) 
        {
            event.cancel();
            return;
        }
        lastShotTime.put(p.getUniqueId(), currentTime);
        p.launchProjectile(Snowball.class);
        p.playSound(p.getLocation(), Sound.ENTITY_EGG_THROW, SoundCategory.MASTER, 1.0f, 1.0f);
        event.cancel();
    }

    @EventHandler
    public void onSnowballHit(ProjectileHitEvent event) 
    {
        if (event.getEntity() instanceof Snowball) 
        {
            if (event.getHitEntity() instanceof LivingEntity && !(event.getHitEntity() instanceof Player))
            {
                LivingEntity hitEntity = (LivingEntity) event.getHitEntity();
                hitEntity.damage(2.5);
                if (random.nextDouble() < 0.075)
                {
                    hitEntity.getWorld().spawnEntity(hitEntity.getLocation(), EntityType.CHICKEN);
                    hitEntity.remove();
                }
            }
        }
    }
}