package com.grampower.fieldforce.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.franmontiel.fullscreendialog.FullScreenDialogContent;
import com.franmontiel.fullscreendialog.FullScreenDialogController;
import com.grampower.fieldforce.Activities.profile;
import com.grampower.fieldforce.Databases.DataBase;
import com.grampower.fieldforce.R;

/**
 * Created by sam on 14/7/17.
 */

public class FullScreenProfileEditor extends Fragment implements FullScreenDialogContent {

    String fieldName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

       fieldName=((profile)getActivity()).getFieldName();
        return inflater.inflate(R.layout.full_screen_profile_editor,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final TextInputEditText  mNewField=(TextInputEditText)getView().findViewById(R.id.field_update_text);
        mNewField.setHint("enter new"+fieldName);
        if(fieldName.equals("mobile")){
            mNewField.setHint("enter new "+fieldName+" number");
        }else{
            mNewField.setHint("enter new "+fieldName);
        }
        Button mClose=(Button)getView().findViewById(R.id.close);
        Button mUpdateButton=(Button)getView().findViewById(R.id.bt_updater);

        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),profile.class));
            }
        });

         mUpdateButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 String updatedValue=mNewField.getText().toString().trim();
                 if(updatedValue.length()==0||updatedValue.equals("")){
                     Toast.makeText(getContext(), "Input is empty!", Toast.LENGTH_SHORT).show();
                 }else{
                     DataBase dataBase=new DataBase(getContext());
                     dataBase.updateProfileFields(dataBase,fieldName,updatedValue);
                     Toast.makeText(getContext(), "Your "+fieldName+" have updated successfully", Toast.LENGTH_SHORT).show();
                 }

             }
         });

    }

    @Override
    public void onDialogCreated(FullScreenDialogController dialogController) {

    }

    @Override
    public boolean onConfirmClick(FullScreenDialogController dialogController) {
        return false;
    }

    @Override
    public boolean onDiscardClick(FullScreenDialogController dialogController) {
        return false;
    }

}
