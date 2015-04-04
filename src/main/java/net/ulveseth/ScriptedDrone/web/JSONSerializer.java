package net.ulveseth.ScriptedDrone.web;

import java.util.ArrayList;
import java.util.Iterator;

import net.ulveseth.ScriptedDrone.Drone;
import net.ulveseth.ScriptedDrone.Drone.Directions;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JSONSerializer {
	
	 /** 
     * Helper method to set value in JSONObject. Exists only to avoid compilation warnings.
     */	
	@SuppressWarnings("unchecked")
	public static void setValue(JSONObject jobject, String key, Object value) {
		jobject.put(key,value);
	}
	
	 /** 
     * Helper method to add item to JSONArray. Exists only to avoid compilation warnings.
     */	
	@SuppressWarnings("unchecked")
	public static void addItem(JSONArray jobject, Object item) {
		jobject.add(item);
	}
	
	public JSONObject serializeLineOfSight(Drone drone) {
		JSONObject jlineOfSight=new JSONObject();
		setValue(jlineOfSight, "Up", serializeBlockMaterials(drone.Look(Directions.Up)));
		setValue(jlineOfSight, "Down", serializeBlockMaterials(drone.Look(Directions.Down)));
		setValue(jlineOfSight, "Forward", serializeBlockMaterials(drone.Look(Directions.Forward)));
		setValue(jlineOfSight, "Back", serializeBlockMaterials(drone.Look(Directions.Back)));
		setValue(jlineOfSight, "Left", serializeBlockMaterials(drone.Look(Directions.Left)));
		setValue(jlineOfSight, "Right", serializeBlockMaterials(drone.Look(Directions.Right)));
		return jlineOfSight;
	}

	public JSONObject serializeLocation(Location loc) {
		 JSONObject jlocation = new JSONObject();
		 setValue(jlocation, "X", loc.getX());
		 setValue(jlocation, "Y", loc.getY());
		 setValue(jlocation, "Z", loc.getZ());
		 setValue(jlocation, "Yaw", loc.getYaw());
		 setValue(jlocation, "Pitch", loc.getPitch());
		 setValue(jlocation, "World", loc.getWorld().getName());
		 return jlocation;
	 }
	 
	public JSONObject serializeItemStack(ItemStack itemStack) {
		JSONObject jitem = new JSONObject();
		{
			setValue(jitem, "Type", itemStack.getTypeId());
			setValue(jitem, "TypeName", itemStack.getType().name());
			setValue(jitem, "Amount", itemStack.getAmount());
			setValue(jitem, "Durability", itemStack.getDurability());
			if (itemStack.hasItemMeta()) {
//				JSONObject obj = serializeMetaData(itemStack.getItemMeta());
//				if (obj != null) {
//					item.put("Meta", obj);
//				}
			}
			if (itemStack.getData().getData() != 0) {
				setValue(jitem, "Data", itemStack.getData().getData());
			}
			JSONArray enchantments = new JSONArray();
			{
				Iterator<Enchantment> enchIter = itemStack.getEnchantments()
						.keySet().iterator();
				while (enchIter.hasNext()) {
					Enchantment cur = enchIter.next();
					JSONObject enchantment = new JSONObject();
					{
						setValue(enchantment, "Name", cur.getName());
						setValue(enchantment, "Level",
								itemStack.getEnchantmentLevel(cur));
					}
					addItem(enchantments, enchantment);
				}
			}
		}
		return jitem;
	}
	
	
	public JSONArray serializeInventory(Inventory inv)  {
		JSONArray jinventory = new JSONArray();
		
		for (int i = 0; i < inv.getSize(); i++) {
			ItemStack itemStack = inv.getItem(i);
			if (itemStack != null) {
				JSONObject item = serializeItemStack(itemStack);
				setValue(item, "Slot", i);
				addItem(jinventory, item);
			}
		}
		return jinventory;
	}
	
	public JSONObject serializeBlockMaterial(Block block)  {
		JSONObject jblock = new JSONObject();
		setValue(jblock, "Type", block.getType().ordinal());
		setValue(jblock, "TypeName", block.getType().name());
		return jblock;
	}

	public JSONArray serializeBlockMaterials(ArrayList<Block> blocks) {
		JSONArray jblocks= new JSONArray();
		for (int i = 0; i < blocks.size(); i++) {
			Block block=blocks.get(i);
			addItem(jblocks, serializeBlockMaterial(block));
		}
		return jblocks;	
	}
}
