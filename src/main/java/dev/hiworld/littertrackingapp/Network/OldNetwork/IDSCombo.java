package dev.hiworld.littertrackingapp.Network.OldNetwork;

public class IDSCombo {
    // Globals
    int ID;
    ServerScheduler.ResultListener Listener;

    public IDSCombo(int ID, ServerScheduler.ResultListener listener) {
        this.ID = ID;
        Listener = listener;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public ServerScheduler.ResultListener getListener() {
        return Listener;
    }

    public void setListener(ServerScheduler.ResultListener listener) {
        Listener = listener;
    }
}