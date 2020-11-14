package info.androidhive.firebaseauthapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;

import java.util.List;

import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.models.StepsEntity;

public class RecipeStepPagerAdapter extends PagerAdapter {
    Context context;
    List<StepsEntity> steps ;

    public RecipeStepPagerAdapter(Context context, List<StepsEntity> steps) {
        this.context = context;
        this.steps = steps;
    }

    @Override
    public int getCount() {
        return steps.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.recipe_step_raw,container,false);

        ImageView step_img = view.findViewById(R.id.step_img);
        TextView step_name = view.findViewById(R.id.step_name);
        TextView step_description = view.findViewById(R.id.step_description);

        Glide.with(context).load(steps.get(position).getStepimage()).into(step_img);
        step_name.setText(steps.get(position).getStepname());
        step_description.setText(steps.get(position).getDescription());

        step_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "u clicked image:"+steps.get(position).getStepimage(), Toast.LENGTH_SHORT).show();
            }
        });

        container.addView(view);
        return view;
    }
}
