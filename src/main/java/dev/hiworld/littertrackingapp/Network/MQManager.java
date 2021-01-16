package dev.hiworld.littertrackingapp.Network;

import android.util.Log;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import dev.hiworld.littertrackingapp.Utility.UtilityManager;

public class MQManager {
    // Globals
    private LinkedList<MQMsg> CommandQueue = new LinkedList<MQMsg>();
    private ArrayList<MQTracker> ListenerList = new ArrayList<MQTracker>();
    private Gson gson = new Gson();

    // Make ID
    String SessionID;

    // TEST CONSTRUCTOR
    public MQManager() {
        SessionID = UtilityManager.GenorateID(20, "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm1234567890!@#$%^&*()");
        Log.d("MQManager", "Genorate Session ID: " + SessionID);
    }

    // MQThread Kill Switch
    private static volatile boolean MQTRunning = true;

    // Execute Single Command
    public synchronized String Execute(MQMsg Input) {
        // Test Data
        // TODO remove test data
        NotifyObserver(new MQMsg(new ArrayList<Object>(),SessionID,"A","Test With Correct SESSION ID"));
        NotifyObserver(new MQMsg(new ArrayList<Object>(),"69","A","Test With InCorrect SESSION ID"));
        NotifyObserver(new MQMsg(new ArrayList<Object>(),SessionID,"6969","Test With InCorrect TRANSACTION ID"));
        NotifyObserver(new MQMsg(new ArrayList<Object>(),SessionID,"A","Test With Correct TRANSACTION ID"));
        NotifyObserver(new MQMsg(new ArrayList<Object>(),SessionID,"ERR","Test With Correct TRANSACTION ID but it is a ERROR"));
        Log.d("MQManager", "Executed Test Data");
        return "A";
    }

    // Execute List of Commands
    public synchronized ArrayList<Integer> ExecuteList(ArrayList<MQMsg> Input) {
        return null;
    }

    // Add observers
    public void AddObserver(ArrayList<String> IDs, MQListener Listener) {
        // Create Main
        MQTracker Main = new MQTracker(IDs, Listener);

        // Add to list
        ListenerList.add(Main);

        // Log
        Log.d("MQManager", "Added Observer " + Main.toString());
    }

    // Add observer with 1 id
    public void AddObserver(String ID, MQListener Listener) {
        // Create Main
        MQTracker Main = new MQTracker(Listener);

        // Add to id list
        Main.TransactionID.add(ID);

        // Add to list
        ListenerList.add(Main);

        // Log
        Log.d("MQManager", "Added Observer " + Main.toString());
    }

    // Remove Observer
    public void RemoveObserver(MQListener Listener){
        ListenerList.remove(Listener);
    }

    // Notify Observer
    public void NotifyObserver(MQMsg Msg){
        // Check if has the same session id
        if (SessionID.equals(Msg.getSessionID())) {
            // Iterate through list of observers
            for (int i = 0; i<ListenerList.size();i++){
                // Get Current
                MQTracker Current = ListenerList.get(i);

                // Get Msg ID
                String TID = Msg.getTransactionID();

                // Check if current id list has the Msg id
                if (Current.getTransactionID().contains(TID)) {
                    // Log
                    Log.d("MQManager", "Found Match in Observer List to" + Msg.toString());

                    // Update Listeners
                    Current.getListener().Update(Msg);

                    // Log
                    Log.d("MQManager", "Msg sent to observer " + TID);

                } else if (TID.equals("ERR")) {
                    // Update Listeners if is tagged with error
                    Current.getListener().Error(Msg);

                    // Log
                    Log.d("MQManager", "Error Detected" + Msg.toString());

                } else {
                    // No match is found
                    Log.d("MQManager", "Match Not found");
                }
            }
        }
    }

    // Observer Interface
    public interface MQListener {
        // When new message comes in
        public void Update(MQMsg Msg);

        // When new error comes in
        public void Error(MQMsg Error);
    }

    // Start Network Thread
    private void StartNet() {
        // Create MqSession Object
        MQSession Sesh = new MQSession(2,SessionID);

        // Start Thread
        Thread NetworkOutThread = new Thread(new MQThread(Sesh));
        NetworkOutThread.setDaemon(true);
        NetworkOutThread.setName("NetThread@" + SessionID);
        NetworkOutThread.start();
    }

    // Class to be executed on different thread
    class MQThread implements Runnable {
        // Globals
        MQSession Sesh;

        // Constructor
        public MQThread(MQSession sesh) {
            Sesh = sesh;
        }

        // Main Func
        public void run(){
            // Execute
            while (MQTRunning) {
                synchronized (MQManager.this) {
                    // Check if any commands in queue
                    if (CommandQueue.size() > 0) {
                        // Get Current Element
                        //MQMsg = CommandQueue.removeFirst();

                        // Serialize int Json

                        // Send
                    }

                    // Sleep
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Log.e("MQManager", e.toString() + " at ReadThread");
                    }
                }
            }
        }
    }

    public String EncodeResult(MQMsg Input){
        Input.AutoFillType();
        return gson.toJson(Input);
    }

    public MQMsg DecodeResult(String input){
        try {
            // Get Overall JSON Object
            JsonObject MainObj = JsonParser.parseString(input).getAsJsonObject();

            // Get Specific Elements
            String SessionID = MainObj.getAsJsonPrimitive("SessionID").getAsString();
            String TID = MainObj.getAsJsonPrimitive("TransactionID").getAsString();
            int Result = MainObj.getAsJsonPrimitive("Result").getAsInt();
            String Cmd = MainObj.getAsJsonPrimitive("Cmd").getAsString();

            // Get Object List
            JsonArray RawObjList = MainObj.getAsJsonArray("Params");

            // TempList Define
            ArrayList<Object>ObjList = new ArrayList<Object>();

            // Iterate through the list
            for (int i = 0; i<RawObjList.size(); i++) {
                // Get Current
                JsonElement Current = RawObjList.get(i);

                // Get Class String
                String ClassString = Current.getAsString();

                // if statement for custom classes
                if (ClassString.equals("Event")) {
                    // Log
                    Log.d("MQManager", "Msg Detected as event");

                    // Add to temp list
                    ObjList.add(gson.fromJson(Current.toString(), Event.class));
                } else if (ClassString.equals("java.util.ArrayList") && Cmd.equals("GetAll")) {
                    // Log
                    Log.d("MQManager", "Msg Detected as arraylist full of events");

                    // Add to TempList
                    Type ListType = new TypeToken<ArrayList<Event>>(){}.getType();
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
                        throw(new NullPointerException());
                    }
                }
            }

            // Return value
            return new MQMsg(ObjList, SessionID, TID, Cmd, Result);

        } catch (Exception e) {
            // Log
            Log.e("MQManager", e.toString() + "@" + "JSONDecoding");

            // Notify Error
            NotifyObserver(ErrorMaker(e, "JSONDecoding"));

            return null;
        }
    }


    private MQMsg ErrorMaker(Exception e, String Where) {
        // Create Params
        ArrayList<Object> Params = new ArrayList<Object>();

        // Add Exeception to params
        Params.add(e);

        // Retunrn MSg
        return new MQMsg(Params, SessionID, "ERR", Where);
    }

}
