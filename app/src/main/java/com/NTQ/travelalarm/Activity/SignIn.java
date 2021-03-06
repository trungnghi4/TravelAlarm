package com.NTQ.travelalarm.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.NTQ.travelalarm.App;
import com.NTQ.travelalarm.BaseActivity;
import com.NTQ.travelalarm.Data.FirebaseHandle;
import com.NTQ.travelalarm.R;
import com.NTQ.travelalarm.Utils.CommonUtils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import butterknife.BindView;

import static com.NTQ.travelalarm.Other.Constants.FB_ACCOUNT;
import static com.NTQ.travelalarm.Other.Constants.FB_FRIENDS;
import static com.NTQ.travelalarm.Other.Constants.ID;

public class SignIn extends BaseActivity implements
        View.OnClickListener {

    public String userID;
    @BindView(R.id.loginButton)
    LoginButton loginButton;
    @BindView(R.id.tv_info)
    TextView tv_info;
    private static final String TAG = "FacebookLogin";
    public static boolean isLoginFirst = false;

    private CallbackManager mCallbackManager;
//
    private FirebaseAuth mAuth;

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void addControl() {
        CommonUtils.askPermissionsAndShowMyLocation(App.getContext(),this);
    }

    @Override
    public void addEvent() {
        mAuth = FirebaseAuth.getInstance();

//        if(mAuth.getCurrentUser() != null)
            InitFacebookLogin();
        //        Log.d(TAG, mAuth.getCurrentUser().getEmail());

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
//        mCallbackManager = CallbackManager.Factory.create();
        mCallbackManager.onActivityResult(requestCode, resultCode, data);


    }

    private void InitFacebookLogin()
    {
        mCallbackManager = CallbackManager.Factory.create();
//        LoginButton loginButton = (LoginButton) findViewById(R.id.loginButton);
        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_friends"));
        Log.d(TAG, "init");
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList(
                "public_profile", "email", "user_friends"));
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //                LoginButton loginButton1 = (LoginButton) findViewById(R.id.loginButton);
                loginButton.setVisibility(View.INVISIBLE);
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
                LoginFacebookHandle();

                isLoginFirst = true;
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException e) {
                Log.d(TAG, "facebook:onError", e);
            }
        });
        if (isLoggedIn()) {
            LoginButton loginButton1 = (LoginButton) findViewById(R.id.loginButton);
            loginButton1.setVisibility(View.INVISIBLE);
            LoginFacebookHandle();
        }

    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            LoginFacebookHandle();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignIn.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void LoginFacebookHandle()
    {
        if (AccessToken.getCurrentAccessToken() == null) {
            disconnectFromFacebook();
            FacebookSdk.sdkInitialize(getApplicationContext());
            Toast.makeText(getBaseContext(),"Đang tiến hành đăng nhập lại",Toast.LENGTH_LONG).show();
            Intent mainIntent = new Intent(SignIn.this, SplashScreen.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mainIntent);
        }
        Toast.makeText(getBaseContext(), getResources().getText(R.string.login_success),Toast.LENGTH_LONG).show();
        UpdateDatabse();
    }

    private void UpdateDatabse()
    {
        UpdateAccountDatabase();
    }

    private AccessToken getAccessToken()
    {
        return AccessToken.getCurrentAccessToken();
    }

    private void Debug(String title, String text)
    {
        Log.e(title, text);
    }

    private boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;

    }

    private void UpdateAccountDatabase()
    {
        /**
         Creating the GraphRequest to fetch user details
         1st Param - AccessToken
         2nd Param - Callback (which will be invoked once the request is successful)
         **/
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            //OnCompleted is invoked once the GraphRequest is successful
            @Override
            public void onCompleted(JSONObject jsonObject, GraphResponse response) {
                String name = "", id = "", avatarURL = "",email="";
                try {
                    name = jsonObject.getString("name");
                    email = jsonObject.getString("email");
                    avatarURL = jsonObject.getJSONObject("picture").getJSONObject("data").getString("url");
                    id =jsonObject.getString("id");

                    userID = id;

                    if(mAuth.getCurrentUser() != null)
                        avatarURL = mAuth.getCurrentUser().getPhotoUrl().toString();

                    //update friend database
                    UpdateFriendsDatabase();
                    //Sử dụng thông tin lấy được
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

                try {
                    if(id != "")
                    {
                        ref.child(FB_ACCOUNT).child(id).child("name").setValue(name);

                        ref.child(FB_ACCOUNT).child(id).child("avatarURL").setValue("");
                        ref.child(FB_ACCOUNT).child(id).child("avatarURL").setValue(avatarURL);

                        savePreference(name, avatarURL, id);

                        FirebaseHandle.getInstance().setUserID(id);
                    }
                    Log.d("Chuyenscene" , "scene");
                    //Khoi chay mainActivity
                    Intent mainIntent = new Intent(SignIn.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();

                }catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        });
        // We set parameters to the GraphRequest using a Bundle.
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,picture.width(200)");
        request.setParameters(parameters);
        // Initiate the GraphRequest
        request.executeAsync();


        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + getAccessToken().getUserId(),
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        JSONObject jsonObject = response.getJSONObject();
                        String name = "", id = "", avatarURL = "";
                        try {
                            name = jsonObject.getString("name");
                            id =jsonObject.getString("id");
                            userID = id;

                            if(mAuth.getCurrentUser() != null)
                                avatarURL = mAuth.getCurrentUser().getPhotoUrl().toString();

                            //update friend database
                            UpdateFriendsDatabase();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

                        try {
                            if(id != "")
                            {
                                ref.child(FB_ACCOUNT).child(id).child("name").setValue(name);

                                ref.child(FB_ACCOUNT).child(id).child("avatarURL").setValue("");
                                ref.child(FB_ACCOUNT).child(id).child("avatarURL").setValue(avatarURL);

                                savePreference(name, avatarURL, id);

                                FirebaseHandle.getInstance().setUserID(id);
                            }
                            Log.d("Chuyenscene" , "scene");
                            //Khoi chay mainActivity
                            Intent mainIntent = new Intent(SignIn.this, MainActivity.class);
                            startActivity(mainIntent);

                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                }
        ).executeAsync();
    }

    private void savePreference(String name, String avatarURL, String userID) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("user_info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if(!name.equals(""))
            editor.putString("name", name);
        editor.putString("avatarURL", avatarURL);
        editor.putString("userID", userID);

        editor.apply();
    }

    private void UpdateFriendsDatabase()
    {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + getAccessToken().getUserId() + "/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        JSONObject jsonObject = response.getJSONObject();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        JSONArray array = new JSONArray();
                        try {
                            array = jsonObject.getJSONArray("data");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        for(int i = 0 ; i < array.length() ; i++){
                            try {
                                JSONObject obj = array.getJSONObject(i);
                                String id = obj.getString("id");
                                if(mAuth.getCurrentUser() != null) {
                                    ref.child(FB_ACCOUNT).child(userID).child(FB_FRIENDS).child(id)
                                            .child(ID).setValue(id);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        ).executeAsync();
    }

    public static void disconnectFromFacebook() {

        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }

        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                AccessToken.setCurrentAccessToken(null);
                LoginManager.getInstance().logOut();
            }
        }).executeAsync();
    }


}


