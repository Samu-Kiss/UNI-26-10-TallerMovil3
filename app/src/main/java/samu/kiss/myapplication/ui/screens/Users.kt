package samu.kiss.myapplication.ui.screens

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.background
import androidx.compose.ui.draw.blur
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.compose.AppTheme
import com.google.firebase.auth.FirebaseAuth
import samu.kiss.myapplication.models.MyUser
import samu.kiss.myapplication.models.MyUsersViewModel
import samu.kiss.myapplication.models.UserLocationViewModel
import samu.kiss.myapplication.ui.components.MyScaffold

@Composable
fun UsersScreen(
    controller: NavHostController,
    locationViewModel: UserLocationViewModel,
    usersViewModel: MyUsersViewModel = viewModel()
) {
    val users by usersViewModel.users.collectAsState()
    val myUid = FirebaseAuth.getInstance().currentUser?.uid
    val isMeAvailable by locationViewModel.isAvailable.collectAsState()

    val me = users.find { it.uid == myUid }
    val others = users.filter { it.available == true && it.uid != myUid }.sortedBy { it.name }

    MyScaffold(
        navController = controller, locationViewModel = locationViewModel
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 128.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            me?.let { myUser ->
                item(key = "me_${myUser.uid}") {
                    UserCard(
                        user = myUser,
                        isMe = true,
                        isMeAvailable = isMeAvailable,
                        navController = controller
                    )
                }
            }

            if (others.isNotEmpty()) {
                item(key = "divider") {
                    Text(
                        text = "Disponibles",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                items(others, key = { it.uid }) { user ->
                    UserCard(
                        user = user, isMe = false, isMeAvailable = false, navController = controller
                    )
                }
            } else if (me != null) {
                item(key = "empty") {
                    Text(
                        text = "No hay otros usuarios disponibles",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun UserCard(
    user: MyUser, isMe: Boolean, isMeAvailable: Boolean, navController: NavHostController
) {
    val context = LocalContext.current
    val borderColor = when {
        isMe && isMeAvailable -> MaterialTheme.colorScheme.primary
        isMe && !isMeAvailable -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    }

    val borderGradient = Brush.linearGradient(
        colors = listOf(borderColor, borderColor.copy(alpha = 0.5f))
    )

    Box(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .blur(50.dp)
                .background(
                    color = Color.White.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(24.dp)
                )
        )
        // Outer box para el borde gradiente
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(brush = borderGradient, shape = RoundedCornerShape(24.dp))
                .padding(1.2.dp)
        ) {
            // Inner content box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(23.dp))
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
                        shape = RoundedCornerShape(23.dp)
                    )
                    .padding(16.dp)
            ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (user.photoUrl.isNotBlank()) {
                    AsyncImage(
                        model = user.photoUrl,
                        contentDescription = "Foto de ${user.name}",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Surface(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user.name.firstOrNull()?.uppercase() ?: "?",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = buildString {
                            append(user.name)
                            if (isMe) append(" (Tú)")
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = user.lastName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    if (isMe) {
                        Text(
                            text = if (isMeAvailable) "Disponible" else "No disponible",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isMeAvailable) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.error
                        )
                    }
                }

                if (!isMe) {
                    IconButton(
                        onClick = {
                            Toast.makeText(
                                context, "Ubicando a ${user.name}", Toast.LENGTH_SHORT
                            ).show()
                            //TODO : Implementar pantalla de seguimiento en tiempo real
                        }) {
                        Icon(
                            imageVector = Icons.Rounded.LocationOn,
                            contentDescription = "Ver ubicación",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserCardPreview() {
    val navController = rememberNavController()
    MaterialTheme {
        val backgroundBrush = Brush.radialGradient(
            colors = listOf(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.primary.copy(0f),
            ), center = Offset(x = 100f, y = 800f), radius = 1000f
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .background(brush = backgroundBrush)
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
            val userWithPhoto = MyUser(
                uid = "1",
                name = "Ana",
                lastName = "Gómez",
                photoUrl = "https://placekitten.com/200/200",
                available = true
            )

            val userWithoutPhoto = MyUser(
                uid = "2", name = "Luis", lastName = "Pérez", photoUrl = "", available = false
            )

            UserCard(
                user = userWithPhoto,
                isMe = false,
                isMeAvailable = false,
                navController = navController
            )
            Spacer(modifier = Modifier.height(8.dp))
            UserCard(
                user = userWithoutPhoto,
                isMe = false,
                isMeAvailable = false,
                navController = navController
            )
            Spacer(modifier = Modifier.height(8.dp))
            UserCard(
                user = userWithoutPhoto.copy(uid = "3", name = "Tú"),
                isMe = true,
                isMeAvailable = true,
                navController = navController
            )
            Spacer(modifier = Modifier.height(8.dp))
            UserCard(
                user = userWithoutPhoto.copy(uid = "4", name = "Tú sin disponibilidad"),
                isMe = true,
                isMeAvailable = false,
                navController = navController
            )
        }
        }
    }
}


@Preview(
    showBackground = true,
    name = "Dark Mode Preview",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    backgroundColor = 0xFF1E1E1E
)
@Composable
fun UserCardDarkPreview() {
    val navController = rememberNavController()
    AppTheme(darkTheme = true) {
        val backgroundBrush = Brush.radialGradient(
            colors = listOf(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.primary.copy(0f),
            ), center = Offset(x = 100f, y = 800f), radius = 1000f
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .background(brush = backgroundBrush)
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
            val userWithPhoto = MyUser(
                uid = "1",
                name = "Ana",
                lastName = "Gómez",
                photoUrl = "https://placekitten.com/200/200",
                available = true
            )

            val userWithoutPhoto = MyUser(
                uid = "2", name = "Luis", lastName = "Pérez", photoUrl = "", available = false
            )

            UserCard(
                user = userWithPhoto,
                isMe = false,
                isMeAvailable = false,
                navController = navController
            )
            Spacer(modifier = Modifier.height(8.dp))
            UserCard(
                user = userWithoutPhoto,
                isMe = false,
                isMeAvailable = false,
                navController = navController
            )
            Spacer(modifier = Modifier.height(8.dp))
            UserCard(
                user = userWithoutPhoto.copy(uid = "3", name = "Tú"),
                isMe = true,
                isMeAvailable = true,
                navController = navController
            )
            Spacer(modifier = Modifier.height(8.dp))
            UserCard(
                user = userWithoutPhoto.copy(uid = "4", name = "Tú sin disponibilidad"),
                isMe = true,
                isMeAvailable = false,
                navController = navController
            )
        }
        }
    }
}


