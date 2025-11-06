package com.devgardenaj.thisday

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.devgardenaj.thisday.room.*
import com.devgardenaj.thisday.screens.BottomPanel
import kotlinx.coroutines.launch
import java.time.LocalDate


@RequiresApi(Build.VERSION_CODES.O)
class TodayActivity : AppCompatActivity() {

    private val viewModel by lazy {
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "category-db"
        ).build()
        val repo = CategoryRepository(db.CategoryDao())
        val infoRepo = InfoRepository(db.InfoAboutDayDao())
        val today = DateToCustomDate(LocalDate.now())

        TodayViewModel(repo, infoRepo, today)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodayScreen(viewModel)
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(viewModel: TodayViewModel) {
    val categories by viewModel.categories





    LaunchedEffect(Unit) {
        viewModel.loadCategories()
        viewModel.loadInfo()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.today)) }
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
                    CategoryRowCountable(
                        category = CategoryTemp(
                            category.categoryID,
                            category.categoryName,
                            category.categoryColor
                        ),
                        viewModel = viewModel,
                    )
                    Divider()
                }
            }


        }
    }
}

@Composable
fun CategoryRowCountable(category: CategoryTemp,  viewModel: TodayViewModel) {

    val infoList by viewModel.info
    var count by remember { mutableIntStateOf(infoList.find { it.categoryID == category.categoryID }?.infoSum ?: 0) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        color = Color(android.graphics.Color.parseColor(category.categoryColor)),
                        shape = CircleShape
                    )
            )
            Spacer(modifier = Modifier.width(12.dp))

            Box(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                Text(
                    text = category.categoryName,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = {
                if (count > 0) {
                    count--
                    viewModel.updateCount(category.categoryID, count)
                }
            }) { Text("-") }
            Text(text = count.toString(), modifier = Modifier.padding(horizontal = 8.dp))
            Button(onClick = {
                count++
                viewModel.updateCount(category.categoryID, count)
            }) { Text("+") }
        }
    }
}


class InfoRepository(private val dao: InfoAboutDayDao) {
    suspend fun getInfoByDay(infoDay: Int, infoMonth: Int, infoYear: Int) = dao.getInfoByDay(infoDay, infoMonth, infoYear)
    suspend fun updateInfo(infoSummary: InfoAboutDay) = dao.updateInfo(infoSummary)
    suspend fun insertInfo(infoSummary: InfoAboutDay) = dao.insertInfo(infoSummary)
    suspend fun getAll(infoDay: Int, infoMonth: Int, infoYear: Int) = dao.getAll(infoDay, infoMonth, infoYear)


}

class TodayViewModel(private val repository: CategoryRepository, private val infoRepository: InfoRepository, private val date : CustomDate) : CategoryViewModel(repository) {

    var info = mutableStateOf<List<InfoSummary>>(emptyList())

    fun loadInfo() {
        viewModelScope.launch {
            info.value = infoRepository.getInfoByDay(date.day, date.month, date.year)
            Log.d("MyDebug", "load " +  info.value)
        }
    }

    fun updateCount(categoryId: Int, newCount: Int) {
        viewModelScope.launch {
            val existing = infoRepository.getAll(date.day, date.month, date.year)
                .find { it.categoryID == categoryId }

            if (existing != null) {
                infoRepository.updateInfo(
                    existing.copy(infoSum = newCount)
                )
            } else {
                infoRepository.insertInfo(
                    InfoAboutDay(
                        iID = 0,
                        categoryID = categoryId,
                        infoSum = newCount,
                        infoDay = date.day,
                        infoMonth = date.month,
                        infoYear = date.year
                    )
                )
            }

            loadInfo()
        }
    }

}