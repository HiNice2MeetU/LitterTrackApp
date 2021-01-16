package dev.hiworld.littertrackingapp.Network;

import java.util.ArrayList;

public class MQMsg {
    // Globals
    private ArrayList<String> TypeList = new ArrayList<String>();
    private ArrayList<Object>Params = new ArrayList<Object>();
    private String SessionID;
    private String TransactionID;
    private String Cmd;
    private int Result;

    // Construtors
    public MQMsg(ArrayList<String> typeList, ArrayList<Object> params, String sessionID, String transactionID, String cmd, int result) {
        TypeList = typeList;
        Params = params;
        SessionID = sessionID;
        TransactionID = transactionID;
        Cmd = cmd;
        Result = result;
    }

    public MQMsg(ArrayList<Object> params, String sessionID, String transactionID, String cmd, int result) {
        Params = params;
        SessionID = sessionID;
        TransactionID = transactionID;
        Cmd = cmd;
        Result = result;
    }

    public MQMsg(ArrayList<Object> params, String sessionID, String transactionID, String cmd) {
        Params = params;
        SessionID = sessionID;
        TransactionID = transactionID;
        Cmd = cmd;
    }

    public MQMsg(ArrayList<Object> params, String TID, String cmd) {
        Params = params;
        TransactionID = TID;
        Cmd = cmd;
    }

    public MQMsg(ArrayList<Object> params, String cmd) {
        Params = params;
        Cmd = cmd;
    }

    public MQMsg(String cmd) {
        Cmd = cmd;
    }

    // Getters / Setters

    public ArrayList<String> getTypeList() {
        return TypeList;
    }

    public void setTypeList(ArrayList<String> typeList) {
        TypeList = typeList;
    }

    public ArrayList<Object> getParams() {
        return Params;
    }

    public void setParams(ArrayList<Object> params) {
        Params = params;
    }

    public String getSessionID() {
        return SessionID;
    }

    public void setSessionID(String sessionID) {
        SessionID = sessionID;
    }

    public String getTransactionID() {
        return TransactionID;
    }

    public void setTransactionID(String transactionID) {
        TransactionID = transactionID;
    }

    public String getCmd() {
        return Cmd;
    }

    public void setCmd(String cmd) {
        Cmd = cmd;
    }

    public int getResult() {
        return Result;
    }

    public void setResult(int result) {
        Result = result;
    }

    // To String

    @Override
    public String toString() {
        return "MQMsg{" +
                "TypeList=" + TypeList +
                ", Params=" + Params +
                ", SessionID='" + SessionID + '\'' +
                ", TransactionID='" + TransactionID + '\'' +
                ", Cmd='" + Cmd + '\'' +
                ", Result=" + Result +
                '}';
    }
}
