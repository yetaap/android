package es.yetaap.wine.catalog;

import android.app.Activity; 
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
  
public class ActivityWineData extends Activity 
{ 
	public static final String XML_AUTO_TYPE = "typelist";
	public static final String XML_AUTO_MADURATION = "madurationlist";
	public static final String XML_AUTO_REGION = "regionlist";
	
	private EditText m_editTextName = null;
	private EditText m_editTextWinery = null;
	private MyAutoCompleteTextView m_autoCompleteRegion = null;
	private EditText m_editTextHarvestYear = null;
	private MyAutoCompleteTextView m_autoCompleteMaduration = null;
	private MyAutoCompleteTextView m_autoCompleteType = null;
	private EditText m_editTextNotes = null;
	private EditText m_editTextAlcohol = null;
	private RatingBar m_Rating = null;
	private TextView m_textViewRating = null;
	Button m_ButtonAddUpdate = null;
     
	private AutoCompleteAdapter m_adapterRegion = null;
	private AutoCompleteAdapter m_adapterMaduration = null;
	private AutoCompleteAdapter m_adapterType = null;
	private ImageView m_WineImage = null;
	
	private Bitmap m_Bitmap = null;
	private long m_IdWine = AppGlobalContext.UNDEFINED;
	
	public void onCreate(Bundle _savedInstanceState) 
    { 
        super.onCreate(_savedInstanceState); 
		requestWindowFeature(Window.FEATURE_NO_TITLE);        
        setContentView(R.layout.layout_datawine); 

        // Create the adapter and set it to the AutoCompleteTextView 
        m_adapterType = new AutoCompleteAdapter(this, R.drawable.dropdown);
        getData(XML_AUTO_TYPE,m_adapterType);

        m_adapterMaduration = new AutoCompleteAdapter(this, R.drawable.dropdown);
        getData(XML_AUTO_MADURATION,m_adapterMaduration);

        m_adapterRegion = new AutoCompleteAdapter(this, R.drawable.dropdown);
        getData(XML_AUTO_REGION,m_adapterRegion);
        
        m_editTextName = (EditText)findViewById(R.id.editTextName);
        m_editTextName.setTypeface(((AppGlobalContext)getApplicationContext()).getFontLight());
        m_editTextWinery = (EditText)findViewById(R.id.editTextProducer);
        m_editTextWinery.setTypeface(((AppGlobalContext)getApplicationContext()).getFontLight());
        m_autoCompleteRegion = (MyAutoCompleteTextView)findViewById(R.id.autoCompleteRegion);
        m_autoCompleteRegion.setTypeface(((AppGlobalContext)getApplicationContext()).getFontLight());
        m_editTextHarvestYear = (EditText)findViewById(R.id.editTextHarvestYear);
        m_editTextHarvestYear.setTypeface(((AppGlobalContext)getApplicationContext()).getFontLight());
        m_autoCompleteMaduration = (MyAutoCompleteTextView)findViewById(R.id.autoCompleteWineAge);
        m_autoCompleteMaduration.setTypeface(((AppGlobalContext)getApplicationContext()).getFontLight());
        m_autoCompleteType = (MyAutoCompleteTextView)findViewById(R.id.autoCompleteKindWine);
        m_autoCompleteType.setTypeface(((AppGlobalContext)getApplicationContext()).getFontLight());
        m_editTextNotes = (EditText)findViewById(R.id.editTextComentary);
        m_editTextNotes.setTypeface(((AppGlobalContext)getApplicationContext()).getFontLight());
        m_editTextAlcohol = (EditText)findViewById(R.id.editTextAlcohol);
        m_editTextAlcohol.setTypeface(((AppGlobalContext)getApplicationContext()).getFontLight());
        m_WineImage = (ImageView)findViewById(R.id.imageViewWine);
        m_Rating = (RatingBar)findViewById(R.id.ratingBarWine);
        m_textViewRating = (TextView)findViewById(R.id.textViewRating);
        m_textViewRating.setTypeface(((AppGlobalContext)getApplicationContext()).getFontLight());
        m_ButtonAddUpdate = (Button) findViewById(R.id.buttonAdd);
        m_ButtonAddUpdate.setTypeface(((AppGlobalContext)getApplicationContext()).getFontLight());
        
        m_WineImage.setTag("empty");
        m_autoCompleteRegion.setAdapter(m_adapterRegion);
        m_autoCompleteType.setAdapter(m_adapterType);   
        m_autoCompleteMaduration.setAdapter(m_adapterMaduration); 
		
		// Si es para un Update entonces existe un bundle con los datos a mostrar
   		Bundle bundle = getIntent().getExtras();
   		if(null != bundle)
   		{
			WineElement element = (WineElement) bundle.getParcelable("es.yetaap.wine.catalog.wineElement");
        
			if(element == null)
				return;
		
   			// Rellenamos los campos con la informacion obtenida
			m_IdWine = element.getId();
			m_editTextName.setText(element.getWineName());
			m_editTextWinery.setText(element.getWineProducer());
			m_autoCompleteRegion.setText(element.getWineRegion());
			m_editTextHarvestYear.setText(String.valueOf(element.getHarvestYear()));
			m_autoCompleteMaduration.setText(element.getWineMaduration());
			m_autoCompleteType.setText(element.getWineType());
			m_editTextNotes.setText(element.getWineComment());
			m_editTextAlcohol.setText(String.valueOf(element.getWineAlcohol()));
			m_Rating.setRating(element.getWineRating());
	
			String pathPhoto = element.getWinePhoto();
			if( pathPhoto == null)
			{
				m_WineImage.setImageResource(R.drawable.nophoto);
			}
			else
			{
				m_WineImage.setImageBitmap(((AppGlobalContext)getApplicationContext()).getSDManager().DecodeJpgFile(pathPhoto));
				m_WineImage.setTag(pathPhoto);
			}			
			
			// Cambiamos el titulo de la activity
			setTitle(R.string.Update);
			
			// Cambiamos el texto del boton
			m_ButtonAddUpdate.setText(R.string.Update);
   		}		
        
        ((AppGlobalContext)getApplicationContext()).setActivityWineData(this);
    }

	public IBinder getWindowToken()
	{
		return m_ButtonAddUpdate.getWindowToken();
	}
	
	public void setImage(Bitmap _Bitmap)
	{
		m_Bitmap = _Bitmap;
		m_WineImage.setImageBitmap(_Bitmap);
	}
	
	private AppGlobalContext.Error getData(String _fileId, ArrayAdapter<String> _adapter) 
    { 
		XmlAutoCompleteMngr xmlParser = new XmlAutoCompleteMngr(getBaseContext(),((AppGlobalContext)getApplicationContext()).getSDManager(),_fileId);
		return xmlParser.parseXmlFile(_adapter); 
    } 
	
	public void onClickAddDataWine(View _arg0) 
	{
		try
		{
			WineElement element = new WineElement();
			
			// Salvamos la imagen si es una nueva
			if(m_Bitmap != null)
			{
				String file = ((AppGlobalContext)getApplicationContext()).getSDManager().saveJpegFromBitmap(m_Bitmap);
				element.setWinePhoto(file);
			}
			else
			{
				String tag = (String) m_WineImage.getTag();
				if(tag.compareTo("empty") != 0)
				{
					element.setWinePhoto(tag);
				}
			}
			
			element.setWineName(m_editTextName.getText().toString());
			element.setWineProducer(m_editTextWinery.getText().toString());
			if(m_editTextHarvestYear.getText().length() > 0)
				element.setHarvestYear(Integer.parseInt(m_editTextHarvestYear.getText().toString()));
			element.setWineComment(m_editTextNotes.getText().toString());
			if(m_editTextAlcohol.getText().length() > 0)
				element.setWineAlcohol(Float.parseFloat(m_editTextAlcohol.getText().toString()));
			element.setWineRegion(m_autoCompleteRegion.getText().toString());
			element.setWineType(m_autoCompleteType.getText().toString());
			element.setWineMaduration(m_autoCompleteMaduration.getText().toString());
			element.setWineRating(m_Rating.getRating());
			
			if(m_autoCompleteRegion.getText().toString().length() > 0)
			{
				if(!findItem(m_autoCompleteRegion.getText().toString(),m_adapterRegion))
				{
					m_adapterRegion.add(m_autoCompleteRegion.getText().toString());
					XmlAutoCompleteMngr xmlParser = new XmlAutoCompleteMngr(getBaseContext(),((AppGlobalContext)getApplicationContext()).getSDManager(),XML_AUTO_REGION);
					xmlParser.dumpXmlFile(m_adapterRegion);
				}
			}
			if(m_autoCompleteMaduration.getText().toString().length() > 0)
			{
				if(!findItem(m_autoCompleteMaduration.getText().toString(),m_adapterMaduration))
				{
					m_adapterMaduration.add(m_autoCompleteMaduration.getText().toString());
					XmlAutoCompleteMngr xmlParser = new XmlAutoCompleteMngr(getBaseContext(),((AppGlobalContext)getApplicationContext()).getSDManager(),XML_AUTO_MADURATION);
					xmlParser.dumpXmlFile(m_adapterMaduration);
				}
			}
			if(m_autoCompleteType.getText().toString().length() > 0)
			{
				if(!findItem(m_autoCompleteType.getText().toString(),m_adapterType))
				{
					m_adapterType.add(m_autoCompleteType.getText().toString());
					XmlAutoCompleteMngr xmlParser = new XmlAutoCompleteMngr(getBaseContext(),((AppGlobalContext)getApplicationContext()).getSDManager(),XML_AUTO_TYPE);
					xmlParser.dumpXmlFile(m_adapterType);
				}
			}
			
			if(m_IdWine == AppGlobalContext.UNDEFINED)
			{
				((AppGlobalContext)getApplicationContext()).getBBDD().AddNewWine(element);
			}
			else
			{
				((AppGlobalContext)getApplicationContext()).getBBDD().UpdateWine(m_IdWine,element);
			}
			m_Bitmap = null;
			
			// Volver a la Activity principal
			Intent intent = new Intent();
			setResult(0, intent);
			finish();
		}
		catch( NumberFormatException ex)
		{
			throw new RuntimeException(ex);
		}
	}	 
	
	private boolean findItem(String _item, ArrayAdapter<String> _adapter)
	{
		for(int i = 0; i < _adapter.getCount(); i++)
		{
			if(_item.equals(_adapter.getItem(i)))
				return true;
		}
		return false;
	}
}
