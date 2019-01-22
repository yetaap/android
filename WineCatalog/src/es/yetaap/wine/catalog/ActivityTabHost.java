package es.yetaap.wine.catalog;

import android.app.TabActivity; 
import android.content.Intent; 
import android.content.pm.PackageManager;
import android.os.Bundle; 
import android.view.Window;
import android.widget.TabHost; 
import android.widget.TextView;
import android.widget.TabHost.TabSpec; 

public class ActivityTabHost extends TabActivity 
{
    @Override
    public void onCreate(Bundle _savedInstanceState) 
    { 
        super.onCreate(_savedInstanceState); 
		requestWindowFeature(Window.FEATURE_NO_TITLE);       
        setContentView(R.layout.layout_tabhost); 
 		
        TabHost tabHost = getTabHost();
		if(tabHost != null)
		{
			tabHost.setup();
	        setTextType(tabHost);
			// Tab for Data 
			TabSpec dataspec = tabHost.newTabSpec(getString(R.string.Data)); 
			// setting Title and Icon for the Tab 
			dataspec.setIndicator(getResources().getString(R.string.Data), getResources().getDrawable(R.drawable.datos));

			Intent dataIntent = new Intent(this, ActivityWineData.class);
			// Si hemos recibido un intent con datos, es para hacer un update de un vino, se los pasamos a la siguiente activity
			Bundle bundle = getIntent().getExtras();
			if(bundle != null)
				dataIntent.putExtras(bundle);
			dataspec.setContent(dataIntent); 
			tabHost.addTab(dataspec); // Adding data tab 
			
			// Tab for Label 
			if(checkCameraHardware())
			{
				TabSpec photospec = tabHost.newTabSpec(getString(R.string.Photo)); 
				// setting Title and Icon for the Tab 
				photospec.setIndicator(getResources().getString(R.string.Photo), getResources().getDrawable(R.drawable.label)); 
				Intent photosIntent = new Intent(this, ActivityCamera.class); 
				photospec.setContent(photosIntent); 
				tabHost.addTab(photospec); // Adding photos tab 
			}
			
			((AppGlobalContext)getApplicationContext()).setTabActivity(tabHost);
		}
    }
	
    private void setTextType(TabHost _tabhost)
    {
    	if(_tabhost != null)
    	{ 
	    	for(int i=0;i<_tabhost.getTabWidget().getChildCount();i++)  
	    	{ 
	    		TextView tv = (TextView) _tabhost.getTabWidget().getChildAt(i).findViewById(android.R.id.title); 
	    		tv.setTypeface(((AppGlobalContext)getApplicationContext()).getFontLight()); 
	    	}
    	}
    }

	private boolean checkCameraHardware() 
	{
		if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))
		{
			// hay camara
			return true;
		} 
		else 
		{
			// no hay camara
			return false;
		}
	}
}
