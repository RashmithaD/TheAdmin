package in.yousee.theadmin;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

import in.yousee.theadmin.constants.RequestCodes;
import in.yousee.theadmin.constants.ResultCodes;
import in.yousee.theadmin.constants.ServerFiles;
import in.yousee.theadmin.model.CustomException;
import in.yousee.theadmin.util.LogUtil;

public class SessionHandler extends Middleware
{
	private Context context;

	private String sessionID;
	private String userID;
	private String userType;
	private UsesLoginFeature loginFeatureClient;
	private OnResponseReceivedListener responseListener;
	private static final String SESSION_DEBUG_TAG = "session_tag";
	private static final String TAG_RESPONSE_MSG= "msg";

	public static boolean isLoggedIn = false;
	private String username = "";
	private String password = "";
	private String phone = "";

	private static final String LOGIN_DATA = "login_data";
	private static final String KEY_PHONE = "phone";
	private static final String KEY_USERNAME = "username";
	private static final String KEY_PASSWORD = "password";
	private static final String KEY_USER_ID = "userId";
	public static final String KEY_SESSION_ID = "sessionID";



	public SessionHandler(OnResponseReceivedListener responseListener)
	{
		super(responseListener);
		this.context = responseListener.getContext();
		this.responseListener = responseListener;
	}
	private static SharedPreferences getLoginSharedPrefs(Context context)
	{
		return context.getSharedPreferences(LOGIN_DATA, Activity.MODE_PRIVATE);
	}

	private boolean getLoginCredentials(String username, String password)
	{
		Log.i(SESSION_DEBUG_TAG, "in getLogin credentials----------------------------------");
		if (isLoginCredentialsExists(context))
		{
			SharedPreferences sharedPrefs = getLoginSharedPrefs(context);
			username = sharedPrefs.getString(KEY_USERNAME, null);
			this.username = username;
			Log.i(SESSION_DEBUG_TAG, "username " + this.username);
			password = sharedPrefs.getString(KEY_PASSWORD, null);
			this.password = password;
			Log.i(SESSION_DEBUG_TAG, "password " + this.password);
			return true;
		}
		return false;

	}

	private void setPhoneNumber(String phone)
	{
		SharedPreferences sharedPrefs = getLoginSharedPrefs(context);
		SharedPreferences.Editor editor = sharedPrefs.edit();
		editor.putString(KEY_PHONE, phone);
		this.phone = phone;
		editor.commit();
		LogUtil.print("setting phone number" + getPhoneNumber(context));


	}
	public static String getPhoneNumber(Context context)
	{
		Log.i(SESSION_DEBUG_TAG, "getphonenumber()");
		SharedPreferences sharedPrefs = getLoginSharedPrefs(context);
		if (isPhoneExists(context))
		{

			String phone = sharedPrefs.getString(KEY_PHONE, "");
			Log.i(SESSION_DEBUG_TAG, "Phone = " + phone);
			return phone;
		}
		Log.i(SESSION_DEBUG_TAG, "phone false");
		return null;

	}
	public static boolean isPhoneExists(Context context)
	{

		SharedPreferences sharedPrefs = getLoginSharedPrefs(context);
		if (sharedPrefs.contains(KEY_PHONE) && sharedPrefs.getString(KEY_PHONE, "") != null)
		{
			return true;
		}
		return false;

	}

	private void setLoginCredentials(String username, String password)
	{
		SharedPreferences sharedPrefs = getLoginSharedPrefs(context);
		SharedPreferences.Editor editor = sharedPrefs.edit();
		editor.putString(KEY_USERNAME, username);
		editor.putString(KEY_PASSWORD, password);
		this.username = username;
		this.password = password;
		editor.commit();

	}

	private void setUserId(int userId)
	{
		SharedPreferences sharedPrefs = getLoginSharedPrefs(context);
		SharedPreferences.Editor editor = sharedPrefs.edit();
		editor.putInt(KEY_USER_ID, userId);
		Log.i(SESSION_DEBUG_TAG, "userid set to : " + userId);
		editor.commit();
	}

	public static boolean isUserIdExists(Context context)
	{

		SharedPreferences sharedPrefs = getLoginSharedPrefs(context);
		if (sharedPrefs.contains(KEY_USER_ID) && sharedPrefs.getInt(KEY_USER_ID, 0) != 0)
		{
			return true;
		}
		return false;

	}

	public static int getUserId(Context context)
	{
		Log.i(SESSION_DEBUG_TAG, "getUserId()");
		SharedPreferences sharedPrefs = getLoginSharedPrefs(context);
		if (isUserIdExists(context))
		{

			int userId = sharedPrefs.getInt(KEY_USER_ID, -1);
			Log.i(SESSION_DEBUG_TAG, "userId = " + userId);
			return userId;
		}
		Log.i(SESSION_DEBUG_TAG, "userId false");
		return 0;

	}


	public static boolean isLoginCredentialsExists(Context context)
	{
		Log.i(SESSION_DEBUG_TAG, "is LoginCredentialExists()");
		SharedPreferences sharedPrefs = getLoginSharedPrefs(context);

		if (sharedPrefs.contains(KEY_USERNAME) && sharedPrefs.contains(KEY_PASSWORD))
		{
			if (!(sharedPrefs.getString(KEY_USERNAME, "").equals("")) && !(sharedPrefs.getString(KEY_PASSWORD, "").equals("")))
			{
				Log.i(SESSION_DEBUG_TAG, "returning true");
				return true;
			}
			Log.i(SESSION_DEBUG_TAG, "returning false");
			return false;
		}
		Log.i(SESSION_DEBUG_TAG, "returning false");
		return false;

	}

	public static String getSessionId(Context context)
	{
		Log.i(SESSION_DEBUG_TAG, "getsessionId()");
		SharedPreferences sharedPrefs = getLoginSharedPrefs(context);
		if (isSessionIdExists(context))
		{

			String sessionId = sharedPrefs.getString(KEY_SESSION_ID, "error");
			Log.i(SESSION_DEBUG_TAG, "sessiocheppan id exixsts = " + sessionId);
			return sessionId;
		}
		Log.i(SESSION_DEBUG_TAG, "session id false");
		return null;

	}

	private void setSessionId(String sessionId)
	{
		Log.i(SESSION_DEBUG_TAG, "setsessionId()");
		SharedPreferences sharedPrefs = getLoginSharedPrefs(context);

		SharedPreferences.Editor editor = sharedPrefs.edit();

		editor.putString(KEY_SESSION_ID, sessionId);
		this.sessionID = sessionId;
		editor.commit();
		Log.i(SESSION_DEBUG_TAG, "sessionId set to = " + sharedPrefs.getString(KEY_SESSION_ID, ""));

	}

	public static boolean isSessionIdExists(Context context)
	{
		Log.i(SESSION_DEBUG_TAG, "isSessionIdExists()");
		SharedPreferences sharedPrefs = getLoginSharedPrefs(context);

		if (sharedPrefs.contains(KEY_SESSION_ID) && sharedPrefs.getString(KEY_SESSION_ID, "").equals("") == false)
		{
			Log.i(SESSION_DEBUG_TAG, "returning " + true);
			return true;
		}
		Log.i(SESSION_DEBUG_TAG, "returning " + false);
		return false;

	};

	public void loginExec() throws CustomException
	{

		Log.i("tag", "in login exec");
		if (getLoginCredentials(username, password))
		{
			loginExec(username, password);
		}
	}

	public void verifyExec(String phone, LoginActivity responseListener) throws CustomException
	{
		this.responseListener = responseListener;

		request.setUrl(NetworkConnectionHandler.DOMAIN + ServerFiles.VERIFY_EXEC);
		request.setRequestCode(RequestCodes.NETWORK_REQUEST_VERIFY);
		request.put("phoneNumber", phone);
		this.phone = phone;
		setPhoneNumber(phone);
		sendRequest();

	}

	public void submitOTP(String phone, String otp, LoginActivity loginFeatureClient) throws CustomException
	{
		request.setUrl(NetworkConnectionHandler.DOMAIN + ServerFiles.LOGIN_EXEC);
		request.put("phoneNumber", phone);
		request.put("otp", otp);
		request.setRequestCode(RequestCodes.NETWORK_REQUEST_OTP_SUBMIT);
		setPhoneNumber(phone);
		this.loginFeatureClient = loginFeatureClient;
		this.phone = phone;
		sendRequest();
	}

	public void loginExec(String username, String password) throws CustomException
	{
		Log.i("tag", "loginExec(" + username + ", " + password + ")");

		this.username = username;
		this.password = password;
		request.setUrl(NetworkConnectionHandler.DOMAIN + ServerFiles.LOGIN_EXEC);
		request.put("username", username);
		request.put("password", password);
		request.setRequestCode(RequestCodes.NETWORK_REQUEST_LOGIN);
		sendRequest();

	}

	public void logout(OnResponseReceivedListener listener) throws CustomException
	{

		Log.i(SESSION_DEBUG_TAG, "logging out");
		SharedPreferences sharedPrefs = getLoginSharedPrefs(context);
		SharedPreferences.Editor editor = sharedPrefs.edit();
		editor.remove(KEY_SESSION_ID);
		editor.remove(KEY_PHONE);
		editor.commit();

	}

	@Override
	public void serveResponse(String result, int requestCode, int resultCode)
	{
		//this.responseListner.onResponseRecieved(result, requestCode);
		//this.loginFeatureClient.onLoginSuccess();
		//Log.i(SESSION_DEBUG_TAG, result);

		LogUtil.print("serving response ==---- " + requestCode);

		if (requestCode == RequestCodes.NETWORK_REQUEST_VERIFY)
		{
			
			//setPhoneNumber(this.phone);
			LogUtil.print("request success -- " + result);
			try {
				JSONObject json = new JSONObject(result);

				String msg = json.getString("msg");
				LogUtil.print(msg);
				listener.onResponseReceived(msg,requestCode, resultCode);
				//int statusCode = json.getInt("status_code");
//				if (statusCode == 1) {
//					LogUtil.print("success -------" );
//					responseListener.onResponseReceived(new Boolean(true), requestCode);
//					return;
//				}
//				else
//				{
//					responseListener.onResponseReceived(new Boolean(false), requestCode);
//
//				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if(requestCode == RequestCodes.NETWORK_REQUEST_OTP_SUBMIT)
		{
			LogUtil.print("OTP submit");
			JSONObject json;
			int statusCode = 0;
			String sessionId = "";
			String msg = "";
			try
			{
				json = new JSONObject(result);
				//statusCode = json.getInt("status_code");
				msg = json.getString(TAG_RESPONSE_MSG);
				LogUtil.print(msg);
			} catch (Exception e) {
				LogUtil.print(e.getMessage());
			}
			listener.onResponseReceived(msg,requestCode,resultCode);
		}
		/*
		else if (requestCode == RequestCodes.NETWORK_REQUEST_LOGOUT)
		{
			Log.i(SESSION_DEBUG_TAG, "logging out");
			SharedPreferences sharedPrefs = getLoginSharedPrefs(context);
			SharedPreferences.Editor editor = sharedPrefs.edit();
			editor.remove(KEY_SESSION_ID);
			editor.commit();
			//logoutListener.onResponseRecieved(null, requestCode);

		}
		*/
	}

	@Override
	public void assembleRequest()
	{

	}

	@Override
	public Context getContext()
	{
		return context;
	}
}
