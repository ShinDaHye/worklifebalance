package com.example.dahye.wlb;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CategoryAdapter extends ArrayAdapter {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    private PopupWindow mPopupWindow;
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

    public CategoryAdapter(Context context, int rsrcId, int txtId, List<CategoryItem> data) {
        super(context, rsrcId, txtId, data);
        this.items = data;
        this.rsrc = rsrcId;
        this.context=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        mDatabase = FirebaseDatabase.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        id =user.getEmail();
        id = id.substring(0,id.indexOf("@"));

        mReference = mDatabase.getReference();

        final Context context = parent.getContext();

        /* 'listview_custom' Layout을 inflate하여 convertView 참조 획득 */
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.customlistview_category, parent, false);
        }

        /* 'listview_custom'에 정의된 위젯에 대한 참조 획득 */
        final TextView category = (TextView) convertView.findViewById(R.id.category) ;
        final TextView score = (TextView) convertView.findViewById(R.id.score) ;

        final CategoryItem item = items.get(position);

        category.setText(item.getCategory());
        score.setText(item.getScore());

        /* 버튼에 대한 이벤트 리스너 */
        ImageButton imgbtn_minus = (ImageButton)convertView.findViewById(R.id.imgbtn_minus);
        ImageButton imgbtn_plus = (ImageButton)convertView.findViewById(R.id.imgbtn_plus);
        ImageButton confirm = (ImageButton)convertView.findViewById(R.id.imgbtn_delete);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar c1 = Calendar.getInstance();
        final String strToday = sdf.format(c1.getTime());

        imgbtn_minus.setOnClickListener(new ImageButton.OnClickListener(){
            public void onClick(View view){
                int origin_num = Integer.parseInt(score.getText().toString());

                if(origin_num>-10) {
                    String minus = Integer.toString(origin_num - 1);
                    score.setText(minus);
                    mReference.child("categories").child(id).child(item.getCategory()).child("score").setValue(minus);
                    mReference.child("split-score").child(id).child(strToday).child(item.getCategory()).child("score").setValue(minus);
                }
            }
        });


        imgbtn_plus.setOnClickListener(new ImageButton.OnClickListener(){
            public void onClick(View view){
                int origin_num = Integer.parseInt(score.getText().toString());
                if(origin_num<10) {
                    String plus = Integer.toString(origin_num + 1);
                    score.setText(plus);
                    mReference.child("categories").child(id).child(item.getCategory()).child("score").setValue(plus);
                    mReference.child("split-score").child(id).child(strToday).child(item.getCategory()).child("score").setValue(plus);
                }

            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder ad = new AlertDialog.Builder(getContext());

                ad.setTitle("카테고리 삭제");       // 제목 설정
                ad.setMessage("정말 삭제하시겠습니까?");   // 내용 설정

                // 취소 버튼 설정
                ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                    }
                });
                // 확인 버튼 설정
                ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                        Calendar c1 = Calendar.getInstance();
                        String strToday = sdf.format(c1.getTime());
                        mReference.child("categories").child(id).child(item.getCategory()).child("score").removeValue();
                        mReference.child("split-score").child(id).child(strToday).child(item.getCategory()).removeValue();

                        dialog.dismiss();     //닫기
                    }
                });

                ad.show();
            }
        });

        return convertView;
    }


}