package es.yetaap.wine.catalog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;

public class ActivitySplash extends Activity 
{
	private SplashReadAllWines m_Task = null; 
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		// Ponemos la ventana traslucida, sin marco y ocupando toda la pantalla
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_splash);

		// Inicializamos el contexto de aplicacion global
		((AppGlobalContext)getApplicationContext()).Initialize();
		
		// Nueva tarea de lectura de la base de datos
		m_Task = new SplashReadAllWines();
		m_Task.execute();
		
		DisplayMetrics metrics = new DisplayMetrics(); 
		getWindowManager().getDefaultDisplay().getMetrics(metrics); 
		 
		((AppGlobalContext)getApplicationContext()).setDensity(metrics.densityDpi);
	}
	
	public class SplashReadAllWines extends AsyncTask<Void, Void, Integer> 
	{
		private final ProgressDialog dialog = new ProgressDialog(ActivitySplash.this);	
		////////////////////////////////////////////////////////////////////////////////////////////////
		//
		//			Clase para realizar la consulta a la BBDD en un hilo
		//
		////////////////////////////////////////////////////////////////////////////////////////////////
		@Override
		protected void onPreExecute() 
		{
			this.dialog.setMessage(getResources().getString(R.string.splashProgress));
			this.dialog.setCancelable(false);
			this.dialog.show();
		}		
		
		@Override
		protected void onPostExecute(Integer _numWines) 
		{
			if (this.dialog.isShowing()) 
			{
				this.dialog.dismiss();		
			}
			
			finish();
			Intent intent = new Intent();
			intent.setClass(ActivitySplash.this,ActivityFinder.class);
			intent.putExtra("numWines", _numWines);
			startActivity(intent); 
			dialog.cancel();
			
			super.onPostExecute(_numWines);
		}

		@Override
		protected Integer doInBackground(Void... params) 
		{
			// Lanzamos la consulta
			return ((AppGlobalContext)getApplicationContext()).getBBDD().ReadAllData(((AppGlobalContext)getApplicationContext()).getArrWines());
		}

		////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////////////////
	}	
}