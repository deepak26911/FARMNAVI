import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.farmnavi.R


class MarketPriceAdapter(private val prices: List<MarketPrice>) :
    RecyclerView.Adapter<MarketPriceAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textMarket: TextView = view.findViewById(R.id.textMarket)
        val textDistrictState: TextView = view.findViewById(R.id.textDistrictState)
        val textCommodityVariety: TextView = view.findViewById(R.id.textCommodityVariety)
        val textDate: TextView = view.findViewById(R.id.textDate)
        val textPrice: TextView = view.findViewById(R.id.textPrice)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_market_price, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = prices.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val price = prices[position]
        holder.textMarket.text = price.Market
        holder.textDistrictState.text = "${price.District}, ${price.State}"
        holder.textCommodityVariety.text = "${price.Commodity} - ${price.Variety} - ${price.Grade}"
        holder.textDate.text = price.Arrival_Date
        holder.textPrice.text = "Min: ₹${price.Min_Price} | Max: ₹${price.Max_Price} | Modal: ₹${price.Modal_Price}"
    }

}
