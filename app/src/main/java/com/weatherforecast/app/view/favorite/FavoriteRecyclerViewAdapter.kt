package com.weatherforecast.app.view.favorite

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.weatherforecast.app.R
import com.weatherforecast.app.model.Favorite
import com.weatherforecast.app.view.main.MainActivity
import com.weatherforecast.app.viewmodel.AlertViewModel
import com.weatherforecast.app.viewmodel.FavoriteViewModel

class FavoriteRecyclerViewAdapter(var favoriteData: ArrayList<Favorite>): RecyclerView.Adapter<FavoriteRecyclerViewAdapter.FavoriteViewHolder>() {
    lateinit var context: Context
    lateinit var viewModel: FavoriteViewModel

    fun updateList(newList: List<Favorite>, viewModel: FavoriteViewModel) {
        this.viewModel = viewModel

        favoriteData.clear()
        favoriteData.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        context = parent.context
        return FavoriteViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.favorite_list_row, parent, false))
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(favoriteData[position], this)
    }

    override fun getItemCount(): Int {
        return favoriteData.size
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    class FavoriteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val favoriteTitleLbl = view.findViewById<TextView>(R.id.favoriteTitleLbl)
        private val deleteButton = view.findViewById<Button>(R.id.FavDeleteButton)


        @SuppressLint("SetTextI18n")
        fun bind(favorite: Favorite, adapter: FavoriteRecyclerViewAdapter) {
            favoriteTitleLbl.text = favorite.title

            deleteButton.setOnClickListener {
                val builder = AlertDialog.Builder(adapter.context)
                builder.setTitle(adapter.context.getText(R.string.Warning))
                builder.setMessage(adapter.context.getText(R.string.WarningMsg))

                builder.setPositiveButton(adapter.context.getText(R.string.yes)) { _, _ ->
                    adapter.viewModel.delete(favorite)
                    adapter.favoriteData.remove(favorite)
                    adapter.notifyDataSetChanged()
                }

                builder.setNegativeButton(adapter.context.getText(R.string.no), null)
                builder.show()
            }

            itemView.setOnClickListener {
                val intent = Intent(adapter.context, MainActivity::class.java)
                intent.putExtra("openFlag", false)
                intent.putExtra("source", "favorite")
                intent.putExtra("lat", favorite.lat)
                intent.putExtra("lon", favorite.lon)
                adapter.context.startActivity(intent)
            }
        }
    }
}