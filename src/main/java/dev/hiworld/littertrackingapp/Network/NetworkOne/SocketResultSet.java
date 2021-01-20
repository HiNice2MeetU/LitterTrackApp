package dev.hiworld.littertrackingapp.Network.NetworkOne;

import android.util.Log;

import java.util.ArrayList;

public class SocketResultSet {
    private String Cmd;
    private ArrayList Param = new ArrayList<String>();
    private ArrayList<String> ParamTypes = new ArrayList<String>();
    private int Id;
    private int Result;

    public SocketResultSet(String cmd) {
        Cmd = cmd;
    }

    public SocketResultSet(String cmd, ArrayList param) {
        Cmd = cmd;
        Param = param;
    }

    public SocketResultSet(String cmd, ArrayList param, int id) {
        Cmd = cmd;
        Param = param;
        Id = id;
    }

    public SocketResultSet(String cmd, ArrayList param, int id, int result) {
        Cmd = cmd;
        Param = param;
        Id = id;
        Result = result;
    }

    public SocketResultSet(String cmd, ArrayList param, ArrayList<String> paramTypes, int id, int result) {
        Cmd = cmd;
        Param = param;
        ParamTypes = paramTypes;
        Id = id;
        Result = result;
    }

    public ArrayList<String> getParamTypes() {
        return ParamTypes;
    }

    public void setParamTypes(ArrayList<String> paramTypes) {
        ParamTypes = paramTypes;
    }

    public void AutoFillType(){
        // Empty Param types
        ParamTypes.clear();

        // Automagically fill the type spaces
        if (Param != null) {
            for (int i = 0; i < Param.size(); i++) {
                // Get current obj
                Object Current = Param.get(i);

                // Add class name to param types
                ParamTypes.add(Current.getClass().getName());
            }
        } else {
            Log.d("SocketResultSet", "AutoFill when param == null");
        }
    }

    public String getCmd() {
        return Cmd;
    }

    public void setCmd(String cmd) {
        Cmd = cmd;
    }

    public ArrayList getParam() {
        return Param;
    }

    public void setParam(ArrayList param) {
        Param = param;
    }

    public int getResult() {
        return Result;
    }

    public void setResult(int result) {
        Result = result;
    }

    @Override
    public String toString() {
        if (Param != null) {
            return "ServerResultSet{" + "Cmd='" + Cmd + '\'' + ", Param=" + Param.toString() + ", Id=" + Id + ", Result=" + Result + '}';
        } else {
            return "ServerResultSet{" + "Cmd='" + Cmd + '\'' + ", Id=" + Id + ", Result=" + Result + '}';
        }
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }
}
