package com.example.weathery.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weathery.R
import com.example.weathery.adapter.CityAdapter
import com.example.weathery.database.CityEntity
import com.example.weathery.database.CityWithWeather
import com.example.weathery.database.DatabaseProvider
import com.example.weathery.utils.ApiKey
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GMapFragment : Fragment(), OnMapReadyCallback {

    // recyclerView
    private lateinit var recyclerView: RecyclerView
    private lateinit var cityAdapter: CityAdapter
    private lateinit var cityWeatherList: MutableList<CityWithWeather>

    // google map
    private lateinit var mapView: MapView
    private var googleMap: GoogleMap? = null

    private val db by lazy { DatabaseProvider.getDatabase(requireContext()) }

    private val cityDao by lazy { db.cityDao() } // CityDao 초기화

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gmap, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // init
        recyclerView = view.findViewById(R.id.city_recycler_view)
        cityWeatherList = mutableListOf() // 초기화된 리스트

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        cityAdapter = CityAdapter(cityWeatherList)
        recyclerView.adapter = cityAdapter

        // init mapView
        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        // DB에서 도시와 날씨 정보를 가져와 RecyclerView 업데이트
        loadCityWeatherData()

        // 장소 자동완성 기능도 추가 (기존 코드 유지)
        setupAutoComplete()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadCityWeatherData() {
        Log.d("project-weathery", "loadCityWeatherData() called")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val citiesWithWeather = cityDao.getCitiesWithWeather() // DB에서 데이터 가져오기

                withContext(Dispatchers.Main) {
                    // UI 업데이트
                    cityWeatherList.clear() // 기존 데이터 초기화
                    cityWeatherList.addAll(citiesWithWeather) // 새 데이터 추가
                    cityAdapter.notifyDataSetChanged() // 어댑터에 변화 알림
                }
            } catch (e: Exception) {
                Log.e("GMapFragment", "Error loading city and weather data: ${e.message}")
            }
        }
    }

    // Google Map이 준비되었을 때 호출되는 콜백 메서드
    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // 기본 마커 설정 (서울)
        val seoul = LatLng(37.568291, 126.997780)
        googleMap?.addMarker(MarkerOptions().position(seoul).title("서울"))
        googleMap?.moveCamera(newLatLngZoom(seoul, 15f))
    }

    // 장소 자동완성 설정 함수 (기존과 동일하게 유지)
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

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                val latLng = place.latLng
                if (latLng != null) {
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    googleMap?.addMarker(MarkerOptions().position(latLng).title(place.name))

                    // 선택된 도시를 로컬 DB에 저장
                    val cityEntity = CityEntity(
                        cityName = place.name ?: "Unknown",
                        latitude = latLng.latitude,
                        longitude = latLng.longitude
                    )

                    CoroutineScope(Dispatchers.IO).launch {
                        cityDao.insertCity(cityEntity)

                        // UI 업데이트는 메인 스레드에서 실행
                        withContext(Dispatchers.Main) {
                            // 새로운 데이터를 가져오고 UI 업데이트
                            loadCityWeatherData()
                        }
                    }
                }
            }

            override fun onError(status: com.google.android.gms.common.api.Status) {
                Log.e("GMapFragment", "Error selecting place: ${status.statusMessage}")
            }
        })
    }


    // Fragment 생명주기에 맞춰 MapView 관리
    override fun onStart() {
        super.onStart()
        mapView.onStart()
        (activity as AppCompatActivity).supportActionBar?.hide() // 액션바 숨기기
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
