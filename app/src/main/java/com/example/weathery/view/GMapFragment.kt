package com.example.weathery.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.weathery.R
import com.example.weathery.data.local.CityEntity
import com.example.weathery.data.local.DatabaseProvider
import com.example.weathery.data.repository.WeatherRepository
import com.example.weathery.utils.ApiKey
import com.example.weathery.utils.LocationManager
import com.example.weathery.viewmodel.HomeViewModel
import com.example.weathery.viewmodel.HomeViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "GMapFragment"

class GMapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var locationManager: LocationManager

    // google map
    private lateinit var mapView: MapView
    private var googleMap: GoogleMap? = null

    // 마커의 정보 저장
    private var selectedLatLng: LatLng? = null
    private var selectedCityName: String? = null

    private val homeViewModel: HomeViewModel by activityViewModels {
        HomeViewModelFactory(
            WeatherRepository(DatabaseProvider.getDatabase(requireContext()).cityDao())
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gmap, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // init
        locationManager = LocationManager(requireContext())

        // init mapView
        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        // 장소 자동완성 기능도 추가
        setupAutoComplete()
    }

    // Google Map이 준비되었을 때 호출되는 콜백 메서드
    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // 마커 클릭 리스너 추가
        googleMap?.setOnMarkerClickListener { marker ->
            selectedLatLng = marker.position
            selectedCityName = marker.title
            showAddCityDialog() // 다이얼로그 표시
            true // 마커 클릭시 기본 동작을 방지하기 위해 true 반환
        }
    }

    // 선택된 도시를 로컬 DB에 저장하고 날씨 데이터를 가져와 저장
    @SuppressLint("NotifyDataSetChanged")
    private fun saveSelectedCity() {
        Log.d(TAG, "saveSelectedCity :: called")
        selectedLatLng?.let {
            selectedCityName?.let { cityName ->
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    // 도시 정보 저장 및 cityId 반환
                    homeViewModel.addCity(
                        CityEntity(
                            cityName = cityName,
                            latitude = it.latitude,
                            longitude = it.longitude
                        )
                    )

                    withContext(Dispatchers.Main) {
                        findNavController().navigate(R.id.action_GMapFragment_to_homeFragment)
                    }
                }
            }
        }
    }

    // 장소 검색창 자동완성 설정 함수
    private fun setupAutoComplete() {
        // Places API 초기화
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), ApiKey.P_API_KEY)
        }
        val autocompleteFragment =
            childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        autocompleteFragment.setPlaceFields(
            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
        )

        // 마커 선택 시 호출되는 메소드
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                val latLng = place.latLng
                if (latLng != null) {
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    googleMap?.addMarker(
                        MarkerOptions().position(latLng).title(
                            locationManager.getCityNameFromCoord(
                                latLng.latitude,
                                latLng.longitude
                            )
                        )
                    )
                }
            }

            override fun onError(status: com.google.android.gms.common.api.Status) {
                Log.e("GMapFragment", "Error selecting place: ${status.statusMessage}")
            }
        })
    }

    // 다이얼로그를 띄워서 도시 추가 여부 확인
    private fun showAddCityDialog() {
        selectedCityName?.let { cityName ->
            // 다이얼로그 생성
            AlertDialog.Builder(requireContext())
                .setTitle("도시 추가")
                .setMessage("${cityName}을(를) 추가하시겠습니까?")
                .setPositiveButton("네") { dialog, _ ->
                    saveSelectedCity()
                    dialog.dismiss()
                }
                .setNegativeButton("아니요") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    // Fragment 생명주기에 맞춰 MapView 관리
    override fun onStart() {
        super.onStart()
        mapView.onStart()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
        (activity as AppCompatActivity).supportActionBar?.show() // 액션바 다시 보이기
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroyView() {
        mapView.onDestroy()
        super.onDestroyView()
    }
}
