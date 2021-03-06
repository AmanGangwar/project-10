package com.example.capstonestage2.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstonestage2.R;
import com.example.capstonestage2.adapters.CoinAdapter;
import com.example.capstonestage2.databases.TransactionDatabase;
import com.example.capstonestage2.models.Coin;
import com.example.capstonestage2.models.Transaction;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;


public class CoinsInfoFragment extends Fragment {

    private static final String CURRENCY_KEY = "currencyKey";
    private static final String COIN_LIST_KEY = "coinListKey";
    private static final String TRANSACTION_LIST_KEY = "transactionListKey";

    @BindView(R.id.coin_card_recycler_view)
    RecyclerView mCoinInfoRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private CoinAdapter mCoinAdapter;

    private String mUsedCurrency;
    private List<Coin> mCoins;
    private List<Transaction> mTransactionList;

    public CoinsInfoFragment() {
        // Required empty public constructor
    }

    public static CoinsInfoFragment newInstance(String currency, ArrayList<Coin> coins, List<Transaction> transactionList) {
        CoinsInfoFragment fragment = new CoinsInfoFragment();
        Bundle args = new Bundle();
        args.putString(CURRENCY_KEY, currency);
        args.putParcelableArrayList(COIN_LIST_KEY, coins);
        args.putParcelableArrayList(TRANSACTION_LIST_KEY, (ArrayList<? extends Parcelable>) transactionList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUsedCurrency = getArguments().getString(CURRENCY_KEY);
            mCoins = getArguments().getParcelableArrayList(COIN_LIST_KEY);
            mTransactionList = getArguments().getParcelableArrayList(TRANSACTION_LIST_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_coins_info, container, false);

        // Set up Libraries
        Timber.plant(new Timber.DebugTree());
        ButterKnife.bind(this, view);


        // Create RecyclerView
        mLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mCoinAdapter = new CoinAdapter(mCoins, mTransactionList, mUsedCurrency);

        mCoinInfoRecyclerView.setLayoutManager(mLinearLayoutManager);
        mCoinInfoRecyclerView.setAdapter(mCoinAdapter);

        // Create Swipe Listener
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int swipeDir) {

                final int deletedCoinIndex = viewHolder.getAdapterPosition();
                final Coin deletedCoin = mCoins.get(deletedCoinIndex);
                Timber.d("Deleting Transaction: %s", deletedCoin.getFullName());
                mCoins.remove(deletedCoinIndex);
                mCoinAdapter.notifyDataSetChanged();

                final TransactionDatabase transactionDatabase = TransactionDatabase.getDatabase(getContext());
                // Delete Transaction in other thread
                Executor deleteExecutor = Executors.newSingleThreadExecutor();
                deleteExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        final Transaction deletedTransaction = transactionDatabase.transactionDao()
                                .getSingleTransaction(deletedCoin.getName());

                        transactionDatabase.transactionDao().deleteTransactionWithPK(deletedCoin.getName());

                        // Show Undo Snackbar
                        Snackbar snackbar = Snackbar
                                .make(view, String.format(getString(R.string.snack_bar_removed_coin), deletedCoin.getFullName()), Snackbar.LENGTH_LONG);
                        snackbar.setAction(getString(R.string.undo_button), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Undo is selected, restore the deleted item on new thread

                                // Get a handler that can be used to post to the main thread
                                final Handler mainHandler = new Handler(getContext().getMainLooper());

                                final Runnable onPostExecute = new Runnable() {
                                    @Override
                                    public void run() {
                                        mCoinAdapter.notifyDataSetChanged();
                                    }
                                };

                                Executor restoreExecutor = Executors.newSingleThreadExecutor();
                                restoreExecutor.execute(new Runnable() {
                                    @Override
                                    public void run() {

                                        mCoins.add(deletedCoinIndex, deletedCoin);
                                        transactionDatabase.transactionDao().insertTransaction(deletedTransaction);

                                        mainHandler.post(onPostExecute);
                                    }
                                });

                            }});
                        snackbar.setActionTextColor(Color.YELLOW);
                        snackbar.show();
                    }
                });


            }
        }).attachToRecyclerView(mCoinInfoRecyclerView);

        // Inflate the layout for this fragment
        return view;
    }

}
