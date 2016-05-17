package com.example.bluetoothdemo.adapter;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bluetoothdemo.R;
import com.example.bluetoothdemo.modle.ChatModle;

import java.util.List;

/**
 * Created by Administrator on 2016/5/17.
 */
public class ChatListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ChatModle> chatModleList;

    public ChatListAdapter(List<ChatModle> chatModleList) {
        this.chatModleList = chatModleList;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ChatModle.type.left.ordinal()) {
            View header = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_chat_left, parent, false);
            return new LeftViewHolder(header);
        } else {
            View showview = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_chat_right, parent, false);
            return new RightViewHolder(showview);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position)== ChatModle.type.left.ordinal()){//左边
            ((LeftViewHolder)holder).textView.setText(chatModleList.get(position).getMessage());
        }else{
            ((RightViewHolder)holder).textView.setText(chatModleList.get(position).getMessage());
        }

    }

    @Override
    public int getItemViewType(int position) {
        return chatModleList.get(position).getT().ordinal();//返回枚举所在的位置 0 ,1
    }


    @Override
    public int getItemCount() {
        return chatModleList.size();
    }

    class LeftViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public LeftViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.chat_message);
        }
    }

    class RightViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public RightViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.chat_message);
        }
    }
}
