package com.cleaneditor.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.delay
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.cleaneditor.app.data.ReminderScheduler
import com.cleaneditor.app.data.DraftRecovery
import com.cleaneditor.app.ui.CleanEditorApp

class MainActivity : ComponentActivity() {
    private val vm: AppViewModel by viewModels()
    private val notifPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ReminderScheduler.ensureChannel(this)
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) notifPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        if (savedInstanceState == null) handle(intent)
        setContent {
            LaunchedEffect(vm) {
                snapshotFlow { vm.docs.map { it.uri to it.text } }
                    .collectLatest {
                        delay(1800)
                        vm.docs.filter { it.dirty }.forEach { doc ->
                            DraftRecovery.backup(applicationContext, doc.uri, doc.name, doc.text)
                            vm.save(doc)
                        }
                    }
            }
            CleanEditorApp(vm)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handle(intent)
    }

    private fun handle(intent: Intent?) {
        if (intent == null) return
        if (intent.getStringExtra("tab") == "lembretes") { vm.editorOpen = false; vm.tab = Tab.LEMBRETES }
        val data = intent.data
        if ((intent.action == Intent.ACTION_VIEW || intent.action == Intent.ACTION_EDIT) && data != null) vm.open(data)
    }
}
