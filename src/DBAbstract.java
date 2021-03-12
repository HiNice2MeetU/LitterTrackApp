import java.util.ArrayList;
import java.sql.*;

public class DBAbstract {
	// Globals
	DBConnection Conn = new DBConnection();

	public void Close() {
		// Close
		Conn.Close();
	}

	public void Connect(String File) {
		// Connect
		Conn.Connect(File);
	}

	public void AddRow(Event Input) {
		// Update Statement
		Conn.Update("INSERT INTO Trash (Lat,Lng, Img, Name) VALUES (?,?,?,?)", EventToSQL(Input));
	}
	
	public ArrayList<Event> GetAll() {
		return SQLToEventList(Conn.Query("SELECT * FROM Trash", null));
	}

	public Event GetRow(int Index) {
		// Query Statement
		return SQLToEvent(Conn.Query("SELECT * FROM Trash WHERE ID = ?",new Object[] {Index}));	
	}

	private Object[] EventToSQL(Event Input) {
		// Serialize a event into sql
		return new Object[] {Input.getLongitude(), Input.getLatitude(), Input.getBmp(), Input.getDisplayName()};
	}

	private Event SQLToEvent(ResultSet Input) {
		try {
			// Init Return
			Event Return = new Event();

			// Serialize a rsult set int sql
			while(Input.next()) {
				// Execute while result set is full
				Return.setLongitude(Input.getDouble(2));
				Return.setLatitude(Input.getDouble(3));
				Return.setBmp(Input.getString(4));
				Return.setDisplayName(Input.getString(5));
			}

			// Return
			return Return;
		} catch (Exception e) {
			Log.Err("SQLITE", e.toString());
			return null;
		}
	}


	private ArrayList<Event> SQLToEventList(ResultSet Input) {
		try {
			// Init Return
			ArrayList<Event> Return = new ArrayList<Event>();

			// Serialize a rsult set int sql
			while(Input.next()) {
				// Define temp return
				Event TempReturn = new Event();
				
				// Set Params
				TempReturn.setLongitude(Input.getDouble(2));
				TempReturn.setLatitude(Input.getDouble(3));
				TempReturn.setBmp(Input.getString(4));
				TempReturn.setDisplayName(Input.getString(5));

				// Add to proper return
				Return.add(TempReturn);
			}

			// Return
			return Return;
		} catch (Exception e) {
			Log.Err("SQLITE", e.toString());
			return null;
		}
	}
}
