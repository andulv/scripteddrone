package net.ulveseth.ScriptedDrone.web;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import net.ulveseth.ScriptedDrone.DroneManager;
import net.ulveseth.ScriptedDrone.PluginMain;
import net.ulveseth.ScriptedDrone.Utils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;


public class DroneAPIHttpHandler implements HttpHandler {
    private final DroneManager droneManager;
    private final RequestHandlerBase handlerRoot;
	
	public DroneAPIHttpHandler(DroneManager droneManager) {
		this.droneManager=droneManager;	
		handlerRoot=new RequestHandlerRoot();
		RequestHandlerBase handlerDroneControl=new RequestHandlerDroneControl();
		//Todo: Use pluginpath + \webserver
		RequestHandlerBase handlerFiles=new RequestHandlerStaticFiles("c:\\webserver\\files\\");	 
		RequestHandlerBase handlerSectionAction=new RequestHandlerBase();
		RequestHandlerBase handlerSectionCommunication=new RequestHandlerBase();
		
		handlerRoot.RegisterHandler("files", handlerFiles);		
		handlerRoot.RegisterHandler("dronecontrol", handlerDroneControl);		
		  handlerDroneControl.RegisterHandler("action", handlerSectionAction);		
			handlerSectionAction.RegisterHandler("mine", new RequestHandlerActionMine());
			handlerSectionAction.RegisterHandler("build", new RequestHandlerActionBuild());
			handlerSectionAction.RegisterHandler("move", new RequestHandlerActionMove());
			handlerSectionAction.RegisterHandler("rotate", new RequestHandlerActionRotate());
		  handlerDroneControl.RegisterHandler("information", new RequestHandlerInformation());		
		  handlerDroneControl.RegisterHandler("communication", handlerSectionCommunication);		
	}

	//http://docs.oracle.com/javase/7/docs/jre/api/net/httpserver/spec/com/sun/net/httpserver/HttpExchange.html
	@Override
	public void handle(HttpExchange httpExchange) throws IOException {	
		
		RequestResult requestResult = RequestResult.Error(500, "internal server error");
		String responseString="internal server error";

		try {
			String requestMethod=httpExchange.getRequestMethod();
			String requestBody=Utils.readStreamAsStringAndClose(httpExchange.getRequestBody());
			final URI requestUri=httpExchange.getRequestURI();
			
			PluginMain.getInstance().getLogger().info("http request: " + requestUri.toString());
			String[] args =Utils.RemoveEmptySegments(requestUri.toString().split("/"),"/");		
			Map<String, Object> objectBag=new HashMap<String, Object>();
			objectBag.put("droneManager", droneManager);
			objectBag.put("serializer", new JSONSerializer());
			requestResult = handlerRoot.HandleRequest(args, objectBag);
			responseString=requestResult.responseBody;
			if(
				(responseString==null || responseString.length()==0 || requestResult.responseCode!=200) 
				&& requestResult.errorMessage!=null && requestResult.errorMessage.length()>0)
					responseString=requestResult.errorMessage;		
		} 
		catch(Exception e) {
			PluginMain.getInstance().getLogger().log(Level.WARNING, "Error handling request", e);
		}
		
		finally {
			WebUtils.sendResponseAndClose(httpExchange, requestResult.responseCode, responseString.getBytes());
		}	
	}
	
}