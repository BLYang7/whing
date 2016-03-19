package com.whing.external;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class MyListview extends ListView{

	
	public MyListview(Context context,AttributeSet attrs){  
        super(context, attrs);  
    }  
  
    /** 
     * ���ò����� 
     */  
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)  
    {  
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,  
                MeasureSpec.AT_MOST);  
        super.onMeasure(widthMeasureSpec, expandSpec);  
    }  
}
