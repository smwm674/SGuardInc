package inc.sguard.android.com.sguardinc;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import inc.sguard.android.com.sguardinc.Model.ChatModel;
import inc.sguard.android.com.sguardinc.Model.User;
import inc.sguard.android.com.sguardinc.UserChat.ChatDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {
    Context context;
    ArrayList<ChatModel> mChat;
    String userid;
    FirebaseUser user;
    public static final int MSG_TYPE_LEFT=0;
    public static final int MSG_TYPE_RIGHT=1;

    public MessageAdapter(Context context, ArrayList<ChatModel> mChat) {
        this.context = context;
        this.mChat = mChat;
    }

    @NonNull
    @Override
    public MessageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        if (i==MSG_TYPE_RIGHT) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.MyViewHolder(view);
        }
        else {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.MyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MyViewHolder holder, final int position) {
        ChatModel chatModel=mChat.get(position);
        holder.showMessage.setText(chatModel.getMessage());
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView showMessage;
        RelativeLayout main_layout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            main_layout = (RelativeLayout) itemView.findViewById(R.id.main);
            showMessage = (TextView) itemView.findViewById(R.id.show_message);
        }
    }

    @Override
    public int getItemViewType(int position) {
        user=FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(user.getUid())){
            return MSG_TYPE_LEFT;
        }
        else return MSG_TYPE_RIGHT;
    }
}
