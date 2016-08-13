package gbarto.ru.smeta;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DBAdapter {

	/////////////////////////////////////////////////////////////////////
	//	Constants & Data
	/////////////////////////////////////////////////////////////////////
	// For logging:
	private static final String TAG = "DBAdapter";
	
	// DB Fields
	public static final String KEY_ROWID = "_id";
	public static final int DATABASE_VERSION = 2;
	public static final String DATABASE_NAME = "MyDb";

	// BASE 1:

	public static final String MATERIAL_KEY_MATERIAL = "name";
	public static final String[] MATERIAL_ALL_KEYS = new String[] {KEY_ROWID, MATERIAL_KEY_MATERIAL};
	public static final String MATERIAL_TABLE = "materialTable";
	private static final String MATERIAL_CREATE_SQL =
			"create table " + MATERIAL_TABLE + " ("
			+ KEY_ROWID + " integer primary key autoincrement, "
			+ MATERIAL_KEY_MATERIAL + " text not null"
			+ ");";

	// BASE 2:

	public static final String WORKS_KEY_TYPE = "type";
	public static final String[] WORKS_ALL_KEYS = new String[] {KEY_ROWID, WORKS_KEY_TYPE};
	public static final String WORKS_TABLE = "worksTable";
	private static final String WORKS_CREATE_SQL =
			"create table " + WORKS_TABLE + " ("
					+ KEY_ROWID + " integer primary key autoincrement, "
					+ WORKS_KEY_TYPE + " text not null"
					+ ");";

    // BASE 3:

    public static final String TYPES_KEY_PLACE = "place";
    public static final String TYPES_KEY_TYPE = "type";
    public static final String[] TYPES_ALL_KEYS = new String[] {KEY_ROWID, TYPES_KEY_PLACE, TYPES_KEY_TYPE};
    public static final String TYPES_TABLE = "typesTable";
    private static final String TYPES_CREATE_SQL =
            "create table " + TYPES_TABLE + " ("
                    + KEY_ROWID + " integer primary key autoincrement, "
                    + TYPES_KEY_PLACE + " text not null, "
                    + TYPES_KEY_TYPE + " text not null"
                    + ");";

	
	// Context of application who uses us.

	protected final Context context;
	private DatabaseHelper myDBHelper;
	private SQLiteDatabase db;

	/////////////////////////////////////////////////////////////////////
	//	Public methods:
	/////////////////////////////////////////////////////////////////////
	
	public DBAdapter(Context ctx) {
		this.context = ctx;
		myDBHelper = new DatabaseHelper(context);
	}
	
	// Open the database connection.
	public DBAdapter open() {
		db = myDBHelper.getWritableDatabase();
		return this;
	}
	
	// Close the database connection.
	public void close() {
		myDBHelper.close();
	}

    private JSONObject ToJSON(MaterialClass material)
    {
        JSONObject temp = new JSONObject();
        try
        {
            temp.put("material", material.getMaterial());
            temp.put("price", material.getPrice());
            temp.put("measuring", material.getMeasuring());
            temp.put("iconID", material.getIconID());
            return temp;
        }
        catch (JSONException e) {
            return null;
        }
    }

    private JSONObject ToJSON(WorkClass work)
    {
        JSONObject temp = new JSONObject();
        try
        {
            temp.put("type", work.getType());
            temp.put("state", work.isState());
            temp.put("measuring", work.getMeasuring());
            temp.put("price", work.getPrice());
            JSONArray tmp = new JSONArray();
            for (int x : work.getMaterials())
                tmp.put(x);
            temp.put("materials", tmp);
            return temp;
        }
        catch (JSONException e) {
            return null;
        }
    }

    private JSONObject ToJSON(TypeClass type)
    {
        JSONObject temp = new JSONObject();
        try
        {
            temp.put("type", type.getType());
            return temp;
        }
        catch (JSONException e) {
            return null;
        }
    }

    private JSONObject FromString(String s)
    {
        try {
            return new JSONObject(s);
        }
        catch (JSONException e) {
            return null;
        }

    }

    private WorkClass JSONtoWorkListView (JSONObject x)
    {
        try {
            JSONArray tmp = x.getJSONArray("materials");
            ArrayList <Integer> temp = new ArrayList<>();
            for (int i = 0; i < tmp.length(); ++i)
                temp.add((Integer)tmp.getInt(i));
            return new WorkClass(x.getBoolean("state"), x.getString("type"), temp, (float)x.getDouble("price"), x.getInt("measuring"));
        }
        catch (JSONException e) {
            return null;
        }
    }

    private MaterialClass JSONtoMaterialClass (JSONObject x)
    {
        try {
            return new MaterialClass(x.getString("material"), (float)x.getDouble("price"), x.getInt("measuring"), x.getInt("iconID"));
        }
        catch (JSONException e) {
            return null;
        }
    }

	// Adds new material to DB and returns you row_id
	public long add(MaterialClass material) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(MATERIAL_KEY_MATERIAL, ToJSON(material).toString());
        material.rowID = db.insert(MATERIAL_TABLE, null, initialValues);
        return material.rowID;
	}

    // Adds new work to DB and returns you row_id
    public long add(WorkClass work) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(WORKS_KEY_TYPE, ToJSON(work).toString());
        work.rowID = db.insert(WORKS_TABLE, null, initialValues);
        return work.rowID;
    }

    // Adds new type to DB and returns you row_id
    public long add(TypeClass type) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(TYPES_KEY_PLACE, type.getPlace());
        initialValues.put(TYPES_KEY_TYPE, type.getType());
        type.rowID = db.insert(TYPES_TABLE, null, initialValues);
        return type.rowID;
    }

	// Delete a row from the database, by rowId (primary key)
	public boolean deleteRow(String tablename, long rowId) {
		String where = KEY_ROWID + "=" + rowId;
		return db.delete(tablename, where, null) != 0;
	}
	
	public void clear(String tablename) {
		Cursor c = db.query(true, tablename, MATERIAL_ALL_KEYS,
                null, null, null, null, null, null);
		long rowId = c.getColumnIndexOrThrow(KEY_ROWID);
		if (c.moveToFirst()) {
			do {
				deleteRow(tablename, c.getLong((int) rowId));
			} while (c.moveToNext());
		}
		c.close();
	}

	public DBObject[] getAllRows(String tablename) //returns whole
	{
		String where = null;
        ArrayList<DBObject> temp = new ArrayList<>();
		Cursor c = null;
		switch (tablename)
		{
			case MATERIAL_TABLE:
				c =	db.query(true, tablename, MATERIAL_ALL_KEYS,
						where, null, null, null, null, null);
				break;
			case WORKS_TABLE:
				c =	db.query(true, tablename, WORKS_ALL_KEYS,
						where, null, null, null, null, null);
				break;
            case TYPES_TABLE:
                c =	db.query(true, tablename, TYPES_ALL_KEYS,
                        where, null, null, null, null, null);
                break;
		}
		if (c != null)
            if (c.moveToFirst())
                do
                {
                    switch (tablename)
                    {
                        case MATERIAL_TABLE:
                        {
                            String structure = c.getString(1);
                            MaterialClass tmp = JSONtoMaterialClass(FromString(structure));
                            tmp.setRowID(c.getInt(0));
                            temp.add(tmp);
                            break;
                        }
                        case WORKS_TABLE:
                        {
                            String structure = c.getString(1);
                            WorkClass tmp = JSONtoWorkListView(FromString(structure));
                            tmp.setRowID(c.getInt(0));
                            temp.add(tmp);
                            break;
                        }
                        case TYPES_TABLE:
                        {
                            TypeClass tmp = new TypeClass(c.getString(1), c.getString(2));
                            tmp.setRowID(c.getInt(0));
                            temp.add(tmp);
                            break;
                        }
                    }
                } while (c.moveToNext());
        DBObject[] tmp2 = new DBObject[temp.size()];
        tmp2 = temp.toArray(tmp2);
		return tmp2;
	}

    public DBObject[] getSelectionRows(String tablename, String where, String[] Args) //returns whole
    {
        ArrayList<DBObject> temp = new ArrayList<>();
        Cursor c = null;
        switch (tablename)
        {
            case MATERIAL_TABLE:
                c =	db.query(true, tablename, MATERIAL_ALL_KEYS,
                        where, Args, null, null, null, null);
                break;
            case WORKS_TABLE:
                c =	db.query(true, tablename, WORKS_ALL_KEYS,
                        where, Args, null, null, null, null);
                break;
            case TYPES_TABLE:
                c =	db.query(true, tablename, TYPES_ALL_KEYS,
                        where, Args, null, null, null, null);
                break;
        }
        if (c != null)
            if (c.moveToFirst())
                do
                {
                    switch (tablename)
                    {
                        case MATERIAL_TABLE:
                        {
                            String structure = c.getString(1);
                            MaterialClass tmp = JSONtoMaterialClass(FromString(structure));
                            tmp.setRowID(c.getInt(0));
                            temp.add(tmp);
                            break;
                        }
                        case WORKS_TABLE:
                        {
                            String structure = c.getString(1);
                            WorkClass tmp = JSONtoWorkListView(FromString(structure));
                            tmp.setRowID(c.getInt(0));
                            temp.add(tmp);
                            break;
                        }
                        case TYPES_TABLE:
                        {
                            TypeClass tmp = new TypeClass(c.getString(1), c.getString(2));
                            tmp.setRowID(c.getInt(0));
                            temp.add(tmp);
                            break;
                        }
                    }
                } while (c.moveToNext());
        DBObject[] tmp2 = new DBObject[temp.size()];
        tmp2 = temp.toArray(tmp2);
        return tmp2;
    }

	// Get a specific row (by rowId)
	public DBObject getRow(String tablename, long rowId) {
		String where = KEY_ROWID + "=" + rowId;
		Cursor c = null;
        switch (tablename)
        {
            case MATERIAL_TABLE:
                c =	db.query(true, tablename, MATERIAL_ALL_KEYS,
                        where, null, null, null, null, null);
                break;
            case WORKS_TABLE:
                c =	db.query(true, tablename, WORKS_ALL_KEYS,
                        where, null, null, null, null, null);
                break;
        }
        if (c != null) {
            c.moveToFirst();
        }
        switch (tablename)
        {
            case MATERIAL_TABLE:
            {
                String structure = c.getString(1);
                return JSONtoMaterialClass(FromString(structure));
            }
            case WORKS_TABLE:
            {
                String structure = c.getString(1);
                return JSONtoWorkListView(FromString(structure));
            }
        }
		return null;
	}

	public boolean updateRow(long rowId, MaterialClass material) //updates material
	{
		String where = KEY_ROWID + "=" + rowId;
		ContentValues newValues = new ContentValues();
		newValues.put(MATERIAL_KEY_MATERIAL,  material.material);
		//newValues.put(MATERIAL_KEY_PRICE,     material.price);
		//newValues.put(MATERIAL_KEY_MEASURING, material.measuring);
		return db.update(MATERIAL_TABLE, newValues, where, null) != 0;
	}


	public boolean updateRow(long rowId, String material, float price, String measuring)
	{
		String where = KEY_ROWID + "=" + rowId;

		// Create row's data:
		ContentValues newValues = new ContentValues();
		newValues.put(MATERIAL_KEY_MATERIAL,  material);
		//newValues.put(MATERIAL_KEY_PRICE,     price);
		//newValues.put(MATERIAL_KEY_MEASURING, measuring);
		
		// Insert it into the database.
		return db.update("", newValues, where, null) != 0;
	}
	
	
	
	/////////////////////////////////////////////////////////////////////
	//	Private Helper Classes:
	/////////////////////////////////////////////////////////////////////
	
	/**
	 * Private class which handles database creation and upgrading.
	 * Used to handle low-level database access.
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

        void add(SQLiteDatabase _db, String tablename, String s)
        {
            ContentValues initialValues = new ContentValues();
            switch (tablename)
            {
                case MATERIAL_TABLE:
                    initialValues.put(MATERIAL_KEY_MATERIAL, s);
                    break;
                case WORKS_TABLE:
                    initialValues.put(WORKS_KEY_TYPE, s);
                    break;
                case TYPES_TABLE:
                    String[] tmp = s.split(":;");
                    initialValues.put(TYPES_KEY_PLACE, tmp[0]);
                    initialValues.put(TYPES_KEY_TYPE, tmp[1]);
                    break;
            }
            _db.insert(tablename, null, initialValues);

        }

		@Override
		public void onCreate(SQLiteDatabase _db)
		{
			_db.execSQL(MATERIAL_CREATE_SQL);
			_db.execSQL(WORKS_CREATE_SQL);
            _db.execSQL(TYPES_CREATE_SQL);
            add(_db, TYPES_TABLE, "Кухня:;Полы");
            add(_db, TYPES_TABLE, "Кухня:;Стены");
            add(_db, TYPES_TABLE, "Кухня:;Потолки");
            add(_db, TYPES_TABLE, "Хуй:;Член");
            add(_db, TYPES_TABLE, "Пидор:;Гомик");
            //add(_db, WORKS_TABLE, "{\"type\":\"Стены\",\"state\":false,\"measuring\":\"\",\"price\":0,\"materials\":[]}");
            //add(_db, WORKS_TABLE, "{\"type\":\"Полы\",\"state\":false,\"measuring\":\"\",\"price\":0,\"materials\":[]}");
            //add(_db, WORKS_TABLE, "{\"type\":\"Потолки\",\"state\":false,\"measuring\":\"\",\"price\":0,\"materials\":[]}");
		}

		@Override
		public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading application's database from version " + oldVersion
					+ " to " + newVersion + ", which will destroy all old data!");
			
			// Destroy old database:
			_db.execSQL("DROP TABLE IF EXISTS " + MATERIAL_TABLE);
			_db.execSQL("DROP TABLE IF EXISTS " + WORKS_TABLE);
            _db.execSQL("DROP TABLE IF EXISTS " + TYPES_TABLE);
			
			// Recreate new database:
			onCreate(_db);
		}

        @Override
        public void onDowngrade(SQLiteDatabase _db, int oldVersion, int newVersion)
        {
            Log.w(TAG, "Upgrading application's database from version " + oldVersion
                    + " to " + newVersion + ", which will destroy all old data!");

            // Destroy old database:
            _db.execSQL("DROP TABLE IF EXISTS " + MATERIAL_TABLE);
            _db.execSQL("DROP TABLE IF EXISTS " + WORKS_TABLE);
            _db.execSQL("DROP TABLE IF EXISTS " + TYPES_TABLE);

            // Recreate new database:
            onCreate(_db);
        }
    }
}
