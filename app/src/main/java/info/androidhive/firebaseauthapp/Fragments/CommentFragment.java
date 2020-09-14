package info.androidhive.firebaseauthapp.Fragments;


import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.adapter.CommentAdapter;
import info.androidhive.firebaseauthapp.models.Comments;

// TODO: 2020/9/9 同時輸入會產生問題
public class CommentFragment extends BottomSheetDialogFragment {

    private String postId;
    LinearLayoutManager layoutManager;
    private ArrayList<Object> commentList;
    private ArrayList<Comments> oldcommentList = new ArrayList<>();
    private TextView tv_title;
    private EditText et_comment;
    private RecyclerView recycler_comment;
    private ImageView btn_pickimage;
    private ImageView btn_send;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    static CommentFragment instance;

    CommentAdapter adapter;

    //使用singleton會造成資源重複仔入
//    public static CommentFragment getInstance() {
//        if (instance == null){
//            instance = new CommentFragment();
//        }
//        return instance;
//    }

    public CommentFragment() {
        // Required empty public constructor
    }

//在CommentFragment 中設置evenValueListener ，食時間聽該post 的comments 並將其反映在recyclerview 上

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);





    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView =  inflater.inflate(R.layout.fragment_comment, container, false);
        //get posting id
        postId = this.getArguments().getString("id");
        Toast.makeText(itemView.getContext(), ""+postId, Toast.LENGTH_SHORT).show();
        //init views
        tv_title = itemView.findViewById(R.id.tv_title);
        et_comment = itemView.findViewById(R.id.et_comment);
        recycler_comment = itemView.findViewById(R.id.recycler_comment);
        btn_pickimage = itemView.findViewById(R.id.btn_pickimage);
        btn_send = itemView.findViewById(R.id.btn_send);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        layoutManager = new LinearLayoutManager(getContext());
        recycler_comment.setLayoutManager(layoutManager);
        recycler_comment.hasFixedSize();
        oldcommentList.clear();
        readDataComment();
        adapter = new CommentAdapter(getContext(),oldcommentList);
        recycler_comment.setAdapter(adapter);



        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Comments comments = new Comments();
                comments.setComment(et_comment.getText().toString());
                comments.setCommentTime(System.currentTimeMillis());
                comments.setUser_avatar(firebaseAuth.getCurrentUser().getPhotoUrl().toString());
                comments.setUser_Id(firebaseAuth.getCurrentUser().getUid());
                comments.setUser_name(firebaseAuth.getCurrentUser().getDisplayName());

                commentList.add(comments);
                databaseReference.child("posting").child(postId).child("comments").setValue(commentList).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.e("comments","ok");
                            recycler_comment.smoothScrollToPosition(adapter.getItemCount());
                        }
                    }
                });

                et_comment.setText("");
            }
        });
        return itemView;
    }
    // TODO:解決新增留言會重複仔入
    // TODO:fragment每重新開啟一次，新輸入的留言便會重複幾次
    private void readDataComment() {
        databaseReference.child("posting").child(postId).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ClearAll();

                if(dataSnapshot.hasChild("comments")){
                    commentList = (ArrayList<Object>)dataSnapshot.child("comments").getValue();
                    Log.e("comment size is",""+dataSnapshot.child("comments").getValue().toString());

                    ArrayList<Comments> adapterlist = new ArrayList<>();

                    for (Object o:commentList){
                        Map<String, Object> map = (Map<String, Object>) o;
                        Comments c = new Comments((long)map.get("commentTime"),(String) map.get("comment"),(String)map.get("user_avatar"),(String)map.get("user_name"),(String)map.get("user_Id"));
                        adapterlist.add(c);
                    }
                    //分成新舊兩隔 list只要將新list與舊list相比有差異的地方加進去adapter即可
                    Log.e("new list",adapterlist.size()+"");
                    Log.e("old list befire assigned",oldcommentList.size()+"");

                    if(oldcommentList.size()!= 0){

                        //第二次以後當有心資料讀入時，此時只要將new list加入就好
                        Log.e("inserted list",adapterlist.size()+"");
                        Log.e("inserted list",adapterlist.get(adapterlist.size()-1).getComment()+"");
                        oldcommentList.add(oldcommentList.size(),adapterlist.get(adapterlist.size()-1));
                        adapter.notifyItemInserted(oldcommentList.size());
                    }else{

                        //第一次載入時，old list的長度為0，故心就沒有差別之分
                        oldcommentList.addAll(adapterlist);
                        Log.e("old list after assigned",oldcommentList.size()+"");
                        //adapter設定為舊list
                        //adapter.notifyDataSetChanged()不起作用? 解決:https://blog.csdn.net/like_program/article/details/52517119

                        adapter.isShimmer = false;
                        adapter.notifyDataSetChanged();
                        recycler_comment.scrollToPosition(adapter.getItemCount()-1);
                    }





                }else{

                    adapter.isShimmer = false;
                    adapter.notifyDataSetChanged();
                    Log.e("no comments","no comments");
                    if(commentList==null){
                        Log.e("commentlist","list is null");
                        commentList = new ArrayList<>();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();

        if (dialog != null) {
            View bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
            bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        View view = getView();
        view.post(() -> {
            View parent = (View) view.getParent();
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) (parent).getLayoutParams();
            CoordinatorLayout.Behavior behavior = params.getBehavior();
            BottomSheetBehavior bottomSheetBehavior = (BottomSheetBehavior) behavior;
            bottomSheetBehavior.setPeekHeight(view.getMeasuredHeight());
        });
    }

    public void ClearAll(){
        if (commentList != null ){
            commentList.clear();
        }
        commentList = new ArrayList<>();

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("commentlist","pause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("commentlist","stop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("commentlist","destroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e("commentlist","detach");
    }
}