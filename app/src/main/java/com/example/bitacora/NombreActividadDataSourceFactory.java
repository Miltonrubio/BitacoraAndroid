package com.example.bitacora;
/*
import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

public class NombreActividadDataSourceFactory extends DataSource.Factory<Integer, NombreActividad> {

    private MutableLiveData<NombreActividadDataSource> dataSourceLiveData = new MutableLiveData<>();
    private Context context;

    public NombreActividadDataSourceFactory(Context context) {
        this.context = context;
    }

    @Override
    public DataSource<Integer, NombreActividad> create() {
        NombreActividadDataSource dataSource = new NombreActividadDataSource(context);
        dataSourceLiveData.postValue(dataSource);
        return dataSource;
    }

    public MutableLiveData<NombreActividadDataSource> getDataSourceLiveData() {
        return dataSourceLiveData;
    }
}
*/