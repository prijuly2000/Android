package com.titanium.imagehosting;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class MultiPhotoSelectActivity extends BaseActivity {

 private ArrayList<String> imageUrls;
 private DisplayImageOptions options;
 private ImageAdapter imageAdapter;
 private int serverResponseCode = 0;
 private ProgressDialog dialog = null;
 ArrayList<String> selectedItems=null ;   
 private String upLoadServerUri = null;
 private String imagepath=null;

 
 
 @Override
 public void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.ac_image_grid);
  imageLoader = ImageLoader.getInstance();
  imageLoader.init(ImageLoaderConfiguration.createDefault(this));
  upLoadServerUri = "http://captureimage.esy.es/UploadToServer.php";   // url to upload on server
 
  final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
  final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
  Cursor imagecursor = managedQuery(
    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
    null, orderBy + " DESC");

  this.imageUrls = new ArrayList<String>();

  for (int i = 0; i < imagecursor.getCount(); i++) {
   imagecursor.moveToPosition(i);
   int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
   imageUrls.add(imagecursor.getString(dataColumnIndex));

   System.out.println("=====> Array path => "+imageUrls.get(i));
  }

  options = new DisplayImageOptions.Builder()
   .showStubImage(R.drawable.stub_image)
   .showImageForEmptyUri(R.drawable.image_for_empty_url)
   .cacheInMemory()
   .cacheOnDisc()
   .build();

  imageAdapter = new ImageAdapter(this, imageUrls);

  GridView gridView = (GridView) findViewById(R.id.gridview);
  gridView.setAdapter(imageAdapter);
 
 }

 
 
 
 
 @Override
 protected void onStop() {
  imageLoader.stop();
  super.onStop();
 }

 public void btnChoosePhotosClick(View v){

  selectedItems = imageAdapter.getCheckedItems();
  
  if(selectedItems.size() > 0)
  {
	  dialog = ProgressDialog.show(MultiPhotoSelectActivity.this, "", "Uploading files...", true);
	  new Thread(new Runnable() 
	  {
               public void run() 
               {
            	   for (String string : selectedItems) 
            	   {
            		      uploadFile(string);   
            	   }                
                                            
               }
       }).start();
  Toast.makeText(MultiPhotoSelectActivity.this, "Total photos uploaded: "+selectedItems.size(), Toast.LENGTH_SHORT).show();
  
  }
  else
  {
	  Toast.makeText(MultiPhotoSelectActivity.this, "No photos is selected to Upload ", Toast.LENGTH_SHORT).show();
	  
  }
  
 }
 //function to up[load th e images on server
 public int uploadFile(String sourceFileUri) 
 {
	    String fileName = sourceFileUri;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;  
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024; 
        File sourceFile = new File(sourceFileUri); 
        
        if (!sourceFile.isFile()) 
        {         
          dialog.dismiss();         
          runOnUiThread(new Runnable() 
          {
              public void run() 
              {
            	  Toast.makeText(MultiPhotoSelectActivity.this, "File Does not exist", Toast.LENGTH_SHORT).show();
              }
          }); 
          
          return 0;
         
        }
        else
        {
          try { 
           
             // open a URL connection to the Servlet
              FileInputStream fileInputStream = new FileInputStream(sourceFile);
              URL url = new URL(upLoadServerUri);
              
              // Open a HTTP  connection to  the URL
              conn = (HttpURLConnection) url.openConnection(); 
              conn.setDoInput(true); // Allow Inputs
              conn.setDoOutput(true); // Allow Outputs
              conn.setUseCaches(false); // Don't use a Cached Copy
              conn.setRequestMethod("POST");
              conn.setRequestProperty("Connection", "Keep-Alive");
              conn.setRequestProperty("ENCTYPE", "multipart/form-data");
              conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
              conn.setRequestProperty("uploaded_file", fileName); 
              
              dos = new DataOutputStream(conn.getOutputStream());
    
              dos.writeBytes(twoHyphens + boundary + lineEnd); 
              dos.writeBytes("Content-Disposition: form-data; name='uploaded_file';filename=\""+ fileName + "\"" + lineEnd);
              
              dos.writeBytes(lineEnd);
    
              // create a buffer of  maximum size
              bytesAvailable = fileInputStream.available(); 
    
              bufferSize = Math.min(bytesAvailable, maxBufferSize);
              buffer = new byte[bufferSize];
    
              // read file and write it into form...
              bytesRead = fileInputStream.read(buffer, 0, bufferSize);  
                
              while (bytesRead > 0) 
              {
               
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);   
                
               }
    
              // send multipart form data necesssary after file data...
              dos.writeBytes(lineEnd);
              dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
    
              // Responses from the server (code and message)
              serverResponseCode = conn.getResponseCode();
              String serverResponseMessage = conn.getResponseMessage();
               
             
              //close the streams //
              fileInputStream.close();
              dos.flush();
              dos.close();
               
         } 
          catch (MalformedURLException ex) 
          {
          
             dialog.dismiss();  
             ex.printStackTrace();
             
             runOnUiThread(new Runnable() 
             {
                 public void run() 
                 {
               
                     Toast.makeText(MultiPhotoSelectActivity.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                 }
             });
             
               
         } 
          catch (final Exception e) 
          {
          
             dialog.dismiss();  
             e.printStackTrace();
             
             runOnUiThread(new Runnable() 
             {
                 public void run() 
                 {
                 
                     Toast.makeText(MultiPhotoSelectActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                 }
             });
               
         }
        
         dialog.dismiss();       
         return serverResponseCode; 
         
         } // End else block 
       }
 
 
 public class ImageAdapter extends BaseAdapter 
 {

  ArrayList<String> mList;
  LayoutInflater mInflater;
  Context mContext;
  SparseBooleanArray mSparseBooleanArray;

  public ImageAdapter(Context context, ArrayList<String> imageList) {
   // TODO Auto-generated constructor stub
   mContext = context;
   mInflater = LayoutInflater.from(mContext);
   mSparseBooleanArray = new SparseBooleanArray();
   mList = new ArrayList<String>();
   this.mList = imageList;

  }

  public ArrayList<String> getCheckedItems() 
  {
   ArrayList<String> mTempArry = new ArrayList<String>();

   for(int i=0;i<mList.size();i++) 
   {
    if(mSparseBooleanArray.get(i)) 
    {
     mTempArry.add(mList.get(i));
    }
   }

   return mTempArry;
  }

  @Override
  public int getCount() 
  {
   return imageUrls.size();
  }

  @Override
  public Object getItem(int position) 
  {
   return null;
  }

  @Override
  public long getItemId(int position) 
  {
   return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) 
  {
   if(convertView == null) 
   {
    convertView = mInflater.inflate(R.layout.row_multiphoto_item, null);
   }

   CheckBox mCheckBox = (CheckBox) convertView.findViewById(R.id.checkBox1);
   final ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView1);

   imageLoader.displayImage("file://" +imageUrls.get(position), imageView, options, new SimpleImageLoadingListener() 
   {   
	   @Override
	   public void onLoadingComplete(Bitmap loadedImage) 
	   {
	     Animation anim = AnimationUtils.loadAnimation(MultiPhotoSelectActivity.this, R.anim.fade_in);
	     imageView.setAnimation(anim);
	     anim.start();
	   }
   });

   mCheckBox.setTag(position);
   mCheckBox.setChecked(mSparseBooleanArray.get(position));
   mCheckBox.setOnCheckedChangeListener(mCheckedChangeListener);

   return convertView;
  }

  OnCheckedChangeListener mCheckedChangeListener = new OnCheckedChangeListener() {

   @Override
   public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    // TODO Auto-generated method stub
    mSparseBooleanArray.put((Integer) buttonView.getTag(), isChecked);
   }
  };
 }
 

 
}

