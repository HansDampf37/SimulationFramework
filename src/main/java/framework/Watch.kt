package framework

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class Watch(val name: String, val min: Double, val max: Double)
