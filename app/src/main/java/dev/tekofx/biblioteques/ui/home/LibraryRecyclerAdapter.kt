package dev.tekofx.biblioteques.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import dev.tekofx.biblioteques.R
import dev.tekofx.biblioteques.model.Library

class LibraryRecyclerAdapter : RecyclerView.Adapter<LibraryRecyclerAdapter.ViewHolder>() {

    private var library: MutableList<Library> = mutableListOf()

    fun setLibraries(libraries: MutableList<Library>) {
        this.library = libraries
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.recycler_view_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(library[position])
    }

    override fun getItemCount(): Int {
        return library.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val municipiNom: TextView = view.findViewById(R.id.municipiNom)
        private val adrecaNom: TextView = view.findViewById(R.id.adrecaNom)
        private val avatar: ImageView = view.findViewById(R.id.imageView)
        private val timetable: TextView = view.findViewById(R.id.timetable)



        fun bind(biblioteca: Library) {
            municipiNom.text = biblioteca.municipi_nom
            adrecaNom.text = biblioteca.adreca_nom
            timetable.text = "horari"

            if (biblioteca.imatge.isNotEmpty())
                avatar.loadUrl(biblioteca.imatge)
        }

        private fun ImageView.loadUrl(url: String) {
            Picasso.with(context).load(url).into(this)
        }
    }
}