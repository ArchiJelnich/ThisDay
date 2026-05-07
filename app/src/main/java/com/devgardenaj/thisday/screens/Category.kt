package com.devgardenaj.thisday.screens

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.parcelize.Parcelize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.clip
import com.devgardenaj.thisday.infra.CategoryColors
import com.devgardenaj.thisday.infra.ThisDayApp
import com.devgardenaj.thisday.infra.ThisDayTheme
import com.devgardenaj.thisday.infra.colorToHex
import com.devgardenaj.thisday.infra.localeChecker
import com.devgardenaj.thisday.room.Category
import androidx.core.graphics.toColorInt
import com.devgardenaj.thisday.R
import com.devgardenaj.thisday.view.CategoryViewModel


class CategoryActivity : AppCompatActivity() {

    private val viewModel by lazy {
        CategoryViewModel((application as ThisDayApp).categoryRepo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        localeChecker(this)
        super.onCreate(savedInstanceState)
        setContent {
            ThisDayTheme {
                CategoryApp(viewModel)
            }
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
fun CategoryApp(viewModel: CategoryViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            CategoryListScreen(navController, viewModel)
        }
        composable("edit/{categoryId}") { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId")?.toIntOrNull()
            CategoryEditScreen(navController, viewModel, categoryId)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(navController: NavHostController, viewModel: CategoryViewModel) {
    val categories by viewModel.categories
    var categoryToDelete by remember { mutableStateOf<Category?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadCategories()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.category),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                )
            )
        },
        bottomBar = { BottomPanel() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("edit/0") },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add))
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(categories, key = { it.categoryID }) { category ->
                CategoryRow(
                    category = CategoryTemp(
                        category.categoryID,
                        category.categoryName,
                        category.categoryColor
                    ),
                    onEdit = { navController.navigate("edit/${category.categoryID}") },
                    onDelete = { categoryToDelete = category }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }
        }

        if (categoryToDelete != null) {
            DeleteWithDialog(
                category = categoryToDelete!!,
                onConfirm = {
                    viewModel.deleteCategory(categoryToDelete!!.categoryID)
                    Log.d("LogLog", "Yes!")
                    categoryToDelete = null
                },
                onDismiss = {
                    Log.d("LogLog", "No!")
                    categoryToDelete = null
                }
            )
        }
    }
}

@Composable
fun CategoryRow(category: CategoryTemp, onEdit: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .background(
                        color = Color(category.categoryColor.toColorInt()),
                        shape = CircleShape
                    )
            )
            Log.d("Here", "Color" + category.categoryColor)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                category.categoryName,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Row {
            IconButton(onClick = onEdit) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryEditScreen(navController: NavHostController, viewModel: CategoryViewModel, categoryId: Int?) {
    val colors = CategoryColors
    var name by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(colors[0]) }
    var showColorPicker by remember { mutableStateOf(false) }

    LaunchedEffect(categoryId) {
        if (categoryId != null && categoryId != 0) {
            val category = viewModel.getCategoryById(categoryId)
            if (category != null) {
                name = category.categoryName
                selectedColor = Color(category.categoryColor.toColorInt())
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.category_name),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                )
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        if (categoryId == null || categoryId == 0) {
                            viewModel.insertCategory(name, colorToHex(selectedColor))
                        } else {
                            viewModel.updateCategory(categoryId, name, colorToHex(selectedColor))
                        }
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = MaterialTheme.shapes.medium
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
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                )
            )

            Text(
                stringResource(R.string.color),
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(selectedColor, shape = CircleShape)
                    .clickable { showColorPicker = !showColorPicker }
            )

            if (showColorPicker) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(6),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(12.dp),
                    modifier = Modifier
                        .padding(top = 8.dp)
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

@Composable
fun DeleteWithDialog(
    category: Category,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.delete_q)) },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    stringResource(R.string.cancel),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text(
                    stringResource(R.string.ok),
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
    )
}
