import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.farmnavi.R
import com.google.android.material.textfield.TextInputEditText
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import android.util.Log
import com.google.android.material.appbar.MaterialToolbar
import java.util.*
import java.util.concurrent.TimeUnit

class MarketFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var searchInput: TextInputEditText

    private val fullList = mutableListOf<MarketPrice>()
    private val filteredList = mutableListOf<MarketPrice>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_market, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        searchInput = view.findViewById(R.id.searchInput)

        // Toolbar back button listener
        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar?.setNavigationOnClickListener {
            // Handle back navigation
            if (requireActivity().supportFragmentManager.backStackEntryCount > 0) {
                requireActivity().supportFragmentManager.popBackStack()
            } else {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        fetchMarketPrices()
        setupSearch()

        return view
    }



    private fun setupSearch() {
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterList(s.toString())
            }
        })
    }

//    private fun filterList(query: String) {
//        filteredList.clear()
//        if (query.isEmpty()) {
//            filteredList.addAll(fullList)
//        } else {
//            val searchStr = query.toLowerCase(Locale.getDefault())
//            val filtered = fullList.filter {
//                it.Commodity.toLowerCase(Locale.getDefault()).contains(searchStr) ||
//                        it.Variety.toLowerCase(Locale.getDefault()).contains(searchStr)
//            }
//            filteredList.addAll(filtered)
//        }
//        recyclerView.adapter?.notifyDataSetChanged()
//    }

    private fun filterList(query: String) {
        val searchQuery = query.trim().lowercase(Locale.getDefault())
        filteredList.clear()
        if (searchQuery.isEmpty()) {
            filteredList.addAll(fullList)
        } else {
            val filtered = fullList.filter { price ->
                price.Commodity.lowercase(Locale.getDefault()).contains(searchQuery) ||
                        price.Variety.lowercase(Locale.getDefault()).contains(searchQuery) ||
                        price.District.lowercase(Locale.getDefault()).contains(searchQuery) ||
                        price.State.lowercase(Locale.getDefault()).contains(searchQuery)
            }
            filteredList.addAll(filtered)
        }
        recyclerView.adapter?.notifyDataSetChanged()

        //emptyView.visibility = if (filteredList.isEmpty()) View.VISIBLE else View.GONE
        recyclerView.visibility = if (filteredList.isEmpty()) View.GONE else View.VISIBLE
    }



    private fun fetchMarketPrices() {
        progressBar.visibility = View.VISIBLE
        val apiUrl = "https://api.data.gov.in/resource/35985678-0d79-46b4-9ed6-6f13308a1d24?api-key=579b464db66ec23bdd000001a10501c810784eef423bb9082cb97663&format=json&limit=500&sort[Arrival_Date]=desc"

        Thread {
            try {
                val client = OkHttpClient.Builder()
                    .readTimeout(30, TimeUnit.SECONDS) // Set read timeout to 30 seconds
                    .connectTimeout(30, TimeUnit.SECONDS) // Also a good idea to set connect timeout
                    .build()
                val request = Request.Builder().url(apiUrl).build()
                val response = client.newCall(request).execute()
                val jsonString = response.body()?.string() ?: ""
                Log.d("APIResponse", jsonString)
                val mainObj = JSONObject(jsonString)
                val records = mainObj.getJSONArray("records")

                fullList.clear()
                for (i in 0 until records.length()) {
                    val item = records.getJSONObject(i)
                    fullList.add(
                        MarketPrice(
                            State = item.optString("State", ""),
                            District = item.optString("District", ""),
                            Market = item.optString("Market", ""),
                            Commodity = item.optString("Commodity", ""),
                            Variety = item.optString("Variety", ""),
                            Grade = item.optString("Grade", ""),
                            Arrival_Date = item.optString("Arrival_Date", ""),
                            Min_Price = item.optString("Min_Price", ""),
                            Max_Price = item.optString("Max_Price", ""),
                            Modal_Price = item.optString("Modal_Price", "")
                        )
                    )
                }

//                val sortedList = fullList.sortedByDescending { it.Arrival_Date }
//
//                val latestPricesMap = mutableMapOf<String, MarketPrice>()
//                for (price in sortedList) {
//                    val key = "${price.Market}_${price.Commodity}_${price.Variety}"
//                    if (!latestPricesMap.containsKey(key)) {
//                        latestPricesMap[key] = price
//                    }
//                }
//                val uniquePrices = latestPricesMap.values.toList()

                // After parsing all records into fullList
                requireActivity().runOnUiThread {
                    progressBar.visibility = View.GONE
                    filteredList.clear()
                    filteredList.addAll(fullList)
                    recyclerView.adapter = MarketPriceAdapter(filteredList)
                }

            } catch (e: Exception) {
                Log.e("MarketFragment", "Error fetching data", e)
                requireActivity().runOnUiThread {
                    progressBar.visibility = View.GONE
                }
            }
        }.start()
    }
}

