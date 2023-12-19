package moe.cyunrei.videolivewallpaper.activity.fragments.ProgressPointsView;

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class ProgressPointsView(context: Context, attrs: AttributeSet) : View(context, attrs) {

        private val paint = Paint().apply {
                color = Color.BLACK
                style = Paint.Style.FILL
        }

        private var points = 0
        private var wavePhase = 0f

        fun setPoints(points: Int) {
                this.points = points
                invalidate()
        }

        override fun onDraw(canvas: Canvas) {
                super.onDraw(canvas)

                val radius = 20f // Increase the radius to make the points bigger
                val spacing = 40f // Increase the spacing to accommodate the bigger points
                val startX = (width - (points * (radius * 2 + spacing) - spacing)) / 2

                for (i in 0 until points) {
                        // Use colors that blend together
                        val red = (Math.sin(i.toDouble()) * 127 + 128).toInt()
                        val green = (Math.sin(i.toDouble() + 2 * Math.PI / 3) * 127 + 128).toInt()
                        val blue = (Math.sin(i.toDouble() + 4 * Math.PI / 3) * 127 + 128).toInt()

                        paint.color = Color.rgb(red, green, blue)

                        val x = startX + i * (radius * 2 + spacing)
                        val y = height / 2f + radius * Math.sin(wavePhase + i * Math.PI / 4).toFloat()
                        canvas.drawCircle(x, y, radius, paint)
                }

                // Update the wave phase
                wavePhase += 0.1f
                if (wavePhase > Math.PI * 2) {
                        wavePhase -= Math.PI.toFloat() * 2
                }

                // Redraw the view
                invalidate()
        }

        // Add an animation
        fun startAnimation() {
                val animator = ValueAnimator.ofInt(0, points).apply {
                        duration = 1000
                        repeatMode = ValueAnimator.RESTART
                        repeatCount = ValueAnimator.INFINITE
                }

                animator.addUpdateListener { animation ->
                        setPoints(animation.animatedValue as Int)
                }

                animator.start()
        }
}
