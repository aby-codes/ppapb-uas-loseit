    package com.example.loseit.food

    import android.view.LayoutInflater
    import android.view.ViewGroup
    import androidx.recyclerview.widget.RecyclerView
    import com.example.loseit.database.FirebaseFoodItem
    import com.example.loseit.databinding.FirebaseFoodItemBinding

    class FirebaseFoodAdapter(
        private val foodList: ArrayList<FirebaseFoodItem>,
        private val listener: OnAdapterListener
    ) : RecyclerView.Adapter<FirebaseFoodAdapter.FoodViewHolder>() {

        inner class FoodViewHolder(val binding: FirebaseFoodItemBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
            val binding = FirebaseFoodItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            return FoodViewHolder(binding)
        }

        override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
            holder.binding.apply {
                tvMakan.text = foodList[position].name
                tvCalories.text = foodList[position].calories.toString() + " kcal"
                btnAdd.setOnClickListener(){
                    listener.onClick(foodList[position])
                }
            }
        }

        override fun getItemCount(): Int {
            return foodList.size
        }

        fun setData(list: List<FirebaseFoodItem>) {
            foodList.clear()
            foodList.addAll(list)
            notifyDataSetChanged()
        }

        interface OnAdapterListener{
            fun onClick(food: FirebaseFoodItem)
        }

    }