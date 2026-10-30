package com.example.contactapp.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.example.contactapp.R
import com.example.contactapp.data_models.Contact
import com.example.contactapp.repository.ContactsRepository
import com.example.contactapp.view_models.ContactsViewModel
import com.example.contactapp.view_models.ContactsViewModelFactory
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationRoute() {
    val backstack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Route.MainScreenRoute::class)
                    subclass(Route.DetailedScreenRoute::class)
                }
            }
        },
        Route.MainScreenRoute
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                navigationIcon = {
                    if (backstack.lastOrNull() is Route.DetailedScreenRoute) {
                        IconButton(onClick = { backstack.removeLastOrNull() }) {
                            Icon(
                                painter = painterResource(R.drawable.arrow_back),
                                contentDescription = stringResource(R.string.back)
                            )
                        }
                    }
                }
            )
        }) { innerPadding ->

        val context = LocalContext.current

        NavDisplay(
            modifier = Modifier.padding(innerPadding),
            backStack = backstack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                entry<Route.MainScreenRoute> {
                    val viewModel: ContactsViewModel = viewModel(
                        factory = ContactsViewModelFactory(ContactsRepository(context))
                    )
                    MainScreenRoute(viewModel, backstack)
                }
                entry<Route.DetailedScreenRoute> { (contact: Contact) ->
                    DetailedScreenRoute(
                        contact = contact,
                        backStack = backstack
                    )
                }
            }
        )
    }
}