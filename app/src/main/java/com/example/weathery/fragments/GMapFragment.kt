package com.example.weathery.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weathery.R
import com.example.weathery.adapter.CityAdapter
import com.example.weathery.data.WeatherDataProcessor
import com.example.weathery.database.CityEntity
import com.example.weathery.database.CityWithWeather
import com.example.weathery.database.DatabaseProvider
import com.example.weathery.repository.WeatherRepository
import com.example.weathery.utils.ApiKey
import com.example.weathery.utils.LocationManager
import com.example.weathery.utils.WeatheryManager
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "main function"

class GMapFragment : Fragment(), OnMapReadyCallback {

    // recyclerView
    private lateinit var recyclerView: RecyclerView
    private lateinit var cityAdapter: CityAdapter
    private lateinit var cityWeatherList: MutableList<CityWithWeather>

    private lateinit var locationManager: LocationManager
    private lateinit var weatheryManager: WeatheryManager

    // google map
    private lateinit var mapView: MapView
    private var googleMap: GoogleMap? = null

    // 마커의 정보 저장
    private var selectedLatLng: LatLng? = null
    private var selectedCityName: String? = null

    // database
    private val db by lazy { DatabaseProvider.getDatabase(requireContext()) }
    private val cityDao by lazy { db.cityDao() } // CityDao 초기화
    private val weatherDao by lazy { db.weatherDao() } // WeatherDao 초기화
    private val weatherRepository by lazy {
        WeatherRepository(
            cityDao,
            weatherDao
        )
    }

    private val weatherDataList = mutableListOf<WeatherDataProcessor>() // 날씨 데이터 리스트
    private val cityNames = mutableListOf<String>() // 도시 이름 리스트

    // 가시성 제어를 위한 변수 추가
    private var isMapVisible = false

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

        cityAdapter = CityAdapter(mutableListOf(), cityNames)
        recyclerView.adapter = cityAdapter

        locationManager = LocationManager(requireContext())
        weatheryManager = WeatheryManager(cityDao, weatherDao, weatherRepository)

        // init mapView
        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        // DB에서 도시와 날씨 정보를 가져와 RecyclerView 업데이트
        loadCityWeatherData()

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
    private fun saveCityToDatabase() {
        selectedLatLng?.let { latLng ->
            selectedCityName?.let { cityName ->
                CoroutineScope(Dispatchers.IO).launch {
                    // 도시 정보 저장 (cityId 반환)
                    val cityId = weatheryManager.saveCity(cityName, latLng.latitude, latLng.longitude)

                    // 날씨 데이터 가져오기
                    val weatherData = weatheryManager.fetchWeatherData(cityId, latLng.latitude, latLng.longitude)

                    weatherData?.let { data ->
                        weatherDataList.add(data)
                        cityNames.add(cityName)

                        withContext(Dispatchers.Main) {
                            cityAdapter.updateData(weatherDataList, cityNames)
                            loadCityWeatherData() // 업데이트된 도시 날씨 데이터 로드

                        }
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadCityWeatherData() {
        Log.d(TAG, "loadCityWeatherData() called")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val citiesWithWeather = cityDao.getCitiesWithWeather() // DB에서 데이터 가져오기

                // UI 업데이트
                withContext(Dispatchers.Main) {
                    weatherDataList.clear() // 기존 데이터 초기화
                    cityNames.clear() // 도시 이름 리스트 초기화

                    citiesWithWeather.forEach { cityWithWeather ->
                        weatherDataList.add(cityWithWeather.weatherData) // 날씨 데이터 추가
                        cityNames.add(cityWithWeather.cityName) // 도시 이름 추가
                    }

                    cityAdapter.updateData(weatherDataList, cityNames) // 어댑터에 변화 알림

                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading city and weather data: ${e.message}")
            }
        }
    }

    // 장소 검색창 자동완성 설정 함수
    private fun setupAutoComplete() {
        // Places API 초기화
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), ApiKey.P_API_KEY)
        }
        val autocompleteFragment = childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        autocompleteFragment.setPlaceFields(
            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
        )

        // 마커 선택 시 호출되는 메소드
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                val latLng = place.latLng
                if (latLng != null) {
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    googleMap?.addMarker(MarkerOptions().position(latLng).title(locationManager.getCityNameFromCoord(latLng.latitude, latLng.longitude)))
                }
                toggleViews()
            }

            override fun onError(status: com.google.android.gms.common.api.Status) {
                Log.e("GMapFragment", "Error selecting place: ${status.statusMessage}")
            }
        })
    }

    // recyclerview, mapview 가시성 전환 메소드
    private fun toggleViews() {
        isMapVisible = !isMapVisible
        if (isMapVisible) {
            recyclerView.visibility = View.GONE
            mapView.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            mapView.visibility = View.GONE
        }
    }

    // 다이얼로그를 띄워서 도시 추가 여부 확인
    private fun showAddCityDialog() {
        selectedCityName?.let { cityName ->
            // 다이얼로그 생성
            AlertDialog.Builder(requireContext())
                .setTitle("도시 추가")
                .setMessage("${cityName}을(를) 추가하시겠습니까?")
                .setPositiveButton("네") { dialog, _ ->
                    saveCityToDatabase()
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
