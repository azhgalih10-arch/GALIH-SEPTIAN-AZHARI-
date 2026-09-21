package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.DuesViewModel
import com.example.ui.components.PaymentBottomSheet
import com.example.ui.components.ReceiptDialog
import com.example.ui.screens.BillsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NotificationScreen
import com.example.ui.screens.TransparencyScreen
import com.example.ui.theme.AmberGold
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        MainAppScreen()
      }
    }
  }
}

@Composable
fun MainAppScreen(duesViewModel: DuesViewModel = viewModel()) {
  val context = LocalContext.current
  val snackbarHostState = remember { SnackbarHostState() }

  // State collectors
  val selectedResident by duesViewModel.selectedResident.collectAsStateWithLifecycle()
  val residents by duesViewModel.residents.collectAsStateWithLifecycle()
  val bills by duesViewModel.billsForResident.collectAsStateWithLifecycle()
  val unpaidBills by duesViewModel.unpaidBillsForResident.collectAsStateWithLifecycle()
  val allBills by duesViewModel.allBills.collectAsStateWithLifecycle()
  val ledgerEntries by duesViewModel.ledgerEntries.collectAsStateWithLifecycle()
  val notifications by duesViewModel.notifications.collectAsStateWithLifecycle()
  val unreadCount by duesViewModel.unreadNotificationCount.collectAsStateWithLifecycle()
  val cashSummary by duesViewModel.cashSummary.collectAsStateWithLifecycle()

  val payingBill by duesViewModel.payingBill.collectAsStateWithLifecycle()
  val activeReceiptBill by duesViewModel.activeReceiptBill.collectAsStateWithLifecycle()
  val toastMessage by duesViewModel.toastMessage.collectAsStateWithLifecycle()

  var currentNavIndex by rememberSaveable { mutableIntStateOf(0) }

  // Request Notification Permission on Android 13+
  val permissionLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted) {
      Toast.makeText(context, "Izin notifikasi tagihan diaktifkan!", Toast.LENGTH_SHORT).show()
    }
  }

  LaunchedEffect(Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      if (ContextCompat.checkSelfPermission(
          context,
          Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
      ) {
        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
      }
    }
  }

  // Toast listener
  LaunchedEffect(toastMessage) {
    toastMessage?.let { msg ->
      snackbarHostState.showSnackbar(
        message = msg,
        duration = SnackbarDuration.Short
      )
      duesViewModel.clearToast()
    }
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    snackbarHost = { SnackbarHost(snackbarHostState) },
    bottomBar = {
      NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier
          .windowInsetsPadding(WindowInsets.navigationBars)
          .testTag("main_bottom_nav")
      ) {
        // Tab 0: Beranda
        NavigationBarItem(
          selected = currentNavIndex == 0,
          onClick = { currentNavIndex = 0 },
          icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Beranda") },
          label = { Text("Beranda") },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = EmeraldDark,
            selectedTextColor = EmeraldDark,
            indicatorColor = EmeraldLight
          ),
          modifier = Modifier.testTag("nav_home")
        )

        // Tab 1: Tagihan
        NavigationBarItem(
          selected = currentNavIndex == 1,
          onClick = { currentNavIndex = 1 },
          icon = {
            BadgedBox(
              badge = {
                if (unpaidBills.isNotEmpty()) {
                  Badge(containerColor = AmberGold) {
                    Text(text = unpaidBills.size.toString())
                  }
                }
              }
            ) {
              Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = "Tagihan")
            }
          },
          label = { Text("Tagihan") },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = EmeraldDark,
            selectedTextColor = EmeraldDark,
            indicatorColor = EmeraldLight
          ),
          modifier = Modifier.testTag("nav_bills")
        )

        // Tab 2: Transparansi
        NavigationBarItem(
          selected = currentNavIndex == 2,
          onClick = { currentNavIndex = 2 },
          icon = { Icon(imageVector = Icons.Default.AccountBalance, contentDescription = "Transparansi") },
          label = { Text("Transparansi") },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = EmeraldDark,
            selectedTextColor = EmeraldDark,
            indicatorColor = EmeraldLight
          ),
          modifier = Modifier.testTag("nav_transparency")
        )

        // Tab 3: Notifikasi
        NavigationBarItem(
          selected = currentNavIndex == 3,
          onClick = { currentNavIndex = 3 },
          icon = {
            BadgedBox(
              badge = {
                if (unreadCount > 0) {
                  Badge(containerColor = AmberGold) {
                    Text(text = unreadCount.toString())
                  }
                }
              }
            ) {
              Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notifikasi")
            }
          },
          label = { Text("Notifikasi") },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = EmeraldDark,
            selectedTextColor = EmeraldDark,
            indicatorColor = EmeraldLight
          ),
          modifier = Modifier.testTag("nav_notifications")
        )
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (currentNavIndex) {
        0 -> HomeScreen(
          selectedResident = selectedResident,
          residents = residents,
          bills = bills,
          unpaidBills = unpaidBills,
          cashSummary = cashSummary,
          unreadNotificationCount = unreadCount,
          onSelectResident = { duesViewModel.selectResident(it) },
          onPayBill = { duesViewModel.openPayment(it) },
          onViewReceipt = { duesViewModel.showReceipt(it) },
          onNavigateToBills = { currentNavIndex = 1 },
          onNavigateToTransparency = { currentNavIndex = 2 },
          onNavigateToNotifications = { currentNavIndex = 3 },
          onTriggerNotificationTest = { duesViewModel.triggerBillNotification() }
        )
        1 -> BillsScreen(
          resident = selectedResident,
          bills = bills,
          onPayBill = { duesViewModel.openPayment(it) },
          onViewReceipt = { duesViewModel.showReceipt(it) }
        )
        2 -> TransparencyScreen(
          cashSummary = cashSummary,
          ledgerEntries = ledgerEntries,
          allBills = allBills
        )
        3 -> NotificationScreen(
          notifications = notifications,
          unreadCount = unreadCount,
          onMarkAsRead = { duesViewModel.markNotificationAsRead(it) },
          onMarkAllAsRead = { duesViewModel.markAllNotificationsAsRead() },
          onTriggerNotificationTest = { duesViewModel.triggerBillNotification() }
        )
      }
    }

    // Payment Dialog / BottomSheet
    payingBill?.let { billToPay ->
      PaymentBottomSheet(
        bill = billToPay,
        onDismiss = { duesViewModel.dismissPayment() },
        onConfirmPayment = { billId, method ->
          duesViewModel.payBill(billId, method)
        }
      )
    }

    // Verified Digital Receipt Dialog
    activeReceiptBill?.let { receiptBill ->
      ReceiptDialog(
        bill = receiptBill,
        onDismiss = { duesViewModel.dismissReceipt() }
      )
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  MyApplicationTheme { Greeting("Android") }
}

