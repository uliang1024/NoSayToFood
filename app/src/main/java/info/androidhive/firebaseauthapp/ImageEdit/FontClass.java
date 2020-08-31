package info.androidhive.firebaseauthapp.ImageEdit;

public class FontClass {

    String fontName;
    String fontPath;
    String language;

    public FontClass(String fontName, String fontPath, String language) {
        this.fontName = fontName;
        this.fontPath = fontPath;
        this.language = language;
    }

    public String getFontName() {
        return fontName;
    }

    public String getFontPath() {
        return fontPath;
    }

    public String getLanguage() {
        return language;
    }


}
