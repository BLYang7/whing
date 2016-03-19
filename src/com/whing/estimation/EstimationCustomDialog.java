package com.whing.estimation;

import java.util.HashMap;
import java.util.List;

import com.whing.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class EstimationCustomDialog extends Dialog {

	public EstimationCustomDialog(Context context) {
		super(context);
	}

	public EstimationCustomDialog(Context context, int theme) {
		super(context, theme);
	}

	public static class Builder {
		private Context context;
		private String title;
		private ListView messageListView;
		private List<HashMap<String, Object>> mData;
		
		private String positiveButtonText;
		private String negativeButtonText;
		
		private View contentView;
		private ScoreAdapter adapter ;
		
		private DialogInterface.OnClickListener positiveButtonClickListener;
		private DialogInterface.OnClickListener negativeButtonClickListener;

		public Builder(Context context) {
			this.context = context;
		}

		public Builder setData(List<HashMap<String, Object>> mData){
			this.mData = mData;
			return this;
		}

		public Builder setAdapter(Context context, List<HashMap<String, Object>> mData) {
			adapter = new ScoreAdapter(context, mData);
			return this;
		}

		public Builder setTitle(int title) {
			this.title = (String) context.getText(title);
			return this;
		}

		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder setContentView(View v) {
			this.contentView = v;
			return this;
		}

		public Builder setPositiveButton(int positiveButtonText,
				DialogInterface.OnClickListener listener) {
			this.positiveButtonText = (String) context
					.getText(positiveButtonText);
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setPositiveButton(String positiveButtonText,
				DialogInterface.OnClickListener listener) {
			this.positiveButtonText = positiveButtonText;
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(int negativeButtonText,
				DialogInterface.OnClickListener listener) {
			this.negativeButtonText = (String) context
					.getText(negativeButtonText);
			this.negativeButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(String negativeButtonText,
				DialogInterface.OnClickListener listener) {
			this.negativeButtonText = negativeButtonText;
			this.negativeButtonClickListener = listener;
			return this;
		}

		public EstimationCustomDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			// instantiate the dialog with the custom Theme
			final EstimationCustomDialog dialog = new EstimationCustomDialog(context,R.style.Dialog);
			View layout = inflater.inflate(R.layout.estimation_dialog_normal_layout, null);
			
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			
			((TextView) layout.findViewById(R.id.estimation_title)).setText(title);
			
			//…Ë÷√listviewµƒœ‘ æ
			this.messageListView = (ListView) layout.findViewById(R.id.estimation_list_view);
		
			
			this.messageListView.setAdapter(adapter);
			
//			SetListViewHeight.setListViewHeightBasedOnChildren(messageListView);
			
			if (positiveButtonText != null) {
				((Button) layout.findViewById(R.id.estimation_positiveButton))
						.setText(positiveButtonText);
				if (positiveButtonClickListener != null) {
					((Button) layout.findViewById(R.id.estimation_positiveButton))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									positiveButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_POSITIVE);
								}
							});
				}
			} else {
				layout.findViewById(R.id.estimation_positiveButton).setVisibility(View.GONE);
			}

			
			if (negativeButtonText != null) {
				((Button) layout.findViewById(R.id.estimation_negativeButton))
						.setText(negativeButtonText);
				if (negativeButtonClickListener != null) {
					((Button) layout.findViewById(R.id.estimation_negativeButton))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									negativeButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_NEGATIVE);
								}
							});
				}
			} else {
				layout.findViewById(R.id.estimation_negativeButton).setVisibility(View.GONE);
			}

			dialog.setContentView(layout);
			return dialog;
		}
		
		public ScoreAdapter getAdapter(){
			return adapter;
		}

	}
}
