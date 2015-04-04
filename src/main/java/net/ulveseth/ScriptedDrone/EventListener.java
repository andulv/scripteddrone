package net.ulveseth.ScriptedDrone;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleDestroyEvent;

public class EventListener implements Listener {
	private final DroneManager droneManager;
	private final Logger logger;
	
	public EventListener(DroneManager droneManager, Logger logger) {
		this.droneManager=droneManager;
		this.logger=logger;
	}
	
	private Logger getLogger() {
		return logger;
	}
	
   @EventHandler
    public void destroyVehicle(VehicleDestroyEvent event) {
	   getLogger().log(Level.INFO, "destroyVehicle. Vehicle destroyed.");
	   Vehicle v=event.getVehicle();
        if (v instanceof StorageMinecart) {
        	getLogger().log(Level.INFO, "destroyVehicle. Vehicle is StorageMinecart.");
    		Drone deadDrone=droneManager.getDroneByAvatarUUID(v.getUniqueId());
    		if(deadDrone!=null) {
    			getLogger().log(Level.INFO, "destroyVehicle. StorageMinecart is a drone.");   			
    			droneManager.deleteDrone(event.getVehicle().getUniqueId());
    			Player ownerPlayer = Bukkit.getPlayer(deadDrone.ownerPlayerUUID);
    			if(ownerPlayer.isOnline()) {
    				getLogger().log(Level.INFO, "destroyVehicle. Sending message to owner.");   			   				
    				MCUtils.sendMessageWithPrefix(ownerPlayer, "Your drone, "+ deadDrone.droneName +  ", has been destroyed.");
    			}    			 
    		}	        	
        }
    }

}
