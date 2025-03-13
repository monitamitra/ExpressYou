package com.example.expressyou

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults.shape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expressyou.ui.theme.ExpressYouTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val Poppins = FontFamily(
            Font(R.font.poppins_regular),
        )

        setContent {
            ExpressYouTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    BackgroundLayout(modifier = Modifier.fillMaxSize())

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 100.dp)
                            .align(Alignment.Center),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        var mood by rememberSaveable { mutableStateOf("") }
                        var dietaryRestrictions by rememberSaveable { mutableStateOf("") }
                        var sweetness by rememberSaveable { mutableStateOf(5f) }
                        var selectedMilkType by rememberSaveable { mutableStateOf("Milk Type") }

                        Row(
                            modifier = Modifier.fillMaxWidth()
                            .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(15.dp)
                        ) {
                            OutlinedTextField(
                                value = mood,
                                onValueChange = {mood = it},
                                label = {Text("Enter your mood", color = Color(0xFFD4A373),
                                    fontFamily = Poppins, fontSize = 16.sp)},
                                modifier = Modifier.weight(1f)
                                    //.height(56.dp)
                                    .background(color = Color(0xFFFFFFFF),
                                        shape = RoundedCornerShape(12.dp)),
                                shape = RoundedCornerShape(12.dp)
                            )

                            var expanded by remember{mutableStateOf(false)}
                            Box(
                                modifier = Modifier
                                    //.padding(16.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { expanded = true },
                                    modifier = Modifier
                                    .height(56.dp)
                                        .background(color = Color(0xFFFFFFFF),
                                        shape = RoundedCornerShape(8.dp)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(selectedMilkType, color = Color(0xFFD4A373),
                                        fontFamily = Poppins, fontSize = 16.sp)
                                    Icon(imageVector = Icons.Filled.ArrowDropDown,
                                        contentDescription = "Dropdown Arrow")
                                }

                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    val milkTypes = listOf("Whole Milk", "Skim Milk", "Almond Milk",
                                        "Oat Milk", "Lactose-Free Milk", "Soy Milk")
                                    milkTypes.forEach { milkType ->
                                        DropdownMenuItem(
                                            text = { Text(milkType, color = Color(0xFFD4A373),
                                                fontFamily = Poppins, fontSize = 16.sp) },
                                            onClick = {
                                                selectedMilkType = milkType
                                                expanded = false
                                            },
                                        )
                                    }

                                }

                            }
                        }

                    }


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
    }
}
