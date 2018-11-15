package com.example.dahye.wlb;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class addcategory extends Activity  implements View.OnClickListener
{
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    NumberPicker numberPicker;
    EditText Category, Score;
    Button addBtn;
    String id, name;
    int sc ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.addcategory);
        mDatabase = FirebaseDatabase.getInstance();

        Category = (EditText) findViewById(R.id.category);
        Score = (EditText) findViewById(R.id.score);
        addBtn= (Button) findViewById(R.id.addbtn);
        numberPicker = (NumberPicker)findViewById(R.id.numberpicker);
        name = Category.getText().toString();

        final int minValue = -10;
        final int maxValue = 10;
        numberPicker.setValue(0);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(maxValue-minValue);
        numberPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return Integer.toString(i+minValue);
            }
        });
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        setDividerColor(numberPicker, android.R.color.white);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {

            }
        });
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            id =user.getEmail();
            id = id.substring(0,id.indexOf("@"));
        }else{
            id = "sdhdonna";
            Toast.makeText(addcategory.this,"로그인해주세요",Toast.LENGTH_LONG).show();
        }
        mReference = mDatabase.getReference();
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sc = numberPicker.getValue() + minValue;
                mReference.child("categories").child(id).child(Category.getText().toString()).child("score").setValue(sc);
            }
        });

    }

    private void setDividerColor(NumberPicker numberPicker, int color) {
        java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for(java.lang.reflect.Field pf : pickerFields){
            if(pf.getName().equals("mSelectionDivider")){
                pf.setAccessible(true);
                try{
                    ColorDrawable colorDrawable = new ColorDrawable(color);
                    pf.set(numberPicker, colorDrawable);
                }catch(IllegalArgumentException e){
                    e.printStackTrace();;
                }catch(Resources.NotFoundException e){
                    e.printStackTrace();
                }catch(IllegalAccessException e){
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    @Override
    public void onClick(View view) {

    }

}
