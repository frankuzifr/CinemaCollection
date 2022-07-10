package space.frankuzi.cinemacollection.utils

import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import space.frankuzi.cinemacollection.R


const val MIN_RATE = "min_rate"
const val MAX_RATE = "max_rate"
const val MIN_YEAR = "min_year"
const val MAX_YEAR = "max_year"
class FirebaseConfig {
    private val firebaseRemoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
    private val firebaseConfigSettings = remoteConfigSettings {
        minimumFetchIntervalInSeconds = 30
    }

    var minRate: Int? = null
        private set

    var maxRate: Int? = null
        private set

    var minYear: Int? = null
            private set

    var maxYear: Int? = null
            private set

    init {
        firebaseRemoteConfig.setConfigSettingsAsync(firebaseConfigSettings)
        firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        firebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    minRate = if (firebaseRemoteConfig.getLong(MIN_RATE).toInt() == 0) null else firebaseRemoteConfig.getLong(MIN_RATE).toInt()
                    maxRate = if (firebaseRemoteConfig.getLong(MAX_RATE).toInt() == 0) null else firebaseRemoteConfig.getLong(MAX_RATE).toInt()
                    minYear = if (firebaseRemoteConfig.getLong(MIN_YEAR).toInt() == 0) null else firebaseRemoteConfig.getLong(MIN_YEAR).toInt()
                    maxYear = if (firebaseRemoteConfig.getLong(MAX_YEAR).toInt() == 0) null else firebaseRemoteConfig.getLong(MAX_YEAR).toInt()

                } else {
                    Log.e("Fetch", "Firebase fetch failed")
                }
            }
    }
}