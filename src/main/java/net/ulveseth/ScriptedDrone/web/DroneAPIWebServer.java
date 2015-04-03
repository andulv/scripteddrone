package net.ulveseth.ScriptedDrone.web;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

import net.ulveseth.ScriptedDrone.DroneManager;
import net.ulveseth.ScriptedDrone.PluginMain;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

public class DroneAPIWebServer {
    private final DroneManager droneManager;
	private HttpServer _server;
    
    public DroneAPIWebServer(Logger logger, DroneManager droneManager) {
    	this.droneManager=droneManager;
    }
    
	public void StartServer(int port) throws IOException {
		StopServer();
		
		InetSocketAddress address=new InetSocketAddress(port);
		_server=HttpServer.create(address, 50);
		HttpContext ctx = _server.createContext("/",new DroneAPIHttpHandler(droneManager));
		_server.setExecutor(null);
		_server.start();
		PluginMain.getInstance().getLogger().info("http Server is started at:" +_server.getAddress().toString());
	}
	
	public void StopServer() {
		if(_server!=null) {
			_server.stop(0);
			_server=null;
		}
	}
	
	
	

}

