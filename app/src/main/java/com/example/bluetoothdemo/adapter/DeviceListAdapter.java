package com.example.bluetoothdemo.adapter;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bluetoothdemo.R;

import java.util.List;

/**
 * Created by Administrator on 2016/5/15.
 */
public class DeviceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<BluetoothDevice> deviceList;
    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    private static final int ITEM_VIEW_TYPE_ITEM = 1;
    private ItemClick itemClick;

    public DeviceListAdapter(List<BluetoothDevice> deviceList) {
        this.deviceList = deviceList;
    }

    public interface ItemClick{
        void onItemClick(BluetoothDevice device);
    }

    public void setItemClick(ItemClick itemClick){
        this.itemClick = itemClick;
    }


    private boolean isHeader(int position) {
        return position == 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW_TYPE_HEADER) {
            View header = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_head, parent, false);
            return new HeaderViewHolder(header);
        }
        View showview = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(showview);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isHeader(position)) {
            return;
        }
        final BluetoothDevice device = deviceList.get(position - 1);
        ((ViewHolder) holder).devicenametv.setText(device.getName() + "");
        ((ViewHolder) holder).devicenametv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClick.onItemClick(device);

            }
        });

    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? ITEM_VIEW_TYPE_HEADER : ITEM_VIEW_TYPE_ITEM;
    }


    @Override
    public int getItemCount() {
        return deviceList.size() + 1;
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView devicenametv;

        public ViewHolder(View itemView) {
            super(itemView);
            devicenametv = (TextView) itemView.findViewById(R.id.item_divicename);
        }
    }


}
