package dev.tekofx.biblioteques.ui.home

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import dev.tekofx.biblioteques.R
import dev.tekofx.biblioteques.model.Library
import java.time.LocalDate
import java.time.LocalTime

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
        private val openIcon: ImageView = view.findViewById(R.id.openIcon)
        private val green = getColor(view.context, R.color.green_open)
        private val red = getColor(view.context, R.color.red_closed)


        fun bind(library: Library) {
            
            val localDate = LocalDate.now()
            val localTime = LocalTime.now()
            municipiNom.text = library.municipiNom
            adrecaNom.text = library.adrecaNom
            timetable.text = library.generateStateMessage(localDate, localTime)


            val openStatusCircle = ShapeDrawable(OvalShape())
            openStatusCircle.intrinsicHeight = 20
            openStatusCircle.intrinsicWidth = 20
            openStatusCircle.paint.color = red

            if (library.isOpen(localDate, localTime)) {
                openStatusCircle.paint.color = green
            }
            openIcon.setImageDrawable(openStatusCircle)


            if (library.imatge.isNotEmpty()) {
                avatar.loadUrl(library.imatge)
            }


        }

        private fun ImageView.loadUrl(url: String) {
            Picasso.with(context).load(url).into(this)
        }
    }
}