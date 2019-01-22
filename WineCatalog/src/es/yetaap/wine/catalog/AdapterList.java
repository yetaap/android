package es.yetaap.wine.catalog;

import java.util.ArrayList;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.RatingBar;

public class AdapterList extends ArrayAdapter<WineElement> 
{
	private Activity m_Context = null;
	private AppGlobalContext m_AppContext = null;
	private int m_iPosition = AppGlobalContext.UNDEFINED;
	 
	static class ViewHolder 
	{
		TextView mMainText;
		TextView mSecondaryText;
		RatingBar mRating;
		ImageView mIconView;
		
		ViewHolder(View item, AppGlobalContext _context)
		{
			mMainText = (TextView)item.findViewById(R.id.textViewMain);
			mMainText.setTextSize(TypedValue.COMPLEX_UNIT_DIP,12);
			mMainText.setTypeface(_context.getFontCondensedBold());
			mSecondaryText = (TextView)item.findViewById(R.id.textViewSecondary);
			mSecondaryText.setTextSize(TypedValue.COMPLEX_UNIT_DIP,10);
			mSecondaryText.setTypeface(_context.getFontLight());
			mRating = (RatingBar)item.findViewById(R.id.ratingBarListWine);
			mIconView = (ImageView)item.findViewById(R.id.imageViewIcon);
			mIconView.setAdjustViewBounds(true);

			switch(_context.getDensity()) 
			{
				case DisplayMetrics.DENSITY_LOW:  //LDPI
					mIconView.setMaxHeight(24);
					mIconView.setMaxWidth(24);
				break;
				case DisplayMetrics.DENSITY_MEDIUM: //MDPI
					mIconView.setMaxHeight(32);
					mIconView.setMaxWidth(32);
				break;
				case DisplayMetrics.DENSITY_HIGH: //HDPI
				default:
					mIconView.setMaxHeight(48);
					mIconView.setMaxWidth(48);
				break;
			} 
		}
	}

	AdapterList(Activity _context,ArrayList<WineElement> _arrWines) 
	{
	  super(_context, R.layout.layout_itemlist,_arrWines);
	  this.m_Context = _context;
	  this.m_AppContext = ((AppGlobalContext)_context.getApplicationContext());
	}
 
	public void setPosition(int _position)
	{
		m_iPosition = _position;
	}
	
	public View getView(int _position, View _convertView, ViewGroup _parent) 
	{
		View item = _convertView;
		ViewHolder holder;
	 
		// item es null cuando creas la lista por primera vez
		if(item == null)
		{
			LayoutInflater inflater = this.m_Context.getLayoutInflater();
			item = inflater.inflate(R.layout.layout_itemlist, null);
	 
			holder = new ViewHolder(item,m_AppContext);

			item.setTag(holder);
		}
		// item != null cuando haces scroll por la lista, se reusan las views que se salen de la vista al hacer scroll
		else			
		{
			holder = (ViewHolder)item.getTag();
		}
	 
		// Si el array ha sido vaciado por una nueva busqueda, no rellenamos el item
		if(m_AppContext.getArrWines().size() <= _position)
			return(item);
		
		// Rellenamos el MainText
		String sMainText = m_AppContext.getArrWines().get(_position).getWineName();
		if(m_AppContext.getArrWines().get(_position).getHarvestYear() != 0)
			sMainText += " - " + String.valueOf(m_AppContext.getArrWines().get(_position).getHarvestYear());
		holder.mMainText.setText(sMainText);
		
		// Rellenamos el SecondaryText
		String sSecondaryText = "";
		if(m_AppContext.getArrWines().get(_position).getWineRegion().length() > 0)
			sSecondaryText = m_AppContext.getArrWines().get(_position).getWineRegion();
		if( m_AppContext.getArrWines().get(_position).getWineMaduration().length() > 0)
			sSecondaryText += " - " + m_AppContext.getArrWines().get(_position).getWineMaduration();
		holder.mSecondaryText.setText(sSecondaryText);
		
		// Rellenamos la calificacion
		holder.mRating.setRating(m_AppContext.getArrWines().get(_position).getWineRating());
		
		// Rellenamos el Icono
		String pathPhoto = m_AppContext.getArrWines().get(_position).getWinePhoto();
		if( pathPhoto == null)
		{
			holder.mIconView.setImageResource(R.drawable.nophoto_big);
		}
		else
		{
   			holder.mIconView.setImageBitmap(m_AppContext.getSDManager().DecodeJpgFile(pathPhoto));
		}			

		// Ponemos el color del fondo segun si esta seleccionado o no
		if(m_iPosition == _position)
		{
			item.setBackgroundResource(R.color.selected_list_item); 
		}
		else
		{
			item.setBackgroundResource(R.drawable.listitembackground/*android.R.color.transparent*/); 
		}
		
		return(item);
	}
	
	public void notifyData()
	{
		notifyDataSetChanged();
	}
}
