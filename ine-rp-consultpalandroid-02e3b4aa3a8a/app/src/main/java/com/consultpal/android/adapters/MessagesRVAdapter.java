package com.consultpal.android.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.consultpal.android.R;
import com.consultpal.android.model.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by ines on 6/14/16.
 */
public class MessagesRVAdapter extends RecyclerView.Adapter<MessagesRVAdapter.MessagesViewHolder> {

    private List<Message> messagesList;
    private Context context;

    public MessagesRVAdapter(List<Message> messagesList, Context context) {
        this.messagesList = messagesList;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    @Override
    public void onBindViewHolder(MessagesViewHolder contactViewHolder, int i) {
        Message message = messagesList.get(i);
        contactViewHolder.messageContent.setText(message.getMessageContent());
        contactViewHolder.senderName.setText(message.getSenderName());
        Date date=new Date(message.getDateSent());
        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM HH:mm");
        String dateSentString = df2.format(date);
        contactViewHolder.dateSent.setText(dateSentString);
    }

    @Override
    public MessagesViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.message_list_item, viewGroup, false);

        return new MessagesViewHolder(itemView);
    }

    // View Holder pattern
    public static class MessagesViewHolder extends RecyclerView.ViewHolder {
        protected TextView messageContent;
        protected TextView senderName;
        protected TextView dateSent;

        public MessagesViewHolder(View v) {
            super(v);
            messageContent =  (TextView) v.findViewById(R.id.message_item_content);
            senderName = (TextView)  v.findViewById(R.id.message_item_sender);
            dateSent = (TextView)  v.findViewById(R.id.message_item_date_sent);
        }
    }

}