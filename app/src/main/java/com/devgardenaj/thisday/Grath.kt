package com.devgardenaj.thisday

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.material3.*
import com.devgardenaj.thisday.infra.localeChecker
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.devgardenaj.thisday.infra.AvarageCount
import com.devgardenaj.thisday.infra.CategoryColors
import com.devgardenaj.thisday.infra.DayInfo
import com.devgardenaj.thisday.infra.MonthAverage
import com.devgardenaj.thisday.infra.MonthCount
import com.devgardenaj.thisday.infra.MonthInfo
import com.devgardenaj.thisday.infra.dateMYToString
import com.devgardenaj.thisday.infra.parseColor
import com.devgardenaj.thisday.room.CustomDate
import com.devgardenaj.thisday.room.DateToCustomDate
import com.devgardenaj.thisday.room.InfoSummary
import com.devgardenaj.thisday.screens.BottomPanel
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.O)
class GraphActivity : ComponentActivity() {

    private val viewModel by lazy {
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "category-db"
        ).build()
        val catRepo = CategoryRepository(db.CategoryDao(), db.InfoAboutDayDao())
        val infoRepo = InfoRepository(db.InfoAboutDayDao())
        val myDay = DateToCustomDate(LocalDate.now())
        val extras = intent.extras
        var asset = 0
        var assetMY = 0
        if (extras != null) {
            asset =  intent.extras?.get("asset") as Int
        }
        if (extras != null) {
            assetMY =  intent.extras?.get("assetMY") as Int
        }

        var newYear = myDay.year+asset
        var newMY = LocalDate.now().plusMonths(assetMY.toLong())

        GrathViewModel(catRepo, infoRepo, newYear, newMY)
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        val extras = intent.extras
        var asset = 0
        var assetMY = 0
        if (extras != null) {
            asset =  intent.extras?.get("asset") as Int
        }
        if (extras != null) {
            assetMY =  intent.extras?.get("assetMY") as Int
        }

        var selectedView = "month"
        if (extras != null) {
            selectedView =  intent.extras?.get("view") as String
        }

        localeChecker(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
                Surface(color = MaterialTheme.colorScheme.background) {
                    GraphScreen(viewModel, asset, assetMY, selectedView)
                }
            }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GraphScreen(viewModel : GrathViewModel, asset: Int, assetMY : Int, selectedViewExtra : String) {
    var selectedView by remember { mutableStateOf(selectedViewExtra) }
    //val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadCategories()
        viewModel.loadInfo()
    }
    AvarageCount(viewModel)
    MonthCount(viewModel)

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ){
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = stringResource(R.string.graph),
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .align(alignment = Alignment.CenterHorizontally)
                        .padding(top = 40.dp)
                        .padding(bottom = 20.dp),
                    textAlign = TextAlign.Center
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = { selectedView = "month" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor =
                                if (selectedView == "month")
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            stringResource(R.string.month),
                            color =
                                if (selectedView == "month")
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = { selectedView = "year" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor =
                                if (selectedView == "year")
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            stringResource(R.string.year),
                            color =
                                if (selectedView == "year")
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }


                    Column(modifier = Modifier.weight(1f)) {
                        PeriodContent(selectedView, asset, assetMY, viewModel)
                    }




                BottomPanel()
            }
        }}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PeriodContent(view: String, asset : Int, assetMY: Int, viewModel : ViewModel) {

    Log.d("MyLog", "LocalDate.now().year" + LocalDate.now().year)
    Log.d("MyLog", "asset" + asset)
    var newAsset = asset
    var newAssetMY = assetMY
    var newDateMY = LocalDate.now().plusMonths(newAssetMY.toLong())
    var newDate = LocalDate.now().year+asset
    val context = LocalContext.current
    var dayOfm = newDateMY.lengthOfMonth()

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = {
                if (view == "year") {
                newAsset--
                val intent = Intent(context, GraphActivity::class.java)
                intent.putExtra("asset", newAsset)
                intent.putExtra("assetMY", 0)
                intent.putExtra("view", view)
                context.startActivity(intent)}

                if (view == "month") {
                    newAssetMY--

                    Log.d("MyAsset", newAssetMY.toString())
                    val intent = Intent(context, GraphActivity::class.java)
                    intent.putExtra("asset", 0)
                    intent.putExtra("assetMY", newAssetMY)
                    intent.putExtra("view", view)
                    context.startActivity(intent)}

            }) { Text("<") }
            Text(text = when (view) {
                "month" -> dateMYToString(newDateMY)
                "year" -> newDate.toString()
                else -> ""
            },
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Button(onClick = {
                if (view == "year") {
                    newAsset++
                    val intent = Intent(context, GraphActivity::class.java)
                    intent.putExtra("asset", newAsset)
                    intent.putExtra("assetMY", 0)
                    intent.putExtra("view", view)
                    context.startActivity(intent)}

                if (view == "month") {
                    newAssetMY++
                    Log.d("MyAsset", newAssetMY.toString())
                    val intent = Intent(context, GraphActivity::class.java)
                    intent.putExtra("asset", 0)
                    intent.putExtra("assetMY", newAssetMY)
                    intent.putExtra("view", view)
                    context.startActivity(intent)}

            }) { Text(">") }
        }
        Spacer(modifier = Modifier.height(8.dp))
        PeriodGrid(view, viewModel as GrathViewModel, dayOfm)

    }
}

@Composable
fun PeriodGrid(    view: String,
                   viewModel: GrathViewModel,
                   dayOfm : Int
) {

    Log.d("MyLog", "Look here!" + viewModel.categories)


    when (view) {
        "year" -> YearGrid(
            monthAverages = viewModel.monthAverages.value,
            categoryColors = viewModel.categoryColors
        )
        "month" -> MonthGrid(
            monthInfo = viewModel.monthInfo.value,
            categoryColors = viewModel.categoryColors,
            dayOfm = dayOfm)
    }

}

@Composable
fun MonthGrid(
    monthInfo: List<DayInfo>,
    categoryColors: Map<Int, Color>,
    dayOfm : Int)
{
    val blockSize = 17.dp
    val blockSpacing = 2.dp
    val maxBlocks = 24

    val blockHeight = blockSize + blockSpacing
    val graphHeight = blockHeight * maxBlocks

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        LazyRow(
            verticalAlignment = Alignment.Bottom
        ) {
            items(monthInfo) { dayInfo ->

                val isToday = dayInfo.day == dayOfm

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Столбец фиксированной высоты
                    Box(
                        modifier = Modifier
                            .height(graphHeight)
                            .width(24.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val orderedData = remember(dayInfo, categoryColors) {
                                reorderByRainbow(dayInfo.sumsByCategory, categoryColors)
                            }

                            orderedData.forEach { (color, value) ->
                                repeat(value.coerceAtMost(maxBlocks)) {
                                    Box(
                                        modifier = Modifier
                                            .size(blockSize)
                                            .background(color, RoundedCornerShape(3.dp))
                                    )
                                    Spacer(modifier = Modifier.height(blockSpacing))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = dayInfo.day.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isToday)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
fun YearGrid(
    monthAverages: List<MonthAverage>,
    categoryColors: Map<Int, Color>
) {
    val blockSize = 17.dp
    val blockSpacing = 2.dp
    val maxBlocks = 24

    val blockHeight = blockSize + blockSpacing
    val graphHeight = blockHeight * maxBlocks

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        LazyRow(
            verticalAlignment = Alignment.Bottom
        ) {
            items(monthAverages) { monthData ->

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(
                        modifier = Modifier
                            .height(graphHeight)
                            .width(24.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val orderedData = remember(monthData, categoryColors) {
                                reorderByRainbow(monthData.avgByCategory, categoryColors)
                            }

                            orderedData.forEach { (color, value) ->
                                repeat(value.coerceAtMost(maxBlocks)) {
                                    Box(
                                        modifier = Modifier
                                            .size(blockSize)
                                            .background(color, RoundedCornerShape(3.dp))
                                    )
                                    Spacer(modifier = Modifier.height(blockSpacing))

                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = monthData.month.toString(),
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}


fun reorderByRainbow(
    values: Map<Int, Int>,
    categoryColors: Map<Int, Color>
): List<Pair<Color, Int>> {

    val rainbowIndex = CategoryColors
        .withIndex()
        .associate { it.value to it.index }

    return values
        .mapNotNull { (categoryId, value) ->
            val color = categoryColors[categoryId] ?: return@mapNotNull null
            color to value
        }
        .sortedBy { (color, _) ->
            rainbowIndex[color] ?: Int.MAX_VALUE
        }
}

class GrathViewModel(private val catRepo: CategoryRepository, private val infoRepository: InfoRepository, val newYear : Int, val newMY : LocalDate) : CategoryViewModel(catRepo) {

    val categoryColors: Map<Int, Color>
        get() = categories.value.associate {
            it.categoryID to parseColor(it.categoryColor)
        }

    var info = mutableStateOf<List<InfoAboutDay>>(emptyList())
    var infoPerM = mutableStateOf<List<InfoAboutDay>>(emptyList())
    var monthAverages = mutableStateOf<List<MonthAverage>>(emptyList())
    var monthInfo = mutableStateOf<List<DayInfo>>(emptyList())

    fun loadInfo() {
        viewModelScope.launch {
            info.value = infoRepository.getInfoByYear(newYear)
        }
    }

    fun loadPerMInfo() {
        viewModelScope.launch {
            infoPerM.value = infoRepository.getInfoByYearMonth(newMY.monthValue, newMY.year)
        }
    }

}