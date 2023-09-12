package com.example.bitacora;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetallesActividadesFragment extends Fragment {

    private MapView mapView;

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
        TextView tvEstadoActividad = view.findViewById(R.id.tvEstadoActividad);
        TextView tvFechaInicio = view.findViewById(R.id.tvFechaInicio);
        TextView tvFechaFinalizado = view.findViewById(R.id.tvFechaFinalizado);
        mapView= view.findViewById(R.id.mapView);
        ViewPagerImagenesEvidencia = view.findViewById(R.id.ViewPagerImagenesEvidencia);

        Bundle bundle = getArguments();
        if (bundle != null) {
            String ID_actividad = bundle.getString("ID_actividad", "");
            String ID_usuario = bundle.getString("ID_usuario", "");
            String estadoActividad = bundle.getString("estadoActividad", "");
            String fecha_fin = bundle.getString("fecha_fin", "");
            String fecha_inicio = bundle.getString("fecha_inicio", "");
            String nombre_actividad = bundle.getString("nombre_actividad", "");
            String descripcionActividad = bundle.getString("descripcionActividad", "");


            // Crear un objeto SimpleDateFormat para el formato deseado
            SimpleDateFormat formatoOriginal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat formatoDeseado = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy ' a las' HH:mm", Locale.getDefault());

            try {
                Date fecha = formatoOriginal.parse(fecha_inicio);
                String fechaFormateada = formatoDeseado.format(fecha);
                Date fechafin = formatoOriginal.parse(fecha_fin);
                String fechaFormateadafin = formatoDeseado.format(fechafin);

                tvFechaInicio.setText("Fecha de inicio: \n" + fechaFormateada);
                tvFechaFinalizado.setText("Fecha de finalizacion: \n" + fechaFormateadafin);

            } catch (Exception e) {
                e.printStackTrace();
            }

            tvNombreActividad.setText(nombre_actividad);
            tvDetallesActividad.setText(descripcionActividad);
            tvEstadoActividad.setText(estadoActividad);

        }


        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                // Configura tu mapa aquí

                // Habilitar controles UI (botones de zoom, brújula, etc.)
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.getUiSettings().setCompassEnabled(true);

                // Establecer el tipo de mapa (normal, satélite, terreno, etc.)
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                // Configurar marcadores, líneas, polígonos, etc.
                LatLng point1 = new LatLng(37.7749, -122.4194); // Coordenadas del primer punto
                LatLng point2 = new LatLng(34.0522, -118.2437); // Coordenadas del segundo punto

                // Agregar marcadores
                googleMap.addMarker(new MarkerOptions().position(point1).title("Punto 1"));
                googleMap.addMarker(new MarkerOptions().position(point2).title("Punto 2"));

                // Utilizar un ViewTreeObserver para esperar a que la vista del mapa se complete
                mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // Remover el listener después de la primera llamada para evitar duplicados
                        mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        // Mover la cámara para mostrar los marcadores
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        builder.include(point1);
                        builder.include(point2);
                        LatLngBounds bounds = builder.build();
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100)); // 100 es el padding
                    }
                });
            }
        });

        return view;
    }




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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }




}