package net.ulveseth.ScriptedDrone;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;


public class DroneManager {
	Drone activeDrone;
	
	Map<String,Drone> dronesByName=new HashMap<String,Drone>();
	Map<UUID,Drone>  dronesByEntityId=new HashMap<UUID,Drone>();
	Map<UUID,Drone>  selectedDronesByOwnerEntityId=new HashMap<UUID,Drone>();
	
	private Logger getLogger() {
		return PluginMain.getInstance().getLogger();
	}
	
	public List<Drone> getDrones(UUID playerUuid) {
		ArrayList<Drone> drones=new ArrayList<Drone>();
		for (Map.Entry<String, Drone> nameDrone : dronesByName.entrySet()) {
			if(playerUuid==null || playerUuid.equals(nameDrone.getValue().ownerPlayerUUID)) {
				drones.add(nameDrone.getValue());
			}
		}
		return drones;
	}
	
	public boolean isManagedDrone(StorageMinecart cart) {
		return dronesByEntityId.containsKey(cart.getUniqueId());
	}
	
	public Drone getSelectedDroneForPlayer(Player player) {
		if (!selectedDronesByOwnerEntityId.containsKey(player.getUniqueId()))
			return null;
		
		return selectedDronesByOwnerEntityId.get(player.getUniqueId());		
	}
	
	public DroneManagerResult setSelectedDroneForPlayer(Player player, String droneName) {
		Drone drone=getDroneByName(droneName);
		if(drone==null) {
			return new DroneManagerResult(false,"Drone does not exist");
		}
		
		if(!drone.ownerPlayerUUID.equals(player.getUniqueId())) {
			return new DroneManagerResult(false,"Drone belongs to different player.");		
		}
		
		selectedDronesByOwnerEntityId.put(player.getUniqueId(), drone);
		return new DroneManagerResult(true,"");
	}
	
	public Drone getDroneByName(String droneName) {
		if(dronesByName.containsKey(droneName)) {
			return dronesByName.get(droneName);
		}
		return null;
	}
	
	public Drone getDroneByAvatarUUID(UUID droneUUID) {
		if(dronesByEntityId.containsKey(droneUUID)) {
			return dronesByEntityId.get(droneUUID);
		}
		return null;
	}
	
	public void saveFile(File file) throws IOException {
		FileConfiguration config = new YamlConfiguration();

		ConfigurationSection dronesSection = config.createSection("drones");		
		for (Map.Entry<String, Drone> nameDrone : dronesByName.entrySet()) {
			Drone drone=nameDrone.getValue();
			if(drone.droneAvatar!=null && drone.droneAvatar.isValid()) {
				ConfigurationSection signData = dronesSection.createSection(nameDrone.getKey());
				signData.set("owner", drone.ownerPlayerUUID.toString());
				signData.set("droneavatar", drone.droneAvatar.getUniqueId().toString());
				signData.set("location", drone.droneAvatar.getLocation());
			}
		}
		
		ConfigurationSection selectedDronesSection = config.createSection("selecteddrones");		
		for (Map.Entry<UUID, Drone> nameDrone : selectedDronesByOwnerEntityId.entrySet()) {						
			Drone drone=nameDrone.getValue();
			if(drone.droneAvatar!=null && drone.droneAvatar.isValid()) {
				selectedDronesSection.set(drone.ownerPlayerUUID.toString(), drone.droneName);
			}
		}
		
		config.save(file);
	}
	
	public void loadFile(File file) throws IOException {
		
		FileConfiguration config = YamlConfiguration
				.loadConfiguration(file);
		
		ConfigurationSection dronesSection = config.getConfigurationSection("drones");
		
		if(dronesSection==null)
			return;
		
		
		//Recreates Drone objects, but without droneAvatar set.
		for (String key : dronesSection.getKeys(false)) {
			UUID droneOwnerUUID = UUID.fromString(dronesSection.getString(key + ".owner"));
			UUID droneAvatarUUID=UUID.fromString(dronesSection.getString(key + ".droneavatar"));
			Drone drone=new Drone(null, droneOwnerUUID, key);		
			Location pos=(Location)dronesSection.get(key + ".location");
			
			//Ensures that chunk with drone is loaded, so that entity exists and can be retrieved with getEntities.
			pos.getChunk().load();
			
			dronesByEntityId.put(droneAvatarUUID, drone);
			dronesByName.put(key, drone);
		}
						
		//Finds droneAvatar (StorageMinecart) instances and sets droneAvatar property in corresponding Drone object. 
		int iEntities=0, iStorageMinecarts=0, iDrones=0;
		for(World w:Bukkit.getServer().getWorlds()){
            for(Entity e: w.getEntities()){
            	iEntities++;
            	if(e instanceof StorageMinecart) {
            		iStorageMinecarts++;
            		if(dronesByEntityId.containsKey(e.getUniqueId())) {
            			iDrones++;
            			Drone drone=dronesByEntityId.get(e.getUniqueId());
            			
            			StorageMinecart cart=(StorageMinecart)e;
            			drone.droneAvatar=cart;
            			
            	    	cart.setDerailedVelocityMod(new Vector());
            	    	cart.setFlyingVelocityMod(new Vector());
            	    	cart.setMaxSpeed(0d);
            	    	cart.setVelocity(new Vector(0,0,0));
            		}
            	}
            }  
        }
		
		getLogger().log(Level.INFO, "Entities: " + iEntities + ", StorageMinecarts: " + iStorageMinecarts + ", Drones: " + iDrones);
		
		removeDeadDrones();
			
		ConfigurationSection selectedDronesSection = config.getConfigurationSection("selecteddrones");				
		for (String key : selectedDronesSection.getKeys(false)) {
			UUID droneOwnerUUID = UUID.fromString(key);
			String droneName = selectedDronesSection.getString(key);
			
			if(dronesByName.containsKey(droneName)) {
				Drone drone=dronesByName.get(droneName);
				selectedDronesByOwnerEntityId.put(droneOwnerUUID, drone);
			}
		}
	}

	private void removeDeadDrones() {
		//Removes Drones if droneAvatar (StorageMinecart) is not found/valid
		Iterator<Map.Entry<UUID,Drone>> iter = dronesByEntityId.entrySet().iterator();
		while (iter.hasNext()) {
		    Map.Entry<UUID,Drone> entry = iter.next();
			Drone drone=entry.getValue();
			if(drone.droneAvatar==null || !drone.droneAvatar.isValid()) {
				getLogger().log(Level.INFO, "Removing missing drone: " + drone.droneName);
				iter.remove();
				dronesByName.remove(drone.droneName);
			}
		}
	}
	
	public DroneManagerResultGeneric<Drone> createAndAddNewDrone(Player ownerPlayer, String droneName) {		
		if(dronesByName.containsKey(droneName)) {
			return new DroneManagerResultGeneric<Drone>(false, "Drone with same name already exists.", null);
		}		
			
		StorageMinecart droneAvatar=createMinecartForDrone(ownerPlayer,droneName);
		Drone drone=new Drone(droneAvatar,ownerPlayer.getUniqueId(),droneName);
		
		dronesByEntityId.put(droneAvatar.getUniqueId(), drone);
		dronesByName.put(droneName, drone);
		selectedDronesByOwnerEntityId.put(ownerPlayer.getUniqueId(), drone);  //Sets newly created drone as selected drone for player.
		return new DroneManagerResultGeneric<Drone>(true, "Drone spawned at location: " + MCUtils.FriendlyStringFromLocation(droneAvatar.getLocation()), drone);		
	}
	
	private StorageMinecart createMinecartForDrone(Player player, String droneName) {
    	Location spawnLocation=player.getEyeLocation();
    	spawnLocation.add(spawnLocation.getDirection());
    	spawnLocation.add(spawnLocation.getDirection());
    	spawnLocation.add(spawnLocation.getDirection());
    	
    	MCUtils.AdjustLocationToCentreOfBlock(spawnLocation);

    	spawnLocation.setYaw(0);
    	spawnLocation.setPitch(0);
    	
    	StorageMinecart cart=(StorageMinecart)player.getWorld().spawnEntity(spawnLocation, EntityType.MINECART_CHEST);
    	cart.setDerailedVelocityMod(new Vector());
    	cart.setFlyingVelocityMod(new Vector());
    	cart.setMaxSpeed(0d);
    	cart.setVelocity(new Vector(0,0,0));
    	cart.setCustomName(droneName);
    	cart.setCustomNameVisible(true);
    	MaterialData mdPumpkin=new MaterialData(Material.JACK_O_LANTERN);
    	
    	//http://minecraft.gamepedia.com/Data_values#Pumpkins_and_Jack_o.27Lanterns
    	//Data value indicates direction of pumpkin face. This seems to align with yaw 0 for minecart location.  
    	mdPumpkin.setData((byte)3);		
    	
    	cart.setDisplayBlock(mdPumpkin);
    	cart.setDisplayBlockOffset(13);  //Height above cart to place pumpkin (value found by trial and error)
    	
    	MCUtils.sendMessageWithPrefix(player, "Drone spawned at location: " + MCUtils.FriendlyStringFromLocation(spawnLocation));
        return cart;
    }
	
	public void deleteDrone(String droneName) {
		Drone removedDrone = dronesByName.remove(droneName);
		dronesByEntityId.remove(removedDrone.ownerPlayerUUID);
		if(selectedDronesByOwnerEntityId.containsKey(removedDrone.ownerPlayerUUID))
			selectedDronesByOwnerEntityId.remove(removedDrone.ownerPlayerUUID);
	}
	
	public void deleteDrone(UUID entityUUID) {
		Drone removedDrone = dronesByEntityId.remove(entityUUID);
		dronesByName.remove(removedDrone.droneName);	
		if(selectedDronesByOwnerEntityId.containsKey(removedDrone.ownerPlayerUUID))
			selectedDronesByOwnerEntityId.remove(removedDrone.ownerPlayerUUID);
	}
	


	

}
