package com.hyperliteapps.pricechecker

import com.hyperliteapps.pricechecker.repository.ShopRepository
import com.hyperliteapps.pricechecker.room.ShopRoomDatabase
import com.hyperliteapps.pricechecker.viewModels.ShoppingListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.module

//module for viewModel injection
val viewModelModule = module {
    factory { ShoppingListViewModel(get()) }
}

//module for repository injection and database initialization
val repositoryModule = module {
    single {
        ShopRoomDatabase.getDatabase(androidApplication(),
            CoroutineScope(SupervisorJob())
        )
    }

    factory { ShopRepository(get<ShopRoomDatabase>().shoppingDao()) }
}