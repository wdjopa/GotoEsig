package fr.tchatat.gotoesig.views.fragments.trajets;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NouveauTrajetViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public NouveauTrajetViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}