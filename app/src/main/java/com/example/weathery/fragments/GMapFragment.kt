package com.example.weathery.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weathery.R
import com.example.weathery.adapter.GMapAdapter
import com.example.weathery.database.DatabaseProvider
import com.example.weathery.database.WeatherEntity
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
    private lateinit var cityAdapter: GMapAdapter
    private var weatherDataList: MutableList<WeatherEntity> = mutableListOf()
    private var cityNames: MutableList<String> = mutableListOf()

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
    private val weatherRepository by lazy { WeatherRepository(cityDao, weatherDao) }

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
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        weatherDataList = mutableListOf()
        cityNames = mutableListOf()
        cityAdapter = GMapAdapter(weatherDataList, cityNames)

        recyclerView.adapter = cityAdapter

        locationManager = LocationManager(requireContext())
        weatheryManager = WeatheryManager(cityDao, weatherDao, weatherRepository)

        // init mapView
        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        // DB에서 도시와 날씨 정보를 가져와 RecyclerView 업데이트
        loadWeatherData()

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
    private fun saveCity() {
        selectedLatLng?.let { latLng ->
            selectedCityName?.let { cityName ->
                CoroutineScope(Dispatchers.IO).launch {
                    // 도시 정보 저장 (cityId 반환)
                    val cityId = weatheryManager.saveCity(cityName, latLng.latitude, latLng.longitude)

                    // 날씨 데이터 가져오기
                    val weatherData = weatheryManager.fetchWeatherData(cityId, latLng.latitude, latLng.longitude)
                    weatherData?.let { data ->
                        // UI 업데이트
                        weatherDataList.add(data)
                        cityNames.add(cityName)

                        withContext(Dispatchers.Main) {
                            cityAdapter.updateData(weatherDataList, cityNames)
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadWeatherData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 모든 도시의 최신 날씨 데이터를 가져옴
                val cities = cityDao.getAllCities()
                weatherDataList.clear()
                cityNames.clear()

                // 각 도시의 cityId로 weather 테이블에서 날씨 데이터를 가져옴
                cities.forEach { city ->
                    val weatherEntity = weatherDao.getLatestWeatherByCityId(city.cityId)
                    weatherEntity?.let {
                        // 3. WeatherEntity에서 필요한 데이터를 직접 가져옴
                        weatherDataList.add(it) // WeatherEntity를 바로 추가
                        cityNames.add(city.cityName) // 도시 이름 추가
                    }
                }

                // UI 업데이트
                withContext(Dispatchers.Main) {
                    cityAdapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading weather data: ${e.message}")
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
                    saveCity()
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
