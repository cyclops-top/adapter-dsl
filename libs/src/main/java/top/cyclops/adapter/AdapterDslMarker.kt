package top.cyclops.adapter

/**
 * 用于标记自定义 DSL（领域特定语言）的注解。
 *
 * 此注解遵循 Kotlin 的 `@DslMarker` 约定，用于标记在自定义 DSL 中使用的接口和类。
 * 通过使用该注解，可以避免在 DSL 中意外的接收器组合，确保 DSL 的使用更加安全和直观。
 *
 * 在这个项目中，`AdapterDslMarker` 注解主要用于标记与 `RecyclerView` 适配器配置相关的 DSL 接口和类，
 * 使得开发者可以使用更简洁、更具可读性的方式来创建和配置适配器。
 *
 * 例如，在配置适配器时，可以使用带有该注解的接口来定义 DSL 块，从而避免不同 DSL 块之间的混淆。
 *
 * @see DslMarker
 */
@DslMarker
annotation class AdapterDslMarker
