package com.devgardenaj.thisday

import android.annotation.SuppressLint
import android.graphics.Color.parseColor
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.core.graphics.toColorInt
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import android.content.Context
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.clip
import com.devgardenaj.thisday.infra.CategoryColors

class CategoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CategoryApp()
        }
    }
}



@Parcelize
data class CategoryTemp(
val categoryID: Int,
val categoryName: String,
val categoryColor: String
) : Parcelable

@Composable
fun CategoryApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            CategoryListScreen(navController)
        }
        composable("edit") {
            CategoryEditScreen(navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(navController: NavHostController) {
    var categories by remember {
        mutableStateOf(
            listOf(
                CategoryTemp(1, "Цветы", "#E57373"),
                CategoryTemp(2, "Овощи", "#FF8C84"),
                CategoryTemp(3, "Фрукты", "#FF1C84")
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.category)) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("edit") }) {
                Text("+")
            }
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(categories, key = { it.categoryID }) { category ->
                CategoryRow(
                    category = category,
                    onEdit = { navController.navigate("edit") },
                    onDelete = {
                        categories = categories.filter { it.categoryID != category.categoryID }
                    }
                )
                Divider()
            }
        }
    }
}

@Composable
fun CategoryRow(category: CategoryTemp, onEdit: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        color = Color(android.graphics.Color.parseColor(category.categoryColor)),
                        shape = CircleShape
                    )            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(category.categoryName, fontWeight = FontWeight.Medium)
        }
        Row {
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit))
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete))
            }
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryEditScreen(navController: NavHostController) {
    val colors = CategoryColors
    var name by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(colors[0]) }
    var showColorPicker by remember { mutableStateOf(false) }



    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.category_name)) })
        },
        bottomBar = {
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(stringResource(R.string.add))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.category_name)) },
                modifier = Modifier.fillMaxWidth()
            )

            Text(stringResource(R.string.color), fontWeight = FontWeight.Medium)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(selectedColor, shape = CircleShape)
                    .clickable { showColorPicker = true }
            )

            if (showColorPicker) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(6),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(12.dp),
                    modifier = Modifier
                        .padding(top = 35.dp)
                        .height(500.dp)
                ) {
                    items(colors) { color ->
                        BoxWithConstraints(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(45.dp)
                                    .align(Alignment.Center)
                                    .clip(CircleShape)
                                    .background(color)
                                    .clickable {
                                        selectedColor = color
                                        showColorPicker = false
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}