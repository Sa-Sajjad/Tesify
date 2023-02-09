package kaerushi.weeabooify.uwu.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kaerushi.weeabooify.uwu.R;
import kaerushi.weeabooify.uwu.VariantContainer;

public class VariantAdapter extends RecyclerView.Adapter<VariantAdapter.VariantViewHolder> {

    int[] imgBanner = {R.drawable.uwu_banner, R.drawable.ae_banner, R.drawable.sc_banner};

    ArrayList<VariantContainer> variantContainerArrayList;

    public VariantAdapter(ArrayList<VariantContainer> variantContainerArrayList) {
        this.variantContainerArrayList = variantContainerArrayList;
    }

    @NonNull
    @Override
    public VariantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VariantViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.variant_container, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VariantViewHolder holder, int position) {

        VariantContainer variantContainer1 = variantContainerArrayList.get(position);

        holder.imgBanner.setImageResource(imgBanner[position]);
        holder.textTitle.setText(variantContainer1.title);
        holder.textDetail.setText(variantContainer1.detail);
    }

    @Override
    public int getItemCount() {
        return variantContainerArrayList.size();
    }

    public static class VariantViewHolder extends RecyclerView.ViewHolder {

        ImageView imgBanner;
        TextView textTitle, textDetail;

        public VariantViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBanner = itemView.findViewById(R.id.variant_image);
            textTitle = itemView.findViewById(R.id.variant_title);
            textDetail = itemView.findViewById(R.id.variant_detail);
        }

    }
}
