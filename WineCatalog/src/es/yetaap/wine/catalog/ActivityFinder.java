package es.yetaap.wine.catalog;

import android.app.Activity; 
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent; 
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle; 
import android.os.Parcelable;
import android.text.Editable; 
import android.text.TextWatcher; 
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View; 
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText; 
import android.widget.ListView; 
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;
 
public class ActivityFinder extends Activity
{ 
	private boolean m_Error = false;
	private AppGlobalContext m_AppContext = null;
	private EditText m_EditSearch = null; 
	private ListView m_Listview = null; 
	private FindWines m_Task = null;
	private int m_iSelectedRow = AdapterView.INVALID_POSITION;
	private AlertDialog.Builder m_Builder = null;
	private AlertDialog m_AlertDlg = null;
	private int m_iDeleteItem = AppGlobalContext.UNDEFINED;

	@Override 
	public void onCreate(Bundle savedInstanceState) 
	{ 
		super.onCreate(savedInstanceState); 
		requestWindowFeature(Window.FEATURE_NO_TITLE);	
		setContentView(R.layout.layout_findwine);		
		
		if(savedInstanceState != null)
		{
			// Lo del checker es necesario porque si no se encuentra el valor en el bundle
			// la funcion getInt devuelve 0 y eso coincidiria con el primer elemento de la lista 
			Boolean bCheck = savedInstanceState.getBoolean("es.yetaap.wine.catalog.finder.checker");
			if(bCheck)
				m_iSelectedRow = savedInstanceState.getInt("es.yetaap.wine.catalog.finder.selected");
		}
		
		// Guardamos referencias a los objetos mas usados
        m_AppContext = (AppGlobalContext)getApplicationContext();
		m_EditSearch = (EditText) findViewById(R.id.search); 
		m_Listview = (ListView) findViewById(R.id.listViewFindWine);

		if(m_AppContext == null || m_EditSearch == null || m_Listview == null)
			return;
		
		// Creamos el dialogo para informar de campos sin completar
		m_Builder = new AlertDialog.Builder(this);
		m_Builder.setCancelable(false);
		
		m_EditSearch.setTypeface(((AppGlobalContext)getApplicationContext()).getFontLight());
		m_EditSearch.addTextChangedListener(new TextWatcher() 
		{ 
			public void afterTextChanged(Editable s) 
			{ 
			} 
	 
			public void beforeTextChanged(CharSequence s, int start, int count, int after) 
			{ 
			} 
				 
			public void onTextChanged(CharSequence s, int start, int before, int count) 
			{ 
				// Nueva tarea de lectura de la base de datos
				if(start == 0 && before == 0 && count == 0 && s.length() == 0)
					return;
				m_Task = new FindWines();
				m_Task.execute(m_EditSearch.getText().toString());
			} 
		}); 
		
        // it's better to use density-aware measurements.  
        DisplayMetrics dm = getResources().getDisplayMetrics(); 
        m_AppContext.CalculateSizes(dm);
 
        final GestureDetector gestureDetector = new GestureDetector(new FinderGestureDetector()); 
        View.OnTouchListener gestureListener = new View.OnTouchListener() 
        { 
            public boolean onTouch(View v, MotionEvent event) 
            { 
                return gestureDetector.onTouchEvent(event);  
            }
		}; 
        m_Listview.setOnTouchListener(gestureListener); 
		
		InitializeActivityData();
	}
 
	protected void onSaveInstanceState(Bundle outState)
	{
		if((outState != null) && (m_iSelectedRow != AdapterView.INVALID_POSITION))
		{
			outState.putBoolean("es.yetaap.wine.catalog.finder.checker", true);
			outState.putInt("es.yetaap.wine.catalog.finder.selected", m_iSelectedRow);
		}
	}
	// Callback desde las ventanas de AddNewCard y ShowCard
	protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) 
	{  
		// Nueva tarea de lectura de la base de datos
		m_Task = new FindWines();
		m_Task.execute(m_EditSearch.getText().toString());
	} 
	
	public void onClickAddWine(View _arg0) 
	{
		LaunchAddNewActivity();
	}	 
	
	public void onSearchDelete(View _arg0)
	{
		if(m_EditSearch != null)
			m_EditSearch.setText("");
	}
	
	public void onClickEdit(View _arg0)
	{
		LaunchUpdateActivity(m_iSelectedRow);
	}

	public void onClickDelete(View _arg0)
	{
		DeleteWine(m_iSelectedRow);
	}
	
	public void onClickExitWine(View _arg0) 
	{
		// Finalizar la aplicación
		FinalizeActivityData();
		finish();
	}		

	private void InitializeActivityData()
	{
	  	// Recogemos los datos del intent que nos envian
   		Bundle bundle = getIntent().getExtras();

		if( bundle == null)
			return;
		
		// Recogemos el numero de elementos a mostrar
		int numElements = bundle.getInt("numWines");

		ConfigureListView();
		
        switch(numElements)
        {
        	// Primera ejecucion, no hay datos creados, se muestra el texto de añadir datos
        	case 0:
        	{
        		TextView empty = (TextView) findViewById(android.R.id.empty);
				if(empty != null)
				{
					empty.setText(R.string.noResultsAdd);
					empty.setGravity(Gravity.CENTER);
				}
        	}
        	break;
        	// Hubo algun error al leer los codigos
         	case AppGlobalContext.UNDEFINED:
         	{
         		TextView empty = (TextView)findViewById(android.R.id.empty);
				if(empty != null)
				{
					empty.setText(R.string.CriticalError);         		
					empty.setGravity(Gravity.CENTER);
				}
        		m_Error = true;
         	}
        	break;
        	// Existen codigos almacenados y se han podido leer correctamente
        	default:
        	break;
        }
	}

	private void ConfigureListView()
	{
        if(null != m_Listview)
        {
			AdapterList adapterList = new AdapterList(this,m_AppContext.getArrWines());
        	TextView empty = (TextView) findViewById(android.R.id.empty);
			if(empty != null)
			{
				empty.setText(R.string.noResults);
				empty.setGravity(Gravity.CENTER);
				m_Listview.setEmptyView(empty);
			}
        	
			m_Listview.setAdapter(adapterList);
        	m_Listview.setTextFilterEnabled(true);
    		m_Listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    		m_Listview.requestFocus();
    		
    		if(m_iSelectedRow != AdapterView.INVALID_POSITION)
    			((AdapterList)m_Listview.getAdapter()).setPosition(m_iSelectedRow);
    		
    		m_Listview.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener()
         	{  
 				public void onItemClick(AdapterView<?> _arg0, View _arg1, int _arg2, long _arg3) 
 				{
 					((AdapterList)m_Listview.getAdapter()).setPosition(_arg2);
 					m_iSelectedRow = _arg2;
 			 	}
         	});
    		m_Listview.setOnItemLongClickListener(new OnItemLongClickListener()
        	{
        		public boolean onItemLongClick(AdapterView<?> parent, View view,
        									   int position, long id) 
        		{
        			((AdapterList)m_Listview.getAdapter()).setPosition(position);
        			m_iSelectedRow = position;
        			LaunchShowActivity(m_iSelectedRow);
        			return true;
        		}
        	});
        }
	}
	
	public void onClickShow(View _arg0)
	{
		int position = m_Listview.getPositionForView(_arg0); 
		((AdapterList)m_Listview.getAdapter()).setPosition(position);
		m_iSelectedRow = position;
    	LaunchShowActivity(position);
	}
	
    // Click sobre el texto de añadir un nuevo elemento
	public void onClickSimpleLayout(View _arg0) 
	{  
		if(!m_Error)
		{
			LaunchAddNewActivity();
		}
	}	 
	
	private void LaunchShowActivity(int _posList)
	{
		if(m_Task != null )
			m_Task.cancel(true);
		
		if(_posList != AdapterView.INVALID_POSITION)
		{
			// Llamar a la Activity de mostrar vino
			WineElement element = m_AppContext.getArrWines().get(_posList);
			Intent intent = new Intent();
			intent.setClass(this,ActivityShowWine.class);
			intent.putExtra("es.yetaap.wine.catalog.wineElement", (Parcelable)element);
			startActivityForResult(intent,0);
		}
	}

	private void LaunchUpdateActivity(int _posList)
	{
		if(m_Task != null )
			m_Task.cancel(true);
		
		if(_posList != AdapterView.INVALID_POSITION)
		{
			// Llamar a la Activity de mostrar vino
			WineElement element = m_AppContext.getArrWines().get(_posList);
			Intent intent = new Intent();
			intent.setClass(this,ActivityTabHost.class);
			intent.putExtra("es.yetaap.wine.catalog.wineElement", (Parcelable)element);
			startActivityForResult(intent,0);
		}
	}
	
	private void LaunchAddNewActivity()
	{
		if(m_Task != null )
			m_Task.cancel(true);
		
		// Llamar a la Activity de añadir vino
		Intent intent = new Intent();
		intent.setClass(this,ActivityTabHost.class);
		startActivityForResult(intent,0); 
	}
	
	private void FinalizeActivityData()
	{
		// Finalizamos el contexto de aplicacion global
		((AppGlobalContext)getApplicationContext()).Finalize();
	}

	// Click en AlertDialog boton aceptar que se puede continuar
	OnClickListener okListener = new DialogInterface.OnClickListener() 
	{
		public void onClick(DialogInterface _arg0, int _arg1) 
		{
			if(m_iDeleteItem != AppGlobalContext.UNDEFINED)
			{
				WineElement element = m_AppContext.getArrWines().get(m_iDeleteItem);
				m_AppContext.getBBDD().DeleteWine(element.getId());
				m_AppContext.getSDManager().DeleteFile(element.getWinePhoto());
				m_iDeleteItem = AppGlobalContext.UNDEFINED;
				m_iSelectedRow = AppGlobalContext.UNDEFINED;
				
				// Nueva tarea de lectura de la base de datos
				m_Task = new FindWines();
				m_Task.execute(m_EditSearch.getText().toString());
			}
			_arg0.dismiss();
		}
	};
	
	// Click en AlertDialog boton cancelar
	OnClickListener negativeListener = new DialogInterface.OnClickListener() 
	{
		public void onClick(DialogInterface _arg0, int _arg1) 
		{
			m_iDeleteItem = AppGlobalContext.UNDEFINED;
			_arg0.dismiss();
		}
	};
	
	private void DeleteWine(int _posId)
	{
		if(_posId != AppGlobalContext.UNDEFINED)
		{
			m_iDeleteItem = _posId;
			m_Builder.setMessage(R.string.alertDelete);
			m_Builder.setPositiveButton(R.string.OK,okListener);
			m_Builder.setNegativeButton(R.string.NO,negativeListener);
			m_AlertDlg = m_Builder.create();
			m_AlertDlg.show();
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//			Clase para realizar la consulta a la BBDD en un hilo
	//
	////////////////////////////////////////////////////////////////////////////////////////////////

	public class FindWines extends AsyncTask<String, Void, Integer> 
	{
		@Override
		protected Integer doInBackground(String... _params)
		{
			if(isCancelled())
				return 0;
			
			// Lanzamos la consulta
			return m_AppContext.getBBDD().QueryWines(_params[0],m_AppContext.getArrWines());
		}

		@Override
		protected void onPostExecute(Integer _query) 
		{
			if(!isCancelled())
			{
				if(AppGlobalContext.UNDEFINED != _query)
				{
					// Tenemos datos nuevos que actualizar
					((AdapterList)m_Listview.getAdapter()).notifyData();
				}
			}
			
			super.onPostExecute(_query);
		}
	}
	////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////
	
    class FinderGestureDetector extends SimpleOnGestureListener
    {  
        @Override  
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) 
        {  
            if (Math.abs(e1.getY() - e2.getY()) > m_AppContext.getSwipeMaxOffPath())  
                return false;  
            if(e1.getX() - e2.getX() > m_AppContext.getSwipeMinDistance() &&  
                Math.abs(velocityX) > m_AppContext.getSwipeThresholdVelocity()) 
            {  
                // de derecha a izquierda  
            	int pos = m_Listview.pointToPosition((int)e2.getX(), (int)e2.getY());
            	DeleteWine(pos);
            }  
            else if (e2.getX() - e1.getX() > m_AppContext.getSwipeMinDistance() &&  
                Math.abs(velocityX) > m_AppContext.getSwipeThresholdVelocity()) 
            {  
            	// de izquierda a derecha 
            	int pos = m_Listview.pointToPosition((int)e2.getX(), (int)e2.getY());
            	LaunchShowActivity(pos);
            }  
            return false;  
        }  
    }
} 
