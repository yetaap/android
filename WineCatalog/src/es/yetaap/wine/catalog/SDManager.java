package es.yetaap.wine.catalog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.util.Log;

public class SDManager 
{
	public enum Estado 
	{
		INCORRECTO, MONTADO, SOLO_LECTURA, MONTADO_DIRECTORY;
	};
	
	private String m_PathApp = "WineCatalog";
	private SDManager.Estado m_SdStatus = SDManager.Estado.INCORRECTO;	// Contiene el estado de la sdcard
	private File m_MainFolder = null;									// Contiene una referencia al directorio de la app

	////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//			METODOS PUBLICOS O INTERFAZ
	//
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	// Resetear el estado de la SDCard
	public void Finalize()
	{
		m_SdStatus = SDManager.Estado.INCORRECTO;
	}
	
	public String GetWorkingDir()
	{
		return m_MainFolder.getAbsolutePath();
	}
	// Inicializar el directorio de trabajo
	public SDManager.Estado InitializeWorkingDirectory()
	{
		// Chequeamos el estado de la sdcard
		ChechkDSStatus();
		
		// Si tenemos sdcard y esta montada para escribir
		if(m_SdStatus.equals(SDManager.Estado.MONTADO))
		{
			try
			{
				// Comprobamos si existe el directorio de la aplicacion
				m_MainFolder = new File(Environment.getExternalStorageDirectory(), m_PathApp);
				// Si no existe el directorio lo creamos
			    if (!m_MainFolder.exists()) 
			    { 
			        if (!m_MainFolder.mkdirs()) 
			        { 
			            Log.e("Problem creating App folder", ""); 
			        } 
			        else
			        	m_SdStatus = SDManager.Estado.MONTADO_DIRECTORY;
			    } 
			    else
			    	m_SdStatus = SDManager.Estado.MONTADO_DIRECTORY;
			}
			catch (NullPointerException ex)
			{
				Log.e("Error creating App folder :: ", ex.toString());
			}
			catch (Exception ex)
			{
				Log.e("Error creating App folder :: ", ex.toString());
			}				
		}
		return m_SdStatus;
	}
	
	@SuppressWarnings("resource")
	public InputStream getXmlAutoCompleteFileInput(Context _context, String _fileName)
	{
		InputStream fIn = null; 
		
		// Si no tenemos la sdcard y el directorio operativos no hacemos nada
		if(!m_SdStatus.equals(SDManager.Estado.MONTADO_DIRECTORY))
			return fIn; 
			
		try   
		{         		
			File file = new File(m_MainFolder.getAbsolutePath(), _fileName + ".xml"); 
			fIn = new FileInputStream(file);
		}   
		catch (FileNotFoundException ex)
		{
			try   
			{
				AssetManager assetManager = _context.getAssets(); 
				fIn = assetManager.open(_fileName + ".xml");
			}
			catch (IOException e)  
			{        
				throw new RuntimeException(e);  
			}
		}			
		
		return fIn;
	}
	
	public OutputStream getXmlAutoCompleteFileOutput(Context _context, String _fileName)
	{
		OutputStream fOut = null; 
		
		// Si no tenemos la sdcard y el directorio operativos no hacemos nada
		if(!m_SdStatus.equals(SDManager.Estado.MONTADO_DIRECTORY))
			return fOut; 
			
		try   
		{        
	        File file = new File(m_MainFolder.getAbsolutePath(),_fileName + ".xml"); 
	        fOut = new FileOutputStream(file); 
		}   
		catch (FileNotFoundException ex)
		{
			throw new RuntimeException(ex);  
		}			
		
		return fOut;
	}
	
	// Decodificar una imagen JPG desde un fichero
	public Bitmap DecodeJpgFile(String _pathFile)
	{
		// Si no tenemos la sdcard y el directorio operativos no hacemos nada
		if(!m_SdStatus.equals(SDManager.Estado.MONTADO_DIRECTORY))
			return null;
		
		String file = m_MainFolder.getAbsolutePath() + "/" + _pathFile;
		
		return BitmapFactory.decodeFile(file);
	}

	// Salvar una imagen JPG a fichero en la SDCard
	public String saveJpegPhoto(byte[] _data)
	{
		// Si no tenemos la sdcard y el directorio operativos no hacemos nada
		if(!m_SdStatus.equals(SDManager.Estado.MONTADO_DIRECTORY))
			return null;
	
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
		String date = dateFormat.format(new Date());
		String photoFile = date + ".jpg";

		File pictureFile = new File(m_MainFolder.getAbsolutePath(),photoFile); 

		try 
		{
			FileOutputStream fos = new FileOutputStream(pictureFile);
			fos.write(_data);
			fos.close();
		} 
		catch (Exception error) 
		{
			photoFile = null;
			Log.e("File not saved: ", photoFile);
		}
		
		return photoFile;
	}
	
	// Salvar una imagen BITMAP convirtiendola a JPG a fichero en la SDCard
	public String saveJpegFromBitmap(Bitmap _bm)
	{
		// Si no tenemos la sdcard y el directorio operativos no hacemos nada
		if(!m_SdStatus.equals(SDManager.Estado.MONTADO_DIRECTORY))
			return null;
	
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
		String date = dateFormat.format(new Date());
		String photoFile = date + ".jpg";

		File pictureFile = new File(m_MainFolder.getAbsolutePath(),photoFile); 

		try 
		{
			FileOutputStream fos = new FileOutputStream(pictureFile);
			_bm.compress(CompressFormat.JPEG, 80, fos);
			fos.close();
		} 
		catch (Exception error) 
		{
			photoFile = null;
			Log.e("File not saved: ", photoFile);
		}
		
		return photoFile;
	}
	
	// Borrar un fichero
	public void DeleteFile(String _file)
	{
		// Si no tenemos la sdcard y el directorio operativos no hacemos nada
		if(!m_SdStatus.equals(SDManager.Estado.MONTADO_DIRECTORY))
			return;
		
		if(_file != null)
		{
			File file = new File(m_MainFolder.getAbsolutePath() + "/" + _file); 
			file.delete();
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//			METODOS PRIVADOS
	//
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	// Comprobar el estado de la SDCard
	private void ChechkDSStatus()
	{
		//Comprobamos el estado de la memoria externa (tarjeta SD)
		String estado = Environment.getExternalStorageState();
		 
		if (estado.equals(Environment.MEDIA_MOUNTED))
		{
			m_SdStatus = SDManager.Estado.MONTADO;
		}
		else if (estado.equals(Environment.MEDIA_MOUNTED_READ_ONLY))
		{
			m_SdStatus = SDManager.Estado.SOLO_LECTURA;
		}
		else
		{
			m_SdStatus = SDManager.Estado.INCORRECTO;
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////
}
