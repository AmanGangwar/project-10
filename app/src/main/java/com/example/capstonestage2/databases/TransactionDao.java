package com.example.capstonestage2.databases;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.capstonestage2.models.Transaction;

import java.util.List;

@Dao
public interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTransaction(Transaction transaction);

    @Query("SELECT * FROM transaction_table")
    LiveData<List<Transaction>> getAllTransactions();

    @Query("SELECT * FROM transaction_table")
    List<Transaction> getAllTransactionsAsList();

    @Query("SELECT * FROM transaction_table WHERE coin_name =:coinName")
    Transaction getSingleTransaction(String coinName);

    @Query("DELETE FROM transaction_table WHERE coin_name=:coinName")
    void deleteTransactionWithPK(String coinName);
}
