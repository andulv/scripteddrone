package net.ulveseth.ScriptedDrone.web;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.bukkit.Bukkit;
import org.json.simple.JSONObject;

import net.ulveseth.ScriptedDrone.Drone;
import net.ulveseth.ScriptedDrone.DroneManagerResult;
import net.ulveseth.ScriptedDrone.PluginMain;
import net.ulveseth.ScriptedDrone.Drone.Directions;

public class RequestHandlerActionBuild extends RequestHandlerBase {

	public RequestHandlerActionBuild() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected RequestResult HandleRequestCore(String[] args, Map<String, Object> objectBag) {
		if(args ==null || args.length==0) {
			return RequestResult.Error(400, "Could not process Build command. The direction must specified.");
		}
		
		Directions directionArgument=Drone.Directions.ParseFromString(args[0]);
		if(directionArgument==null){		
			return RequestResult.Error(400, "Could not process Build command. Unkown direction.");
		}
		
		Drone drone=(Drone)objectBag.get("drone");
		
		int invIndex=-1;
		if(args.length>1) {
			invIndex = Integer.parseInt(args[1]);
		}
		
		if(invIndex==-1) {
			invIndex = drone.FindPlaceableItemInventoryIndex();
		}
		
		JSONSerializer serializer=(JSONSerializer)objectBag.get("serializer");
		JSONObject retValue=new JSONObject();
		
		if(invIndex<0) {
			JSONSerializer.setValue(retValue, "Success", false);
			JSONSerializer.setValue(retValue, "ResultText", "No material to use for building could be found in inventory.");
			return RequestResult.Success(retValue.toJSONString());
		}
		
		DroneManagerResult buildResult=null;
		
		try {
			buildResult = DoBuildInBukkitThread(drone,directionArgument, invIndex);
		} catch (InterruptedException | ExecutionException e) {
			return RequestResult.Error(500, "Failed to execute Build command on drone: " + e.getMessage());
		}
		
		JSONSerializer.setValue(retValue, "Success", buildResult.getSuccess());
		JSONSerializer.setValue(retValue, "ResultText", buildResult.getResultText());
		JSONSerializer.setValue(retValue, "LineOfSight", serializer.serializeLineOfSight(drone));
		JSONSerializer.setValue(retValue, "Inventory", serializer.serializeInventory(drone.GetInventory()));			
		return RequestResult.Success(retValue.toJSONString());
	}
	
	private DroneManagerResult DoBuildInBukkitThread(final Drone drone, final Directions directionArgument, final int inventoryIndex) throws InterruptedException, ExecutionException {
		Future<DroneManagerResult> future = Bukkit.getServer().getScheduler().callSyncMethod(PluginMain.getInstance(), new Callable<DroneManagerResult>(){

			@Override
			public DroneManagerResult call() throws Exception {
				return drone.Build(directionArgument, inventoryIndex);
			}
		});
		return future.get();
	}
	

}
