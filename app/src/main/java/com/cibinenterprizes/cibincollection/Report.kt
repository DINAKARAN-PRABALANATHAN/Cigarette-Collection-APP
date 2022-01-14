package com.cibinenterprizes.cibincollection

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cibinenterprizes.cibincollection.Model.ReportDetails
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_report.*

class Report : AppCompatActivity() {

    private lateinit var recview: RecyclerView
    val idAuth = FirebaseAuth.getInstance().currentUser?.uid.toString()
    var database = FirebaseDatabase.getInstance().reference
    lateinit var storeId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        recview = findViewById(R.id.report_recycle)
        recview.setLayoutManager(LinearLayoutManager(this))

        report_back_botton.setOnClickListener {
            finish()
        }
    }
    override fun onStart() {
        super.onStart()
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                storeId = snapshot.child("Store ID").child(idAuth).child("id").getValue().toString()
                var collection =
                    snapshot.child("User Details").child(storeId)
                        .child("totalCollected").getValue().toString()
                var amount =
                    snapshot.child("User Details").child(storeId)
                        .child("totalAmount").getValue().toString()
                report_collected.setText(collection)
                report_amount.setText(amount)
                Log.i("Store", storeId)
                val options: FirebaseRecyclerOptions<ReportDetails> = FirebaseRecyclerOptions.Builder<ReportDetails>()
                    .setQuery(FirebaseDatabase.getInstance().getReference().child("User Details").child(storeId).child("Report"), ReportDetails::class.java).build()

                val adapter: FirebaseRecyclerAdapter<ReportDetails, myviewholder> =object: FirebaseRecyclerAdapter<ReportDetails, myviewholder>(options){
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myviewholder {
                        val view: View = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_report, parent, false)
                        return myviewholder(view)
                    }

                    override fun onBindViewHolder(holder: myviewholder, position: Int, model: ReportDetails) {
                        holder.collected?.setText(model.Collected)
                        holder.Amount?.setText(model.Amount)
                        holder.date?.setText(model.Date)
                    }

                }
                recview.setAdapter(adapter)
                adapter.startListening()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


    }
    class myviewholder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var collected: TextView? = null
        var Amount: TextView? = null
        var date: TextView? = null

        init {
            collected= itemView.findViewById(R.id.report_new_collected)
            Amount= itemView.findViewById(R.id.report_new_amount)
            date= itemView.findViewById(R.id.report_date)
            super.itemView
        }
    }
}