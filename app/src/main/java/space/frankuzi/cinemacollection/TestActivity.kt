package space.frankuzi.cinemacollection

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import space.frankuzi.cinemacollection.utils.LoadIdlingResource
import javax.inject.Inject

class TestActivity : Activity() {
    @Inject lateinit var idlingResource: LoadIdlingResource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}