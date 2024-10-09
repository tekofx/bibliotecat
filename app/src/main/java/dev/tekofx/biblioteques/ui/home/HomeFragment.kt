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
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.network.parseGetRequest
import com.fleeksoft.ksoup.network.parseGetRequestBlocking
import com.fleeksoft.ksoup.select.Elements
import dev.tekofx.biblioteques.BibliotequesAPIService
import dev.tekofx.biblioteques.LibraryCard
import dev.tekofx.biblioteques.R
import dev.tekofx.biblioteques.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Document
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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

        searchBiblioteques()


        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val searchButton: Button = binding.SearchButton
        val searchView: SearchView = binding.searchView


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

    private fun searchBiblioteques(){
        CoroutineScope(Dispatchers.IO).launch {
            val call=getRetrofit().create(BibliotequesAPIService::class.java).getBiblioteques("https://do.diba.cat/api/dataset/biblioteques/format/json/pag-ini/1/pag-fi/29999")
            val output=call.body()
            if (call.isSuccessful){
                withContext(Dispatchers.Main){
                    Toast.makeText(this@HomeFragment.context, output.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}