package framework

import framework.display.Display
import java.awt.Dimension
import java.lang.IllegalStateException
import java.lang.reflect.Field
import javax.swing.*
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.superclasses
import kotlin.reflect.jvm.isAccessible

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class WatchDouble(val displayName: String, val min: Double, val max: Double)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class WatchFloat(val displayName: String, val min: Float, val max: Float)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class WatchInt(val displayName: String, val min: Int, val max: Int)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class WatchString(val displayName: String)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class WatchBoolean(val displayName: String)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class WatchAttributeObject(val displayName: String)

abstract class WatchedField<T, S : JComponent>(val displayName: String, val field: Field, val obj: Any) {

    abstract var controlComponent: S
    abstract var displayComponent: JLabel

    fun set(value: T) {
        field.isAccessible = true
        field.set(obj, value)
        val memberProperty = obj::class.members.find { it.name == field.name }
        if (memberProperty is KMutableProperty<*>) {
            memberProperty.isAccessible = true
            memberProperty.setter.call(obj, value)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun get(): T = field.get(obj) as T

    abstract fun updateControlComponent()
}

@Suppress("LeakingThis", "UNCHECKED_CAST")
abstract class WatchedNumber<T : Number>(displayName: String, field: Field, obj: Any, min: Number, max: Number) :
    WatchedField<T, JSlider>(displayName, field, obj) {

    override var controlComponent: JSlider = JSlider(
        JSlider.HORIZONTAL,
        (min.toDouble() * 100).toInt(),
        (max.toDouble() * 100).toInt(),
        (get().toDouble() * 100).toInt()
    ).apply {
        preferredSize = Dimension(100, 25)
        maximumSize = Dimension(100, 25)
        minimumSize = Dimension(100, 25)
        majorTickSpacing = ((max.toDouble() * 100).toInt() - (min.toDouble() * 100).toInt()) / 100
        paintTicks = false
        paintLabels = false
        addChangeListener {
            val newValue = value / 100.0
            set(newValue as T)
            displayComponent.text = "$newValue"
        }
    }
    override var displayComponent: JLabel = JLabel(Display.round(controlComponent.value / 100.0))

    override fun updateControlComponent() {
        controlComponent.value = (get().toDouble() * 100).toInt()
    }
}

class WatchedDouble(displayName: String, field: Field, obj: Any, min: Double, max: Double) :
    WatchedNumber<Double>(displayName, field, obj, min, max)

class WatchedFloat(displayName: String, field: Field, obj: Any, min: Float, max: Float) :
    WatchedNumber<Float>(displayName, field, obj, min, max)

class WatchedInt(displayName: String, field: Field, obj: Any, min: Int, max: Int) :
    WatchedNumber<Int>(displayName, field, obj, min, max)

class WatchedString(displayName: String, field: Field, obj: Any) :
    WatchedField<String, JTextField>(displayName, field, obj) {
    override var controlComponent: JTextField = JTextField(get()).apply {
        preferredSize = Dimension(100, 25)
        maximumSize = Dimension(100, 25)
        minimumSize = Dimension(100, 25)
        // todo update value in model and view when changed
    }
    override var displayComponent: JLabel = JLabel()

    override fun updateControlComponent() {
        controlComponent.text = get()
    }
}

class WatchedBoolean(displayName: String, field: Field, obj: Any) :
    WatchedField<Boolean, JCheckBox>(displayName, field, obj) {
    override var controlComponent: JCheckBox = JCheckBox().apply {
        preferredSize = Dimension(100, 25)
        maximumSize = Dimension(100, 25)
        minimumSize = Dimension(100, 25)
        isSelected = get()
        addChangeListener {
            val newValue = isSelected
            set(newValue)
            displayComponent.text = isSelected.toString()
        }
        // todo update value in model and view when changed
    }
    override var displayComponent: JLabel = JLabel()

    override fun updateControlComponent() {
        controlComponent.isSelected = get()
    }
}

fun collectWatchedFields(objects: Collection<Any>): Map<Any, List<WatchedField<*, *>>> {
    fun getAllFields(c: KClass<*>): Set<Field> {
        val fields = c.java.declaredFields.toMutableSet()
        for (superclass in c.superclasses) {
            fields.addAll(getAllFields(superclass))
        }
        return fields
    }

    val watchedFieldsForObjects = mutableMapOf<Any, List<WatchedField<*, *>>>()
    objects.forEach { obj ->
        val watchedFields = mutableListOf<WatchedField<*, *>>()
        for (field in getAllFields(obj::class)) {
            field.setAccessible(true)
            if (field.isAnnotationPresent(WatchDouble::class.java)) {
                val annotation = field.getAnnotation(WatchDouble::class.java)
                val watchedField = WatchedDouble(annotation.displayName, field, obj, annotation.min, annotation.max)
                watchedFields.add(watchedField)
            } else if (field.isAnnotationPresent(WatchFloat::class.java)) {
                val annotation = field.getAnnotation(WatchFloat::class.java)
                val watchedField = WatchedFloat(annotation.displayName, field, obj, annotation.min, annotation.max)
                watchedFields.add(watchedField)
            } else if (field.isAnnotationPresent(WatchInt::class.java)) {
                val annotation = field.getAnnotation(WatchInt::class.java)
                val watchedField = WatchedInt(annotation.displayName, field, obj, annotation.min, annotation.max)
                watchedFields.add(watchedField)
            } else if (field.isAnnotationPresent(WatchString::class.java)) {
                val annotation = field.getAnnotation(WatchString::class.java)
                val watchedField = WatchedString(annotation.displayName, field, obj)
                watchedFields.add(watchedField)
            } else if (field.isAnnotationPresent(WatchBoolean::class.java)) {
                val annotation = field.getAnnotation(WatchBoolean::class.java)
                val watchedField = WatchedBoolean(annotation.displayName, field, obj)
                watchedFields.add(watchedField)
            }
        }
        watchedFieldsForObjects[obj] = watchedFields
    }
    return watchedFieldsForObjects
}