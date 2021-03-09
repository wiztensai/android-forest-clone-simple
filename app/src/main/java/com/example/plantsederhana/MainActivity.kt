package com.example.plantsederhana

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.example.plantsederhana.databinding.ActivityMainBinding
import com.google.android.material.slider.Slider
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    lateinit var bind:ActivityMainBinding
    lateinit var countdownTimer:CountDownTimer

    var sliderValue = 1F
    var stepInterval = 5000 // 5s
    var currentExp = 0L
    var running = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)

        // todo: step akhir. cek level
        currentExp = getProgress()
        isLevelUp()

        resetUI(true)
        onListener()
    }

    fun onListener() {
        bind.slider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
                // Responds to when slider's touch event is being started
            }

            override fun onStopTrackingTouch(slider: Slider) {
                // Responds to when slider's touch event is being stopped
                sliderValue = slider.value

                bind.tvTimer.text = formatWaktu((stepInterval * slider.value).toLong())
            }
        })

        bind.button.setOnClickListener {
            if (running == true) {
                resetUI(true)
                stopPlant()
            } else {
                resetUI(false)
                startPlant()
            }

            running = !running
        }

        bind.btnResetLevel.setOnClickListener {
            saveProgress(0L)
            currentExp = getProgress()
            isLevelUp()
        }
    }

    fun startPlant () {
        countdownTimer = object : CountDownTimer((stepInterval * sliderValue).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                bind.tvTimer.text = formatWaktu(millisUntilFinished)
            }

            override fun onFinish() {
                currentExp = currentExp + (stepInterval * sliderValue).toLong()

                resetUI(true)

                running = !running

                saveProgress(currentExp)
                isLevelUp()
            }
        }.start()
    }

    fun stopPlant () {
        countdownTimer.cancel()
    }

    fun formatWaktu(millisUntilFinished: Long): kotlin.String {
        val waktu = String.format("%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)))

        return waktu
    }

    fun isLevelUp() {
        val maxExp = 5000 // millisecond

        if (currentExp < maxExp) { // lvl 1
            bind.ivPlant.setImageDrawable(resources.getDrawable(R.drawable.plant_lv1))
        }

        if (currentExp >= maxExp) { // lvl 2
            bind.ivPlant.setImageDrawable(resources.getDrawable(R.drawable.plant_lv2))
        }

        if (currentExp >= maxExp*2) { // lvl 3
            bind.ivPlant.setImageDrawable(resources.getDrawable(R.drawable.plant_lv3))
        }
    }

    fun saveProgress(currentExp:Long) {
        val prefs = getSharedPreferences("plant-prefs", Context.MODE_PRIVATE)
        prefs.edit().putLong("exp", currentExp).apply()
    }

    fun getProgress():Long {
        val prefs = getSharedPreferences("plant-prefs", Context.MODE_PRIVATE)
        return prefs.getLong("exp", 0L)
    }

    fun resetUI(b:Boolean) {
        if (b) {
            bind.tvSuggest.text = "Start planting today!"
            bind.button.text = "Plant"

            // format timer
            bind.tvTimer.text = formatWaktu((stepInterval * sliderValue).toLong())
        } else {
            bind.tvSuggest.text = "Stop phubbing!"
            bind.button.text = "Give up"
        }
    }
}