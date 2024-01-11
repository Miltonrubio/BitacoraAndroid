package com.bitala.bitacora;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bitala.bitacora.Adaptadores.AdaptadorListaActividades;
import com.bitala.bitacora.Adaptadores.AdaptadorUbicaciones;
import com.bitala.bitacora.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DetallesActividadesFragment extends Fragment implements OnMapReadyCallback, AdaptadorUbicaciones.OnActivityActionListener {

    private MapView mapView;
    private GoogleMap googleMap;
    private ArrayList<Ubicaciones> markerList = new ArrayList<>();


    private double LATITUD;
    private double LONGITUD;

    String url;

    private Handler sliderHandler = new Handler();
    List<SlideItem> slideItems = new ArrayList<>();

    int colorBlanco, colorAmarillo, colorVerde, colorRojo, colorAzulito, colorNegro, colorGris;
    ViewPager2 ViewPagerImagenesEvidencia;

    Context context;
    LinearLayout ContenedorEvidencias;
    LottieAnimationView animacionSinEvidencias;
    TextView textSinEvidencias;

    LinearLayout EvidenciaMapa;
    LinearLayout SinEvidenciasMapa;

    AdaptadorUbicaciones adaptadorUbicaciones;


    List<JSONObject> listaUbicaciones = new ArrayList<>();


    String ID_actividad;
    RecyclerView RecyclerViewUbicaciones;

    String fotoUrl;
    public DetallesActividadesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detalles_actividades, container, false);

        context = requireContext();
        url = context.getResources().getString(R.string.urlApi);
        fotoUrl = context.getResources().getString(R.string.fotoUrl);

        TextView tvNombreActividad = view.findViewById(R.id.tvNombreActividad);
        TextView tvDetallesActividad = view.findViewById(R.id.tvDetallesActividad);
        TextView tvEstadoActividad = view.findViewById(R.id.tvEstadoActividad);
        TextView tvFechaInicio = view.findViewById(R.id.tvFechaInicio);
        TextView tvFechaFinalizado = view.findViewById(R.id.tvFechaFinalizado);
        TextView evidenciasDeActividad = view.findViewById(R.id.evidenciasDeActividad);
        ViewPagerImagenesEvidencia = view.findViewById(R.id.ViewPagerImagenesEvidencia);
        TextView tvMotivoCancelacion = view.findViewById(R.id.tvMotivoCancelacion);
        LinearLayout tvCancelacion = view.findViewById(R.id.Cancelacion);
        textSinEvidencias = view.findViewById(R.id.textSinEvidencias);
        animacionSinEvidencias = view.findViewById(R.id.animacionSinEvidencias);
        ContenedorEvidencias = view.findViewById(R.id.ContenedorEvidencias);
        EvidenciaMapa = view.findViewById(R.id.EvidenciaMapa);
        SinEvidenciasMapa = view.findViewById(R.id.SinEvidenciasMapa);

        RecyclerViewUbicaciones = view.findViewById(R.id.RecyclerViewUbicaciones);


        adaptadorUbicaciones = new AdaptadorUbicaciones(listaUbicaciones, context, this);
        RecyclerViewUbicaciones.setLayoutManager(new LinearLayoutManager(context));
        RecyclerViewUbicaciones.setAdapter(adaptadorUbicaciones);


        colorBlanco = ContextCompat.getColor(context, R.color.white);
        colorAmarillo = ContextCompat.getColor(context, R.color.amarillo);
        colorVerde = ContextCompat.getColor(context, R.color.verde);
        colorRojo = ContextCompat.getColor(context, R.color.rojo);
        colorAzulito = ContextCompat.getColor(context, R.color.azulitoSuave);
        colorNegro = ContextCompat.getColor(context, R.color.black);
        colorGris = ContextCompat.getColor(context, R.color.gris);


        Bundle bundle = getArguments();
        if (bundle != null) {
            ID_actividad = bundle.getString("ID_actividad", "");
            String ID_usuario = bundle.getString("ID_usuario", "");
            String estadoActividad = bundle.getString("estadoActividad", "");
            String fecha_fin = bundle.getString("fecha_fin", "");
            String fecha_inicio = bundle.getString("fecha_inicio", "");
            String nombre_actividad = bundle.getString("nombre_actividad", "");
            String descripcionActividad = bundle.getString("descripcionActividad", "");
            String motivocancelacion = bundle.getString("motivocancelacion", "");


            String nombre = bundle.getString("nombre", "");
            String correo = bundle.getString("correo", "");
            String telefono = bundle.getString("telefono", "");
            String foto_usuario = bundle.getString("foto_usuario", "");


            CargarImagenes(ID_actividad);
            CargarUbicaciones(ID_actividad);
            mapView = view.findViewById(R.id.mapView);

            // Crear un objeto SimpleDateFormat para el formato deseado
            SimpleDateFormat formatoOriginal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat formatoDeseado = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy ' a las' HH:mm", Locale.getDefault());

            try {
                Date fecha = formatoOriginal.parse(fecha_inicio);
                String fechaFormateada = formatoDeseado.format(fecha);

                tvFechaInicio.setText("Inicio: \n" + fechaFormateada);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Date fechafin = formatoOriginal.parse(fecha_fin);
                String fechaFormateadafin = formatoDeseado.format(fechafin);

                if (estadoActividad.equalsIgnoreCase("Cancelado")) {
                    tvFechaFinalizado.setText("Cancelada el :" + fechaFormateadafin);
                    tvFechaFinalizado.setTextColor(colorRojo);
                } else if ((estadoActividad.equalsIgnoreCase("Finalizada"))) {
                    tvFechaFinalizado.setText("Finalizada el: " + fechaFormateadafin);
                } else {
                    tvFechaFinalizado.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                tvFechaFinalizado.setVisibility(View.GONE);
            }
            tvNombreActividad.setText(nombre_actividad);


            tvDetallesActividad.setText("Descripción de la actividad: \n" + descripcionActividad);

            if (estadoActividad.equalsIgnoreCase("Cancelado")) {
                tvEstadoActividad.setTextColor(colorRojo);
                tvCancelacion.setVisibility(View.VISIBLE);
                tvMotivoCancelacion.setVisibility(View.VISIBLE);
                tvMotivoCancelacion.setText(motivocancelacion);
            }
            tvEstadoActividad.setText(estadoActividad);

            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(this);
        }

        return view;
    }


    private void CargarImagenes(String ID_actividad) {
        slideItems.clear();
        StringRequest stringRequest3 = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (!TextUtils.isEmpty(response) || !response.equals("No se encontraron evidencias")) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject fotoObj = jsonArray.getJSONObject(i);
                                    String nombreFoto = fotoObj.getString("nombreFoto");
                                    slideItems.add(new SlideItem(fotoUrl + nombreFoto));
                                }

                                SlideAdapter slideAdapter = new SlideAdapter(slideItems, ViewPagerImagenesEvidencia);

                                ViewPagerImagenesEvidencia.setAdapter(slideAdapter);
                                ViewPagerImagenesEvidencia.setClipToPadding(false);
                                ViewPagerImagenesEvidencia.setClipChildren(false);
                                ViewPagerImagenesEvidencia.setOffscreenPageLimit(4);
                                ViewPagerImagenesEvidencia.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
                                CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
                                compositePageTransformer.addTransformer(new MarginPageTransformer(10));
                                compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
                                    @Override
                                    public void transformPage(@NonNull View page, float position) {
                                        float r = 1 - Math.abs(position);
                                        page.setScaleY(0.85f + 0.15f);
                                    }
                                });

                                ViewPagerImagenesEvidencia.setPageTransformer(compositePageTransformer);
                                ViewPagerImagenesEvidencia.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                                    public void onPageSelected(int position) {
                                        super.onPageSelected(position);
                                        sliderHandler.removeCallbacks(sliderRunnable);
                                        sliderHandler.postDelayed(sliderRunnable, 3000);
                                    }
                                });

                                if (slideItems.size() > 0) {

                                    OcultarYMostrar("Evidencias");
                                } else {

                                    OcultarYMostrar("SinEvidencias");
                                }


                            } catch (JSONException e) {

                                OcultarYMostrar("SinEvidencias");
                            }
                        } else {

                            Utils.crearToastPersonalizado(context, "No tienes conexión a internet, no se cargaran las evidencias");
                            OcultarYMostrar("SinEvidencias");

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utils.crearToastPersonalizado(context, "No tienes conexión a internet, no se cargaran las evidencias");
                        OcultarYMostrar("SinEvidencias");

                    }
                }
        ) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("opcion", "10");
                params.put("ID_actividad", ID_actividad);
                return params;
            }
        };

        RequestQueue requestQueue3 = Volley.newRequestQueue(requireContext());
        requestQueue3.add(stringRequest3);

    }


    private void OcultarYMostrar(String estado) {

        if (estado.equalsIgnoreCase("SinEvidencias")) {
            ContenedorEvidencias.setVisibility(View.GONE);
            animacionSinEvidencias.setVisibility(View.VISIBLE);
            textSinEvidencias.setVisibility(View.VISIBLE);
        } else {

            ContenedorEvidencias.setVisibility(View.VISIBLE);
            animacionSinEvidencias.setVisibility(View.GONE);
            textSinEvidencias.setVisibility(View.GONE);
        }
    }

    private void CargarUbicaciones(String ID_actividad) {
        listaUbicaciones.clear();
        StringRequest stringRequest3 = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!TextUtils.isEmpty(response) && !response.equals("fallo")) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject ubicacionObj = jsonArray.getJSONObject(i);
                                    String latitud = ubicacionObj.getString("latitud_actividad");
                                    String longitud = ubicacionObj.getString("longitud_actividad");
                                    String ID_ubicacion_actividad = ubicacionObj.getString("ID_ubicacion_actividad");

                                    LATITUD = Double.parseDouble(latitud);
                                    LONGITUD = Double.parseDouble(longitud);

                                    Log.d("Coordenadas", "Latitud: " + latitud + ", Longitud: " + longitud);
                                    markerList.add(new Ubicaciones(LATITUD, LONGITUD, "Numero de evidencia de ubicacion: " + ID_ubicacion_actividad));
                                    listaUbicaciones.add(ubicacionObj);
                                }


                                if (listaUbicaciones.size() > 0) {
                                    RecyclerViewUbicaciones.setVisibility(View.VISIBLE);
                                    SinEvidenciasMapa.setVisibility(View.GONE);
                                    mapView.setVisibility(View.VISIBLE);

                                } else {

                                    RecyclerViewUbicaciones.setVisibility(View.GONE);
                                    SinEvidenciasMapa.setVisibility(View.VISIBLE);
                                    mapView.setVisibility(View.GONE);

                                }


                                adaptadorUbicaciones.notifyDataSetChanged();
                                adaptadorUbicaciones.setFilteredData(listaUbicaciones);
                                adaptadorUbicaciones.filter("");


                            } catch (JSONException e) {


                                RecyclerViewUbicaciones.setVisibility(View.GONE);
                                SinEvidenciasMapa.setVisibility(View.VISIBLE);
                                mapView.setVisibility(View.GONE);

                            }
                        } else {
                            RecyclerViewUbicaciones.setVisibility(View.GONE);
                            SinEvidenciasMapa.setVisibility(View.VISIBLE);
                            mapView.setVisibility(View.GONE);

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        RecyclerViewUbicaciones.setVisibility(View.GONE);
                        SinEvidenciasMapa.setVisibility(View.VISIBLE);
                        mapView.setVisibility(View.GONE);
                    }
                }
        ) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("opcion", "19");
                params.put("ID_actividad", ID_actividad);
                return params;
            }
        };

        RequestQueue requestQueue3 = Volley.newRequestQueue(requireContext());
        requestQueue3.add(stringRequest3);
    }


    @Override
    public void onTomarCoordenadas(String latitud, String longitud) {

        double newLat = Double.parseDouble(latitud);
        double newLng = Double.parseDouble(longitud);

        LatLng newLatLng = new LatLng(newLat, newLng);
        String address = getAddressFromLatLng(newLat, newLng); // Puedes usar geocodificación inversa para obtener la dirección
        MarkerOptions markerOptions = new MarkerOptions()
                .position(newLatLng)
                .title(address);

        // Agrega el marcador al mapa
        googleMap.addMarker(markerOptions);

        // Mueve la cámara a la nueva ubicación
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(newLatLng));
    }


    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            ViewPagerImagenesEvidencia.setCurrentItem(ViewPagerImagenesEvidencia.getCurrentItem() + 1);
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (!markerList.isEmpty()) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Ubicaciones ubicacion : markerList) {
                double lat = ubicacion.getLatitud_inicio();
                double lng = ubicacion.getLongitud_inicio();
                LatLng latLng = new LatLng(lat, lng);
                builder.include(latLng);

                // Utiliza geocodificación inversa para obtener la dirección
                String address = getAddressFromLatLng(lat, lng);

                // Crea un marcador en el mapa con la dirección como título
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .title(address);

                // Agrega el marcador al mapa
                googleMap.addMarker(markerOptions);
            }
            LatLngBounds bounds = builder.build();

            // Espera a que el mapa se realice antes de ajustar la cámara
            googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    int padding = 50; // Espacio en píxeles desde los bordes del mapa para los marcadores
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    googleMap.animateCamera(cu);
                }
            });
        } else {
            // Manejar el caso en el que markerList esté vacío
            // Puedes centrar el mapa en una ubicación predeterminada o mostrar un mensaje de error, por ejemplo.
            Log.d("MARKERLIST: ", "El markerlist está vacío");
        }
    }


    // Método para obtener la dirección a partir de las coordenadas
    private String getAddressFromLatLng(double lat, double lng) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                // Formatea la dirección como desees, por ejemplo:
                return address.getAddressLine(0); // Obtén la primera línea de la dirección
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Dirección desconocida"; // En caso de error o si no se encuentra la dirección
    }


}

