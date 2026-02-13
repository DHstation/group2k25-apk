package com.quantiumcode.group2k25

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.messaging.FirebaseMessaging
import com.quantiumcode.group2k25.data.api.models.RegisterDeviceRequest
import com.quantiumcode.group2k25.databinding.ActivityMainBinding
import com.quantiumcode.group2k25.util.Result
import com.quantiumcode.group2k25.util.gone
import com.quantiumcode.group2k25.util.visible
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var isInAuthFlow = true
    private var pendingNotificationIntent: Intent? = null

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        Log.d("MainActivity", "Notification permission granted: $granted")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController

        // Hide bottom nav initially (auth flow is default)
        binding.navView.gone()

        // Control bottom nav visibility based on destination
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val topLevelDestinations = setOf(
                R.id.navigation_home,
                R.id.navigation_contracts,
                R.id.navigation_installments,
                R.id.navigation_more
            )
            when {
                destination.id in topLevelDestinations -> binding.navView.visible()
                else -> binding.navView.gone()
            }
        }

        // Log all intent extras for debugging
        intent?.extras?.let { extras ->
            Log.d("MainActivity", "Intent extras: ${extras.keySet().map { "$it=${extras.getString(it)}" }}")
        }

        // Save notification intent if app was opened from notification
        if (intent?.extras?.keySet()?.any { it in listOf("navigate_to", "type", "installment_id") } == true) {
            pendingNotificationIntent = intent
            Log.d("MainActivity", "Saved pending notification intent")
        }

        // Default is auth_nav_graph (login screen) from XML.
        // Only switch to main if user has a valid token.
        checkAuthAndNavigate()
    }

    private fun checkAuthAndNavigate() {
        val app = application as App
        val tokenManager = app.container.tokenManager

        Log.d("MainActivity", "hasToken=${tokenManager.hasToken()}")

        if (!tokenManager.hasToken()) {
            // Already on auth_nav_graph from XML, nothing to do
            return
        }

        // Validate token with backend
        binding.splashLoading.visible()

        lifecycleScope.launch {
            val result = app.container.authRepository.getMe()
            binding.splashLoading.gone()
            when (result) {
                is Result.Success -> {
                    Log.d("MainActivity", "Token valid, navigating to main")
                    navigateToMain()
                    requestNotificationPermission()
                    registerFcmToken()
                    // Process pending notification after nav graph is ready
                    pendingNotificationIntent?.let {
                        binding.root.post { handleNotificationIntent(it) }
                        pendingNotificationIntent = null
                    }
                }
                else -> {
                    Log.d("MainActivity", "Token invalid, staying on auth")
                    tokenManager.clearAll()
                    // Already on auth_nav_graph, nothing to do
                }
            }
        }
    }

    private fun navigateToMain() {
        isInAuthFlow = false
        val navGraph = navController.navInflater.inflate(R.navigation.mobile_navigation)
        navController.graph = navGraph

        binding.navView.setOnItemSelectedListener { item ->
            if (navController.currentDestination?.id == item.itemId) return@setOnItemSelectedListener true
            // Pop back to home, then navigate to selected tab
            navController.popBackStack(R.id.navigation_home, false)
            if (item.itemId != R.id.navigation_home) {
                navController.navigate(item.itemId)
            }
            true
        }

        binding.navView.setOnItemReselectedListener { item ->
            navController.popBackStack(item.itemId, false)
        }

        // Sync bottom nav selection when navigating via back button or deep link
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val menuItemId = when (destination.id) {
                R.id.navigation_home -> R.id.navigation_home
                R.id.navigation_contracts, R.id.contractDetailFragment -> R.id.navigation_contracts
                R.id.navigation_installments, R.id.paymentFragment -> R.id.navigation_installments
                R.id.navigation_more, R.id.simulatorFragment, R.id.loanRequestFragment,
                R.id.documentUploadFragment, R.id.proposalFragment, R.id.pixKeyFragment -> R.id.navigation_more
                else -> null
            }
            menuItemId?.let { binding.navView.menu.findItem(it)?.isChecked = true }
        }

        binding.navView.visible()
    }

    private fun navigateToAuth() {
        isInAuthFlow = true
        binding.navView.gone()
        val navGraph = navController.navInflater.inflate(R.navigation.auth_nav_graph)
        navController.graph = navGraph
    }

    fun onLoginSuccess() {
        navigateToMain()
        requestNotificationPermission()
        registerFcmToken()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun registerFcmToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            Log.d("MainActivity", "FCM token: ${token.take(20)}...")
            val app = application as App
            lifecycleScope.launch {
                try {
                    app.container.apiService.registerDevice(
                        RegisterDeviceRequest(token, "android")
                    )
                    Log.d("MainActivity", "FCM token registered on backend")
                } catch (e: Exception) {
                    Log.e("MainActivity", "Failed to register FCM token", e)
                }
            }
        }
    }

    fun onLogout() {
        navigateToAuth()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent?) {
        if (intent == null) return
        // "navigate_to" comes from our FCMService, "type" comes from FCM data when system handles notification
        val navigateTo = intent.getStringExtra("navigate_to")
            ?: intent.getStringExtra("type")
            ?: return
        val installmentId = intent.getStringExtra("installment_id") ?: ""
        val contractId = intent.getStringExtra("contract_id") ?: ""

        Log.d("MainActivity", "Notification tap: type=$navigateTo, installment=$installmentId, contract=$contractId")

        // Clear extras to avoid re-navigation
        intent.removeExtra("navigate_to")
        intent.removeExtra("type")

        if (isInAuthFlow) return

        when (navigateTo) {
            "installment_due_today", "installment_overdue" -> {
                if (installmentId.isNotEmpty()) {
                    // Navigate to payment screen for this installment
                    navController.navigate(
                        R.id.paymentFragment,
                        bundleOf("installmentId" to installmentId)
                    )
                } else {
                    // Navigate to installments tab
                    navController.navigate(R.id.navigation_installments)
                }
            }
            else -> {
                // Default: go to installments tab
                navController.navigate(R.id.navigation_installments)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
