package com.example.dahye.wlb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CategoryAdapter_main extends ArrayAdapter {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    String id;

    private List<CategoryItem> items;
    private int rsrc;
    private Context context;

    @Override
    public void add(Object object) {
        super.add(object);
        items.add((CategoryItem)object);
    }

    @Override
    public void clear() {
        super.clear();
        items=new ArrayList<CategoryItem>();
    }

    public CategoryAdapter_main(Context context, int rsrcId, int txtId, List<CategoryItem> data) {
        super(context, rsrcId, txtId, data);
        this.context=context;
        this.rsrc = rsrcId;
        this.items = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final String today = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            id =user.getEmail();
            id = id.substring(0,id.indexOf("@"));
        }else{
            id = "sdhdonna";
        }

        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("split-score").child(id).child(today);

        final Context context = parent.getContext();

        /* 'listview_custom' Layout을 inflate하여 convertView 참조 획득 */
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.category_main, parent, false);
        }

        /* 'listview_custom'에 정의된 위젯에 대한 참조 획득 */
        final TextView category = (TextView) convertView.findViewById(R.id.main_category) ;
        final TextView score = (TextView) convertView.findViewById(R.id.main_score) ;
        final TextView unit = (TextView) convertView.findViewById(R.id.unit) ;

        final CategoryItem item = items.get(position);

        category.setText(item.getCategory());
        score.setText(item.getScore());
        unit.setText(item.getUnit());

        /* 버튼에 대한 이벤트 리스너 */
        ImageButton imgbtn_minus = (ImageButton)convertView.findViewById(R.id.main_imgbtn_minus);
        ImageButton imgbtn_plus = (ImageButton)convertView.findViewById(R.id.main_imgbtn_plus);


        imgbtn_minus.setOnClickListener(new ImageButton.OnClickListener(){
            public void onClick(View view){
                int origin_num = Integer.parseInt(unit.getText().toString());
                if(origin_num>0) {
                    unit.setText(Integer.toString(origin_num - 1));
                    mReference.child(item.getCategory()).child("unit").setValue(Integer.toString(origin_num - 1));
                }
            }
        });

        imgbtn_plus.setOnClickListener(new ImageButton.OnClickListener(){
            public void onClick(View view){
                int origin_num = Integer.parseInt(unit.getText().toString());
                if(origin_num<24) {
                    unit.setText(Integer.toString(origin_num + 1));
                    mReference.child(item.getCategory()).child("unit").setValue(Integer.toString(origin_num + 1));
                }
            }
        });

        return convertView;
    }


}