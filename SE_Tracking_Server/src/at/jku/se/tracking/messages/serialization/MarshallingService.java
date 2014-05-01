package at.jku.se.tracking.messages.serialization;

import java.util.ArrayList;
import java.util.Map;

import at.jku.se.tracking.messages.MessageType;
import at.jku.se.tracking.messages.MsgLocationUpdate;
import at.jku.se.tracking.messages.MsgLogin;
import at.jku.se.tracking.messages.MsgLogout;
import at.jku.se.tracking.messages.MsgRegister;
import at.jku.se.tracking.messages.MsgRequestSessionList;
import at.jku.se.tracking.messages.MsgRequestSessionTrack;
import at.jku.se.tracking.messages.MsgRequestSetSettings;
import at.jku.se.tracking.messages.MsgRequestSettings;
import at.jku.se.tracking.messages.MsgRequestUserList;
import at.jku.se.tracking.messages.MsgSession;
import at.jku.se.tracking.messages.MsgStartObservation;
import at.jku.se.tracking.messages.MsgStopObservation;

import com.json.generators.JSONGenerator;
import com.json.generators.JsonGeneratorFactory;
import com.json.parsers.JSONParser;
import com.json.parsers.JsonParserFactory;

public class MarshallingService {

	private static JSONParser parser;
	private static JSONGenerator generator;

	// ------------------------------------------------------------------------

	public static JSONParser getParser() {
		if (parser == null) {
			parser = JsonParserFactory.getInstance().newJsonParser();
		}
		return JsonParserFactory.getInstance().newJsonParser(); // needs
																// investigation
																// -> reuse not
																// possible?
	}

	// ------------------------------------------------------------------------

	public static JSONGenerator getGenerator() {
		if (generator == null) {
			generator = JsonGeneratorFactory.getInstance().newJsonGenerator();
		}
		return JsonGeneratorFactory.getInstance().newJsonGenerator(); // see
																		// parser
	}

	// ------------------------------------------------------------------------

	public static String toJSON(AMessage m) {
		return getGenerator().generateJson(m.getMap());
	}

	// ------------------------------------------------------------------------

	@SuppressWarnings("rawtypes")
	public static AMessage fromJSON(String json) throws InvalidMessageException {
		Map map = getParser().parseJson(json);
		// Unpack if necessary
		if (map.containsKey("root")) {
			Object root = map.get("root");
			if (root instanceof ArrayList<?>) {
				for (Object o : (ArrayList<?>) root) {
					if (o instanceof Map) {
						map = (Map) o;
						break;
					}
				}
			}
		}
		// --
		if (!map.containsKey(AMessage.FIELD_MESSAGE_TYPE)) {
			throw new InvalidMessageException("message type not defined");
		}
		// --
		MessageType type = MessageType.parse((String) map.get(AMessage.FIELD_MESSAGE_TYPE));
		if (type == null) {
			throw new InvalidMessageException("invalid message type");
		} else {
			switch (type) {
			case LOGIN:
				return new MsgLogin(map);
			case REGISTRATION:
				return new MsgRegister(map);
			case SESSION:
				return new MsgSession(map);
			case LOGOUT:
				return new MsgLogout(map);
			case USER_LIST:
				return new MsgRequestUserList(map);
			case LOCATION_UPDATE:
				return new MsgLocationUpdate(map);
			case SESSION_LIST:
				return new MsgRequestSessionList(map);
			case START_OBSERVATION:
				return new MsgStartObservation(map);
			case STOP_OBSERVATION:
				return new MsgStopObservation(map);
			case SESSION_POINTS:
				return new MsgRequestSessionTrack(map);
			case GET_SETTINGS:
				return new MsgRequestSettings(map);
			case SET_SETTINGS:
				return new MsgRequestSetSettings(map);
			default:
				break;
			}
		}
		return null;
	}
}
