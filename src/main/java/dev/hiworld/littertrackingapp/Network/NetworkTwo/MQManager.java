package dev.hiworld.littertrackingapp.Network.NetworkTwo;

import android.util.Log;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import dev.hiworld.littertrackingapp.Network.Event;
import dev.hiworld.littertrackingapp.Network.MQMsg;
import dev.hiworld.littertrackingapp.Network.MQTracker;
import dev.hiworld.littertrackingapp.Network.MsgType;
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

    // ================ EXECUTES ================ //

    // Execute Single Command
    public synchronized String Execute(MQMsg Input) {
        return "A";
    }

    public void Test() {
        // Test Data
        // TODO remove test data
        NotifyObserver(new MQMsg(new ArrayList<Object>(),SessionID,"A","Test With Correct SESSION ID"));
        NotifyObserver(new MQMsg(new ArrayList<Object>(),"69","A","Test With InCorrect SESSION ID"));
        NotifyObserver(new MQMsg(new ArrayList<Object>(),SessionID,"6969","Test With InCorrect TRANSACTION ID"));
        NotifyObserver(new MQMsg(new ArrayList<Object>(),SessionID,"A","Test With Correct TRANSACTION ID"));
        NotifyObserver(new MQMsg(new ArrayList<Object>(),SessionID,"ERR","Test With Correct TRANSACTION ID but it is a ERROR"));
        Log.d("MQManager", "Executed Test Data");
        //return "A";

        // Log
        Log.d("MQManager", "===============================================");

        // Json Testing Serialization
        ArrayList<Object>TestParam = new ArrayList<Object>();
        TestParam.add("Hallo");
        TestParam.add(69);
        TestParam.add(new Event(6969,2323,"A"));
        String RawJson = EncodeResult(new MQMsg(TestParam,SessionID,"A","JSoN TeStiNg"));
        Log.d("MQManager", "Serialization: " + RawJson);

        // Json testing deseriralization
        MQMsg FromJSON = DecodeResult(RawJson);
        Log.d("MQManager", "DeSerialization: " + FromJSON.toString());
    }

    // Execute List of Commands
    public synchronized ArrayList<Integer> ExecuteList(ArrayList<MQMsg> Input) {
        return null;
    }

    // ================ OBSERVER ================ //

    // Observer Interface
    public interface MQListener {
        // When new message comes in
        public void Update(MQMsg Msg);

        // When new error comes in
        public void Error(MQMsg Error);
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
        Main.getTransactionID().add(ID);

        // Add to list
        ListenerList.add(Main);

        // Log
        Log.d("MQManager", "Added Observer " + Main.toString());
    }

    // Remove Observer
    public void RemoveObserver(MQListener Listener){
        ListenerList.remove(Listener);
    }

    // Notify Observer of msg
    public void NotifyObserver(MQMsg Msg){
        // Check if has the same session id


        // Iterate through list
        for(int i = 0; i<ListenerList.size();i++){
            // Get values
            MQTracker Current = ListenerList.get(i);
            MQListener CL = Current.getListener();
            ArrayList<String> TID = Current.getTransactionID();

            // Check if valid
            switch (Validate(Msg,TID)) {
                case ERR:
                    // If is error
                    CL.Error(Msg);
                    break;
                case YES:
                    // If is valid
                    CL.Update(Msg);
                    break;
            }
        }
    }

    // ================ CORE FUNCTIONS ================ //

    public MsgType Validate(MQMsg Msg, ArrayList<String> InputTID){
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

    // Turn MQMsg to JSON
    private String EncodeResult(MQMsg Input){
        Input.AutoFillType();
        return gson.toJson(Input);
    }

    // Turn JSON to MQMsg
    private MQMsg DecodeResult(String input){
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
            ArrayList<Object>ObjList = new ArrayList<Object>();

            // Iterate through the list
            for (int i = 0; i<RawObjList.size(); i++) {
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

    // Make Error
    private MQMsg ErrorMaker(Exception e, String Where) {
        // Create Params
        ArrayList<Object> Params = new ArrayList<Object>();

        // Add Exeception to params
        Params.add(e);

        // Retunrn MSg
        return new MQMsg(Params, SessionID, "ERR", Where);
    }


}
