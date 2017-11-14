package es.yetaap.wifispot5l;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

public class WifiSpotUpdater extends BroadcastReceiver 
{
	public static final String INTENT_UPDATE_WIDGET = "es.yetaap.wifispot";
	public static final String INTENT_UPDATE_WIDGET_ACTION = "es.yetaap.wifispot5l.UPDATE_WIDGET";
	public static final int ON = 1;
	public static final int OFF = 0;

	private static final String WIFI_AP_STATE_CHANGED_ACTION = "android.net.wifi.WIFI_AP_STATE_CHANGED";
	private static final String INTENT_EXTRA_WIFI_AP_STATE = "wifi_state";
	//private static final String EXTRA_PREVIOUS_WIFI_AP_STATE = "previous_wifi_state";

	
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		String action = intent.getAction();
		// Si el intent es de que el estado del AP ha cambiado
		if ((action != null) && (action.equals(WIFI_AP_STATE_CHANGED_ACTION))) 
		{
			// Si tiene informacion del estado
			if(intent.hasExtra(INTENT_EXTRA_WIFI_AP_STATE))
			{
				int status = intent.getExtras().getInt(INTENT_EXTRA_WIFI_AP_STATE, WifiSpotProvider.WIFI_AP_STATE_FAILED);

				if ((status == WifiSpotProvider.WIFI_AP_STATE_ENABLED) || (status == WifiManager.WIFI_STATE_ENABLED))
			    {
		        	Intent n = new Intent(context,WifiSpotProvider.class);
		        	n.setAction(INTENT_UPDATE_WIDGET_ACTION);
		        	n.putExtra(INTENT_UPDATE_WIDGET, true);
		        	context.sendBroadcast(n);
			    } 
			    else if ((status == WifiSpotProvider.WIFI_AP_STATE_DISABLED) || (status == WifiManager.WIFI_STATE_DISABLED))
			    {
		        	Intent n = new Intent(context,WifiSpotProvider.class);
		        	n.setAction(INTENT_UPDATE_WIDGET_ACTION);
		        	n.putExtra(INTENT_UPDATE_WIDGET, false);
		        	context.sendBroadcast(n);
			    }
		    }
	    }		  
	}
}
