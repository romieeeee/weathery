package com.example.weathery.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.weathery.R
import com.example.weathery.adapter.CityAdapter
import com.example.weathery.database.AppDatabase
import com.example.weathery.database.City
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
    private lateinit var itemList: MutableList<City>

    // google map
    private lateinit var mapView: MapView
    private var googleMap: GoogleMap? = null

    // Room 데이터베이스 초기화 (DB 연결을 위한 객체)
    private val db by lazy {
        Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "weather_table"
        ).build()
    }

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
        itemList = mutableListOf()

        cityAdapter = CityAdapter(itemList)
        recyclerView.adapter = cityAdapter

        // init mapView
        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)

        // 콜백을 통해 지도를 로드
        mapView.getMapAsync(this)

        // Places API 초기화
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), "YOUR_API_KEY")
        }

        // AutoCompleteSupportFragment 초기화 (자동완성 검색)
        val autocompleteFragment =
            childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        // 장소 검색 필터 설정
        autocompleteFragment.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG
            )
        )

        // 장소가 선택되면 호출되는 리스너
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onPlaceSelected(place: Place) {
                // 선택한 장소의 LatLng 좌표 가져오기
                val latLng = place.latLng
                if (latLng != null) {
                    // 카메라를 선택한 위치로 이동시키고, 마커 추가
                    googleMap?.moveCamera(newLatLngZoom(latLng, 15f))
                    googleMap?.addMarker(MarkerOptions().position(latLng).title(place.name))

                    // 선택한 도시 데이터를 로컬 DB에 저장
                    val cityEntity = City(
                        cityName = place.name ?: "Unknown",
                        latitude = latLng.latitude,
                        longitude = latLng.longitude
                    )

                    // 코루틴을 사용하여 비동기적으로 DB 작업 수행
                    CoroutineScope(Dispatchers.IO).launch {
                        cityDao.insertCity(cityEntity) // 도시 데이터를 DB에 저장

                        // UI 업데이트를 위한 작업은 메인 스레드에서 수행해야 함
                        withContext(Dispatchers.Main) {
                            itemList.add(cityEntity) // UI 목록에 추가
                            cityAdapter.notifyDataSetChanged() // 어댑터 업데이트
                        }
                    }
                }

                // 지도 보이기, RecyclerView 숨기기
                mapView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }

            override fun onError(status: com.google.android.gms.common.api.Status) {
                Log.e("GMAP", "${status.statusMessage}")
            }
        })
    }

    // Google Map이 준비되었을 때 호출되는 콜백 메서드
    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // 기본 마커 설정 (서울)
        val seoul = LatLng(37.568291, 126.997780)
        googleMap?.addMarker(MarkerOptions().position(seoul).title("서울"))
        googleMap?.moveCamera(newLatLngZoom(seoul, 15f))
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
