package com.example.bitacora;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
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
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.android.PolyUtil;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

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

public class DetallesActividadesFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;
    private ArrayList<Ubicaciones> markerList = new ArrayList<>();


    private double LATITUD;
    private double LONGITUD;

    String apiKey = "AIzaSyCkF9dXkDa3GjKlrLUdLc7BEx5031MELDQ";

    String url = "http://192.168.1.113/milton/bitacoraPHP/mostrar.php";

    String urlApi = "http://192.168.1.124/android/mostrar.php";

    private Handler sliderHandler = new Handler();

    ViewPager2 ViewPagerImagenesEvidencia;

    public static DetallesActividadesFragment newInstance(String param1, String param2) {
        DetallesActividadesFragment fragment = new DetallesActividadesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

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

        TextView tvNombreActividad = view.findViewById(R.id.tvNombreActividad);
        TextView tvDetallesActividad = view.findViewById(R.id.tvDetallesActividad);
        TextView tvNombreDeUsuario = view.findViewById(R.id.tvNombreDeUsuario);
        TextView tvTelefonoUsuario = view.findViewById(R.id.tvTelefonoUsuario);
        TextView tvEstadoActividad = view.findViewById(R.id.tvEstadoActividad);
        TextView tvFechaInicio = view.findViewById(R.id.tvFechaInicio);
        TextView tvFechaFinalizado = view.findViewById(R.id.tvFechaFinalizado);


        mapView = view.findViewById(R.id.mapView);
        TextView evidenciasDeActividad = view.findViewById(R.id.evidenciasDeActividad);

        ViewPagerImagenesEvidencia = view.findViewById(R.id.ViewPagerImagenesEvidencia);

        int colorBlanco = ContextCompat.getColor(requireContext(), R.color.white);
        int colorAmarillo = ContextCompat.getColor(requireContext(), R.color.amarillo);
        int colorVerde = ContextCompat.getColor(requireContext(), R.color.verde);
        int colorRojo = ContextCompat.getColor(requireContext(), R.color.rojo);
        int colorAzulito = ContextCompat.getColor(requireContext(), R.color.azulitoSuave);
        int colorNegro = ContextCompat.getColor(requireContext(), R.color.black);
        int colorGris = ContextCompat.getColor(requireContext(), R.color.gris);

        Bundle bundle = getArguments();
        if (bundle != null) {
            String ID_actividad = bundle.getString("ID_actividad", "");
            String ID_usuario = bundle.getString("ID_usuario", "");
            String estadoActividad = bundle.getString("estadoActividad", "");
            String fecha_fin = bundle.getString("fecha_fin", "");
            String fecha_inicio = bundle.getString("fecha_inicio", "");
            String nombre_actividad = bundle.getString("nombre_actividad", "");
            String descripcionActividad = bundle.getString("descripcionActividad", "");

            CargarUbicaciones(ID_actividad);

            String nombre = bundle.getString("nombre", "");
            String correo = bundle.getString("correo", "");
            String telefono = bundle.getString("telefono", "");
            String foto_usuario = bundle.getString("foto_usuario", "");


            tvNombreDeUsuario.setText(nombre);
            tvTelefonoUsuario.setText(telefono);

            CargarImagenes(ID_actividad);


            // Crear un objeto SimpleDateFormat para el formato deseado
            SimpleDateFormat formatoOriginal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat formatoDeseado = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy ' a las' HH:mm", Locale.getDefault());

            try {
                Date fecha = formatoOriginal.parse(fecha_inicio);
                String fechaFormateada = formatoDeseado.format(fecha);

                tvFechaInicio.setText("Fecha de inicio: \n" + fechaFormateada);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Date fechafin = formatoOriginal.parse(fecha_fin);
                String fechaFormateadafin = formatoDeseado.format(fechafin);
                tvFechaFinalizado.setText("Fecha de finalizacion: \n" + fechaFormateadafin);

            } catch (Exception e) {
                e.printStackTrace();
            }

            tvNombreActividad.setText(nombre_actividad);
            tvDetallesActividad.setText(descripcionActividad);
            tvEstadoActividad.setText(estadoActividad);
        }


        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return view;
    }


    private void CargarImagenes(String ID_actividad) {

        StringRequest stringRequest3 = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        List<SlideItem> slideItems = new ArrayList<>();

                        if (!TextUtils.isEmpty(response) || !response.equals("No se encontraron evidencias")) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject fotoObj = jsonArray.getJSONObject(i);
                                    String nombreFoto = fotoObj.getString("nombreFoto");
                                    String fotoUrl = "http://192.168.1.113/milton/bitacoraPHP/fotos/";
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

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d("API Response", "Respuesta vacía");
                                ViewPagerImagenesEvidencia.setVisibility(View.GONE);
                            }
                        } else {
                            Log.d("API Response", "Respuesta vacía");
                            ViewPagerImagenesEvidencia.setVisibility(View.GONE);

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("API Error", "Error en la solicitud: " + error.getMessage());
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


    private void CargarUbicaciones(String ID_actividad) {
        StringRequest stringRequest3 = new StringRequest(Request.Method.POST, urlApi,
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
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d("API Response", "Respuesta vacía");
                            }
                        } else {
                            Log.d("API Response", "Respuesta vacía");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("API Error", "Error en la solicitud: " + error.getMessage());
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
            int padding = 50; // Espacio en píxeles desde los bordes del mapa para los marcadores
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            googleMap.animateCamera(cu);
        } else {
            // Manejar el caso en el que markerList esté vacío
            // Puedes centrar el mapa en una ubicación predeterminada o mostrar un mensaje de error, por ejemplo.
            Log.d("MARKERLIST: ", "El markerlist está vacío");
            mapView.setVisibility(View.GONE);
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

