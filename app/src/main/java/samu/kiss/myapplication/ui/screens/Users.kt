package samu.kiss.myapplication.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
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
                .padding(horizontal = 16.dp),
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
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(
            containerColor = when {
                isMe && isMeAvailable -> MaterialTheme.colorScheme.primaryContainer
                isMe && !isMeAvailable -> MaterialTheme.colorScheme.errorContainer
                else -> MaterialTheme.colorScheme.surface
            }
        ), elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (user.photoUrl.isNotBlank()) {
                AsyncImage(
                    model = user.photoUrl,
                    contentDescription = "Foto de ${user.name}",
                    modifier = Modifier.size(56.dp)
                )
            } else {
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
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
                    }, style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = user.lastName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                            context, "Ubicando a ${user.name}", Toast.LENGTH_SHORT).show()
                        //TODO : Implementar pantalla de seguimiento en tiempo real
                    }) {
                    Icon(
                        imageVector = Icons.Rounded.LocationOn,
                        contentDescription = "Ver ubicación",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}