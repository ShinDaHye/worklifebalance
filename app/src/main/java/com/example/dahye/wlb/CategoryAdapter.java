package com.example.dahye.wlb;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends ArrayAdapter {
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
        final Context context = parent.getContext();

        /* 'listview_custom' Layout을 inflate하여 convertView 참조 획득 */
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.customlistview_category, parent, false);
        }

        /* 'listview_custom'에 정의된 위젯에 대한 참조 획득 */
        TextView category = (TextView) convertView.findViewById(R.id.category) ;
        TextView score = (TextView) convertView.findViewById(R.id.score) ;

        CategoryItem item = items.get(position);

        category.setText(item.getCategory());
        score.setText(item.getScore());

        /* 버튼에 대한 이벤트 리스너 */
        ImageButton imgbtn_minus = (ImageButton)convertView.findViewById(R.id.imgbtn_minus);
        ImageButton imgbtn_plus = (ImageButton)convertView.findViewById(R.id.imgbtn_plus);

        imgbtn_minus.setOnClickListener(new ImageButton.OnClickListener(){
            public void onClick(View view){
                Toast.makeText(context, "minus", Toast.LENGTH_LONG).show();
            }
        });

        imgbtn_plus.setOnClickListener(new ImageButton.OnClickListener(){
            public void onClick(View view){
                Toast.makeText(context, "plus", Toast.LENGTH_LONG).show();
            }
        });

        return convertView;
    }


}