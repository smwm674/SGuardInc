package inc.sguard.android.com.sguardinc;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import inc.sguard.android.com.sguardinc.Model.Guard;
import inc.sguard.android.com.sguardinc.Model.UserBooking;

public class UserCurrentOrderAdapter extends RecyclerView.Adapter<UserCurrentOrderAdapter.MyViewHolder> {

    Context context;
    ArrayList<UserBooking> profiles;

    public UserCurrentOrderAdapter(Context c, ArrayList<UserBooking> p) {
        context = c;
        profiles = p;
    }

    @NonNull
    @Override
    public UserCurrentOrderAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserCurrentOrderAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.order_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserCurrentOrderAdapter.MyViewHolder holder, final int position) {
        holder.name.setText("Event Name: " + profiles.get(position).getCurrent_Booking());
        holder.number.setText("Contact: " + profiles.get(position).getCurrent_Booking_Number());
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name,number;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.event_name);
            number = (TextView) itemView.findViewById(R.id.event_number);
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