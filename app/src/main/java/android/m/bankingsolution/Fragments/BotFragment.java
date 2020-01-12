package android.m.bankingsolution.Fragments;

import android.content.Intent;
import android.m.bankingsolution.BusinessCardTicketClass;
import android.m.bankingsolution.MoreCardInfoActivity;
import android.m.bankingsolution.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BotFragment extends Fragment {
    private View myCardView;
    private RecyclerView BCRecyclerView;
    private DatabaseReference OthersBCReference;
    private String clicked_user_uid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myCardView = inflater.inflate(R.layout.fragment_chat, container, false);
        displayBusinessCardRecyclerView();
        return myCardView;
    }

    private void displayBusinessCardRecyclerView() {
        OthersBCReference = FirebaseDatabase.getInstance().getReference().child("OthersBusinessCards");
        BCRecyclerView = (RecyclerView) myCardView.findViewById(R.id.listOfOthersBusinessCards);
        BCRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<BusinessCardTicketClass>()
                .setQuery(OthersBCReference, BusinessCardTicketClass.class)
                .build();

        final FirebaseRecyclerAdapter<BusinessCardTicketClass, cardPlacementHolder> adapter
                = new FirebaseRecyclerAdapter<BusinessCardTicketClass, cardPlacementHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final cardPlacementHolder BCHolder,
                                            int i, @NonNull final BusinessCardTicketClass BCTicketClass) {


                final String usersIDS = getRef(i).getKey();
                OthersBCReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
//                            BCHolder.nameoncard.setText(BCTicketClass.getName());
//                            BCHolder.emailoncard.setText(BCTicketClass.getEmail());
//                            BCHolder.phoneoncard.setText(BCTicketClass.getPhone());

                            BCHolder.DeleteButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    OthersBCReference.child(usersIDS).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(getContext(), "Deleted my posted ticket", Toast.LENGTH_LONG).show();

                                        }
                                    });

                                }
                            });


                            BCHolder.moreInfo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getActivity(), MoreCardInfoActivity.class);
                                    intent.putExtra("clickedCardUID", usersIDS);
                                    startActivity(intent);
                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }

            @NonNull
            @Override
            public cardPlacementHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_others_business_card, parent, false);
                cardPlacementHolder viewHolder = new cardPlacementHolder(view);
                return viewHolder;
            }

        };

        BCRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    public static class cardPlacementHolder extends RecyclerView.ViewHolder {
        TextView nameoncard, emailoncard, phoneoncard;
        RelativeLayout DeleteButton, moreInfo;

        public cardPlacementHolder(@NonNull View itemView) {
            super(itemView);
            nameoncard = itemView.findViewById(R.id.BCname);
            emailoncard = itemView.findViewById(R.id.BCemail);
            phoneoncard = itemView.findViewById(R.id.BCphone);
            DeleteButton = itemView.findViewById(R.id.delete_single_businesscard);
            moreInfo = itemView.findViewById(R.id.moreInformationAboutCard);

        }

    }


}
