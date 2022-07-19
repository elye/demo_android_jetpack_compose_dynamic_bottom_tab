package com.example.myapplication.navigation

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.SparseArray
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.BottomNavigationBar
import com.example.myapplication.GroupedCheckbox
import com.example.myapplication.NavigationItem
import com.example.myapplication.TopBar
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlin.reflect.KProperty


class RestorableActivity : FragmentActivity() {
    private var savedStateSparseArray = SparseArray<Fragment.SavedState>()
    private var currentSelectItemId = 0

    companion object {
        const val SAVED_STATE_CONTAINER_KEY = "ContainerKey"
        const val SAVED_STATE_CURRENT_TAB_KEY = "CurrentTabKey"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            savedStateSparseArray = savedInstanceState.getSparseParcelableArray(
                SAVED_STATE_CONTAINER_KEY
            )
                ?: savedStateSparseArray
            currentSelectItemId = savedInstanceState.getInt(SAVED_STATE_CURRENT_TAB_KEY)
        }
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    var state by rememberSaveable(
                        stateSaver = mapSaver(
                            save = { it },
                            restore = { it as Map<String, Boolean> }
                        )
                    ) {
                        mutableStateOf(enumValues<NavigationItem>().associate { it.route to true })
                    }

                    Column {
                        GroupedCheckbox(
                            mItemsList = state
                        ) { state = it }
                        MainScreen(state)
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSparseParcelableArray(SAVED_STATE_CONTAINER_KEY, savedStateSparseArray)
        outState.putInt(SAVED_STATE_CURRENT_TAB_KEY, currentSelectItemId)
    }

    override fun onBackPressed() {
        supportFragmentManager.fragments.forEach { fragment ->
            if (fragment != null && fragment.isVisible) {
                with(fragment.childFragmentManager) {
                    if (backStackEntryCount > 0) {
                        popBackStack()
                        return
                    }
                }
            }
        }
        super.onBackPressed()
    }

    @Composable
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    fun MainScreen(state: Map<String, Boolean>) {
        val navController = rememberNavController()
        Scaffold(
            topBar = { TopBar() },
            bottomBar = { BottomNavigationBar(navController, state) }
        ) { Navigation(navController, ::getCommitFunction) }
    }

    private fun getCommitFunction(
        fragment: Fragment,
        tag: String
    ): FragmentTransaction.(containerId: Int) -> Unit =
        {
            saveAndRetrieveFragment(supportFragmentManager, it, fragment)
            replace(it, fragment, tag)
        }

    private fun saveAndRetrieveFragment(
        supportFragmentManager: FragmentManager,
        tabId: Int,
        fragment: Fragment
    ) {
        val currentFragment = supportFragmentManager.findFragmentById(currentSelectItemId)
        if (currentFragment != null) {
            savedStateSparseArray.put(
                currentSelectItemId,
                supportFragmentManager.saveFragmentInstanceState(currentFragment)
            )
        }
        currentSelectItemId = tabId
        fragment.setInitialSavedState(savedStateSparseArray[currentSelectItemId])
    }
}
