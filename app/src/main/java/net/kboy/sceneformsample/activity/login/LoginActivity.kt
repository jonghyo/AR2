package net.kboy.sceneformsample.activity.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.getInstance
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import net.kboy.sceneformsample.R
import net.kboy.sceneformsample.activity.SceneformActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // splash
        try {
            Thread.sleep(1000)

        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        setTheme(net.kboy.sceneformsample.R.style.AppTheme)
        super.onCreate(savedInstanceState)
        createSignInIntent()
        setContentView(net.kboy.sceneformsample.R.layout.login_activity)
    }

    private fun createSignInIntent() {
        // [START auth_fui_create_intent]
        // Choose authentication providers
        val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.FacebookBuilder().build())

        // Create and launch sign-in intent
        startActivityForResult(
                getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.LoginTheme)
                        .setLogo(net.kboy.sceneformsample.R.drawable.ar2)      // Set logo drawable
                        .build(),
                RC_SIGN_IN)
        // [END auth_fui_create_intent]
    }

    // [START auth_fui_result]
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                val intent = Intent(this, SceneformActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }
    // [END auth_fui_result]

    private fun signOut() {
        // [START auth_fui_signout]
        getInstance()
                .signOut(this)
                .addOnCompleteListener {
                    // ...
                }
        // [END auth_fui_signout]
    }

    companion object {
        private const val RC_SIGN_IN = 123
    }
}
