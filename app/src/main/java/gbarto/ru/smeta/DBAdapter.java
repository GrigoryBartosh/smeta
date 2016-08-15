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
	public static final int DATABASE_VERSION = 1;
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

    public static final String WORKS_KEY_STATE = "workState";
	public static final String WORKS_KEY_NAME = "workName";
    public static final String WORKS_KEY_MATERIALS = "workMaterials";
    public static final String WORKS_KEY_PRICE = "workPrice";
    public static final String WORKS_KEY_MEASURING = "workMeasuring";
    public static final String WORKS_KEY_WORKTYPE = "workType";
	public static final String[] WORKS_ALL_KEYS = new String[] {KEY_ROWID, WORKS_KEY_STATE, WORKS_KEY_NAME, WORKS_KEY_MATERIALS, WORKS_KEY_PRICE, WORKS_KEY_MEASURING, WORKS_KEY_WORKTYPE};
	public static final String WORKS_TABLE = "worksTable";
	private static final String WORKS_CREATE_SQL =
			"create table " + WORKS_TABLE + " ("
					+ KEY_ROWID + " integer primary key autoincrement, "
					+ WORKS_KEY_STATE + " int not null, "
                    + WORKS_KEY_NAME + " text not null, "
                    + WORKS_KEY_MATERIALS + " text not null, "
                    + WORKS_KEY_PRICE + " float not null, "
                    + WORKS_KEY_MEASURING + " int not null, "
                    + WORKS_KEY_WORKTYPE + " int not null"
					+ ");";

    // BASE 3:

    public static final String TYPES_KEY_PLACE = "place";
    public static final String TYPES_KEY_TYPE = "workType";
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
            temp.put("name", material.getName());
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
            JSONArray tmp = new JSONArray();
            JSONArray tmp2 = new JSONArray();
            for (Pair <Long, Float> x : work.getMaterials())
            {
                tmp.put(x.first);
                tmp2.put(x.second);
            }
            temp.put("materials", tmp);
            temp.put("requirements", tmp);
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
            temp.put("name", type.getName());
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
            JSONArray tmp2 = x.getJSONArray("requirements");
            ArrayList <Pair <Long, Float> > temp = new ArrayList<>();
            for (int i = 0; i < tmp.length(); ++i)
                temp.add(new Pair(tmp.getLong(i), (float)tmp2.getDouble(i)));
            return new WorkClass(false, "", temp, 0, 0, 0);
        }
        catch (JSONException e) {
            return null;
        }
    }

    private MaterialClass JSONtoMaterialClass (JSONObject x)
    {
        try {
            return new MaterialClass(x.getString("name"), (float)x.getDouble("price"), x.getInt("measuring"), x.getInt("iconID"));
        }
        catch (JSONException e) {
            return null;
        }
    }

    public long add(DBObject x)
    {
        if (x instanceof WorkClass)
            return add((WorkClass)x);
        if (x instanceof TypeClass)
            return add((TypeClass)x);
        if (x instanceof MaterialClass)
            return add((MaterialClass)x);
        return -1;
    }

    public boolean update(DBObject x)
    {
        if (x instanceof WorkClass)
            return update((WorkClass)x);
        if (x instanceof TypeClass)
            return update((TypeClass)x);
        if (x instanceof MaterialClass)
            return update((MaterialClass)x);
        return false;
    }

	// Adds new material to DB and returns you row_id
	public long add(MaterialClass material) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(MATERIAL_KEY_MATERIAL, ToJSON(material).toString());
        material.rowID = db.insert(MATERIAL_TABLE, null, initialValues);
        return material.rowID;
	}

    public boolean update(MaterialClass material)
    {
        String where = KEY_ROWID + "=" + material.rowID;
        ContentValues newValues = new ContentValues();
        newValues.put(MATERIAL_KEY_MATERIAL,  ToJSON(material).toString());
        return db.update(MATERIAL_TABLE, newValues, where, null) != 0;
    }

    // Adds new work to DB and returns you row_id
    public long add(WorkClass work) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(WORKS_KEY_NAME, work.getName());
        initialValues.put(WORKS_KEY_STATE, work.isState());
        initialValues.put(WORKS_KEY_MEASURING, work.getMeasuring());
        initialValues.put(WORKS_KEY_MATERIALS, ToJSON(work).toString());
        initialValues.put(WORKS_KEY_PRICE, work.getPrice());
        initialValues.put(WORKS_KEY_WORKTYPE, work.getWorkType());
        work.rowID = db.insert(WORKS_TABLE, null, initialValues);
        return work.rowID;
    }

    public boolean update(WorkClass work)
    {
        String where = KEY_ROWID + "=" + work.rowID;
        ContentValues newValues = new ContentValues();
        newValues.put(MATERIAL_KEY_MATERIAL,  ToJSON(work).toString());
        return db.update(MATERIAL_TABLE, newValues, where, null) != 0;
    }

    // Adds new workType to DB and returns you row_id
    public long add(TypeClass type) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(TYPES_KEY_PLACE, type.getPlace());
        initialValues.put(TYPES_KEY_TYPE, ToJSON(type).toString());
        type.rowID = db.insert(TYPES_TABLE, null, initialValues);
        return type.rowID;
    }

    public boolean update(TypeClass type)
    {
        String where = KEY_ROWID + "=" + type.rowID;
        ContentValues newValues = new ContentValues();
        newValues.put(MATERIAL_KEY_MATERIAL,  ToJSON(type).toString());
        return db.update(MATERIAL_TABLE, newValues, where, null) != 0;
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
                            String structure = c.getString(3);
                            WorkClass tmp = JSONtoWorkListView(FromString(structure));
                            tmp.setState(c.getInt(1) == 1);
                            tmp.setName(c.getString(2));
                            tmp.setPrice(c.getFloat(4));
                            tmp.setMeasuring(c.getInt(5));
                            tmp.setWorkType(c.getInt(6));
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

    public DBObject[] getSelectionRows(String tablename, String where)
    {
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
                            String structure = c.getString(3);
                            WorkClass tmp = JSONtoWorkListView(FromString(structure));
                            tmp.setState(c.getInt(1) == 1);
                            tmp.setName(c.getString(2));
                            tmp.setPrice(c.getFloat(4));
                            tmp.setMeasuring(c.getInt(5));
                            tmp.setWorkType(c.getInt(6));
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

    public DBObject[] getWorksByType(long type)
    {
        String where = WORKS_KEY_WORKTYPE + "=" + Long.toString(type);
        return getSelectionRows(WORKS_TABLE, where);
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

        void add(SQLiteDatabase _db, String tablename, DBObject t)
        {
            ContentValues initialValues = new ContentValues();
            switch (tablename)
            {
                case MATERIAL_TABLE:
                    //public static final String[] MATERIAL_ALL_KEYS = new String[] {KEY_ROWID, MATERIAL_KEY_MATERIAL};
                    initialValues.put(MATERIAL_KEY_MATERIAL, t.getName());
                    break;
                case WORKS_TABLE:
                    //public static final String[] WORKS_ALL_KEYS = new String[] {KEY_ROWID, WORKS_KEY_STATE, WORKS_KEY_NAME, WORKS_KEY_MATERIALS, WORKS_KEY_PRICE, WORKS_KEY_MEASURING, WORKS_KEY_WORKTYPE};
                    WorkClass s = (WorkClass)t;
                    initialValues.put(WORKS_KEY_STATE, s.isState());
                    initialValues.put(WORKS_KEY_NAME, s.getName());
                    initialValues.put(WORKS_KEY_MATERIALS, ToJSON(s).toString());
                    initialValues.put(WORKS_KEY_PRICE, s.getPrice());
                    initialValues.put(WORKS_KEY_MEASURING, s.getMeasuring());
                    initialValues.put(WORKS_KEY_WORKTYPE, s.getWorkType());
                    break;
                case TYPES_TABLE:
                    //public static final String[] TYPES_ALL_KEYS = new String[] {KEY_ROWID, TYPES_KEY_PLACE, TYPES_KEY_TYPE};
                    initialValues.put(TYPES_KEY_PLACE, "");
                    initialValues.put(TYPES_KEY_TYPE, t.getName());
                    break;
            }
            _db.insert(tablename, null, initialValues);

        }

        private JSONObject ToJSON(WorkClass work)
        {
            JSONObject temp = new JSONObject();
            try
            {
                JSONArray tmp = new JSONArray();
                JSONArray tmp2 = new JSONArray();
                for (Pair <Long, Float> x : work.getMaterials())
                {
                    tmp.put(x.first);
                    tmp2.put(x.second);
                }
                temp.put("materials", tmp);
                temp.put("requirements", tmp);
                return temp;
            }
            catch (JSONException e) {
                return null;
            }
        }

		@Override
		public void onCreate(SQLiteDatabase _db)
		{
			_db.execSQL(MATERIAL_CREATE_SQL);
			_db.execSQL(WORKS_CREATE_SQL);
            _db.execSQL(TYPES_CREATE_SQL);

            TypeClass t1 = new TypeClass("", "Пол");
            add(_db, TYPES_TABLE, t1);
            t1 = new TypeClass("", "Стены");
            add(_db, TYPES_TABLE, t1);
            t1 = new TypeClass("", "Потолок");
            add(_db, TYPES_TABLE, t1);

            WorkClass t2 = new WorkClass(false, "Намазать пол говном", new ArrayList<Pair <Long, Float>>(), 1.15f, 1, 1);
            add(_db, WORKS_TABLE, t2);
            t2 = new WorkClass(false, "Намазать стены говном", new ArrayList<Pair <Long, Float>>(), 1.15f, 1, 2);
            add(_db, WORKS_TABLE, t2);
            t2 = new WorkClass(false, "Намазать потолок говном", new ArrayList<Pair <Long, Float>>(), 1.15f, 1,3);
            add(_db, WORKS_TABLE, t2);
            t2 = new WorkClass(false, "Вымазать потолок говном", new ArrayList<Pair <Long, Float>>(), 1.15f, 1, 3);
            add(_db, WORKS_TABLE, t2);
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
