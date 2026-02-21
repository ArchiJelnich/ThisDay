package com.devgardenaj.thisday

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.devgardenaj.thisday.infra.CategoryColors
import com.devgardenaj.thisday.infra.localeChecker
import com.devgardenaj.thisday.room.CategoryDao
import com.devgardenaj.thisday.room.*
import com.devgardenaj.thisday.screens.BottomPanel
import com.devgardenaj.thisday.widget.WidgetProvider
import com.devgardenaj.thisday.widget.WidgetProvider.Companion.updateWidget
import com.devgardenaj.thisday.widget.forceWidgetUpdate
import kotlinx.coroutines.launch


class CategoryActivity : AppCompatActivity() {

    private val viewModel by lazy {
        val db = AppDatabase.getInstance(applicationContext)
        val repo = CategoryRepository(db.CategoryDao(), db.InfoAboutDayDao())
        CategoryViewModel(repo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        localeChecker(this)
        super.onCreate(savedInstanceState)
        setContent {
            CategoryApp(viewModel)
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
            CategoryListScreen(navController,viewModel)
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
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.category)) }
            )
                    },
        bottomBar = {
            BottomPanel()
        }

    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
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
                        onDelete = {

                            categoryToDelete = category

                        }
                    )
                    Divider()
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = { navController.navigate("edit/0") }) {
                    Text("+")
                }
            }
        }


        if (categoryToDelete != null) {
            DeleteWithDialog(
                category = categoryToDelete!!,
                onConfirm = {
                    viewModel.deleteCategory(categoryToDelete!!.categoryID)
                    Log.d("LogLog","Yes!")
                    categoryToDelete = null
                },
                onDismiss = {
                    Log.d("LogLog","No!")
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
            Log.d("Here", "Color" + category.categoryColor)
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
fun CategoryEditScreen(navController: NavHostController, viewModel: CategoryViewModel, categoryId: Int?) {


    val colors = CategoryColors
    var name by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(colors[0]) }
    var showColorPicker by remember { mutableStateOf(false) }
    var context = LocalContext.current

    LaunchedEffect(categoryId) {
        if (categoryId != null && categoryId != 0) {
            val category = viewModel.getCategoryById(categoryId)
            if (category != null) {
                name = category.categoryName
                selectedColor = Color(android.graphics.Color.parseColor(category.categoryColor))
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.category_name)) }) },
        bottomBar = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        if (categoryId == null || categoryId == 0) {
                            viewModel.insertCategory(name, ColorToHex(selectedColor))
                        } else {
                            viewModel.updateCategory(categoryId, name, ColorToHex(selectedColor))
                        }

                        navController.popBackStack()
                    }
                },
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
                Text(stringResource(R.string.cancel))
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
            }) {
                Text(stringResource(R.string.ok))
            }
        }
    )
}

class CategoryRepository(private val dao: CategoryDao, private val infoDao: InfoAboutDayDao) {
    suspend fun insert(category: Category) = dao.insertAll(category)
    suspend fun getAllNotDeleted() = dao.getAllNotDeleted()
    suspend fun update(category: Category) = dao.update(category)
    suspend fun deleteCategoryByID(id : Int) = dao.deleteCategoryByID(id)
    suspend fun deleteInfoByID(id : Int) = infoDao.deleteInfoByID(id)
}

open class CategoryViewModel(private val repository: CategoryRepository) : ViewModel() {

    var categories = mutableStateOf<List<Category>>(emptyList())
        private set

    fun loadCategories() {
        viewModelScope.launch {
            categories.value = repository.getAllNotDeleted()

        }
    }

    fun insertCategory(name: String, color: String) {
        viewModelScope.launch {
            repository.insert(Category(0, name, color, 0))
            loadCategories()
        }
    }

    fun deleteCategory(id : Int)
    {
        viewModelScope.launch {
            repository.deleteCategoryByID(id)
            repository.deleteInfoByID(id)
            loadCategories()
        }


    }

    fun getCategoryById(id: Int): Category? {
        return categories.value.find { it.categoryID == id }
    }

    fun updateCategory(id: Int, name: String, color: String) {
        viewModelScope.launch {
            val updated = Category(id, name, color, 0)
            repository.update(updated)
            loadCategories()
        }
    }
}