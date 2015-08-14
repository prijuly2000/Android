package com.titanium.imagehosting;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;


import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.MediaStore;

public class CaptureImage extends ActionBarActivity{
	final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
 	Uri imageUri		             = null;
    static TextView messageText= null;
    static TextView imageDetails      = null;
    public  static ImageView showImg  = null;
    
    CaptureImage CameraActivity = null;
    Button btn=null;
    int serverResponseCode = 0;
    ProgressDialog dialog = null;
    
    String upLoadServerUri = null;
     
    /**********  File Path *************/
    static String uploadFilePath = "";
    static String uploadFileName = "";
    
@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.capture_image);

	 CameraActivity = this;
    
        imageDetails = (TextView) findViewById(R.id.imageDetails);
         
        showImg = (ImageView) findViewById(R.id.showImg);
         btn=(Button)findViewById(R.id.button1);
        messageText=(TextView)findViewById(R.id.messageText);
        final Button photo = (Button) findViewById(R.id.photo);
         
        /************* Php script path ****************/
        upLoadServerUri = "http://captureimage.esy.es/UploadToServer.php";
         btn.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				dialog = ProgressDialog.show(CaptureImage.this, "", "Uploading file...", true);
	            imageDetails.setVisibility(TextView.INVISIBLE);
	            new Thread(new Runnable() {
	                    public void run() {
	                         runOnUiThread(new Runnable() {
	                                public void run() {
	                                    messageText.setText("uploading started.....");
	                                }
	                            });                      
	                       
	                         uploadFile(uploadFilePath + "" + uploadFileName);
	                                                  
	                    }
	                  }).start(); 
				
			}
		});
        photo.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v) {
                
                /*************************** Camera Intent Start ************************/ 
                   
                  // Define the file-name to save photo taken by Camera activity
                   
                  String fileName = "Camera_Example.jpg";
                  uploadFileName=fileName;
                  uploadFilePath="/storage/sdcard/DCIM/Camera/";
                  //messageText.setText("Uploading file path :- '/storage/sdcard/DCIM/Camera/"+uploadFileName+"'");
                  // Create parameters for Intent with filename
                   
                  ContentValues values = new ContentValues();
                   
                  values.put(MediaStore.Images.Media.TITLE, fileName);
                   
                  values.put(MediaStore.Images.Media.DESCRIPTION,"Image capture by camera");
                   
                  // imageUri is the current activity attribute, define and save it for later usage  
                   
                  imageUri = getContentResolver().insert(
                          MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                   
                  /**** EXTERNAL_CONTENT_URI : style URI for the "primary" external storage volume. ****/
   
                   
                  // Standard Intent action that can be sent to have the camera
                  // application capture an image and return it.  
                   
                  Intent intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
                   
                   intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    
                   intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                    
                  startActivityForResult( intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                  
               /*************************** Camera Intent End ************************/
                  
                  
                   
                   
            }


        });
        
}

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
      
     if (!sourceFile.isFile()) {
          
          dialog.dismiss(); 
           
          Log.e("uploadFile", "Source File not exist :"
                              +uploadFilePath + "" + uploadFileName);
           
          runOnUiThread(new Runnable() {
              public void run() {
                //  messageText.setText("Source File not exist :"
                  //        +uploadFilePath + "" + uploadFileName);
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
              dos.writeBytes("Content-Disposition: form-data; name='uploaded_file';filename='"
                                        + fileName + "'" + lineEnd);
               
              dos.writeBytes(lineEnd);
     
              // create a buffer of  maximum size
              bytesAvailable = fileInputStream.available(); 
     
              bufferSize = Math.min(bytesAvailable, maxBufferSize);
              buffer = new byte[bufferSize];
     
              // read file and write it into form...
              bytesRead = fileInputStream.read(buffer, 0, bufferSize);  
                 
              while (bytesRead > 0) {
                   
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
                
              Log.i("uploadFile", "HTTP Response is : "
                      + serverResponseMessage + ": " + serverResponseCode);
               
              if(serverResponseCode == 200)
              {
                   
                  runOnUiThread(new Runnable() 
                  {
                       public void run() 
                       {
                            
                           String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
                                         +"http://captureimage.esy.es/uploads/"
                                         +uploadFileName;
                            
                           messageText.setText(msg);
                           Toast.makeText(CaptureImage.this, "File Upload Complete.", 
                                        Toast.LENGTH_SHORT).show();
                       }
                   });                
              }    
               
              //close the streams //
              fileInputStream.close();
              dos.flush();
              dos.close();
                
         } 
          catch (MalformedURLException ex) {
              
             dialog.dismiss();  
             ex.printStackTrace();
              
             runOnUiThread(new Runnable() {
                 public void run() {
                     messageText.setText("MalformedURLException Exception : check script url.");
                     Toast.makeText(CaptureImage.this, "MalformedURLException", 
                                                         Toast.LENGTH_SHORT).show();
                 }
             });
              
             Log.e("Upload file to server", "error: " + ex.getMessage(), ex);  
         } catch (Exception e) {
              
             dialog.dismiss();  
             e.printStackTrace();
              
             runOnUiThread(new Runnable() {
                 public void run() {
                     messageText.setText("Got Exception : see logcat ");
                     Toast.makeText(CaptureImage.this, "Got Exception : see logcat ", 
                             Toast.LENGTH_SHORT).show();
                 }
             });
             Log.e("Upload file to server Exception", "Exception : "
                                              + e.getMessage(), e);  
         }
         dialog.dismiss();       
         return serverResponseCode; 
          
      } // End else block 
    } 



  @Override
     protected void onActivityResult( int requestCode, int resultCode, Intent data)
        {
            if ( requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                 
                if ( resultCode == RESULT_OK) {
                 
                   /*********** Load Captured Image And Data Start ****************/
                     
                    String imageId = convertImageUriToFile( imageUri,CameraActivity);
                    
                     
 
                   //  Create and excecute AsyncTask to load capture image
 
                    new LoadImagesFromSDCard().onPostExecute(""+imageId);
                     
                  /*********** Load Captured Image And Data End ****************/
                     
                
                } else if ( resultCode == RESULT_CANCELED) {
                     
                    Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
                } else {
                     
                    Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
                }
            }
            
        }
      
      
     /************ Convert Image Uri path to physical path **************/
      
     public static String convertImageUriToFile ( Uri imageUri, CaptureImage activity )  {
      
            Cursor cursor = null;
            int imageID = 0;
             
            try {
             
                /*********** Which columns values want to get *******/
                String [] proj={
                                 MediaStore.Images.Media.DATA,
                                 MediaStore.Images.Media._ID,
                                 MediaStore.Images.Thumbnails._ID,
                                 MediaStore.Images.ImageColumns.ORIENTATION
                               };
                 
                cursor = activity.managedQuery(
                         
                                imageUri,         //  Get data for specific image URI
                                proj,             //  Which columns to return
                                null,             //  WHERE clause; which rows to return (all rows)
                                null,             //  WHERE clause selection arguments (none)
                                null              //  Order-by clause (ascending by name)
                                 
                             );
                                   
                //  Get Query Data
                 
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                int columnIndexThumb = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);
                int file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                 
                //int orientation_ColumnIndex = cursor.
                //    getColumnIndexOrThrow(MediaStore.Images.ImageColumns.ORIENTATION);
                 
                int size = cursor.getCount();
                 
                /*******  If size is 0, there are no images on the SD Card. *****/
                 
                if (size == 0) {
 
 
                    imageDetails.setText("No Image");
                }
                else
                {
                
                    int thumbID = 0;
                    if (cursor.moveToFirst()) {
                         
                        /**************** Captured image details ************/
                         
                        /*****  Used to show image on view in LoadImagesFromSDCard class ******/
                        imageID     = cursor.getInt(columnIndex);
                         
                        thumbID     = cursor.getInt(columnIndexThumb);
                         
                        String Path = cursor.getString(file_ColumnIndex);
                         
                        //String orientation =  cursor.getString(orientation_ColumnIndex);
                         
                        
                        uploadFileName= Path.substring(Path.lastIndexOf("/")+1);
                        uploadFilePath=Path.substring(0, Path.lastIndexOf("/")+1);
                        
                        String CapturedImageDetails = " CapturedImageDetails : \n\n"
                               +" ImageID :"+imageID+"\n"
                               +" ThumbID :"+thumbID+"\n"
                               +" Path :"+uploadFilePath+uploadFileName+"\n";
                        // Show Captured Image detail on activity
                        imageDetails.setText( CapturedImageDetails );
                        System.out.println(" Path :"+uploadFilePath+uploadFileName+"\n");
                         
                    }
                }    
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
             
            // Return Captured Image ImageID ( By this ImageID Image will load from sdcard )
             
            return ""+imageID;
        }
      
      
         /**
         * Async task for loading the images from the SD card.
         *
         * @author Android Example
         *
         */
          
        // Class with extends AsyncTask class
         
     public class LoadImagesFromSDCard  extends AsyncTask<String, Void, Void> {
             
            private ProgressDialog Dialog = new ProgressDialog(CaptureImage.this);
             
            Bitmap mBitmap;
             
            protected void onPreExecute() {
                /****** NOTE: You can call UI Element here. *****/
                 
                // Progress Dialog
                Dialog.setMessage(" Loading image from Sdcard..");
                Dialog.show();
            }
 
 
            public void onPostExecute(String string) {
				// TODO Auto-generated method stub
				
			}


			// Call after onPreExecute method
            protected Void doInBackground(String... urls) {
                 
                Bitmap bitmap = null;
                Bitmap newBitmap = null;
                Uri uri = null;       
                     
                     
                    try {
                         
                        /**  Uri.withAppendedPath Method Description
                        * Parameters
                        *    baseUri  Uri to append path segment to
                        *    pathSegment  encoded path segment to append
                        * Returns
                        *    a new Uri based on baseUri with the given segment appended to the path
                        */
                         
                        uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + urls[0]);
                         
                        /**************  Decode an input stream into a bitmap. *********/
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                         
                        if (bitmap != null) {
                             
                            /********* Creates a new bitmap, scaled from an existing bitmap. ***********/
 
                            newBitmap = Bitmap.createScaledBitmap(bitmap, 170, 170, true);
                             
                            bitmap.recycle();
                             
                            if (newBitmap != null) {
                                 
                                mBitmap = newBitmap;
                            }
                        }
                    } catch (IOException e) {
                        // Error fetching image, try to recover
                         
                        /********* Cancel execution of this task. **********/
                        cancel(true);
                    }
                 
                return null;
            }
             
             
            protected void onPostExecute(Void unused) {
                 
                // NOTE: You can call UI Element here.
                 
                // Close progress dialog
                  Dialog.dismiss();
                 
                if(mBitmap != null)
                {
                  // Set Image to ImageView  
                   
                   showImg.setImageBitmap(mBitmap);
                }  
                 
            }
             
        }
}
