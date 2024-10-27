package dev.tekofx.biblioteques.ui.home

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import dev.tekofx.biblioteques.R
import dev.tekofx.biblioteques.model.Library
import java.time.LocalDate
import java.time.LocalTime

class LibraryRecyclerAdapter : RecyclerView.Adapter<LibraryRecyclerAdapter.ViewHolder>(),
    Filterable {

    private var libraries: MutableList<Library> = mutableListOf()
    private var originalLibraryList: List<Library> = listOf()

    fun setLibraries(libraries: MutableList<Library>) {
        this.libraries = libraries
        this.originalLibraryList = libraries

        notifyDataSetChanged()
    }

    fun setFilteredLibraries(libraries: MutableList<Library>) {
        this.libraries = libraries
        notifyDataSetChanged()
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredResults = FilterResults()
                if (constraint.isNullOrEmpty()) {
                    filteredResults.values = originalLibraryList
                } else {
                    val filterPattern = constraint.toString().lowercase().trim()
                    val filteredList = originalLibraryList.filter { item ->
                        // Apply your filtering logic here
                        item.adrecaNom.contains(filterPattern, ignoreCase = true) ||
                                item.municipiNom.contains(filterPattern, ignoreCase = true)
                    }
                    filteredResults.values = filteredList
                }
                return filteredResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                val filteredList = results?.values as List<Library>
                setFilteredLibraries(filteredList.toMutableList())
                //Log.d("LibraryRecyclerAdapter", "Filtered Results: $filteredList")

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.recycler_view_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(libraries[position])
    }

    override fun getItemCount(): Int {
        return libraries.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val municipiNom: TextView = view.findViewById(R.id.municipiNom)
        private val adrecaNom: TextView = view.findViewById(R.id.adrecaNom)
        private val avatar: ImageView = view.findViewById(R.id.imageView)
        private val timetable: TextView = view.findViewById(R.id.timetable)
        private val openIcon: ImageView = view.findViewById(R.id.openIcon)
        private val green = getColor(view.context, R.color.green_open)
        private val red = getColor(view.context, R.color.red_closed)
        private val yellow = getColor(view.context, R.color.yellow_soon)


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
                // If the library is closing in less than an hour
                if (library.isClosingSoon(localDate, localTime)) {
                    openStatusCircle.paint.color = yellow
                } else {
                    openStatusCircle.paint.color = green

                }
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