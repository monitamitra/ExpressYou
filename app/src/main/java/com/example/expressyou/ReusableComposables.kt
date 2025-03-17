package com.example.expressyou

import android.R.attr.shape
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.expressyou.ui.theme.ExpressYouTheme
import okhttp3.Request


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

    val sampleRecipe = CoffeeRecipe(
        name = "Vanilla Latte",
        ingredients = listOf(
            Ingredient("Espresso", "1 shot"),
            Ingredient("Milk", "1 cup"),
            Ingredient("Vanilla Syrup", "2 tbsp")
        ),
        instructions = listOf(
            "Brew the espresso shot.",
            "Steam the milk until frothy.",
            "Add vanilla syrup to the espresso and mix.",
            "Pour the steamed milk over the espresso.",
            "Enjoy!"
        ),
        imageUrl = "https://www.yesmooretea.com/wp-content/uploads/2020/07/Tea-Leaves-Boba-Kit.jpg",
    )

    var showModal by remember { mutableStateOf(false) }

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
            modifier = Modifier
                .fillMaxWidth()
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
                modifier = Modifier
                    .fillMaxWidth()
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
            modifier = Modifier
                .fillMaxWidth()
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
                modifier = Modifier
                    .fillMaxWidth()
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
                onClick = { showModal = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD4A373), // Background color
                    contentColor = Color.White // Text/Icon color
                ),

                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Generate My Coffee!", fontFamily = poppins, fontSize = 14.sp)
            }

            OutlinedButton(
                onClick = { showModal = true },
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
    if (showModal) {
        GenerateCoffeeRecipeModal(
            coffeeRecipe = sampleRecipe,
            sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            showBottomSheet = showModal,
            onDismissRequest = {showModal = false}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateCoffeeRecipeModal(
    modifier: Modifier = Modifier,
    coffeeRecipe: CoffeeRecipe,
    sheetShape: RoundedCornerShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    showBottomSheet: Boolean,
    onDismissRequest:  () -> Unit
) {
    //var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )


    val poppinsSemiBold = FontFamily(
        Font(R.font.poppins_semibold),
    )

    val poppins_regular = FontFamily(
        Font(R.font.poppins_regular)
    )

    val poppins_medium = FontFamily(
        Font(R.font.poppins_medium)
    )

    if (showBottomSheet) {
        ModalBottomSheet(
            modifier = modifier.fillMaxHeight()
                .padding(horizontal = 14.dp),
            onDismissRequest = onDismissRequest,
            sheetState = sheetState,
            containerColor = Color(0xFFF5E6CA)
        ) {

            Box(

            ) {
                AsyncImage(
                    model = coffeeRecipe.imageUrl,
                    contentDescription = "Coffee Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                )

                var isFavorite by remember { mutableStateOf(coffeeRecipe.isFavorite) }
                IconButton(
                    onClick = {
                        isFavorite = !isFavorite
                        coffeeRecipe.isFavorite = isFavorite
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(color = Color.White)
                        .clip(shape = RoundedCornerShape(40.dp))
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else
                            Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = Color(0xFFD4A373)
                    )
                }
            }

                Column(
                    modifier = modifier
                        .background(color = Color(0xFFF5E6CA))

                ) {
                    Text(coffeeRecipe.name, color = Color(0xFF4B2E2E),
                        fontFamily = poppinsSemiBold, fontSize = 25.sp,
                        modifier = modifier
                            .padding(top = 40.dp, bottom = 20.dp, start = 10.dp)
                            .align(Alignment.CenterHorizontally)
                    )

                    val options = listOf("Ingredients", "Instructions")
                    var selectedIndex by remember { mutableIntStateOf(0) }
                    var isIngredients by remember { mutableStateOf(true) }

                    Row(
                        modifier = modifier.align(Alignment.CenterHorizontally)
                            .border(2.dp, Color(0xFF4B2E2E), shape = RoundedCornerShape(8.dp))
                            .padding(0.dp),
                    )
                    {
                        options.forEachIndexed({index, option ->
                            FilterChip(
                                selected = index == selectedIndex,
                                onClick = {
                                    selectedIndex = index
                                    isIngredients = index == 0
                                },
                                label = {Text(option, fontFamily = poppins_medium,
                                    fontSize = 14.sp,
                                    modifier = modifier.padding(vertical = 10.dp))},
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF4B2E2E),
                                    selectedLabelColor = Color.White,
                                    labelColor = Color(0xFF4B2E2E),
                                    containerColor = Color(0xFFF5E6CA)
                                ),
                                border = BorderStroke(0.dp, Color.Transparent)
                            )
                        })
                    }

                    Spacer(modifier = Modifier.height(12.dp))


                    val itemCount = if (isIngredients) coffeeRecipe.ingredients.size else
                        coffeeRecipe.instructions.size
                    Text("$itemCount items", fontFamily = poppins_regular, fontSize = 12.sp,
                        color = Color(0xFF4B2E2E), modifier = modifier.padding(start = 10.dp))


                    LazyColumn (
                        modifier = modifier
                            .fillMaxWidth()
                    ) {
                        if (isIngredients) {
                            items(coffeeRecipe.ingredients) { item ->
                                Card(
                                    modifier = Modifier.fillMaxWidth()
                                        .padding(start = 10.dp, bottom = 12.dp, top = 12.dp,
                                            end = 10.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFFF5E6CA)
                                    ),
                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = 6.dp
                                    ),
                                    border = BorderStroke(1.dp, Color(0xFF4B2E2E))
                                ) {
                                    Row(
                                        modifier = modifier.fillMaxWidth()
                                    ) {
                                        Text(item.name, fontFamily = poppins_medium, fontSize = 14.sp,
                                            color = Color(0xFF4B2E2E), modifier =
                                            modifier.weight(1f)
                                                .fillMaxWidth()
                                                .padding(start = 5.dp, bottom = 5.dp, top = 5.dp))

                                        Text(item.amount, fontFamily = poppins_regular, fontSize = 12.sp,
                                            color = Color(0xFF4B2E2E), textAlign = TextAlign.End,
                                            modifier = modifier.padding(top = 5.dp, bottom = 5.dp,
                                                end = 5.dp))
                                    }

                                }
                            }
                        } else {
                            items(coffeeRecipe.instructions) { item ->
                                Card(
                                    modifier = Modifier.fillMaxWidth()
                                        .padding(start = 10.dp, bottom = 12.dp, top = 12.dp,
                                            end = 10.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFFF5E6CA)
                                    ),
                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = 6.dp
                                    ),
                                    border = BorderStroke(1.dp, Color(0xFF4B2E2E))
                                ) {
                                    Text(item, fontFamily = poppins_regular, fontSize = 12.sp,
                                        color = Color(0xFF4B2E2E), modifier = modifier.fillMaxWidth().
                                        padding(top = 5.dp, bottom = 5.dp, start = 5.dp),
                                        maxLines = Int.MAX_VALUE
                                    )
                                }
                            }
                        }


                    }
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
