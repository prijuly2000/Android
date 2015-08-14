package com.titanium.imagehosting;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
public class DowloadImage extends Activity 
{
	List<String> multipleFiles;
	Bitmap myBitmap;
	ProgressDialog dialog;
	String selectedFile;
	WebView webView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.download_image);
		
		multipleFiles=new ArrayList<String>();
		webView=(WebView)findViewById(R.id.webview);
		WebSettings webSettings = webView.getSettings();
		webView.loadUrl("http://captureimage.esy.es/ShowImages.php");
		webSettings.setJavaScriptEnabled(true);
		webView.addJavascriptInterface(this, "Android");
		
		
	}
	
	@JavascriptInterface
    public void receiveValueFromJs(String selected) 
    {
    	if(selected.equals(""))
    		selected="Nothing Selected";
    	Toast.makeText(this, "Received : " + selected,Toast.LENGTH_SHORT).show();
    	multipleFiles.add(selected);
    	
    }  
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@JavascriptInterface
    public void download()
    {
		if(multipleFiles.size()==0)
		{
			Toast.makeText(getApplicationContext(), "No File Selected", Toast.LENGTH_SHORT);
			return;
		}
	    // TODO Auto-generated method stub
		dialog = ProgressDialog.show(this, "", "Downloading files...", true);
		new Thread(new Runnable()
			{				
				@Override
				public void run()
				{
					// TODO Auto-generated method stub
					downloadFile();
					
				}
			}).start();
		
		    
    }

	
	private void downloadFile()
    {
		
	    // TODO Auto-generated method stub
		try
        {   Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	        
	        if(isSDPresent) 
	        {
	        	
                Object str[]= multipleFiles.toArray();
                System.out.println("-----------------------------------------------------------------");
                for (Object string : str)
                {
                	selectedFile=(String)string;
	                System.out.println(selectedFile);
	                String imageUrl="http://captureimage.esy.es/uploads/"+selectedFile;
	    	        URL url = new URL(imageUrl);
	    	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	    	        connection.setDoInput(true);
	    	        connection.connect();
	    	        InputStream input = connection.getInputStream();
	    	        myBitmap = BitmapFactory.decodeStream(input);
	    	      
	    	        //System.out.println( " SD Card path is==> " +isSDPresent );
		        	final String root = Environment.getExternalStorageDirectory().toString() + "/Download";
		        	System.out.println("Root : "+root);
		        	File myDir = new File(root);
		        	if(!myDir.exists())
		        		myDir.mkdirs();
		        	
		        	File file = new File(myDir, (String)string);
		        	if (file.exists())
		        	    file.delete();
		        	FileOutputStream out = new FileOutputStream(file);
		            myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
		            out.flush();
		            out.close();
		            
		            runOnUiThread(new Runnable()
					{
						
						@Override
						public void run()
						{
							Toast.makeText(DowloadImage.this, "Location : "+root+"/"+selectedFile,Toast.LENGTH_SHORT).show();					
							
						}
					});
                }
                dialog.dismiss();

                System.out.println("DOWNLOAD Complete & SAVED-----------------");
	            
	            

	        }
	       

	        
        } 
	    catch (final Exception e)
        {
	        // TODO Auto-generated catch block
        	System.out.println("----------Downloaad File excp------");
        	System.out.println(e.getMessage());
        	runOnUiThread(new Runnable()
			{
				
				@Override
				public void run()
				{
					Toast.makeText(DowloadImage.this,e.getMessage() , Toast.LENGTH_LONG).show();
				}
			});
	        e.printStackTrace();
        } 
		
	    
    }	

	
}
