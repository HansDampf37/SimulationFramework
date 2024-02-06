import framework.WatchBoolean
import framework.display.ColorPalette
import java.awt.Color

object Conf {
    val colorScheme = ColorPalette(
        smallObjectColor = Color(76, 135, 200),
        bigObjectColor = Color(231, 136, 149),
        linkColor = Color(47, 52, 63),
        colorOutline = Color(228, 251, 193),
        horizon = ColorPalette.Horizon(
            listOf(
                Color(38, 43, 51)
            )
        )
    )

    @Suppress("unused")
    val colorSchemeDebug = ColorPalette(
        smallObjectColor = Color(0,0,255),
        bigObjectColor = Color(0,255,0),
        linkColor = Color(255,0,0),
        colorOutline = Color(228,251,193),
        horizon = ColorPalette.Horizon(
            listOf(
                Color(255,0,0)
            )
        )
    )
    @WatchBoolean("shading")
    var shadingOnSpheres: Boolean = true

    /*val colorScheme1 = ColorPalette(
        listOf(
            Color.decode("#594F4F"),
            Color.decode("#547980"),
            Color.decode("#45ADA8"),
            Color.decode("#9DE0AD"),
            Color.decode("#E5FCC2")
        ),
        listOf(12, 1, 1, 1, 4)
    )*/
}