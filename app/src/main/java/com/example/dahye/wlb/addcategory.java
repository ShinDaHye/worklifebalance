package com.example.dahye.wlb;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class addcategory extends Activity{
    private Button categoryadd;
    private PopupWindow mPopupWindow;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    private ListView listView;
    private CategoryAdapter adapter;
    List<CategoryItem> Array = new ArrayList<CategoryItem>();

    private String id;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addcategory);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar c1 = Calendar.getInstance();
        final String strToday = sdf.format(c1.getTime());


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            id =user.getEmail();
            id = id.substring(0,id.indexOf("@"));
        }else{
            id = "sdhdonna";
            Toast.makeText(addcategory.this,"로그인해주세요",Toast.LENGTH_LONG).show();
        }
        listView = (ListView) findViewById(R.id.listviewmsg);

        initDatabase(id);

        //버튼 이벤트 생성(팝업)
        categoryadd = findViewById(R.id.categoryadd);

        categoryadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View popupView = getLayoutInflater().inflate(R.layout.popupaddcategory,null);
               //팝업 크기 설정
                final EditText test_add = popupView.findViewById(R.id.category);
                final NumberPicker numberPicker = popupView.findViewById(R.id.numberpicker);
                Button addBtn = popupView.findViewById(R.id.addbtn);
                final int minValue = -10;
                final int maxValue = 10;
                numberPicker.setMinValue(0);
                numberPicker.setMaxValue(maxValue-minValue);
                numberPicker.setValue((maxValue-minValue)/2);
                numberPicker.setFormatter(new NumberPicker.Formatter() {
                    @Override
                    public String format(int i) {
                        return Integer.toString(i+minValue);
                    }
                });
                numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
//                setDividerColor(numberPicker, android.R.color.white);
                numberPicker.setWrapSelectorWheel(false);
                numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker numberPicker, int i, int i1) {

                    }
                });
                mPopupWindow = new PopupWindow(popupView,LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                //팝업 애니메이션 설정 -1: 설정
//                mPopupWindow.setAnimationStyle(-1);

                // 팝업 뷰 포커스
                mPopupWindow.setFocusable(true);
                //팝업 뷰 터치
                mPopupWindow.setTouchable(true);
                //팝업 뷰 이외에도 터치 되게(터치하면 팝업 닫기)
                mPopupWindow.setOutsideTouchable(true);
                mPopupWindow.showAtLocation(popupView, Gravity.CENTER,0,0);

                mReference = mDatabase.getReference();
                addBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int sc = numberPicker.getValue() + minValue;
                        String categoryName = test_add.getText().toString();
                        mReference.child("categories").child(id).child(categoryName).child("score").setValue(sc);
                        Splititem splititem = new Splititem();
                        splititem.setScore(sc);
                        splititem.setUnit("0");
                        mReference.child("split-score").child(id).child(strToday).child(categoryName).setValue(splititem);

                        mPopupWindow.dismiss();
                    }
                });
            }
        });
    }

    private void initDatabase(String id) {
        mDatabase = FirebaseDatabase.getInstance();

        mReference = mDatabase.getReference("categories").child(id); // 변경값을 확인할 child 이름

        mReference.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                adapter.clear();
                for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                    String msg1 = messageData.getKey().toString();
                    String msg2 = messageData.child("score").getValue().toString();
                    Array.add(new CategoryItem(msg1,msg2));
                    adapter.add(new CategoryItem(msg1,msg2));
                }
            }
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        adapter = new CategoryAdapter(this, R.layout.customlistview_category,R.id.category, new ArrayList<CategoryItem>());
        listView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
