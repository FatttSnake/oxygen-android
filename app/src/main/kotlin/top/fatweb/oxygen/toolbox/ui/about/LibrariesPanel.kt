package top.fatweb.oxygen.toolbox.ui.about

import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.items
import top.fatweb.oxygen.toolbox.ui.component.LibraryCard

fun LazyStaggeredGridScope.librariesPanel(
    librariesScreenUiState: LibrariesScreenUiState,
    onClickLicense: (key: String) -> Unit
) {
    when (librariesScreenUiState) {
        LibrariesScreenUiState.Loading, LibrariesScreenUiState.Nothing, LibrariesScreenUiState.NotFound -> Unit

        is LibrariesScreenUiState.Success -> {
            items(
                items = librariesScreenUiState.dependencies.libraries,
                key = { it.uniqueId }
            ) {
                LibraryCard(
                    library = it,
                    licenses = librariesScreenUiState.dependencies.licenses.filter { license ->
                        it.licenses.contains(license.key)
                    },
                    onClickLicense = onClickLicense
                )
            }
        }
    }
}
