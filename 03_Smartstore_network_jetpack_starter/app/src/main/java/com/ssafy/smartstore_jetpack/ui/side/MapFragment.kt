package com.ssafy.smartstore_jetpack.ui.side

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.airbnb.lottie.LottieAnimationView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.ssafy.smartstore_jetpack.R
import com.ssafy.smartstore_jetpack.databinding.FragmentMapBinding
import com.ssafy.smartstore_jetpack.ui.MainActivity
import com.ssafy.smartstore_jetpack.util.PermissionChecker
import kotlin.math.*

class MapFragment : Fragment(), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null
    private var currentMarker: Marker? = null
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mapView: MapView
    private var currentPosition: Location = Location("").apply {
        latitude = 0.0
        longitude = 0.0
    }

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(layoutInflater, container, false)
        mapView = binding.map
        mapView.onCreate(savedInstanceState)

        // 위치 정보 클라이언트 초기화
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        checkAndRequestPermissions()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestLocationAndCalculateDistance()
    }

    private fun requestLocationAndCalculateDistance() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mFusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    currentPosition = it
                    val storeLatitude = 36.108145
                    val storeLongitude = 128.4181908
                    val distance = calculateDistance(
                        it.latitude, it.longitude,
                        storeLatitude, storeLongitude
                    )
                    showDistanceDialog(distance)
                }
            }
        }
    }

    private fun showDistanceDialog(distance: Double) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_distance, null)
        val textViewDistance = dialogView.findViewById<TextView>(R.id.textviewDistance)

        textViewDistance.text = "The distance from the restaurant is ${String.format("%.0f", distance)} m."

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Find Location") { _, _ ->
                val destinationLat = 36.108145
                val destinationLng = 128.4181908
                openGoogleMaps(destinationLat, destinationLng)
            }
            .setNegativeButton("Close") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun openGoogleMaps(latitude: Double, longitude: Double) {
        val uri = Uri.parse("google.navigation:q=$latitude,$longitude")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.google.android.apps.maps")
        }
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), "Not Installed Google Maps.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun checkAndRequestPermissions() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 1000
            )
        } else {
            mapView.getMapAsync(this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            mapView.getMapAsync(this)
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        Log.d("MapFragment", "onMapReady called")

        // 매장 위치
        val storeLocation = LatLng(36.108145, 128.4181908)

        // 매장 마커 추가
        googleMap?.addMarker(MarkerOptions().position(storeLocation).title("CAFEZINO").icon(
            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
        ).snippet("Destination."))
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(storeLocation, 16f))

        // 위치 권한 확인 후 내 위치 표시 활성화
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap?.isMyLocationEnabled = true // "내 위치" 버튼 활성화

            // 현재 위치 가져오기
            mFusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    currentPosition = location
                    val currentLatLng = LatLng(location.latitude, location.longitude)

                    // 현재 위치에 카메라 이동
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16f))

                    // 현재 위치 마커 추가
                    googleMap?.addMarker(
                        MarkerOptions()
                            .position(currentLatLng)
                            .title("Now")

                    )
                } else {
                    Log.e("MapFragment", "현재 위치를 가져올 수 없습니다.")
                }
            }
        } else {
            Log.e("MapFragment", "위치 권한이 없습니다.")
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371e3
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        googleMap?.let { map ->
            val latLng = LatLng(currentPosition.latitude, currentPosition.longitude)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        } ?: Log.e("MapFragment", "GoogleMap is not initialized")
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putDouble("currentLatitude", currentPosition.latitude)
        outState.putDouble("currentLongitude", currentPosition.longitude)
    }


    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        savedInstanceState?.let {
            val lat = it.getDouble("latitude", 0.0)
            val lng = it.getDouble("longitude", 0.0)
            currentPosition.latitude = lat
            currentPosition.longitude = lng
        }

        googleMap?.let { map ->
            val currentLatLng = LatLng(currentPosition.latitude, currentPosition.longitude)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
        }
    }
}
