package es.yetaap.wine.catalog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

public class ActivityCamera extends Activity implements SurfaceHolder.Callback, Camera.PreviewCallback, Camera.AutoFocusCallback 
{
	private Camera m_Camera = null;
    private SurfaceHolder m_Holder = null;
    private boolean m_PreviewRunning = false;
    private SurfaceView m_SurfaceView = null;
    private AppGlobalContext m_AppContext = null;
    private boolean m_bClick = false;

    
	////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//			EVENTOS DE LA VENTANA
	//
	////////////////////////////////////////////////////////////////////////////////////////////////
    
    @Override
    protected void onCreate(Bundle _savedInstanceState) 
	{
        super.onCreate(_savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        m_AppContext = (AppGlobalContext)getApplicationContext();

        // Ponemos la ventana traslucida, sin marco y ocupando toda la pantalla
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
		// Eliminar el titulo de la ventana
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Ponemos el layout de la camara
        setContentView(R.layout.layout_camera); 

		// Obtenemos el SurfaceView donde hacer el preview de la camara
        m_SurfaceView = (SurfaceView) findViewById(R.id.surfaceViewCamera); 
		
		if(m_SurfaceView != null)
		{
			// Registramos callbacks para cuando el SurfaceView/Holder sea creado, destruido y redimensionado
			m_Holder = m_SurfaceView.getHolder();
			if(m_Holder != null)
			{
				m_Holder.addCallback(this);
				m_Holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			}
		}
		
		TextView textView = (TextView) findViewById(R.id.textViewCamera);
		textView.setTypeface(((AppGlobalContext)getApplicationContext()).getFontCondensedBold());
    }
    
    @Override
    protected void onResume()
    {
    	super.onResume();
    	
        // Ocultar el teclado siempre en esta ventana
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(m_AppContext.getActivityWineData().getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);        
        imm.hideSoftInputFromWindow(m_AppContext.getActivityWineData().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        if (null != m_Camera) 
		{
        	if (m_PreviewRunning == false) 
        	{
    			m_Camera.startPreview();
    			m_Camera.autoFocus(this);
    			m_PreviewRunning = true;
        	}
		}
    }

    @Override
    protected void onPause()
    {
    	super.onPause();
        if (null != m_Camera) 
		{
        	if (m_PreviewRunning) 
        	{
        		m_Camera.cancelAutoFocus();
        		m_Camera.stopPreview();
        		m_PreviewRunning = false;
        	}
		}    	
    }
    
    // Se crea la SurfaceView para la preview
    public void surfaceCreated(SurfaceHolder _holder) 
	{
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
    	// Abrimos la camara, por defecto es la "first rear facing camera"
    	m_Camera = Camera.open();
    	
        try 
		{
            if (null != m_Camera) 
			{
                m_Camera.setPreviewDisplay(_holder);
                Camera.Parameters parameters = m_Camera.getParameters();
                if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) 
                {
                  parameters.set("orientation", "portrait");
                  m_Camera.setDisplayOrientation(90);
                }
                
				String focusMode = findSettableValue(parameters.getSupportedFocusModes(),
						 Camera.Parameters.FOCUS_MODE_AUTO,
						 Camera.Parameters.FOCUS_MODE_MACRO,
						 Camera.Parameters.FOCUS_MODE_INFINITY);     
				if (focusMode != null) 
				{       
					parameters.setFocusMode(focusMode);     
				}  					
                
                m_Camera.setParameters(parameters);
            }
        } 
		catch (IOException exception) 
		{
            Log.e("IOException caused by setParameters() :: ", exception.toString());
        }
	}

    // Se destruye la SurfaceView para la preview
    public void surfaceDestroyed(SurfaceHolder _holder) 
	{
        // Surface will be destroyed when we return, so stop the preview.
        if (null != m_Camera) 
		{
        	if (m_PreviewRunning) 
        	{
        		m_Camera.cancelAutoFocus();
        		m_Camera.stopPreview();
        	}
			m_Camera.release();
			m_PreviewRunning = false;
	        m_Camera = null;       
        }
    }

    // Se modifica la SurfaceView para la preview
    public void surfaceChanged(SurfaceHolder _holder, int _format, int _w, int _h) 
	{
    	if (m_PreviewRunning) 
    	{
			if (null != m_Camera) 
			{
				m_Camera.cancelAutoFocus();
				m_Camera.stopPreview();
			}
			m_PreviewRunning = false;
		}
    	
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
		if(null != m_Camera)
		{
			try
			{
				Camera.Parameters parameters = m_Camera.getParameters();
				if(parameters != null)
				{
					Camera.Size size = getBestPreviewSize(_w, _h, parameters);

					if (null != size) 
						parameters.setPreviewSize(size.width, size.height);
					else
						parameters.setPreviewSize(m_SurfaceView.getWidth(),m_SurfaceView.getHeight());
						
					m_Camera.setParameters(parameters);
				}
			}
			catch(RuntimeException ex)
			{
				Log.e("RuntimeException caused by setParameters() :: ", ex.toString());
			}
			
			// Comenzamos la preview		
			m_Camera.startPreview();
			m_Camera.autoFocus(this);
			m_PreviewRunning = true;
		}
    }
    
	////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//			METODOS AUXILIARES DE LA CLASE
	//
	////////////////////////////////////////////////////////////////////////////////////////////////

	private Camera.Size getBestPreviewSize(int _width, int _height, Camera.Parameters _parameters) 
	{
		Camera.Size result = null;
    
		for (Camera.Size size : _parameters.getSupportedPreviewSizes()) 
		{
			if (size.width <= _width && size.height <= _height) 
			{
				if (result==null) 
				{
					result=size;
				}
				else 
				{
					int resultArea = result.width*result.height;
					int newArea = size.width*size.height;
          
					if (newArea > resultArea) 
					{
						result=size;
					}
				}
			}
		}
    
		return(result);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void onPreviewFrame(byte[] _data, Camera _camera) 
	{
		if(null != _data)
		{
			// Vamos a decodificar la imagen para guardar el vino
			try 
			{
				Camera.Parameters parameters = _camera.getParameters();
				int imageFormat = parameters.getPreviewFormat();
				Bitmap bitmapOrg = null;
				
				if ( imageFormat == ImageFormat.NV21 )
				{						
					// Convert to JPG 
					Size previewSize = parameters.getPreviewSize();  
					YuvImage yuvimage = new YuvImage(_data, ImageFormat.NV21, previewSize.width, previewSize.height, null); 
					ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); 
					yuvimage.compressToJpeg(new Rect(0,0,previewSize.width, previewSize.height), 80, outputStream); 
					// Convert to Bitmap 
					bitmapOrg = BitmapFactory.decodeByteArray(outputStream.toByteArray(), 0, outputStream.size());
				}
				else if ( imageFormat == ImageFormat.JPEG || imageFormat == ImageFormat.RGB_565 )
				{
					// Convert to Bitmap 
					bitmapOrg = BitmapFactory.decodeByteArray( _data, 0, _data.length );
				}
				else if(imageFormat == ImageFormat.NV16)
				{
					Size previewSize = m_Camera.getParameters().getPreviewSize(); 
					int[] rgb = decodeYUV420SP(_data,previewSize.width,previewSize.height);
					// Convert to Bitmap 
					bitmapOrg = Bitmap.createBitmap(rgb, previewSize.width,previewSize.height,Bitmap.Config.ARGB_8888);
				}
				else
				{
					((AppGlobalContext)getApplicationContext()).getTabActivity().setCurrentTab(0);
				}
				
				if(null != bitmapOrg)
				{
					// Rotar la imagen 90 grados
					Matrix matrix = new Matrix(); 
					matrix.postRotate(90); 
					Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, 
							bitmapOrg.getWidth(), bitmapOrg.getHeight(), matrix, true); 
					// Salvamos la imagen y volvemos al tab de datos
			        ((AppGlobalContext)getApplicationContext()).getActivityWineData().setImage(resizedBitmap);
					((AppGlobalContext)getApplicationContext()).getTabActivity().setCurrentTab(0);
				}
			} 
			catch (Exception e) 
			{
				Log.e("onPreviewFrame exception :: ", e.toString());
			}
			finally 
			{
			}
		}
	}   
	
	private int[] decodeYUV420SP(byte[] yuv420sp, int width, int height) 
	{
		final int frameSize = width * height;
		int rgb[]=new int[width*height];  

		for (int j = 0, yp = 0; j < height; j++) 
		{
			int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
			for (int i = 0; i < width; i++, yp++) 
			{
				int y = (0xff & ((int) yuv420sp[yp])) - 16;
				if (y < 0)
					y = 0;
				if ((i & 1) == 0) 
				{
					v = (0xff & yuv420sp[uvp++]) - 128;
					u = (0xff & yuv420sp[uvp++]) - 128;
				}

				int y1192 = 1192 * y;
				int r = (y1192 + 1634 * v);
				int g = (y1192 - 833 * v - 400 * u);
				int b = (y1192 + 2066 * u);

				if (r < 0)
					r = 0;
				else if (r > 262143)
					r = 262143;
				if (g < 0)
					g = 0;
				else if (g > 262143)
					g = 262143;
				if (b < 0)
					b = 0;
				else if (b > 262143)
					b = 262143;

				rgb[yp] = (0xff000000 | ((r << 6) & 0xff0000)
						| ((g >> 2) & 0xff00) | ((b >> 10) & 0xff));
			}
		}
		return rgb;
	}
	
	public void onClickSurface(View _arg0)
	{
		m_Camera.autoFocus(this);
		m_bClick = true;
	}

	public void onAutoFocus(boolean arg0, Camera arg1) 
	{
		if(m_bClick == true)
		{
			m_Camera.setOneShotPreviewCallback(ActivityCamera.this);
			Vibrate.vibrar(getApplicationContext(), Vibrate.CORTO);
			m_bClick = false;
		}
	}
	
	private static String findSettableValue(Collection<String> supportedValues,                                           
            String... desiredValues) 
	{     
		String result = null;     
		if (supportedValues != null) 
		{       
			for (String desiredValue : desiredValues) 
			{   	      
				if (supportedValues.contains(desiredValue)) 
				{           
					result = desiredValue;           
					break;         
				}       
			}     
		}     

		return result;   
	} 	
	
}
