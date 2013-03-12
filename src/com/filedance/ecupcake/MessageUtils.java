package com.filedance.ecupcake;

//	This class provides message utilities, such as sending a text message


//	These permissions have to be added to the AndroidManifest.xml file to send text directly, without using a texting app.  This is not needed is you use intents.
//
//		<uses-permission android:name="android.permission.SEND_SMS"/>
//



import java.io.ByteArrayOutputStream;

import org.apache.http.protocol.HTTP;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

public class MessageUtils {

	// This routine sends an intent request to send a text message via a user selected texting app
	public static void sendSMSintent(Context context, String telephoneNumber, String body) {
		
	    Uri uri = Uri.parse("tel:" + telephoneNumber);
	
	//    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
	    Intent intent = new Intent(Intent.ACTION_SEND, uri);
	    intent.putExtra("sms_body", body);
	//    intent.setType("vnd.android-dir/mms-sms");
	//    intent.setType("text/plain");
	    
	    
	//	intent.setType(HTTP.PLAIN_TEXT_TYPE);
		intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"jakester3d@gmail.com"});	// recipients
		intent.putExtra(Intent.EXTRA_SUBJECT, "subject test");
	//	intent.putExtra(Intent.EXTRA_TEXT, body);
		
		intent.setType("text/plain");
	
	    context.startActivity(intent);
    
	}
	
	
    public void onReceiveIntent(Context context, Intent intent) {
	    if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
		    StringBuilder buf = new StringBuilder();
		    Bundle bundle = intent.getExtras();
		    if (bundle != null) {
		    	BroadcastReceiver receiver;
		//	    SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
			    SmsMessage[] messages = null;
			    for (int i = 0; i < messages.length; i++) {
				    SmsMessage message = messages[i];
				    buf.append("Received SMS from ");
				    buf.append(message.getDisplayOriginatingAddress());
				    buf.append(" - ");
				    buf.append(message.getDisplayMessageBody());
				    //TODO : Do some opearation on SMS.
			    }
		    }
	    }
    }
    
    
	//Send SMS message to another device directly, without using a texting app
	public static void sendSMS(String phoneNumber, String message)
	{
		SmsManager sms = SmsManager.getDefault();  //Public constructor
		sms.sendTextMessage(phoneNumber, null, message, null, null);
	}
	
	//This will not sent MMS!
	public static void sendSMSwithData(String phoneNumber, String message, Bitmap bitmap)
	{
		SmsManager sms = SmsManager.getDefault();  //Public constructor
		
		//Convert bitmap to byte array
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		
	//	sms.sendDataMessage(phoneNumber, null, (short) 0, message.getBytes(), null, null);
		sms.sendDataMessage(phoneNumber, null, (short) 8091, byteArray, null, null);   //this crashes!!!!!!!
	}
	
	
	//Routines to send commands to another phone (the commands are the text message, they start with "launch:")
	public static void sendWebPage(String phoneNumber, String webPage) {
		sendSMS(phoneNumber, "launch:01:" + webPage);
		log("sendWebPage() phoneNumber=" + phoneNumber + ", webPage=" + webPage);
	}
	
	public static void sendMapAddress(String phoneNumber, String address) {
		sendSMS(phoneNumber, "launch:02:" + address);
		log("sendMapAddress() phoneNumber=" + phoneNumber + ", addres=" + address);
	}
	
	
	private static void log(String text) {	Log.d("eCupcake", text);	}
}
