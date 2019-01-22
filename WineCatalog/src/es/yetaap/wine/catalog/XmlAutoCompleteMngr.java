package es.yetaap.wine.catalog;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;
import android.content.Context;
import android.util.Xml;
import android.widget.ArrayAdapter;

public class XmlAutoCompleteMngr 
{
	private String m_arrName;     
	private Context m_context;
	private SDManager m_sdMngr;
	
	public XmlAutoCompleteMngr(Context _contextActivity, SDManager _sd, String _fileAndArrName)    
	{        
		this.m_arrName = _fileAndArrName;       
		this.m_context = _contextActivity;
		this.m_sdMngr = _sd;
	}
	
	public AppGlobalContext.Error parseXmlFile(ArrayAdapter<String> _array)    
	{        
		AppGlobalContext.Error error = AppGlobalContext.Error.ERROR_XML;
		//Instanciamos la fábrica para DOM        
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();        
     
		try        
		{            
			//Creamos un nuevo parser DOM            
			DocumentBuilder builder = factory.newDocumentBuilder();             
			//Realizamos lalectura completa del XML
			InputStream in = this.getInputStream();
			Document dom = builder.parse(in);             
			//Nos posicionamos en el nodo principal del árbol (<array>)            
			Element root = dom.getDocumentElement();             
			//Localizamos todos los elementos <item>            
			NodeList items = root.getElementsByTagName("item");             
			
			//Recorremos la lista de items            
			for (int i=0; i < items.getLength(); i++)            
			{                              
				//Obtenemos el item actual                
				Node item = items.item(i);  
				_array.add(obtenerTexto(item));
			}
			error = AppGlobalContext.Error.ERROR_OK;
			in.close();
		}       
		catch (Exception ex)  
		{          
			throw new RuntimeException(ex); 
		}        
		
		return error;
	}    
	
	private String obtenerTexto(Node _dato)  
	{      
		StringBuilder texto = new StringBuilder();   
		NodeList fragmentos = _dato.getChildNodes(); 
		for (int k=0; k < fragmentos.getLength();k++)   
		{         
			texto.append(fragmentos.item(k).getNodeValue()); 
		}        
		return texto.toString();
	}   
	
	private InputStream getInputStream() 
	{     
		return m_sdMngr.getXmlAutoCompleteFileInput(m_context,m_arrName);
	}

	public AppGlobalContext.Error dumpXmlFile(AutoCompleteAdapter _array)
	{
		AppGlobalContext.Error error = AppGlobalContext.Error.ERROR_XML;
		
		//Creamos el serializer
		XmlSerializer ser = Xml.newSerializer(); 
		OutputStream fOut = m_sdMngr.getXmlAutoCompleteFileOutput(m_context,m_arrName);
		OutputStreamWriter fout = new OutputStreamWriter(fOut); 

		try
		{
			//Asignamos el resultado del serializer al fichero
			ser.setOutput(fout); 

			//Construimos el XML
			ser.startTag("", "array"); 
			ser.attribute("", "name", m_arrName);
			
			for(int i = 0; i < _array.getOrigCount(); i++)
			{
				ser.startTag("", "item");
				ser.text(_array.getOrigItem(i));
				ser.endTag("", "item"); 
			}
			
			ser.endTag("", "array");
			ser.endDocument(); 
			fout.close();
			error = AppGlobalContext.Error.ERROR_OK;
		}
		catch (IOException ex)
		{
			throw new RuntimeException(ex);  
		}			
		catch (IllegalArgumentException ex)
		{
			throw new RuntimeException(ex);  
		}		
		catch (IllegalStateException ex)
		{
			throw new RuntimeException(ex);  
		}	
		
		return error;
	}
}
