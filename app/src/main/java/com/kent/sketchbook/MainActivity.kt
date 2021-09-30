package com.kent.sketchbook

import android.graphics.*
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.SeekBar
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

    private var stampSize = 1f
    private var stampInterval = 1f
    private var stampBitmap:Bitmap? = null

    private val TICKNESS50: Float = 50f
    private val TICKNESS40: Float = 40f
    private val TICKNESS30: Float = 30f
    private val TICKNESS20: Float = 20f
    private val TICKNESS10: Float = 10f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val closeButton = findViewById<ImageView>(R.id.close)
        closeButton.setOnClickListener {
            finish()
        }

        val penButton = findViewById<RadioButton>(R.id.pen)
        penButton.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                selectMode(DrawMode.PEN)
            }
        }

        val stampButton = findViewById<RadioButton>(R.id.stamp)
        stampButton.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                selectMode(DrawMode.STAMP)
            }
        }

        penButton.isChecked = true
    }

    private fun selectMode(mode: DrawMode) {
        if (mode == DrawMode.PEN) {
            findViewById<View>(R.id.layout_pen).visibility = View.VISIBLE
            findViewById<View>(R.id.layout_stamp).visibility = View.GONE
            initPenMode()
        } else if (mode == DrawMode.STAMP) {
            findViewById<View>(R.id.layout_pen).visibility = View.GONE
            findViewById<View>(R.id.layout_stamp).visibility = View.VISIBLE
            initStampMode()
        }
    }

    private fun initPenMode() {
        val penLayout = findViewById<LinearLayout>(R.id.pen_layout)
        val tickLayout = findViewById<LinearLayout>(R.id.tickness_layout)

        for (i in 0 until penLayout.childCount) {
            try {
                penSetting(penLayout.getChildAt(i) as ImageView);
            } catch (e: Exception) {

            }
        }

        for (i in 0 until tickLayout.childCount) {
            try {
                penTicknessSetting(tickLayout.getChildAt(i) as ImageView);
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

            penTicknessColorSetting(penColor)
        }

        return x
    }

    private fun penTicknessSetting(x: ImageView) {
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

    fun penTicknessColorSetting(color: Int) {
        var drawable: Int = 0
        when (color) {
            Color.RED -> {
                drawable = R.drawable.tickness_red
            }
            Color.YELLOW -> {
                drawable = R.drawable.tickness_yellow
            }
            0xFFFFA500.toInt() -> {
                drawable = R.drawable.tickness_orange
            }
            Color.GREEN -> {
                drawable = R.drawable.tickness_green
            }
            Color.BLUE -> {
                drawable = R.drawable.tickness_blue
            }
            Color.BLACK -> {
                drawable = R.drawable.tickness_black
            }
            Color.WHITE -> {
                drawable = R.drawable.tickness_eraser
            }
        }

        findViewById<ImageView>(R.id.tick_max).setImageResource(drawable)
        findViewById<ImageView>(R.id.tick_upper_mid).setImageResource(drawable)
        findViewById<ImageView>(R.id.tick_mid).setImageResource(drawable)
        findViewById<ImageView>(R.id.tick_under_mid).setImageResource(drawable)
        findViewById<ImageView>(R.id.tick_min).setImageResource(drawable)
    }

    private fun initStampMode() {
        val stampLayout = findViewById<LinearLayout>(R.id.stamp_layout)

        for (i in 0 until stampLayout.childCount) {
            try {
                stampSetting(stampLayout.getChildAt(i) as ImageView);
            } catch (e: Exception) {

            }
        }

        val draw = findViewById<ImageView>(R.id.draw_area)
        draw.setOnTouchListener{ view: View, motionEvent: MotionEvent ->
            if (canvas == null && draw.width > 0 && draw.height > 0) {
                bitmap = Bitmap.createBitmap(draw.width, draw.height, Bitmap.Config.ARGB_8888);
                canvas = Canvas(bitmap)
                canvas?.drawColor(Color.WHITE);
                draw.setImageBitmap(bitmap)
            }

            if (stampBitmap == null || stampBitmap!!.width == 0 || stampBitmap!!.height == 0) {
                false
            }

            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    var right = motionEvent.x.toInt() + ((stampBitmap!!.width * 0.3f).toInt())
                    var bottom = motionEvent.y.toInt() + ((stampBitmap!!.height * 0.3f).toInt())
                    var dst : Rect = Rect(motionEvent.x.toInt(), motionEvent.y.toInt(), right, bottom)
                    canvas?.drawBitmap(stampBitmap!!, null, dst, null);
                    //canvas?.drawBitmap(stampBitmap!!, motionEvent.x, motionEvent.y, null);
                    draw.setImageBitmap(bitmap)
                }
//                MotionEvent.ACTION_MOVE -> {
//                    var dst : Rect = Rect(motionEvent.x.toInt(), motionEvent.y.toInt(), ((stampBitmap!!.width * stampSize).toInt()), ((stampBitmap!!.height * stampSize).toInt()))
//                    //canvas?.drawBitmap(stampBitmap!!, null, dst, null);
//                    canvas?.drawBitmap(stampBitmap!!, motionEvent.x, motionEvent.y, null);
//                    draw.setImageBitmap(bitmap)
//                }
                MotionEvent.ACTION_UP -> {
                    //draw.setImageBitmap(bitmap)
                }
            }

            true
        }
    }

    private fun stampSetting(x: ImageView): ImageView {
        var shape = 0
        when (x.id) {
            R.id.circle -> {
                shape = R.drawable.circle
            }
            R.id.triangle -> {
                shape = R.drawable.triangle
            }
            R.id.square -> {
                shape = R.drawable.square
            }
            R.id.star -> {
                shape = R.drawable.star
            }
            R.id.sun -> {
                shape = R.drawable.sun
            }
            R.id.moon -> {
                shape = R.drawable.moon
            }
        }

        x.setOnClickListener{
            stampBitmap = BitmapFactory.decodeResource(resources, shape)
        }


        return x
    }
}