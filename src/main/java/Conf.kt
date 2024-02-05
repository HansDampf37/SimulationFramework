import framework.display.ColorPalette
import java.awt.Color

object Conf {
    val mass_color = Color(204, 117, 85).toVec()
    val background_color: Color = Color(30, 30, 60)
    val colorScheme = ColorPalette(
        smallObjectColor = Color.decode("#262b33"),
        bigObjectColor = Color.decode("#E78895"),
        linkColor = Color.decode("#2f343f"),
        colorOutline = Color.decode("#000000"),
        horizon = ColorPalette.Horizon(
            listOf(
                Color.decode("#FFF7F1")
            )
        )
    )

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