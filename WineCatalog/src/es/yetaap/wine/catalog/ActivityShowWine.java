package es.yetaap.wine.catalog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class ActivityShowWine extends Activity 
{
	private TextView m_FirstLineText = null;
	private TextView m_SecondLineText = null;
	private TextView m_ThirdLineText = null;
	private TextView m_FourthLineText = null;
	private RatingBar m_Rating = null;
	private ImageView m_IconView;
	WineElement m_Element = null;
	private AlertDialog.Builder m_Builder = null;
	private AlertDialog m_AlertDlg = null;
	

    @Override
    public void onCreate(Bundle _savedInstanceState) 
    { 
        super.onCreate(_savedInstanceState); 
		requestWindowFeature(Window.FEATURE_NO_TITLE);       
        setContentView(R.layout.layout_showwine); 

  		// Recogemos los datos del intent que nos envian
   		Bundle bundle = getIntent().getExtras();

		if( bundle == null)
			return;
		
		// Recogemos el elemento a mostrar
		m_Element = (WineElement) bundle.getParcelable("es.yetaap.wine.catalog.wineElement");
        
		if(m_Element == null)
			return;
		
		m_FirstLineText = (TextView)findViewById(R.id.textViewFirstLine);
		m_SecondLineText = (TextView)findViewById(R.id.textViewSecondLine);
		m_ThirdLineText = (TextView)findViewById(R.id.textViewThirdLine);
		m_FourthLineText = (TextView)findViewById(R.id.textViewFourthLine);
		m_Rating = (RatingBar)findViewById(R.id.ratingBarShow);
		m_IconView = (ImageView)findViewById(R.id.imageViewShowWine);
		
		if(m_FirstLineText == null || m_SecondLineText == null || m_ThirdLineText == null || m_FourthLineText == null || m_Rating == null || m_IconView == null)
			return;
		
		// Rellenamos los textos
		String firstLine = m_Element.getWineName();
		if(m_Element.getHarvestYear() != 0)
			firstLine += " - " + String.valueOf(m_Element.getHarvestYear());
		if(m_Element.getWineAlcohol() != 0.0)
			firstLine += " - (" + String.valueOf(m_Element.getWineAlcohol()) + "%)";

		String secondLine = "";
		if( m_Element.getWineProducer().length() > 0)
			secondLine += m_Element.getWineProducer();
		if( m_Element.getWineRegion().length() > 0)
			secondLine += " - " + m_Element.getWineRegion();
		String thirdLine = "";
		if( m_Element.getWineMaduration().length() > 0)
			thirdLine += m_Element.getWineMaduration();
		if( m_Element.getWineType().length() > 0)
			thirdLine += " - " + m_Element.getWineType();

		m_FirstLineText.setTypeface(((AppGlobalContext)getApplicationContext()).getFontCondensedBold());
		m_FirstLineText.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
		m_FirstLineText.setText(firstLine); 
		
		m_SecondLineText.setTypeface(((AppGlobalContext)getApplicationContext()).getFontCondensed());
		m_SecondLineText.setTextSize(TypedValue.COMPLEX_UNIT_DIP,18);
		m_SecondLineText.setText(secondLine);   
		
		m_ThirdLineText.setTypeface(((AppGlobalContext)getApplicationContext()).getFontCondensed());
		m_ThirdLineText.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);
		m_ThirdLineText.setText(thirdLine);
		
		m_FourthLineText.setTypeface(((AppGlobalContext)getApplicationContext()).getFontLight());
		m_FourthLineText.setTextSize(TypedValue.COMPLEX_UNIT_DIP,18);
		m_FourthLineText.setText(m_Element.getWineComment());	
		
		m_Rating.setRating(m_Element.getWineRating());
		
		String pathPhoto = m_Element.getWinePhoto();
		if( pathPhoto == null)
		{
			m_IconView.setImageResource(R.drawable.nophoto_extra_big);
		}
		else
		{
			m_IconView.setImageBitmap(((AppGlobalContext)getApplicationContext()).getSDManager().DecodeJpgFile(pathPhoto));
		}
		
		// Creamos el dialogo para informar de campos sin completar
		m_Builder = new AlertDialog.Builder(this);
		m_Builder.setCancelable(false);
   } 
    
	public void onClickAddWine(View _arg0) 
	{
		// Llamar a la Activity de añadir vino
		Intent intent = new Intent();
		intent.setClass(this,ActivityTabHost.class);
		startActivity(intent);
		finish();
	}	 
	
	public void onClickEdit(View _arg0)
	{
		// Llamar a la Activity de actualizar vino
		Intent intent = new Intent();
		intent.setClass(this,ActivityTabHost.class);
		intent.putExtra("es.yetaap.wine.catalog.wineElement", (Parcelable)m_Element);
		startActivity(intent);
		finish();
	}

	public void onClickDelete(View _arg0)
	{
		m_Builder.setMessage(R.string.alertDelete);
		m_Builder.setPositiveButton(R.string.OK,okListener);
		m_Builder.setNegativeButton(R.string.NO,negativeListener);
		m_AlertDlg = m_Builder.create();
		m_AlertDlg.show();
	}
	
	// Click en AlertDialog boton aceptar que se puede continuar
	OnClickListener okListener = new DialogInterface.OnClickListener() 
	{
		public void onClick(DialogInterface _arg0, int _arg1) 
		{
			((AppGlobalContext)getApplicationContext()).getBBDD().DeleteWine(m_Element.getId());
			((AppGlobalContext)getApplicationContext()).getSDManager().DeleteFile(m_Element.getWinePhoto());
			_arg0.dismiss();
		}
	};
	
	// Click en AlertDialog boton cancelar
	OnClickListener negativeListener = new DialogInterface.OnClickListener() 
	{
		public void onClick(DialogInterface _arg0, int _arg1) 
		{
			_arg0.dismiss();
		}
	};
	
	public void onClickExitWine(View _arg0) 
	{
		finish();
	}		
}
