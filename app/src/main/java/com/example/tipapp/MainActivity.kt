package com.example.tipapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tipapp.components.InputField
import com.example.tipapp.ui.theme.TipAppTheme
import com.example.tipapp.utils.calculateTip
import com.example.tipapp.utils.calculateTotalPerPerson
import com.example.tipapp.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
//           MyApp()
            MainContent()
        }
    }
}

@Composable
fun MyApp(){

    TipAppTheme {
        Surface() {
            TopHeader()
        }
    }
}

//@Preview
@Composable
fun TopHeader(totalPerPerson: Double = 0.0){
    Surface (modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp)
        .height(140.dp)
        .clip(shape = CircleShape.copy(all = CornerSize(12.dp))),
        color = Color(0xFFE9D7F7)
    ){
        Column (modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            val total = "%.2f".format(totalPerPerson)
            Text(text="Total Per Person",
                style = MaterialTheme.typography.headlineSmall)
            Text(text = "$$total",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold)
        }
    }
}


@Preview
@Composable
fun MainContent(){
    val splitByState = remember {
        mutableStateOf(1)
    }
    val range = IntRange(start = 1, endInclusive = 10)
    val tipAmountState = remember {
        mutableStateOf(0.0)
    }

    val totalPerPersonState = remember {
        mutableStateOf(0.0)
    }
    BillForm(splitByState = splitByState, range = range,
        tipAmountState = tipAmountState, totalPerPersonState = totalPerPersonState){

    }
}

@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    range: IntRange = 1..10,
    splitByState: MutableState<Int>,
    tipAmountState: MutableState<Double>,
    totalPerPersonState: MutableState<Double>,
              onValueChange: (String) -> Unit = {}){

    val totalBillState = remember {
        mutableStateOf("")
    }

    val isValidState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }

    val keyBoardController = LocalSoftwareKeyboardController.current

    val sliderPositionState = remember {
        mutableStateOf(0f)
    }

    var tipPercentage = (sliderPositionState.value *100).toInt()


    Surface(modifier = modifier.
        padding(8.dp),
        border = BorderStroke(width = 1.dp, color = Color.LightGray),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)))
    {
        Column (
            modifier = modifier.padding(3.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start) {
            TopHeader(totalPerPerson = totalPerPersonState.value)
            InputField(valueState = totalBillState,
                labelId = "Enter Bill",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if (!isValidState) return@KeyboardActions
                    keyBoardController?.hide()

                    onValueChange(totalBillState.value.trim())
                })
            if (isValidState) {
                Row(
                    modifier = modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Split",
                        modifier = modifier.align(
                            alignment = Alignment.CenterVertically
                        )
                    )
                    Spacer(modifier = modifier.width(120.dp))
                    Row (modifier = modifier
                        .padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End) {
                        RoundIconButton(
                            imageVector = Icons.Default.Remove,
                            onClick = {
                                splitByState.value =
                                    if (splitByState.value > 1) splitByState.value- 1 else 1
                                totalPerPersonState.value =
                                    calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(),
                                        splitBy = splitByState.value,
                                        tipPercentage = tipPercentage)
                            }
                        )
                        Text(text = "${splitByState.value}",
                            modifier = modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 9.dp, end = 9.dp))
                        RoundIconButton(
                            imageVector = Icons.Default.Add,
                            onClick = {
                                if(splitByState.value < range.last)  splitByState.value = splitByState.value + 1

                                totalPerPersonState.value =
                                    calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(),
                                        splitBy = splitByState.value,
                                        tipPercentage = tipPercentage)
                            })
                    }
                }

            //Tip Row

            Row (
                modifier = modifier.padding(horizontal = 3.dp,
                    vertical = 12.dp)
            ){
                Text(text = "Tip",
                    modifier = modifier.align(Alignment.CenterVertically))
                Spacer(modifier = modifier.width(200.dp))
                
                Text(text = "$ ${tipAmountState.value}",
                    modifier = modifier.align(Alignment.CenterVertically))
            }

            Column (verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "${tipPercentage}%")
                Spacer(modifier = modifier.height(14.dp))

                //Slider
                Slider(value = sliderPositionState.value,
                    onValueChange = { newVal ->
                        sliderPositionState.value = newVal
                        val totalBill = totalBillState.value.toDoubleOrNull() ?: 0.0
                        val updatedTipPercentage = (sliderPositionState.value *100).toInt()
                        tipPercentage = updatedTipPercentage
                        tipAmountState.value =
                            calculateTip(totalBill = totalBill,tipPercentage= tipPercentage)

                        // Calculating total Per Person
                        totalPerPersonState.value =
                            calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(),
                                splitBy = splitByState.value,
                                tipPercentage = tipPercentage
                            )
                    },
                    modifier = modifier.padding(start = 16.dp,
                        end = 16.dp),
                    steps = 5)
            }

            } else{
                Box(){
                }
            }

        }
    }
}




@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TipAppTheme {
      Text(text="Hello Again")
    }
}