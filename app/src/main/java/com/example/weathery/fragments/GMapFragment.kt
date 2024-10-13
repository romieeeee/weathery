package com.example.weathery.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.weathery.R
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

class GMapFragment : Fragment(), OnMapReadyCallback {

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

        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)


        // 콜백을 통해 지도를 로드
        mapView.getMapAsync(this)

        // Places 초기화
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), "AIzaSyAlOHnrk2qe3bpt9STiuM_2UDVXBqCVgjI")
        }

        // AutoCompleteSupportFragment 초기화
        val autocompleteFragment = childFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                as AutocompleteSupportFragment

        // 장소 검색 필터 설정 (예: 도시만 필터링하고 싶다면 여기에 설정 가능)
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
                // 선택한 장소의 좌표(LatLng)를 가져와 지도의 카메라를 이동
                val latLng = place.latLng
                if (latLng != null) {
                    googleMap?.moveCamera(newLatLngZoom(latLng, 15f))

                    // 선택한 장소에 마커 추가
                    googleMap?.addMarker(MarkerOptions().position(latLng).title(place.name))
                }
            }

            override fun onError(status: com.google.android.gms.common.api.Status) {
                // 에러 발생 시 Toast 메시지 출력
                Toast.makeText(
                    requireContext(),
                    "Error: ${status.statusMessage}",
                    Toast.LENGTH_LONG
                ).show()

                Log.e("GMAP", "${status.statusMessage}")
            }
        })
    }

    // Google Map이 준비되었을 때 호출되는 콜백 메서드
    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // 기본 마커 설정 (서울)
        val seoul = LatLng(37.568291, 126.997780)
        googleMap?.addMarker(MarkerOptions().position(seoul).title("여기"))
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 15f))
    }

    // Fragment의 생명 주기에 맞춰 MapView의 생명 주기도 호출
    override fun onStart() {
        super.onStart()
        mapView.onStart()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
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
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    // Fragment가 파괴될 때 MapView도 정리합니다.
    override fun onDestroyView() {
        mapView.onDestroy()
        super.onDestroyView()
    }

    // 메모리 누수를 방지하기 위해 MapView를 null로 설정합니다.
    override fun onDestroy() {
        super.onDestroy()
    }
}
