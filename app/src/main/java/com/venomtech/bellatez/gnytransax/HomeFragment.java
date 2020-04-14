package com.venomtech.bellatez.gnytransax;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class HomeFragment extends Fragment {

    ImageView income;
    ImageView loans;
    ImageView savings;
    ImageView balancesheet;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        savings = view.findViewById(R.id.savings);
        loans = view.findViewById(R.id.loans);
        balancesheet = view.findViewById(R.id.balancesheet);
        income = view.findViewById(R.id.income);

        savings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SavingsFragment nextFrag= new SavingsFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_holder, nextFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

        income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransactionFragment nextFrag= new TransactionFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_holder, nextFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

        loans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DebtFragment nextFrag= new DebtFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_holder, nextFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

        balancesheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BalanceSheetFragment nextFrag= new BalanceSheetFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_holder, nextFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}

