package com.example.hw2_android.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hw2_android.databinding.HighScoreBinding
import com.example.hw2_android.interfaces.CallbackHighScoreClicked
import com.example.hw2_android.item_model.Score


class HighScoreAdapter (private var scoreList: MutableList<Score>) :

    RecyclerView.Adapter<HighScoreAdapter.ScoreViewHolder>() {
    var callback: CallbackHighScoreClicked? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        val binding = HighScoreBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ScoreViewHolder(binding)
    }

    override fun getItemCount(): Int = scoreList.size

    private fun getItem(position: Int): Score = scoreList[position]

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        with(holder) {
            with(getItem(position)) {
                binding.hsitemLBLScore.text = buildString {
                    append("Score: ")
                    append(scoreValue)
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateScores(newScores: List<Score>) {
        scoreList.clear()
        scoreList.addAll(newScores)
        notifyDataSetChanged()
    }

    inner class ScoreViewHolder(val binding: HighScoreBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.itemCLScore.setOnClickListener {
                val score = getItem(adapterPosition)
                callback?.highScoreClicked(score.latitude, score.longitude)
            }
        }
    }
}