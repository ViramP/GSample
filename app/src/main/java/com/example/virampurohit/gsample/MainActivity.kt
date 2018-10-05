package com.example.virampurohit.gsample

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.Auth.GOOGLE_SIGN_IN_API
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {
    override fun onConnectionFailed(p0: ConnectionResult) {


    }

    var RC_SIGN_IN = 9001
    lateinit var googleSignInClient : GoogleApiClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                requestEmail().
                build()


        googleSignInClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(GOOGLE_SIGN_IN_API, gso)
                .build()
//        googleSignInClient = GoogleSignIn.getClient(this,gso)


        btn_Login.setOnClickListener(object  : View.OnClickListener{
            override fun onClick(v: View?) {

                signIn()
            }
        })
        btn_LoginOut.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {

                Auth.GoogleSignInApi.signOut(googleSignInClient).setResultCallback {
                    updateUI(false)

                }
            }

        });

    }


    override fun onStart() {
        super.onStart()
        val opr = Auth.GoogleSignInApi.silentSignIn(googleSignInClient)
        if (opr.isDone) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d("TAG", "Got cached sign-in")
            val result = opr.get()
            handleSigninResult(result)
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
//            showProgressDialog()
            opr.setResultCallback(object  : ResultCallback<GoogleSignInResult> {
                override fun onResult(googleSignInResult: GoogleSignInResult) {
                    if (googleSignInResult != null) {
                        handleSigninResult(googleSignInResult)
                    }

                }

            });

        }
    }

    fun signIn() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleSignInClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN){
            var task = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            handleSigninResult(task)
        }
    }

    fun handleSigninResult(result : GoogleSignInResult) {
        Log.d("TAG", "handleSignInResult:" + result.isSuccess());

        if(result.isSuccess) {
            // Signed in successfully, show authenticated UI.
            val acct = result.signInAccount
            Log.w("TAG", "signInResult:displayName " + acct!!.displayName);
            Log.w("TAG", "signInResult:displayName " + acct!!.email);
            //Similarly you can get the email and photourl using acct.getEmail() and  acct.getPhotoUrl()

//            if (acct.photoUrl != null)
//                LoadProfileImage(imgProfilePic).execute(acct.photoUrl.toString())

            updateUI(true)
        }else{
            updateUI(true)
        }
    }


    fun updateUI(b: Boolean) {
        if(b){
            txt_label.setText(" Signin")
        }else{
            txt_label.setText(" Sign out ")
        }

    }
}
