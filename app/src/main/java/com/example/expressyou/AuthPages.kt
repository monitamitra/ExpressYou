package com.example.expressyou

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController


@Preview(showBackground = true)
@Composable
fun StartingScreenPreview() {
    SignupScreen(navController = rememberNavController())
}


@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    var isLoading by remember{ mutableStateOf(false)}

    val poppinsMedium = FontFamily(
        Font(R.font.poppins_medium)
    )

    val poppinsRegular = FontFamily(
        Font(R.font.poppins_regular)
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        BackgroundLayout()

        Text("Generate Coffee Your Way. Every Day.", color = Color.White,
            fontFamily = poppinsMedium, fontSize = 30.sp, modifier = Modifier
                .padding(start = 40.dp,
                    end = 30.dp, top = 190.dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 35.dp)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Spacer(modifier = Modifier.height(90.dp))

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", fontFamily = poppinsMedium) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF5E6CA),
                    unfocusedContainerColor = Color(0xFFF5E6CA), 
                    focusedTextColor = Color(0xFF4B2E2E),
                    unfocusedTextColor = Color(0xFF4B2E2E)
                )
            )

            Spacer(modifier = Modifier.height(25.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", fontFamily = poppinsMedium) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF5E6CA),
                    unfocusedContainerColor = Color(0xFFF5E6CA),
                    focusedTextColor = Color(0xFF4B2E2E),
                    unfocusedTextColor = Color(0xFF4B2E2E)
                )
            )

            Spacer(modifier = Modifier.height(60.dp))

            // Login Button
            Button(
                onClick = {
                    isLoading = true
                    authViewModel.login(email, password) { success, errorMessage ->
                        if (success) {
                            isLoading = false
                            navController.navigate("mainapp_screen")
                        } else {
                            isLoading = false
                            AppUtil.showToast(context, errorMessage ?: "Something went wrong")
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4B2E2E))
            ) {
                Text(text = if (isLoading) "Logging In" else "Log In", color = Color.White, fontFamily = poppinsMedium,
                    fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row {
                Text("Don't have an account?", fontFamily = poppinsRegular,
                    color = Color.White, modifier = Modifier.padding(start = 10.dp)
                    .align(Alignment.CenterVertically))
                TextButton(
                    onClick = { navController.navigate("signup_screen") },
                    modifier = Modifier.padding(0.dp)
                   ) {
                    Text(text = if (isLoading) "Creating account" else "Sign Up", color = Color.White, fontFamily = poppinsMedium)
                }
            }
        }
    }
}


@Composable
fun SignupScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    var isLoading by remember{ mutableStateOf(false)}

    val poppinsMedium = FontFamily(
        Font(R.font.poppins_medium)
    )

    val poppinsRegular = FontFamily(
        Font(R.font.poppins_regular)
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        BackgroundLayout()

        Text("Generate Coffee Your Way. Every Day.", color = Color.White,
            fontFamily = poppinsMedium, fontSize = 30.sp, modifier = Modifier
                .padding(start = 40.dp,
                    end = 30.dp, top = 190.dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 35.dp)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Spacer(modifier = Modifier.height(90.dp))

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", fontFamily = poppinsMedium) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF5E6CA),
                    unfocusedContainerColor = Color(0xFFF5E6CA),
                    focusedTextColor = Color(0xFF4B2E2E),
                    unfocusedTextColor = Color(0xFF4B2E2E)
                )
            )

            Spacer(modifier = Modifier.height(25.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", fontFamily = poppinsMedium) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF5E6CA),
                    unfocusedContainerColor = Color(0xFFF5E6CA),
                    focusedTextColor = Color(0xFF4B2E2E),
                    unfocusedTextColor = Color(0xFF4B2E2E)
                )
            )

            Spacer(modifier = Modifier.height(60.dp))

            // Sign up Button
            Button(
                onClick = {
                    isLoading = true
                    authViewModel.signup(email, password) {success, errorMessage ->
                        if (success) {
                            isLoading = false
                            navController.navigate("mainapp_screen")
                        } else {
                            isLoading = false
                            AppUtil.showToast(context, errorMessage ?: "Something went wrong")
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4B2E2E))
            ) {
                Text(text = "Sign Up", color = Color.White, fontFamily = poppinsMedium,
                    fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row {
                Text("Already have an account?", fontFamily = poppinsRegular,
                    color = Color.White, modifier = Modifier.padding(start = 10.dp)
                        .align(Alignment.CenterVertically))
                TextButton(
                    onClick = { navController.navigate("login_screen") },
                    modifier = Modifier.padding(0.dp)
                ) {
                    Text(text = "Log In", color = Color.White, fontFamily = poppinsMedium)
                }
            }
        }
    }
}

