package com.example.expressyou

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.sp
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
                .padding(top = 8.dp)
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
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }

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


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreenUI(
    modifier: Modifier = Modifier
) {
    val poppins = FontFamily(
        Font(R.font.poppins_regular),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 100.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        var mood by rememberSaveable { mutableStateOf("") }
        var dietaryRestrictions by rememberSaveable { mutableStateOf("") }
        var selectedSweetness by rememberSaveable { mutableStateOf("") }
        var selectedMilkType by rememberSaveable { mutableStateOf("Milk Type") }

        OutlinedTextField(
            value = mood,
            onValueChange = {mood = it},
            label = {Text("Mood",
                fontFamily = poppins, fontSize = 16.sp)},
            modifier = Modifier.fillMaxWidth()
                .padding(start = 25.dp, end = 25.dp),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFFFFFFF),
                unfocusedContainerColor = Color(0xFFFFFFFF),
                focusedLabelColor = Color(0xFFFFFFFF),
                unfocusedLabelColor = Color(0xFFD4A373),
                unfocusedTextColor = Color(0xFFD4A373),
                focusedTextColor = Color(0xFFD4A373)),
        )

        val milkOptions = listOf("Oat", "Whole", "Soy",
            "Almond", "Skim")
        FlowRow (
            modifier = Modifier.padding(start = 20.dp, end = 20.dp)
        ) {
            Text(
                text = "Milk Type",
                fontSize = 18.sp,
                color = Color(0xFFFFFFFF),
                fontFamily = poppins,
                modifier = Modifier.fillMaxWidth()
                    .padding(bottom = 10.dp, start = 14.dp, top = 10.dp)
            )
            milkOptions.forEach{milkType ->
                FilterChip(
                    selected = selectedMilkType == milkType,
                    onClick = { selectedMilkType = milkType },
                    label = { Text(milkType, fontFamily = poppins,
                        fontSize = 16.sp, modifier = Modifier.padding(
                            horizontal = 12.dp, vertical = 8.dp
                        )) },
                    leadingIcon = if (selectedMilkType == milkType) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = "Done icon",
                                modifier = Modifier.size(FilterChipDefaults.IconSize),
                                tint = Color.White
                            )
                        }
                    } else {
                        null
                    },
                    modifier = Modifier.padding(horizontal = 10.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color.White,
                        selectedContainerColor = Color(0xFFD4A373),
                        labelColor = Color(0xFFD4A373),
                        selectedLabelColor = Color.White
                    )

                )
            }
        }

        OutlinedTextField(
            value = dietaryRestrictions,
            onValueChange = {dietaryRestrictions = it},
            label = {Text("Dietary Preferences",
                fontFamily = poppins, fontSize = 16.sp)},
            modifier = Modifier.fillMaxWidth()
                .padding(start = 25.dp, end = 25.dp),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFFFFFFF),
                unfocusedContainerColor = Color(0xFFFFFFFF),
                focusedLabelColor = Color(0xFFFFFFFF),
                unfocusedLabelColor = Color(0xFFD4A373),
                unfocusedTextColor = Color(0xFFD4A373),
                focusedTextColor = Color(0xFFD4A373))
        )

        val sweetnessOptions = listOf("Unsweetened", "Low", "Medium", "High")

        FlowRow (
            modifier = Modifier.padding(start = 20.dp, end = 20.dp)
        ) {
            Text(
                text = "Sweetness Level",
                fontSize = 18.sp,
                color = Color(0xFFFFFFFF),
                fontFamily = poppins,
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 14.dp, top = 10.dp, bottom = 8.dp)
            )
            sweetnessOptions.forEach{sweetness ->
                FilterChip(
                    selected = selectedSweetness == sweetness,
                    onClick = { selectedSweetness = sweetness },
                    label = { Text(sweetness, fontFamily = poppins,
                        fontSize = 16.sp, modifier = Modifier.padding(
                            horizontal = 12.dp, vertical = 8.dp
                        )) },
                    leadingIcon = if (selectedSweetness == sweetness) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = "Done icon",
                                modifier = Modifier.size(FilterChipDefaults.IconSize),
                                tint = Color.White
                            )
                        }
                    } else {
                        null
                    },
                    modifier = Modifier.padding(horizontal = 10.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color.White,
                        selectedContainerColor = Color(0xFFD4A373),
                        labelColor = Color(0xFFD4A373),
                        selectedLabelColor = Color.White))
            }
        }

        Row(
            modifier = modifier.padding(start = 12.dp, end = 15.dp, top = 22.dp),
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Button(
                onClick = { /* Your action */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD4A373), // Background color
                    contentColor = Color.White // Text/Icon color
                ),

                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Generate My Coffee!", fontFamily = poppins, fontSize = 14.sp)
            }

            OutlinedButton(
                onClick = { /* Your action */ },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFD4A373),
                    containerColor = Color.White
                ),
                border = BorderStroke(2.dp, Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Surprise Me!", fontFamily = poppins, fontSize = 14.sp)
            }

        }







    }
}

@Preview(showBackground = true)
@Composable
fun DefaultHomeScreen() {
    ExpressYouTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            BackgroundLayout(modifier = Modifier.fillMaxSize())
            HomeScreenUI()
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
