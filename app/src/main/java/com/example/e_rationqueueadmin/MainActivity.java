package com.example.e_rationqueueadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.item;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private static final String DATE_FORMAT ="dd-MM-yyyy";
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter adapter;
    private FirebaseRecyclerOptions<item> options;
    private TextView availableTextView;
    private TextView messageTextView;
    String todayDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayoutManager linearLayoutManager;
        recyclerView=findViewById(R.id.orderListRecyclerView);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,
                false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        final Calendar calendar = Calendar.getInstance();
        int day=calendar.get(Calendar.DATE);
        int month=calendar.get(Calendar.MONTH);
        int year=calendar.get(Calendar.YEAR);
        Date date=new GregorianCalendar(year,month,day+1).getTime();
        todayDate=dateToString(date);
        availableTextView=findViewById(R.id.text);
        messageTextView=findViewById(R.id.messageTextView);

        loadBookings();
    }
    public String dateToString(Date selectedDate)
    {

        SimpleDateFormat dateFormat=new SimpleDateFormat(DATE_FORMAT, Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String dateToString=dateFormat.format(selectedDate);
        Log.d("todayDate",dateToString);
        return dateToString;
    }

    private void loadBookings() {
        messageTextView.setText("There is no booking on today.");
        messageTextView.setVisibility(View.VISIBLE);
        final Query query= FirebaseDatabase.getInstance().getReference("bookedTimeSlot")
                .child(todayDate).orderByKey();


        options =new FirebaseRecyclerOptions.Builder<item>().setQuery(
                query, new SnapshotParser<item>() {
                    @NonNull
                    @Override
                    public item parseSnapshot(@NonNull DataSnapshot snapshot) {
                        messageTextView.setVisibility(View.GONE);

                        String timeSlot= snapshot.getKey();
                        String uid=snapshot.getValue().toString();

                        return new item(timeSlot,uid);
                    }
                }).build();

        adapter=new FirebaseRecyclerAdapter<item,ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull item item) {

                viewHolder.timeSlotText.setText(item.getTimeSlot());
                viewHolder.setUserDetail(item.getUid());


            }


            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,
                        parent, false);
                return new ViewHolder(view);
            }

        };
        recyclerView.setAdapter(adapter);
    }




    public class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView timeSlotText;
        private final TextView customerNameText;
        private final TextView cardNoText;
        private final TextView cardTypeText;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            timeSlotText=itemView.findViewById(R.id.timeSlotText);
            customerNameText=itemView.findViewById(R.id.customerNameText);
            cardNoText=itemView.findViewById(R.id.cardNoText);
            cardTypeText=itemView.findViewById(R.id.cardTypeText);


        }
        private void setUserDetail(String uid) {

            DatabaseReference users=FirebaseDatabase.getInstance().getReference("users");
            users.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String customerName=dataSnapshot.child("customerName").getValue().toString()
                            .toUpperCase();
                    customerNameText.setText(customerName);
                    String cardNo=dataSnapshot.child("cardNo").getValue().toString();
                    cardNoText.setText(cardNo);
                    String cardType=dataSnapshot.child("cardType").getValue().toString()
                            .toUpperCase();
                    cardTypeText.setText(cardType);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }



    }
    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}