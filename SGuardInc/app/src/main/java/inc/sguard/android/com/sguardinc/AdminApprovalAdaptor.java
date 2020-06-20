package inc.sguard.android.com.sguardinc;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import inc.sguard.android.com.sguardinc.Model.Guard;
import inc.sguard.android.com.sguardinc.UserChat.AdminChat;
import inc.sguard.android.com.sguardinc.UserChat.ChatDetails;
import inc.sguard.android.com.sguardinc.UserChat.chat;

public class AdminApprovalAdaptor extends RecyclerView.Adapter<AdminApprovalAdaptor.MyViewHolder> {

    Context context;
    ArrayList<Guard> profiles;
    AdminHome adminHome;
    String userid;

    public AdminApprovalAdaptor(Context c, ArrayList<Guard> p, AdminHome adminHome, String userid) {
        context = c;
        profiles = p;
        this.adminHome = adminHome;
        this.userid = userid;
    }

    @NonNull
    @Override
    public AdminApprovalAdaptor.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AdminApprovalAdaptor.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.approval_guard_list_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdminApprovalAdaptor.MyViewHolder holder, final int position) {
        ChatDetails.chatname = profiles.get(position).getName();
        holder.name.setText("Name: " + profiles.get(position).getName());
        holder.email.setText("Email: " + profiles.get(position).getEmail());
        holder.dob.setText("DateOfBirth: " + profiles.get(position).getDateOfBirth());
        holder.status.setText("Status: " + profiles.get(position).getStatus());
        if (profiles.get(position).getPhotoURL().equals("default")) {
            holder.profilePic.setImageDrawable(context.getResources().getDrawable(R.drawable.ifour));
        } else
            Picasso.get().load(profiles.get(position).getPhotoURL()).into(holder.profilePic);

        holder.approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String guard_id = profiles.get(position).getId();
                FirebaseDatabase.getInstance().getReference().child("Guard").child(guard_id).child("NotApproved").removeValue();
                profiles.remove(position);
                notifyDataSetChanged();
            }
        });

        holder.view_pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String guard_id = profiles.get(position).getId();
               // ChatDetails.chatWith = guard_id;
                Intent intent = new Intent(adminHome, AdminViewProfileGuard.class);
                intent.putExtra("VIEW_GUARD_ID", guard_id);
                context.startActivity(intent);
            }
        });

        holder.chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String guard_id = profiles.get(position).getId();
                ChatDetails.chatWith = guard_id;
                ChatDetails.username = userid;
                context.startActivity(new Intent(adminHome, AdminChat.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, email, dob, status;
        ImageView profilePic;
        Button approve, chat,view_pro;
        RelativeLayout main_layout;

        public MyViewHolder(View itemView) {
            super(itemView);
            main_layout = (RelativeLayout) itemView.findViewById(R.id.main);
            name = (TextView) itemView.findViewById(R.id.name);
            email = (TextView) itemView.findViewById(R.id.email);
            profilePic = (ImageView) itemView.findViewById(R.id.imageView);
            dob = (TextView) itemView.findViewById(R.id.dob);
            status = (TextView) itemView.findViewById(R.id.status);
            approve = (Button) itemView.findViewById(R.id.approve);
            chat = (Button) itemView.findViewById(R.id.chat);
            view_pro= (Button) itemView.findViewById(R.id.view_pro);
        }

        public void onClick(final int position) {
          /*  profilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, profiles.get(position).getName() + " is clicked", Toast.LENGTH_SHORT).show();
                }
            });*/
        }
    }
}