package dev.hiworld.littertrackingapp.Network.NetworkOne;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;

import dev.hiworld.littertrackingapp.Network.Event;
import dev.hiworld.littertrackingapp.Utility.UtilityManager;

public class ServerTransport {

    // Globals
    private static ArrayList<ServerTransport.ServerListener> ServerListeners = new ArrayList<ServerTransport.ServerListener>();
    private static UtilityManager UM = new UtilityManager();
    private static int ConnectionAttempts = 0;
    protected static LinkedList<SocketResultSet> CommandQueue = new LinkedList<SocketResultSet>();
    protected static ArrayList<SocketResultSet> ProcessedQueue = new ArrayList<SocketResultSet>();
    protected static PrintWriter out;
    protected static BufferedReader in;
    private static Gson gson = new Gson();

    // Kill Vars
    private static volatile boolean NetworkRun = true;
    private static volatile boolean Connected = false;
    // Singleton pattern
    private static ServerTransport instance = new ServerTransport();
    private ServerTransport(){}
    public static ServerTransport getInstance(){
        return instance;
    }

    // Decode Result
    private SocketResultSet DecodeResult(String input){
        try {
            // Get the json obj
            JsonObject JayObj = JsonParser.parseString(input).getAsJsonObject();

            // Get all the id and other raw types
            int ProID = JayObj.get("Id").getAsInt();
            int Result = JayObj.get("Result").getAsInt();
            String Cmd = JayObj.get("Cmd").getAsString();

            // Get the param type json array
            JsonArray JArrayType = JayObj.getAsJsonArray("ParamTypes");

            // Get the param json array
            JsonArray JParay = JayObj.getAsJsonArray("Param");

            // Create Temp Array
            ArrayList TempList = new ArrayList();

            // Iterate through Jparay
            if (JParay != null) {
                // if neither are null
                for (int i = 0; i < JParay.size(); i++) {
                    // Get Current
                    JsonElement Current = JParay.get(i);

                    // Get Class String
                    String ClassString = JArrayType.get(i).getAsString();

                    // Seperate through types that arent in cleint
                    if (ClassString.equals("Event")) {
                        TempList.add(gson.fromJson(Current.toString(), Event.class));
                    } else if (ClassString.equals("java.util.ArrayList") && (JayObj.getAsJsonPrimitive("Cmd").getAsString()).equals("GetAll")) {
                        //TempList.add(gson.fromJson(Current.toString(), new ArrayList<Event>().getClass()));
                        Type ListType = new TypeToken<ArrayList<Event>>(){}.getType();
                        TempList.add(new Gson().fromJson(Current.toString(), ListType));

                    } else {
                        // Seperate through primatives
                        // Get Class
                        Class CurrentType = Class.forName(ClassString);

                        // Log
                        Log.d("ServerTransport", Current.toString() + " Class = " + CurrentType.getName());

                        // Filter through primatives
                        if (CurrentType == String.class) {
                            TempList.add(Current.getAsString());
                        } else if (CurrentType == Integer.class) {
                            TempList.add(Current.getAsInt());
                        } else if (CurrentType == Double.class) {
                            TempList.add(Current.getAsInt());
                        } else {
                            Log.e("ServerTransport", "There was an unexpected datatype: " + Current.toString() + " DATATYPE: " + ClassString);
                        }
                    }
                }
            } else {
                // If there are no params
                Log.e("ServerTransport", "JayObj or JayParay == null: " + JayObj.toString());
            }
            // Return decoded socket result set
            SocketResultSet Return = new SocketResultSet(Cmd, TempList, ProID, Result);
            return Return;

        } catch (JsonSyntaxException e) {
            Log.e("ServerTransport",e.toString());
            return null;
        } catch (ClassNotFoundException f) {
            Log.e("ServerTransport", f.toString());
            return null;
        }

    }

    // Make a formatted msg so server can read
    private String EncodeMsg(SocketResultSet input){
        // Auto fill
        input.AutoFillType();

        // Return json string
        String Return = gson.toJson(input);

        // Log
        Log.d("ServerTransport", "Encoded Msg = " + Return);

        // Return
        return Return;
    }

    // Observer Pattern
    public void AddObserver(ServerListener Listener) {
        ServerListeners.add(Listener);
    }

    // Remove Observers
    public void RemoveObserver(ServerListener Listener) {
        ServerListeners.remove(Listener);
    }

    // Tell Observes
    public void NotifyObservers(SocketResultSet Msg) {
        for (int i=0;i<ServerListeners.size();i++){
            ServerListeners.get(i).Update(Msg);
        }
    }

    // Observer Interface
    public interface ServerListener {
        public void Update(SocketResultSet Msg);
    }



    // Look through Proccesed QUEUE
    private static SocketResultSet FindResultSet(int ID){
        for (int i=0; i<ProcessedQueue.size(); i++){
            SocketResultSet Current = ProcessedQueue.get(i);
            if (Current.getId() == ID){
                return Current;
            }
        }
        return null;
    }

    // Start Threads
    public void Start(){
        // Send Thread
        Thread NetworkOutThread = new Thread(new SendThread());
        NetworkOutThread.setDaemon(true);
        NetworkOutThread.setName("NetOut");
        NetworkOutThread.start();

        // Recieve Thread
        Thread NetworkInThread = new Thread(new ReadThread());
        NetworkInThread.setDaemon(true);
        NetworkInThread.setName("NetIn");
        NetworkInThread.start();
    }

    // Stop Threads
    public void Stop(){
        NetworkRun = false;
    }

    // Add to command queue
    public int Execute(SocketResultSet input){
        synchronized (this) {
            // Genorate ID
            int ID = UM.GenorateID();

            // Set ID
            input.setId(ID);

            // Add
            CommandQueue.addLast(input);

            // Return id
            return ID;
        }
    }

    // Execute Different CMD
    private void CommandExecutor(SocketResultSet input){
        // Get Differen PARAMS
        int ID = input.getId();
        ArrayList Params = input.getParam();
        String CMD = input.getCmd();

        Log.d("ServerTransport", "EXECUTOR Executing: " + CMD);

        // Switch block to handle special commands
        switch (CMD) {
            case "Connect":
                // Handle connecting
                Connected = true;

                // Make vars
                int Port = (int)Params.get(1);
                String IP = Params.get(0).toString();

                // Connect
                try {
                    // Make Socket
                    Socket Soc = new Socket(IP, Port);

                    // Writers
                    out = new PrintWriter(Soc.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(Soc.getInputStream()));
                    Log.d("ServerTransport", "Everything AOK at " + IP);
                    NotifyObservers(new SocketResultSet("CONNECT", new ArrayList(), -1, 0));
                    //return 0;
                } catch (UnknownHostException e) {
                    //System.err.println("Don't know about host " + IP);
                    //System.exit(1);
                    Log.e("ServerTransport", "Don't know about host " + IP);
                    NotifyObservers(new SocketResultSet("CONNECT", new ArrayList(), -1, 1));
                    //return 1;
                } catch (IOException e) {
                    //System.err.println("Couldn't get I/O for the connection to " + IP);
                    //System.exit(1);
                    Log.e("ServerTransport", "Couldn't get I/O for the connection to " + IP);
                    NotifyObservers(new SocketResultSet("CONNECT", new ArrayList(), -1, 2));
                    //return 2;
                }
                break;
            case "Close":
                // Handle Closing
                try {
                    // Stop Reading thread
                    Connected = false;

                    // Log state of connected
                    Log.d("ServerTransport", "Connected = " + Connected);

                    // Close streams
                    if (in != null && out != null) {
                        out.close();
                        in.close();
                    } else {
                        Log.e("ServerTransport", "Could not close streams because it wasnt connected");
                        NotifyObservers(new SocketResultSet("CLOSE", null, -1, 3));
                    }
                    
                } catch (IOException e) {
                    // Error
                    Log.e("ServerTransport", e.toString() + " at CommandExecutor");
                    NotifyObservers(new SocketResultSet("CLOSE", null, -1, 4));
                }
                // Break
                return;
        }

        // Check if null
        if (in != null && out != null){
            // Log
            Log.d("ServerTransport", "Connected = " + Connected);
            Log.d("ServerTransport", "IN = " + in.toString() + " OUT = " +out.toString());

            // Send Msg to server
            out.println(EncodeMsg(input));
        } else {
            // Notify Observer
            Log.e("ServerTransport", "Could not send becuase not connected");
            NotifyObservers(new SocketResultSet("CONNECT", null, -1, 5));
        }
    }

    // Send Thread to be use for sending to server
    class SendThread implements Runnable {
        public void run() {
            while (NetworkRun) {
                synchronized (this) {
                    if (CommandQueue.size() > 0) {
                        // Remove first element and then put in processed
                        SocketResultSet Current = CommandQueue.removeFirst();
                        ProcessedQueue.add(Current);

                        // Log commands
                        Log.d("ServerTransport", "Current Observer List - " + ServerListeners.size() + ": " + ServerListeners.toString());
                        Log.d("ServerTransport", "Current Queue - " + CommandQueue.size() + ": " + CommandQueue.toString());
                        Log.d("ServerTransport", "Executing: " + Current.getCmd());

                        // Execute corresponding command
                        CommandExecutor(Current);
                    }

                    // Sleeeeeeeeeeeeeeeeeeeeeeeeeeeep
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Log.e("ServerTransport", e.toString() + " at SendThread");
                    }

                }
            }
        }
    }

    // Read Thread to be used to listen from server
    class ReadThread implements Runnable {
        public void run(){
            while (NetworkRun) {
                // Check if in isnt null
                if (in != null && Connected) {
                    try {
                        // Get return from server
                        String Return = in.readLine();

                        // Notfiy Observer + Log
                        if (Return != null) {
                            // If msg recieved
                            SocketResultSet DecodedResult = DecodeResult(Return);
                            NotifyObservers(DecodedResult);
                            Log.d("ServerTransport", "Return From Server: " + Return);
                            Log.d("ServerTransport", "Formatted Return From Server: " + DecodedResult);
                        }

                    } catch (IOException e){
                        Log.e("ServerTransport", e.toString() + " at Read Thread");
                        NotifyObservers(new SocketResultSet("CONNECT", null, -1, 6));
                    }
                }

                // Sleep
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Log.e("ServerTransport", e.toString() + " at ReadThread");
                }
            }
        }
    }

    public ConnectionStates AdvanceConnect(ArrayList ConDetails, SocketResultSet Msg){
        // Get info
        int ID = Msg.getId();
        int Result = Msg.getResult();

        if (ID == -1) {
            // Detec Connection Errors
            switch (Result) {
                case 2:
                case 1:
                case 5:
                    // Try tp reconnect
                    if (ConnectionAttempts < 3) {
                        // Add to connection attempts
                        ConnectionAttempts += 1;

                        // Log
                        Log.e("ServerTransport", "Attempting to reconnect, try " + String.valueOf(ConnectionAttempts));

                        // Reconnect
                        this.Execute(new SocketResultSet("Connect",ConDetails));

                        // Return
                        return ConnectionStates.RECONNECTING;
                    } else {
                        // Reset
                        ConnectionAttempts = 0;

                        // Return
                        return ConnectionStates.CONNECTION_ERROR;
                    }
                case 0:
                    // If sucessfully Connected //
                    ConnectionAttempts = 0;
                    return ConnectionStates.CONNECTED;
                default:
                    // Another error
                    return ConnectionStates.ERROR;
            }
        } else if (Result == 20) {
            // If server sent disconnected msg
            Execute(new SocketResultSet("Close"));
            return ConnectionStates.DISCONNECTED;
        } else {
            return ConnectionStates.IGNORE;
        }
    }
}