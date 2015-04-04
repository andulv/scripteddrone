package net.ulveseth.ScriptedDrone;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import net.ulveseth.ScriptedDrone.DroneManagerResult;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Drone {
	String droneName;
	UUID ownerPlayerUUID;	
	StorageMinecart droneAvatar;	
	
	private static final int MAX_LINE_OF_SIGHT=10;

	public Drone(StorageMinecart droneAvatar, UUID ownerPlayerUUID, String droneName) {
		this.ownerPlayerUUID = ownerPlayerUUID;
		this.droneAvatar = droneAvatar;
		this.droneName=droneName;
	}
	
	public Location GetLocation() {
		return droneAvatar.getLocation();
	}
	
	public String GetName() {
		return droneName;
	}
	
	 /** 
     * Rotates the drone. Will always succeed. No known circumstances that can prevent drone from rotating.
     * @param direction 	the direction the drone will rotate
     */
	public void Rotate(RotateDirections direction) {
		Location loc=droneAvatar.getLocation();
		float yaw=loc.getYaw();
		
		if(direction==RotateDirections.Left)
			yaw-=90;
		else
			yaw+=90;
		
		if(yaw>=360)
			yaw-=360;	
		if(yaw<0)
			yaw+=360;
		
		loc.setYaw(yaw);
		
		MCUtils.adjustYawAndPitch(loc);	
		droneAvatar.teleport(loc, TeleportCause.PLUGIN);
	}
	
	
	 /** 
     * Moves the drone one block in specified direction.
     * Will fail if destination block is occupied by a block. (non air)
     *
     * @param direction the direction to move the drone
     * @return          success property of return value is <code>true</code> if the drone was successfully moved, <code>false</code> otherwise. 
     * 					If not success, resultText property of return value may tell why it failed.
     */
	public DroneManagerResult Move(Directions direction) {
		Location loc=droneAvatar.getLocation();
			
		MCUtils.AdjustToNearbyLocation(direction, loc);
		MCUtils.AdjustLocationToCentreOfBlock(loc);
		
		//Alternative: Check for Material.IsSolid()
		if(loc.getBlock().getType()==Material.AIR) {
			droneAvatar.teleport(loc, TeleportCause.PLUGIN);
			return new DroneManagerResult(true,null);
		}
		else {
			return new DroneManagerResult(false, "Destination contains: " + loc.getBlock().getType());			
		}	
	}
	
	 /** 
     * Mines a block adjacent to drone.
     * Will fail if destination block is empty/not mineable. (air or liquid)
     *
     * @param direction the direction to mine. 
     * @return          success property of return value is <code>true</code> if the block was successfully mined, <code>false</code> otherwise. 
     * 					If not success, resultText property of return value may tell why it failed.
     */	
	public DroneManagerResultMine MineBlock(Directions direction) {
		Location loc=droneAvatar.getLocation();			
		MCUtils.AdjustToNearbyLocation(direction, loc);
		
		Block blockToMine=loc.getBlock();
		
		if(blockToMine.isEmpty() || blockToMine.isLiquid()) {
			return new DroneManagerResultMine(false,"Block is empty or liquid",null,null);
		}		
		Collection<ItemStack> blockDrops=blockToMine.getDrops();
		blockToMine.setType(Material.AIR);
		
		
		ArrayList<ItemStack> inventoryItems=new ArrayList<ItemStack>();
		ArrayList<ItemStack> leftItems=new ArrayList<ItemStack>();
		for(ItemStack stack : blockDrops) {
			HashMap<Integer,ItemStack> overflowItems = this.droneAvatar.getInventory().addItem(stack);
			if(overflowItems==null || overflowItems.isEmpty()) {
				inventoryItems.add(stack);
			}
			else {
				for(ItemStack surplusStack : overflowItems.values()) {
					leftItems.add(surplusStack);
					if(surplusStack.getAmount() < stack.getAmount()) {
						stack.setAmount(stack.getAmount() - surplusStack.getAmount());
						inventoryItems.add(stack);
					}
				}
			}
		}
		
		return new DroneManagerResultMine(true,"",inventoryItems,leftItems);
	}
	
	//Todo: Check if item (material) is placeable. (Only allow certain materials?)
	public DroneManagerResult Build(Directions direction, int inventoryIndex) {
		if(inventoryIndex<0 || inventoryIndex >= droneAvatar.getInventory().getSize()) {
			return new DroneManagerResult(false, "Inventoryindex out of range.");
		}

		Location loc=droneAvatar.getLocation();			
		MCUtils.AdjustToNearbyLocation(direction, loc);	
		Block blockToReplace=loc.getBlock();
		
		if(!(blockToReplace.isEmpty() || blockToReplace.isLiquid())) {
			return new DroneManagerResult(false, "Destination contains: " + blockToReplace.getType());
		}
		
		ItemStack stackToPlaceFrom = droneAvatar.getInventory().getItem(inventoryIndex);
		
		if(stackToPlaceFrom==null || stackToPlaceFrom.getAmount()==0 || stackToPlaceFrom.getType()==Material.AIR) {
			return new DroneManagerResult(false, "Inventory does not contain any item at index: " + inventoryIndex);
		}
		
		Material blockMaterial=stackToPlaceFrom.getType();
		int newAmount=stackToPlaceFrom.getAmount()-1;
		if(newAmount==0)
			droneAvatar.getInventory().clear(inventoryIndex);
		else
			stackToPlaceFrom.setAmount(newAmount);

		blockToReplace.setType(blockMaterial);		
		return new DroneManagerResult(true,null);
	}
	
	public ArrayList<Block> Look(Directions direction) {
		ArrayList<Block> retValue=new  ArrayList<Block>();		
		Location loc=droneAvatar.getLocation();
		
		int i=0;
		boolean isAbleToSeeThroughBlock=true;
		
		while(i < MAX_LINE_OF_SIGHT && isAbleToSeeThroughBlock) {
			MCUtils.AdjustToNearbyLocation(direction, loc);		
			Block blockToMine=loc.getBlock();
			
			retValue.add(blockToMine);
			isAbleToSeeThroughBlock = !blockToMine.getType().isOccluding();	
			i++;
		}
		return retValue;
	}

	public int GetInventoryIndex(Material material){
		return droneAvatar.getInventory().first(material);
	}
	
	public ItemStack GetInventoryItem(int index){
		return droneAvatar.getInventory().getItem(index);
	}
	
	public int GetInventorySize() {
		return droneAvatar.getInventory().getSize();				
	}
	
	public Inventory GetInventory() {
		return droneAvatar.getInventory();				
	}
	
	//-1 if placeable item not found in inventory
	//TODO: Exclude non-placeable/weird items
	public int FindPlaceableItemInventoryIndex() {
		boolean found=false; 
		int i=0;
		while(!found && i<GetInventorySize()) {
			ItemStack invContent=GetInventoryItem(i);
			if(invContent!=null && invContent.getType()!=Material.AIR && invContent.getAmount()>0) {
				found=true;
			}
			else {
				i++;
			}
		}
		
		if(!found)
			i=-1;
		return i;
	}
	

	
	public enum Directions {
		Up,
		Down,
		Left,
		Right,
		Forward,
		Back;
		
		public static final Directions ParseFromString(String s){
			String directionString=s.toLowerCase();
			Directions directionArgument=null;

			if(directionString.startsWith("d"))
				directionArgument=Down;
			else if(directionString.startsWith("u"))
				directionArgument=Up;
			else if(directionString.startsWith("l"))
				directionArgument=Left;
			else if(directionString.startsWith("r"))
				directionArgument=Right;
			else if(directionString.startsWith("f"))
				directionArgument=Forward;
			else if(directionString.startsWith("b"))
				directionArgument=Back;

			return directionArgument;
		}	
	}
	
	public enum RotateDirections {
		Left,
		Right;
		
		public static final RotateDirections ParseFromString(String s){
			String directionString=s.toLowerCase();
			RotateDirections directionArgument=null;

			if(directionString.startsWith("l"))
				directionArgument=Left;
			else if(directionString.startsWith("r"))
				directionArgument=Right;

			return directionArgument;
		}
	}
}

