package il.mfix.linktest

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
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
        addLog(if (installed) "נמצאה אפליקציית יש חשבונית." else "לא נמצאה אפליקציית יש חשבונית בחבילה $targetPackage.")
        addLog("זהו ניסוי תקשורת בלבד. לא נוצרת חשבונית ולא מתבצע שינוי בחשבון.")
    }

    private fun buildUi() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutDirection = ViewGroup.LAYOUT_DIRECTION_RTL
            setPadding(28, 24, 28, 24)
            setBackgroundColor(0xFFF4F6F9.toInt())
        }
        root.addView(TextView(this).apply {
            text = "MFIX – בדיקת חיבור"
            textSize = 26f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(0xFF142238.toInt())
            gravity = Gravity.RIGHT
        }, matchWrap())

        root.addView(TextView(this).apply {
            text = "בדיקת תקשורת בין MFIX לאפליקציית יש חשבונית. שליחת טקסט בלבד; אין הפקת מסמך."
            textSize = 17f
            setTextColor(0xFF46556A.toInt())
            gravity = Gravity.RIGHT
            setPadding(0, 14, 0, 22)
        }, matchWrap())

        root.addView(makeButton("1. פתח את יש חשבונית") { openInvoiceApp() }, matchWrap())
        root.addView(makeButton("2. שלח פרטי מכירת בדיקה") { sendTestShare() }, matchWrap())
        root.addView(makeButton("נקה יומן") { logView.text = "" }, matchWrap())

        root.addView(TextView(this).apply {
            text = "תוצאות הבדיקה"
            textSize = 20f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(0xFF142238.toInt())
            gravity = Gravity.RIGHT
            setPadding(0, 22, 0, 8)
        }, matchWrap())

        logView = TextView(this).apply {
            textSize = 15f
            setTextColor(0xFF172235.toInt())
            gravity = Gravity.RIGHT
            textDirection = ViewGroup.TEXT_DIRECTION_RTL
            setPadding(14, 14, 14, 14)
            setBackgroundColor(0xFFFFFFFF.toInt())
        }
        val scroll = ScrollView(this).apply { addView(logView) }
        root.addView(scroll, LinearParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
        setContentView(root)
    }

    private fun openInvoiceApp() {
        try {
            val launch = packageManager.getLaunchIntentForPackage(targetPackage)
            if (launch == null) {
                addLog("לא נמצאה אפליקציה מותקנת עם package id: $targetPackage")
                return
            }
            startActivity(launch)
            addLog("Android פתח את אפליקציית יש חשבונית.")
        } catch (e: Exception) {
            addLog("פתיחה נכשלה: ${e.javaClass.simpleName}: ${e.message}")
        }
    }

    private fun sendTestShare() {
        val text = listOf(
            "MFIX – בדיקת תקשורת בלבד",
            "מוצר: מגן מסך (בדיקה)",
            "כמות: 1",
            "מחיר: 50.00 ILS",
            "סה״כ: 50.00 ILS",
            "מזהה בדיקה: MFIX-LINK-TEST-001",
            "אין להפיק מסמך. זו הודעת בדיקה בלבד."
        ).joinToString("\n")

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "MFIX test")
            putExtra(Intent.EXTRA_TEXT, text)
            setPackage(targetPackage)
        }
        try {
            startActivity(intent)
            addLog("נשלח ACTION_SEND אל $targetPackage.")
            addLog("בדוק מה הופיע באפליקציה. פתיחת פעילות אינה מוכיחה שהטקסט יובא למסמך.")
        } catch (e: ActivityNotFoundException) {
            addLog("האפליקציה לא חשפה פעילות שמקבלת ACTION_SEND מסוג text/plain.")
        } catch (e: Exception) {
            addLog("שליחה נכשלה: ${e.javaClass.simpleName}: ${e.message}")
        }
    }

    private fun makeButton(label: String, action: () -> Unit) = Button(this).apply {
        text = label
        textSize = 17f
        isAllCaps = false
        setOnClickListener { action() }
        layoutParams = LinearParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun addLog(message: String) {
        if (!::logView.isInitialized) return
        val time = SimpleDateFormat("HH:mm:ss", Locale("he", "IL")).format(Date())
        logView.append("[$time] $message\n\n")
    }

    private fun matchWrap() = LinearParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    private fun LinearParams(w: Int, h: Int, weight: Float = 0f) = LinearLayout.LayoutParams(w, h, weight)
}
