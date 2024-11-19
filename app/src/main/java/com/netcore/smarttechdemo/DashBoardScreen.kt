package com.netcore.smarttechdemo
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.netcore.android.Smartech
import com.netcore.android.smartechappinbox.SmartechAppInbox
import com.netcore.android.smartechpush.SmartPush

import java.lang.ref.WeakReference

class DashBoardScreen : AppCompatActivity() {
    // Define your views
    private lateinit var tvFcmToken: TextView
    private lateinit var tvGuid: TextView
    private lateinit var tvAddToWishList: TextView
    private lateinit var tvAddToCart: TextView
    private lateinit var tvCheckout: TextView
    private lateinit var tvUpdateProfile: TextView
    private lateinit var tvClearIdentity: TextView
    private lateinit var tvLogout: TextView
    private lateinit var tvAppInbox: TextView
    private lateinit var tvCustomAppInbox: TextView
    private lateinit var tvSetLocation: TextView
    private lateinit var preferences: SharedPreferences
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard_screen)

        // Initialize all views
        initializeViews()

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Set click listeners
        setClickListeners()
    }

    private fun initializeViews() {
        tvFcmToken = findViewById(R.id.tv_fcm_token)
        tvGuid = findViewById(R.id.tv_guid)
        tvAddToWishList = findViewById(R.id.tv_add_to_wish_list)
        tvAddToCart = findViewById(R.id.tv_add_to_cart)
        tvCheckout = findViewById(R.id.tv_checkout)
        tvUpdateProfile = findViewById(R.id.tv_update_profile)
        tvClearIdentity = findViewById(R.id.tv_clear_identity)
        tvLogout = findViewById(R.id.tv_logout)
        tvAppInbox = findViewById(R.id.tv_appinox)
        tvCustomAppInbox = findViewById(R.id.tv_customappinox)
        tvSetLocation = findViewById(R.id.tv_set_location)
        preferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
    }

    private fun setClickListeners() {
        tvFcmToken.setOnClickListener { copyFcmToken() }
        tvGuid.setOnClickListener { copyDeviceGuid() }
        tvAddToWishList.setOnClickListener { trackAddToWishList() }
        tvAddToCart.setOnClickListener { trackAddToCart() }
        tvCheckout.setOnClickListener { trackCheckout() }
        tvUpdateProfile.setOnClickListener { updateProfile() }
        tvClearIdentity.setOnClickListener { clearIdentity() }
        tvLogout.setOnClickListener { logoutUser() }
        tvAppInbox.setOnClickListener { openAppInbox() }
        tvCustomAppInbox.setOnClickListener { openCustomAppInbox() }
        tvSetLocation.setOnClickListener { setLocation() }
    }

    private fun copyFcmToken() {
        Toast.makeText(this, "FCM Token copied!", Toast.LENGTH_SHORT).show()
        // Add clipboard functionality if needed
       val pushtoken= SmartPush.getInstance(WeakReference(applicationContext)).getDevicePushToken()
        tvFcmToken.setText(pushtoken)
    }

    private fun copyDeviceGuid() {
        Toast.makeText(this, "Device GUID copied!", Toast.LENGTH_SHORT).show()
        // Add clipboard functionality if needed

        val guid=Smartech.getInstance(WeakReference(applicationContext)).getDeviceUniqueId()
        tvGuid.setText(guid)
    }

    private fun trackAddToWishList() {
        Toast.makeText(this, "Added to Wishlist!", Toast.LENGTH_SHORT).show()
        val payload : HashMap<String, Any> = HashMap()
        payload["test"] = "5"
        payload["hello"] = "harish"
        Smartech.getInstance(WeakReference(applicationContext)).trackEvent("add_to_whishlist", payload)
    }

    private fun trackAddToCart() {
        Toast.makeText(this, "Added to Cart!", Toast.LENGTH_SHORT).show()
    }

    private fun trackCheckout() {
        val payload : HashMap<String, Any> = HashMap()
            payload["drawdown-flexyreqid"] = "FLEXI9098888 5"
            payload["drawdown_flexyreqid"] = "flexi9098888"
        Smartech.getInstance(WeakReference(applicationContext)).trackEvent("checkout", payload)
        Toast.makeText(this, "Proceeding to Checkout!", Toast.LENGTH_SHORT).show()
    }

    private fun updateProfile() {
        val payload : HashMap<String, Any> = HashMap()
        payload["FIRST_NAME"] = "Harish"
        payload["LAST_NAME"] = "Reddy"
        payload["AGE"] = 25

        Smartech.getInstance(WeakReference(applicationContext)).updateUserProfile(payload)
    }

    private fun clearIdentity() {
        Toast.makeText(this, "User identity cleared!", Toast.LENGTH_SHORT).show()
        Smartech.getInstance(WeakReference(applicationContext)).clearUserIdentity()
    }

    private fun logoutUser() {
        Toast.makeText(this, "User Logged Out!", Toast.LENGTH_SHORT).show()
        preferences.edit().clear().apply()
        Smartech.getInstance(WeakReference(this)).logoutAndClearUserIdentity(true)
    }

    private fun openAppInbox() {
        SmartechAppInbox.getInstance(WeakReference(applicationContext)).displayAppInbox(this)
    }

    private fun openCustomAppInbox() {
        Toast.makeText(this, "Opening Custom App Inbox!", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, CustomInbox::class.java))
    }

    private fun setLocation() {
        if (checkPermissions()) {
            getCurrentLocation()
        } else {
            requestPermissions()
        }
    }

  /*  private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions()
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val latitude = it.latitude
                val longitude = it.longitude
                Smartech.getInstance(WeakReference(applicationContext))
                    .setUserLocation(latitude, longitude)
                Toast.makeText(this, "Location set: $latitude, $longitude", Toast.LENGTH_SHORT).show()
            } ?: Toast.makeText(this, "Location unavailable. Ensure GPS is enabled.", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Failed to get location: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }*/


    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions() // Request permissions if not already granted
            return
        }

        // Retrieve the last known location
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude

                    // Pass the location to Smartech
                    Smartech.getInstance(WeakReference(applicationContext))
                        .setUserLocation(location)

                    // Inform user about successful location retrieval
                    Toast.makeText(this, "Location set: $latitude, $longitude", Toast.LENGTH_SHORT).show()
                } else {
                    // Handle null location scenario
                    Toast.makeText(this, "Location unavailable. Ensure GPS is enabled.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors while retrieving the location
                Toast.makeText(this, "Failed to get location: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation()
        } else {
            Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show()
        }
    }
}



































































/*import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.netcore.android.Smartech
import com.netcore.android.smartechappinbox.SmartechAppInbox
import java.lang.ref.WeakReference*/


/*
class DashBoardScreen : AppCompatActivity() {
    // Define your views
    private lateinit var tvFcmToken: TextView
    private lateinit var tvGuid: TextView
    private lateinit var tvAddToWishList: TextView
    private lateinit var tvAddToCart: TextView
    private lateinit var tvCheckout: TextView
    private lateinit var tvUpdateProfile: TextView
    private lateinit var tvClearIdentity: TextView
    private lateinit var tvLogout: TextView
    private lateinit var tvAppInbox: TextView
    private lateinit var tvCustomAppInbox: TextView
    private lateinit var tvSetLocation: TextView
    private lateinit var preferences: SharedPreferences
    private lateinit var fusedLocationClient: FusedLocationProviderClient





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard_screen)



        // Initialize all views
        tvFcmToken = findViewById(R.id.tv_fcm_token)
        tvGuid = findViewById(R.id.tv_guid)
        tvAddToWishList = findViewById(R.id.tv_add_to_wish_list)
        tvAddToCart = findViewById(R.id.tv_add_to_cart)
        tvCheckout = findViewById(R.id.tv_checkout)
        tvUpdateProfile = findViewById(R.id.tv_update_profile)
        tvClearIdentity = findViewById(R.id.tv_clear_identity)
        tvLogout = findViewById(R.id.tv_logout)
        tvAppInbox = findViewById(R.id.tv_appinox)
        tvCustomAppInbox = findViewById(R.id.tv_customappinox)
        tvSetLocation = findViewById(R.id.tv_set_location)

        preferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Set click listeners using lambdas
        tvFcmToken.setOnClickListener { copyFcmToken() }
        tvGuid.setOnClickListener { copyDeviceGuid() }
        tvAddToWishList.setOnClickListener { trackAddToWishList() }
        tvAddToCart.setOnClickListener { trackAddToCart() }
        tvCheckout.setOnClickListener { trackCheckout() }
        tvUpdateProfile.setOnClickListener { updateProfile() }
        tvClearIdentity.setOnClickListener { clearIdentity() }
        tvLogout.setOnClickListener { logoutUser() }
        tvAppInbox.setOnClickListener { openAppInbox() }
        tvCustomAppInbox.setOnClickListener { openCustomAppInbox() }
        tvSetLocation.setOnClickListener { setLocation() }


    }

    // Example method implementations
    private fun copyFcmToken() {

        Toast.makeText(this, "FCM Token copied!", Toast.LENGTH_SHORT).show()
        // You can also implement clipboard functionality here
    }

    private fun copyDeviceGuid() {


        Toast.makeText(this, "Device GUID copied!", Toast.LENGTH_SHORT).show()
        // Similar to above, clipboard functionality can be added
    }

    private fun trackAddToWishList() {
        // Track add to wish list event
        Toast.makeText(this, "Added to Wishlist!", Toast.LENGTH_SHORT).show()
    }

    private fun trackAddToCart() {
        // Track add to cart event
        Toast.makeText(this, "Added to Cart!", Toast.LENGTH_SHORT).show()
    }

    private fun trackCheckout() {
        // Track checkout event
        Toast.makeText(this, "Proceeding to Checkout!", Toast.LENGTH_SHORT).show()
        val payload : HashMap<String, Any> = HashMap()
        payload["drawdown-flexyreqid"] = "FLEXI9098888"
        payload["drawdown_flexyreqid"] = "flexi9098888"


        Smartech.getInstance(WeakReference(applicationContext)).trackEvent( "check out", payload)
    }

    private fun updateProfile() {
        // Code to update profile
        Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show()


    }

    private fun clearIdentity() {
        // Code to clear user identity
        Toast.makeText(this, "User identity cleared!", Toast.LENGTH_SHORT).show()
    }

    private fun logoutUser() {
        // Handle user logout
        Toast.makeText(this, "User Logged Out!", Toast.LENGTH_SHORT).show()

        Toast.makeText(applicationContext, "Logging out...", Toast.LENGTH_SHORT).show()

        val editor = preferences.edit()
        editor.clear() // Clear preferences on logout
        editor.apply()
        // Implement logout logic, like clearing session data
        Smartech.getInstance(WeakReference(this)).logoutAndClearUserIdentity(true)

    }

    private fun openAppInbox() {
        SmartechAppInbox.getInstance(WeakReference(applicationContext)).displayAppInbox(this)
    }

    private fun openCustomAppInbox() {
        // Open the Custom App Inbox
        Toast.makeText(this, "Opening Custom App Inbox!", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, CustomInbox::class.java) // Navigate to your CustomAppInboxActivity
        startActivity(intent)
    }

    private fun setLocation() {
        // Handle location setting
        Toast.makeText(this, "Setting location!", Toast.LENGTH_SHORT).show()


    }




    }
*/




