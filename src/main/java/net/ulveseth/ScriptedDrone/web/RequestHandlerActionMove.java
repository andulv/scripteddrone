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

public class RequestHandlerActionMove extends RequestHandlerBase {

	public RequestHandlerActionMove() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected RequestResult HandleRequestCore(String[] args, Map<String, Object> objectBag) {
		if(args ==null || args.length==0) {
			return RequestResult.Error(400, "Could not process Move command. The direction must specified.");
		}
		
		Directions directionArgument=Drone.Directions.ParseFromString(args[0]);
		if(directionArgument==null){		
			return RequestResult.Error(400, "Could not process Move command. Unkown direction.");
		}
		
		Drone drone=(Drone)objectBag.get("drone");
		
		DroneManagerResult moveresult=null;
		try {
			moveresult = DoMoveInBukkitThread(drone,directionArgument);
		} catch (InterruptedException | ExecutionException e) {
			return RequestResult.Error(500, "Failed to execute move command on drone: " + e.getMessage());
		}
		
		JSONSerializer serializer=(JSONSerializer)objectBag.get("serializer");
		JSONObject retValue=new JSONObject();
		JSONSerializer.setValue(retValue, "Success", moveresult.getSuccess());
		JSONSerializer.setValue(retValue, "ResultText", moveresult.getResultText());
		JSONSerializer.setValue(retValue, "Location", serializer.serializeLocation(drone.GetLocation()));
		JSONSerializer.setValue(retValue, "LineOfSight", serializer.serializeLineOfSight(drone));
		return RequestResult.Success(retValue.toJSONString());
	}
	
	private DroneManagerResult DoMoveInBukkitThread(final Drone drone, final Directions directionArgument) throws InterruptedException, ExecutionException {
		Future<DroneManagerResult> future = Bukkit.getServer().getScheduler().callSyncMethod(PluginMain.getInstance(), new Callable<DroneManagerResult>(){

			@Override
			public DroneManagerResult call() throws Exception {
				return drone.Move(directionArgument);
			}
		});
		return future.get();
	}
	

}
