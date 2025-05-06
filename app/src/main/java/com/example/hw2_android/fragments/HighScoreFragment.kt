package com.example.hw2_android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.content.Context
import com.example.hw2_android.R
import com.example.hw2_android.item_model.Score
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.hw2_android.logic.ScoreManager
import com.example.hw2_android.adapters.HighScoreAdapter
import com.google.android.material.textview.MaterialTextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hw2_android.interfaces.CallbackHighScoreClicked


class HighScoreFragment  : Fragment() {

    lateinit var highScore_LBL_title: MaterialTextView
    private lateinit var highScore_RV_records: RecyclerView
    private var score: MutableList<Score> = mutableListOf()
    private val highScoreAdapter = HighScoreAdapter(score)

    var highScoreClicked: CallbackHighScoreClicked? = null
        set(value) {
            field = value
            highScoreAdapter.callback = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_high_score, container, false)
        findViews(v)
        initViews(context)
        return v
    }


    private fun findViews(v: View) {
        highScore_LBL_title = v.findViewById(R.id.highScore_LBL_title)
        highScore_RV_records = v.findViewById(R.id.highScore_RV_records)
    }

    private fun initViews(context: Context?) {
        if (context == null) {
            return
        }
        highScore_RV_records.adapter = highScoreAdapter
        highScoreAdapter.callback = object : CallbackHighScoreClicked {
            override fun highScoreClicked(lat: Double, lon: Double) {
                highScoreClicked?.highScoreClicked(lat, lon)
            }
        }
        highScore_RV_records.layoutManager = LinearLayoutManager(context)
    }

    fun updateHighScore() {
        val newScores = ScoreManager.getInstance(requireContext()).scores
        highScoreAdapter.updateScores(newScores)
    }
}