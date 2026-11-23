package com.example.dogapp.ui.screens

import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.SimpleColorFilter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import com.example.dogapp.R
import com.example.dogapp.ui.theme.DarkGrayBackground
import com.example.dogapp.ui.theme.DogAppTheme
import com.example.dogapp.ui.theme.FredokaFontFamily
import com.example.dogapp.ui.theme.White

/**
 * HU 1.0: Ventana Login
 * Criterio 1: Fondo gris oscuro, sin toolbar.
 * Criterio 2: Perro en la parte superior derecha con fondo transparente (solo cabeza), optimizado para pantallas.
 * Criterio 3: Título "DogApp" en blanco bold, centrado debajo de la imagen con tipografía estilizada.
 * Criterio 4: Parte inferior central imagen dinámica de huella digital (Lottie).
 * Criterio 5: Al presionar la huella -> ventana emergente con:
 *             - Título: "Autenticación con Biometría"
 *             - Subtítulo: "Ingrese su huella digital"
 *             - Botón: "Cancelar"
 * Criterio 6: Autenticación biométrica: si es correcta dirige a HU 2.0 Home, si no muestra error y no permite entrar.
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val loginSuccess by rememberUpdatedState(onLoginSuccess)
    val authenticators = BiometricManager.Authenticators.BIOMETRIC_WEAK

    val prompt = remember(activity) {
        activity?.let {
            BiometricPrompt(
                it,
                ContextCompat.getMainExecutor(it),
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        loginSuccess()
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        if (errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON &&
                            errorCode != BiometricPrompt.ERROR_USER_CANCELED &&
                            errorCode != BiometricPrompt.ERROR_CANCELED
                        ) {
                            Toast.makeText(it, errString, Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onAuthenticationFailed() {
                        Toast.makeText(
                            it,
                            "Huella no reconocida. Intente nuevamente.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
        }
    }

    LoginScreenContent(onFingerprintClick = {
        if (prompt == null || activity == null) {
            Toast.makeText(context, "No se pudo iniciar la autenticación.", Toast.LENGTH_LONG).show()
        } else {
            val availability = BiometricManager.from(activity).canAuthenticate(authenticators)
            when (availability) {
                BiometricManager.BIOMETRIC_SUCCESS -> prompt.authenticate(
                    BiometricPrompt.PromptInfo.Builder()
                        .setTitle("Autenticación con Biometría")
                        .setSubtitle("Ingrese su huella digital")
                        .setNegativeButtonText("Cancelar")
                        .setAllowedAuthenticators(authenticators)
                        .build()
                )
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> Toast.makeText(
                    context,
                    "Registre su huella en los ajustes del dispositivo.",
                    Toast.LENGTH_LONG
                ).show()
                else -> Toast.makeText(
                    context,
                    "La autenticación biométrica no está disponible.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    })
}

@Composable
fun LoginScreenContent(
    onFingerprintClick: () -> Unit
) {
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.fingerprint)
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkGrayBackground)
            .safeDrawingPadding()
            .padding(horizontal = 24.dp)
    ) {
        val logoSize = minOf(maxWidth * 0.64f, maxHeight * 0.37f, 240.dp)
        val fingerprintSize = minOf(maxWidth * 0.34f, maxHeight * 0.23f, 130.dp)
            .coerceAtLeast(48.dp)
        val fingerprintCenter = maxHeight * 0.68f

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .heightIn(min = maxHeight),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = (fingerprintCenter - fingerprintSize / 2).coerceAtLeast(0.dp)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.dog_brand),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 16.dp)
                        .size(logoSize)
                )
                Text(
                    text = "DogApp",
                    fontFamily = FredokaFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 52.sp,
                    color = White,
                    textAlign = TextAlign.Center
                )
            }

            Box(
                modifier = Modifier
                    .size(fingerprintSize)
                    .clickable(
                        onClickLabel = "Autenticarse con biometría",
                        role = Role.Button,
                        onClick = onFingerprintClick
                    )
                    .semantics { contentDescription = "Huella digital" },
                contentAlignment = Alignment.Center
            ) {
                val fingerprintColor = Color(0xFF8AC6C6)
                if (composition == null) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = null,
                        tint = fingerprintColor,
                        modifier = Modifier.fillMaxSize(0.72f)
                    )
                } else {
                    LottieAnimation(
                        composition = composition,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier.fillMaxSize(),
                        dynamicProperties = rememberLottieDynamicProperties(
                            rememberLottieDynamicProperty(
                                property = LottieProperty.COLOR_FILTER,
                                value = SimpleColorFilter(fingerprintColor.toArgb()),
                                keyPath = arrayOf("**")
                            )
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    DogAppTheme {
        LoginScreenContent(
            onFingerprintClick = {}
        )
    }
}
