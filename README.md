# Lifecycle Aware Flow Collection with Zero Boilerplate
This article is about the flow collection in a lifecycle aware manner from activity or fragment. 
In my experience in the software industry, I have seen many developers prefers live data for state 
management for their application because it is lifecycle aware and with just one simple method
called it can be observed from activity or fragment. They avoid StateFlow for state management because
it is by default not lifecycle aware and to make it lifecycle aware they need to write more boilerplate 
than a live data. However, stateflow is a kotlin coroutine stream, usable for KMM projects, always contains
a initial state (no null handling needed), all the flow operators it can use. I agree with their concerns regarding boilerplate code. 
If we see how a live data observe look like from the below example:

```
val state: LiveData<String> = ...
state.observe(viewLifecycleOwner, Observer<String> { text: String? ->
    // Update the UI.
})
```
It seems very simple having a method call with some arguments(lifecycleOwner and lambda). However 
if we look at same example with stateflow:
```
val state: StateFlow<String> = ...
lifecycleScope.launch {
    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
        state.collect { text ->
            // Update the UI.
        }
    }
}
```
It is not as simple as live data which is true. Although, it is the safest way to collect stateflow 
in a lifecycle aware manner. There is blog post available from [Manual Vivo](https://medium.com/@manuelvicnt) about [A safer way to collect flows from Android UIs](https://medium.com/androiddevelopers/a-safer-way-to-collect-flows-from-android-uis-23080b1f8bda)
where from we see some similar example like above. From the above examples it is evident that collection
flow/stateflow is not as simple as live data. So the concerns of those developer is justified. Having said that,
does it makes sense to introduce a new observable (live data) just because it is more boilerplate
(if we are using coroutines and flow everywhere in the codebase)? Is it enough reason to lose all the capabilities
of flow? I think it is not enough reason to introduce live data rather we can do something to get rid
of the boilerplate to collect flow in ui lifecycle aware manner. How can we achieve that?

We can achieve that by creating an extension function of flow which is responsible for collecting flow 
items in lifecycle aware manner. The following example shows the extension function: 
```
fun <T> Flow<T>.lifecycleAwareCollect(
    lifecycleOwner: LifecycleOwner,
    state: Lifecycle.State = Lifecycle.State.STARTED,
    collector: FlowCollector<T>,
) {
    val stateFlow = this
    with(lifecycleOwner) {
        lifecycleScope.launch {
            repeatOnLifecycle(state) {
                stateFlow.collect(collector)
            }
        }
    }
}
```
The above extension function needs 2 mandatory parameter and one optional parameter. `LifecycleOwner` and `FlowCollector`
are mandatory. For activity `this` keyword will be enough for LifecycleOwner but for fragment it is
`ViewLifecycleOwner`. FlowCollector is just the implementation of FlowCollector [SAM](https://kotlinlang.org/docs/fun-interfaces.html)
which it will be defined after flow collection what will happen e.g., update the ui. `Lifecycle.State`
is an optional parameter by default the flow collection will be started on activity or fragment lifecycle 
is in the started state. It will stop collecting when activity or fragment lifecycle is in the Stop state
that mean when there will be no ui of available on the screen of our application. We can customize this 
behavior according our need e.g., we can use `Lifecycle.State.RESUMED` to trigger collection at onResume 
and stop it during onPause. 

Now if we see how the lifecycle aware flow/stateflow collection is going to look like with our extension function. 
```
val state: StateFlow<String> = .....
state.lifecycleAwareCollect(viewLifecycleOwner) { text ->
    // update ui
}
```
Is not it look much simple than previous? Now those developer can easily use this new extension function
with a simpler implementation and do not need to introduce live data for the seek of simplicity. 
The full implementation with example is available in [my github repository](https://github.com/WasimReza2K8/FlowVlog).
