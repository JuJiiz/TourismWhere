package com.tourismwhere.tourismwhere.ui

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.annotation.DrawableRes
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.tourismwhere.tourismwhere.Constant
import com.tourismwhere.tourismwhere.Constant.LOCATION_PERMISSION_REQUEST_CODE
import com.tourismwhere.tourismwhere.R
import com.tourismwhere.tourismwhere.adapter.AttractionsAdapter
import com.tourismwhere.tourismwhere.model.AttractionModel
import com.tourismwhere.tourismwhere.safeLet
import com.tourismwhere.tourismwhere.ui.fragment.DetailFragment
import com.tourismwhere.tourismwhere.ui.fragment.PermissionDialogFragment
import com.tourismwhere.tourismwhere.ui.fragment.RadiusDialogFragment
import com.tourismwhere.tourismwhere.ui.fragment.SearchFragment
import com.tourismwhere.tourismwhere.viewmodel.MainViewModel
import com.tourismwhere.tourismwhere.viewmodel.ViewModelFactory
import dagger.android.AndroidInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import javax.inject.Inject

class MainActivity : AppCompatActivity(), OnMapReadyCallback, RadiusDialogFragment.OnRadiusSetting,
    AttractionsAdapter.OnItemClick, SearchFragment.OnItemClick {
    @Inject
    lateinit var mAdapter: AttractionsAdapter
    @Inject
    lateinit var mViewModelFactory: ViewModelFactory
    private lateinit var mViewModel: MainViewModel
    private var mPermissionStateDisposable: Disposable? = null
    private var mFetchDataStateDisposable: Disposable? = null
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var centerLocation: LatLng
    private var dialogFragment = PermissionDialogFragment()
    private var radius = 5
    var venues: ArrayList<AttractionModel> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(MainViewModel::class.java)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        rvAttractions.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvAttractions.adapter = mAdapter

        btnRefresh.setOnClickListener {
            getCurrentLocation()
        }

        btnRadius.setOnClickListener {
            if (supportFragmentManager.findFragmentByTag(RadiusDialogFragment.Tag) == null) {
                val radiusDialogFragment = RadiusDialogFragment()
                val bundle = Bundle()
                bundle.putInt("RADIUS", radius)
                radiusDialogFragment.arguments = bundle
                radiusDialogFragment.show(supportFragmentManager, RadiusDialogFragment.Tag)
            }
        }

        btnSearch.setOnClickListener {
            if (supportFragmentManager.findFragmentByTag(SearchFragment.Tag) == null) {
                val searchFragment = SearchFragment()
                val bundle = Bundle()
                bundle.putParcelableArrayList("ATTRACTION", venues)
                searchFragment.arguments = bundle

                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down)
                    .replace(android.R.id.content, searchFragment, SearchFragment.Tag)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    override fun onResume() {
        super.onResume()
        tvRadius.text = "$radius"
        disposeSessionState()
        mPermissionStateDisposable = mViewModel.mPermissionState.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                handleSessionState(it)
            }, {
                Timber.e("error on getting login result: ${it.message}")
            })
        mViewModel.checkSession(this)
    }

    override fun onPause() {
        disposeSessionState()
        stopObserveFetchData()
        super.onPause()
    }

    private fun handleSessionState(it: MainViewModel.ResultPermission) {
        //Timber.d("session_state: ${it.permissionState} message: ${it.message}" )
        when (it.permissionState) {
            MainViewModel.PermissionState.PERMISSION_OFF -> {
                if (supportFragmentManager.findFragmentByTag(PermissionDialogFragment.Tag) == null) {
                    dialogFragment.show(supportFragmentManager, PermissionDialogFragment.Tag)
                }
            }
            MainViewModel.PermissionState.PERMISSION_ON -> {
                if (supportFragmentManager.findFragmentByTag(PermissionDialogFragment.Tag) != null) {
                    dialogFragment.dismiss()
                }
                getCurrentLocation()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constant.LOCATION_PERMISSION_REQUEST_CODE) {
            permissions.forEachIndexed { index, permission ->
                if (grantResults[index] == PackageManager.PERMISSION_DENIED) {
                    val showRationale = shouldShowRequestPermissionRationale(permission)
                    if (!showRationale) {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivityForResult(intent, LOCATION_PERMISSION_REQUEST_CODE)
                    }
                } else {
                    mViewModel.setResultPermission(MainViewModel.PermissionState.PERMISSION_ON, "permission granted.")
                }
            }
        }
    }

    private fun disposeSessionState() {
        mPermissionStateDisposable?.apply { if (!isDisposed) dispose() }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        mMap.clear()
        if (MainViewModel.ResultPermission.permissionState == MainViewModel.PermissionState.PERMISSION_ON)
            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                if (location != null) {
                    centerLocation = LatLng(location.latitude, location.longitude)
                    mMap.addMarker(
                        MarkerOptions().position(centerLocation).title("Here").icon(
                            bitmapDescriptorFromVector(this, R.drawable.ic_person_pin)
                        )
                    )
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(centerLocation, 12F))
                    val currentLatLng = "${location.latitude},${location.longitude}"
                    val currentRadius = "$radius"
                    mViewModel.getAttractions(currentLatLng, currentRadius)
                    startObserveFetchData()
                }
            }
    }

    private fun bitmapDescriptorFromVector(context: Context, @DrawableRes vectorDrawableResourceId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId)
        vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap =
            Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private fun startObserveFetchData() {
        stopObserveFetchData()
        mFetchDataStateDisposable = mViewModel.mAttractionPublish
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                handleFetchedResult(it)
            }, { Timber.e("error on getting fetched result: ${it.message}") })
    }

    private fun handleFetchedResult(result: MainViewModel.AttractionResult?) {
        when (result?.fetchState) {
            MainViewModel.FetchAttractionState.SUCCESS -> {
                result.attractionModel?.run {
                    this.forEach {
                        safeLet(it.location?.latitude, it.location?.longitude) { latitude, longitude ->
                            mMap.addMarker(
                                MarkerOptions().position(
                                    LatLng(
                                        latitude,
                                        longitude
                                    )
                                ).title(if (it.name != null) it.name else "-")
                            )
                        }
                    }

                    venues = this
                    mAdapter.mCurrentLocation = centerLocation
                    mAdapter.mAttractionsList = this.reversed()
                    mAdapter.notifyDataSetChanged()

                    mMap.setOnInfoWindowClickListener { marker ->
                        val selected = venues.singleOrNull { attractionModel -> attractionModel.name == marker.title }
                        if (selected != null) showDetail(selected)
                    }
                }
            }
            MainViewModel.FetchAttractionState.FAILED -> {
                Snackbar.make(rootView, result.message, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun stopObserveFetchData() {
        mFetchDataStateDisposable?.run { if (!isDisposed) dispose() }
    }

    override fun updateRadius(newRadius: Int) {
        radius = newRadius
        tvRadius.text = "$radius"
        getCurrentLocation()
    }

    override fun showAttraction(attraction: AttractionModel) {
        showDetail(attraction)
    }

    private fun showDetail(attraction: AttractionModel) {
        if (supportFragmentManager.findFragmentByTag(DetailFragment.Tag) == null) {
            val detailFragment = DetailFragment()
            val bundle = Bundle()
            bundle.putParcelable("ATTRACTION", attraction)
            detailFragment.arguments = bundle

            supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down)
                .replace(android.R.id.content, detailFragment, DetailFragment.Tag)
                .addToBackStack(null)
                .commit()
        }
    }
}
