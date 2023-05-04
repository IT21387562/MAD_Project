package com.example.expenses_tracker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var deleteTransaction: FoodTransaction
    private lateinit var transactions : List<FoodTransaction>
    private lateinit var oldTransactions : List<FoodTransaction>
    private lateinit var transactionAdapter: FoodTransactionAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var db : AppDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        transactions = arrayListOf()

        transactionAdapter = FoodTransactionAdapter(transactions)
        linearLayoutManager = LinearLayoutManager(this)

        db = Room.databaseBuilder(this,
            AppDatabase::class.java,
            "foodtransactions").build()

        //3 - 24.00
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerview.apply {
        adapter = transactionAdapter
        layoutManager = linearLayoutManager
        }

//        val recyclerView : RecyclerView = findViewById(R.id.recyclerview)
//        val adapter = FoodTransactionAdapter(transactions)



//        recyclerView.adapter = adapter
//        recyclerView.layoutManager = LinearLayoutManager(this)


        val addBtn : FloatingActionButton = findViewById(R.id.addBtn)

        // swipe to remove
        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deleteTransaction(transactions[viewHolder.adapterPosition])
            }
        }

        val swipeHelper = ItemTouchHelper(itemTouchHelper)
        swipeHelper.attachToRecyclerView(recyclerview)

        addBtn.setOnClickListener {
            val intent = Intent(this, AddFoodTransactionActivity::class.java)
            startActivity(intent)
        }

    }

    private fun fetchAll(){
        GlobalScope.launch {

            transactions = db.foodTransactionDao().getAll()

            runOnUiThread {
                updateDashboard()
                transactionAdapter.setData(transactions)
            }
        }
    }

    private fun updateDashboard(){
        val totalAmount : Double = transactions.map { it.amount }.sum()
        val budgetAmount : Double = transactions.filter { it.amount>0 }.map{ it.amount }.sum()
        val expenseAmount : Double = totalAmount - budgetAmount

        val totalBalance : TextView = findViewById(R.id.totalBalance)
        val budget :TextView = findViewById(R.id.budget)
        val expense :TextView = findViewById(R.id.expense)


        totalBalance.text = "Rs %.2f".format(totalAmount)
        budget.text = "Rs %.2f".format(budgetAmount)
        expense.text = "Rs %.2f".format(expenseAmount)
    }

    private fun undoDelete(){
        GlobalScope.launch {
            db.foodTransactionDao().insetAll(deleteTransaction)

            transactions = oldTransactions

            runOnUiThread {
                transactionAdapter.setData(transactions)
                updateDashboard()
            }
        }
    }

    private fun showSnackBar(){
        val view = findViewById<View>(R.id.coordinator)
        val snackbar = Snackbar.make(view, "Food Transaction Deleted!!", Snackbar.LENGTH_LONG)
        snackbar.setAction("Undo"){
            undoDelete()
        }
            .setActionTextColor(ContextCompat.getColor(this,R.color.red))
            .setTextColor(ContextCompat.getColor(this, R.color.white))
            .show()
    }

    private fun deleteTransaction(transaction: FoodTransaction){
        deleteTransaction = transaction
        oldTransactions = transactions

        GlobalScope.launch {
            db.foodTransactionDao().delete(transaction)

            transactions =  transactions.filter { it.id != transaction.id }
            runOnUiThread {
                updateDashboard()
                transactionAdapter.setData(transactions)
                showSnackBar()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        fetchAll()
    }
}