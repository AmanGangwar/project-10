package com.example.capstonestage2.databases;

import android.os.AsyncTask;

import com.example.capstonestage2.models.Transaction;

import java.util.List;



public class GetDatabaseAsyncTask extends AsyncTask<TransactionDatabase, Void, List<Transaction>> {

    private OnDatabaseTaskCompleted mListener;

    public interface OnDatabaseTaskCompleted {

        /**
         * Gets called when GetDataBaseAsyncTask is finished
         * @param transactions Transactions loaded from Database
         */
        void onDatabaseTaskCompleted(List<Transaction> transactions);
    }
    public GetDatabaseAsyncTask(OnDatabaseTaskCompleted listener) {
        mListener = listener;
    }

    @Override
    protected List<Transaction> doInBackground(TransactionDatabase... transactionDatabases) {
        List<Transaction> transactionList = transactionDatabases[0].transactionDao().getAllTransactionsAsList();

        mListener.onDatabaseTaskCompleted(transactionList);
        return null;
    }
}
