package dev.tekofx.biblioteques.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.tekofx.biblioteques.R
import dev.tekofx.biblioteques.call.LibraryService
import dev.tekofx.biblioteques.databinding.FragmentHomeBinding
import dev.tekofx.biblioteques.repository.LibraryRepository


class HomeFragment : Fragment() {

    private val TAG = "HomeFragment"
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // Home view model
    lateinit var viewModel: HomeViewModel

    // Recycler
    lateinit var libraryRecyclerView: RecyclerView
    val libraryRecyclerAdapter: LibraryRecyclerAdapter = LibraryRecyclerAdapter()


    /**
     * View creation event handler
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel = ViewModelProvider(
            this,
            HomeViewModelFactory(LibraryRepository(LibraryService.getInstance()))
        )[HomeViewModel::class.java]

        setUpRecyclerView(root)

        viewModel.libraries.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "onCreate: $it")
            libraryRecyclerAdapter.setLibraries(it.toMutableList())
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, Observer {})
        viewModel.getLibraries()

        setUpRecyclerView(root)
        return root
    }

    /**
     * Set up the recicler view
     */
    private fun setUpRecyclerView(root: View) {

        try {
            libraryRecyclerView = root.findViewById(R.id.bibliotequesRecycler)
            libraryRecyclerView.setHasFixedSize(true)
            libraryRecyclerView.layoutManager = LinearLayoutManager(this.context)
            libraryRecyclerView.adapter = libraryRecyclerAdapter
        } catch (ex: Exception) {
            println(ex)
        }

    }

    /**
     * Set up search view
     */
    private fun setUpSearchView() {

        val searchView: SearchView = binding.searchView
        val searchButton: Button = binding.SearchButton

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
        }

    }

    /**
     * View destruction event handler
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}