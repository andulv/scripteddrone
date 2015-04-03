package net.ulveseth.ScriptedDrone;

import net.ulveseth.ScriptedDrone.web.DroneAPIWebServer;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class PluginMain extends JavaPlugin {
    private static PluginMain instance;
      
    private DroneAPIWebServer webApiServer;
    private DroneManager droneManager;
    
    @Override
    public void onEnable() {
        PluginMain.instance = this;

        if (!getDataFolder().exists()) {
            boolean success = getDataFolder().mkdir();
            if (!success) {
                getLogger().warning("Failed to create plugin directory!");
            }
        }

        File config = new File(getDataFolder(), "config.yml");
        if (!config.exists()) {
            saveDefaultConfig();
        }
        
        droneManager=new DroneManager();
        File dronesFile = new File(getDataFolder(), "drones.yml");
        if (dronesFile.exists()) {
            try {
        		PluginMain.getInstance().getLogger().info("drones.yml found. Loading and parsing.");
        		droneManager.loadFile(dronesFile);
			} catch (IOException e) {
				getLogger().log(java.util.logging.Level.WARNING, "Failed to load drones.yml",e);				
			}
        }
                               
        webApiServer=new DroneAPIWebServer(getLogger(), droneManager);
        try {
        	webApiServer.StartServer(8086);
		} catch (IOException e) {
			getLogger().log(java.util.logging.Level.SEVERE, "Failed to start http server",e);
		}

        getServer().getPluginCommand("drone").setExecutor(new CommandListener(droneManager));
        getServer().getPluginManager().registerEvents(new EventListener(droneManager,getLogger()), this);
        
        getLogger().info("ScriptedDrone (v" + getDescription().getVersion() + ") has been successfully enabled!");
    }

    @Override
    public void onDisable() {
    	
        File dronesFile = new File(getDataFolder(), "drones.yml");
        try {
    		PluginMain.getInstance().getLogger().info("Saving drones.yml");
    		droneManager.saveFile(dronesFile);
		} catch (IOException e) {
			getLogger().log(java.util.logging.Level.WARNING, "Failed to save drones.yml",e);				
		}
    	
    	if(webApiServer!=null) {
    		webApiServer.StopServer();
    		webApiServer=null;
    	}
    }

    /**
     * Obtains a instance of the plugin
     *
     * @return A PluginMain instance
     */
    public static PluginMain getInstance() {
        return instance;
    }
   
}
