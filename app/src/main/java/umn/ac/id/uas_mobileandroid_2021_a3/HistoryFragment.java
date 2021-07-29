package umn.ac.id.uas_mobileandroid_2021_a3;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HistoryFragment extends Fragment {
    RecyclerView recyclerView;
    DatabaseReference reference;
    FirebaseUser user;
    List<History> mHistory;
    HistoryAdapter historyAdapter;
    TextView empty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        recyclerView = view.findViewById(R.id.recyclerHistory);
        empty = view.findViewById(R.id.historyEmpty);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Orders");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(user.getUid())){
                    mHistory = new ArrayList<>();
                    int i = 0;
                    for(DataSnapshot ds: snapshot.child(user.getUid()).getChildren()){
                        float rTotal;
                        String id = ds.child("ID").getValue(String.class);
                        String type = ds.child("BookType").getValue(String.class);
                        String made = ds.child("OrderMade").getValue(String.class);
                        String date = ds.child("Dtl").child("date").getValue(String.class);
                        String time = ds.child("Dtl").child("time").getValue(String.class);
                        String location = ds.child("Dtl").child("location").getValue(String.class);
                        Long total = ds.child("TotalPrice").getValue(Long.class);
                        if(total != null){
                            rTotal = total;
                        }
                        else{
                            rTotal = 0f;
                        }
                        History history = new History(id, type, made, date, time, location, rTotal);
                        mHistory.add(history);
                        i++;
                    }
                    historyAdapter = new HistoryAdapter(getContext(), getActivity(), mHistory);
                    recyclerView.setAdapter(historyAdapter);
                }
                else {
                    empty.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }
}
