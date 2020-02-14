package at.fh.swengb.nemetz

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    companion object{
        val USER_TOKEN = "TOKEN"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)

        if(sharedPreferences.getString(USER_TOKEN,null)== null){
            Log.e("Token","No Token found!")
        }
        else{
            val intent = Intent(this, NoteListActivity::class.java)
            startActivity(intent)
        }

        main_login_button.setOnClickListener {
            if(main_login_username.text.toString().isNotEmpty() and main_login_password.text.toString().isNotEmpty()) {
                val authRequest =
                    AuthRequest(main_login_username.text.toString(), main_login_password.text.toString())
                login(authRequest,
                    success = {
                        sharedPreferences.edit().putString(USER_TOKEN, it.token).apply()

                        val intent = Intent(this, NoteListActivity::class.java)
                        startActivity(intent)

                    },
                    error = {
                        Toast.makeText(this, this.getString(R.string.login_info_error) , Toast.LENGTH_SHORT).show()
                    }
                )
            }
            else {
                Toast.makeText(this, this.getString(R.string.login_info_missing_message) , Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun login (
        request: AuthRequest,
        success: (response: AuthResponse) -> Unit,
        error: (errorMessage: String) -> Unit) {
        NoteApi.retrofitService.login(request).enqueue(object: retrofit2.Callback<AuthResponse>{
            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                error("Login unsuccessful")
            }

            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    success(responseBody)
                }
                else {
                    error("unknown Error")
                }

            }
        })
    }
}
