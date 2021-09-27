package com.kent.sketchbook

import android.graphics.*
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    enum class DrawMode {
        PEN, STAMP
    }

    private var penColor = Color.TRANSPARENT
    private var penId = 0
    private var penX = -1f
    private var penY = -1f
    private var path: Path = Path()
    private var paint: Paint = Paint()
    private var canvas: Canvas? = null
    private var bitmap: Bitmap = Bitmap.createBitmap(1,1, Bitmap.Config.ARGB_8888)
    private var drawMode:DrawMode = DrawMode.PEN

    private val TICKNESS50: Float = 50f
    private val TICKNESS40: Float = 40f
    private val TICKNESS30: Float = 30f
    private val TICKNESS20: Float = 20f
    private val TICKNESS10: Float = 10f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val closeButton = findViewById<ImageView>(R.id.close)
        val penLayout = findViewById<LinearLayout>(R.id.pen_layout)
        val tickLayout = findViewById<LinearLayout>(R.id.tickness_layout)

        closeButton.setOnClickListener {
            finish()
        }

        for (i in 0 until penLayout.childCount) {
            try {
                penSetting(penLayout.getChildAt(i) as ImageView);
            } catch (e: Exception) {

            }
        }

        for (i in 0 until tickLayout.childCount) {
            try {
                ticknessSetting(tickLayout.getChildAt(i) as ImageView);
            } catch (e: Exception) {

            }
        }

        val draw = findViewById<ImageView>(R.id.draw_area)
        paint!!.strokeWidth = TICKNESS20
        paint!!.style = Paint.Style.STROKE
        paint!!.strokeJoin = Paint.Join.ROUND
        paint!!.strokeCap = Paint.Cap.ROUND
        paint!!.isDither = true
        paint!!.isAntiAlias = true

        draw.setOnTouchListener{ view: View, motionEvent: MotionEvent ->
            if (canvas == null && draw.width > 0 && draw.height > 0) {
                bitmap = Bitmap.createBitmap(draw.width, draw.height, Bitmap.Config.ARGB_8888);
                canvas = Canvas(bitmap)
                canvas?.drawColor(Color.WHITE);
                draw.setImageBitmap(bitmap)
            }
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    paint?.color = penColor
                    penX = motionEvent.x
                    penY = motionEvent.y
                    if (path == null) {
                        path = Path()
                    } else {
                        path?.reset()
                    }
                    path?.moveTo(penX, penY)
                }
                MotionEvent.ACTION_MOVE -> {
                    var newpenX = motionEvent.x
                    var newpenY = motionEvent.y
                    path?.quadTo(penX, penY, newpenX, newpenY);
                    canvas?.drawPath(path!!, paint);
                    draw.setImageBitmap(bitmap)
                    penX = newpenX
                    penY = newpenY
                    path?.moveTo(penX, penY)
                }
                MotionEvent.ACTION_UP -> {
                    var newpenX = motionEvent.x
                    var newpenY = motionEvent.y
                    path?.quadTo(penX, penY, newpenX, newpenY);
                    canvas?.drawPath(path!!, paint);
                    draw.setImageBitmap(bitmap)
                    penX = 0f
                    penY = 0f
                }
            }

            true
        }
    }

    private fun penSetting(x: ImageView): ImageView {
        var color: Int = Color.TRANSPARENT
        when (x.id) {
            R.id.red -> {
                color = Color.RED
            }
            R.id.orange -> {
                color = 0xFFFFA500.toInt();
            }
            R.id.yellow -> {
                color = Color.YELLOW
            }
            R.id.green -> {
                color = Color.GREEN
            }
            R.id.blue -> {
                color = Color.BLUE
            }
            R.id.magenta -> {
                color = Color.MAGENTA
            }
            R.id.black -> {
                color = Color.BLACK
            }
            R.id.eraser -> {
                color = Color.WHITE
            }
        }

        x.setOnClickListener{
            drawMode = DrawMode.PEN
            if (penColor != Color.TRANSPARENT) {
                var view = findViewById<ImageView>(penId)
                val beforeParams = view.layoutParams as ViewGroup.MarginLayoutParams
                beforeParams.setMargins(beforeParams.leftMargin, convertDpToPx(20, baseContext), beforeParams.rightMargin, beforeParams.bottomMargin)
                view.layoutParams = beforeParams;
            }

            penColor = color
            penId = x.id;
            val params = it.layoutParams as ViewGroup.MarginLayoutParams
            params.setMargins(params.leftMargin, 0, params.rightMargin, params.bottomMargin)
            it.layoutParams = params;

            var cf = PorterDuffColorFilter(penColor, PorterDuff.Mode.OVERLAY)

            penTicknessColorSetting(cf)

        }

        return x
    }

    private fun ticknessSetting(x: ImageView) {
        var penTick = 0f
        when (x.id) {
            R.id.tick_max -> {
                penTick = TICKNESS50
            }
            R.id.tick_upper_mid -> {
                penTick = TICKNESS40
            }
            R.id.tick_mid -> {
                penTick = TICKNESS30
            }
            R.id.tick_under_mid -> {
                penTick = TICKNESS20
            }
            R.id.tick_min -> {
                penTick = TICKNESS10
            }
        }

        x.setOnClickListener{
            paint!!.strokeWidth = penTick
        }

    }

    public fun penTicknessColorSetting(colorFilter: ColorFilter) {

        var drawable: ShapeDrawable = ShapeDrawable(OvalShape())
//        drawable.paint.colorFilter = colorFilter
        //drawable.paint.color = color

        findViewById<ImageView>(R.id.tick_max).colorFilter = colorFilter
        findViewById<ImageView>(R.id.tick_upper_mid).colorFilter = colorFilter
        findViewById<ImageView>(R.id.tick_mid).colorFilter = colorFilter
        findViewById<ImageView>(R.id.tick_under_mid).colorFilter = colorFilter
        findViewById<ImageView>(R.id.tick_min).colorFilter = colorFilter
    }
}