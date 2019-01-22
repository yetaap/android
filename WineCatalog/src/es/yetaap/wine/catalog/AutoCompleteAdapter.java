package es.yetaap.wine.catalog;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

public class AutoCompleteAdapter extends ArrayAdapter<String> implements Filterable 
{
	private List<String> m_Data = null;
	private List<String> m_Filtered = null;
	
	public AutoCompleteAdapter(Context _context, int _textViewResourceId) 
	{
        super(_context, _textViewResourceId);
		m_Data = new ArrayList<String>();
		m_Filtered = new ArrayList<String>();
	}

    @Override
    public int getCount() 
    {
		if(m_Filtered != null)
			return m_Filtered.size();
		else
			return 0;
    }

    @Override
    public String getItem(int _index) 
    {
		if(m_Filtered != null)
			return m_Filtered.get(_index);
		else
			return null;
    }

    public int getOrigCount() 
    {
		if(m_Data != null)
			return m_Data.size();
		else
			return 0;
    }

    public String getOrigItem(int _index) 
    {
		if(m_Data != null)
			return m_Data.get(_index);
		else
			return null;
    }
    
    public void addFilter(String _item)
    {
		if(m_Filtered != null)
			m_Filtered.add(_item);
    }
    
    @Override
    public void add(String _item)
    {
		if(m_Data != null)
			m_Data.add(_item);
    }
    
	@Override
	public Filter getFilter() 
	{
	    Filter myFilter = new Filter() 
	    {
	        @Override
	        protected FilterResults performFiltering(CharSequence _constraint) 
	        {
	            List<String> resultsSuggestions = new ArrayList<String>();
	            if(_constraint != null)
	            {
		            for (int i = 0; i < getOrigCount(); i++) 
		            {
		            	String item = getOrigItem(i);
		            	if(item != null)
		            	{
		            		if(item.toLowerCase().contains(_constraint.toString().toLowerCase()))
		            		{
		            			resultsSuggestions.add(getOrigItem(i));
		            		}
		            	}
		            }
	            }
	            
	            FilterResults results = new FilterResults();
	            results.values = resultsSuggestions;
	            results.count = resultsSuggestions.size();
	            return results;
	        }

	        @Override
	        @SuppressWarnings("unchecked")
	        protected void publishResults(CharSequence _constraint, FilterResults _results) 
	        {
				if((m_Filtered != null) && (_results != null))
				{
					m_Filtered.clear();
					ArrayList<String> newValues = (ArrayList<String>) _results.values;
					if(newValues !=null) 
					{
						for (int i = 0; i < newValues.size(); i++) 
						{
							addFilter(newValues.get(i));
						}
						if(_results.count>0)
						{
							notifyDataSetChanged();
						} 
						else
						{
							notifyDataSetInvalidated();
						}
					}
				}
	        }
	    };
	    return myFilter;
	}
}
