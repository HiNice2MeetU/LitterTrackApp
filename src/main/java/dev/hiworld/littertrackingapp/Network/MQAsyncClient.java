package dev.hiworld.littertrackingapp.Network;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


import java.lang.reflect.Type;
import java.util.ArrayList;

import dev.hiworld.littertrackingapp.Utility.UtilityManager;

public class MQAsyncClient {
    // Globals
    private MqttAsyncClient MQAsync;
    private MemoryPersistence Persistence = new MemoryPersistence();
    private String SessionID;
    private Gson gson = new Gson();
    private String SendTopic = "Client";
    private String RecieveTopic = "Server";
    private long DefaultDisTimeout = 1000;
    private int DefaultQOS = 2;

    public MQAsyncClient(String sessionID) {
        SessionID = sessionID;
    }

    public MQAsyncClient() {
        SessionID = UtilityManager.GenorateID(20, "QWERTYUIOPASDFGHJKLZXCVBNMwertyuiopasdfghjklzxcvbnm!@#$%^&*()123456890");
        Log.d("MQAsyncClient", "SessionID = " + SessionID);
    }

    // Connect
    public void Connect(String BrokerUri, MqttCallback RecieveCall, boolean AutoSub, IMqttActionListener SucessCall) {
        // Make Client
        try {
            MQAsync = new MqttAsyncClient(BrokerUri, SessionID, Persistence);
        } catch (MqttException e) {
            Log.d("MQAsyncClient", e.toString() + "@Connect");
        }

        // Set Callback
        MQAsync.setCallback(RecieveCall);

        // Make Connection Options
        MqttConnectOptions MQConOptions = new MqttConnectOptions();

        // Set Connection Options
        MQConOptions.setCleanSession(true);
        MQAsync.setCallback(RecieveCall);

        // Connect MQAsync to server
        try {
            MQAsync.connect(MQConOptions, null, SucessCall);
        } catch (MqttException e) {
            Log.d("MQAsyncClient", e.toString() + "@Connect");
        }

        // Detec if to automatically sub to recieve topic
        if (AutoSub) {
            // Subscribe to revieve topic
            Subscribe(RecieveTopic, SucessCall);
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
            }
        } catch (Exception e) {
            // Log
            Log.e("MQAsyncClient", e.toString() +"@" + "Disconnect");

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
                SucessCall.onFailure(new MqttToken(), new NullPointerException());
            }
        } catch (Exception e) {
            // Log
            Log.e("MQAsyncClient", e.toString()+"@" + "Disconnect");

            // Notify listener
            SucessCall.onFailure(new MqttToken(), e);
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
            }

        } catch (Exception e) {
            // Log
            Log.e("MQAsyncClient", e.toString()+"@" + "Publish");

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

            // Publish
            if (MQAsync.isConnected()) {
                // If mqasync is connected
                MQAsync.publish(SendTopic, Msg);
            } else {
                // If MqAsync isnt connected
                SucessCall.onFailure(new MqttToken(), new NullPointerException());
            }
        } catch (Exception e) {
            Log.e("MQAsyncClient", e.toString()+"@" + "Publish");

            // Notify listener
            SucessCall.onFailure(new MqttToken(), e);
        }
    }

    // Subscribe with default qos
    public void Subscribe(String Topic, IMqttActionListener SucessCall) {
        // Subscribe
        try {
            // Publish
            if (MQAsync.isConnected()) {
                // If mqasync is connected
                MQAsync.subscribe(Topic, DefaultQOS);
            } else {
                // If MqAsync isnt connected
                SucessCall.onFailure(new MqttToken(), new NullPointerException());
            }
        } catch (Exception e) {
            Log.e("MQAsyncClient", e.toString()+"@" + "Subscribe");

            // Notify listener
            SucessCall.onFailure(new MqttToken(), e);
        }
    }

    // Subscribe with specified qos
    public void Subscribe(String Topic, int QOS, IMqttActionListener SucessCall) {
        // Subscribe
        try {
            // Publish
            if (MQAsync.isConnected()) {
                // If mqasync is connected
                MQAsync.subscribe(Topic, QOS);
            } else {
                // If MqAsync isnt connected
                SucessCall.onFailure(new MqttToken(), new NullPointerException());
            }
        } catch (Exception e) {
            Log.e("MQAsyncClient", e.toString()+"@" + "Subscribe");

            // Notify listener
            SucessCall.onFailure(new MqttToken(), e);
        }
    }

    // Turn MQMsg to JSON
    private String EncodeResult(MQMsg Input) {
        Input.AutoFillType();
        return gson.toJson(Input);
    }

    // Turn JSON to MQMsg
    private MQMsg DecodeResult(String input) {
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

                Log.d("MQManager", "ClassString = " + ClassString);

                // if statement for custom classes
                if (ClassString.equals("Event") || ClassString.equals("dev.hiworld.littertrackingapp.Network.Event")) {
                    // Log
                    Log.d("MQManager", "Msg Detected as event");

                    // Add to temp list
                    ObjList.add(gson.fromJson(Current.toString(), Event.class));
                } else if (ClassString.equals("java.util.ArrayList") && Cmd.equals("GetAll")) {
                    // Log
                    Log.d("MQManager", "Msg Detected as arraylist full of events");

                    // Add to TempList
                    Type ListType = new TypeToken<ArrayList<Event>>() {
                    }.getType();
                    ObjList.add(gson.fromJson(Current.toString(), ListType));
                } else {
                    // Log
                    Log.d("MQManager", "Msg Detected as primative");

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
                        throw (new NullPointerException());
                    }
                }
            }

            // Return value
            return new MQMsg(ObjList, SessionID, TID, Cmd, Result);

        } catch (Exception e) {
            // Log
            Log.e("MQManager", e.toString() + "@" + "JSONDecoding");

            return null;
        }
    }

    // Validate Msg from list of transaction ids
    public MsgType Validate(MQMsg Msg, ArrayList<String> InputTID) {
        if (SessionID.equals(Msg.getSessionID())) {
            // Get Msg ID
            String TID = Msg.getTransactionID();

            // Check if current id list has the Msg id
            if (InputTID.contains(TID)) {
                // Log
                Log.d("MQManager", "Found Match in Observer List to" + Msg.toString());
                Log.d("MQManager", "Msg sent to observer " + TID);

                // Return
                return MsgType.YES;

            } else if (TID.equals("ERR")) {
                // Log
                Log.d("MQManager", "Error Detected" + Msg.toString());

                // Return
                return MsgType.ERR;

            } else {
                // No match is found
                Log.d("MQManager", "Match Not found");

                // Return
                return MsgType.NAY;
            }

        } else {
            // Return
            return MsgType.NAY;
        }
        //return null;
    }

    // Validate Msg from single transaction id
    public MsgType Validate(MQMsg Msg, String InputTID) {
        if (SessionID.equals(Msg.getSessionID())) {
            // Get Msg ID
            String TID = Msg.getTransactionID();

            // Check if current id list has the Msg id
            if (InputTID.contains(TID)) {
                // Log
                Log.d("MQManager", "Found Match in Observer List to" + Msg.toString());
                Log.d("MQManager", "Msg sent to observer " + TID);

                // Return
                return MsgType.YES;

            } else if (TID.equals("ERR")) {
                // Log
                Log.d("MQManager", "Error Detected" + Msg.toString());

                // Return
                return MsgType.ERR;

            } else {
                // No match is found
                Log.d("MQManager", "Match Not found");

                // Return
                return MsgType.NAY;
            }

        } else {
            // Return
            return MsgType.NAY;
        }
        //return null;
    }
}
