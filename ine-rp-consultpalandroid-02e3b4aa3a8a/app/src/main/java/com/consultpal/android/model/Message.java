package com.consultpal.android.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ines on 6/14/16.
 */
public class Message implements Parcelable{

    private String messageContent;
    private String senderName;
    private long dateSent;
    private boolean read;

    public Message(String messageContent, String senderName, long dateSent, boolean read) {
        this.messageContent = messageContent;
        this.senderName = senderName;
        this.dateSent = dateSent;
        this.read = read;
    }

    public Message() {
        super();
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public long getDateSent() {
        return dateSent;
    }

    public void setDateSent(long dateSent) {
        this.dateSent = dateSent;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    protected Message(Parcel in) {
        messageContent = in.readString();
        senderName = in.readString();
        dateSent = in.readLong();
        read = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(messageContent);
        dest.writeString(senderName);
        dest.writeLong(dateSent);
        dest.writeByte((byte) (read ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };
}
