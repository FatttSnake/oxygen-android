package top.fatweb.oxygen.toolbox.ui.about

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import top.fatweb.oxygen.toolbox.R
import top.fatweb.oxygen.toolbox.repository.tool.ToolRepository
import top.fatweb.oxygen.toolbox.ui.util.ResourcesHelper
import javax.inject.Inject

@HiltViewModel
class AboutScreenViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val toolRepository: ToolRepository
) : ViewModel() {
    fun clearCache() {
        viewModelScope.launch {
            toolRepository.clearToolBaseCache()
            Toast.makeText(
                context,
                ResourcesHelper.getString(
                    context = context,
                    resId = R.string.feature_settings_clear_cache_success
                ),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}