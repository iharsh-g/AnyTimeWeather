package com.android.example.anytimeweather.Models;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {

    MutableLiveData<String> mutableLiveData = new MutableLiveData<>();

    public void setString(String n){
        mutableLiveData.setValue(n);
    }

    public MutableLiveData<String> getString(){
        return mutableLiveData;
    }

}
