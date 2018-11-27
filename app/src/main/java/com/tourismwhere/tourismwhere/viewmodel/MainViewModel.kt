package com.tourismwhere.tourismwhere.viewmodel

import android.app.Activity
import android.arch.lifecycle.ViewModel
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import com.tourismwhere.tourismwhere.Constant
import com.tourismwhere.tourismwhere.Constant.LOCATION_PERMISSION_REQUEST_CODE
import com.tourismwhere.tourismwhere.Constant.TOKEN
import com.tourismwhere.tourismwhere.di.Production
import com.tourismwhere.tourismwhere.model.AttractionModel
import com.tourismwhere.tourismwhere.network.ApiService
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject


class MainViewModel @Inject constructor(@param:Production private val mApiServiceProduction: ApiService) : ViewModel() {
    val mPermissionState: BehaviorSubject<ResultPermission> by lazy { BehaviorSubject.create<ResultPermission>() }
    val mAttractionPublish: PublishSubject<AttractionResult> by lazy { PublishSubject.create<AttractionResult>() }

    private var mGetAttractionsDisposable: Disposable? = null
    private var mEnableCheckingFromServer: Boolean = true

    enum class PermissionState { PERMISSION_ON, PERMISSION_OFF }
    object ResultPermission {
        var permissionState: PermissionState = MainViewModel.PermissionState.PERMISSION_OFF
        var message = "no permission granted."
    }

    enum class FetchAttractionState { SUCCESS, FAILED }
    object AttractionResult {
        var fetchState: FetchAttractionState = MainViewModel.FetchAttractionState.FAILED
        var message = "no data."
        var attractionModel: ArrayList<AttractionModel>? = null
    }

    fun requestPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    fun setResultPermission(
        permissionState: PermissionState = PermissionState.PERMISSION_OFF,
        message: String = "no permission granted."
    ) {
        val result = ResultPermission
        result.message = message
        result.permissionState = permissionState
        mPermissionState.apply { onNext(result) }
    }

    fun checkSession(activity: Activity) {
        if (ActivityCompat.checkSelfPermission(
                activity,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            setResultPermission(PermissionState.PERMISSION_OFF, "no permission granted.")
        } else {
            setResultPermission(PermissionState.PERMISSION_ON, "permission granted.")
        }
    }

    fun getAttractions(LatLng: String, radius: String) {
        val internet = checkInternet()
        if (internet && mEnableCheckingFromServer) {
            mGetAttractionsDisposable?.run { if (!isDisposed) dispose() }
            mGetAttractionsDisposable = mApiServiceProduction.run {
                getAttractions(TOKEN, LatLng, radius)
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe { mEnableCheckingFromServer = false }
                    .doOnTerminate { mEnableCheckingFromServer = true }
                    .subscribe({ response ->
                        if (response.status == "success") {
                            setAttractionResult(
                                FetchAttractionState.SUCCESS,
                                "fetch data success.",
                                response.result.venues
                            )
                        } else {
                            setAttractionResult(
                                FetchAttractionState.SUCCESS,
                                "request not success.",
                                response.result.venues
                            )
                        }
                    }, { error ->
                        setAttractionResult(
                            FetchAttractionState.SUCCESS,
                            "$error",
                            null
                        )
                    })
            }
        }
    }

    private fun checkInternet(): Boolean {
        return Constant.isInternetConnectionAvailable()
    }

    private fun setAttractionResult(
        fetchState: FetchAttractionState = FetchAttractionState.FAILED,
        message: String,
        attractions: ArrayList<AttractionModel>?
    ) {
        val result = AttractionResult
        result.fetchState = fetchState
        result.message = message
        result.attractionModel = attractions
        mAttractionPublish.onNext(result)
    }
}