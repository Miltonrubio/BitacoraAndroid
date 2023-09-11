package com.example.bitacora;
/*
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.paging.PositionalDataSource;

import java.util.ArrayList;
import java.util.List;

public class NombreActividadDataSource extends PositionalDataSource<NombreActividad> {

    private Context context;

    public NombreActividadDataSource(Context context) {
        this.context = context;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<NombreActividad> callback) {
        // Carga los datos iniciales aquí desde tu fuente de datos (por ejemplo, una API)

        // params.pageSize contiene el tamaño de página deseado
        // params.requestedStartPosition contiene la posición de inicio deseada

        int startPosition = params.requestedStartPosition;
        int pageSize = params.pageSize;

        // Realiza una solicitud a tu fuente de datos para cargar los datos iniciales
        List<NombreActividad> data = cargarDatosDesdeFuente(startPosition, pageSize);

        // Llama a callback.onResult() para proporcionar los datos cargados
        callback.onResult(data, startPosition);
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<NombreActividad> callback) {
        // Carga más datos aquí cuando el usuario se desplaza por la lista

        // params.startPosition contiene la posición de inicio deseada
        // params.loadSize contiene la cantidad de datos que se debe cargar

        int startPosition = params.startPosition;
        int loadSize = params.loadSize;

        // Realiza una solicitud a tu fuente de datos para cargar más datos
        List<NombreActividad> data = cargarDatosDesdeFuente(startPosition, loadSize);

        // Llama a callback.onResult() para proporcionar los datos cargados
        callback.onResult(data);
    }

    private List<NombreActividad> cargarDatosDesdeFuente(int startPosition, int pageSize) {
        // Implementa la lógica para cargar datos desde tu fuente de datos (por ejemplo, una API)
        // Retorna una lista de NombreActividad con los datos cargados
        // Asegúrate de ajustar esta lógica según tu fuente de datos real
        List<NombreActividad> data = new ArrayList<>();

        // Aquí debes realizar la solicitud a tu fuente de datos y obtener los datos

        return data;
    }
}
*/