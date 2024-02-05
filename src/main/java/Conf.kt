import framework.WatchBoolean
import framework.display.ColorPalette
import java.awt.Color

object Conf {
    val mass_color = Color(204, 117, 85).toVec()
    val background_color: Color = Color(30, 30, 60)
    val colorScheme = ColorPalette(
        smallObjectColor = Color.decode("#4C87C8"),
        bigObjectColor = Color.decode("#E78895"),
        linkColor = Color.decode("#2f343f"),
        colorOutline = Color.decode("#E4FBC1"),
        horizon = ColorPalette.Horizon(
            listOf(
                Color.decode("#262B33")
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