package net.ulveseth.ScriptedDrone;

import java.text.DecimalFormat;

import net.ulveseth.ScriptedDrone.Drone.Directions;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
//import org.bukkit.util.Vector;

public class MCUtils {
    private final static String PREFIX = ChatColor.GOLD + "[" + ChatColor.BLUE + "Drone" + ChatColor.GOLD + "]" + ChatColor.RESET + " ";
    
    public static void sendMessageWithPrefix(CommandSender receiver, String message) {
    	receiver.sendMessage(PREFIX + message);
    }
       
	//Sets pitch to 0 (straight forward) and rounds yaw to multiples of 90 degrees.
	//Forces entity to look straight north, west, south or east.
	//Forces entity to be exact inside a block (rounds x, y, z to no decimals)
	//Returns new yaw as 0,1,2 or 3 (0=0, 1=90, 2=180, 3=270)
	public static int adjustYawAndPitch(Location loc) {
		int rotationQuadValue = Math.round(loc.getYaw() / (float)90);
		if(rotationQuadValue >= 4)
			rotationQuadValue -= 4;
		loc.setYaw(rotationQuadValue * 90); 
		loc.setPitch(0);
		return rotationQuadValue;
	}
	
	public static void AdjustLocationToCentreOfBlock(Location loc) {
		Location blockLoc = loc.getBlock().getLocation();
		double offset=0.5;
		double newX=blockLoc.getX()+offset;
		double newY=blockLoc.getY();
		double newZ=blockLoc.getZ()+offset;
		loc.setX(newX);
		loc.setY(newY);
		loc.setZ(newZ);
	}
	
	
//	private static final Vector GetNearbyLocation(Directions direction, int rotationQuadValue){
//		switch(direction) {
//		case Up:
//			return new Vector(0.0, 1.0, 0.0);
//		case Down:
//			return new Vector(0.0, -1.0, 0.0);
//		case Back:
//			switch(rotationQuadValue) {
//			case 0:
//				return new Vector(0.0, 0.0, -1.0);
//			case 1:
//				return new Vector(1.0, 0.0, 0.0);
//			case 2:
//				return new Vector(0.0, 0.0, 1.0);
//			case 3:
//				return new Vector(-1.0, 0.0, 0.0);
//			}
//			break;
//		case Forward:
//			switch(rotationQuadValue) {
//			case 0:
//				return new Vector(0.0, 0.0, 1.0);
//			case 1:
//				return new Vector(-1.0, 0.0, 0.0);
//			case 2:
//				return new Vector(0.0, 0.0, -1.0);
//			case 3:
//				return new Vector(1.0, 0.0, 0.0);
//			}
//			break;
//		case Left:
//			switch(rotationQuadValue) {
//			case 0:
//				return new Vector(1.0, 0.0, 0.0);
//			case 1:
//				return new Vector(0.0, 0.0, -1.0);
//			case 2:
//				return new Vector(-1.0, 0.0, 0.0);
//			case 3:
//				return new Vector(0.0, 0.0, 1.0);
//			}
//			break;
//		case Right:
//			switch(rotationQuadValue) {
//			case 0:
//				return new Vector(-1.0, 0.0, 0.0);
//			case 1:
//				return new Vector(0.0, 0.0, 1.0);
//			case 2:
//				return new Vector(1.0, 0.0, 0.0);
//			case 3:
//				return new Vector(0.0, 0.0, -1.0);
//			}
//			break;
//		default:
//			break;
//		}
//		return null;
//	}
	
	public static void AdjustToNearbyLocation(Directions direction, Location loc) {
		int rotationQuadValue = adjustYawAndPitch(loc);

		//You will not believe me, but once upon a time I was quite good at math... 
		switch(direction) {
			case Up:
				loc.setY(loc.getY()+1.0);
				break;
			case Down:
				loc.setY(loc.getY()-1.0);
				break;
			case Back:
				switch(rotationQuadValue) {
				case 0:
					loc.setZ(loc.getZ()-1.0);
					break;
				case 1:
					loc.setX(loc.getX()+1.0);
					break;
				case 2:
					loc.setZ(loc.getZ()+1.0);
					break;
				case 3:
					loc.setX(loc.getX()-1.0);
					break;
				}
				break;
			case Forward:
				switch(rotationQuadValue) {
				case 0:
					loc.setZ(loc.getZ()+1.0);
					break;
				case 1:
					loc.setX(loc.getX()-1.0);
					break;
				case 2:
					loc.setZ(loc.getZ()-1.0);
					break;
				case 3:
					loc.setX(loc.getX()+1.0);
					break;
				}
				break;
			case Left:
				switch(rotationQuadValue) {
				case 0:
					loc.setX(loc.getX()+1.0);
					break;
				case 1:
					loc.setZ(loc.getZ()-1.0);
					break;
				case 2:
					loc.setX(loc.getX()-1.0);
					break;
				case 3:
					loc.setZ(loc.getZ()+1.0);
					break;
				}
				break;
			case Right:
				switch(rotationQuadValue) {
				case 0:
					loc.setX(loc.getX()-1.0);
					break;
				case 1:
					loc.setZ(loc.getZ()+1.0);
					break;
				case 2:
					loc.setX(loc.getX()+1.0);
					break;
				case 3:
					loc.setZ(loc.getZ()-1.0);
					break;
			}
				break;
			default:
				break;
		}
	}

    
    public static String FriendlyStringFromLocation(Location loc) {
		DecimalFormat df = new DecimalFormat("#.00");		
		String locationString=	"X: " + df.format(loc.getX()) + ", Y: " + df.format(loc.getY()) + ", Z: " + df.format(loc.getZ()) + 
								", yaw:" + df.format(loc.getYaw());	// + ", pitch: " + df.format(loc.getPitch())
		return locationString;
	}
    

}
