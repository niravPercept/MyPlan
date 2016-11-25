package percept.myplan.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by percept on 6/10/16.
 */

public class ContactTable extends DataBaseHelper {
    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     *
     * @param context
     */

    public String TITLE = "Title";
    public String DIRECTOR_TITLE = "DirectorTitle";
    public String PHOTOGRAPHER_TITLE = "PhotographerTitle";
    public String INITIAL_DATE = "InitialDate";
    public String ID = "_id";
    public String SCRIPT_FILE = "ScriptFile";
    public String TABLE = "tblFilm";

    public ContactTable(Context context) {
        super(context);
    }


    public long insertData(String title, String directorTitle, String photographerTitle, String initialDate,
                           String scriptFile) {

        SQLiteDatabase db = this.openDataBase();

        try {
            ContentValues values = new ContentValues();
            values.put(TITLE, title);
            values.put(DIRECTOR_TITLE, directorTitle);
            values.put(PHOTOGRAPHER_TITLE, photographerTitle);
            values.put(INITIAL_DATE, initialDate);
            values.put(SCRIPT_FILE, scriptFile);
            long id = db.insert(TABLE, null, values);
            return id;
        } catch (Exception e) {
            return 0;
        } finally {
            db.close();
            this.close();
        }
    }


  /*  public ArrayList<FilmDo> getFilm() {
        SQLiteDatabase db = this.openDataBase();
        ArrayList<FilmDo> arrayList = new ArrayList<FilmDo>();
        try {

            String query = "SELECT * FROM '" + TABLE + "' ORDER BY " + ID + " DESC";
            Cursor c = db.rawQuery(query, null);
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        String _title = c.getString(c
                                .getColumnIndex(TITLE));
                        String _directorTitle = c.getString(c.getColumnIndex(DIRECTOR_TITLE));
                        String _photographerTitle = c.getString(c.getColumnIndex(PHOTOGRAPHER_TITLE));
                        String _initialDate = c.getString(c.getColumnIndex(INITIAL_DATE));
                        String _scriptFile = c.getString(c.getColumnIndex(SCRIPT_FILE));
                        int _ID = c.getInt(c.getColumnIndex(ID));

                        FilmDo _obj = new FilmDo();
                        _obj.setID(_ID);
                        _obj.setTitle(_title);
                        _obj.setDirectorTitle(_directorTitle);
                        _obj.setPhotographerTitle(_photographerTitle);
                        _obj.setInitialDate(_initialDate);
                        _obj.setScriptFile(_scriptFile);
                        arrayList.add(_obj);
                    } while (c.moveToNext());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
            this.close();
        }
        return arrayList;
    }

    public String getFilmScript(int film_id) {
        SQLiteDatabase db = this.openDataBase();
        String _scriptFile = "";
        try {

            String query = "SELECT * FROM '" + TABLE + "' WHERE " + ID + " = " + film_id + "";
            Cursor c = db.rawQuery(query, null);
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        _scriptFile = c.getString(c.getColumnIndex(SCRIPT_FILE));
                    } while (c.moveToNext());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
            this.close();
        }
        return _scriptFile;
    }

    public FilmDo getFilm(int _id) {
        SQLiteDatabase db = this.openDataBase();
        FilmDo _obj = new FilmDo();
        try {

            String query = "SELECT * FROM '" + TABLE + "' WHERE " + ID + " = " + _id + "";
            Cursor c = db.rawQuery(query, null);
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        String _title = c.getString(c
                                .getColumnIndex(TITLE));
                        String _directorTitle = c.getString(c.getColumnIndex(DIRECTOR_TITLE));
                        String _photographerTitle = c.getString(c.getColumnIndex(PHOTOGRAPHER_TITLE));
                        String _initialDate = c.getString(c.getColumnIndex(INITIAL_DATE));
                        String _scriptFile = c.getString(c.getColumnIndex(SCRIPT_FILE));
                        int _ID = c.getInt(c.getColumnIndex(ID));

                        _obj.setID(_ID);
                        _obj.setTitle(_title);
                        _obj.setDirectorTitle(_directorTitle);
                        _obj.setPhotographerTitle(_photographerTitle);
                        _obj.setInitialDate(_initialDate);
                        _obj.setScriptFile(_scriptFile);

                    } while (c.moveToNext());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
            this.close();
        }
        return _obj;
    }

    public void updateScriptPath(int film_id, String scriptFile) {
        SQLiteDatabase db = this.openDataBase();
        try {
            String _query = "UPDATE " + TABLE + " SET " + SCRIPT_FILE + " = '" + scriptFile + "' WHERE " + ID + " = " + film_id + "";

//            String query = "UPDATE " + TABLE + " SET " + TITLE + " = '" + title + "'+ DIRECTOR_TITLE +  = '" + directorTitle + PHOTOGRAPHER_TITLE + " = '" + photographerTitle +
//                    INITIAL_DATE + " = '" + initialDate + SCRIPT_FILE + " = '" + scriptFile + "' WHERE " + ID + " = " + film_id + "";
            db.execSQL(_query);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
            this.close();
        }
    }

    public void updateFilm(int film_id, String title, String directorTitle, String photographerTitle, String initialDate,
                           String scriptFile) {
        SQLiteDatabase db = this.openDataBase();
        try {
            String _query = "UPDATE " + TABLE + " SET " + TITLE + " = '" + title + "' ," +
                    DIRECTOR_TITLE + " = '" + directorTitle + "' ," +
                    PHOTOGRAPHER_TITLE + " = '" + photographerTitle + "' ," +
                    INITIAL_DATE + " = '" + initialDate + "' ," +
                    SCRIPT_FILE + " = '" + scriptFile + "' WHERE " + ID + " = " + film_id + "";

//            String query = "UPDATE " + TABLE + " SET " + TITLE + " = '" + title + "'+ DIRECTOR_TITLE +  = '" + directorTitle + PHOTOGRAPHER_TITLE + " = '" + photographerTitle +
//                    INITIAL_DATE + " = '" + initialDate + SCRIPT_FILE + " = '" + scriptFile + "' WHERE " + ID + " = " + film_id + "";
            db.execSQL(_query);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
            this.close();
        }
    }


    public void deleteFilm(int _id) {
        SQLiteDatabase db = this.openDataBase();
        try {
            String query = "DELETE FROM " + TABLE + " WHERE "
                    + ID + " = " + _id + "";
            db.execSQL(query);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
            this.close();
        }

    }*/
}

