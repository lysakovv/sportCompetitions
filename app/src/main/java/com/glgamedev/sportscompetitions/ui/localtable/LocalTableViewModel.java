package com.glgamedev.sportscompetitions.ui.localtable;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LocalTableViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public LocalTableViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}