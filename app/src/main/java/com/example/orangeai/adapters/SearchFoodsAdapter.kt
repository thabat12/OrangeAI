package com.example.orangeai.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.orangeai.R
import com.example.orangeai.models.FoodNutrients
import kotlinx.android.synthetic.main.rv_food_search.view.*
import java.util.ArrayList

open class SearchFoodsAdapter(
    private val context: Context,
    private var list: ArrayList<FoodNutrients>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private var onClickListener: OnClickListener? = null
    private var selectedPos: Int = RecyclerView.NO_POSITION


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.rv_food_search,
                parent,
                false
            )
        )
    }




    /**
     * Binds each item in the ArrayList to a view
     *
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     */
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.setSelected(selectedPos == position)
        val model = list[position]

        var nameShortened = ""
        if (model.foodName.length > 25) {
            nameShortened = model.foodName.substring(0, 24) + "..."
        } else {
            nameShortened = model.foodName
        }
        nameShortened = nameShortened.toLowerCase().capitalize()


        holder.itemView.food_name.text = nameShortened
        holder.itemView.calories.text = model.calories.toString()
        holder.itemView.protein.text = "Protein: ${model.proteinValue}"
        holder.itemView.lipids.text = "Lipids: ${model.lipidValue}"
        holder.itemView.carbs.text = "Carbs: ${model.carbValue}"
        var description = ""
        var calories = model.calories
        if (calories < 40) {
            description = "This is a light meal"
        } else if (calories >= 40 && calories < 400) {
            description = "This is a moderate meal"
        } else {
            description = "This is a heavy meal"
        }
        description += " (per serving)"
        holder.itemView.description.text = description



        if (holder is MyViewHolder) {
            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick(position, model)
                }
            }
            // END
        }
    }
    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return list.size
    }





    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }
    // END

    // TODO (Step 4: Create an interface for onclickListener)
    // START
    interface OnClickListener {
        fun onClick(position: Int, model: FoodNutrients)
    }
    // END

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}