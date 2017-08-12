package com.grampower.fieldforce.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grampower.fieldforce.R;

import org.apmem.tools.layouts.FlowLayout;

public class CommentWithTag extends AppCompatActivity {

    Context context;
    AutoCompleteTextView mAutoCompleteTextView;
    FlowLayout mFlowLayout;

    String[] str={"Andoid","Jelly Bean","Froyo",
            "Ginger Bread","Eclipse Indigo","Eclipse Juno"};

    String products[] = {"Dell Inspiron", "HTC One X", "HTC Wildfire S", "HTC Sense", "HTC Sensation XE",
            "iPhone 4S", "Samsung Galaxy Note 800",
            "Samsung Galaxy S3", "MacBook Air", "Mac Mini", "MacBook Pro"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_with_tag);
        context=this;

        mAutoCompleteTextView=(AutoCompleteTextView)findViewById(R.id.actv_tag_person);
        mFlowLayout=(FlowLayout)findViewById(R.id.fl_tagged_persons);


        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, products);

        mAutoCompleteTextView.setAdapter(adapter);
        mAutoCompleteTextView.setThreshold(1);

        mAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("az","text changed");
                adapter.getFilter().filter(s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        mAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("az","text changed");
                String name=adapter.getItem(position).toString();
                createTaggedView(name);
            }
        });


    }


    void createTaggedView(String personName){

        CardView cardView=new CardView(context);
        cardView.setBackground(getResources().getDrawable(R.drawable.curved_corner_view));
        FlowLayout.LayoutParams cardParams = new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(dp2Px(2),dp2Px(2),0,0);

        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity=Gravity.CENTER_VERTICAL;
        //linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        TextView textName=new TextView(context);
        textName.setText(personName);
        LinearLayout.LayoutParams nameTextParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        nameTextParams.gravity= Gravity.CENTER_VERTICAL;
        nameTextParams.setMargins(dp2Px(4),0,0,0);
        linearLayout.addView(textName,nameTextParams);

        FrameLayout imageFrameLayout = new FrameLayout(context);
        LinearLayout.LayoutParams imageFrameParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imageFrameParams.gravity = Gravity.CENTER_VERTICAL;
        imageFrameParams.setMargins(dp2Px(2),0,0,0);

        imageFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View parentView=(View)v.getParent().getParent();
                mFlowLayout.removeView(parentView);
            }
        });

        ImageView crossImage = new ImageView(context);
        crossImage.setImageResource(R.drawable.ic_clear_black_24dp);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imageParams.setMargins(dp2Px(2),dp2Px(2),dp2Px(2),dp2Px(2));
        imageParams.gravity=Gravity.CENTER_VERTICAL;

        imageFrameLayout.addView(crossImage,imageParams);

        linearLayout.addView(imageFrameLayout,imageFrameParams);

        cardView.addView(linearLayout,layoutParams);

        mFlowLayout.removeView(mAutoCompleteTextView);
        mFlowLayout.addView(cardView,cardParams);
        mFlowLayout.addView(mAutoCompleteTextView);

        mAutoCompleteTextView.setText("");
        mAutoCompleteTextView.requestFocus();

    }



    public int dp2Px(int dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        int pixelValue = (int) (dpValue * scale + 0.5f);
        return pixelValue;
    }

}
