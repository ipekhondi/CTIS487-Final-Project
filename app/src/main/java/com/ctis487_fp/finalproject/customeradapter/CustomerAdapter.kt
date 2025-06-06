package com.ctis487_fp.finalproject.customeradapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ctis487_fp.finalproject.R
import com.ctis487_fp.finalproject.model.Medicine

class CustomerAdapter(
    private val items: MutableList<Medicine>,
    private val onLongPress: (Medicine, Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_ONE = 1
        const val TYPE_TWO = 2
    }

    // Update the list of items
    fun updateItems(newItems: List<Medicine>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_ONE) {
            val view = inflater.inflate(R.layout.item_type_one, parent, false)
            TypeOneViewHolder(view, onLongPress)
        } else {
            val view = inflater.inflate(R.layout.item_type_two, parent, false)
            TypeTwoViewHolder(view, onLongPress)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is TypeOneViewHolder -> {
                holder.bind(item, position)
            }
            is TypeTwoViewHolder -> {
                holder.bind(item, position)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    class TypeOneViewHolder(
        view: View,
        private val onLongPress: (Medicine, Int) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.textView6)
        private val description: TextView = view.findViewById(R.id.textView7)

        init {
            itemView.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onLongPress(getItemData(), position)
                }
                true
            }
        }

        fun bind(item: Medicine, position: Int) {
            title.text = item.title
            description.text = item.description
        }

        private fun getItemData(): Medicine {
            return Medicine(
                TYPE_ONE,
                title.text.toString(),
                description.text.toString()
            )
        }
    }

    class TypeTwoViewHolder(
        view: View,
        private val onLongPress: (Medicine, Int) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        private val text: TextView = view.findViewById(R.id.textView7)
        private val description: TextView = view.findViewById(R.id.textView8)

        init {
            itemView.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onLongPress(getItemData(), position)
                }
                true
            }
        }

        fun bind(item: Medicine, position: Int) {
            text.text = item.title
            description.text = item.description
        }

        private fun getItemData(): Medicine {
            return Medicine(
                TYPE_TWO,
                text.text.toString(),
                description.text.toString()
            )
        }
    }
}