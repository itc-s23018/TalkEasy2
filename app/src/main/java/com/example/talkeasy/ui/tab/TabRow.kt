package com.example.talkeasy.ui.tab

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.talkeasy.R

@Composable
fun TabRowView(
    modifier: Modifier = Modifier,
    tabIndex: Int,
    onTabChange: (Int) -> Unit = {}
) {
    val icons = listOf(R.drawable.top, R.drawable.chat)

    Surface(
        modifier = modifier.width(200.dp),
        shape = RoundedCornerShape(50.dp),
        color = Color.White,
        border = BorderStroke(3.dp, Color.Black)
    ) {
        TabRow(
            selectedTabIndex = tabIndex,
            containerColor = Color.Transparent,
            indicator = { },
            divider = { }
        ) {
            icons.forEachIndexed { index, icon ->
                Tab(
                    selected = tabIndex == index,
                    onClick = { onTabChange(index) },
                    icon = {
                        val iconSize = if (tabIndex == index) 40.dp else 32.dp
                        Icon(
                            painter = painterResource(id = icon),
                            contentDescription = null,
                            modifier = Modifier
                                .size(iconSize)
                                .animateContentSize()
                        )
                    },
                    modifier = Modifier.padding(8.dp),
                    selectedContentColor = Color.Black,
                    unselectedContentColor = Color.Gray
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "White background")
@Composable
private fun TabRowPreview() {
    var index by remember { mutableStateOf(0) }
    TabRowView(tabIndex = index, onTabChange = { index = it })
}

@Preview(showBackground = true, name = "Black background", backgroundColor = 0xFF000000L)
@Composable
private fun TabRowPreviewBlack() {
    var index by remember { mutableStateOf(0) }
    TabRowView(tabIndex = index, onTabChange = { index = it })
}