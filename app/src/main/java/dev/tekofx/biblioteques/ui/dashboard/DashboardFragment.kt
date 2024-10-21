package dev.tekofx.biblioteques.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.network.parseGetRequestBlocking
import com.fleeksoft.ksoup.select.Elements
import dev.tekofx.biblioteques.databinding.FragmentDashboardBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

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
            try {
                fetchWebsiteData()

            } catch (Exception: Exception) {
                Toast.makeText(this.context, "Error", Toast.LENGTH_SHORT).show()
            }

            //val libraryCardFragment = LibraryCard.newInstance("param1", "param2")
            //childFragmentManager.beginTransaction().add(R.id.linearLayout, libraryCardFragment).commit()
        }
        return root
    }

    fun fetchWebsiteData() {
        CoroutineScope(Dispatchers.IO).launch {
            val doc: com.fleeksoft.ksoup.nodes.Document =
                Ksoup.parseGetRequestBlocking(url = "https://aladi.diba.cat/search*cat/?searchtype=X&searcharg=mistborn&searchscope=171&submit=Cercar")
            val headlines: Elements = doc.select("span.titular")

            withContext(Dispatchers.Main) {
                // Currentiza la interfaz de usuario con los resultados
                for (headline in headlines) {
                    val headlineText = headline.getElementsByTag("a")[0].text()
                    println("headlineText " + headlineText)

                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}