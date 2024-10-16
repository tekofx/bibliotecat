package dev.tekofx.biblioteques.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.tekofx.biblioteques.BibliotequesAPIService
import dev.tekofx.biblioteques.R
import dev.tekofx.biblioteques.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var mRecyclerView: RecyclerView
    val mAdapter: RecyclerAdapter = RecyclerAdapter()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        //searchBiblioteques()


        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val searchButton: Button = binding.SearchButton
        val searchView: SearchView = binding.searchView

        setUpRecyclerView(root)

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

        return root
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://do.diba.cat/api/dataset/biblioteques/format/json/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun searchBiblioteques() {
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(BibliotequesAPIService::class.java)
                .getBiblioteques("https://do.diba.cat/api/dataset/biblioteques/format/json/pag-ini/1/pag-fi/29999")
            val output = call.body()
            if (call.isSuccessful) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@HomeFragment.context, output.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    fun setUpRecyclerView(root: View) {

        try {
            mRecyclerView = root.findViewById(R.id.bibliotequesRecycler)

        } catch (ex: Exception) {
            println(ex)
        }
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this.context)
        this.context?.let { mAdapter.RecyclerAdapter(getBiblioteques(), it) }
        mRecyclerView.adapter = mAdapter
    }

    fun getBiblioteques(): MutableList<Biblioteca> {
        var biblioteques: MutableList<Biblioteca> = ArrayList()
        biblioteques.add(
            Biblioteca(
                "biblioteca424096",
                "Biblioteca L Esqueller",
                "Biblioteca L Esqueller. Sant Pere de Torelló",
                "Sant Pere de Torelló",
                "https://bibliotecavirtual.diba.cat/documents/350986/0/P1120129.JPGfoto+portada.jpg/9ff2c56c-7424-4d95-b734-0ef67225a281?t=1364040065786"
            )
        )
        biblioteques.add(
            Biblioteca(
                "biblioteca21323915",
                "Biblioteca Municipal L'Ateneu",
                "Biblioteca Municipal L'Ateneu. Esparreguera",
                "Esparreguera",
                "https://bibliotecavirtual.diba.cat/documents/350883/3667382/Copia+de+Exterior+Biblioteca-xemenia-bis.jpg/3f89dcbd-8bd8-4dd4-bc10-38d10dc26c8e?t=1322135992180"
            )
        )
        biblioteques.add(
            Biblioteca(
                "biblioteca423328",
                "Biblioteca L Esqueller",
                "Biblioteca L Esqueller. Sant Pere de Torelló",
                "Sant Pere de Torelló",
                "https://bibliotecavirtual.diba.cat/documents/347883/451634/Fa%C3%A7ana+492x366.jpg/9f177990-bf6c-276a-c094-17e8858619e0?t=1613732917600"
            )
        )

        return biblioteques
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}