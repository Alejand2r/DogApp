package com.example.dogapp.ui.components

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

/**
 * Forma con la esquina inferior izquierda curvada/cóncava o con arco suave,
 * tal como se especifica en Criterio 2 de HU 2.0:
 * "header de fondo gris oscuro que tiene la parte inferior izquierda curvada"
 */
class HeaderCurvedShape(private val cornerRadius: Dp) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val radiusPx = with(density) { cornerRadius.toPx() }
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height)
            // Lado inferior hacia la izquierda
            lineTo(radiusPx, size.height)
            // Curva en la esquina inferior izquierda
            arcTo(
                rect = Rect(0f, size.height - (radiusPx * 2), radiusPx * 2, size.height),
                startAngleDegrees = 90f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )
            lineTo(0f, 0f)
            close()
        }
        return Outline.Generic(path)
    }
}
