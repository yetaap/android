package es.yetaap.wine.catalog;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;
import android.view.View;

public class MyAutoCompleteTextView extends AutoCompleteTextView implements View.OnClickListener,View.OnFocusChangeListener
{
	public MyAutoCompleteTextView(Context context) { 
        super(context); 
    } 
 
    public MyAutoCompleteTextView(Context context, AttributeSet attrs) { 
        super(context, attrs); 
    } 
 
    public MyAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) { 
        super(context, attrs, defStyle); 
    } 
 
    // Allows you to show options on empty string.
    @Override
    public boolean enoughToFilter() { 
        return true; 
    }

	public void onClick(View v) 
	{
		((MyAutoCompleteTextView)v).showDropDown();
	} 
	
	public void onFocusChange(View v, boolean hasFocus) 
	{
		if(hasFocus)
			((MyAutoCompleteTextView)v).showDropDown();
	}
}

 