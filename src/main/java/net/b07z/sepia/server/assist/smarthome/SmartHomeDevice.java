package net.b07z.sepia.server.assist.smarthome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONObject;

import net.b07z.sepia.server.assist.assistant.LANGUAGES;
import net.b07z.sepia.server.assist.parameters.Room;
import net.b07z.sepia.server.assist.parameters.SmartDevice;
import net.b07z.sepia.server.core.tools.Debugger;
import net.b07z.sepia.server.core.tools.Is;
import net.b07z.sepia.server.core.tools.JSON;

public class SmartHomeDevice {
	
	private String name;
	private String type; 		//see: net.b07z.sepia.server.assist.parameters.SmartDevice.Types
	private String room;		//see: net.b07z.sepia.server.assist.parameters.Room.Types
	private String roomIndex;	//e.g. 1, 212, etc. ... maybe "1st floor" ... thats why its a string
	private String state;		//e.g.: ON, OFF, 1-100, etc.
	private String stateType;	//e.g.: STATE_TYPE_NUMBER_PERCENT
	private String stateMemory;		//state storage for e.g. default values after restart etc.
	private String link;		//e.g. HTTP direct URL to device
	private JSONObject meta;	//space for custom stuff
	
	//global tags used to store SEPIA specific device data in other HUB systems
	public static final String SEPIA_TAG_NAME = "sepia-name";
	public static final String SEPIA_TAG_TYPE = "sepia-type";
	public static final String SEPIA_TAG_ROOM = "sepia-room";
	public static final String SEPIA_TAG_ROOM_INDEX = "sepia-room-index";
	public static final String SEPIA_TAG_DATA = "sepia-data";
	public static final String SEPIA_TAG_MEM_STATE = "sepia-mem-state";
	public static final String SEPIA_TAG_STATE_TYPE = "sepia-state-type";
	public static final String SEPIA_TAG_SET_CMDS = "sepia-set-cmds";
	
	//generalized device states
	public static final String STATE_ON = "ON";
	public static final String STATE_OFF = "OFF";
	public static final String STATE_OPEN = "OPEN";
	public static final String STATE_CLOSED = "CLOSED";
	public static final String STATE_CONNECTED = "CONNECTED";
	public static final String STATE_DISCONNECTED = "DISCONNECTED";
	//public static final String STATE_UP = "up";
	//public static final String STATE_DOWN = "down";
	public static final String REGEX_STATE_ENABLE = "ON|OPEN|CONNECTED|UP|ENABLED";
	public static final String REGEX_STATE_DISABLE = "OFF|CLOSED|DISCONNECTED|DOWN|DISABLED";
	
	//device state types
	public static final String STATE_TYPE_TEXT_BINARY = "text_binary";	//ON, OFF, OPEN, CLOSED, ...
	public static final String STATE_TYPE_TEXT_RAW = "text_raw";		//just text as given by device
	public static final String STATE_TYPE_NUMBER_PLAIN = "number_plain";
	public static final String STATE_TYPE_NUMBER_PERCENT = "number_percent";
	public static final String STATE_TYPE_NUMBER_TEMPERATURE = "number_temperature";
	public static final String STATE_TYPE_NUMBER_TEMPERATURE_C = "number_temperature_c";
	public static final String STATE_TYPE_NUMBER_TEMPERATURE_F = "number_temperature_f";
	public static final String REGEX_STATE_TYPE_NUMBER = "^number_.*";
	public static final String REGEX_STATE_TYPE_TEXT = "^text_.*";
	
	//locals
	private static HashMap<String, String> states_de = new HashMap<>();
	private static HashMap<String, String> states_en = new HashMap<>();
	static {
		states_de.put("on", "an");
		states_de.put("off", "aus");
		states_de.put("open", "offen");
		states_de.put("close", "geschlossen");
		states_de.put("closed", "geschlossen");
		states_de.put("unreachable", "nicht erreichbar");
		states_de.put("home", "zu Hause");
		states_de.put("gone", "weg");
		
		states_en.put("on", "on");
		states_en.put("off", "off");
		states_en.put("open", "open");
		states_en.put("close", "closed");
		states_en.put("closed", "closed");
		states_en.put("unreachable", "unreachable");
		states_en.put("home", "home");
		states_en.put("gone", "gone");
	}
	/**
	 * Translate state value.
	 * If state is unknown returns original string.
	 * @param state - generalized state 
	 * @param language - ISO language code
	 */
	public static String getStateLocal(String state, String language){
		String localName = "";
		state = state.toLowerCase();
		if (language.equals(LANGUAGES.DE)){
			localName = states_de.get(state);
		}else if (language.equals(LANGUAGES.EN)){
			localName = states_en.get(state);
		}
		if (localName == null){
			if (!state.matches("\\d+")){
				Debugger.println(SmartHomeDevice.class.getSimpleName() + 
					" - getStateLocal() has no '" + language + "' version for '" + state + "'", 3);
			}
			return state;
		}else{
			return localName;
		}
	}
	
	//------- main -------
	
	/**
	 * Create new generalized SEPIA smart home device object filled with data obtained by calling specific HUBs etc. 
	 * @param name - device name
	 * @param type - type as seen e.g. in {@link SmartDevice.Types}
	 * @param room - room as seen e.g. in {@link Room.Types}
	 * @param state - e.g. ON, OFF, 1-100, etc.
	 * @param stateType - e.g. STATE_TYPE_NUMBER_PERCENT
	 * @param stateMemory - last known active state e.g. for toggle events to restore previously set brightness etc. 
	 * @param link - direct link to device generated by the specific smart home HUB integration
	 * @param meta - any data generated by the specific smart home HUB integration that might be needed in other HUB methods
	 */
	public SmartHomeDevice(String name, String type, String room, String state, String stateType, String stateMemory, String link, JSONObject meta){
		this.name = name;
		this.type = type;
		this.room = room;
		this.state = state;
		this.stateType = stateType;
		this.stateMemory = stateMemory;
		this.link = link;
		this.meta = meta;
	}
	/**
	 * Create new generalized SEPIA smart home device object to be filled with data obtained by calling specific HUBs etc.
	 */
	public SmartHomeDevice(){}
	
	@Override
	public String toString(){
		return ("name: " + this.name + " - type: " + this.type+ " - room: " + this.room);
	}
	
	/**
	 * Device name
	 * @return
	 */
	public String getName() {
		return name;
	}
	/**
	 * Set object variable (no write to HUB!)
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Device type
	 * @return
	 */
	public String getType() {
		return type;
	}
	/**
	 * Set object variable (no write to HUB!)
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * Device room
	 * @return
	 */
	public String getRoom() {
		return room;
	}
	/**
	 * Set object variable (no write to HUB!)
	 */
	public void setRoom(String room) {
		this.room = room;
	}
	/**
	 * Device room index
	 * @return
	 */
	public String getRoomIndex() {
		return roomIndex;
	}
	/**
	 * Set object variable (no write to HUB!)
	 */
	public void setRoomIndex(String roomIndex) {
		this.roomIndex = roomIndex;
	}
	
	/**
	 * Device state
	 * @return
	 */
	public String getState() {
		return state;
	}
	/**
	 * Set object variable (no write to HUB!)
	 */
	public void setState(String state) {
		this.state = state;
	}
	/**
	 * Type of device state
	 * @return
	 */
	public String getStateType() {
		return stateType;
	}
	/**
	 * Set state type (no write to HUB!)
	 */
	public void setStateType(String stateType) {
		this.stateType = stateType;
	}
	/**
	 * Write state to HUB.
	 * @param hub - HUB to write to
	 * @param newState - state to write
	 * @param stateType - type of state variable, e.g. {@link SmartHomeDevice#STATE_TYPE_NUMBER_PERCENT} = number in percent
	 * @return success/error
	 */
	public boolean writeState(SmartHomeHub hub, String newState, String stateType){
		return hub.setDeviceState(this, newState, stateType);
	}
	
	/**
	 * Device state memory
	 * @return
	 */
	public String getStateMemory() {
		return stateMemory;
	}
	/**
	 * Set object variable (no write to HUB!)
	 */
	public void setStateMemory(String stateMemory) {
		this.stateMemory = stateMemory;
	}
	/**
	 * Write state memory to HUB.
	 * @param hub - HUB to write to
	 * @param newStateMem - state to write
	 * @return success/error
	 */
	public boolean writeStateMemory(SmartHomeHub hub, String newStateMem){
		return hub.setDeviceStateMemory(this, newStateMem);
	}
	
	/**
	 * Device direct link
	 * @return
	 */
	public String getLink() {
		return link;
	}
	/**
	 * Set object variable (no write to HUB!)
	 */
	public void setLink(String link) {
		this.link = link;
	}
	
	/**
	 * Device custom meta data
	 * @return
	 */
	public JSONObject getMeta() {
		return meta;
	}
	/**
	 * Get certain value of meta data as string.
	 * @param key
	 * @return
	 */
	public String getMetaValueAsString(String key){
		if (meta != null){
			return JSON.getString(meta, key);
		}else{
			return null;
		}
	}
	/**
	 * Set object variable (no write to HUB!)
	 */
	public void setMeta(JSONObject meta) {
		this.meta = meta;
	}
	/**
	 * Set object variable (no write to HUB!)
	 */
	public void setMetaValue(String key, Object value) {
		if (meta == null){
			meta = new JSONObject();
		}
		JSON.put(meta, key, value);
	}
	
	/**
	 * Export device to JSON.
	 * @return
	 */
	public JSONObject getDeviceAsJson(){
		//create common object
		JSONObject newDeviceObject = JSON.make(
				"name", name, 
				"type", type, 
				"room", room, 
				"state", state,
				"link", link
		);
		JSON.put(newDeviceObject, "room-index", roomIndex);
		JSON.put(newDeviceObject, "state-type", stateType);
		JSON.put(newDeviceObject, "state-memory", stateMemory);		//NOTE: this CAN BE smart HUB specific (not identical to generalized state value)
		JSON.put(newDeviceObject, "meta", meta);
		return newDeviceObject;
	}
	/**
	 * Import device from JSON object.
	 * @param deviceJson
	 */
	public SmartHomeDevice importJsonDevice(JSONObject deviceJson){
		this.name = JSON.getString(deviceJson, "name");
		this.type = JSON.getString(deviceJson, "type");
		this.room = JSON.getString(deviceJson, "room");
		this.roomIndex = JSON.getString(deviceJson, "room-index");
		this.state = JSON.getString(deviceJson, "state");
		this.stateType = JSON.getString(deviceJson, "state-type");
		this.stateMemory = JSON.getString(deviceJson, "state-memory");
		this.link = JSON.getString(deviceJson, "link");
		this.meta = JSON.getJObject(deviceJson, "meta");
		return this;
	}
	
	//--------- static helper methods ----------
	
	/**
	 * Get devices from the list that match type and room (optionally).
	 * @param devices - map of devices taken e.g. from getDevices()
	 * @param deviceType - type of device (or null), see {@link SmartDevice.Types}
	 * @param roomType - type of room (or null), see {@link Room.Types}
	 * @param roomIndex - e.g. a number (as string) or null
	 * @param maxDevices - maximum number of matches (0 or negative for all possible)
	 * @return list of devices (can be empty)
	 */
	public static List<SmartHomeDevice> getMatchingDevices(Map<String, SmartHomeDevice> devices, 
				String deviceType, String roomType, String roomIndex, int maxDevices){
		List<SmartHomeDevice> matchingDevices = new ArrayList<>();
		//get all devices with right type and optionally room
		int found = 0;
		for (Map.Entry<String, SmartHomeDevice> entry : devices.entrySet()){
			//check type
			SmartHomeDevice data = entry.getValue();
			String thisType = data.getType();
			if (Is.nullOrEmpty(thisType)){
				continue;
			}
			if (Is.notNullOrEmpty(deviceType) && !thisType.equals(deviceType)){
				continue;
			}
			//check room?
			if (Is.notNullOrEmpty(roomType)){
				String thisRoom = data.getRoom();
				if (thisRoom == null || !thisRoom.equals(roomType)){
					continue;
				}else{
					//check room index
					String thisRoomIndex = data.getRoomIndex();
					if (Is.notNullOrEmpty(roomIndex)){
						if (thisRoomIndex == null && roomIndex.equals("1")){	
							//if no room index is defined and user looks for room 1 this is still ok
							matchingDevices.add(data);
							found++;
						}else if (thisRoomIndex == null || !thisRoomIndex.equals(roomIndex)){
							continue;
						}else{
							matchingDevices.add(data);
							found++;
						}
					}else{
						if (thisRoomIndex == null || thisRoomIndex.equals("1")){
							//if room index is not in search this must be null or 1 (1 is OK because it basically is the default room)
							matchingDevices.add(data);
							found++;
						}else{
							continue;
						}
					}
				}
			}else{
				matchingDevices.add(data);
				found++;
			}
			//max results reached?
			if (maxDevices > 0 && found >= maxDevices){
				break;
			}
			//TODO: we should do a device name check too, but this is not taken into account in SmartDevice parameter yet :-( 
			//e.g. "Light 1", "Lamp A" or "Desk-Lamp" ...
			//I suggest to create an additional parameter called SMART_DEVICE_NAME
		}
		return matchingDevices;
	}
	
	/**
	 * Search for a given number in device name and return first match. If match is not found return given index or null.
	 * @param devicesList - list of devices, usually already filtered by device type and room
	 * @param number - a number to look for in device name (e.g. 1 for "Light 1 in living-room") 
	 * @param defaultIndex - list index to return when no match was found, typically 0 (first device as fallback) or -1 (return null, no fallback)
	 * @return match, fallback or null
	 */
	public static SmartHomeDevice findFirstDeviceWithNumberInNameOrDefault(List<SmartHomeDevice> devicesList, int number, int defaultIndex){
		String indexRegExp = ".*(^|\\b|\\D)" + number + "(\\b|\\D|$).*";
		for (SmartHomeDevice shd : devicesList){
			//System.out.println(shd.getName() + " - " + shd.getName().matches(indexRegExp));		//DEBUG
			if (shd.getName().matches(indexRegExp)){
				return shd;
			}
		}
		if (defaultIndex == -1 || defaultIndex > (devicesList.size() - 1)){
			return null;
		}else{
			return devicesList.get(defaultIndex);
		}
	}
	
	/**
	 * Find correct, generalized stateType from given state.
	 * @param state - state as given by parameter
	 * @return found state type or null
	 */
	public static String findStateType(String state){
		String genStateType = null;
		//plain
		if (state.matches("[\\d.,]+")){
			genStateType = STATE_TYPE_NUMBER_PLAIN;
		//percent
		}else if (state.matches(".*[\\d.,]+(\\+s|)(%|pct)\\b.*")){
			genStateType = STATE_TYPE_NUMBER_PERCENT;
		//temperature
		}else if (state.matches(".*[\\d.,]+(\\+s|)(°|deg|)C\\b.*")){
			genStateType = STATE_TYPE_NUMBER_TEMPERATURE_C;
		}else if (state.matches(".*[\\d.,]+(\\+s|)(°|deg|)F\\b.*")){
			genStateType = STATE_TYPE_NUMBER_TEMPERATURE_F;
		}else if (state.matches(".*[\\d.,]+(\\+s|)(°|deg)\\b.*")){
			genStateType = STATE_TYPE_NUMBER_TEMPERATURE;
		//ON/OFF
		}else if (state.matches("(?i)(on|off|open|close(d|)|up|down|(dis|)connected|(in|)active)")){
			genStateType = STATE_TYPE_TEXT_BINARY;
		}
		return genStateType;
	}
	/**
	 * When state type is known try to convert state value itself to generalized SEPIA value.
	 * @param state - found state (as seen by HUB)
	 * @param stateType - predefined or interpreted state type, e.g. STATE_TYPE_NUMBER_PERCENT
	 * @return converted state or original if no conversion possible
	 */
	public static String convertState(String state, String stateType){
		//all numbers
		if (stateType.matches(REGEX_STATE_TYPE_NUMBER) && state.matches(".*\\d.*")){
			//return first number including "," and "." and replace ","
			return state.replaceAll(".*?([\\d.,]+).*", "$1").replaceAll(",", ".").trim();
		//certain texts
		}else if (stateType.equals(STATE_TYPE_TEXT_BINARY)){
			if (state.equalsIgnoreCase("down")){		//NOTE: we assume a fixed state "down" is closed - TODO: we might need deviceType here
				return STATE_CLOSED;
			}else if (state.equalsIgnoreCase("up")){	//NOTE: we assume a fixed state "up" is open
				return STATE_OPEN;
			}else{
				return state;
			}
		//other
		}else{
			return state;
		}
		//TODO: add more?
	}
	
	/**
	 * If state type is a plain number make smart assumption about the intended type using device type info.
	 * E.g.: In "set lights to 50" the 50 is most likely intended to be "50 percent".
	 * If no assumption can be made just return STATE_TYPE_NUMBER_PLAIN again. 
	 * @param deviceType - {@link SmartDevice.Types}
	 * @return
	 */
	public static String makeSmartTypeAssumptionForPlainNumber(SmartDevice.Types deviceType){
		if (deviceType.equals(SmartDevice.Types.light) || deviceType.equals(SmartDevice.Types.roller_shutter)){
			return STATE_TYPE_NUMBER_PERCENT;
		}else if (deviceType.equals(SmartDevice.Types.heater)){
			return STATE_TYPE_NUMBER_TEMPERATURE;
		}else{
			return STATE_TYPE_NUMBER_PLAIN;
		}
	}
}
