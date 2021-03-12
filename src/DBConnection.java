import java.sql.*;

public class DBConnection {
	// Globals
	Connection Conn = null;

	public void Connect(String FilePath) {
		try {
			// Get Driver
			Class.forName("org.sqlite.JDBC");

			// Create Connection to db
			Conn = DriverManager.getConnection("jdbc:sqlite:"+FilePath);
			Log.Stn("SQLITE","Connection to"+FilePath+" Is active");	
		} catch (Exception e) {
			// Log
			Log.Err("SQLITE", e);
		}
	}

	protected void Update (String SQL, Object[] Params) {
		// Base SQL Execute Function
		try {
			// Make Prepared Statement
			PreparedStatement PST = Conn.prepareStatement(SQL);

			// Iterate through params
			if (Params != null) {
				for (int i=0; i<Params.length; i++) {
					// Add param to prepared statement
					Log.Stn("SQLITE/Inserting ", Params[i].toString());
					PST.setObject(i+1, Params[i]);
				}
			}

			// Execute
			PST.executeUpdate();

		} catch (SQLException e) {
			// Log
			Log.Err("SQLITE", e);
		}
	}
	
	
	protected ResultSet Query (String SQL, Object[] Params) {
		// Base SQL Execute Function
		try {
			// Make Prepared Statement
			PreparedStatement PST = Conn.prepareStatement(SQL);

			// Iterate through params
			if (Params != null) {
				for (int i=0; i<Params.length; i++) {
					// Add param to prepared statement
					Log.Stn("SQLITE/Inserting ", Params[i].toString());
					PST.setObject(i+1, Params[i]);
				}
			}

			// Return result set
			return PST.executeQuery();

		} catch (SQLException e) {
			// Log
			Log.Err("SQLITE", e);
			return null;
		}
	}

	protected void Close() {
		// Close Connection
		try {
			// Check if con is null
			if (Conn != null) {
				// Close connection
				Conn.close();
				Log.Stn("SQLITE", "Connection has been closed");
			}
		} catch (Exception e) {
			// In case something goes wrong
			Log.Err("SQLITE",e.toString());
		}
	}
}
