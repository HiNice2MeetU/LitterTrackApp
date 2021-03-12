import java.util.Arrays;
import java.util.ArrayList;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.io.*;
import java.util.Date;
import org.eclipse.paho.client.mqttv3.*;
import java.sql.Timestamp;
public class Main {
	// Globals
	private static String LogPath = "../.log";
	private static MQAsyncManager MQM = new MQAsyncManager();
	private static DBAbstract DBConn = new DBAbstract();
	private static IMqttActionListener GenericResponse= new IMqttActionListener() {
		@Override
		public void onSuccess(IMqttToken asyncActionToken) {
			Log.d("Server", "MQM Action Sucesfull");
			MQM.Next();
		}

		@Override
		public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
			Log.e("Server", "MQM Action failed");
			exception.printStackTrace();
		}
	};

	private static MqttCallback DefaultCall = new MqttCallback() {
		@Override
		public void connectionLost(Throwable cause) {
			// If loses connection to server
			Log.Err("Server", "Connection to network lost!");
		}

		@Override
		public void messageArrived(String topic, MqttMessage message) {
			// Get Msg
			String RawMsg = message.toString(); 

			// Log
			Log.Stn("Client", "Raw Msg Arrived: " + RawMsg);

			// Format Msg
			MQMsg FormattedMsg = MQAsyncClient.DecodeResult(RawMsg);

			// Run it through choose action
			ChooseAction(FormattedMsg);

			Log.Stn("Client", "Formatted Msg Arrived: " + FormattedMsg.toString());
		}

		@Override
		public void deliveryComplete(IMqttDeliveryToken token) {

		}
	};	



	public static void main(String[] args) {

		try {
			// Set log file
			PrintStream Out = new PrintStream(new File(LogPath + "/" + new Timestamp(System.currentTimeMillis())+ ".log"));
			//System.setOut(Out);

			// Init DBConn
			DBConn.Connect("../Data.db");
			//DBConn.AddRow(new Event(0.0,0.0,"SixetyNine"));
			DBConn.GetRow(0);

			// Test run of MQTT
			MQM.Add(new MQMsg(new ArrayList<Object>(Arrays.asList("ssl://hallo.home", DefaultCall, false)), "Connect"),GenericResponse);
			MQM.Add(new MQMsg(new ArrayList<Object>(Arrays.asList()), "Subscribe"), GenericResponse);
			MQM.Add(new MQMsg(new ArrayList<Object>(Arrays.asList(new Event(692,86,"FiftyFour"), new Event(96,96,"NinetySix"))), "Ladida"), GenericResponse);
			MQM.Next();
			
			// Add Shutdown func
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					// Shutdown
					ExitProgram();
				}
			});

		} catch (Exception e) {
			Log.Err("Admin/ReadIN", e.toString()); 
		}


	}

	public static void ExitProgram() {
		//System.exit(0);
		Log.Stn("Admin", "Exiting");
		MQM.Add(new MQMsg("Disconnect"), GenericResponse);
		DBConn.Close();
		//System.exit(0);
	}

	public static void ChooseAction (MQMsg Input) {
		// Get Params
		ArrayList<Object> Params = Input.getParams();
		String Cmd = Input.getCmd();
		MQAsyncClient Sesh = MQM.GetMQClient();

		// B I G Switch statement to determine waht to do
		switch (Cmd) {
			case "GetRow":
				// Get 1 Row
				Event ReturnVal = DBConn.GetRow((int)Params.get(0));

				// Set Values on msg
				Input.setParams(new ArrayList<Object>(Arrays.asList(ReturnVal)));

				// Log
				Log.Stn("Client", "GetRow Triggered");

				// Break
				break;
			case "GetAll":
				// Dfine return
				ArrayList<Object>ReturnVal2 = new ArrayList<Object>();

				// Get all rows
				ReturnVal2.addAll(DBConn.GetAll());

				// Set Values on msg
				Input.setParams(ReturnVal2);

				// Log
				Log.Stn("Client", "GetAll Triggered");

				// Break
				break;
			case "AddRow":
				// Add Row
				DBConn.AddRow((Event)Params.get(0));

				// Log
				Log.Stn("Client", "AddRow Triggered");
			default:
				Log.Err("Server", "Could not find cmd");
		}

		// Add to send queue
		Sesh.Publish(Input, GenericResponse);
	}


}
