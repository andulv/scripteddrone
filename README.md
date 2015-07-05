# scripteddrone
Plugin for Bukkit / Spigot Minecraft servers. 

Adds new entity to game: Drone. 

Drones are "hovering" minecarts with a inventory. They can move freely in any direction, but only one block at a time. In addition to moving around, the drones can also mine blocks (drops from the mined block goes into the drones inventory) and build (using blocks from same inventory).

In-game the drone can be controlled with commands. Drone move up, Drone move left, Drone mine down, Drone build <...>, etc.

But using the in-game commands is probably no fun. :)

This plugin also contains a built in webserver. The webserver exposes a simple (REST-inspired) JSON API for controlling the drone. This API can be invoked from any client-language that is able to make HTTP GET requests.

For easy access to the API, there is also a HTML interface (webpage) that allows the user to see information about the drone and write/run javascript snippets that controls the drone. 
