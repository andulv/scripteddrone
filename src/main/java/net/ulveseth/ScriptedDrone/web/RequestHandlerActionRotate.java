package net.ulveseth.ScriptedDrone.web;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.json.simple.JSONObject;

import net.ulveseth.ScriptedDrone.Drone;
import net.ulveseth.ScriptedDrone.PluginMain;

public class RequestHandlerActionRotate extends RequestHandlerBase {

	public RequestHandlerActionRotate() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected RequestResult HandleRequestCore(String[] args, Map<String, Object> objectBag) {
		if(args ==null || args.length==0) {
			return RequestResult.Error(404, "Could not process Rotate command. The direction must specified.");
		}
		
		Drone.RotateDirections directionArgument=Drone.RotateDirections.ParseFromString(args[0]);
		
		if(directionArgument==null) {
			return RequestResult.Error(404, "Could not process Rotate command. Unkown direction.");
		}
				
		Drone drone=(Drone)objectBag.get("drone");
		
		try {
			DoRotateInBukkitThread(drone, directionArgument);
		} catch (InterruptedException | ExecutionException e) {
			return RequestResult.Error(500, "Failed to execute Rotate command on drone: " + e.getMessage());
		}
		
		JSONSerializer serializer=(JSONSerializer)objectBag.get("serializer");
		JSONObject retValue=new JSONObject();
		JSONSerializer.setValue(retValue, "Location", serializer.serializeLocation(drone.GetLocation()));
		JSONSerializer.setValue(retValue, "LineOfSight", serializer.serializeLineOfSight(drone));
		return RequestResult.Success(retValue.toJSONString());
	}
	
	private Location DoRotateInBukkitThread(final Drone drone, final Drone.RotateDirections directionArgument) 
			throws InterruptedException, ExecutionException {
		Future<Location> future = Bukkit.getServer().getScheduler().callSyncMethod(PluginMain.getInstance(), new Callable<Location>(){

			@Override
			public Location call() throws Exception {
				drone.Rotate(directionArgument);
				return drone.GetLocation();
			}
		});
		return future.get();
	}
	

}
