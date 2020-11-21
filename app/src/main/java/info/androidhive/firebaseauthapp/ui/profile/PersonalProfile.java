package info.androidhive.firebaseauthapp.ui.profile;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import info.androidhive.firebaseauthapp.FastRecordsActivity;
import info.androidhive.firebaseauthapp.MainActivity;
import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.SQLite.PersonalInformation;
import info.androidhive.firebaseauthapp.first.HelloUser;

public class PersonalProfile extends AppCompatActivity {

    PersonalInformation myDb;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = null;

    private int user_exercise_level,age;
    private String gender;
    private float height,fat;

    private TextView tv_gender,tv_age,tv_height,tv_fat,tv_exercise;
    private ImageView iv_leftarrow;
    String[] exercise = {"久坐","輕量活動","中度活動量","高度活動量","非常高度活動量"};

    private NumberPicker numberPicker,numberPicker2;
    private PopupWindow popupWindow;
    private View workingAge_view,workingAge_view2;
    private Button submit_workingAge,btn_cancel,submit_workingAge2,btn_cancel2;
    private LinearLayout ll_gender,ll_age;
    String[] gender1 = {"男性","女性"};
    private String gender_data;
    private Integer age_data;
    private float height_data,fat_data;
    private int exercise_level;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalprofile);



        tv_gender = (TextView)findViewById(R.id.tv_gender);
        tv_age = (TextView)findViewById(R.id.tv_age);
        tv_height = (TextView)findViewById(R.id.tv_height);
        tv_fat = (TextView)findViewById(R.id.tv_fat);
        tv_exercise = (TextView)findViewById(R.id.tv_exercise);
        ll_gender = (LinearLayout)findViewById(R.id.ll_gender);
        ll_age = (LinearLayout)findViewById(R.id.ll_age);
        iv_leftarrow=(ImageView)findViewById(R.id.iv_leftarrow);

        uid = user.getUid();
        myDb = new PersonalInformation(this);
        Cursor res = myDb.getAllData();
        while (res.moveToNext()) {
            if(uid.equals(res.getString(0))){
                user_exercise_level = res.getInt(8);
                gender = res.getString(2);
                age = res.getInt(3);
                height = res.getFloat(4);
                fat = res.getFloat(7);
            }
        }
        gender_data=gender;
        age_data=age;
        height_data=height;
        fat_data=fat;
        exercise_level=user_exercise_level;
        tv_gender.setText(gender);
        tv_age.setText(String.valueOf(age));
        tv_exercise.setText(exercise[user_exercise_level]);
        tv_height.setText(String.valueOf(height));
        tv_fat.setText(String.valueOf(fat));

        initNumberPicker();
        initNumberPicker2();

        ll_gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 设置初始值
                numberPicker.setValue(0);

                // 强制隐藏键盘
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                // 填充布局并设置弹出窗体的宽,高
                popupWindow = new PopupWindow(workingAge_view,
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                // 设置弹出窗体可点击
                popupWindow.setFocusable(true);
                // 设置弹出窗体动画效果
                popupWindow.setAnimationStyle(R.style.AnimBottom);
                // 触屏位置如果在选择框外面则销毁弹出框
                popupWindow.setOutsideTouchable(true);
                // 设置弹出窗体的背景
                popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                popupWindow.showAtLocation(workingAge_view,
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

                // 设置背景透明度
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 0.5f;
                getWindow().setAttributes(lp);

                // 添加窗口关闭事件
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

                    @Override
                    public void onDismiss() {
                        WindowManager.LayoutParams lp = getWindow().getAttributes();
                        lp.alpha = 1f;
                        getWindow().setAttributes(lp);
                    }

                });
            }

        });
        ll_age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(PersonalProfile.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthofYear, int dayOfMonth) {
                        String strDate = year + "-" + monthofYear + "-" + dayOfMonth;
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date birthDay = null;
                        try {
                            birthDay = sdf.parse(strDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        int birth = countAge(birthDay);
                        if (birth<0) {
                            Toast.makeText(getApplicationContext(), "生日輸入有誤", Toast.LENGTH_SHORT).show();
                            tv_age.setText("");
                        } else {
                            tv_age.setText(String.valueOf(birth));
                            age_data = Integer.parseInt(tv_age.getText().toString());
                        }
                        age_data = Integer.parseInt(tv_age.getText().toString());
                    }
                    //設定初始的顯示日期
                }, 2000, 0, 1).show();
            }
        });

        submit_workingAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_gender.setText(gender1[numberPicker.getValue()]);
                popupWindow.dismiss();

                gender_data = tv_gender.getText().toString();
            }
        });
        submit_workingAge2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_exercise.setText(exercise[numberPicker2.getValue()]);
                popupWindow.dismiss();
                exercise_level = numberPicker2.getValue();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        btn_cancel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        iv_leftarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean updateData2 = false;
                updateData2 = myDb.updateData2(uid,
                        gender_data,
                        age_data,
                        height_data,
                        fat_data,
                        exercise_level);
                finish();
            }
        });


    }
    private int countAge(Date birthDay) {
        Calendar cal = Calendar.getInstance();

        if (cal.before(birthDay)) {
            throw new IllegalArgumentException(
                    "The birthDay is before Now.It's unbelievable!");
        }
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(birthDay);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH) + 1;
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

        int age = yearNow - yearBirth;
        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) age--;
            } else {
                age--;
            }
        }
        return age;

    }
    public void height(View view) {
        LayoutInflater inflater = LayoutInflater.from(PersonalProfile.this);
        final View v = inflater.inflate(R.layout.show_yourkg, null);
        new AlertDialog.Builder(PersonalProfile.this)
                .setTitle("請輸入你的身高")
                .setView(v)
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = (EditText) (v.findViewById(R.id.editText1));
                        if(editText.getText().toString().matches("")) {
                            tv_height.setText("");
                        }else{
                            tv_height.setText(editText.getText().toString());
                            height_data = Float.parseFloat(tv_height.getText().toString());
                        }
                    }
                })
                .show();
    }
    public void fat(View view) {
        LayoutInflater inflater = LayoutInflater.from(PersonalProfile.this);
        final View v = inflater.inflate(R.layout.show_yourkg, null);
        new AlertDialog.Builder(PersonalProfile.this)
                .setTitle("請輸入你的體脂率")
                .setView(v)
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = (EditText) (v.findViewById(R.id.editText1));
                        if(editText.getText().toString().matches("")) {
                            tv_fat.setText("");
                        }else{
                            tv_fat.setText(editText.getText().toString());
                            if (tv_fat.getText().toString() != null &&!tv_fat.getText().toString().isEmpty()&& !tv_fat.getText().toString().equals("null")) {
                                fat_data = Float.parseFloat(tv_fat.getText().toString());
                            }
                        }
                    }
                })
                .show();
    }
    public void exercise(final View view) {
        LayoutInflater inflater = LayoutInflater.from(PersonalProfile.this);
        final View v = inflater.inflate(R.layout.daily_activity_show, null);
        new AlertDialog.Builder(PersonalProfile.this)
                .setTitle("活動量表")
                .setView(v)
                .setPositiveButton("了解", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 设置初始值
                        numberPicker2.setValue(0);

                        // 强制隐藏键盘
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                        // 填充布局并设置弹出窗体的宽,高
                        popupWindow = new PopupWindow(workingAge_view2,
                                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        // 设置弹出窗体可点击
                        popupWindow.setFocusable(true);
                        // 设置弹出窗体动画效果
                        popupWindow.setAnimationStyle(R.style.AnimBottom);
                        // 触屏位置如果在选择框外面则销毁弹出框
                        popupWindow.setOutsideTouchable(false);
                        // 设置弹出窗体的背景
                        popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        popupWindow.showAtLocation(workingAge_view2,
                                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

                        // 设置背景透明度
                        WindowManager.LayoutParams lp = getWindow().getAttributes();
                        lp.alpha = 0.5f;
                        getWindow().setAttributes(lp);

                        // 添加窗口关闭事件
                        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

                            @Override
                            public void onDismiss() {
                                WindowManager.LayoutParams lp = getWindow().getAttributes();
                                lp.alpha = 1f;
                                getWindow().setAttributes(lp);
                            }

                        });
                    }
                })
                .show();
    }
    private void initNumberPicker() {
        workingAge_view = LayoutInflater.from(this).inflate(R.layout.popupwindow, null);
        btn_cancel = (Button)workingAge_view.findViewById(R.id.btn_cancel);
        submit_workingAge = (Button) workingAge_view.findViewById(R.id.submit_workingAge);
        numberPicker = (NumberPicker) workingAge_view.findViewById(R.id.numberPicker);
        numberPicker.setMaxValue(gender1.length-1);
        numberPicker.setMinValue(0);
        numberPicker.setDisplayedValues(gender1);
        numberPicker.setFocusable(false);
        numberPicker.setFocusableInTouchMode(false);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        setNumberPickerDividerColor(numberPicker);
    }
    private void initNumberPicker2() {
        workingAge_view2 = LayoutInflater.from(this).inflate(R.layout.popupwindow2, null);
        btn_cancel2 = (Button)workingAge_view2.findViewById(R.id.btn_cancel2);
        submit_workingAge2 = (Button) workingAge_view2.findViewById(R.id.submit_workingAge2);
        numberPicker2 = (NumberPicker) workingAge_view2.findViewById(R.id.numberPicker2);
        numberPicker2.setMaxValue(exercise.length-1);
        numberPicker2.setMinValue(0);
        numberPicker2.setDisplayedValues(exercise);
        numberPicker2.setFocusable(false);
        numberPicker2.setFocusableInTouchMode(false);
        numberPicker2.setWrapSelectorWheel(false);
        numberPicker2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        setNumberPickerDividerColor(numberPicker2);
    }
    private void setNumberPickerDividerColor(NumberPicker number) {
        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    //设置分割线的颜色值
                    pf.set(number, new ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimaryDark)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}