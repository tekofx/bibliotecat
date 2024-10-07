package dev.tekofx.biblioteques.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.lifecycle.ViewModelProvider
import dev.tekofx.biblioteques.LibraryCard
import dev.tekofx.biblioteques.R
import dev.tekofx.biblioteques.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        val searchButton: Button = binding.SearchButton
        val searchView: SearchView = binding.searchView
        val linearLayout: LinearLayout = binding.linearLayout
        var test:LinearLayout=root.findViewById(R.id.linearLayout)

        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it

        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Acción al pulsar el botón de búsqueda en el teclado
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchButton.isEnabled = !newText.isNullOrEmpty()
                return true
            }
        })

        searchButton.setOnClickListener {
            val query: String = searchView.query.toString()
            Toast.makeText(this.context, query, Toast.LENGTH_SHORT).show()
            val libraryCardFragment = LibraryCard.newInstance("param1", "param2")
            childFragmentManager.beginTransaction().add(R.id.linearLayout, libraryCardFragment).commit()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}