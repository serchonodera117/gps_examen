package com.example.gps_examen

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import java.io.IOException


class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var map:GoogleMap
    lateinit var locacionCliente: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createFragment()
                                         //---------- busqueda por nombre
        search_name?.setOnClickListener {
           buscarNombre()
        }
                                          //---------- busqueda por coordenadas
        search_coor.setOnClickListener {
           var busqueda:String = busqueda_nombre.text.toString()
            if(u_latitud.text.isNotEmpty() && u_longitud.text.isNotEmpty()) {
                var latitud: Double = u_latitud.text.toString().toDouble()
                var longitud: Double = u_longitud.text.toString().toDouble()
                buscarCoordenadas(latitud, longitud)
            }

        }
                                              //-------- ubicación del dispositivo
        ubicacion_dispositivo.setOnClickListener {
                ubicacionDispositivo()
        }

    }

    private  fun createFragment(){
        val mapFragment:SupportMapFragment = supportFragmentManager.findFragmentById(R.id.mapa) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
                                          //---- cargar mapa
    override fun onMapReady(p0: GoogleMap) {
        map = p0
    }
                                                             //---------- buscar por coordenada
    private fun buscarCoordenadas(Latitud:Double, Longitud:Double){
        var coordenadas = LatLng(Latitud, Longitud)

        var addressList: List<Address>? = null
        var geocoder = Geocoder(this)
        addressList = geocoder.getFromLocation(Latitud, Longitud, 1)
        var busqueda = addressList!![0]
       setBusqueda(coordenadas, busqueda.thoroughfare.toString())
        datos.setText("Busqueda: " + busqueda.getAddressLine(0).toString())



    }
                         //------------ buscar por nombre
    private fun buscarNombre(){
        if(busqueda_nombre.text.isNotEmpty()){
            if(busqueda_nombre.text.toString() == "Mi dispositivo") ubicacionDispositivo()
            else{
             try {
                 var addressList: List<Address>? =null

                 var locacion = busqueda_nombre.text.toString()
                 var geocoder = Geocoder(this)
                 addressList = geocoder.getFromLocationName(locacion, 1)

                 var direccion = addressList!![0]
                 var latLng = LatLng(direccion.latitude, direccion.longitude)
                 datos.setText("Busqueda: " + direccion.getAddressLine(0).toString())

                 setBusqueda(latLng, locacion)


             }catch (e: IOException) {
                e.printStackTrace()
                 Toast.makeText(this, "El nombre dio problemas Xd", Toast.LENGTH_SHORT).show()

             }
            }
    }
  }

                                       //------- ubicación del dispositivo
     private fun ubicacionDispositivo(){
         locacionCliente = LocationServices.getFusedLocationProviderClient(this)

         var lat:Double = 0.0
         var long:Double = 0.0
                                                    //-------- permisos
        var task:Task<Location> = locacionCliente.lastLocation
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
        != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 101)
            return
        }
        task.addOnSuccessListener {
            if(it != null){
                lat = it.latitude.toDouble()
                long = it.longitude.toDouble()
                //------------------- coordenadas
                var myCoor = LatLng(lat,long)
                setBusqueda(myCoor, "Mi dispositivo")
                datos.setText("Mi dispositivo")
            }
        }
    }                                                          //------------------ realizar la busqueda con el marcador-
   private fun setBusqueda(coordenadas:LatLng, busqueda:String){
       var marcador:MarkerOptions = MarkerOptions().position(coordenadas).title(busqueda)
       map.addMarker(marcador)
       map.animateCamera(
           CameraUpdateFactory.newLatLngZoom(coordenadas, 18f),
           4000,
           null
       )
       datos1.setText("Latitud: " + coordenadas.latitude)
       datos2.setText("Longitud: " + coordenadas.longitude)
   }

}