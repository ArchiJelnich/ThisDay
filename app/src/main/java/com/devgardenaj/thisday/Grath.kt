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
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.devgardenaj.thisday.infra.AvarageCount
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
        val catRepo = CategoryRepository(db.CategoryDao())
        val infoRepo = InfoRepository(db.InfoAboutDayDao())
        val myDay = DateToCustomDate(LocalDate.now())

        GrathViewModel(catRepo, infoRepo, myDay)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        localeChecker(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
                Surface(color = MaterialTheme.colorScheme.background) {
                    GraphScreen(viewModel)
                }
            }
    }
}

@Composable
fun GraphScreen(viewModel : GrathViewModel) {
    var selectedView by remember { mutableStateOf("month") }
    //val context = LocalContext.current

    AvarageCount(viewModel)


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
                    Button(onClick = { selectedView = "month" }) { Text(stringResource(R.string.month)) }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(onClick = { selectedView = "year" }) { Text(stringResource(R.string.year)) }
                }


                    Column(modifier = Modifier.weight(1f)) {
                        PeriodContent(selectedView)
                    }




                BottomPanel()
            }
        }}

@Composable
fun PeriodContent(view: String) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = {
                /* TODO= */
            }) { Text("<") }
            Text(text = when (view) {
                "month" -> stringResource(R.string.month)
                "year" -> stringResource(R.string.year)
                else -> ""
            },
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Button(onClick = {
                /* TODO= */
            }) { Text(">") }
        }
        Spacer(modifier = Modifier.height(8.dp))
        PeriodGrid(view)
    }
}

@Composable
fun PeriodGrid(view: String) {
    val columns = when (view) {
        "month" -> 31
        "year" -> 12
        else -> 0
    }
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        LazyRow {
            items((1..columns).toList()) { columnNumber ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    repeat(24) {
                        Box(
                            modifier = Modifier
                                .size(17.dp)
                                .background(
                                    Color(
                                        Random.nextFloat(),
                                        Random.nextFloat(),
                                        Random.nextFloat(),
                                        1f
                                    )
                                )
                        )
                    }
                    Text(text = columnNumber.toString(), modifier = Modifier.padding(top = 4.dp))
                }
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}



class GrathViewModel(private val catRepo: CategoryRepository, private val infoRepository: InfoRepository, private val date : CustomDate) : CategoryViewModel(catRepo) {

    var info = mutableStateOf<List<InfoAboutDay>>(emptyList())
    val myDate = date

    fun loadInfo() {
        viewModelScope.launch {
            info.value = infoRepository.getInfoByYear(date.year)
        }
    }

}