    package com.example.loseit.food

    import android.view.LayoutInflater
    import android.view.ViewGroup
    import androidx.recyclerview.widget.RecyclerView
    import com.example.loseit.database.FoodItem
    import com.example.loseit.databinding.FoodItemBinding

    class FoodAdapter(
        private val foodList: ArrayList<FoodItem>,
        private val listener:OnAdapterListener
    ) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

        inner class FoodViewHolder(val binding: FoodItemBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
            val binding = FoodItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            return FoodViewHolder(binding)
        }

        override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
            holder.binding.apply {
                val foodItem = foodList[position]

                tvMakan.text = foodItem.name
                tvJam.text = foodItem.time

                tvCalories.text = foodItem.calories.toString() + " kcal"

                btnDelete.setOnClickListener {
                    listener.onDelete(foodItem)
                }
            }
        }

        override fun getItemCount(): Int {
            return foodList.size
        }

        fun setData(list: List<FoodItem>) {
            foodList.clear()
            foodList.addAll(list)
            notifyDataSetChanged()
        }


        interface OnAdapterListener{
            fun onDelete(food: FoodItem)
        }
    }