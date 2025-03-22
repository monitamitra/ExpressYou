package com.example.expressyou

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
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
    modifier: Modifier = Modifier,
    recipeViewModel: RecipeViewModel,
    weatherViewModel: WeatherViewModel
) {
    val poppins = FontFamily(
        Font(R.font.poppins_regular),
    )

    val recipeResult = recipeViewModel.recipeResult.observeAsState()
    val weatherState by weatherViewModel.weatherOverview.observeAsState()
    var generatedRecipe: CoffeeRecipe? by remember { mutableStateOf(null) }
    var showModal by remember { mutableStateOf(false) }

    val curWeatherSummary = when (weatherState) {
        is NetworkResponse.Success -> (weatherState as NetworkResponse.Success<String>).data
        is NetworkResponse.Error -> "Unknown Weather"
        is NetworkResponse.Loading -> "Fetching Weather..."
        else -> "Unknown Weather"
    }

    val focusManager = LocalFocusManager.current

    LaunchedEffect(showModal) {
        if (!showModal) {
            focusManager.clearFocus()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 100.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        var mood by rememberSaveable { mutableStateOf("") }
        var dietaryRestrictions by rememberSaveable { mutableStateOf("") }
        var selectedSweetness by rememberSaveable { mutableStateOf("") }
        var selectedMilkType by rememberSaveable { mutableStateOf("No Milk") }


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
                onClick = {
                    showModal = true
                    recipeViewModel.generateCoffeeRecipe(mood = mood, sweetness = selectedSweetness,
                        milkType = selectedMilkType, dietaryRestrictions = dietaryRestrictions,
                        weatherOverview = curWeatherSummary)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD4A373),
                    contentColor = Color.White
                ),

                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Generate My Coffee!", fontFamily = poppins, fontSize = 14.sp)
            }

            OutlinedButton(
                onClick = {
                    showModal = true
                    recipeViewModel.generateSurpriseCoffeeRecipe(dietaryRestrictions = dietaryRestrictions,
                        weatherOverview = curWeatherSummary)
                },
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

    when(val res = recipeResult.value) {
        is NetworkResponse.Error -> {
            Text(text = res.message)
        }
        NetworkResponse.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Gray.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(80.dp),
                    color = Color(0xFF4B2E2E),
                    strokeWidth = 10.dp
                )
            }
        }
        is NetworkResponse.Success -> {
            generatedRecipe = res.data
            showModal = true
            Log.d("HomeScreen", "Coffee recipe: ${res.data.name}")
        }
        null -> {}
    }

    if (showModal && generatedRecipe != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1f)
        ) {
            GenerateRecipeModal(
                coffeeRecipe = generatedRecipe!!,
                showBottomSheet = showModal,
                recipeViewModel = recipeViewModel,
                onDismissRequest = { showModal = false }
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Gray.copy(alpha = 0.2f), shape = RoundedCornerShape(16.dp))
                    .clickable(onClick = { showModal = false })
                    .zIndex(1f)
            )
        }
    }

}


@Composable
fun FavoritesScreen(viewModel: RecipeViewModel = viewModel()) {
    var searchQuery by remember { mutableStateOf("") }
    val favoriteRecipes by viewModel.favoriteRecipes.collectAsState()

    // Fetch favorite recipes when screen is first loaded
    LaunchedEffect(Unit) {
        viewModel.fetchFavoriteRecipes()
    }

    val filteredRecipes = favoriteRecipes.filter { recipe ->
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
                RecipeCard(recipe, viewModel)
            }
        }

    }
}


@Composable
fun RecipeCard(recipe: CoffeeRecipe, recipeViewModel: RecipeViewModel) {
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

            val isFavorite by remember { derivedStateOf {recipe.isFavorite} }
            IconButton(
                onClick = {
                    recipe.isFavorite = !isFavorite
                    recipeViewModel.removeRecipeFromFavorites(recipe)
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
            GenerateRecipeModal (
                coffeeRecipe = it,
                showBottomSheet = showBottomSheet,
                recipeViewModel = recipeViewModel,
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
fun Home(
    recipeViewModel: RecipeViewModel,
    weatherViewModel: WeatherViewModel
) {
    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundLayout(modifier = Modifier.fillMaxSize())
        HomeScreenUI(recipeViewModel = recipeViewModel, weatherViewModel = weatherViewModel)
    }
}

enum class CoffeeScreen() {
    Home,
    Favorites
}

@Composable
fun MainScreen(recipeViewModel: RecipeViewModel, weatherViewModel: WeatherViewModel) {
    val navController = rememberNavController()

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = CoffeeScreen.Home.name,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(route = CoffeeScreen.Home.name) {
                Home(recipeViewModel, weatherViewModel)
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
