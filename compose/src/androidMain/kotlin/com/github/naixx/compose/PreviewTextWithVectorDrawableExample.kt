package com.github.naixx.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true)
@Composable
fun PreviewTextWithVectorDrawableExample() {
    Column {
        Text(
            text = "Your text here",
            modifier = Modifier
                .drawableStart(
                    Icons.Default.Person,
                )
        )

        Text(
            text = "Your text here2",
            modifier = Modifier
                .drawableStart(rememberVectorPainter(Icons.Default.Person), 20.dp, 4.dp, tint = Color.Red)
        )
        Text(
            text = "Your text here",
            modifier = Modifier
                .drawableStart(
                    Icons.Default.Person,
                    tint = Color.Red
                )
        )
        Text(
            text = "Your teasdadasdasdxt here",
            modifier = Modifier
                .drawableEnd(Icons.Default.Person, 34.dp, 3.dp)
        )
    }
}
