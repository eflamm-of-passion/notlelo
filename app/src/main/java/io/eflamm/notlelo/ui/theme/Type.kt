package io.eflamm.notlelo.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import io.eflamm.notlelo.R

// TODO add the caveat brush font family
val caveatBrush = FontFamily(
    Font(R.font.caveat_brush, style = FontStyle.Normal)
)

// Set of Material typography styles to start with
val Typography = Typography(
    body1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 1.sp,
        color = DarkGrey
    ),
    h1 = TextStyle(
        fontFamily = caveatBrush,
        fontWeight = FontWeight.Normal,
        fontSize = 120.sp,
        color = White,
    ),
    h2 = TextStyle(
        fontFamily = caveatBrush,
        fontWeight = FontWeight.Normal,
        fontSize = 50.sp,
        color = White,
    ),
    h3 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 35.sp,
        letterSpacing = 1.sp,
        color = DarkGrey,
    ),
    h4 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Light,
        fontSize = 24.sp,
        letterSpacing = 3.sp,
        color = DarkGrey,
    ),
    h5 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 30.sp,
        letterSpacing = 1.sp,
        color = DarkGrey,
    ),
    h6 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 25.sp,
        letterSpacing = 1.sp,
        color = DarkGrey,
    ),
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp,
        color = White
    ),
    /* Other default text styles to override
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)