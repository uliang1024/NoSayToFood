package info.androidhive.firebaseauthapp.classModels;

import java.util.List;
import info.androidhive.firebaseauthapp.classModels.ClassdataEntity;
public class FitClass {

    private String className;
    private String classImage;
    private List<ClassdataEntity> classData;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassImage() {
        return classImage;
    }

    public void setClassImage(String classImage) {
        this.classImage = classImage;
    }

    public List<ClassdataEntity> getClassData() {
        return classData;
    }

    public void setClassData(List<ClassdataEntity> classData) {
        this.classData = classData;
    }
}
