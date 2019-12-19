package fr.tchatat.gotoesig.ui.roads;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EvaluerTrajetViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public EvaluerTrajetViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}