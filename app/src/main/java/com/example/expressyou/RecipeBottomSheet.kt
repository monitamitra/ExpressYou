package com.example.expressyou

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateRecipeModal(
    modifier: Modifier = Modifier,
    coffeeRecipe: CoffeeRecipe,
    showBottomSheet: Boolean,
    recipeViewModel: RecipeViewModel,
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
                        if (isFavorite) {
                            recipeViewModel.saveRecipeToFavorites(coffeeRecipe)
                        } else {
                            recipeViewModel.removeRecipeFromFavorites(coffeeRecipe)
                        }
                    },
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else
                            Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = Color(0xFF4B2E2E)
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
                            label = {
                                Text(option, fontFamily = poppinsMedium,
                                fontSize = 14.sp,
                                modifier = modifier.padding(vertical = 10.dp))
                            },
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteRecipeModal(
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