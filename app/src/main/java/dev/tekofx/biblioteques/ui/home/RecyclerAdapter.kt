package dev.tekofx.biblioteques.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import dev.tekofx.biblioteques.R

class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    var biblioteques: MutableList<Biblioteca>  = ArrayList()
    lateinit var context: Context

    fun RecyclerAdapter(superheros : MutableList<Biblioteca>, context: Context){
        this.biblioteques = superheros
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = biblioteques.get(position)
        holder.bind(item, context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.recycler_view_item, parent, false))
    }

    override fun getItemCount(): Int {
        return biblioteques.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val adrecaNom = view.findViewById(R.id.adrecaNom) as TextView
        val municipiNom = view.findViewById(R.id.municipiNom) as TextView
        val avatar = view.findViewById(R.id.imageView) as ImageView

        fun bind(biblioteca:Biblioteca, context: Context){
            adrecaNom.text = biblioteca.adreca_nom
            municipiNom.text = biblioteca.municipi_nom
            avatar.loadUrl(biblioteca.imatge)
            itemView.setOnClickListener(View.OnClickListener { Toast.makeText(context, biblioteca.adreca_nom, Toast.LENGTH_SHORT).show() })
        }
        fun ImageView.loadUrl(url: String) {
            Picasso.with(context).load(url).into(this)
        }
    }
}