package com.hagtrop.zagadki;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;

public class TimeIsOverDialog extends DialogFragment implements OnClickListener{
	public static final String DIALOG_TYPE = "Time Is Over Message";
	
	NoticeDialogListener mListener;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try{
			mListener = (NoticeDialogListener) activity;
		}
		catch(ClassCastException e){
			throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.time_is_over_dialog, container, false);
		view.findViewById(R.id.time_is_over_dialog_okBtn).setOnClickListener(this);
		return view;
	}
	
	@NonNull
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
	}



	@Override
	public void onStart() {
		super.onStart();
		if (getDialog() == null) return;
		//Задаём анимациюплавного появления/исчезновения
		getDialog().getWindow().setWindowAnimations(R.style.dialog_animation_fade);
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		mListener.onDialogDismiss(TimeIsOverDialog.this, DIALOG_TYPE);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.time_is_over_dialog_okBtn:
			this.dismiss();
			break;
		default: break;
		}
	}
}
