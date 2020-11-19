package info.androidhive.firebaseauthapp.models;

import java.util.List;

public  class Recipe {

    private String recipeName;
    private String recipeImage;
    private List<StepsEntity> steps;

    public Recipe() {
    }

    public Recipe(String recipename, String recipeimage, List<StepsEntity> steps) {
        this.recipeName = recipename;
        this.recipeImage = recipeimage;
        this.steps = steps;
    }

    public String getRecipename() {
        return recipeName;
    }

    public void setRecipename(String recipename) {
        this.recipeName = recipename;
    }

    public String getRecipeimage() {
        return recipeImage;
    }

    public void setRecipeimage(String recipeimage) {
        this.recipeImage = recipeimage;
    }

    public List<StepsEntity> getSteps() {
        return steps;
    }

    public void setSteps(List<StepsEntity> steps) {
        this.steps = steps;
    }



}
