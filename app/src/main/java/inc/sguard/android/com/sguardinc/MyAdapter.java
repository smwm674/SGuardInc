package inc.sguard.android.com.sguardinc;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

import inc.sguard.android.com.sguardinc.Model.Guard;
import inc.sguard.android.com.sguardinc.UserChat.ChatDetails;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    ArrayList<Guard> profiles;

    public MyAdapter(Context c, ArrayList<Guard> p) {
        context = c;
        profiles = p;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.guard_list_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        ChatDetails.chatname =  profiles.get(position).getName();
        holder.name.setText("Name: " + profiles.get(position).getName());
        holder.email.setText("Email: " + profiles.get(position).getEmail());
        holder.dob.setText("DateOfBirth: " + profiles.get(position).getDateOfBirth());
        holder.status.setText("Status: " + profiles.get(position).getStatus());
        if (profiles.get(position).getPhotoURL().equals("default")) {
            holder.profilePic.setImageDrawable(context.getResources().getDrawable(R.drawable.ifour));
        } else
            Picasso.get().load(profiles.get(position).getPhotoURL()).into(holder.profilePic);

    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, email, dob, status;
        ImageView profilePic;
        RelativeLayout main_layout;

        public MyViewHolder(View itemView) {
            super(itemView);
            main_layout = (RelativeLayout) itemView.findViewById(R.id.main);
            name = (TextView) itemView.findViewById(R.id.name);
            email = (TextView) itemView.findViewById(R.id.email);
            profilePic = (ImageView) itemView.findViewById(R.id.imageView);
            dob = (TextView) itemView.findViewById(R.id.dob);
            status = (TextView) itemView.findViewById(R.id.status);
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