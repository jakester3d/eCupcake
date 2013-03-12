package com.filedance.ecupcake;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.ShareActionProvider;
import android.widget.Spinner;
import android.widget.Toast;


public class MainActivity extends Activity implements OnItemSelectedListener {
 
	//Constants
    private final static int BUTTON_COUNT = 8;
    
    //Store Android version
    private int mBuildVersion = VERSION.SDK_INT;
    
	//Reference our custom view
    private StackView mStackView;
    private int mLayerCurrent;
    private ImageView[] mImageButtons = new ImageView[BUTTON_COUNT];
    private String mImageFilePath;
    
    //Controls
    private Spinner mPhraseSpinner;
    private EditText mPhraseEditText;
    private ShareActionProvider mShareActionProvider;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        // Inflate our UI from its XML layout description.
		setContentView(R.layout.activity_main);
        
		//Stack View
        mStackView = (StackView) findViewById(R.id.main_stack_view);
        
        //Image Buttons
        mImageButtons[0] = (ImageView) findViewById(R.id.main_button00);
        mImageButtons[1] = (ImageView) findViewById(R.id.main_button01);
        mImageButtons[2] = (ImageView) findViewById(R.id.main_button02);
        mImageButtons[3] = (ImageView) findViewById(R.id.main_button03);
        mImageButtons[4] = (ImageView) findViewById(R.id.main_button04);
        mImageButtons[5] = (ImageView) findViewById(R.id.main_button05);
        mImageButtons[6] = (ImageView) findViewById(R.id.main_button06);
        mImageButtons[7] = (ImageView) findViewById(R.id.main_button07);
 
        //Phrase
        mPhraseSpinner = (Spinner) findViewById(R.id.main_spinner_phrase);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.phrases_array, android.R.layout.simple_spinner_item);  // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  // Apply the adapter to the spinner
        mPhraseSpinner.setAdapter(adapter);
        mPhraseSpinner.setOnItemSelectedListener(this);  //Setup listener
        
        //Phrase Edit
        mPhraseEditText = (EditText) findViewById(R.id.main_edit_phrase);
        
        //See if this is a restored activity
		if (savedInstanceState == null) {
			log("onCreate() NOT restore--------------------------------------------");
	        mLayerCurrent = 0;
	        mImageFilePath = "";
		
	        //Set default radio button choice
	        RadioButton radioButtonDefault = (RadioButton) findViewById(R.id.main_radio0);
	        radioButtonDefault.setChecked(true);
	        
		} else {
			log("onCreate() restore!------------------------------------------------");
			//Restore in process app data
			mImageFilePath = savedInstanceState.getString("mImageFilePath");
			mLayerCurrent = savedInstanceState.getInt("mLayerCurrent");
			
			//Let the custom view restore its internal state
			mStackView.restoreStackViewState(savedInstanceState);
			
			mPhraseEditText.setText(mStackView.getPhraseText());
		}
        
        //Fill buttons with image choices
        updateImageButtons();
	}
	
	@Override
	public void onSaveInstanceState(Bundle icicle) {
		//The built in logic will handle saving UI element parameters that have an ID field
		super.onSaveInstanceState(icicle);
		
		//Save data during configuration change (such as screen rotation), this won't work if app is destroyed
		icicle.putString("mImageFilePath", mImageFilePath);
		icicle.putInt("mLayerCurrent", mLayerCurrent);
		
		//Let the custom view save its internal state data to this Activity Bundle
		mStackView.saveStackViewState(icicle);
		log("onSaveInstanceState() data saved!");
	}
	
	//Clicked Radio Button
	public void onRadioButtonClicked(View view) {
		mLayerCurrent = Integer.decode(view.getTag().toString());  //Get the layer # from the tag
		updateImageButtons();
	}
	
	//Update Image Buttons
	private void updateImageButtons() {
    	for (int choice = 0; choice < StackView.CHOICE_COUNT; choice++) {
    		mImageButtons[choice].setImageDrawable(mStackView.getImage(mLayerCurrent, choice));
    	}
	}
	
	//Clicked Image Button
	public void onImageViewClicked(View view) {
	    mStackView.setImage(mLayerCurrent, Integer.decode(view.getTag().toString()));  //Get the choice # from the tag
	}
	
	//Clicked Update Button
	public void onUpdateButtonClicked(View view) {
		String phrase;
		phrase = mPhraseEditText.getText().toString();
	    mStackView.setPhraseText(phrase);
	}
	
	//Phrase Spinner Item selected
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		if (view == null) {
			log("onItemSelected() view is null");
		} else {
			log("onItemSelected() view=" + view.toString() + ", pos=" + pos + ", id=" + id);
			String phrase;
		    phrase = (String) parent.getItemAtPosition(pos);
		    if (phrase.equals("(custom)")) {
		    	//Dialog box goes here!
		    } else {
	//	    	mPhraseEditText.setText(phrase);
			    try {
			    	mStackView.setPhraseText(phrase);
			    }
			    catch(Exception e) {
			    	log("onItemSelected() mStackView.setPhraseText failed! phrase=" + phrase);
			    }

		    }
		    
		    try {
		    	mStackView.setPhraseText(phrase);
		    }
		    catch(Exception e) {
		    	log("onItemSelected() mStackView.setPhraseText failed! phrase=" + phrase);
		    }
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		Toast.makeText(getApplicationContext(), "Nothing Selected!", Toast.LENGTH_LONG).show();
	}
	
	
	//Action bar (menu) option chosen
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
	        case R.id.menu_settings:
	            break;
	
	        case R.id.menu_share:   //This won't fire because it shows a submenu instead of this call back!!!!!!!!!!!
	            break;
	            
	        case R.id.menu_send_sms:   //This won't fire because it shows a submenu instead of this call back!!!!!!!!!!!
	//        	Bitmap bitmap;
	//        	bitmap = getBitmapFromView(mStackView);
	//        	log("onOptionsItemSelected() bitmap=" + bitmap.toString());
	//        	MessageUtils.sendSMSwithData("9192724385", "message goes here", bitmap);
	        	
	        	
	        	startActivity(Launch.sendImageMMS(this, "9192724385", "body goes here", mImageFilePath));
	        	
	            break;
            
        }	
        return super.onOptionsItemSelected(item);
	}
	
	//Clicked Sex Button
	public void onSexButtonClicked(View view) {
		//Save view to file or media store
    	saveImage();

		//Update the share menu intent
		updateShareIntent();
	}

	//Save image to file or media store
	public void saveImage() {

		Boolean useMediaStore = true;
		
		Bitmap bitmap;
//		//Get a bitmap from a resource
//	 	bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.background03);
		
//		//Image from a web site:
//		bitmap = ((BitmapDrawable)draw).getBitmap();  //Don't remember if this is related to a web site image or not
//		
//		String str_url = "http:www.image.png"
//		String name = this.getString(str_url);
//		URL url_value = new URL(name);
//		ImageView profile = (ImageView)v.findViewById(R.id.vdo_icon);
//		if (profile != null) {
//		    Bitmap mIcon1 = BitmapFactory.decodeStream(url_value.openConnection().getInputStream());
//		    profile.setImageBitmap(mIcon1);
//		}

		//Get a bitmap from a View
		bitmap = getBitmapFromView(mStackView);
		
		if (useMediaStore) {
			//Save the view image directly to the media store gallery
	 	    mImageFilePath = MediaStore.Images.Media.insertImage(getContentResolver(), getBitmapFromView(mStackView), "eCupcake", "A Cupcake for you!");
	 	    
	 	    //Save the image to a media store album
//	    	Uri uri =null;
//	    	uri = addToTouchActiveAlbum(this,"eCupcake Title", mImageFilePath);
//			Toast.makeText(this, "Image uri=" + uri.toString(), Toast.LENGTH_LONG).show();
		} else {
			String strFolderName = "Pictures";
			String strFileName = "test4";
			String strFileExt = "PGN";
			
			//Find a folder to store the image in

			String folder = "";
			if (isExternalStorageWritable()) {
				//Use external storage location
				folder = Environment.getExternalStorageDirectory().toString() + "/" + strFolderName;
			} else {
				//Use private, internal storage just for this app (will be deleted when app is uninstalled)
		        folder = this.getFilesDir().toString();
			}
		    
			//Create a file
			File file = null;
		    file = new File(folder, strFileName + "." + strFileExt);
		    OutputStream outStream = null;
		    try {
				outStream = new FileOutputStream(file);
				if (strFileExt.equals("PNG")) {
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
				}
				else if (strFileExt.equals("JPEG")) {
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
				}
				mImageFilePath = "file:/" + file.getAbsolutePath();
		    }
		    catch(Exception e) {
		    	log("saveImage() failed to create file in folder=" + folder.toString());
				Toast.makeText(this, "The file failed to save!", Toast.LENGTH_LONG).show();
				mImageFilePath = "";
				return;
		    }
		}
    	log("saveImage() mImageFilePath=" + mImageFilePath);
	}
	
//	public static Uri addToTouchActiveAlbum( Context context, String title, String filePath ) {
//	    ContentValues values = new ContentValues(); 
//	    values.put( Media.TITLE, title ); 
//	    values.put( Images.Media.DATE_TAKEN, System.currentTimeMillis() );
//	    values.put( Images.Media.BUCKET_ID, filePath.hashCode() );
//	    values.put( Images.Media.BUCKET_DISPLAY_NAME, "eCupcake Bucket" );
//
//	    values.put( Images.Media.MIME_TYPE, "image/png" );
//	    values.put( Media.DESCRIPTION, context.getResources().getString( R.string.app_name ) ); 
//	    values.put( MediaStore.MediaColumns.DATA, filePath );
//	    Uri uri = context.getContentResolver().insert( Media.EXTERNAL_CONTENT_URI , values );
//
//	    return uri;
//	}
	
	
	public static Bitmap getBitmapFromView(View view) {
	    Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
	    Canvas canvas = new Canvas(returnedBitmap);
	    Drawable bgDrawable =view.getBackground();
	    if (bgDrawable!=null) 
	        bgDrawable.draw(canvas);
	    else 
	        canvas.drawColor(Color.WHITE);
	    view.draw(canvas);
	    return returnedBitmap;
	}
	
	@SuppressLint("NewApi")
	private void updateShareIntent() {
		if (mBuildVersion >= 14) {
		    // Attach an intent to this ShareActionProvider.  You can update this at any time,
		    // like when the user selects a new piece of data they might like to share.

			Intent intent;
//			intent = Launch.shareImage(this, "", "A cupcake for you!", "Here is the image: " + mImageFilePath, mImageFilePath);
			if (mImageFilePath.isEmpty()) {
				log("updateShareIntent() mImageFilePath is emtpy=" + mImageFilePath);
				intent = Launch.shareImage(this, "", "A cupcake for you!", "blank body", "");
			} else {
				intent = Launch.sendImageMMS(this, "9192724385", "body goes here", mImageFilePath);
			}

			
			
	//		Bitmap bitmap;
	//		bitmap = getBitmapFromView(mStackView);
	//		intent = launch.shareBitmap(this, "", "A cupcake for you!", "Here is the image: " + mImageFilePath, bitmap);
	//		intent = launch.shareImage(this, "", "A cupcake for you!", "Here is the image: " + mImageFile.getAbsolutePath(), mImageFile.getAbsolutePath());  //This doesn't work!!!!!!!
			
//			String subject = "A cupcake man!";
//			String body = "File Path: " + mImageFile.getAbsolutePath();
//			
//			Intent intent = new Intent(Intent.ACTION_SEND);
//			intent.setType("text/plain");
//			intent.putExtra(Intent.EXTRA_SUBJECT, subject);
//			intent.putExtra(Intent.EXTRA_TEXT, body);
//			Uri uri = Uri.fromFile(mImageFile);
//			intent.putExtra(Intent.EXTRA_STREAM, uri);
			
			mShareActionProvider.setShareIntent(intent);
			
	//		Toast.makeText(this, "Share intent updated: " + mImageFilePath, Toast.LENGTH_SHORT).show();
		}
		else {
			Toast.makeText(this, "Android version " + mBuildVersion + " does not support this command!", Toast.LENGTH_LONG).show();
		}
	}

	
	//Create menus
	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);

		if (mBuildVersion >= 14) {
		    // Get the menu item.
		    MenuItem menuItem = menu.findItem(R.id.menu_share);
			
		    // Get the provider and hold onto it to set/change the share intent.
		    mShareActionProvider = (ShareActionProvider) menuItem.getActionProvider();
	
		    //Assign a simple intent for now, do an image intent later
			mShareActionProvider.setShareIntent(Launch.shareImage(this, "", "Email subject", "Email message text", ""));
		}
		return true;
	}
	
	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}

	/* Checks if external storage is available to at least read */
	public boolean isExternalStorageReadable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state) ||
	        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}
	
	private void log(String text) {	Log.d("eCupcake", text);	}
	
}

