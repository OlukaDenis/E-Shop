package com.mcdenny.easyshopug.ViewHolder;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mcdenny.easyshopug.Model.GridObject;
import com.mcdenny.easyshopug.R;

import java.util.List;
import java.util.Random;


/**Created by McDenny 3/8/2018
 *
 */

public class RecyclerGrid extends RecyclerView.Adapter<RecyclerViewHolder> implements View.OnClickListener {
    private int[] colors = {0x80ff0000, 0x8000b3b3, 0x800000ff, 0x80800080, 0x80ffff00, 0x8000ff00, 0x80ff00ff, 0x80808000};
    private List<GridObject> itemList;
    private Context ctx;
    private Random random = new Random();
    private GridObject gridObject;

    public RecyclerGrid(Context context, List<GridObject> itemList) {
        this.itemList = itemList;
        ctx = context;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_list, null);
        RecyclerViewHolder rcv = new RecyclerViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        gridObject = itemList.get(position);

        holder.actionName.setText(gridObject.getName());
        try {
            Log.e("NAME", gridObject.getName());
            Log.e("ICON", gridObject.getIcon() + "");
            holder.actionPhoto.setImageResource(gridObject.getIcon());
        } catch (Exception ex) {
//            ex.printStackTrace();
        }

        int colorLength = colors.length;
        int randomColor = colors[position % colorLength];

        try {
            ((GradientDrawable) holder.actionPhoto.getBackground()).setColor(randomColor);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

    @Override
    public void onClick(View view) {
        //clickEventHandler();
    }
}
