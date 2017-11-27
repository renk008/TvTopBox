package top.renk.tvtopbox.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import top.renk.tvtopbox.R;
import top.renk.tvtopbox.widget.CustomRecyclerView;

public class HomeTvAdapter extends CustomRecyclerView.CustomAdapter<Integer> {

    public HomeTvAdapter(Context context, List<Integer> data) {
        super(context, data);
    }

    @Override
    protected RecyclerView.ViewHolder onSetViewHolder(View view) {
        return new GalleryViewHolder(view);
    }

    @NonNull
    @Override
    protected int onSetItemLayout() {
        return R.layout.item;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onSetItemData(RecyclerView.ViewHolder viewHolder, int position) {
        GalleryViewHolder holder = (GalleryViewHolder) viewHolder;
        holder.tv.setText("MV:" + position);
    }


    @Override
    protected void onItemFocus(View itemView, int position) {
        TextView tvFocus = itemView.findViewById(R.id.tv_focus);
        ImageView focusBg = itemView.findViewById(R.id.focus_bg);

        tvFocus.setVisibility(View.VISIBLE);
        focusBg.setVisibility(View.VISIBLE);

        if (Build.VERSION.SDK_INT >= 21) {
            //抬高Z轴
            ViewCompat.animate(itemView).scaleX(1.10f).scaleY(1.10f).translationZ(1).start();
        } else {
            ViewCompat.animate(itemView).scaleX(1.10f).scaleY(1.10f).start();
            ViewGroup parent = (ViewGroup) itemView.getParent();
            parent.requestLayout();
            parent.invalidate();
        }
    }

    @Override
    protected void onItemGetNormal(View itemView, int position) {

        TextView tvFocus = itemView.findViewById(R.id.tv_focus);
        ImageView focusBg = itemView.findViewById(R.id.focus_bg);

        tvFocus.setVisibility(View.VISIBLE);
        focusBg.setVisibility(View.INVISIBLE);

        if (Build.VERSION.SDK_INT >= 21) {
            ViewCompat.animate(itemView).scaleX(1.0f).scaleY(1.0f).translationZ(0).start();
        } else {
            Log.i("renk", "HomeTvAdapter.normalStatus.scale build version < 21");
            ViewCompat.animate(itemView).scaleX(1.0f).scaleY(1.0f).start();
            ViewGroup parent = (ViewGroup) itemView.getParent();
            parent.requestLayout();
            parent.invalidate();
        }
    }

    @Override
    protected int getCount() {
        return mData.size();
    }

    private class GalleryViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        ImageView iv;

        GalleryViewHolder(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv_focus);
            iv = itemView.findViewById(R.id.im);
        }
    }
}
