package com.example.weathery.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.weathery.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class GMapFragment : Fragment(), OnMapReadyCallback {

    // MapView를 늦은 초기화(lateinit)를 통해 나중에 초기화합니다.
    private lateinit var mapView: MapView
    private var googleMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // XML 파일을 통해 Fragment의 뷰를 생성합니다.
        return inflater.inflate(R.layout.fragment_gmap, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // MapView를 뷰에서 찾아서 초기화하고, 생명 주기를 맞춰줍니다.
        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)

        // MapView가 준비되면 콜백을 통해 지도를 로드합니다.
        mapView.getMapAsync(this)
    }

    // Google Map이 준비되었을 때 호출되는 콜백 메서드
    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // 마커를 표시할 위치 (서울의 좌표)
        val seoul = LatLng(37.568291, 126.997780)

        // 해당 위치에 마커를 추가합니다.
        googleMap?.addMarker(MarkerOptions().position(seoul).title("여기"))

        // 카메라를 지정된 좌표로 이동하면서 줌을 동시에 설정합니다.
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 15f))
    }

    // Fragment의 생명 주기에 맞춰 MapView의 생명 주기도 호출해줍니다.
    override fun onStart() {
        super.onStart()
        mapView.onStart()
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
