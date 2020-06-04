package com.example.capstonestage2.databases;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.capstonestage2.models.Transaction;

import java.util.List;

import timber.log.Timber;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<Transaction>> transactions;

    public MainViewModel(@NonNull Application application) {
        super(application);
        Timber.plant(new Timber.DebugTree());
        Timber.d("Retrieving data from database");
        TransactionDatabase database = TransactionDatabase.getDatabase(this.getApplication());
        transactions = database.transactionDao().getAllTransactions();
    }

    public LiveData<List<Transaction>> getTransactions() {
        return transactions;
    }

}
