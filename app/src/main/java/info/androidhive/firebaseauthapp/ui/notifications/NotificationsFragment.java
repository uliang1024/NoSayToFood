package info.androidhive.firebaseauthapp.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import javax.annotation.Nullable;

import info.androidhive.firebaseauthapp.R;

/**
 * Created by Belal on 1/23/2018.
 */

public class NotificationsFragment extends Fragment {
    GridView gridView;
    String[] numbers = {"在家輕鬆動","啞鈴鍊肌","重量訓練","有氧瑜珈","暖身操"};
    int [] pics ={R.drawable.exercise,R.drawable.gym,R.drawable.gym2,R.drawable.healthy,R.drawable.workout};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment_notifications = inflater.inflate(R.layout.fragment_notifications, container, false);
        //INIT VIEWS
        init(fragment_notifications);

        CustomAdapter adapter = new CustomAdapter();
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(),"pointing item: "+numbers[position],Toast.LENGTH_SHORT).show();
            }
        });

        return fragment_notifications;
    }
    private void init(View v) {
        gridView = v.findViewById(R.id.grid_view);
    }

    private class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return numbers.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.raw_data,null);
            TextView tv_grid_item = view.findViewById(R.id.tv_grid_item);
            ImageView img_grid_item = view.findViewById(R.id.img_grid_item);

            tv_grid_item.setText(numbers[position]);
            img_grid_item.setImageResource(pics[position]);

            return  view;
        }
    }
}