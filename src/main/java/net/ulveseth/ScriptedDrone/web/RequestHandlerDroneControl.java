package net.ulveseth.ScriptedDrone.web;

import java.util.Map;

import net.ulveseth.ScriptedDrone.Drone;
import net.ulveseth.ScriptedDrone.DroneManager;
import net.ulveseth.ScriptedDrone.PluginMain;
import net.ulveseth.ScriptedDrone.Utils;

//Expects to find instance of DroneManager with key "droneManager" in objectBag
public class RequestHandlerDroneControl extends RequestHandlerBase {

	public RequestHandlerDroneControl() {
		//Inneholder actions
		//Inneholder inventory
		//Inneholder information
		//Inneholder communication
	}
	
	@Override
	public RequestResult HandleRequest(String[] args, Map<String, Object> objectBag) {
		PluginMain.getInstance().getLogger().info("RequestHandlerBase.HandleRequest. args " + Utils.GetDebugStringFromArgs(args) );
		
		String droneName=args[0];
		DroneManager droneManager = (DroneManager)objectBag.get("droneManager");

		Drone drone=droneManager.getDroneByName(droneName);
		if(drone==null) {
			return RequestResult.Error(404, "Drone with name " + droneName + " was not found");
		}
		
		objectBag.put("drone", drone);
		String[] remainingArgs = Utils.ExtractRemainingArgs(args, 1);
		return GetResultFromSubHandlerOrSelf(remainingArgs, objectBag);
	}
	
	//Will only be called if request is of type droneControl/<validDroneName>/unkowncommand
	@Override
	protected RequestResult HandleRequestCore(String[] args, Map<String, Object> objectBag) {
		return RequestResult.Error(404, "Invalid URL/arguments");
	}

}
