package il.mfix.linktest

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class InvoiceScreenProbeService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || event.packageName?.toString() != "com.app.yeshinvoice") return
        val root = rootInActiveWindow ?: return
        val lines = mutableListOf<String>()
        fun walk(node: AccessibilityNodeInfo?, depth: Int) {
            if (node == null || lines.size >= 180 || depth > 18) return
            val text = node.text?.toString()?.replace("\n", " ")?.take(100).orEmpty()
            val hint = node.hintText?.toString()?.replace("\n", " ")?.take(80).orEmpty()
            val id = node.viewIdResourceName.orEmpty()
            val cls = node.className?.toString()?.substringAfterLast('.').orEmpty()
            if (text.isNotBlank() || hint.isNotBlank() || id.isNotBlank() || node.isEditable) {
                lines.add("${"  ".repeat(depth)}class=$cls text=$text hint=$hint id=$id editable=${node.isEditable}")
            }
            for (i in 0 until node.childCount) walk(node.getChild(i), depth + 1)
        }
        walk(root, 0)
        getSharedPreferences("probe", MODE_PRIVATE).edit()
            .putString("package", event.packageName?.toString().orEmpty())
            .putString("screen", lines.joinToString("\n"))
            .putLong("captured", System.currentTimeMillis())
            .apply()
    }
    override fun onInterrupt() {}
}