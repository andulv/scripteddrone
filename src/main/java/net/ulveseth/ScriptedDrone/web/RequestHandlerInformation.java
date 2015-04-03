package net.ulveseth.ScriptedDrone.web;

import org.json.simple.JSONObject;
import java.util.Map;
import net.ulveseth.ScriptedDrone.Drone;

public class RequestHandlerInformation extends RequestHandlerBase {
	public RequestHandlerInformation() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected RequestResult HandleRequestCore(String[] args, Map<String, Object> objectBag) {
		Drone drone=(Drone)objectBag.get("drone");
		JSONSerializer serializer=(JSONSerializer)objectBag.get("serializer");
		
		JSONObject retValue=new JSONObject();
		JSONSerializer.setValue(retValue, "Name", drone.GetName());
		JSONSerializer.setValue(retValue, "Location", serializer.serializeLocation(drone.GetLocation()));
		JSONSerializer.setValue(retValue, "Inventory", serializer.serializeInventory(drone.GetInventory()));			
		JSONSerializer.setValue(retValue, "LineOfSight", serializer.serializeLineOfSight(drone));
			
		String retString = retValue.toJSONString();
		return RequestResult.Success(retString);

	}



}
