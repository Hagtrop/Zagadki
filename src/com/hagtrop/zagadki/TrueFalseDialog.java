package com.hagtrop.zagadki;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

public class TrueFalseDialog extends DialogFragment {
	
	public static final String DIALOG_TYPE = "True-False Message";
	
	private int layoutId;
	
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

	public TrueFalseDialog(boolean answerTrue){
		if(answerTrue) this.layoutId = R.layout.true_dialog;
		else this.layoutId = R.layout.false_dialog;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(layoutId, container, false);
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
	public void show(FragmentManager manager, String tag) {
		new CloseDialog(this).start();
		super.show(manager, tag);
	}

	@Override
	public int show(FragmentTransaction transaction, String tag) {
		new CloseDialog(this).start();
		return super.show(transaction, tag);
	}
	

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		mListener.onDialogDismiss(TrueFalseDialog.this, DIALOG_TYPE);
	}


	class CloseDialog extends Thread{
		TrueFalseDialog dialog;
		
		public CloseDialog(TrueFalseDialog dialog){
			super();
			this.dialog = dialog;
		}

		@Override
		public void run() {
			try{
				sleep(3000);
				dialog.dismiss();
			}catch(Exception e){
				Log.e("mLog", e.toString());
			}
		}
	}
}
