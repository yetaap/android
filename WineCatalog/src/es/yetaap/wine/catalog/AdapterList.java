package es.yetaap.wine.catalog;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
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
	 
		if(item == null)
		{
			LayoutInflater inflater = this.m_Context.getLayoutInflater();
			item = inflater.inflate(R.layout.layout_itemlist, null);
	 
			holder = new ViewHolder();
			holder.mMainText = (TextView)item.findViewById(R.id.textViewMain);
			holder.mMainText.setTextSize(TypedValue.COMPLEX_UNIT_DIP,12);
			holder.mMainText.setTypeface(m_AppContext.getFontCondensedBold());
			holder.mSecondaryText = (TextView)item.findViewById(R.id.textViewSecondary);
			holder.mSecondaryText.setTextSize(TypedValue.COMPLEX_UNIT_DIP,10);
			holder.mSecondaryText.setTypeface(m_AppContext.getFontLight());
			holder.mRating = (RatingBar)item.findViewById(R.id.ratingBarListWine);
			holder.mIconView = (ImageView)item.findViewById(R.id.imageViewIcon);
			holder.mIconView.setAdjustViewBounds(true);
	
			switch(m_AppContext.getDensity()) 
			{
				case DisplayMetrics.DENSITY_LOW:  //LDPI
					holder.mIconView.setMaxHeight(24);
					holder.mIconView.setMaxWidth(24);
				break;
				case DisplayMetrics.DENSITY_MEDIUM: //MDPI
					holder.mIconView.setMaxHeight(32);
					holder.mIconView.setMaxWidth(32);
				break;
				case DisplayMetrics.DENSITY_HIGH: //HDPI
				default:
					holder.mIconView.setMaxHeight(48);
					holder.mIconView.setMaxWidth(48);
				break;
			} 

			item.setTag(holder);
		}
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
