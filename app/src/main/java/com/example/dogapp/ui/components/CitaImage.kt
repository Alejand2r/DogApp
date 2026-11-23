package com.example.dogapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.example.dogapp.R
import com.example.dogapp.data.local.Cita

@Composable
fun CitaImage(
    cita: Cita,
    contentDescription: String?,
    contentScale: ContentScale,
    modifier: Modifier = Modifier
) {
    val avatar = when (val resource = cita.avatarRes) {
        R.drawable.pet1 -> R.drawable.pet_zeus
        R.drawable.pet2 -> R.drawable.pet_kira
        R.drawable.dog_head, null -> R.drawable.dog_brand
        else -> resource
    }
    val localImage = painterResource(avatar)
    if (cita.fotoUrl.isNullOrBlank()) {
        Image(localImage, contentDescription, modifier, contentScale = contentScale)
    } else {
        AsyncImage(
            model = cita.fotoUrl,
            contentDescription = contentDescription,
            placeholder = localImage,
            error = localImage,
            modifier = modifier,
            contentScale = contentScale
        )
    }
}
