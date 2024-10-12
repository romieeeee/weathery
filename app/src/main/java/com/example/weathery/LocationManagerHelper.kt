package com.example.weathery

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

/**
 * 위치 서비스와 관련된 기능을 제공하는 헬퍼 클래스
 * - 위치 서비스 초기화
 * - 권한 체크 및 요청
 * - 위치 업데이트 요청
 * - 권한 처리 결과 핸들링
 */
class LocationManagerHelper(private val context: Context, private val listener: LocationListener) {

    // 위치 관리자 객체 (위치 관련 서비스를 관리)
    private var locationManager: LocationManager? = null

    /**
     * 위치 관리자 초기화 함수
     * - 시스템 서비스로부터 위치 서비스를 가져와 locationManager에 할당
     */
    fun initializeLocationmanager(){
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    /**
     * 위치 권한을 체크하고, 권한이 없다면 요청하는 함수
     * @param fragment 위치 권한 요청을 수행할 Fragment
     * @return 권한이 이미 허용되었는지 여부 (허용: true, 미허용: false)
     */
    fun checkPermissions(fragment: Fragment): Boolean {
        // 위치 권한이 부여되었는지 확인
        return if (ActivityCompat.checkSelfPermission(
                context, ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                context, ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 만약 권한이 없다면, 사용자에게 권한을 요청
            fragment.requestPermissions(
                arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION),
                100
            )
            false // 권한이 허용되지 않음
        } else {
            true // 권한이 이미 허용됨
        }
    }

}