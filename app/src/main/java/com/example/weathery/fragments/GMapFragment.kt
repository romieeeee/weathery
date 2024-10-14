package com.example.weathery.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.weathery.R
import com.example.weathery.adapter.CityAdapter
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

class GMapFragment : Fragment(), OnMapReadyCallback {

    // recyclerView
    private lateinit var recyclerView: RecyclerView
    private lateinit var cityAdapter: CityAdapter
    private lateinit var itemList: MutableList<City>

    // google map
    private lateinit var mapView: MapView
    private var googleMap: GoogleMap? = null

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
        itemList = mutableListOf(
            City("서울시 종로구", (37.5665 to 126.9780), "22℃"),
            City("부산시 해운대구", (35.1796 to 129.0756), "23℃")
        )

        cityAdapter = CityAdapter(itemList)
        recyclerView.adapter = cityAdapter

        // init mapView
        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)

        // 콜백을 통해 지도를 로드
        mapView.getMapAsync(this)

        // Places API 초기화
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), "AIzaSyAlOHnrk2qe3bpt9STiuM_2UDVXBqCVgjI")
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
            override fun onPlaceSelected(place: Place) {
                // 선택한 장소의 LatLng 좌표 가져오기
                val latLng = place.latLng
                if (latLng != null) {
                    // 카메라를 선택한 위치로 이동시키고, 마커 추가
                    googleMap?.moveCamera(newLatLngZoom(latLng, 15f))
                    googleMap?.addMarker(MarkerOptions().position(latLng).title(place.name))
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
