package net.ulveseth.ScriptedDrone;

import java.util.List;
import java.util.UUID;

import net.ulveseth.ScriptedDrone.Drone.Directions;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandListener implements CommandExecutor {   
    private final DroneManager droneManager;
       
    public CommandListener(DroneManager droneManager) {
    	this.droneManager=droneManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String name, String[] args) {   	
        if (args.length == 0) {
            showHelp(sender);
            return true;
        }

    	if(sender instanceof Player) {
    		return ParsePlayerCommand((Player)sender, args);        		
        }
		return false;
    }
    
	private boolean ParsePlayerCommand(Player sender, String[] args) {
		String command=args[0];
		String[] remainingArgs = Utils.ExtractRemainingArgs(args,1);
			
		if(command.equalsIgnoreCase("create")) {
			return ParsePlayerCreateCommand(sender, remainingArgs);
		}
		else if(command.equalsIgnoreCase("move") || command.equalsIgnoreCase("mv")) {
			return ParsePlayerMoveCommand(sender, remainingArgs);
		}
		else if(command.equalsIgnoreCase("rotate")|| command.equalsIgnoreCase("rot")) {
			return ParsePlayerRotateCommand(sender, remainingArgs);
		}
		else if(command.equalsIgnoreCase("mine")|| command.equalsIgnoreCase("mineblock")) {
			return ParsePlayerMineBlockCommand(sender, remainingArgs);
		}
		else if(command.equalsIgnoreCase("build")|| command.equalsIgnoreCase("bld")) {
			return ParsePlayerBuildCommand(sender, remainingArgs);
		}
		else if(command.equalsIgnoreCase("getpos") ||command.equalsIgnoreCase("gp") ||command.equalsIgnoreCase("pos")) {
			return ParsePlayerGetPosCommand(sender);
		}
		else if(command.equalsIgnoreCase("list") ||command.equalsIgnoreCase("lst") ||command.equalsIgnoreCase("ls")) {
			return ParsePlayerListCommand(sender, remainingArgs);
		}
		else if(command.equalsIgnoreCase("select") ||command.equalsIgnoreCase("select")) {
			return ParsePlayerSelectCommand(sender, remainingArgs);
		}
	
		return false;
	}

	private boolean ParsePlayerCreateCommand(Player sender, String[] args) {
		String droneName = args!=null && args.length>0 
						 	? args[0] 
				 			: sender.getName() + "-drone";
		MCUtils.sendMessageWithPrefix(sender, "Creating drone for you with name: " + droneName );			
		DroneManagerResultGeneric<Drone> droneResult = droneManager.createAndAddNewDrone(sender, droneName);
		
		if(droneResult.success) {
			MCUtils.sendMessageWithPrefix(sender, "Drone has been spawned. Should be in front of your eyes.");			
		}
		else{
			MCUtils.sendMessageWithPrefix(sender, "Could not spawn drone. " + droneResult.resultText);			
		}
		
		return true;
	}

	private boolean ParsePlayerGetPosCommand(Player sender) {
		Drone currentDrone = droneManager.getSelectedDroneForPlayer(sender);
		if(currentDrone==null) {
			MCUtils.sendMessageWithPrefix(sender, "Could not find a drone to control.");
			return false;
		}
		Location loc=currentDrone.GetLocation();
		MCUtils.sendMessageWithPrefix(sender,MCUtils.FriendlyStringFromLocation(loc));
		return true;
	}

	private boolean ParsePlayerMoveCommand(Player sender, String[] args) {
		Drone currentDrone = droneManager.getSelectedDroneForPlayer(sender);
		if(currentDrone==null) {
			MCUtils.sendMessageWithPrefix(sender, "Could not find a drone to control.");
			return false;
		}

		if(args ==null || args.length==0) {
			MCUtils.sendMessageWithPrefix(sender, "Could not parse Move command. The direction must specified.");
			return false;
		}

		Directions directionArgument = Drone.Directions.ParseFromString(args[0]);
		if(directionArgument==null) {		
			MCUtils.sendMessageWithPrefix(sender, "Could not parse Move command. Unknown direction: " + args[0]);
			return false;
		}
	
		currentDrone.Move(directionArgument);
		return true;
	}
	
	private boolean ParsePlayerMineBlockCommand(Player sender, String[] args) {
		Drone currentDrone = droneManager.getSelectedDroneForPlayer(sender);
		if(currentDrone==null) {
			MCUtils.sendMessageWithPrefix(sender, "Could not find a drone to control.");
			return false;
		}

		if(args ==null || args.length==0) {
			MCUtils.sendMessageWithPrefix(sender, "Could not parse Mine command. The direction must specified.");
			return false;
		}

		Directions directionArgument = Drone.Directions.ParseFromString(args[0]);
		if(directionArgument==null) {		
			MCUtils.sendMessageWithPrefix(sender, "Could not parse Mine command. Unknown direction: " + args[0]);
			return false;
		}
	
		currentDrone.MineBlock(directionArgument);
		return true;
	}
	
	private boolean ParsePlayerBuildCommand(Player sender, String[] args) {
		Drone currentDrone = droneManager.getSelectedDroneForPlayer(sender);
		if(currentDrone==null) {
			MCUtils.sendMessageWithPrefix(sender, "Could not find a drone to control.");
			return false;
		}

		if(args ==null || args.length==0) {
			MCUtils.sendMessageWithPrefix(sender, "Could not parse Build command. The direction must specified.");
			return false;
		}

		Directions directionArgument = Drone.Directions.ParseFromString(args[0]);
		if(directionArgument==null) {		
			MCUtils.sendMessageWithPrefix(sender, "Could not parse Build command. Unknown direction: " + args[0]);
			return false;
		}
		
		int i = -1;			
		currentDrone.FindPlaceableItemInventoryIndex();
	
		if(i<0) {
			MCUtils.sendMessageWithPrefix(sender, "Could not execute Build command. Nothing placeable found in inventory.");
			return false;			
		}
		
		MCUtils.sendMessageWithPrefix(sender, "Placing block from inventory with index: " + i);
		currentDrone.Build(directionArgument, i);
		return true;
	}

	private boolean ParsePlayerRotateCommand(Player sender, String[] args) {
		Drone currentDrone = droneManager.getSelectedDroneForPlayer(sender);

		if(currentDrone==null) {
			MCUtils.sendMessageWithPrefix(sender, "Could not find a drone to control.");
			return false;
		}
		
		if(args ==null || args.length==0) {
			MCUtils.sendMessageWithPrefix(sender, "Could not parse Rotate command. The direction must specified.");
			return false;
		}

		String cmdArgDir=args[0].toLowerCase();
		Drone.RotateDirections directionArgument=Drone.RotateDirections.Left;

		if(cmdArgDir.startsWith("l"))
			directionArgument=Drone.RotateDirections.Left;
		else if(cmdArgDir.startsWith("r"))
			directionArgument=Drone.RotateDirections.Right;
		else {
			MCUtils.sendMessageWithPrefix(sender, "Could not parse rotate command. Unkown direction.");
			return false;
		}
			
		currentDrone.Rotate(directionArgument);
		return true;
	}
	
	private boolean ParsePlayerSelectCommand(Player sender, String[] args) {	
		if(args ==null || args.length==0) {
			MCUtils.sendMessageWithPrefix(sender, "Could not parse Select command. Name of drone must be specified.");
			return false;
		}
		
		String cmdArgDronename=args[0];
		DroneManagerResult result = droneManager.setSelectedDroneForPlayer(sender, cmdArgDronename);
		if(StringUtils.isNotEmpty(result.resultText)) {
			MCUtils.sendMessageWithPrefix(sender, result.resultText);
		}

		return result.success;
	}
	
	private boolean ParsePlayerListCommand(Player sender, String[] args) {
		UUID playerUUID=null;
		if(args!=null && args.length>0) {
			String cmdArgPlayername=args[0];
			Player player = Bukkit.getPlayerExact(cmdArgPlayername);
			if(player==null) {
				MCUtils.sendMessageWithPrefix(sender, "Could not execute list command. Unkown playername: " + cmdArgPlayername);
				return false;				
			}		
		}
		else {
			//If player has permission to list others drones: default to list all. If not list only players drones.
			if(!sender.isOp()){		//TODO: change to permission check (ScriptedDrone.List.Others)
				playerUUID=sender.getUniqueId();
			}
		}
		
		List<Drone> droneList=droneManager.getDrones(playerUUID);		
		if(droneList.isEmpty()) {
			MCUtils.sendMessageWithPrefix(sender, "No drones to list.");			
		}
		else {
			for (Drone drone : droneList) {
				String locString=MCUtils.FriendlyStringFromLocation(drone.droneAvatar.getLocation());
				Player ownerPlayer=Bukkit.getPlayer(drone.ownerPlayerUUID);
				Drone selectedDrone = droneManager.getSelectedDroneForPlayer(ownerPlayer);
				String selectedPrefix=drone==selectedDrone ? "* " : "  ";
				MCUtils.sendMessageWithPrefix(sender, selectedPrefix + drone.droneName + ", Owner: " + ownerPlayer.getPlayerListName());
				MCUtils.sendMessageWithPrefix(sender, "    " + locString);
			}
			
			MCUtils.sendMessageWithPrefix(sender, "* indicates currently selected drone for player.");
		}
	
		return true;
	}


    
    /**
     * Shows a small help page to a user, or the console
     *
     * @param sender The player/console to send the help to
     */
    private void showHelp(CommandSender sender) {
        MCUtils.sendMessageWithPrefix(sender, "ScriptedDrone commands:");
        MCUtils.sendMessageWithPrefix(sender, "   /drone create (<droneName>(");
        MCUtils.sendMessageWithPrefix(sender, "         Spawns a new drone in front of you.");
        MCUtils.sendMessageWithPrefix(sender, "   /drone list (<playerName>)");
        MCUtils.sendMessageWithPrefix(sender, "         Lists existing drones.");
        MCUtils.sendMessageWithPrefix(sender, "   /drone select <droneName>");
        MCUtils.sendMessageWithPrefix(sender, "         Selects a drone by its name.");
        MCUtils.sendMessageWithPrefix(sender, "   /drone getpos");
        MCUtils.sendMessageWithPrefix(sender, "         Displays location of selected drone.");
        MCUtils.sendMessageWithPrefix(sender, "   /drone move <up>|<down>|<left>|<right>|<forward>|<back>");
        MCUtils.sendMessageWithPrefix(sender, "         Moves drone one block.");
        MCUtils.sendMessageWithPrefix(sender, "   /drone rotate <left>|<right>");
        MCUtils.sendMessageWithPrefix(sender, "         Rotates drone 90 degrees.");
        MCUtils.sendMessageWithPrefix(sender, "   /drone mine <up>|<down>|<right>|<left>|<forward>|<back>");
        MCUtils.sendMessageWithPrefix(sender, "         Mines a block. Itemdrops are stored in drones inventory.");
        MCUtils.sendMessageWithPrefix(sender, "   /drone build <up>|<down>|<right>|<left>|<forward>|<back>");
        MCUtils.sendMessageWithPrefix(sender, "         Places a block from inventory.");
        MCUtils.sendMessageWithPrefix(sender, "   /drone help");
        MCUtils.sendMessageWithPrefix(sender, "         Shows this help message.");
    }

}
