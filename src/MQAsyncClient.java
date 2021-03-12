import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

public class MQAsyncClient {
	// Globals
	private MqttAsyncClient MQAsync;
	private MemoryPersistence Persistence = new MemoryPersistence();
	private String SessionID;
	private static Gson gson = new Gson();
	private String SendTopic = "Server";
	private String RecieveTopic = "Client";
	private long DefaultDisTimeout = 1000;
	private int DefaultQOS = 2;
	private static ArrayList<Integer> Users = new ArrayList<Integer>();
	private String ClientCrt = "../Security/SPPems/m2mqtt_ca.pem";
	private String ServerCrt = "../Security/SPPems/m2mqtt_srv.pem";
	private String ClientKey = "../Security/SPPems/m2mqtt_ca.key";

	public MQAsyncClient(String sessionID) {
		SessionID = sessionID;
	}

	public MQAsyncClient() {
		SessionID = GenorateID(20, "QWERTYUIOPASDFGHJKLZXCVBNMwertyuiopasdfghjklzxcvbnm!@#$%^&*()123456890");
		Log.Stn("Server", "SessionID = " + SessionID);
	}

	public static String GenorateID(int Limit, String Chars) {
		// Create Random
		Random rand = new Random();

		// Make return string
		String Return = "";

		// Make String
		for (int i = 0; i < Limit; i++){
			// Get Cheracter
			String SelectedCharacter = String.valueOf(Chars.charAt(rand.nextInt(Chars.length())));

			// Add to return
			Return = Return.concat(SelectedCharacter);
		}

		// Check if already in list
		for (int j = 0; j < Users.size(); j++){
			if (Users.get(j).equals(Return)) {
				return GenorateID(Limit, Chars);
			}
		}

		return Return;
	}

	// Check Ping
	public void CheckPing(IMqttActionListener SucessCall) {
		// Disconnect
		try {
			if (MQAsync.isConnected()) {
				// If mqasync is connected
				MQAsync.checkPing(null, SucessCall);
			}
		} catch (Exception e) {
			// Log
			Log.e("Server", e.toString() +"@" + "CheckPing");

			// Notify listener
			SucessCall.onFailure(new MqttToken(), e);
		}
	}

	// Connect
	public void Connect(String BrokerUri, MqttCallback RecieveCall, boolean AutoSub, IMqttActionListener SucessCall) {
		// Make Client
		try {
			MQAsync = new MqttAsyncClient(BrokerUri, SessionID, Persistence);

			// Set Callback
			MQAsync.setCallback(RecieveCall);

			// Make Connection Options
			MqttConnectOptions MQConOptions = new MqttConnectOptions();

			// SSL Magic
			MQConOptions.setSocketFactory(SslUtil.getSocketFactory(ServerCrt,
					ClientCrt, ClientKey, "MqCambourneVC1!"));

			// Set Connection Options
			MQConOptions.setCleanSession(true);
			MQAsync.setCallback(RecieveCall);

			// Connect MQAsync to server
			MQAsync.connect(MQConOptions, null, SucessCall);

			if (AutoSub) {
				// Sub
				Subscribe(RecieveTopic, SucessCall);

				// Log
				Log.Stn("Server", "AutoSubbed to: " + RecieveTopic);
			} else {
				// Log
				Log.Stn("Server", "Didn't autosub");
			}

		} catch (Exception e) {
			Log.Stn("Server", e.toString() + "@Connect");
		}
	}

	// DisConnect
	public void Disconnect(long TimeOut, IMqttActionListener SucessCall) {
		// Disconnect
		try {
			if (MQAsync.isConnected()) {
				// If mqasync is connected
				MQAsync.disconnect(TimeOut, SucessCall);
			} else {
				// If MqAsync isnt connected
				SucessCall.onFailure(new MqttToken(), new NullPointerException());

				// Log
				Log.e("Server", "No Connection " + "@Disconnect");
			}
		} catch (Exception e) {
			// Log
			Log.e("Server", e.toString() +"@" + "Disconnect");

			// Notify listener
			SucessCall.onFailure(new MqttToken(), e);
		}
	}

	// Disconnect with default timeout
	public void Disconnect(IMqttActionListener SucessCall) {
		// Disconnect
		try {
			if (MQAsync.isConnected()) {
				// If mqasync is connected
				MQAsync.disconnect(DefaultDisTimeout, SucessCall);
			} else {
				// If MqAsync isnt connected
				if (SucessCall != null) {
					SucessCall.onFailure(new MqttToken(), new NullPointerException());
				}

				// Log
				Log.e("Server", "No Connection " + "@Disconnect");
			}
		} catch (Exception e) {
			// Log
			Log.e("Server", e.toString()+"@" + "Disconnect");

			// Notify listener
			if (SucessCall != null) {
				SucessCall.onFailure(new MqttToken(), e);
			}
		}
	}

	// Publish
	public void Publish(MQMsg Content, IMqttActionListener SucessCall, String Topic) {
		// Publish
		try {
			// Make msg
			MqttMessage Msg = new MqttMessage();

			// Set params
			Msg.setPayload(EncodeResult(Content).getBytes());

			// Publish
			if (MQAsync.isConnected()) {
				// If mqasync is connected
				MQAsync.publish(Topic, Msg);
			} else {
				// If MqAsync isnt connected
				SucessCall.onFailure(new MqttToken(), new NullPointerException());

				// Log
				Log.e("Server", "No Connection " + "@Publish");
			}

		} catch (Exception e) {
			// Log
			Log.e("Server", e.toString()+"@" + "Publish");

			// Notify listener
			SucessCall.onFailure(new MqttToken(), e);
		}
	}

	// Publish with defualt topic
	public void Publish(MQMsg Content, IMqttActionListener SucessCall) {
		// Publish
		try {
			// Make msg
			MqttMessage Msg = new MqttMessage();

			// Set params
			Msg.setPayload(EncodeResult(Content).getBytes());

			// Log
			Log.Stn("Server", "Publishing: " + new String(Msg.getPayload(), StandardCharsets.UTF_8));

			// Publish
			if (MQAsync.isConnected()) {
				// If mqasync is connected
				MQAsync.publish(SendTopic, Msg);

				SucessCall.onSuccess(new MqttToken());
			} else {
				// If MqAsync isnt connected
				SucessCall.onFailure(new MqttToken(), new NullPointerException());

				// Log
				Log.e("Server", "No Connection " + "@Publish");
			}
		} catch (Exception e) {
			Log.e("Server", e.toString()+"@" + "Publish");

			// Notify listener
			SucessCall.onFailure(new MqttToken(), e);
		}
	}

	// Subscribe with default qos and default topic
	public void Subscribe(IMqttActionListener SucessCall) {
		// Subscribe
		try {
			// Subscribe
			MQAsync.subscribe(RecieveTopic, DefaultQOS);

			SucessCall.onSuccess(new MqttToken());
		} catch (Exception e) {
			Log.e("Server", e.toString()+"@" + "Subscribe");

			// Notify listener
			SucessCall.onFailure(new MqttToken(), e);
		}
	}

	// Subscribe with default qos
	public void Subscribe(String Topic, IMqttActionListener SucessCall) {
		// Subscribe
		try {
			// Subscribe
			MQAsync.subscribe(Topic, DefaultQOS);

			SucessCall.onSuccess(new MqttToken());
		} catch (Exception e) {
			Log.e("Server", e.toString()+"@" + "Subscribe");

			// Notify listener
			SucessCall.onFailure(new MqttToken(), e);
		}
	}

	// Subscribe with specified qos
	public void Subscribe(String Topic, int QOS, IMqttActionListener SucessCall) {
		// Subscribe
		try {
			// Publish
			MQAsync.subscribe(Topic, QOS);

			SucessCall.onSuccess(new MqttToken());
		} catch (Exception e) {
			// Log
			Log.e("Server", e.toString()+"@" + "Subscribe");

			// Notify listener
			SucessCall.onFailure(new MqttToken(), e);
		}
	}

	// Turn MQMsg to JSON
	public static String EncodeResult(MQMsg Input) {
		Input.AutoFillType();
		return gson.toJson(Input);
	}

	// Turn JSON to MQMsg
	public static MQMsg DecodeResult(String input) {
		try {
			// Get Overall JSON Object
			JsonObject MainObj = JsonParser.parseString(input).getAsJsonObject();

			// Get Specific Elements
			String SessionID = MainObj.getAsJsonPrimitive("SessionID").getAsString();
			String TID = MainObj.getAsJsonPrimitive("TransactionID").getAsString();
			int Result = MainObj.getAsJsonPrimitive("Result").getAsInt();
			String Cmd = MainObj.getAsJsonPrimitive("Cmd").getAsString();

			// Get Object/Object Type List
			JsonArray RawObjList = MainObj.getAsJsonArray("Params");
			JsonArray ObjListType = MainObj.getAsJsonArray("TypeList");

			// TempList Define
			ArrayList<Object> ObjList = new ArrayList<Object>();

			// Iterate through the list
			for (int i = 0; i < RawObjList.size(); i++) {
				// Get Current
				JsonElement Current = RawObjList.get(i);

				// Get Class String using ObjListType
				String ClassString = ObjListType.get(i).getAsString();

				Log.Stn("Admin", "ClassString = " + ClassString);

				// if statement for custom classes
				if (ClassString.equals("Event") || ClassString.equals("dev.hiworld.littertrackingapp.Network.Event")) {
					// Log
					Log.Stn("Admin", "Msg Detected as event");

					// Add to temp list
					ObjList.add(gson.fromJson(Current.toString(), Event.class));
				} else if (ClassString.equals("java.util.ArrayList") && Cmd.equals("GetAll")) {
					// Log
					Log.Stn("Admin", "Msg Detected as arraylist full of events");

					// Add to TempList
					Type ListType = new TypeToken<ArrayList<Event>>() {
					}.getType();
					ObjList.add(gson.fromJson(Current.toString(), ListType));
				} else {
					// Log
					Log.Stn("Admin", "Msg Detected as primative");

					// Get Current Type
					Class CurrentType = Class.forName(ClassString);

					// if statement for primatives
					if (CurrentType == String.class) {
						ObjList.add(Current.getAsString());
					} else if (CurrentType == Integer.class) {
						ObjList.add(Current.getAsInt());
					} else if (CurrentType == Double.class) {
						ObjList.add(Current.getAsInt());
					} else {
						// No datatype found
						Log.e("ServerTransport", "There was an unexpected datatype: " + Current.toString() + " DATATYPE: " + ClassString);

						// Throw Error
						// throw (new NullPointerException());
					}
				}
			}

			// Return value
			return new MQMsg(ObjList, SessionID, TID, Cmd, Result);

		} catch (Exception e) {
			// Log
			Log.e("Admin", e.toString() + "@" + "JSONDecoding");

			return null;
		}
	}

}

