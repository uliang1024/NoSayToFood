package info.androidhive.firebaseauthapp.ImageEdit.InterFace;

public interface BrushFragmentListener {
    void onBrushSizeChanged(float size);
    void onBrushOpacityChanged(int opacity);
    void onBrushColorChangeListener(int color);
    void onBrushStateChangeListener(boolean isEraser);
}
