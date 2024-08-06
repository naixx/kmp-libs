package com.github.naixx.compose

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Preview
@Composable
fun PreviewInlinedText() {
    InlinedText(style = MaterialTheme.typography.labelLarge) {
        +"asdasd"
        +Icons.Default.Refresh
        +"asdasdax"
        -"TERSMS"
        +Icons.Default.Lock
        icon(Icons.Default.Star, 44.sp)
        icon(Icons.Default.Star, 14.sp)
    }
}
