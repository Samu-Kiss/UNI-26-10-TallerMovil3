package samu.kiss.myapplication.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.compose.AppTheme
import samu.kiss.myapplication.models.MyUser

@Composable
fun UserProfileBubble(
    user: MyUser,
    bubbleSize: Dp = 48.dp,
    onImageLoaded: (() -> Unit)? = null,
    preloadedBitmap: ImageBitmap? = null
) {
    val context = LocalContext.current
    val imageRequest = remember(user.photoUrl) {
        ImageRequest.Builder(context).data(user.photoUrl).allowHardware(false).build()
    }

    val painter = rememberAsyncImagePainter(
        model = imageRequest, onSuccess = { onImageLoaded?.invoke() })

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .border(1.dp, Color.White, CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHighest),
        contentAlignment = Alignment.Center
    ) {
        if (preloadedBitmap != null) {
            Image(
                bitmap = preloadedBitmap,
                contentDescription = "Foto de perfil de ${user.name}",
                modifier = Modifier
                    .size(bubbleSize)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else if (user.photoUrl.isNotEmpty()) {
            val state = painter.state
            if (state is AsyncImagePainter.State.Success) {
                Image(
                    painter = painter,
                    contentDescription = "Foto de perfil de ${user.name}",
                    modifier = Modifier
                        .size(bubbleSize)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    modifier = Modifier.size(12.dp),
                    imageVector = Icons.Rounded.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Text(
                text = user.name.firstOrNull()?.uppercase() ?: "U",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

        }

    }
}

@Preview(showBackground = true)
@Composable
fun UserProfileBubblePreview() {
    AppTheme {
        UserProfileBubble(
            user = MyUser(name = "Laura", photoUrl = ""),
        )
    }
}