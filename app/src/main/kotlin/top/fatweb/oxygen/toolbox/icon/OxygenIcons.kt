package top.fatweb.oxygen.toolbox.icon

import android.graphics.BitmapFactory
import android.graphics.drawable.PictureDrawable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Reorder
import androidx.compose.material.icons.filled.Upgrade
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Store
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import com.caverock.androidsvg.SVG
import top.fatweb.oxygen.toolbox.util.decodeToByteArray
import top.fatweb.oxygen.toolbox.util.decodeToString
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

object OxygenIcons {
    val ArrowDown = Icons.Rounded.KeyboardArrowDown
    val Back = Icons.Rounded.ArrowBackIosNew
    val Box = Icons.Default.Inbox
    val Close = Icons.Default.Close
    val Code = Icons.Default.Code
    val Delete = Icons.Default.Delete
    val Download = Icons.Default.Download
    val Error = Icons.Default.Cancel
    val Home = Icons.Rounded.Home
    val HomeBorder = Icons.Outlined.Home
    val Info = Icons.Outlined.Info
    val MoreVert = Icons.Default.MoreVert
    val Reorder = Icons.Default.Reorder
    val Search = Icons.Rounded.Search
    val Star = Icons.Rounded.Star
    val StarBorder = Icons.Outlined.StarBorder
    val Store = Icons.Rounded.Store
    val StoreBorder = Icons.Outlined.Store
    val Success = Icons.Rounded.CheckCircle
    val Time = Icons.Default.AccessTime
    val Tool = Icons.Default.Build
    val Upgrade = Icons.Default.Upgrade

    @OptIn(ExperimentalEncodingApi::class)
    fun fromSvgBase64(base64String: String): ImageBitmap {
        val svg = SVG.getFromString(Base64.decodeToString(base64String))
        val drawable = PictureDrawable(svg.renderToPicture())
        return drawable.toBitmap().asImageBitmap()
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun fromPngBase64(base64String: String): ImageBitmap {
        val byteArray = Base64.decodeToByteArray(base64String)
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size).asImageBitmap()
    }
}
