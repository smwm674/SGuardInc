package inc.sguard.android.com.sguardinc;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import inc.sguard.android.com.sguardinc.Model.AdminID;
import inc.sguard.android.com.sguardinc.Model.ChatModel;
import inc.sguard.android.com.sguardinc.Model.User;
import inc.sguard.android.com.sguardinc.UserChat.AdminChat;
import inc.sguard.android.com.sguardinc.UserChat.ChatDetails;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity {
    TextView message;
    FirebaseUser user;
    DatabaseReference reference,reference1;
    Intent intent;
    android.support.v7.widget.Toolbar toolbar;
    ImageView sendBtn;
    EditText messageArea;
    MessageAdapter adapter;
    List<ChatModel> mChat;
    RecyclerView messagesRecyclerView;
    String admin_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        initToolBar();
        messagesRecyclerView=findViewById(R.id.chatRecyclerView);
        messagesRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        messagesRecyclerView.setLayoutManager(linearLayoutManager);
        sendBtn=findViewById(R.id.sendButton);
        messageArea=findViewById(R.id.messageArea);
//        final String userid=getIntent().getStringExtra("userid");
        reference1 = FirebaseDatabase.getInstance().getReference().child("Admin_ID");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("DataRecievied ", dataSnapshot.toString());
                AdminID users = dataSnapshot.getValue(AdminID.class);
                Log.i("admin_id",users.getAdmin_id());
                admin_id = users.getAdmin_id();
                readMessage(user.getUid(),admin_id);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg=messageArea.getText().toString();
                if (!msg.equals("")){
                    sendMessage(user.getUid(),admin_id,msg);
                }
                else{
                    Toast.makeText(MessageActivity.this, "You cannot send empty message", Toast.LENGTH_SHORT).show();
                }
                messageArea.setText("");

            }
        });
        user=FirebaseAuth.getInstance().getCurrentUser();
//        reference=FirebaseDatabase.getInstance().getReference(getString(R.string.user)).child(userid);
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                User u=dataSnapshot.getValue(User.class);
//                toolbar.setTitle(u.getName());
//                readMessage(user.getUid(),userid);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

    }

    private void initToolBar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");

        this.setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                }
        );
    }
    private void sendMessage(String sender,String receiver,String message){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap=new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        reference.child("Chats").push().setValue(hashMap);
    }
    private void readMessage(final String myid, final String userid){
        mChat=new ArrayList<ChatModel>();
        reference=FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    ChatModel chat=snapshot.getValue(ChatModel.class);
                    if ((chat.getSender().equals(myid)&& chat.getReceiver().equals(userid))|| (chat.getReceiver().equals(myid)&& chat.getSender().equals(userid))){
                        mChat.add(chat);
                    }
                    adapter=new MessageAdapter(MessageActivity.this, (ArrayList<ChatModel>) mChat);
                    messagesRecyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
