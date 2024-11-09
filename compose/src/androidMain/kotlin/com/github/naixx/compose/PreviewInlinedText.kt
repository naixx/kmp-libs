package com.github.naixx.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*

@Preview
@Composable
fun PreviewInlinedText() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        Column {

            InlinedText(style = MaterialTheme.typography.labelLarge) {
                +"asdasd"
                +Icons.Default.Refresh
                +"asdasdax"
                -"TERSMS"
                +"\n"
                -"click "["http://yahoo.com"]
                -"click2 "["http://google.com"]

                +Icons.Default.Lock
                icon(Icons.Default.Star, 44.sp)
                icon(Icons.Default.Star, 14.sp)
            }
            Card({}) {

            }
            InlinedText(style = MaterialTheme.typography.labelLarge, color = Color.Red) {
                +"asdasd"
                +Icons.Default.Refresh
                +"asdasdax"
                -"TERSMS"
                +"\n"
                -"click "["http://yahoo.com"]
                -"click2 "["http://google.com"]

                +Icons.Default.Lock
                icon(Icons.Default.Star, 44.sp)
                icon(Icons.Default.Star, 14.sp)
            }

            Card({

            }, elevation =  CardDefaults.cardElevation(
                pressedElevation = 40.dp
            )) {
                InlinedText(style = MaterialTheme.typography.labelLarge, color = Color.Red) {
                    +"asdasd"
                    +Icons.Default.Refresh
                    +"asdasdax"
                    -"TERSMS"
                }
            }
        }

    }
}
