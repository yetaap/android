package es.yetaap.wine.catalog;

import java.util.ArrayList;

import android.app.Application;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.widget.TabHost;

public class AppGlobalContext extends Application 
{ 
	public enum Error
	{
		ERROR_OK, ERROR_XML, ERROR_FICHERO;
	};

	public static final int UNDEFINED = -1;
	private static final int BBDD_VERSION = 1;
    private static int REL_SWIPE_MIN_DISTANCE = -1;  
    private static int REL_SWIPE_MAX_OFF_PATH = -1; 
    private static int REL_SWIPE_THRESHOLD_VELOCITY = -1; 

	private static Typeface m_RobotoCondensed = null;
	private static Typeface m_RobotoCondensedBold = null;
	private static Typeface m_RobotoLight = null;
	
	private SDManager m_SdManager = null;
	private TabHost m_Tab = null;
	private BBDDConnection m_BBDD = null;
	private ActivityWineData m_WineDataActivity = null;
	private ArrayList<WineElement> m_ArrWines = null;
	private int m_iDensity = DisplayMetrics.DENSITY_DEFAULT;

	public AppGlobalContext()
	{
		super();
	}

    public void CalculateSizes(DisplayMetrics _dm)
    {
	    REL_SWIPE_MIN_DISTANCE = (int)(120.0f * _dm.densityDpi / 160.0f + 0.5);  
	    REL_SWIPE_MAX_OFF_PATH = (int)(250.0f * _dm.densityDpi / 160.0f + 0.5); 
	    REL_SWIPE_THRESHOLD_VELOCITY = (int)(200.0f * _dm.densityDpi / 160.0f + 0.5); 
    }	

	public void Initialize()
	{
		m_SdManager = new SDManager();
		m_SdManager.InitializeWorkingDirectory();
		m_BBDD = new BBDDConnection(this,BBDD_VERSION);
		m_ArrWines = new ArrayList<WineElement>();
		m_RobotoCondensed = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Condensed.ttf"); 
		m_RobotoCondensedBold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-BoldCondensed.ttf");
		m_RobotoLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
	}
	
	public void Finalize()
	{
		m_ArrWines.clear();
		m_BBDD.Finalize();
		m_SdManager.Finalize();
	}
	
	public int getSwipeMinDistance()
	{
		return REL_SWIPE_MIN_DISTANCE;
	}
	
	public int getSwipeMaxOffPath()
	{
		return REL_SWIPE_MAX_OFF_PATH;
	}

	public int getSwipeThresholdVelocity()
	{
		return REL_SWIPE_THRESHOLD_VELOCITY;
	}
	
	public Typeface getFontCondensed()
	{
		return m_RobotoCondensed; 
	}

	public Typeface getFontCondensedBold()
	{
		return m_RobotoCondensedBold; 
	}

	public Typeface getFontLight()
	{
		return m_RobotoLight; 
	}
	
	public SDManager getSDManager()
	{ 
		return m_SdManager; 
	}  

	public BBDDConnection getBBDD()
	{
		return m_BBDD;
	}

	public ArrayList<WineElement> getArrWines()
	{
		return m_ArrWines;
	}

	public TabHost getTabActivity()
	{ 
		return m_Tab; 
	}  

	public ActivityWineData getActivityWineData()
	{
		return m_WineDataActivity;
	}
	
	public int getDensity()
	{
		return m_iDensity;
	}

	public void setTabActivity(TabHost tabHost)
	{ 
		m_Tab = tabHost; 
	}  

	public void setActivityWineData(ActivityWineData wda)
	{
		m_WineDataActivity = wda;
	}
	
	public void setDensity(int _density)
	{
		m_iDensity = _density;
	}
}
