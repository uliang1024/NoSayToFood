package info.androidhive.firebaseauthapp.manager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.utils.CommonUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;



import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.UpdatePostingActivity;
import info.androidhive.firebaseauthapp.adapter.PicturePostAdapter;
import info.androidhive.firebaseauthapp.models.Item;
import info.androidhive.firebaseauthapp.models.PicturePost;
import info.androidhive.firebaseauthapp.models.PicturePostGridImage;
import info.androidhive.firebaseauthapp.models.TextPost;
import info.androidhive.firebaseauthapp.models.VideoPost;
import info.androidhive.firebaseauthapp.util.ScrollCalculatorHelper;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import static info.androidhive.firebaseauthapp.util.Constants.POST_TYPE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragUpdate#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragUpdate extends Fragment implements PicturePostAdapter.OnItemClickedListener{

    //綜合的item
    private DatabaseReference myRef;
    List<Item> items = new ArrayList<>();;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView mRecyclerView;
    boolean mFull = false;
    LinearLayoutManager layoutManager;
    ScrollCalculatorHelper scrollCalculatorHelper;
    Context mContext;
    PicturePostAdapter adapter;
    PicturePostAdapter.OnItemClickedListener clickedListener;
    TextView tv_noData;


    private OnUpdateFragmentListener mListener;
    private Button btn_return_update;

    public FragUpdate() {
        // Required empty public constructor
    }



    public static FragUpdate newInstance() {
        FragUpdate fragment = new FragUpdate();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_frag_update, container, false);
        btn_return_update = view.findViewById(R.id.btn_return_update);

        //INIT VIEWS
        init(view);
        items.clear();
        //讀取資料
        addData(new DataListener() {
            @Override
            public void onReceiveData(boolean dataLoadComplete,int loadedDataSize) {

                //如果收到的資料筆數!=0
                if (dataLoadComplete && loadedDataSize!= 0){
                    //先暫停兩秒等待shimmer動畫效果播出
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            adapter.isShimmer = false;
                            adapter.notifyDataSetChanged();
                        }
                    },300);
                    Log.e("dataload","ok");
                }
                //如果接收到的資料筆數為0
                else{
                    adapter.isShimmer = false;
                    adapter.notifyDataSetChanged();
                    tv_noData.setVisibility(View.VISIBLE);
                    Toast.makeText(mContext, "更新列表中沒有貼文喔", Toast.LENGTH_LONG).show();
                }

            }
        });
        //下滑刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                items.clear();
                adapter.notifyDataSetChanged();
                addData(new DataListener() {
                    //如果接收資料成功
                    @Override
                    public void onReceiveData(boolean dataLoadComplete,int loadedDataSize) {
                        //Log.e("load status","status "+dataLoadComplete +" loadedItem "+loadedItemSize);
                        //如果收到的資料筆數!=0
                        if (dataLoadComplete && loadedDataSize!= 0){
                            adapter.isShimmer = false;
                            adapter.notifyDataSetChanged();
                            Log.e("dataload","ok");
                        }
                        //如果接收到的資料筆數為0
                        else{
                            adapter.isShimmer = false;
                            adapter.notifyDataSetChanged();
                            tv_noData.setVisibility(View.VISIBLE);
                            Toast.makeText(mContext, "更新列表中沒有貼文喔", Toast.LENGTH_LONG).show();
                        }

                    }
                });

                swipeRefreshLayout.setRefreshing(false);
            }
        });
        clickedListener = this::onItemClicked;


        //自定播放帮助类
        int playTop = CommonUtil.getScreenHeight(mContext) / 2 - CommonUtil.dip2px(mContext, 200);
        int playBottom = CommonUtil.getScreenHeight(mContext) / 2 + CommonUtil.dip2px(mContext, 200);
        scrollCalculatorHelper = new ScrollCalculatorHelper(R.id.video_item_player, playTop, playBottom);

        //============================================================
        layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutManager);

        adapter = new PicturePostAdapter(mContext,items);
        adapter.setOnItemClickedListener(clickedListener);
        mRecyclerView.setAdapter(adapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int firstVisibleItem, lastVisibleItem;
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                scrollCalculatorHelper.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                firstVisibleItem   = layoutManager.findFirstVisibleItemPosition();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                int position = GSYVideoManager.instance().getPlayPosition();

                //这是滑动自动播放的代码
                if (!mFull) {
                    scrollCalculatorHelper.onScroll(recyclerView, firstVisibleItem, lastVisibleItem, lastVisibleItem - firstVisibleItem);
                }

                if (GSYVideoManager.instance().getPlayPosition() >= 0) {
                    //当前播放的位置

                    //对应的播放列表TAG
                    if ( (position < firstVisibleItem || position > lastVisibleItem)) {
                        GSYVideoManager.releaseAllVideos();
                        adapter.notifyItemChanged(position);

                    }
                }
            }

        });

        btn_return_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBack("ok update");
            }
        });
        //將左右滑動加入recyclerview
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        return view;
    }

    //處理左滑右滑的動作
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getAbsoluteAdapterPosition();

            switch (direction){
                case ItemTouchHelper.LEFT:
                    Item updatedItem= items.get(position);
                    writetoNormal(updatedItem, new AddNormalListener() {
                        @Override
                        public void onDataAddToNormal(boolean dataAddToNormalComplete) {
                            if (dataAddToNormalComplete){
                                items.remove(items.get(position));
                                adapter.notifyItemRemoved(position);
                                Snackbar.make(mRecyclerView,"已將貼文移除更新頁面", BaseTransientBottomBar.LENGTH_LONG)
                                        .show();

                            }
                        }
                    });
                    break;
            }
        }


        //滑動時的背景
        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(),R.color.light_green))
                    .addSwipeLeftActionIcon(R.drawable.ic_checked)
                    .setSwipeLeftLabelColor(R.color.colorBlack)
                    .setSwipeLeftLabelTextSize(TypedValue.COMPLEX_UNIT_SP,20)
                    .addSwipeLeftLabel("移除更新清單")

                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private void writetoNormal(Item item, AddNormalListener listener) {
        String postId ="";
        if (item.getObject() instanceof TextPost){
            postId = ((TextPost) item.getObject()).getPostID();
            myRef.child("posting").child(postId).child("toUpdate").setValue(0).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        listener.onDataAddToNormal(true);
                    }else{
                        Toast.makeText(getContext(),"something wrong",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else if (item.getObject() instanceof PicturePost){
            postId = ((PicturePost) item.getObject()).getPostID();
            myRef.child("posting").child(postId).child("toUpdate").setValue(0).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        listener.onDataAddToNormal(true);
                    }else{
                        Toast.makeText(getContext(),"something wrong",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            postId = ((VideoPost) item.getObject()).getPostID();
            myRef.child("posting").child(postId).child("toUpdate").setValue(0).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        listener.onDataAddToNormal(true);
                    }else{
                        Toast.makeText(getContext(),"something wrong",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void sendBack(String sendBackText) {
        if (mListener != null) {
            mListener.onFragmentUpdateTouched(sendBackText);
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragDelete.OnDeleteFragmentListener) {
            mListener = (OnUpdateFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    private void addData(DataListener dataListener) {


        myRef.addListenerForSingleValueEvent (new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("posting")){
                    //取得共幾筆資料
                    HashMap<String,Object> loadedItems = (HashMap<String,Object>) dataSnapshot.child("posting").getValue();

                    if (loadedItems == null||loadedItems.size() == 0){
                        return;
                    }
                    Log.e("loaded list size update",loadedItems.size()+"");
                    int loadCounter= 0;

                    items.clear();
                    //Toast.makeText(mContext, "count"+count , Toast.LENGTH_SHORT).show();
                    for(DataSnapshot postSnapShot:dataSnapshot.child("posting").getChildren()){

                        if(postSnapShot.hasChild(POST_TYPE)){
                            int type = Integer.parseInt(postSnapShot.child(POST_TYPE).getValue().toString());
                            if(type ==0){
                                if ((long)postSnapShot.child("toUpdate").getValue() ==1 ){
                                    PicturePost p = postSnapShot.getValue(PicturePost.class);
                                    items.add(new Item(0,p));
                                }
                                loadCounter++;

                            }
                            if(type==1){
                                if ((long)postSnapShot.child("toUpdate").getValue() ==1 ){
                                    TextPost t = postSnapShot.getValue(TextPost.class);
                                    items.add(new Item(1,t));
                                }
                                loadCounter++;

                            }
                            else if(type==2){
                                if ((long)postSnapShot.child("toUpdate").getValue() ==1 ){
                                    VideoPost v = postSnapShot.getValue(VideoPost.class);
                                    items.add(new Item(2,v));
                                }
                                loadCounter++;

                            }


                        }
                        else{
                            Toast.makeText(mContext, "no..." , Toast.LENGTH_SHORT).show();
                        }
                        //adapter.notifyDataSetChanged(); //notify

                    }

                    if (loadCounter == loadedItems.size()){
                        Collections.reverse(items);
                        //回傳加載成功，以及收到的資料筆數
                        dataListener.onReceiveData(true,items.size());
                    }
                }else{
                    adapter.isShimmer = false;
                    adapter.notifyDataSetChanged();
                    tv_noData.setVisibility(View.VISIBLE);
                    Toast.makeText(mContext, "更新列表中沒有貼文喔", Toast.LENGTH_LONG).show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void init(View v) {
        btn_return_update = v.findViewById(R.id.btn_return_update);
        tv_noData = v.findViewById(R.id.tv_noData_update);
        swipeRefreshLayout = v.findViewById(R.id.swipe_refresh_update);
        items = new ArrayList<>();
        mRecyclerView = v.findViewById(R.id.recycler_view_update);
        mContext = v.getContext();
        myRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onItemClicked(int position) {
        Intent postingIntent = new Intent(getActivity(), UpdatePostingActivity.class);

        //post type 2 = video, 0=images 1=text
        if (items.get(position).getObject() instanceof TextPost){
            TextPost textPost = (TextPost)items.get(position).getObject();
            Bundle bundle = new Bundle();
            bundle.putParcelable("post",textPost);
            postingIntent.putExtras(bundle);
            startActivity(postingIntent);
        }
        else if (items.get(position).getObject() instanceof PicturePost){
            PicturePost picturePost = (PicturePost)items.get(position).getObject();

            Bundle bundle = new Bundle();
            bundle.putParcelable("post",picturePost);
            bundle.putParcelableArrayList("images",picturePost.getImages());
            postingIntent.putExtras(bundle);
            startActivity(postingIntent);
        }
        else if (items.get(position).getObject() instanceof VideoPost){
            VideoPost videoPost = (VideoPost)items.get(position).getObject();
            Bundle bundle = new Bundle();
            bundle.putParcelable("post",videoPost);
            postingIntent.putExtras(bundle);
            startActivity(postingIntent);
        }
    }

    interface DataListener{
        void onReceiveData(boolean dataLoadComplete,int loadedDataSize);
    }

    public interface OnUpdateFragmentListener {

        void onFragmentUpdateTouched(String sendBackText);
    }


    interface AddNormalListener{
        void onDataAddToNormal(boolean dataAddToNormalComplete);
    }

}