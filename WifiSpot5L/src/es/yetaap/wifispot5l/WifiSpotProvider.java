package es.yetaap.wifispot5l;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class WifiSpotProvider extends AppWidgetProvider
{
	// On ZTE Skate 2.3.7 CyanogenMod7 the status value is the WIFI_STATE instead WIFI_AP_STATE
	public static final int WIFI_AP_STATE_DISABLING = 10;
	public static final int WIFI_AP_STATE_DISABLED = 11;
	public static final int WIFI_AP_STATE_ENABLING = 12;
	public static final int WIFI_AP_STATE_ENABLED = 13;
	public static final int WIFI_AP_STATE_FAILED = 14;
	
	private static final String LOG_TAG = "WifiSpot";
	
	private static final String PREFS_EXTRA_AP_STATE_TO_REFRESH = "apStateRefresh";
	private static final String PREFS_EXTRA_SAVED_WIFI_STATE = "wifiSavedState";
	private static final String WIFIMANGER_HIDE_METHOD_SET_AP_ENABLED = "setWifiApEnabled";
	private static final String WIFIMANGER_HIDE_METHOD_GET_AP_STATE = "getWifiApState";

    @Override
    public void onUpdate( Context _context, AppWidgetManager _appWidgetManager, int[] _appWidgetIds )
    {
    	RemoteViews remoteViews = new RemoteViews( _context.getPackageName(), R.layout.activity_main );
    	ComponentName watchWidget = new ComponentName( _context, WifiSpotProvider.class );

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(_context);
		if (prefs != null) 
		{
			Boolean status = prefs.getBoolean(PREFS_EXTRA_AP_STATE_TO_REFRESH, false);
			Log.d(LOG_TAG,"onUpdate status " + status);
			if (status != null) 
			{
				refreshWidget(_context, status);
			}
		}
    	
        refreshPendingIntent(_context, _appWidgetManager, _appWidgetIds, remoteViews, watchWidget);
    }
    
    @Override
    public void onReceive(Context _context, Intent _intent) 
    {
        String action = _intent.getAction();
        Log.d(LOG_TAG,"onReceive action " + action);
        
    	// Intent por click del usuario en el widget
        if (action == null) 
        {
			Log.d(LOG_TAG,"onReceive cambiamos estado");
			configApState(_context);
        }
    	// Intent de actualizar el estado desde el BroadcastReceiver
        else if (action.contentEquals(WifiSpotUpdater.INTENT_UPDATE_WIDGET_ACTION))
        {
        	// Intent desde cambio en el estado del Wifi AP
        	if(_intent.hasExtra(WifiSpotUpdater.INTENT_UPDATE_WIDGET))
            {
        		Log.d(LOG_TAG,"onReceive actualizamos imagen");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(_context);
                if(prefs != null)
                {
                    SharedPreferences.Editor editor = prefs.edit();
                    if(editor != null) 
                    {
                        editor.putBoolean(PREFS_EXTRA_AP_STATE_TO_REFRESH, _intent.getBooleanExtra(WifiSpotUpdater.INTENT_UPDATE_WIDGET,false));
                        editor.commit();
                    }
                }
        		
        		AppWidgetManager gm = AppWidgetManager.getInstance(_context);
        		int[] ids = gm.getAppWidgetIds(new ComponentName(_context, WifiSpotProvider.class));
        		this.onUpdate(_context, gm, ids);            
            }
        	// Intent desde el boot completed
        	else
        	{
        		Log.d(LOG_TAG,"onReceive evento de boot completed");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(_context);
                if(prefs != null)
                {
                    SharedPreferences.Editor editor = prefs.edit();
                    if(editor != null) 
                    {
                        editor.putBoolean(PREFS_EXTRA_AP_STATE_TO_REFRESH, isApOn(_context));
                        editor.commit();
                    }
                }
        		
        		AppWidgetManager gm = AppWidgetManager.getInstance(_context);
        		int[] ids = gm.getAppWidgetIds(new ComponentName(_context, WifiSpotProvider.class));
        		this.onUpdate(_context, gm, ids);            
        	}
        }
        else if (action.contentEquals(AppWidgetManager.ACTION_APPWIDGET_UPDATE))
        {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(_context);
            if(prefs != null)
            {
                SharedPreferences.Editor editor = prefs.edit();
                if(editor != null) 
                {
                    editor.putBoolean(PREFS_EXTRA_AP_STATE_TO_REFRESH, isApOn(_context));
                    editor.commit();
                }
            }
        }
        
        super.onReceive(_context, _intent);        
    }	

    private void refreshWidget(Context _context, boolean _bON)
    {
    	RemoteViews remoteViews = new RemoteViews( _context.getPackageName(), R.layout.activity_main );
    	ComponentName watchWidget = new ComponentName( _context, WifiSpotProvider.class );
        
        if(_bON)
        {
        	Log.d(LOG_TAG,"UpdateWidget ON");
            remoteViews.setImageViewResource(R.id.imageView1, R.drawable.on);
        }
        else 
        {
        	Log.d(LOG_TAG,"UpdateWidget OFF");
            remoteViews.setImageViewResource(R.id.imageView1, R.drawable.off);
        }

        (AppWidgetManager.getInstance(_context)).updateAppWidget( watchWidget, remoteViews );
    }

    private void refreshPendingIntent(Context _context, AppWidgetManager _appWidgetManager, int[] _appWidgetIds, 
    								  RemoteViews _remoteViews, ComponentName _watchWidget)
    {
        for (int i = 0; i < _appWidgetIds.length; i++) 
    	{
        	Intent intentClick = new Intent(_context,WifiSpotProvider.class);
        	intentClick.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, _appWidgetIds[0]);

        	PendingIntent pendingIntent = PendingIntent.getBroadcast(_context, _appWidgetIds[0],intentClick, 0);
        	_remoteViews.setOnClickPendingIntent(R.id.imageView1, pendingIntent);
        	_appWidgetManager.updateAppWidget( _watchWidget, _remoteViews );
    	}
    }
    
    private void refreshWidgetWorking(Context _context)
    {
    	RemoteViews remoteViews = new RemoteViews( _context.getPackageName(), R.layout.activity_main );
    	ComponentName watchWidget = new ComponentName( _context, WifiSpotProvider.class );
        
        remoteViews.setImageViewResource(R.id.imageView1, R.drawable.work);

        (AppWidgetManager.getInstance(_context)).updateAppWidget( watchWidget, remoteViews );
    }
    
	//turn on/off wifi hotspot as toggle
	private boolean configApState(final Context _context)
	{
	    WifiManager wifimanager = (WifiManager)_context.getSystemService(Context.WIFI_SERVICE);
	    WifiConfiguration wificonfiguration = null;
	    
	    if(wifimanager == null)
	    	return false;
	    
	    try 
	    {  
	        Method method = wifimanager.getClass().getMethod(WIFIMANGER_HIDE_METHOD_SET_AP_ENABLED, 
	        												 WifiConfiguration.class, boolean.class);
	        if(method != null)
	        {
	        	method.setAccessible(true);
	        	if(isApOn(_context)) 
	        	{
	        		method.invoke(wifimanager, wificonfiguration, false);
	        		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(_context);
	        		if (prefs != null) 
	        		{
	        			Integer status = prefs.getInt(PREFS_EXTRA_SAVED_WIFI_STATE, 0);
	        			if ((status != null) && ((status == WifiManager.WIFI_STATE_ENABLED) ||
	        									 (status == WifiManager.WIFI_STATE_ENABLING))) 
	        			{
	        				wifimanager.setWifiEnabled(true);
	        			}
	        		}
	        	}
	        	else
	        	{
	        		if(isWifiOn(_context))
	        		{
	        			//turn off whether wifi is on
	        			wifimanager.setWifiEnabled(false);
	        		}      
	        	
	        		method.invoke(wifimanager, wificonfiguration, true);
	        	}
		        
	        	refreshWidgetWorking(_context);	        	
	        }

	        return true;
	    } 
	    catch (NoSuchMethodException e) 
	    {
	        e.printStackTrace();
	    } 
	    catch (IllegalArgumentException e) 
	    {
	        e.printStackTrace();
	    } 
	    catch (IllegalAccessException e) 
	    {
	        e.printStackTrace();
	    } 
	    catch (InvocationTargetException e) 
	    {
	        e.printStackTrace();
	    }
	    return false;
	}
    
	private boolean isWifiOn(Context _context)
	{
		boolean bRet = false;
	    WifiManager wifimanager = (WifiManager)_context.getSystemService(Context.WIFI_SERVICE);
        int status = wifimanager.getWifiState();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(_context);
        if(prefs != null)
        {
            SharedPreferences.Editor editor = prefs.edit();
            if(editor != null) 
            {
                editor.putInt(PREFS_EXTRA_SAVED_WIFI_STATE, status);
                editor.commit();
            }
        }

	    if(status == WifiManager.WIFI_STATE_ENABLING || status == WifiManager.WIFI_STATE_ENABLED)
	    	bRet = true;

	    Log.d(LOG_TAG,"isWifiOn " + bRet);
	    return bRet;
	}
	
	private boolean isApOn(Context _context)
	{
		boolean bRet = false;
	    WifiManager wifimanager = (WifiManager)_context.getSystemService(Context.WIFI_SERVICE);
	    try
	    {
			Class<?> noparams[] = {};
			Method method = wifimanager.getClass().getMethod(WIFIMANGER_HIDE_METHOD_GET_AP_STATE, noparams);
	        method.setAccessible(true);
	        int status = ((Integer)(method.invoke(wifimanager,(Object[]) null))); 
	        switch(status)
	        {
	        	case WifiManager.WIFI_STATE_ENABLED:
	        	case WifiManager.WIFI_STATE_ENABLING:
	        	case WIFI_AP_STATE_ENABLED:
	        	case WIFI_AP_STATE_ENABLING:
	        		bRet = true;
	        	break;
	        	case WifiManager.WIFI_STATE_DISABLED:
	        	case WifiManager.WIFI_STATE_DISABLING:
	        	case WIFI_AP_STATE_DISABLED:
	        	case WIFI_AP_STATE_DISABLING:
	        		bRet = false;
	        	break;
	        }
	    }
	    catch (InvocationTargetException e) 
	    {
	        e.printStackTrace();
	    }
	    catch (Throwable ignored)
	    {
	    	ignored.printStackTrace();
	    }

        Log.d(LOG_TAG,"isApon " + bRet);	    
	    return bRet;
	}
}


// CODIGO PARA REALIZAR UNA TAREA CADA X TIEMPO HASTA PARAR CUANDO QUERAMOS

/*	final Handler handler = new Handler();
	handler.postDelayed(new Runnable() 
	{
		Timer t = new Timer();
		@Override
		public void run() 
		{
			//Set the schedule function and rate
			t.scheduleAtFixedRate(new TimerTask() 
			{
				int m_iTimes = 0;
			    @Override
			    public void run() 
			    {
	  	        	UpdateWidget(_context);
	  	        	if( (m_eAPWifiState == eWifiState.WIFI_STATE_ON) ||  (m_eAPWifiState == eWifiState.WIFI_STATE_OFF))
	  		        {
	  	        		m_iTimes++;
	  	        		if(m_iTimes == 3)
	  	        		{
	  	        			Log.d(LOG_TAG,"cancelo");
	  	        			t.cancel();
	  	        		}
	  		        }
			    }
			},
			//Set how long before to start calling the TimerTask (in milliseconds)
			(long)0,
			//Set the amount of time between each execution (in milliseconds)
			(long)500);
		  }
	}, 500);
*/      	
