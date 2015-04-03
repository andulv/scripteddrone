package net.ulveseth.ScriptedDrone;

import java.util.List;

import org.bukkit.inventory.ItemStack;



public class DroneManagerResultMine extends DroneManagerResult {
	private final List<ItemStack> inventoryPlacedItems;
	private final List<ItemStack> leftBehindItems;
	
	public DroneManagerResultMine(boolean success, String resultText, List<ItemStack> inventoryPlacedItems, List<ItemStack> leftBehindItems) {
		 super(success,resultText);
		 
		 this.inventoryPlacedItems = inventoryPlacedItems;
		 this.leftBehindItems=leftBehindItems;		 
	 }

	public List<ItemStack> getInventoryPlacedItems() {
		return inventoryPlacedItems;
	}

	public List<ItemStack> getLeftBehindItems() {
		return leftBehindItems;
	}

}
