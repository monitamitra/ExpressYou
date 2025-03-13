package com.example.expressyou

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.expressyou.ui.theme.ExpressYouTheme

// function to set up background linear gradient and app logo
@Composable
fun BackgroundLayout(
    modifier: Modifier = Modifier
) {
    val gradientColors = listOf(
        Color(0xFF6F4E37),
        Color(0xFF875F43),
        Color(0xFF9E6F4E),
        Color(0xFFB08160)
    )

    val gradientBrush = Brush.linearGradient(
        colors = gradientColors
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = gradientBrush)
    ) {
        Image(
            painter = painterResource(id = R.drawable.app_logo),
            contentDescription = "App Logo",
            modifier = Modifier
                .height(100.dp)
                .width(200.dp)
                .align(Alignment.TopCenter)
                .padding(top = 2.dp)
        )
    }
}

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unSelectedIcon: ImageVector
)

@Composable
fun BottomNavBar(
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavigationItem(
            title = "Home",
            selectedIcon = Icons.Filled.Home,
            unSelectedIcon = Icons.Outlined.Home
        ),
        BottomNavigationItem(
            title = "Favorites",
            selectedIcon = Icons.Filled.Favorite,
            unSelectedIcon = Icons.Outlined.FavoriteBorder
        )
    )

    val poppinsFontFamily = FontFamily(
        Font(R.font.poppins_medium),
    )

    // Preserve the state of the selected item across recompositions
    var selectedItemIndex by rememberSaveable { mutableStateOf(0) }

            NavigationBar(
                containerColor = Color(0xFFFFFFFF)
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItemIndex == index,
                        onClick = {
                            selectedItemIndex = index
                        },
                        label = {
                            Text(item.title,
                                fontFamily = poppinsFontFamily
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = if (selectedItemIndex == index) item.selectedIcon else
                                    item.unSelectedIcon,
                                contentDescription = item.title,
                                tint = Color(0xFFD4A373)
                            )
                        }
                    )
                }
            }
}

@Preview(showBackground = true)
@Composable
fun DefaultHomeScreen() {
    ExpressYouTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            BackgroundLayout(modifier = Modifier.fillMaxSize())
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                BottomNavBar()
            }
        }
    }
}
