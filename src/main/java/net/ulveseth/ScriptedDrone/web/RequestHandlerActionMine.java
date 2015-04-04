package net.ulveseth.ScriptedDrone.web;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.bukkit.Bukkit;
import org.json.simple.JSONObject;

import net.ulveseth.ScriptedDrone.Drone;
import net.ulveseth.ScriptedDrone.DroneManagerResultMine;
import net.ulveseth.ScriptedDrone.PluginMain;
import net.ulveseth.ScriptedDrone.Drone.Directions;

public class RequestHandlerActionMine extends RequestHandlerBase {

	public RequestHandlerActionMine() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected RequestResult HandleRequestCore(String[] args, Map<String, Object> objectBag) {
		if(args ==null || args.length==0) {
			return RequestResult.Error(400, "Could not process Mine command. The direction must specified.");
		}
		
		Directions directionArgument=Drone.Directions.ParseFromString(args[0]);
		if(directionArgument==null){		
			return RequestResult.Error(400, "Could not process Mine command. Unkown direction.");
		}
		
		Drone drone=(Drone)objectBag.get("drone");
		
		DroneManagerResultMine mineResult=null;
		try {
			mineResult = DoMineInBukkitThread(drone,directionArgument);
		} catch (InterruptedException | ExecutionException e) {
			return RequestResult.Error(500, "Failed to execute Mine command on drone: " + e.getMessage());
		}
		
		JSONSerializer serializer=(JSONSerializer)objectBag.get("serializer");
		JSONObject retValue=new JSONObject();
		JSONSerializer.setValue(retValue, "Success", mineResult.getSuccess());
		JSONSerializer.setValue(retValue, "ResultText", mineResult.getResultText());
		JSONSerializer.setValue(retValue, "LineOfSight", serializer.serializeLineOfSight(drone));
		JSONSerializer.setValue(retValue, "Inventory", serializer.serializeInventory(drone.GetInventory()));			
		return RequestResult.Success(retValue.toJSONString());
	}
	
	private DroneManagerResultMine DoMineInBukkitThread(final Drone drone, final Directions directionArgument) throws InterruptedException, ExecutionException {
		Future<DroneManagerResultMine> future = Bukkit.getServer().getScheduler().callSyncMethod(PluginMain.getInstance(), new Callable<DroneManagerResultMine>(){

			@Override
			public DroneManagerResultMine call() throws Exception {
				return drone.MineBlock(directionArgument);
			}
		});
		return future.get();
	}
	

}
