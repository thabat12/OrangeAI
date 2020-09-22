package com.example.orangeai.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.orangeai.R
import com.example.orangeai.activities.DietActivity
import com.example.orangeai.activities.MainActivity
import com.example.orangeai.models.ActivityPrograms
import com.example.orangeai.models.FoodImages
import com.example.orangeai.models.Recipes
import kotlinx.android.synthetic.main.activity_log_food.*
import kotlinx.android.synthetic.main.activity_splash.view.*
import kotlinx.android.synthetic.main.rv_food_pic_history.view.*
import kotlinx.android.synthetic.main.rv_for_recipes.view.*
import kotlinx.android.synthetic.main.rv_programs.view.*
import java.io.File
import kotlin.collections.ArrayList


open class FoodImageAdapter(
    private val context: Context,
    private var list: ArrayList<FoodImages>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // TODO (Step 5: Add a variable for onClickListener interface.)
    // START
    private var onClickListener: OnClickListener? = null
    private var selectedPos: Int = RecyclerView.NO_POSITION
    // END

    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.rv_food_pic_history,
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
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.setSelected(selectedPos == position)
        val model = list[position]
        if (model.imageUri != null) {
            holder.itemView.iv_food_pic.visibility = View.VISIBLE
            holder.itemView.iv_food_pic.setImageURI(Uri.fromFile(File(model.imageUri)))
        }
        holder.itemView.food_name_image.text = model.foodName
        holder.itemView.food_calories_image.text = model.calories.toString()
        holder.itemView.date_image.text = model.date

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

    // TODO (Step 6: Create a function to bind the onclickListener)
    // START
    /**
     * A function to bind the onclickListener.
     */
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }
    // END

    // TODO (Step 4: Create an interface for onclickListener)
    // START
    interface OnClickListener {
        fun onClick(position: Int, model: FoodImages)
    }
    // END

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}