package com.example.expressyou

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import coil.compose.AsyncImage
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
    val unSelectedIcon: ImageVector,
    val route: String
)

@Composable
fun BottomNavBar(
    navController: NavController
) {
    val items = listOf(
        BottomNavigationItem(
            title = "Home",
            selectedIcon = Icons.Filled.Home,
            unSelectedIcon = Icons.Outlined.Home,
            route = CoffeeScreen.Home.name
        ),
        BottomNavigationItem(
            title = "Favorites",
            selectedIcon = Icons.Filled.Favorite,
            unSelectedIcon = Icons.Outlined.FavoriteBorder,
            route = CoffeeScreen.Favorites.name
        )
    )

    val poppinsFontFamily = FontFamily(
        Font(R.font.poppins_medium),
    )

    // Preserve the state of the selected item across recompositions
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }

            NavigationBar(
                containerColor = Color(0xFFFFFFFF),
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItemIndex == index,
                        onClick = {
                            selectedItemIndex = index
                            navController.navigate(item.route)
                        },
                        icon = {
                            Icon(
                                imageVector = if (selectedItemIndex == index) item.selectedIcon else
                                    item.unSelectedIcon,
                                contentDescription = item.title,
                                tint = Color(0xFFD4A373),
                                modifier = Modifier.size(45.dp)
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
        mood = "Calm",
        weather = "Sunny",
        sweetness = "Low",
        milkType = "Whole",
        dietaryRestrictions = listOf("Vegan")
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

    val poppinsRegular = FontFamily(
        Font(R.font.poppins_regular)
    )

    val poppinsMedium = FontFamily(
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

            Box {
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
                        options.forEachIndexed{index, option ->
                            FilterChip(
                                selected = index == selectedIndex,
                                onClick = {
                                    selectedIndex = index
                                    isIngredients = index == 0
                                },
                                label = {Text(option, fontFamily = poppinsMedium,
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
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))


                    val itemCount = if (isIngredients) coffeeRecipe.ingredients.size else
                        coffeeRecipe.instructions.size
                    Text("$itemCount items", fontFamily = poppinsRegular, fontSize = 12.sp,
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
                                        Text(item.name, fontFamily = poppinsMedium, fontSize = 14.sp,
                                            color = Color(0xFF4B2E2E), modifier =
                                            modifier.weight(1f)
                                                .fillMaxWidth()
                                                .padding(start = 5.dp, bottom = 5.dp, top = 5.dp))

                                        Text(item.amount, fontFamily = poppinsRegular, fontSize = 12.sp,
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
                                    Text(item, fontFamily = poppinsRegular, fontSize = 12.sp,
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

val sampleFavoriteRecipes = listOf(
    CoffeeRecipe(
        name = "Vanilla Honey Latte",
        ingredients = listOf(
            Ingredient("Espresso", "40 mg"),
            Ingredient("Steamed milk", "1/2 cup"),
            Ingredient("Vanilla syrup", "1 tsp"),
            Ingredient("Honey", "1/2 tsp"),
        ),
        instructions = listOf(
            "Brew a shot of espresso.",
            "Steam milk and mix with vanilla syrup and honey.",
            "Pour over espresso and stir well."
        ),
        imageUrl = "https://www.yesmooretea.com/wp-content/uploads/2020/07/Tea-Leaves-Boba-Kit.jpg",
        isFavorite = true,
        mood = "Cozy",
        weather = "Rainy",
        sweetness = "Medium",
        milkType = "Whole",
        dietaryRestrictions = listOf("")
    ),
    CoffeeRecipe(
        name = "Vanilla Honey Latte",
        ingredients = listOf(
            Ingredient("Espresso", "40 mg"),
            Ingredient("Steamed milk", "1/2 cup"),
            Ingredient("Vanilla syrup", "1 tsp"),
            Ingredient("Honey", "1/2 tsp"),
        ),
        instructions = listOf(
            "Brew a shot of espresso.",
            "Steam milk and mix with vanilla syrup and honey.",
            "Pour over espresso and stir well."
        ),
        imageUrl = "https://www.yesmooretea.com/wp-content/uploads/2020/07/Tea-Leaves-Boba-Kit.jpg",
        isFavorite = true,
        mood = "Cozy",
        weather = "Rainy",
        sweetness = "Medium",
        milkType = "Whole",
        dietaryRestrictions = listOf("")
    ),
    CoffeeRecipe(
        name = "Vanilla Honey Latte",
        ingredients = listOf(
            Ingredient("Espresso", "40 mg"),
            Ingredient("Steamed milk", "1/2 cup"),
            Ingredient("Vanilla syrup", "1 tsp"),
            Ingredient("Honey", "1/2 tsp"),
        ),
        instructions = listOf(
            "Brew a shot of espresso.",
            "Steam milk and mix with vanilla syrup and honey.",
            "Pour over espresso and stir well."
        ),
        imageUrl = "https://www.yesmooretea.com/wp-content/uploads/2020/07/Tea-Leaves-Boba-Kit.jpg",
        isFavorite = true,
        mood = "Cozy",
        weather = "Rainy",
        sweetness = "Medium",
        milkType = "Whole",
        dietaryRestrictions = listOf("")
    ),
    CoffeeRecipe(
        name = "Vanilla Honey Latte",
        ingredients = listOf(
            Ingredient("Espresso", "40 mg"),
            Ingredient("Steamed milk", "1/2 cup"),
            Ingredient("Vanilla syrup", "1 tsp"),
            Ingredient("Honey", "1/2 tsp"),
        ),
        instructions = listOf(
            "Brew a shot of espresso.",
            "Steam milk and mix with vanilla syrup and honey.",
            "Pour over espresso and stir well."
        ),
        imageUrl = "https://www.yesmooretea.com/wp-content/uploads/2020/07/Tea-Leaves-Boba-Kit.jpg",
        isFavorite = true,
        mood = "Cozy",
        weather = "Rainy",
        sweetness = "Medium",
        milkType = "Whole",
        dietaryRestrictions = listOf("")
    ),
    CoffeeRecipe(
        name = "Iced Caramel Macchiato",
        ingredients = listOf(
            Ingredient("Espresso", "40 mg"),
            Ingredient("Milk", "1/2 cup"),
            Ingredient("Vanilla syrup", "1 tsp"),
            Ingredient("Caramel drizzle", "1/2 tsp"),
            Ingredient("Ice", "5-6 cubes")
        ),
        instructions = listOf(
            "Add vanilla syrup to a glass with ice and milk.",
            "Pour brewed espresso over the top.",
            "Drizzle caramel sauce on top and enjoy."
        ),
        imageUrl = "https://cdn.vox-cdn.com/thumbor/6kLvmWfhU4h64EhC0S6tsn714fI=/0x0:4032x3024/1200x900/filters:focal(1694x1190:2338x1834)/cdn.vox-cdn.com/uploads/chorus_image/image/59740845/IMG_1503.42.jpg",
        isFavorite = true,
        mood = "Energized",
        weather = "Sunny",
        sweetness = "High",
        milkType = "Whole",
        dietaryRestrictions = listOf("")
    ),
    CoffeeRecipe(
        name = "Mocha Delight",
        ingredients = listOf(
            Ingredient("Espresso", "40 mg"),
            Ingredient("Steamed milk", "1/2 cup"),
            Ingredient("Chocolate syrup", "2 tsp"),
            Ingredient("Whipped cream", "1 pump")
        ),
        instructions = listOf(
            "Brew a shot of espresso.",
            "Mix espresso with steamed milk and chocolate syrup.",
            "Top with whipped cream and enjoy."
        ),
        imageUrl = "https://cdn.vox-cdn.com/thumbor/6kLvmWfhU4h64EhC0S6tsn714fI=/0x0:4032x3024/1200x900/filters:focal(1694x1190:2338x1834)/cdn.vox-cdn.com/uploads/chorus_image/image/59740845/IMG_1503.42.jpg",
        isFavorite = true,
        mood = "Indulgent",
        weather = "Cold",
        sweetness = "High",
        milkType = "Whole",
        dietaryRestrictions = listOf("")
    ),
    CoffeeRecipe(
        name = "Cinnamon Spiced Cold Brew",
        ingredients = listOf(
            Ingredient("Cold brew coffee", "60 mg"),
            Ingredient("Cinnamon", "1 tsp"),
            Ingredient("Oat milk", "1/2 cup"),
            Ingredient("Honey", "1 tsp")
        ),
        instructions = listOf(
            "Mix cold brew with milk and honey.",
            "Add a sprinkle of cinnamon on top.",
            "Stir and serve over ice."
        ),
        imageUrl = "https://cdn.vox-cdn.com/thumbor/6kLvmWfhU4h64EhC0S6tsn714fI=/0x0:4032x3024/1200x900/filters:focal(1694x1190:2338x1834)/cdn.vox-cdn.com/uploads/chorus_image/image/59740845/IMG_1503.42.jpg",
        isFavorite = true,
        mood = "Relaxed",
        weather = "Breezy",
        sweetness = "Low",
        milkType = "Whole",
        dietaryRestrictions = listOf("")
    )
)



@Composable
fun FavoritesScreen() {
    var searchQuery by remember { mutableStateOf("") }

    val filteredRecipes = sampleFavoriteRecipes.filter { recipe ->
        val query = searchQuery.trim().lowercase()

        query.isBlank() || recipe.name.lowercase().contains(query) ||
                recipe.mood.lowercase().contains(query) ||
                recipe.weather.lowercase().contains(query) ||
                recipe.sweetness.lowercase().contains(query) ||
                recipe.dietaryRestrictions.any { it.lowercase().contains(query) }
    }


    val poppinsRegular = FontFamily(
        Font(R.font.poppins_regular),
    )

    val poppinsMedium = FontFamily(
        Font(R.font.poppins_medium),
    )



    Column(
        modifier = Modifier.padding(top = 110.dp)
            .fillMaxSize()
    )
    {
    TextField(
        value = searchQuery,
        onValueChange = {searchQuery = it},
        placeholder = {Text("Search Your Favorites", color = Color(0xFFD4A373),
            fontFamily = poppinsRegular)},
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
            .padding(start = 28.dp, end = 28.dp)
            .border(1.dp, Color(0xFFD4A373), RoundedCornerShape(25.dp))
            .align(Alignment.CenterHorizontally),
        shape = RoundedCornerShape(25.dp),
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon",
                tint = Color(0xFFD4A373),
                modifier = Modifier.padding(start = 8.dp))
        },
        colors = TextFieldDefaults.colors(
            unfocusedTextColor = Color(0xFFD4A373),
            focusedTextColor = Color(0xFFD4A373),
            focusedIndicatorColor = Color(0xFFD4A373),
            unfocusedIndicatorColor = Color(0xFFD4A373),
            focusedContainerColor = Color(0xFFFFFFFF),
            unfocusedContainerColor = Color(0xFFFFFFFF)
        )
    )
        Spacer(modifier = Modifier.height(25.dp))

        LazyColumn (
            modifier = Modifier.fillMaxWidth()
                .padding(bottom = 140.dp)
        ) {
            items(filteredRecipes) { recipe ->
                RecipeCard(recipe)
            }
        }

    }
}


@Composable
fun RecipeCard(recipe: CoffeeRecipe) {
    var selectedRecipe by remember { mutableStateOf<CoffeeRecipe?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }

    val poppinsRegular = FontFamily(
        Font(R.font.poppins_regular),
    )

    val poppinsMedium = FontFamily(
        Font(R.font.poppins_medium),
    )

    Card(
        modifier = Modifier.fillMaxWidth()
            .padding(top = 15.dp, start = 15.dp, end = 15.dp)
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5E6CA)
        ),
        border = BorderStroke(2.dp, Color(0xFF4B2E2E)),
        onClick = {
            selectedRecipe = recipe
            showBottomSheet = true
        }
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        )
        {
            AsyncImage(
                model = recipe.imageUrl,
                contentDescription = "Coffee Image",
                modifier = Modifier
                    .size(80.dp)
                    .padding(top = 15.dp, start = 10.dp, end = 10.dp)
            )

            Spacer(modifier = Modifier.padding(4.dp))

            Column(
                modifier = Modifier.fillMaxHeight()
                .padding(top = 20.dp)
                    .weight(1f)
            ) {
                Text(recipe.name, color = Color(0xFF4B2E2E),
                    modifier = Modifier.padding(end = 10.dp),
                    fontFamily = poppinsMedium,
                    maxLines = Int.MAX_VALUE)

                Spacer(modifier = Modifier.height(4.dp))

                Text("${recipe.mood}, ${recipe.weather}",
                    color = Color(0xFF4B2E2E), fontFamily = poppinsRegular,
                    fontSize = 14.sp, modifier = Modifier.padding(bottom = 10.dp))

                Spacer(modifier = Modifier.height(10.dp))
            }

            var isFavorite by remember { mutableStateOf(recipe.isFavorite) }
            IconButton(
                onClick = {
                    isFavorite = !isFavorite
                    recipe.isFavorite = isFavorite
                },
                modifier = Modifier.padding(top = 15.dp, end = 8.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else
                        Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = Color(0xFF4B2E2E)
                )
            }
        }
    }

    if (showBottomSheet) {
        selectedRecipe?.let {
            GenerateCoffeeRecipeModal(
                coffeeRecipe = it,
                showBottomSheet = showBottomSheet,
                onDismissRequest = { showBottomSheet = false }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FavoritesScreenPreview() {
    ExpressYouTheme {
        Favorites()
    }
}

@Composable
fun Favorites() {
    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundLayout(modifier = Modifier.fillMaxSize())
        FavoritesScreen()
    }
}

@Composable
fun Home() {
    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundLayout(modifier = Modifier.fillMaxSize())
        HomeScreenUI()
    }
}

enum class CoffeeScreen() {
    Home,
    Favorites
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = CoffeeScreen.Home.name,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(route = CoffeeScreen.Home.name) {
                Home()
            }

            composable(route = CoffeeScreen.Favorites.name) {
                Favorites()
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            BottomNavBar(navController)
        }
    }
}
