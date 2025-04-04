package com.songoda.ultimaterepairing.utils;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.ServerVersion;
import com.songoda.third_party.com.cryptomorin.xseries.XMaterial;
import com.songoda.ultimaterepairing.UltimateRepairing;
import com.songoda.core.math.MathUtils;
import com.songoda.ultimaterepairing.repair.RepairType;
import com.songoda.ultimaterepairing.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by songoda on 2/25/2017.
 */
public class Methods {
    static Random rand = new Random();

    public static XMaterial getRainbowGlass() {
        return CompatibleMaterial.getGlassPaneForColor(rand.nextInt(16));
    }

    public static int getCost(RepairType type, ItemStack item) {
        String equationXP = Settings.EXPERIENCE_EQUATION.getString();
        String equationECO = Settings.ECONOMY_EQUATION.getString();
        String equationITEM = Settings.ITEM_EQUATION.getString();

        short maxDurability = item.getType().getMaxDurability();
        short damage;
        if (ServerVersion.isServerVersionBelow(ServerVersion.V1_11)) {
            damage = item.getDurability();
        } else {
            damage = (short) ((Damageable) item.getItemMeta()).getDamage();
        }

        equationXP = equationXP.replace("{MaxDurability}", Short.toString(maxDurability))
                .replace("{Durability}", Integer.toString(maxDurability - damage))
                .replace("{Damage}", Short.toString(damage));
        int XPCost = (int) Math.round(MathUtils.eval(equationXP));

        equationECO = equationECO.replace("{MaxDurability}", Short.toString(maxDurability))
                .replace("{Durability}", Integer.toString(maxDurability - damage))
                .replace("{Damage}", Short.toString(damage))
                .replace("{XPCost}", Integer.toString(XPCost));

        int ECOCost = (int) Math.round(MathUtils.eval(equationECO));

        equationITEM = equationITEM.replace("{MaxDurability}", Short.toString(maxDurability))
                .replace("{Durability}", Integer.toString(maxDurability - damage))
                .replace("{Damage}", Short.toString(damage))
                .replace("{XPCost}", Integer.toString(XPCost));

        int ITEMCost = (int) Math.round(MathUtils.eval(equationITEM));

        if (item.hasItemMeta() &&
                item.getItemMeta().hasEnchants()) {
            int multi = UltimateRepairing.getInstance().getConfig().getInt("Main.Cost Multiplier For Enchanted Items");
            XPCost = XPCost * multi;
            ECOCost = ECOCost * multi;
            ITEMCost = ITEMCost * multi;
        }

        if (type == RepairType.EXPERIENCE)
            return XPCost;
        else if (type == RepairType.ITEM)
            return ITEMCost;
        else if (type == RepairType.ECONOMY)
            return ECOCost;
        return 9999999;
    }

    public static Material getType(ItemStack item) {
        if (Settings.REPAIR_ONLY_SAME_TYPE.getBoolean()) {
            if (item.getType().name().contains("NETHERITE"))
                return XMaterial.NETHERITE_INGOT.parseMaterial();
            if (item.getType().name().contains("DIAMOND"))
                return XMaterial.DIAMOND.parseMaterial();
            if (item.getType().name().contains("IRON"))
                return XMaterial.IRON_INGOT.parseMaterial();
            if (item.getType().name().contains("GOLD"))
                return XMaterial.GOLD_INGOT.parseMaterial();
            if (item.getType().name().contains("LEATHER"))
                return XMaterial.LEATHER.parseMaterial();
            if (item.getType().name().contains("STONE"))
                return XMaterial.STONE.parseMaterial();
            if (item.getType().name().contains("WOOD"))
                return XMaterial.OAK_WOOD.parseMaterial();
        }
        
        return Settings.ITEM_ICON.getMaterial(XMaterial.DIAMOND).parseMaterial();
    }

    /**
     * Checks if the inventory contains the specified item.
     *
     * @param inventory The inventory to check
     * @param item      The item to check for.
     *
     * @return Whether or not the inventory contains the item.
     */
    public static boolean inventoryContains(Inventory inventory, ItemStack item) {
        int count = 0;
        ItemStack[] items = inventory.getContents();
        for (ItemStack item1 : items) {
            if (item1 != null && item1.getType() == item.getType() && item1.getDurability() == item.getDurability()) {
                count += item1.getAmount();
            }

            if (count >= item.getAmount()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Removes the specified item from the inventory
     *
     * @param inventory The inventory to remove from.
     * @param item      The item to remove.
     */
    public static void removeFromInventory(Inventory inventory, ItemStack item) {
        int amt = item.getAmount();
        ItemStack[] items = inventory.getContents();
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null && items[i].getType() == item.getType() && items[i].getDurability() == item.getDurability()) {
                if (items[i].getAmount() > amt) {
                    items[i].setAmount(items[i].getAmount() - amt);
                    break;
                } else if (items[i].getAmount() == amt) {
                    items[i] = null;
                    break;
                } else {
                    amt -= items[i].getAmount();
                    items[i] = null;
                }
            }
        }

        inventory.setContents(items);
    }

    /**
     * Serializes the location of the block specified.
     *
     * @param b The block whose location is to be saved.
     *
     * @return The serialized data.
     */
    public static String serializeLocation(Block b) {
        if (b == null) {
            return "";
        }

        return serializeLocation(b.getLocation());
    }

    /**
     * Serializes the location specified.
     *
     * @param location The location that is to be saved.
     *
     * @return The serialized data.
     */
    public static String serializeLocation(Location location) {
        if (location == null || location.getWorld() == null) {
            return "";
        }

        String w = location.getWorld().getName();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        String str = w + ":" + x + ":" + y + ":" + z;
        str = str.replace(".0", "")
                .replace("/", "");
        return str;
    }

    private static Map<String, Location> serializeCache = new HashMap<>();

    /**
     * Deserializes a location from the string.
     *
     * @param str The string to parse.
     *
     * @return The location that was serialized in the string.
     */
    public static Location unserializeLocation(String str) {
        if (str == null || str.equals("")) {
            return null;
        }
        if (serializeCache.containsKey(str)) {
            return serializeCache.get(str).clone();
        }

        String cacheKey = str;
        str = str.replace("y:", ":")
                .replace("z:", ":")
                .replace("w:", "")
                .replace("x:", ":")
                .replace("/", ".");
        List<String> args = Arrays.asList(str.split("\\s*:\\s*"));

        World world = Bukkit.getWorld(args.get(0));
        double x = Double.parseDouble(args.get(1)),
                y = Double.parseDouble(args.get(2)),
                z = Double.parseDouble(args.get(3));
        Location location = new Location(world, x, y, z, 0, 0);
        serializeCache.put(cacheKey, location.clone());
        return location;
    }
}
