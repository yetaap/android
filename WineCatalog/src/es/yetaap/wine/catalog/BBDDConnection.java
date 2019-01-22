package es.yetaap.wine.catalog;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BBDDConnection extends SQLiteOpenHelper 
{
    //Sentencia SQL para crear la tabla de Wines
    private static final String m_Table = "Wines";	
    private static final String m_SqlCreate = "CREATE TABLE " + m_Table + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
    		                                         "wineName TEXT, " +
    		                                         "wineProducer TEXT, " + 
    		                                         "wineRegion TEXT, " + 
    		                                         "harvestYear INTEGER, " + 
    		                                         "wineAge TEXT, " +
    		                                         "wineKind TEXT, " +
    		                                         "wineComment TEXT, " +
    		                                         "winePhoto TEXT, " +
    		                                         "wineAlcohol float(24), " +
    		                                         "wineRating float(24), " +
													 "finder TEXT)";
    private static final String m_SqlQueryReadAll = "SELECT * FROM " + m_Table;
    private static final String m_BBDDName = "WinesCatalog.bd";
 
	////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//			METODOS PUBLICOS O INTERFAZ
	//
	////////////////////////////////////////////////////////////////////////////////////////////////

	// Constructor
    public BBDDConnection(Context _contexto, int _version) 
	{
        super(_contexto, ((AppGlobalContext)_contexto).getSDManager().GetWorkingDir() + "/" + m_BBDDName, null, _version);
    }
 
    @Override
    public void onCreate(SQLiteDatabase _db) 
	{
        //Se ejecuta la sentencia SQL de creación de la tabla
        _db.execSQL(m_SqlCreate);
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase _db, int _versionAnterior, int _versionNueva) 
	{
        //NOTA: Por simplicidad del ejemplo aquí utilizamos directamente la opción de
        //      eliminar la tabla anterior y crearla de nuevo vacía con el nuevo formato.
        //      Sin embargo lo normal será que haya que migrar datos de la tabla antigua
        //      a la nueva, por lo que este método debería ser más elaborado.
 
        //Se elimina la versión anterior de la tabla
        _db.execSQL("DROP TABLE IF EXISTS " + m_Table);
 
        //Se crea la nueva versión de la tabla
        _db.execSQL(m_SqlCreate);
    }
	
    public void Finalize()
    {
    	// Cerramos la base de datos que estaba cacheada
    	SQLiteDatabase db = getWritableDatabase();
		if(db != null)
			db.close();
    }
    
	// Borrar un Vino
	public synchronized long DeleteWine(long _Id)
	{
		SQLiteDatabase db = null;
		try
		{
	        db = getWritableDatabase();
	 
	        //Si hemos abierto correctamente la base de datos
	        if(null != db)
	        {
	            //Actualizamos los datos en la tabla Wines
				String[] whereArgs = new String[]{String.valueOf(_Id)}; 
				db.delete(m_Table, "id = ?", whereArgs);
				db.close();
	        }
		}
		catch(SQLiteException ex)
		{
			if(db != null)
				db.close();
			Log.e("Error deleting item BBDD :: ", ex.toString());
			return AppGlobalContext.UNDEFINED;
		}
		
		return _Id;	
	}
	
	// Añadir un vino a la BBDD
	public synchronized long AddNewWine(WineElement _element)
	{
		long result = AppGlobalContext.UNDEFINED;
		SQLiteDatabase db = null;
		try
		{
	        db = getWritableDatabase();
	 
	        //Si hemos abierto correctamente la base de datos
	        if(null != db)
	        {
	            //Insertamos los datos en la tabla QRCodes
				//Creamos el registro a insertar como objeto ContentValues
				ContentValues nuevoRegistro = new ContentValues();
				nuevoRegistro.put("wineName", _element.getWineName());
				nuevoRegistro.put("wineProducer", _element.getWineProducer());
				nuevoRegistro.put("wineRegion", _element.getWineRegion());
				nuevoRegistro.put("harvestYear", _element.getHarvestYear());
				nuevoRegistro.put("wineAge", _element.getWineMaduration());
				nuevoRegistro.put("wineKind", _element.getWineType());
				nuevoRegistro.put("wineComment", _element.getWineComment());
				nuevoRegistro.put("winePhoto", _element.getWinePhoto());
				nuevoRegistro.put("wineAlcohol", _element.getWineAlcohol());
				nuevoRegistro.put("wineRating", _element.getWineRating());
				nuevoRegistro.put("finder", _element.getWineName() + " " +
											_element.getWineProducer() + " " +
											_element.getWineRegion() + " " +
											_element.getHarvestYear() + " " +
											_element.getWineMaduration() + " " +
											_element.getWineType() + " " +
											_element.getWineComment() + " " +
											_element.getWineAlcohol() + " " +
											_element.getWineRating());
							
				//Insertamos el registro en la base de datos
				result = db.insert(m_Table, null, nuevoRegistro);
				db.close();
	        }
		}
		catch(SQLiteException ex)
		{
			if(db != null)
				db.close();
			
			Log.e("Error adding item BBDD :: ", ex.toString());
			return AppGlobalContext.UNDEFINED;
		}
		
		return result;
    }
	
	// Cargar todas los Vinos existentes en la BBDD
	public synchronized int ReadAllData(ArrayList<WineElement> _arrWines)
	{	
		SQLiteDatabase db = null;
		try
		{
	        db = getReadableDatabase();
	        _arrWines.clear();
	        
	        //Si hemos abierto correctamente la base de datos
	        if(db != null)
	        {
				Cursor c = db.rawQuery(m_SqlQueryReadAll,null);
				//Nos aseguramos de que existe al menos un registro
				if (c.moveToFirst()) 
				{
					//Recorremos el cursor hasta que no haya más registros
					do 
					{		           
						_arrWines.add(
								new WineElement(
										Long.parseLong(c.getString(0).trim()), // _Id
										c.getString(1), // _wineName
										c.getString(2), // _wineProducer
										c.getString(3), // _wineRegion
										Integer.parseInt(c.getString(4)), // _harvestYear
										c.getString(5), // _wineAge
										c.getString(6), // _wineKind
										c.getString(7), // _wineComment
										c.getString(8), // _winePhoto
										Float.parseFloat(c.getString(9)), // _wineAlcohol
										Float.parseFloat(c.getString(10)) // _wineRating
										));
					} while(c.moveToNext());
				}
				c.close();
				db.close();
			}
		}
		catch(SQLiteException ex)
		{
			if(db != null)
				db.close();
			
			Log.e("Error reading all items BBDD :: ", ex.toString());
			return AppGlobalContext.UNDEFINED;
		}

		return _arrWines.size();
	}

	// Consultar por un vino en particular 
	public synchronized int QueryWines(String _sFinder, ArrayList<WineElement> _arrWines)
	{	
		SQLiteDatabase db = null;
		try
		{
	        db = getReadableDatabase();
	        _arrWines.clear();
	        
	        //Si hemos abierto correctamente la base de datos
	        if(db != null)
	        {
				Cursor c = db.query(m_Table, new String[] {"*"}, 
					"finder like '%" + _sFinder + "%'", null, null, null, null);
					
				//Nos aseguramos de que existe al menos un registro
				if (c.moveToFirst()) 
				{
					//Recorremos el cursor hasta que no haya más registros
					do 
					{		           
						_arrWines.add(
								new WineElement(
										Long.parseLong(c.getString(0).trim()), // _Id
										c.getString(1), // _wineName
										c.getString(2), // _wineProducer
										c.getString(3), // _wineRegion
										Integer.parseInt(c.getString(4)), // _harvestYear
										c.getString(5), // _wineAge
										c.getString(6), // _wineKind
										c.getString(7), // _wineComment
										c.getString(8), // _winePhoto
										Float.parseFloat(c.getString(9)), // _wineAlcohol
										Float.parseFloat(c.getString(10)) // _wineRating
										));
					} while(c.moveToNext());
				}
				c.close();
				db.close();
			}
		}
		catch(SQLiteException ex)
		{
			if(db != null)
				db.close();
			
			Log.e("Error quering item BBDD :: ", ex.toString());
			return AppGlobalContext.UNDEFINED;
		}

		return _arrWines.size();
	}
	
	// Actualizar un Wine en la BBDD
	public synchronized long UpdateWine(long _Id, WineElement _element)
	{
		SQLiteDatabase db = null;
		try
		{
	        db = getWritableDatabase();
	 
	        //Si hemos abierto correctamente la base de datos
	        if(null != db)
	        {
	            //Actualizamos los datos en la tabla QRCodes
				ContentValues updateRegistro = new ContentValues();
				String[] whereArgs = new String[]{String.valueOf(_Id)}; 
				
				updateRegistro.put("wineName", _element.getWineName());
				updateRegistro.put("wineProducer", _element.getWineProducer());
				updateRegistro.put("wineRegion", _element.getWineRegion());
				updateRegistro.put("harvestYear", _element.getHarvestYear());
				updateRegistro.put("wineAge", _element.getWineMaduration());
				updateRegistro.put("wineKind", _element.getWineType());
				updateRegistro.put("wineComment", _element.getWineComment());
				updateRegistro.put("winePhoto", _element.getWinePhoto());
				updateRegistro.put("wineAlcohol", _element.getWineAlcohol());
				updateRegistro.put("wineRating", _element.getWineRating());
				updateRegistro.put("finder", _element.getWineName() + " " +
											_element.getWineProducer() + " " +
											_element.getWineRegion() + " " +
											_element.getHarvestYear() + " " +
											_element.getWineMaduration() + " " +
											_element.getWineType() + " " +
											_element.getWineComment() + " " +
											_element.getWineAlcohol() + " " +
											_element.getWineRating());
				db.update(m_Table, updateRegistro, "id = ?", whereArgs);
				db.close();
	        }
	  	}
		catch(SQLiteException ex)
		{
			if(db != null)
				db.close();
			
			Log.e("Error updating item BBDD :: ", ex.toString());
			return AppGlobalContext.UNDEFINED;
		}
		
		return _Id;	
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////
}
