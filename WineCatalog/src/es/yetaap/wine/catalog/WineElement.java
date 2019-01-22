package es.yetaap.wine.catalog;

import android.os.Parcel;
import android.os.Parcelable;

public class WineElement implements Parcelable
{
	private long m_Id;
	private String m_wineName;
    private String m_wineProducer;
    private String m_wineRegion;
	private int m_harvestYear;
	private String m_wineMaduration;
	private String m_wineType;
	private String m_wineComment;
	private String m_winePhoto;
	private float m_wineAlcohol;
	private float m_wineRating;

	public WineElement()
	{
		m_Id = AppGlobalContext.UNDEFINED;
	}
	
	public WineElement(long _Id, String _wineName, String _wineProducer, 
			           String _wineRegion, int _harvestYear, String _wineMaduration, 
			           String _wineType, String _wineComment, String _winePhoto,
			           float _wineAlcohol, float _rating)
	{
		m_Id = _Id;
		m_wineName = _wineName;
		m_wineProducer = _wineProducer;
		m_wineRegion = _wineRegion;
		m_harvestYear = _harvestYear;
		m_wineMaduration = _wineMaduration;
		m_wineType = _wineType;
		m_wineComment = _wineComment;
	    m_winePhoto = _winePhoto;
	    m_wineAlcohol = _wineAlcohol;		
	    m_wineRating = _rating;
	}

    public long getId()
    {
        return m_Id;
    }

    public String getWineName()
    {
        return m_wineName;
    }
		 
    public String getWineProducer()
    {
        return m_wineProducer;
    }

    public String getWineRegion()
    {
        return m_wineRegion;
    }

    public int getHarvestYear()
    {
    	return m_harvestYear;
    }
    
	public String getWineMaduration()
	{
		return m_wineMaduration;
	}
		
	public String getWineType()
	{
		return m_wineType;
	}
	
	public String getWineComment()
	{
		return m_wineComment;
	}	
	
	public String getWinePhoto()
	{
		return m_winePhoto;
	}
	
	public float getWineAlcohol()
	{
		return m_wineAlcohol;
	}	
	
	public float getWineRating()
	{
		return m_wineRating;
	}	
	
    public void setId(long _Id)
    {
        m_Id = _Id;
    }

	public void setWineName(String _wineName)
	{
		m_wineName = _wineName;
	}

	public void setWineProducer(String _wineProducer)
	{
		m_wineProducer = _wineProducer;
	}
	    
	public void setWineRegion(String _wineRegion)
	{
		m_wineRegion = _wineRegion;
	}    
		
	public void setHarvestYear(int _harvestYear)
    {
    	m_harvestYear = _harvestYear;
    }
    
	public void setWineMaduration(String _wineMaduration)
	{
		m_wineMaduration = _wineMaduration;
	}
		
	public void setWineType(String _wineType)
	{
		m_wineType = _wineType;
	}
	
	public void setWineComment(String _wineComment)
	{
		m_wineComment = _wineComment;
	}	
	
	public void setWinePhoto(String _winePhoto)
	{
		m_winePhoto = _winePhoto;
	}
	
	public void setWineAlcohol(float _wineAlcohol)
	{
		m_wineAlcohol = _wineAlcohol;
	}		
	
	public void setWineRating(float _wineRating)
	{
		m_wineRating = _wineRating;
	}	
	
	////////////////////////////////////////////////////////////

    public int describeContents() 
	{
        return 0;
    }

    // write your object's data to the passed-in Parcel
    public void writeToParcel(Parcel out, int flags) 
	{
        out.writeLong(m_Id);
		out.writeString(m_wineName);
		out.writeString(m_wineProducer);
		out.writeString(m_wineRegion);
		out.writeInt(m_harvestYear);
		out.writeString(m_wineMaduration);
		out.writeString(m_wineType);
		out.writeString(m_wineComment);
		out.writeString(m_winePhoto);
		out.writeFloat(m_wineAlcohol);
		out.writeFloat(m_wineRating);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<WineElement> CREATOR = new Parcelable.Creator<WineElement>() 
	{
        public WineElement createFromParcel(Parcel in) 
		{
            return new WineElement(in);
        }

        public WineElement[] newArray(int size) 
		{
            return new WineElement[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private WineElement(Parcel in) 
	{
        m_Id = in.readLong();
		m_wineName = in.readString();
		m_wineProducer = in.readString();
		m_wineRegion = in.readString();
		m_harvestYear = in.readInt();
		m_wineMaduration = in.readString();
		m_wineType = in.readString();
		m_wineComment = in.readString();
		m_winePhoto = in.readString();
		m_wineAlcohol = in.readFloat();
		m_wineRating = in.readFloat();
    }
	////////////////////////////////////////////////////
}
