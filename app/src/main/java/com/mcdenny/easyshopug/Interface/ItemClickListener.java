package com.mcdenny.easyshopug.Interface;

import android.view.View;

public interface ItemClickListener {
    //This will listen on the clicks on the items of the recyclerview
    void onClick(View view, int position, boolean isLongClicked);
}
