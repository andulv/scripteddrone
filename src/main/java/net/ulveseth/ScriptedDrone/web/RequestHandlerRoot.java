package net.ulveseth.ScriptedDrone.web;

import java.util.Map;

public class RequestHandlerRoot extends RequestHandlerBase {

	public RequestHandlerRoot() {
		//Inneholder files
		//Inneholder droneApi.js
		//Inneholder dronecontrol
		//favicon.ico?
	}
	
	@Override
	protected RequestResult HandleRequestCore(String[] args, Map<String, Object> objectBag) {
		return RequestResult.Success("This is the root folder");
		
	}

}
