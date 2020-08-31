package info.androidhive.firebaseauthapp.ImageEdit.InterFace;

public interface EditImageFragmentListener {
    void onBrightnessChanged(int brightness);
    void onContrastChanged(float contrast);
    void onSaturationChanged(float saturation);

    void onEditStarted();
    void onEditCompleted();

    void onFilterCleared();
}
