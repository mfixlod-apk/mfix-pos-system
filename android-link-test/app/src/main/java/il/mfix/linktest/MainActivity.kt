package il.mfix.linktest

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : Activity() {
    private lateinit var logView: TextView
    private val targetPackage = "com.app.yeshinvoice"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildUi()
        val installed = packageManager.getLaunchIntentForPackage(targetPackage) != null
        addLog(if (installed) "נמצאה אפליקציית יש חשבונית." else "לא נמצאה אפליקציית יש חשבונית.")
        addLog("גרסת אבחון: קוראת את שמות השדות והכפתורים במסך, לא מפיקה מסמכים.")
        showSavedScan()
    }

    private fun buildUi() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutDirection = ViewGroup.LAYOUT_DIRECTION_RTL
            setPadding(24, 20, 24, 20)
            setBackgroundColor(0xFFF4F6F9.toInt())
        }
        root.addView(TextView(this).apply {
            text = "MFIX – אבחון חיבור"
            textSize = 25f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(0xFF142238.toInt())
            gravity = Gravity.RIGHT
        }, matchWrap())
        root.addView(TextView(this).apply {
            text = "שלב אבחון למסך חשבונית מס/קבלה. אין הפקת מסמך ואין שליחת מידע החוצה."
            textSize = 16f
            setTextColor(0xFF46556A.toInt())
            gravity = Gravity.RIGHT
            setPadding(0, 12, 0, 16)
        }, matchWrap())
        root.addView(makeButton("1. הפעל הרשאת נגישות ל־MFIX") {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }, matchWrap())
        root.addView(makeButton("2. פתח את יש חשבונית") { openInvoiceApp() }, matchWrap())
        root.addView(makeButton("3. הצג סריקה אחרונה") { showSavedScan() }, matchWrap())
        root.addView(makeButton("העתק דוח סריקה") { copyScan() }, matchWrap())
        root.addView(makeButton("נקה יומן") { logView.text = "" }, matchWrap())
        root.addView(TextView(this).apply {
            text = "דוח מסך"
            textSize = 20f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(0xFF142238.toInt())
            gravity = Gravity.RIGHT
            setPadding(0, 18, 0, 8)
        }, matchWrap())
        logView = TextView(this).apply {
            textSize = 14f
            setTextColor(0xFF172235.toInt())
            gravity = Gravity.RIGHT
            textDirection = ViewGroup.TEXT_DIRECTION_RTL
            setPadding(12, 12, 12, 12)
            setBackgroundColor(0xFFFFFFFF.toInt())
        }
        root.addView(ScrollView(this).apply { addView(logView) },
            LinearParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
        setContentView(root)
    }

    private fun openInvoiceApp() {
        try {
            val launch = packageManager.getLaunchIntentForPackage(targetPackage)
            if (launch == null) {
                addLog("לא נמצאה אפליקציית יש חשבונית.")
                return
            }
            startActivity(launch)
            addLog("האפליקציה נפתחה. היכנס למסך חשבונית מס/קבלה; הסריקה תתעדכן אוטומטית.")
        } catch (e: Exception) {
            addLog("פתיחה נכשלה: ${e.javaClass.simpleName}: ${e.message}")
        }
    }

    private fun showSavedScan() {
        val p = getSharedPreferences("probe", MODE_PRIVATE)
        val report = p.getString("screen", null)
        val captured = p.getLong("captured", 0L)
        if (report.isNullOrBlank()) {
            addLog("עדיין אין סריקה. הפעל הרשאת נגישות, פתח את מסך החשבונית ביש חשבונית, חזור לכאן ולחץ הצג סריקה.")
        } else {
            val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(captured))
            addLog("סריקה אחרונה בשעה $time:\n$report")
        }
    }

    private fun copyScan() {
        val report = getSharedPreferences("probe", MODE_PRIVATE).getString("screen", "").orEmpty()
        if (report.isBlank()) {
            Toast.makeText(this, "אין עדיין דוח להעתקה", Toast.LENGTH_SHORT).show()
            return
        }
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("MFIX screen probe", report))
        Toast.makeText(this, "דוח הסריקה הועתק", Toast.LENGTH_SHORT).show()
    }

    private fun makeButton(label: String, action: () -> Unit) = Button(this).apply {
        text = label
        textSize = 16f
        isAllCaps = false
        setOnClickListener { action() }
        layoutParams = LinearParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun addLog(message: String) {
        if (!::logView.isInitialized) return
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        logView.append("[$time] $message\n\n")
    }

    private fun matchWrap() = LinearParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    private fun LinearParams(w: Int, h: Int, weight: Float = 0f) = LinearLayout.LayoutParams(w, h, weight)
}