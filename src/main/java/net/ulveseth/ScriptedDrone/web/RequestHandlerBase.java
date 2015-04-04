package net.ulveseth.ScriptedDrone.web;

import java.util.HashMap;
import java.util.Map;

import net.ulveseth.ScriptedDrone.PluginMain;
import net.ulveseth.ScriptedDrone.Utils;

public class RequestHandlerBase {
	private final Map<String,RequestHandlerBase> SubHandlers = new HashMap<String,RequestHandlerBase>();
	
	public RequestHandlerBase() {
	}
	
	public void RegisterHandler(String id, RequestHandlerBase handler) {
		//Todo: Check for illegal chars in ID (eg. "/")
		SubHandlers.put(id.toLowerCase(), handler);
	}
	
	protected RequestResult GetResultFromSubHandlerOrSelf(String[] args, Map<String, Object> objectBag) {
		if(args!=null && args.length>0 && SubHandlers.containsKey(args[0].toLowerCase())){
			RequestHandlerBase subHandler=SubHandlers.get(args[0].toLowerCase());
			String[] remainingArgs=Utils.ExtractRemainingArgs(args, 1);
			return subHandler.HandleRequest(remainingArgs, objectBag);
		}
		return HandleRequestCore(args, objectBag);
	}
	
	public RequestResult HandleRequest(String[] args, Map<String, Object> objectBag) {

		PluginMain.getInstance().getLogger().info("RequestHandlerBase.HandleRequest. args " + Utils.GetDebugStringFromArgs(args) );
		return GetResultFromSubHandlerOrSelf(args, objectBag);
	}
	
	protected RequestResult HandleRequestCore(String[] args, Map<String, Object> objectBag) {
		return new RequestResult();
	}
}



